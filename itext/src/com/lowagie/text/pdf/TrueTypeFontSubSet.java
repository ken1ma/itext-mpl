/*
 * TrueTypeFontSubSet.java
 *
 * Created on November 28, 2001, 2:33 PM
 */

package com.lowagie.text.pdf;

import java.io.*;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Arrays;
import com.lowagie.text.DocumentException;

/** Subsets a True Type font by removing the unneeded glyphs from
 * the font.
 *
 * @author  Paulo Soares (psoares@consiste.pt)
 */
class TrueTypeFontSubSet {
    static final String tableNamesSimple[] = {"cvt ", "fpgm", "glyf", "head",
        "hhea", "hmtx", "loca", "maxp", "prep"};
    static final String tableNamesCmap[] = {"cmap", "cvt ", "fpgm", "glyf", "head",
        "hhea", "hmtx", "loca", "maxp", "prep"};
    static final int entrySelectors[] = {0,0,1,1,2,2,2,2,3,3,3,3,3,3,3,3,4,4,4,4,4};
    static final int TABLE_CHECKSUM = 0;
    static final int TABLE_OFFSET = 1;
    static final int TABLE_LENGTH = 2;
    static final int HEAD_LOCA_FORMAT_OFFSET = 51;

    static final int ARG_1_AND_2_ARE_WORDS = 1;
    static final int WE_HAVE_A_SCALE = 8;
    static final int MORE_COMPONENTS = 32;
    static final int WE_HAVE_AN_X_AND_Y_SCALE = 64;
    static final int WE_HAVE_A_TWO_BY_TWO = 128;
    
    
    /** Contains the location of the several tables. The key is the name of
     * the table and the value is an <CODE>int[3]</CODE> where position 0
     * is the checksum, position 1 is the offset from the start of the file
     * and position 2 is the length of the table.
     */
    protected HashMap tableDirectory;
    /** The file in use.
     */
    protected RandomAccessFile rf;
    /** The file name.
     */
    protected String fileName;
    protected boolean includeCmap;
    protected boolean locaShortTable;
    protected int locaTable[];
    protected HashMap glyphsUsed;
    protected ArrayList glyphsInList;
    protected int tableGlyphOffset;
    protected int newLocaTable[];
    protected byte newLocaTableOut[];
    protected byte newGlyfTable[];
    protected int glyfTableRealSize;
    protected int locaTableRealSize;
    protected byte outFont[];
    protected int fontPtr;

    /** Creates a new TrueTypeFontSubSet
     * @param fileName the file name of the font
     * @param glyphsUsed the glyphs used
     * @param includeCmap <CODE>true</CODE> if the table cmap is to be included in the generated font
     */
    TrueTypeFontSubSet(String fileName, HashMap glyphsUsed, boolean includeCmap) {
        this.fileName = fileName;
        this.glyphsUsed = glyphsUsed;
        this.includeCmap = includeCmap;
        glyphsInList = new ArrayList(glyphsUsed.keySet());
    }
    
    /** Does the actual work of subsetting the font.
     * @throws IOException on error
     * @throws DocumentException on error
     * @return the subset font
     */    
    byte[] process() throws IOException, DocumentException {
        rf = null;
        try {
            rf = new RandomAccessFile(fileName, "r");
            createTableDirectory();
            readLoca();
            flatGlyphs();
            createNewGlyphTables();
            locaTobytes();
            assembleFont();
            return outFont;
        }
        finally {
            if (rf != null) {
                try {
                    rf.close();
                }
                catch (Exception e) {
                }
            }
        }
    }
    
