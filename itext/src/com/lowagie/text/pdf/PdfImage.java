/*
 * @(#)PdfImage.java				0.36 2000/09/10
 *               iText0.35*:		0.35* 2000/08/21
 *               iText0.36:			0.36 2000/09/10
 * 
 * Copyright (c) 1999, 2000 Bruno Lowagie.
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Library General Public License as published
 * by the Free Software Foundation; either version 2 of the License, or any
 * later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Library general Public License for more
 * details.
 *
 * You should have received a copy of the GNU Library General Public License along
 * with this library; if not, write to the Free Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA.
 *
 * If you didn't download this code from the following link, you should check if
 * you aren't using an obsolete version:
 * http://www.lowagie.com/iText/
 *
 * ir-arch Bruno Lowagie,
 * Adolf Baeyensstraat 121
 * 9040 Sint-Amandsberg
 * BELGIUM
 * tel. +32 (0)9 228.10.97
 * bruno@lowagie.com
 *
 * REMARK:
 * LZW/GIF is covered by a software patent which is owned by Unisys Corporation.
 * Unisys refuses to license this patent for PDF-related use in software
 * even when this software is released for free and may be freely distributed.
 * HOWEVER:
 * This library doesn't compress or decompress data using the LZW
 * algorithm, nor does it create or visualize GIF-images in any way;
 * it only copies parts of an existing GIF file into a PDF file.
 *  
 * More information about the GIF format can be found in the following documents:
 * * GRAPHICS INTERCHANGE FORMAT(sm) Version 89a
 *   (c)1987,1988,1989,1990 Copyright CompuServe Incorporated. Columbus, Ohio
 * * LZW and GIF explained
 *   Steve Blackstock
 * * http://mistress.informatik.unibw-muenchen.de/
 *   very special thanks to klee@informatik.unibw-muenchen.de for the algorithm
 *   to extract the LZW data from a GIF.
 *
 * Very special thanks to Paulo Soares for the algorithm to parse the PNG format.
 */

package com.lowagie.text.pdf;

import com.lowagie.text.Image;
import com.lowagie.text.Png;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.IOException;

/**
 * <CODE>PdfImage</CODE> is a <CODE>PdfStream</CODE> containing an image-<CODE>Dictionary</CODE> and -stream.
 *
 * @author  bruno@lowagie.com
 * @version 0.36 2000/08/21
 * @since   iText0.31
 */

class PdfImage extends PdfStream {

// membervariables

	/** This is the <CODE>PdfName</CODE> of the image. */
	protected PdfName name = null;

// constructor

	/**
	 * Constructs a <CODE>PdfImage</CODE>-object.
	 *
	 * @param		Image		the <CODE>Image</CODE>-object
	 *
	 * @since		iText0.31
	 */

