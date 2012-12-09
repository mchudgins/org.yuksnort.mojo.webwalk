/*
 * $Id$
 */
package org.yuksnort.web.util.validator;

import org.apache.log4j.Logger;

/**
 * one line description.
 *
 * longer description of the class.
 *
 * @author	Mike Hudgins <mchudgins@dstsystems.com>
 * @version	Nov 14, 2012
 *
 */
public class Message
	{
	@SuppressWarnings("unused")
	private static final Logger	log	= Logger.getLogger( Message.class );

	private	int	lastLine;
	private	int	lastColumn;
	private	String	message;
	private	String	messageId;
	private	String	explanation;
	private	String	type;
	private	String	subType;
	private	String	extract;
	private int		hiliteStart;
	private int		hiliteLength;
	private int		firstColumn;

	public int getFirstColumn() {
		return firstColumn;
	}
	public void setFirstColumn(int firstColumn) {
		this.firstColumn = firstColumn;
	}

	/**
	 * @return the lastLine
	 */
	public int getLastLine()
		{
		return this.lastLine;
		}
	/**
	 * @param lastLine the lastLine to set
	 */
	public void setLastLine( final int lastLine )
		{
		this.lastLine = lastLine;
		}
	/**
	 * @return the lastColumn
	 */
	public int getLastColumn()
		{
		return this.lastColumn;
		}
	/**
	 * @param lastColumn the lastColumn to set
	 */
	public void setLastColumn( final int lastColumn )
		{
		this.lastColumn = lastColumn;
		}
	/**
	 * @return the message
	 */
	public String getMessage()
		{
		return this.message;
		}
	/**
	 * @param message the message to set
	 */
	public void setMessage( final String message )
		{
		this.message = message;
		}
	/**
	 * @return the messageId
	 */
	public String getMessageId()
		{
		return this.messageId;
		}
	public String getMessageid()
		{
		return( getMessageId() );
		}
	/**
	 * @param messageId the messageId to set
	 */
	public void setMessageId( final String messageId )
		{
		this.messageId = messageId;
		}
	public void setMessageid( final String messageId )
		{
		setMessageId( messageId );
		}
	/**
	 * @return the explanation
	 */
	public String getExplanation()
		{
		return this.explanation;
		}
	/**
	 * @param explanation the explanation to set
	 */
	public void setExplanation( final String explanation )
		{
		this.explanation = explanation;
		}
	/**
	 * @return the type
	 */
	public String getType()
		{
		return this.type;
		}
	/**
	 * @param type the type to set
	 */
	public void setType( final String type )
		{
		this.type = type;
		}

	/**
	 * @return the subType
	 */
	public String getSubtype()
		{
		return this.subType;
		}
	/**
	 * @param subType the subType to set
	 */
	public void setSubtype( final String subType )
		{
		this.subType = subType;
		}
	
	// used by validator.nu
	public String getSubType()
		{
		return( this.subType );
		}
	
	public void setSubType( final String subType )
		{
		this.subType = subType;
		}

	public String getExtract() {
		return extract;
	}
	public void setExtract(String extract) {
		this.extract = extract;
	}
	public int getHiliteStart() {
		return hiliteStart;
	}
	public void setHiliteStart(int hiliteStart) {
		this.hiliteStart = hiliteStart;
	}
	public int getHiliteLength() {
		return hiliteLength;
	}
	public void setHiliteLength(int hiliteLength) {
		this.hiliteLength = hiliteLength;
	}

	@Override
	public String toString()
		{
		final StringBuffer	buf	= new StringBuffer();

		buf.append( "message  [ lastLine:  " );
		buf.append( this.lastLine );
		buf.append( ", lastColumn:  " );
		buf.append( this.lastColumn );
		buf.append( ", message:  " );
		buf.append( this.message );
		buf.append( ", messageid:  " );
		buf.append( this.messageId );
		buf.append( ", explanation:  " );
		buf.append( this.explanation );
		buf.append( ", type:  " );
		buf.append( this.type );
		buf.append( ", extract:  " );
		buf.append( this.extract );
		buf.append( ", hiliteStart:  " );
		buf.append( this.hiliteStart );
		buf.append( ", hiliteLength:  " );
		buf.append( this.hiliteLength );
		buf.append( " ]" );
		return( buf.toString() );
		}
	}
