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
 * @version	Nov 13, 2012
 *
 */
public class Response
	{
	@SuppressWarnings("unused")
	private static final Logger	log	= Logger.getLogger( Response.class );
	private	String	url;
	private Message[] messages;
	private Source source;

	public void setUrl( final String url )
		{
		this.url	= url;
		}

	public String getUrl()
		{
		return( this.url );
		}

	public void setMessages( final Message[] msgs )
		{
		this.messages	= msgs;
		}
	public Message[] getMessages()
		{
		return( this.messages );
		}

	public void setSource( final Source src )
		{
		this.source	= src;
		}
	public Source getSource()
		{
		return( this.source );
		}

	@Override
	public String toString()
		{
		final StringBuffer	buf	= new StringBuffer();

		buf.append( "Response[ url:  " );
		buf.append( this.url );
		buf.append( " ], messages[ " );
		if ( this.messages == null )
			buf.append( "null" );
		else
			{
			for ( int i = 0; i < this.messages.length; i++ )
				{
				buf.append( this.messages[ i ] );
				buf.append( ", " );
				}
			}
		buf.append( " ], Source:  " );
		buf.append( this.source );

		buf.append( " ] ]" );

		return( buf.toString() );
		}
	}
