/**
 * $Id$
 *
 * 
 */
package org.yuksnort.mojo;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.yuksnort.web.util.SimpleUrlFlinger;

/**
 * @author Mike Hudgins <mchudgins@dstsystems.com>
 *
 */
public class PageInfo
	{
	@SuppressWarnings( "unused" )
	private static final Logger	log	= Logger.getLogger( PageInfo.class );
	
	private	Timestamp	retrievedDT		= null;
	private	Map< Request, RestMethodInfo > list	= new HashMap< Request, RestMethodInfo >();
	
	public static class Request
		{
		private	final HttpMethod	method;
		private	final MediaType		mediaType;
		
		public Request( final HttpMethod m, final MediaType t )
			{
			this.method	= m;
			this.mediaType	= t;
			}
		
		public boolean equals( Request o )
			{
			if ( o == null )
				return( false );
			else
				return( ( o.method == this.method ) && ( o.mediaType == this.mediaType ) );
			}
		}
	
	
	public static class RestMethodInfo
		{
		private	final HttpMethod method;
		private	final MediaType	mediaRequested;
		private	final long	responseMillis;
		private	final HttpStatus result;
		private	final MediaType	mediaSupported;
		private	final boolean	fSupportsCompression;
		
		public RestMethodInfo( final HttpMethod m,
				final MediaType t,
				final long millis,
				final HttpStatus rc,
				final MediaType supported,
				final boolean fSupportsCompression )
			{
			this.method		= m;
			this.mediaRequested	= t;
			this.responseMillis	= millis;
			this.result		= rc;
			this.mediaSupported	= supported;
			this.fSupportsCompression = fSupportsCompression;
			}
		
		public RestMethodInfo( final SimpleUrlFlinger< ? > access )
			{
			this.method		= access.getRequestMethod();
			this.mediaRequested	= access.getRequestedMedia();
			this.responseMillis	= access.getResponseTime();
			this.result		= access.getResponseCode();
			this.mediaSupported	= access.getResponseHeaders().getContentType();
			this.fSupportsCompression = false;
			}

		/**
		 * @return the method
		 */
		public HttpMethod getMethod()
			{
			return method;
			}

		/**
		 * @return the mediaRequested
		 */
		public MediaType getMediaRequested()
			{
			return mediaRequested;
			}

		/**
		 * @return the responseMillis
		 */
		public long getResponseMillis()
			{
			return responseMillis;
			}

		/**
		 * @return the result
		 */
		public HttpStatus getResult()
			{
			return result;
			}

		/**
		 * @return the mediaSupported
		 */
		public MediaType getMediaSupported()
			{
			return mediaSupported;
			}

		/**
		 * @return the fSupportsCompression
		 */
		public boolean isfSupportsCompression()
			{
			return fSupportsCompression;
			}

		}
	
	/**
	 * @return the retrievedDT
	 */
	public Timestamp getRetrievedDT()
		{
		return retrievedDT;
		}

	public PageInfo( final long retrievalTimeMillis )
		{
		this.retrievedDT	= new Timestamp( retrievalTimeMillis );
		}
	
	public void addRestMethodInfo( SimpleUrlFlinger< ? > access )
		{
		RestMethodInfo	rest	= new RestMethodInfo( access );
		this.list.put( new Request( access.getRequestMethod(), access.getRequestedMedia() ), rest );
		}
	
	public String toString()
		{
		StringBuffer	buf	= new StringBuffer();
		
		for ( Request r : this.list.keySet() )
			{
			RestMethodInfo	data	= this.list.get( r );
			
			buf.append( "Service Time:  " );
			buf.append( data.getResponseMillis() );
			buf.append( ", Request Method:  " );
			buf.append( data.getMethod() );
			buf.append( ", Response Code:  " );
			buf.append( data.getResult() );
			buf.append( ", Media Requested:  " );
			buf.append( data.getMediaRequested() );
			buf.append( ", Media Served:  " );
			buf.append( data.getMediaSupported() );
			buf.append( "\n" );
			}
		
		return( buf.toString() );
		}

	}
