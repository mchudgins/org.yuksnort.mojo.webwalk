package org.yuksnort.web.util.validator;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

public abstract class ValidatorImpl implements Validator {
	private static final Logger	log	= Logger.getLogger( W3CValidator.class );

	protected Response		result	= null;
	protected HttpHeaders	headers	= null;
	protected HttpStatus	code	= null;
	protected boolean		valid	= false;
	protected int			warnings= 0;
	protected int			errors	= 0;
	protected String		host	= null;
	
	public ValidatorImpl()
		{
		}

	public ValidatorImpl setHost( final String host )
		{
		this.host	= host;
		
		return( this );
		}
	
	public String getHost()
		{
		return( this.host );
		}
	
	@Override
	public abstract void validate( final String html );

	public static class PostErrorHandler implements ResponseErrorHandler
		{

		private	HttpStatus	status;

		public PostErrorHandler()
			{
			super();
			}

		/* (non-Javadoc)
		 * @see org.springframework.web.client.ResponseErrorHandler#
		 * 	hasError(org.springframework.http.client.ClientHttpResponse)
		 */
		@Override
		public boolean hasError( final ClientHttpResponse response ) throws IOException
			{
			this.status	= response.getStatusCode();

			return false;
			}

		/* (non-Javadoc)
		 * @see org.springframework.web.client.ResponseErrorHandler#
		 * 	handleError(org.springframework.http.client.ClientHttpResponse)
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

	public Response getResponse()
		{
		return( this.result );
		}

	public boolean isValid()
		{
		return( this.valid );
		}

	public	HttpHeaders getResponseHeaders()
		{
		return( this.headers );
		}

	public	int getErrorCount()
		{
		return( this.errors );
		}

	public int getWarningCount()
		{
		return( this.warnings );
		}

	public HttpStatus getHttpStatus()
		{
		return( this.code );
		}

	@Override
	public String toString()
		{
		final StringBuffer	buf	= new StringBuffer();

		buf.append( "validator[ statusCode:  " );
		buf.append( this.code );
		if ( this.headers == null )
			buf.append( "null" );
		else
			{
			buf.append( ", response headers[ " );
			for ( final String key : this.headers.keySet() )
				{
				buf.append( key );
				buf.append( ":  " );
				buf.append( this.headers.get( key ) );
				buf.append( ", " );
				}
			buf.append( " ]" );
			}
		buf.append( ", response body[ " );
		buf.append( this.result );
		buf.append( " ]" );
		buf.append( " ]" );

		return( buf.toString() );
		}
	}

