/*
 * @(#)Png.java					0.37 2000/10/05
 *       release iText0.36:		0.36 2000/09/10
 *       release iText0.37:		0.36 2000/10/05
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
 * Very special thanks to Paulo Soares who wrote some methods for PNG support.   
 */

package com.lowagie.text;

import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * An <CODE>Png</CODE> is the representation of a graphic element (PNG)
 * that has to be inserted into the document
 *
 * @see		Element
 * @see		Image
 * @see		Gif
 * @see		Jpeg
 * 
 * @author  bruno@lowagie.com
 * @version 0.37 2000/10/05
 * @since   iText0.36
 */

public class Png extends Image implements Element {

// public final static membervariables

	/** Some PNG specific values. */
	public static final int[] PNGID = {137, 80, 78, 71, 13, 10, 26, 10};

	/** A PNG marker. */
	public static final String IHDR = "IHDR";

	/** A PNG marker. */
	public static final String PLTE = "PLTE";

	/** A PNG marker. */
	public static final String IDAT = "IDAT";

	/** A PNG marker. */
	public static final String IEND = "IEND";

// Constructors

	/**
	 * Constructs a <CODE>Png</CODE>-object, using a <VAR>filename</VAR>.
	 *
	 * @param		filename	a <CODE>String</CODE>-representation of the file that contains the Image.
	 *
	 * @since		iText0.36
	 */

	public Png(String filename, int width, int height) throws MalformedURLException, BadElementException, IOException {
		this(Image.toURL(filename), width, height);
	}

	/**
	 * Constructs a <CODE>Png</CODE>-object, using an <VAR>url</VAR>.
	 *
	 * @param		url			the <CODE>URL</CODE> where the image can be found.
	 *
	 * @since		iText0.36
	 */

	public Png(URL url, int width, int height) throws BadElementException, IOException {
		this(url);
		scaledWidth = width;
		scaledHeight = height;
	}

	/**
	 * Constructs a <CODE>Png</CODE>-object, using a <VAR>filename</VAR>.
	 *
	 * @param		filename	a <CODE>String</CODE>-representation of the file that contains the Image.
	 *
	 * @since		iText0.36
	 */

	public Png(String filename) throws MalformedURLException, BadElementException, IOException {
		this(Image.toURL(filename));
	}

	/**
	 * Constructs a <CODE>Png</CODE>-object, using an <VAR>url</VAR>.
	 *
	 * @param		url			the <CODE>URL</CODE> where the image can be found.
	 *
	 * @since		iText0.36
	 */

	public Png(URL url) throws BadElementException, IOException {
		 super(url);
		 type = PNG;
		 InputStream is = null;
		 try {
			 is = url.openStream();
			 for (int i = 0; i < PNGID.length; i++) {
				 if (PNGID[i] != is.read())	{
					 throw new BadElementException(url.toString() + " is not a valid PNG-file.");
				 }
			 }
			 while(true) {
				int len = getInt(is);
				if (IHDR.equals(getString(is))) {
					scaledWidth = getInt(is);
					setRight((int) scaledWidth);
					scaledHeight = getInt(is);
					setTop((int) scaledHeight);
					break;
				}
				if (IEND.equals(getString(is))) {
					break;
				}
				skip(is, len + 4);
			 }
		 }
		 finally {
			if (is != null) {
				is.close();
			}
			plainWidth = width();
			plainHeight = height();
		 }
	}

// private methods

	/**
	 * Gets an <CODE>int</CODE> from an <CODE>InputStream</CODE>.
	 *
	 * @param		an <CODE>InputStream</CODE>
	 * @return		the value of an <CODE>int</CODE>
	 */

	public static final int getInt(InputStream is) throws IOException {
		return (is.read() << 24) + (is.read() << 16) + (is.read() << 8) + is.read();
	}

	/**
	 * Gets a <CODE>String</CODE> from an <CODE>InputStream</CODE>.
	 *
	 * @param		an <CODE>InputStream</CODE>
	 * @return		the value of an <CODE>int</CODE>
	 */

	public static final String getString(InputStream is) throws IOException {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < 4; i++) {
			buf.append((char)is.read());
		}
		return buf.toString();
	}

// methods to retrieve information

	/**
	 * Returns a representation of this <CODE>Rectangle</CODE>.
	 *
	 * @return		a <CODE>String</CODE>
	 *
	 * @since		iText0.36
	 */

	public String toString() {
		StringBuffer buf = new StringBuffer("<PNG>");
		buf.append(super.toString());
		buf.append("</PNG>");
		return buf.toString();
	}
}