    protected void assembleFont() throws IOException, DocumentException {
        int tableLocation[];
        int fullFontSize = 0;
        String tableNames[];
        if (includeCmap)
            tableNames = tableNamesCmap;
        else
            tableNames = tableNamesSimple;
        int tablesUsed = 2;
        int len = 0;
        for (int k = 0; k < tableNames.length; ++k) {
            String name = tableNames[k];
            if (name.equals("glyf") || name.equals("loca"))
                continue;
            tableLocation = (int[])tableDirectory.get(name);
            if (tableLocation == null)
                continue;
            ++tablesUsed;
            fullFontSize += (tableLocation[TABLE_LENGTH] + 3) & (~3);
        }
        fullFontSize += newLocaTableOut.length;
        fullFontSize += newGlyfTable.length;
        int ref = 16 * tablesUsed + 12;
        fullFontSize += ref;
        outFont = new byte[fullFontSize];
        fontPtr = 0;
        writeFontInt(0x00010000);
        writeFontShort(tablesUsed);
        int selector = entrySelectors[tablesUsed];
        writeFontShort((1 << selector) * 16);
        writeFontShort(selector);
        writeFontShort((tablesUsed - (1 << selector)) * 16);
        for (int k = 0; k < tableNames.length; ++k) {
            String name = tableNames[k];
            tableLocation = (int[])tableDirectory.get(name);
            if (tableLocation == null)
                continue;
            writeFontString(name);
            if (name.equals("glyf")) {
                writeFontInt(calculateChecksum(newGlyfTable));
                len = glyfTableRealSize;
            }
            else if (name.equals("loca")) {
                writeFontInt(calculateChecksum(newLocaTableOut));
                len = locaTableRealSize;
            }
            else {
                writeFontInt(tableLocation[TABLE_CHECKSUM]);
                len = tableLocation[TABLE_LENGTH];
            }
            writeFontInt(ref);
            writeFontInt(len);
            ref += (len + 3) & (~3);
        }
        for (int k = 0; k < tableNames.length; ++k) {
            String name = tableNames[k];
            tableLocation = (int[])tableDirectory.get(name);
            if (tableLocation == null)
                continue;
            if (name.equals("glyf")) {
                System.arraycopy(newGlyfTable, 0, outFont, fontPtr, newGlyfTable.length);
                fontPtr += newGlyfTable.length;
                newGlyfTable = null;
            }
            else if (name.equals("loca")) {
                System.arraycopy(newLocaTableOut, 0, outFont, fontPtr, newLocaTableOut.length);
                fontPtr += newLocaTableOut.length;
                newLocaTableOut = null;
            }
            else {
                rf.seek(tableLocation[TABLE_OFFSET]);
                rf.readFully(outFont, fontPtr, tableLocation[TABLE_LENGTH]);
                fontPtr += (tableLocation[TABLE_LENGTH] + 3) & (~3);
            }
        }
    }
    
    protected void createTableDirectory() throws IOException, DocumentException {
        tableDirectory = new HashMap();
        int id = rf.readInt();
        if (id != 0x00010000)
            throw new DocumentException(fileName + " is not a true type file.");
        rf.seek(4);
        int num_tables = rf.readUnsignedShort();
        rf.seek(12);
        for (int k = 0; k < num_tables; ++k) {
            String tag = readStandardString(4);
            int tableLocation[] = new int[3];
            tableLocation[TABLE_CHECKSUM] = rf.readInt();
            tableLocation[TABLE_OFFSET] = rf.readInt();
            tableLocation[TABLE_LENGTH] = rf.readInt();
            tableDirectory.put(tag, tableLocation);
        }
    }
    
    protected void readLoca() throws IOException, DocumentException {
        int tableLocation[];
        tableLocation = (int[])tableDirectory.get("head");
        if (tableLocation == null)
            throw new DocumentException("Table 'head' does not exist in " + fileName);
        rf.seek(tableLocation[TABLE_OFFSET] + HEAD_LOCA_FORMAT_OFFSET);
        locaShortTable = (rf.readUnsignedShort() == 0);
        tableLocation = (int[])tableDirectory.get("loca");
        if (tableLocation == null)
            throw new DocumentException("Table 'loca' does not exist in " + fileName);
        rf.seek(tableLocation[TABLE_OFFSET]);
        if (locaShortTable) {
            int entries = tableLocation[TABLE_LENGTH] / 2;
            locaTable = new int[entries];
            for (int k = 0; k < entries; ++k)
                locaTable[k] = rf.readUnsignedShort() * 2;
        }
        else {
            int entries = tableLocation[TABLE_LENGTH] / 4;
            locaTable = new int[entries];
            for (int k = 0; k < entries; ++k)
                locaTable[k] = rf.readInt();
        }
    }
    
    protected void createNewGlyphTables() throws IOException {
        newLocaTable = new int[locaTable.length];
        int activeGlyphs[] = new int[glyphsInList.size()];
        for (int k = 0; k < activeGlyphs.length; ++k)
            activeGlyphs[k] = ((Integer)glyphsInList.get(k)).intValue();
        Arrays.sort(activeGlyphs);
        int glyfSize = 0;
        for (int k = 0; k < activeGlyphs.length; ++k) {
            int glyph = activeGlyphs[k];
            glyfSize += locaTable[glyph + 1] - locaTable[glyph];
        }
        glyfTableRealSize = glyfSize;
        glyfSize = (glyfSize + 3) & (~3);
        newGlyfTable = new byte[glyfSize];
        int glyfPtr = 0;
        int listGlyf = 0;
        for (int k = 0; k < newLocaTable.length; ++k) {
            newLocaTable[k] = glyfPtr;
            if (listGlyf < activeGlyphs.length && activeGlyphs[listGlyf] == k) {
                ++listGlyf;
                newLocaTable[k] = glyfPtr;
                int start = locaTable[k];
                int len = locaTable[k + 1] - start;
                if (len > 0) {
                    rf.seek(tableGlyphOffset + start);
                    rf.readFully(newGlyfTable, glyfPtr, len);
                    glyfPtr += len;
                }
            }
        }
    }
    
