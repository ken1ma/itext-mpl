/*
 * @(#)Document.java				0.37 2000/10/05
 *       release iText0.3:			0.26 2000/02/14
 *       release iText0.35:			0.33 2000/08/11
 *       release iText0.36:			0.36 2000/09/08
 *       release iText0.37:			0.37 2000/10/05
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

import java.util.Date;

/**
 * A generic Document class.
 * <P>
 * All kinds of Text-elements can be added to a <CODE>HTMLDocument</CODE>.
 * The <CODE>Document</CODE> signals all the listeners when an element
 * has been added.
 * <P>
 * Remark:
 * <OL>
 *     <LI>Once a document is created you can add some meta information.
 *     <LI>You can also set the headers/footers.
 *     <LI>You have to open the document before you can write content.
 *     <LI>You can only write content (no more meta-formation!) once a document is opened.
 *     <LI>When you change the header/footer on a certain page, this will be effective starting on the next page.
 *     <LI>Ater closing the document, every listener (as well as its <CODE>OutputStream</CODE>) is closed too.
 * </OL>
 * Example:
 * <BLOCKQUOTE><PRE>
 * // creation of the document with a certain size and certain margins
 * <STRONG>Document document = new Document(PageSize.A4, 50, 50, 50, 50);</STRONG>
 * try {
 *    // creation of the different writers
 *    HtmlWriter.getInstance(<STRONG>document</STRONG>, System.out);
 *    PdfWriter.getInstance(<STRONG>document</STRONG>, new FileOutputStream("text.pdf"));
 *
 *    // we add some meta information to the document
 *    <STRONG>document.addAuthor("Bruno Lowagie");</STRONG>
 *    <STRONG>document.addSubject("This is the result of a Test.");</STRONG>
 *
 *    // we define a header and a footer
 *    HeaderFooter header = new HeaderFooter(new Phrase("This is a header."), false);
 *    HeaderFooter footer = new HeaderFooter(new Phrase("This is page "), new Phrase("."));
 *    footer.setAlignment(Element.ALIGN_CENTER);
 *    <STRONG>document.setHeader(header);</STRONG>
 *	  <STRONG>document.setFooter(footer);</STRONG>
 *    // we open the document for writing
 *    <STRONG>document.open();</STRONG>		 
 *    <STRONG>document.add(new Paragraph("Hello world"));</STRONG>
 * }
 * catch(DocumentException de) {
 *    System.err.println(de.getMessage());
 * }
 * <STRONG>document.close();</CODE>
 * </PRE></BLOCKQUOTE>
 *
 * @author  bruno@lowagie.com
 * @version 0.37, 2000/10/05
 *
 * @since   iText0.30
 */

public class Document implements DocListener {

// membervariables

	/** The DocListener. */
	private ArrayList listeners = new ArrayList();

	/** Is the document open or not? */
	protected boolean open;

	/** Has the document allready been closed? */
	protected boolean close;

// membervariables concerning the layout

	/** The size of the page. */
	protected Rectangle pageSize;

	/** The watermark on the pages. */
	protected Watermark watermark = null;

	/** margin in x direction starting from the left */
	protected int marginLeft = 0;

	/** margin in x direction starting from the right */
	protected int marginRight = 0;

	/** margin in y direction starting from the top */
	protected int marginTop = 0;

	/** margin in y direction starting from the bottom */
	protected int marginBottom = 0;

	// headers, footers

	/** Current pagenumber */
	protected int pageN = 0;

	/** This is the textual part of a Page; it can contain a header */
	protected HeaderFooter header = null;

	/** This is the textual part of the footer */
	protected HeaderFooter footer = null;

// constructor

	/**
	 * Constructs a new <CODE>Document</CODE>-object.
	 *
	 * @since	iText0.30
	 */

	public Document() {
		this(PageSize.A4);
	}

	/**
	 * Constructs a new <CODE>Document</CODE>-object.
	 *
	 * @param	pageSize	the pageSize
	 *
	 * @since	iText0.30
	 */

