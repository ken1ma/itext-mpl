/*
 * @(#)Chapter.java					0.22 2000/02/02
 *       release iText0.3:			0.22 2000/02/14
 *       release iText0.35:			0.22 2000/08/11
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
import java.util.Iterator;

/**
 * A <CODE>Chapter</CODE> is a special <CODE>Section</CODE>.
 * <P>
 * A chapter number has to be created using a <CODE>Paragraph</CODE> as title
 * and an <CODE>int</CODE> as chapternumber. The chapter number is shown be
 * default. If you don't want to see the chapter number, you have to set the
 * numberdepth to <VAR>0</VAR>.
 * <P>
 * Example:
 * <BLOCKQUOTE><PRE>
 * Paragraph title2 = new Paragraph("This is Chapter 2", new Font(Font.HELVETICA, 18, Font.BOLDITALIC, new Color(0, 0, 255)));
 * <STRONG>Chapter chapter2 = new Chapter(title2, 2);</STRONG>
 * <STRONG>chapter2.setNumberDepth(0);</STRONG>
 * Paragraph someText = new Paragraph("This is some text");
 * <STRONG>chapter2.add(someText);</STRONG>				
 * Paragraph title21 = new Paragraph("This is Section 1 in Chapter 2", new Font(Font.HELVETICA, 16, Font.BOLD, new Color(255, 0, 0)));
 * Section section1 = <STRONG>chapter2.addSection(title21);</STRONG>
 * Paragraph someSectionText = new Paragraph("This is some silly paragraph in a chapter and/or section. It contains some text to test the functionality of Chapters and Section.");
 * section1.add(someSectionText);
 * </PRE></BLOCKQUOTE>
 *
 * @author  bruno@lowagie.com
 * @version 0.22, 2000/02/02
 *
 * @since   iText0.30
 */

public class Chapter extends Section implements Element {

// constructors

	/**
	 * Constructs a new <CODE>Chapter</CODE>.
	 *
	 * @param	title		the Chapter title (as a <CODE>Paragraph</CODE>)
	 * @param	number		the Chapter number
	 *
	 * @since	iText0.30
	 */

	public Chapter(Paragraph title, int number) {
		super(title, 1);
		numbers = new ArrayList();
		numbers.add(new Integer(number));
	}

	/**
	 * Constructs a new <CODE>Chapter</CODE>.
	 *
	 * @param	title		the Chapter title (as a <CODE>String</CODE>)
	 * @param	number		the Chapter number
	 *
	 * @since	iText0.30
	 */

	public Chapter(String title, int number) {
		this(new Paragraph(title), number);
	}

// implementation of the Element-methods

    /**
     * Gets the type of the text element. 
     *
     * @return	a type
	 *
     * @since	iText0.30
     */

    public int type() {
		return Element.CHAPTER;
	}

// methods

	/**
	 * Returns a representation of this <CODE>Section</CODE>.
	 *
	 * @return	a <CODE>String</CODE>
	 *
	 * @since	iText0.30
	 */

	public String toString() {
		StringBuffer buf = new StringBuffer("<CHAPTER CHAPTERINDENTATION=\"");
		buf.append(sectionIndent);
		if (indentationLeft != 0) {
			buf.append("\" LEFTINDENTATION=\"");
			buf.append(indentationLeft);
		}
		if (indentationRight != 0) {
			buf.append("\" RIGHTINDENTATION=\"");
			buf.append(indentationRight);
		}
		buf.append("\">\n");
		buf.append("\t<TITLE>\n");
		buf.append("\t\t<NUMBERS DEPTH=\"");
		buf.append(numberDepth);
		buf.append("\">");
		buf.append(numbers.toString());
		buf.append("<NUMBERS>\n");
		if (title != null) {
			buf.append(title.toString());
		}
		buf.append("\t</TITLE>\n");
		for (Iterator i = iterator(); i.hasNext(); ) {
			buf.append(i.next().toString());
		}
		buf.append("\n</CHAPTER>\n");								
		return buf.toString();
	}
}