/*
 * $Id$
 */
package org.yuksnort.web.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

/**
 * This class flings stuff RESTful-ly against a url.
 *
 * longer description of the class.
 *
 * @author	Mike Hudgins <mchudgins@dstsystems.com>
 * @version	Nov 14, 2012
 *
 */
public class SimpleUrlFlinger< T >
	{
	private static final Logger	log	= Logger.getLogger( SimpleUrlFlinger.class );

	private HttpHeaders	headers	= null;
	private	HttpMethod	method	= null;
	private HttpStatus 	code	= null;
	private	MediaType	requestedMedia = null;
	private T		response= null;
	private	long		millis	= 0;

	private	class PostErrorHandler implements ResponseErrorHandler
		{

		private	HttpStatus	status;

		public PostErrorHandler()
			{
			super();
			}

		/* (non-Javadoc)
		 * @see org.springframework.web.client.ResponseErrorHandler#hasError(org.springframework.http.client.ClientHttpResponse)
		 */
		@Override
		public boolean hasError( final ClientHttpResponse response ) throws IOException
			{
			this.status	= response.getStatusCode();

			return false;
			}

		/* (non-Javadoc)
		 * @see org.springframework.web.client.ResponseErrorHandler#handleError(org.springframework.http.client.ClientHttpResponse)
		 */
		@Override
		public void handleError( final ClientHttpResponse response ) throws IOException
			{
			// TODO Auto-generated method stub
			log.trace( "handleError" );
			}

		public	HttpStatus	getStatusCode()
			{
			return( this.status );
			}

		}
	
	public SimpleUrlFlinger( final String url, final HttpMethod verb,
			final MediaType acceptType,
			final Class< ? extends T > responseType, final boolean fCompress )
		{
		fling( url, verb, acceptType, responseType, fCompress );
		}
	

	public SimpleUrlFlinger( final String url, final HttpMethod verb,
			final MediaType acceptType,
			final Class< ? extends T > responseType )
		{
		fling( url, verb, acceptType, responseType, false );
		}
	
	public HttpMethod getRequestMethod()
		{
		return( this.method );
		}
	
	public MediaType getRequestedMedia()
		{
		return( this.requestedMedia );
		}
	
	public long getResponseTime()
		{
		return( this.millis );
		}
	
	protected void fling( final String url, final HttpMethod verb,
			final MediaType acceptType,
			final Class< ? extends T > responseType, final boolean fCompress )
		{
		this.method		= verb;
		this.requestedMedia	= acceptType;
		
		final HttpHeaders	headers	= new HttpHeaders();
		final RestTemplate	t	= new RestTemplate();
		final List< MediaType > media	= new ArrayList< MediaType >( 2 );
		final PostErrorHandler hdr	= new PostErrorHandler();

		t.setErrorHandler( hdr );

		media.add( this.requestedMedia );
		headers.setAccept( media );
		
		if ( fCompress )
			headers.add( "Accept-Encoding", "gzip, deflate" );
		
		final HttpEntity< String >	req	= new HttpEntity< String >( headers );
		final long			now	= System.currentTimeMillis();
		final HttpEntity< ? extends T > resp	= t.exchange( url,
							this.method,
							req,
							responseType );
		this.millis	= System.currentTimeMillis() - now;
		this.code	= hdr.getStatusCode();
		this.response	= resp.getBody();
		this.headers	= resp.getHeaders();
		}

	public HttpStatus getResponseCode()
		{
		return( this.code );
		}

	public T getResponse()
		{
		return( this.response );
		}

	public HttpHeaders getResponseHeaders()
		{
		return( this.headers );
		}
	}