	public Document(Rectangle pageSize) {
		this(pageSize, 30, 30, 30, 30);
	}

	/**
	 * Constructs a new <CODE>Document</CODE>-object.
	 *
	 * @param	pageSize		the pageSize
	 * @param	marginLeft		the margin on the left
	 * @param	marginRight		the margin on the right
	 * @param	marginTop		the margin on the top
	 * @param	marginBottom	the margin on the bottom
	 *
	 * @since	iText0.30
	 */

	public Document(Rectangle pageSize, int marginLeft, int marginRight, int marginTop, int marginBottom) {
		this.pageSize = pageSize;		
		this.marginLeft = marginLeft;
		this.marginRight = marginRight;
		this.marginTop = marginTop;
		this.marginBottom = marginBottom;
	}

	/**
	 * Closes the <CODE>Document</CODE> when gc is invoked.
	 *
     * @since   iText0.30
	 */

	public void finalize() {
		close();
	}

// listener methods

	/**
	 * Adds a <CODE>DocListener</CODE> to the <CODE>Document</CODE>.
	 *
	 * @param	listener	the new DocListener.
	 * @return	<CODE>void</CODE>
	 *
	 * @since	iText0.30
	 */

	public final void addDocListener(DocListener listener) {
		listeners.add(listener);
	}

	/**
	 * Removes a <CODE>DocListener</CODE> from the <CODE>Document</CODE>.
	 *
	 * @param	listener	the DocListener that has to be removed.
	 * @return	<CODE>void</CODE>
	 *
	 * @since	iText0.30
	 */

	public final void removeDocListener(DocListener listener) {
		listeners.remove(listener);
	}

// methods implementing the DocListener interface

    /**
     * Adds an <CODE>Element</CODE> to the <CODE>Document</CODE>. 
     *
	 * @return	<CODE>true</CODE> if the element was added, <CODE>false</CODE> if not
	 * @throws	DocumentException	when a document isn't open yet, or has been closed
	 *
     * @since   iText0.30
     */

    public boolean add(Element element) throws DocumentException {
		if (close) {
			throw new DocumentException("The document has been closed. You can't add any Elements.");
		}
		int type = element.type();
		if (open) {
			if (! (type == Element.CHUNK ||
				type == Element.PHRASE ||
				type == Element.PARAGRAPH ||
				type == Element.TABLE ||
				type == Element.ANCHOR ||
				type == Element.ANNOTATION ||
				type == Element.CHAPTER ||
				type == Element.SECTION ||
				type == Element.LIST ||
				type == Element.LISTITEM ||
				type == Element.RECTANGLE ||
				type == Element.PNG ||
				type == Element.JPEG ||
				type == Element.GIF ||
				type == Element.GRAPHIC)) {
				throw new DocumentException("The document is open; you can only add Elements with content.");
			}
		}
		else {
			if (! (type == Element.HEADER ||
				type == Element.TITLE ||
				type == Element.SUBJECT ||
				type == Element.KEYWORDS ||
				type == Element.AUTHOR ||
				type == Element.PRODUCER ||
				type == Element.CREATIONDATE)) {
				throw new DocumentException("The document is not open yet; you can only add Meta information.");
			}
		}
		boolean success = false;
		DocListener listener;
		for (Iterator iterator = listeners.iterator(); iterator.hasNext(); ) {
			listener = (DocListener) iterator.next();
			success |= listener.add(element);
		}
		return success;
	}

	/**
	 * Opens the document.
	 * <P>
	 * Once the document is opened, you can't write any Header- or Meta-information
	 * anymore. You have to open the document before you can begin to add content
	 * to the body of the document.
	 *
	 * @return	<CODE>void</CODE>
	 *
	 * @since	iText0.30
	 */

	public void open() {
		if (! close) {
			open = true;
		}
		DocListener listener;
		for (Iterator iterator = listeners.iterator(); iterator.hasNext(); ) {
			listener = (DocListener) iterator.next();
			listener.setPageSize(pageSize);
			listener.setMargins(marginLeft, marginRight, marginTop, marginBottom);
			listener.open();
		}
	}			  

