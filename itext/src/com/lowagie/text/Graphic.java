/*
 * @(#)Graphic.java					0.37 2000/10/05
 *       release iText0.35:			0.37 2000/10/05
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
 */

package com.lowagie.text;

import java.util.ArrayList;

import com.lowagie.text.pdf.PdfContent;

/**
 * A <CODE>Graphic</CODE> element can contain several geometric figures (curves, lines,...).
 * <P>
 * If you want to use this <CODE>Element</CODE>, please read the Sections 8.4 and 8.5 of
 * the PDF Reference Manual version 1.3 first.
 *
 * @see		Element
 * 
 * @author  bruno@lowagie.com
 * @version 0.37 2000/10/05
 * @since   iText0.37
 */

public class Graphic extends PdfContent implements Element {

// constructor

	/**
	 * Constructs a <CODE>Graphic</CODE>-object.
	 *
	 * @since		iText0.37
	 */

	public Graphic() {
		super();
	}

// implementation of the Element interface

    /**
     * Processes the element by adding it (or the different parts) to a
	 * <CODE>DocListener</CODE>. 
     *
	 * <CODE>true</CODE> if the element was processed successfully
     * @since   iText0.37
     */

    public boolean process(DocListener listener) {
		try {
			return listener.add(this);
		}
		catch(DocumentException de) {
			return false;
		}
	}

    /**
     * Gets the type of the text element. 
     *
     * @return	a type
     * @since	iText0.37
     */

    public int type() {
		return Element.GRAPHIC;
	}		

    /**
     * Gets all the chunks in this element. 
     *
     * @return	an <CODE>ArrayList</CODE>
	 *
     * @since	iText0.37
     */

    public ArrayList getChunks() {
		 return new ArrayList();
	}
}