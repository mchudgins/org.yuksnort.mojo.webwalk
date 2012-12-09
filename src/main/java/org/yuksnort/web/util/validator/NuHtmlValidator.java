/*
 * $Id$
 */
package org.yuksnort.web.util.validator;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;


/**
 * verifies validity of HTML using validator.nu
 *
 * Throws the html given in the object constructor up against
 * validator.nu to see if the html is valid.
 *
 * Alternatively, using curl
 * curl -v  -F "out=json" -F parser=html5 -F "doc=@good.html;type=text/html" http://bigred.local:8888/
 * 
 * @author	Mike Hudgins <mchudgins@dstsystems.com>
 * @version	Nov 14, 2012
 *
 */

public class NuHtmlValidator extends ValidatorImpl
	{
	private static final Logger	log	= Logger.getLogger( NuHtmlValidator.class );
	private static final String	HOST = "http://localhost:8888";

	public NuHtmlValidator()
		{
		setHost( HOST );
		}

	public NuHtmlValidator( final String html )
		{
		setHost( HOST ).validate( html );
		}

	public NuHtmlValidator( final String host, final String html )
		{
		setHost( host ).validate( html );
		}
	
	public static class HtmlPart
		{
		private	final String	html;
		
		public HtmlPart( final String html )
			{
			this.html	= html;
			}
		
		public String getHtml()
			{
			return( this.html );
			}
		}
	
	public static class HtmlPartConverter implements HttpMessageConverter< HtmlPart >
		{

		@Override
		public boolean canRead(Class<?> arg0, MediaType arg1) {
			return false;
		}

		@Override
		public boolean canWrite(Class<?> arg0, MediaType arg1) {
			if ( arg0.isAssignableFrom( HtmlPart.class ) )
				return( true );
			
			return false;
		}

		@Override
		public List<MediaType> getSupportedMediaTypes() {
			return null;
		}

		@Override
		public HtmlPart read(Class<? extends HtmlPart> arg0, HttpInputMessage arg1)
				throws IOException, HttpMessageNotReadableException {
			return null;
		}

		@Override
		public void write( final HtmlPart html, MediaType mt, HttpOutputMessage resp )
				throws IOException, HttpMessageNotWritableException {
			resp.getHeaders().setContentDispositionFormData( "doc", "hut.html" );
			resp.getHeaders().setContentType( MediaType.TEXT_HTML );
			resp.getHeaders().setContentLength( html.getHtml().length() );
			resp.getBody().write( html.getHtml().getBytes() );
			}
			
		}
	
	public  static class SpecialFormConverter extends FormHttpMessageConverter
		{
		protected String getFilename( Object part )
			{
			String	name	= super.getFilename( part );

			if ( part instanceof InputStream )
				return( "validate.html" );
			
			return( name );
			}
		}

	@Override
	public	void	validate( final String html )
		{
		final HttpHeaders		headers	= new HttpHeaders();
		final RestTemplate		t		= new RestTemplate();
		final List< MediaType > media	= new ArrayList< MediaType >( 2 );
		final PostErrorHandler	hdr		= new PostErrorHandler();

		// work-around springframework issue:
		// there's a conflict with the pre-constructed message converters
		// so re-initialize the resttemplate with only string & form converters
		final List< HttpMessageConverter< ? > > mc	= new ArrayList< HttpMessageConverter< ? > >();
		FormHttpMessageConverter fc		= new SpecialFormConverter();
		fc.addPartConverter( new HtmlPartConverter() );
		
		mc.add( new StringHttpMessageConverter() );
		mc.add( new MappingJacksonHttpMessageConverter() );
		mc.add( fc );
		t.setMessageConverters( mc );

		// set the error handler
		t.setErrorHandler( hdr );
		
		// sending multipart/form-data
//		headers.setContentType( new MediaType( "multipart", "form-data" ) );
		headers.setContentType( MediaType.MULTIPART_FORM_DATA );
		
		// I'd like a response in JSON, please
		media.add( MediaType.APPLICATION_JSON );
		headers.setAccept( media );

		final MultiValueMap<String, Object> form = new LinkedMultiValueMap<String, Object>();

		form.add( "out", "json" ); // the validator site makes us send this param when json is requested
										// it must be BEFORE the "part"
		form.add( "parser", "html5" );

		log.trace( "validating:  " + html.replace( "<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge,chrome=1\" >", "" ) );
		// html5boilerplate suggests this tag be in html (or on web server),
		// the w3c boys hate it when in html.
		// since we know w3c will report an error, remove it now.
//		form.add( "doc", html.replace( "<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge,chrome=1\" >", "" ).concat( ";type=text/html" ) );
		form.add( "doc",
				new HtmlPart(
						html.replace( "<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge,chrome=1\" >", "" ) ) );
		
		log.trace( "using:  " + this.host );

		final HttpEntity< MultiValueMap< String, Object > >	req
					= new HttpEntity< MultiValueMap< String, Object > >( form, headers );
		try
			{
			if ( log.isTraceEnabled() )
				{
				final HttpEntity< String >	test		= t.postForEntity( this.host,
							req, String.class, form );
				log.trace( "Response:  " + test );
				}
			
			final HttpEntity< Response >	resp	= t.exchange( this.host,
							HttpMethod.POST,
							req,
							Response.class );

			this.code	= hdr.getStatusCode();
			if ( hdr.getStatusCode() != HttpStatus.OK )
				{
				log.warn( "****** W3C Response Headers ******" );
				log.warn( "Status Code:  " + hdr.getStatusCode() );

				final HttpHeaders	respHeaders	= resp.getHeaders();
				for ( final String key : respHeaders.keySet() )
					{
					log.warn( key + ":  " + respHeaders.get( key ) );
					}
				log.warn( "****** test result unreliable ******" );
				}
			else
				{
				this.headers	= resp.getHeaders();
				this.result		= resp.getBody();

				this.valid		= true;
				this.errors		= 0;
				this.warnings	= 0;
				
				log.trace( "there are " + this.result.getMessages().length + " messages." );				
				for ( Message m : this.result.getMessages() )
					{
					log.trace( m );					
					if ( m.getType().equalsIgnoreCase( "error" ) )
						{
						this.valid	= false;
						this.errors++;
						}
					if ( m.getType().equalsIgnoreCase( "warning"  ) )
						this.warnings++;
					}
				}
			}
		catch ( final RestClientException e )
			{
			log.info( e.getClass().getName() + ":  " + e.getMessage() );
			this.valid		= false;
			this.errors		= 1;
			this.result		= new Response();
			}
		catch ( final Exception e )
			{
			log.info( e.getClass().getName() + ":  " + e.getMessage() );
			this.valid		= false;
			this.errors		= 1;
			this.result		= new Response();
			}
		}

	}