	/**
	 * Sets the pagesize.
	 *
	 * @param	pageSize	the new pagesize
	 * @return	a <CODE>boolean</CODE>
	 *
	 * @since	iText0.30
	 */

	public boolean setPageSize(Rectangle pageSize) {
		this.pageSize = pageSize;
		DocListener listener;
		for (Iterator iterator = listeners.iterator(); iterator.hasNext(); ) {
			listener = (DocListener) iterator.next();
			listener.setPageSize(pageSize);
		}
		return true;
	}

    /**
     * Sets the <CODE>Watermark</CODE>. 
     *
	 * @return	<CODE>true</CODE> if the element was added, <CODE>false</CODE> if not.
	 *
     * @since   iText0.31
     */

    public boolean add(Watermark watermark) {
		this.watermark = watermark;
		DocListener listener;
		for (Iterator iterator = listeners.iterator(); iterator.hasNext(); ) {
			listener = (DocListener) iterator.next();
			listener.add(watermark);
		}
		return true;
	}

	/**
	 * Removes the <CODE>Watermark</CODE>.
	 *
	 * @since	iText0.31;
	 */

	public void removeWatermark() {
		this.watermark = null;	  
		DocListener listener;
		for (Iterator iterator = listeners.iterator(); iterator.hasNext(); ) {
			listener = (DocListener) iterator.next();
			listener.removeWatermark();
		}
	}

	/**
	 * Sets the margins.
	 *			   							
	 * @param	marginLeft		the margin on the left
	 * @param	marginRight		the margin on the right
	 * @param	marginTop		the margin on the top
	 * @param	marginBottom	the margin on the bottom
	 * @return	a <CODE>boolean</CODE>
	 *
	 * @since	iText0.30
	 */

	public boolean setMargins(int marginLeft, int marginRight, int marginTop, int marginBottom) {
		this.marginLeft = marginLeft;
		this.marginRight = marginRight;
		this.marginTop = marginTop;
		this.marginBottom = marginBottom;
		DocListener listener;
		for (Iterator iterator = listeners.iterator(); iterator.hasNext(); ) {
			listener = (DocListener) iterator.next();
			listener.setMargins(marginLeft, marginRight, marginTop, marginBottom);
		}
		return true;
	}

    /**
     * Signals that an new page has to be started. 
     *
	 * @return	<CODE>true</CODE> if the page was added, <CODE>false</CODE> if not.
	 * @throws	DocumentException	when a document isn't open yet, or has been closed
	 *
     * @since   iText0.30
     */

    public boolean newPage() throws DocumentException {
		if (!open || close) {
			return false;
		}
		DocListener listener;
		for (Iterator iterator = listeners.iterator(); iterator.hasNext(); ) {
			listener = (DocListener) iterator.next();
			listener.newPage();
		}
		return true;
	}

	/**
	 * Changes the header of this document.
	 * 
	 * @param	header		the new header
	 * @return	<CODE>void</CODE>
	 *
	 * @since	iText0.30
	 */

	public void setHeader(HeaderFooter header) {
		this.header = header;
		DocListener listener;
		for (Iterator iterator = listeners.iterator(); iterator.hasNext(); ) {
			listener = (DocListener) iterator.next();
			listener.setHeader(header);
		}
	}

	/**
	 * Resets the header of this document.
	 * 
	 * @param	header		the new header
	 * @return	<CODE>void</CODE>
	 *
	 * @since	iText0.30
	 */

	public void resetHeader() {
		this.header = null;			 
		DocListener listener;
		for (Iterator iterator = listeners.iterator(); iterator.hasNext(); ) {
			listener = (DocListener) iterator.next();
			listener.resetHeader();
		}
	}

	/**
	 * Changes the footer of this document.
	 * 
	 * @param	footer		the new footer
	 * @return	<CODE>void</CODE>
	 *
	 * @since	iText0.30
	 */