	public PdfImage(Image image, String name) throws BadPdfFormatException {
		super();
		this.name = new PdfName(name);
		dictionary.put(PdfName.TYPE, PdfName.XOBJECT);
		dictionary.put(PdfName.SUBTYPE, PdfName.IMAGE);
		dictionary.put(PdfName.NAME, this.name);
		dictionary.put(PdfName.WIDTH, new PdfNumber(image.width()));
		dictionary.put(PdfName.HEIGHT, new PdfNumber(image.height()));
		try {
			InputStream is = image.url().openStream();
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			int i = 0;
			switch(image.type()) {
			case Image.PNG:
				dictionary.put(PdfName.FILTER, PdfName.FLATEDECODE);
				for (int j = 0; j < Png.PNGID.length; j++) {
					if (Png.PNGID[j] != is.read()) {
						throw new BadPdfFormatException(image.url().toString() + " is not a PNG file.");
					}
				}
				int colorType = 0;
				while (true) {
					int len = Png.getInt(is);
					String marker = Png.getString(is);
					if (Png.IDAT.equals(marker)) {
						for (int j = 0; j < len; j++) {
							stream.write(is.read());
						}
						Png.getInt(is);
					}
					else if (Png.IHDR.equals(marker)) {
						int w = Png.getInt(is);
						int h = Png.getInt(is);

						int bitDepth = is.read();
						if (bitDepth == 16) {
							throw new BadPdfFormatException(image.url().toString() + " Bit depth 16 is not suported.");
						}
						dictionary.put(PdfName.BITSPERCOMPONENT, new PdfNumber(bitDepth));

						colorType = is.read();
						if (! (colorType == 0 || colorType == 2 || colorType == 3)) {
							throw new BadPdfFormatException(image.url().toString() + " Colortype " + colorType + " is not suported.");
						}
						if (colorType == 0) {
							dictionary.put(PdfName.COLORSPACE, PdfName.DEVICEGRAY);
						}
						else if (colorType == 2) {
							dictionary.put(PdfName.COLORSPACE, PdfName.DEVICERGB);
						}

						int compressionMethod = is.read();
						int filterMethod = is.read();

						int interlaceMethod = is.read();
						if (interlaceMethod != 0) {
							throw new BadPdfFormatException(image.url().toString() + " Interlace method " + interlaceMethod + " is not suported.");
						}

						PdfDictionary decodeparms = new PdfDictionary();
						decodeparms.put(PdfName.BITSPERCOMPONENT, new PdfNumber(bitDepth));
						decodeparms.put(PdfName.PREDICTOR, new PdfNumber(15));
						decodeparms.put(PdfName.COLUMNS, new PdfNumber(w));
						decodeparms.put(PdfName.COLORS, new PdfNumber((colorType == 2) ? 3: 1));
						dictionary.put(PdfName.DECODEPARMS, decodeparms);

						Png.getInt(is);
					}
					else if (Png.PLTE.equals(marker)) {
						if (colorType == 3) {
							PdfArray colorspace = new PdfArray();
							colorspace.add(PdfName.INDEXED);
							colorspace.add(PdfName.DEVICERGB);
							colorspace.add(new PdfNumber(len / 3 - 1));
							ByteArrayOutputStream colortable = new ByteArrayOutputStream();
							while ((len--) > 0) {
								colortable.write(is.read());
							}
							colorspace.add(new PdfGifColorTable(colortable.toByteArray()));
							dictionary.put(PdfName.COLORSPACE, colorspace);
							Png.getInt(is);
						}
						else {
							for (int j = -4; j < len; j++) {
								is.read();
							}
						}
					}
					else if (Png.IEND.equals(marker)) {
						break;
					}
					else {
						for (int j = -4; j < len; j++) {
							is.read();
						}
					}		 
				
				}
				break;
			case Image.JPEG:
				dictionary.put(PdfName.FILTER, PdfName.DCTDECODE);
				switch(image.colorspace()) {
				case 1:
					dictionary.put(PdfName.COLORSPACE, PdfName.DEVICEGRAY);
					break;
				case 3:	
					dictionary.put(PdfName.COLORSPACE, PdfName.DEVICERGB);
					break;
				default: 
					dictionary.put(PdfName.COLORSPACE, PdfName.DEVICECMYK);
				}
				dictionary.put(PdfName.BITSPERCOMPONENT, new PdfNumber(8));
				while ((i = is.read()) >= 0) {
					stream.write(i);
				}
				break;
			case Image.GIF:
				// HEADER + INFO + COLORTABLE

				// Byte 0-2: header
				// checks if the file really is a GIF-file
				if (is.read() != 'G' || is.read() != 'I' || is.read() != 'F') {
					throw new BadPdfFormatException(image.url().toString() + " is not a GIF-file (GIF header not found).");
				}

				dictionary.put(PdfName.FILTER, PdfName.LZWDECODE);
				
				PdfDictionary decodeparms = new PdfDictionary();
				decodeparms.put(PdfName.EARLYCHANGE, new PdfNumber(0));
				dictionary.put(PdfName.DECODEPARMS, decodeparms);

				PdfArray colorspace = new PdfArray();
				colorspace.add(PdfName.INDEXED);
				colorspace.add(PdfName.DEVICERGB);
				// Byte 3-5: version
				// Byte 6-7: logical screen width
				// Byte 8-9: logical screen height
				// Byte 10: Packed Fields
				for (int j = 0; j < 8; j++) {
					i = is.read();
				}
				// Byte 10: bit 1: Global Color Table Flag
				if ((i & 0x80) == 0) {
					throw new BadPdfFormatException(image.url().toString() + " is not a supported GIF-file (there is no global color table present).");
				} 
				// Byte 10: bit 6-8: Size of Global Color Table
				int nColors = 1 << ((i & 7) + 1);
				colorspace.add(new PdfNumber(nColors - 1));
				// Byte 11: Background color index
				is.read();
				// Byte 12: Pixel aspect ratio
				is.read();
				// Byte 13-...: Global color table
				ByteArrayOutputStream colortable = new ByteArrayOutputStream();
				for (int j = 0; j < nColors; j++) {
					colortable.write(is.read());	// red
					colortable.write(is.read());	// green
					colortable.write(is.read());	// blue
				}
				colorspace.add(new PdfGifColorTable(colortable.toByteArray()));
				dictionary.put(PdfName.COLORSPACE, colorspace);
				dictionary.put(PdfName.BITSPERCOMPONENT, new PdfNumber(8));

				// IMAGE DESCRIPTOR

				// Byte 0: Image separator 
				// only simple gif files with image immediate following global color table are supported
				// 0x2c is a fixed value for the image separator
				if (is.read() != 0x2c) {
					throw new BadPdfFormatException(image.url().toString() + " is not a supported GIF-file (the image separator '0x2c' is not found after reading the color table).");
				}
				// Byte 1-2: Image Left Position
				// Byte 3-4: Image Top Position
				// Byte 5-6: Image Width
				// Byte 7-8: Image Height
				// ignore position and size
				for (int j = 0; j < 8; j++) {
					is.read();
				}
				// Byte 9: Packed Fields
				// Byte 9: bit 1: Local Color Table Flag
				// Byte 9: bit 2: Interlace Flag
				if ((is.read() & 0xc0) > 0) {
					throw new BadPdfFormatException(image.url().toString() + " is not a supported GIF-file (interlaced gifs or gifs using local color table can't be inserted).");
				}

				// Byte 10: LZW initial code
				if (is.read() != 0x08) {
					throw new BadPdfFormatException(image.url().toString() + " is not a supported GIF-file (initial LZW code not supported).");
				}
				// Read the Image Data
				int code = 0;
				int codelength = 9;
				int tablelength = 257;
				int bitsread = 0;
				int bitstowrite = 0;
				int bitsdone = 0;
				int bitsleft = 23;
				int bytesdone = 0;
				int bytesread = 0;
				int byteswritten = 0;
				// read the size of the first Data Block
				int size = is.read();
				// Check if there is any data in the GIF
				if (size < 1) {
					throw new BadPdfFormatException(image.url().toString() + " is not a supported GIF-file. (no image data found).");
				}
				// if possible, we read the first 24 bits of data
				size--; bytesread++; bitsread = is.read();
				if (size > 0) {
					size--; bytesread++; bitsread += (is.read() << 8);
					if (size > 0) {
						size--; bytesread++; bitsread += (is.read() << 16);
					}
				}
				while (bytesread > byteswritten) {
					tablelength++;
					// we extract a code with length=codelength
					code = (bitsread >> bitsdone) & ((1 << codelength) - 1);
					// we delete the bytesdone in bitsread and append the next byte(s)
					bytesdone = (bitsdone + codelength) / 8;
					bitsdone = (bitsdone + codelength) % 8;
					while (bytesdone > 0) {
						bytesdone--;
						bitsread = (bitsread >> 8);
						if (size > 0) {
							size--; bytesread++; bitsread += (is.read() << 16);
						}
						else {
							size = is.read();
							if (size > 0) {
								size--; bytesread++; bitsread += (is.read() << 16);
							}
						}
					}
					// we package all the bits that are done into bytes and write them to the stream
					bitstowrite += (code << (bitsleft - codelength + 1));
					bitsleft -= codelength;
					while (bitsleft < 16) {
						stream.write(bitstowrite >> 16);
						byteswritten++;
						bitstowrite = (bitstowrite & 0xFFFF) << 8;
						bitsleft += 8;
					}
					if (code == 256) {
						codelength = 9;
						tablelength = 257;
					}
					if (code == 257) {
						break;
					}
					if (tablelength == (1 << codelength)) {
						codelength++;
					}
				}
				if (bytesread - byteswritten > 2) {
					throw new BadPdfFormatException(image.url().toString() + " is not a supported GIF-file (unexpected end of data block).");
				}
				break;
			default:
				throw new BadPdfFormatException(image.url().toString() + " is an unknown Image format.");
			}
			bytes = stream.toByteArray();
			dictionary.put(PdfName.LENGTH, new PdfNumber(bytes.length));
		}
		catch(IOException ioe) {
			throw new BadPdfFormatException(ioe.getMessage());
		}
	}

	/**
	 * Returns the <CODE>PdfName</CODE> of the image.
	 *
	 * @return		the name
	 *
	 * @since		iText0.31
	 */

	public final PdfName name() {
		return name;
	}
}