    protected void locaTobytes() {
        if (locaShortTable)
            locaTableRealSize = newLocaTable.length * 2;
        else
            locaTableRealSize = newLocaTable.length * 4;
        newLocaTableOut = new byte[(locaTableRealSize + 3) & (~3)];
        outFont = newLocaTableOut;
        fontPtr = 0;
        for (int k = 0; k < newLocaTable.length; ++k) {
            if (locaShortTable)
                writeFontShort(newLocaTable[k] / 2);
            else
                writeFontInt(newLocaTable[k]);
        }
        
    }
    
    protected void flatGlyphs() throws IOException, DocumentException {
        int tableLocation[];
        tableLocation = (int[])tableDirectory.get("glyf");
        if (tableLocation == null)
            throw new DocumentException("Table 'glyf' does not exist in " + fileName);
        Integer glyph0 = new Integer(0);
        if (!glyphsUsed.containsKey(glyph0)) {
            glyphsUsed.put(glyph0, null);
            glyphsInList.add(glyph0);
        }
        tableGlyphOffset = tableLocation[TABLE_OFFSET];
        for (int k = 0; k < glyphsInList.size(); ++k) {
            int glyph = ((Integer)glyphsInList.get(k)).intValue();
            checkGlyphComposite(glyph);
        }
    }

    protected void checkGlyphComposite(int glyph) throws IOException {
        int start = locaTable[glyph];
        if (start == locaTable[glyph + 1]) // no contour
            return;
        rf.seek(tableGlyphOffset + start);
        int numContours = rf.readShort();
        if (numContours >= 0)
            return;
        rf.skipBytes(8);
        for(;;) {
            int flags = rf.readUnsignedShort();
            Integer cGlyph = new Integer(rf.readUnsignedShort());
            if (!glyphsUsed.containsKey(cGlyph)) {
                glyphsUsed.put(cGlyph, null);
                glyphsInList.add(cGlyph);
            }
            if ((flags & MORE_COMPONENTS) == 0)
                return;
            int skip;
            if ((flags & ARG_1_AND_2_ARE_WORDS) != 0)
                skip = 4;
            else
                skip = 2;
            if ((flags & WE_HAVE_A_SCALE) != 0)
                skip += 2;
            else if ((flags & WE_HAVE_AN_X_AND_Y_SCALE) != 0)
                skip += 4;
            if ((flags & WE_HAVE_A_TWO_BY_TWO) != 0)
                skip += 8;
            rf.skipBytes(skip);
        }
    }
    
    /** Reads a <CODE>String</CODE> from the font file as bytes using the Cp1252
     *  encoding.
     * @param length the length of bytes to read
     * @return the <CODE>String</CODE> read
     * @throws IOException the font file could not be read
     */
    protected String readStandardString(int length) throws IOException {
        byte buf[] = new byte[length];
        rf.readFully(buf);
        try {
            return new String(buf, PdfObject.ENCODING);
        }
        catch (Exception e) {
            return new String(buf);
        }
    }
    
    protected void writeFontShort(int n) {
        outFont[fontPtr++] = (byte)(n >> 8);
        outFont[fontPtr++] = (byte)(n);
    }

    protected void writeFontInt(int n) {
        outFont[fontPtr++] = (byte)(n >> 24);
        outFont[fontPtr++] = (byte)(n >> 16);
        outFont[fontPtr++] = (byte)(n >> 8);
        outFont[fontPtr++] = (byte)(n);
    }

    protected void writeFontString(String s) {
        byte b[];
        try {
            b = s.getBytes(PdfObject.ENCODING);
        }
        catch (Exception e) {
            b = s.getBytes();
        }
        System.arraycopy(b, 0, outFont, fontPtr, b.length);
        fontPtr += b.length;
    }
    
    protected int calculateChecksum(byte b[]) {
        int len = b.length / 4;
        int v0 = 0;
        int v1 = 0;
        int v2 = 0;
        int v3 = 0;
        int ptr = 0;
        for (int k = 0; k < len; ++k) {
            v3 += (int)b[ptr++] & 0xff;
            v2 += (int)b[ptr++] & 0xff;
            v1 += (int)b[ptr++] & 0xff;
            v0 += (int)b[ptr++] & 0xff;
        }
        return v0 + (v1 << 8) + (v2 << 16) + (v3 << 24);
    }
}