	public void setFooter(HeaderFooter footer) {
		this.footer = footer;	
		DocListener listener;
		for (Iterator iterator = listeners.iterator(); iterator.hasNext(); ) {
			listener = (DocListener) iterator.next();
			listener.setFooter(footer);
		}
	}

	/**
	 * Resets the footer of this document.
	 *
	 * @return	<CODE>void</CODE>
	 *
	 * @since	iText0.30
	 */

	public void resetFooter() {
		this.footer = footer;		 
		DocListener listener;
		for (Iterator iterator = listeners.iterator(); iterator.hasNext(); ) {
			listener = (DocListener) iterator.next();
			listener.resetFooter();
		}
	}
	
	/**
	 * Sets the page number to 0.
	 *
	 * @return	<CODE>void</CODE>
	 *
	 * @since	iText0.30
	 */

	public void resetPageCount() {
		pageN = 0;				
		DocListener listener;
		for (Iterator iterator = listeners.iterator(); iterator.hasNext(); ) {
			listener = (DocListener) iterator.next();
			listener.resetPageCount();
		}
	}

	/**
	 * Sets the page number.
	 *
	 * @param	pageN		the new page number
	 * @return	<CODE>void</CODE>
	 *
	 * @since	iText0.30
	 */

	public void setPageCount(int pageN) {
		this.pageN = pageN;		   
		DocListener listener;
		for (Iterator iterator = listeners.iterator(); iterator.hasNext(); ) {
			listener = (DocListener) iterator.next();
			listener.setPageCount(pageN);
		}
	}

	/**
	 * Closes the document.
	 * <B>
	 * Once all the content has been written in the body, you have to close
	 * the body. After that nothing can be written to the body anymore.
	 *
	 * @return	<CODE>void</CODE>
	 * 
	 * @since	iText0.30
	 */

	public void close() {
		if (! close) {
			open = false;
			close = true;
		}
		DocListener listener;
		for (Iterator iterator = listeners.iterator(); iterator.hasNext(); ) {
			listener = (DocListener) iterator.next();
			listener.close();
		}
	}

// methods concerning the header or some meta information

	/**
	 * Adds a user defined header to the document.
	 *
	 * @param	name	the name of the header
	 * @param	content	the content of the header
	 * @return	<CODE>true</CODE> if successful, <CODE>false</CODE> otherwise
	 *
	 * @since	iText0.30	
	 */

	public boolean addHeader(String name, String content) {
		try {
			return add(new Header(name, content));
		}
		catch(DocumentException de) {
			return false;
		}
	}

	/**
	 * Adds the title to a Document.
	 *
	 * @param	title	the title
	 * @return	<CODE>true</CODE> if successful, <CODE>false</CODE> otherwise
	 *
	 * @since	iText0.30
	 */

	public boolean addTitle(String title) {
		try {
			return add(new Meta(Element.TITLE, title));
		}
		catch(DocumentException de) {
			return false;
		}
	}

	/**
	 * Adds the subject to a Document.
	 *
	 * @param	subject		the subject
	 * @return	<CODE>true</CODE> if successful, <CODE>false</CODE> otherwise
	 *
	 * @since	iText0.30
	 */

	public boolean addSubject(String subject) {
		try {
			return add(new Meta(Element.SUBJECT, subject));
		}
		catch(DocumentException de) {
			return false;
		}
	}

	/**
	 * Adds the keywords to a Document.
	 *
	 * @param	keuwords	the keywords
	 * @return	<CODE>true</CODE> if successful, <CODE>false</CODE> otherwise
	 *
	 * @since	iText0.30
	 */

	public boolean addKeywords(String keywords) {
		try {
			return add(new Meta(Element.KEYWORDS, keywords));
		}
		catch(DocumentException de) {
			return false;
		}
	}

