/*
 * @(#)PdfStream.java				0.32 2000/08/09
 *       release rugPdf0.10:		0.04 99/03/29
 *               rugPdf0.20:		0.14 99/11/30
 *               iText0.3:			0.23 2000/02/14
 *               iText0.35:			0.32 2000/08/11
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
 *     
 * Very special thanks to Troy Harrison, Systems Consultant
 * of CNA Life Department-Information Technology
 * Troy.Harrison@cnalife.com <mailto:Troy.Harrison@cnalife.com>
 * His input concerning the changes in version rugPdf0.20 was
 * really very important.
 */

package com.lowagie.text.pdf;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.zip.DeflaterOutputStream;

/**
 * <CODE>PdfStream</CODE> is the Pdf stream object.
 * <P>
 * A stream, like a string, is a sequence of characters. However, an application can
 * read a small portion of a stream at a time, while a string must be read in its entirety.
 * For this reason, objects with potentially large amounts of data, such as images and
 * page descriptions, are represented as streams.<BR>
 * A stream consists of a dictionary that describes a sequence of characters, followed by
 * the keyword <B>stream</B>, followed by zero or more lines of characters, followed by
 * the keyword <B>endstream</B>.<BR>
 * All streams must be <CODE>PdfIndirectObject</CODE>s. The stream dictionary must be a direct
 * object. The keyword <B>stream</B> that follows the stream dictionary should be followed by
 * a carriage return and linefeed or just a linefeed.<BR> 
 * Remark: In this version only the FLATEDECODE-filter is supported.<BR>
 * This object is described in the 'Portable Document Format Reference Manual version 1.3'
 * section 4.8 (page 41-53).<BR>
 *
 * @see		PdfObject
 * @see		PdfDictionary
 *
 * @author  bruno@lowagie.com
 * @version 0.32 2000/08/09
 * @since   rugPdf0.10
 */

class PdfStream extends PdfObject {
	
// membervariables

	/** the stream dictionary */
	protected PdfDictionary dictionary;

	/** is the stream compressed? */
	private boolean compressed = false;

// constructors

	/**
	 * Constructs a <CODE>PdfStream</CODE>-object.
	 *				   
	 * @param		dictionary		the stream dictionary
	 * @param		str				the stream
	 *
	 * @since		rugPdf0.10
	 */

	PdfStream(PdfDictionary dictionary, String stream) {
		super(STREAM);					   
		this.dictionary = dictionary;

		// The length of a line in a PDF document is limited to 256 characters
		StringBuffer streamContent = new StringBuffer();
		int numberProcessed = 0;
		int numberToProcess = stream.length();
		int positionOfNewLine;
		int lengthOfTheLine;
		while (numberProcessed < numberToProcess) {
			positionOfNewLine = stream.indexOf('\n', numberProcessed) + 1;
			lengthOfTheLine = positionOfNewLine - numberProcessed;
			if (lengthOfTheLine < 250 && lengthOfTheLine > 0) {
				streamContent.append(stream.substring(numberProcessed, positionOfNewLine));
				numberProcessed = positionOfNewLine;
			}
			else {
				try {
					streamContent.append(stream.substring(numberProcessed, numberProcessed + 250) + "\n");
				}
				catch(IndexOutOfBoundsException ioobe) {
					streamContent.append(stream.substring(numberProcessed) + "\n");
				}
				numberProcessed += 250;
			}
		}
		
		setContent(streamContent.toString());
		dictionary.put(PdfName.LENGTH, new PdfNumber(bytes.length));
	}

	/**
	 * Constructs a <CODE>PdfStream</CODE>-object.
	 *
	 * @param		bytes			content of the new <CODE>PdfObject</CODE> as an array of <CODE>byte</CODE>.
	 *
	 * @since		iText0.30
	 */
 /*
	protected PdfStream(int type, byte[] bytes) {
		super(STREAM);
		this.bytes = bytes;
		dictionary.put(PdfName.LENGTH, new PdfNumber(bytes.length));
	}
   */
	/**
	 * Constructs a <CODE>PdfStream</CODE>-object.
	 *				   
	 * @param		str				the stream
	 *
	 * @since		rugPdf0.10
	 */

	PdfStream(String stream) {
		this(new PdfDictionary(), stream);
	}

	/**
	 * Constructs a <CODE>PdfStream</CODE>-object.
	 *
	 * @since		iText0.31
	 */

	protected PdfStream() {
		super(STREAM);
		dictionary = new PdfDictionary();
	}

// methods overriding some methods of PdfObject

	/**
     * Returns the PDF representation of this <CODE>PdfObject</CODE> as an array of <CODE>bytes</CODE>s.
	 *
	 * @return		an array of <CODE>byte</CODE>s
     *
	 * @since		rugPdf0.10
     */

    final byte[] toPdf() {
		try {
			ByteArrayOutputStream pdfStream = new ByteArrayOutputStream();
			pdfStream.write(dictionary.toPdf());
			pdfStream.write("\nstream\n".getBytes());
			pdfStream.write(bytes);
			pdfStream.write("\nendstream".getBytes());
			return pdfStream.toByteArray();
		}
		catch(IOException ioe) {
			throw new RuntimeException(ioe.getMessage());
		}
    }

// methods

	/**
	 * Compresses the stream.
	 *
	 * @return		<CODE>void<CODE>
	 * @throws		<CODE>PdfException<CODE> if a filter is allready defined
	 *
	 * @since		rugPdf0.20
	 * @author		Troy Harrison
	 * @author		Bruno Lowagie
	 */

	synchronized final void flateCompress() throws PdfException {
		// check if the flateCompress-method has allready been
		if (compressed) {
			return;
		}
		// check if a filter allready exists
		PdfObject filter = dictionary.get(PdfName.FILTER);
		if (filter != null) {
			if (filter.isName() && ((PdfName) filter).compareTo(PdfName.FLATEDECODE) == 0) {
				return;
			}
			else if (filter.isArray() && ((PdfArray) filter).contains(PdfName.FLATEDECODE)) {
				return;
			}
			else {
				throw new PdfException("Stream could not be compressed: filter is not a name or array.");
			}
		}
		try {
			// compress
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			DeflaterOutputStream zip = new DeflaterOutputStream(stream);
			zip.write(bytes);
			zip.close();
			// update the object
			bytes = stream.toByteArray();
			dictionary.put(PdfName.LENGTH, new PdfNumber(bytes.length));
			if (filter == null) {
				dictionary.put(PdfName.FILTER, PdfName.FLATEDECODE);
			}
			else { 
				PdfArray filters = new PdfArray(filter);
				filters.add(PdfName.FLATEDECODE);
				dictionary.put(PdfName.FILTER, filters);
			}
			compressed = true;
		}
		catch(IOException ioe) {
			System.err.println("The stream was not compressed: " + ioe.getMessage());
		}
	}
}