	/**
	 * Adds the author to a Document.
	 *
	 * @param	author		the name of the author
	 * @return	<CODE>true</CODE> if successful, <CODE>false</CODE> otherwise
	 *
	 * @since	iText0.30
	 */

	public boolean addAuthor(String author) {
		try {
			return add(new Meta(Element.AUTHOR, author));
		}
		catch(DocumentException de) {
			return false;
		}
	}

	/**
	 * Adds the producer to a Document.
	 *
	 * @return	<CODE>true</CODE> if successful, <CODE>false</CODE> otherwise
	 *
	 * @since	iText0.30
	 */

	public boolean addProducer() {
		try {
			return add(new Meta(Element.PRODUCER, "iText by Bruno Lowagie"));
		}
		catch(DocumentException de) {
			return false;
		}
	}

	/**
	 * Adds the current date and time to a Document.
	 *
	 * @return	<CODE>true</CODE> if successful, <CODE>false</CODE> otherwise
	 *
	 * @since	iText0.30
	 */

	public boolean addCreationDate() {
		try {
			return add(new Meta(Element.CREATIONDATE, new Date().toString()));
		}
		catch(DocumentException de) {
			return false;
		}
	}

// methods to get the layout of the document.

	/**
	 * Returns the left margin.
	 *
	 * @return	the left margin
	 *
	 * @since	iText0.30
	 */

	public int leftMargin() {
		return marginLeft;
	}

	/**
	 * Return the right margin.
	 *
	 * @return	the right margin
	 *
	 * @since	iText0.30
	 */

	public int rightMargin() {
		return marginRight;
	}

	/**
	 * Returns the top margin.
	 *
	 * @return	the top margin
	 *
	 * @since	iText0.30
	 */

	public int topMargin() {
		return marginTop;
	}

	/**
	 * Returns the bottom margin.
	 *
	 * @return	the bottom margin
	 *
	 * @since	iText0.30
	 */

	public int bottomMargin() {
		return marginBottom;
	}

	/**
	 * Returns the lower left x-coordinate.
	 *
	 * @return	the lower left x-coordinate
	 *
	 * @since	iText0.30
	 */

	public int left() {
		return pageSize.left(marginLeft);
	}

	/**
	 * Returns the upper right x-coordinate.
	 *
	 * @return	the upper right x-coordinate
	 *
	 * @since	iText0.30
	 */

	public int right() {
		return pageSize.right(marginRight);
	}

	/**
	 * Returns the upper right y-coordinate.
	 *
	 * @return	the upper right y-coordinate
	 *
	 * @since	iText0.30
	 */

	public int top() {
		return pageSize.top(marginTop);
	}

	/**
	 * Returns the lower left y-coordinate.
	 *
	 * @return	the lower left y-coordinate
	 *
	 * @since	iText0.30
	 */

	public int bottom() {
		return pageSize.bottom(marginBottom);
	}

	/**
	 * Returns the lower left x-coordinate considering a given margin.
	 *
	 * @param	margin			a margin
	 * @return	the lower left x-coordinate
	 *
	 * @since	iText.30
	 */

	public int left(int margin) {
		return pageSize.left(marginLeft + margin);
	}

	/**
	 * Returns the upper right x-coordinate, considering a given margin.
	 *
	 * @param	margin			a margin
	 * @return	the upper right x-coordinate
	 *
	 * @since	iText0.30
	 */

	public int right(int margin) {
		return pageSize.right(marginRight + margin);
	}

	/**
	 * Returns the upper right y-coordinate, considering a given margin.
	 *
	 * @param	margin			a margin
	 * @return	the upper right y-coordinate
	 *
	 * @since	iText0.30
	 */

	public int top(int margin) {
		return pageSize.top(marginTop + margin);
	}

	/**
	 * Returns the lower left y-coordinate, considering a given margin.
	 *										   
	 * @param	margin			a margin
	 * @return	the lower left y-coordinate
	 *
	 * @since	iText0.30
	 */

	public int bottom(int margin) {
		return pageSize.bottom(marginBottom + margin);
	}
}