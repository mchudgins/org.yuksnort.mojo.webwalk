/*
 * $Id$
 */
package org.yuksnort.web.util.validator;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

// invoke w3c html validator:
// curl -v --header "Accept:  application/json" --data-urlencode "fragment@good.html" \
//		--data-urlencode "output=json" http://validator.w3.org/check


/**
 * verifies validity of HTML using w3c validator.
 *
 * Throws the html given in the object constructor up against the
 * w3c's website (http://validator.w3.org/check) to see if the html is valid.
 *
 * @author	Mike Hudgins <mchudgins@dstsystems.com>
 * @version	Nov 14, 2012
 *
 */

public class W3CValidator extends ValidatorImpl
	{
	private static final Logger	log	= Logger.getLogger( W3CValidator.class );
	private static final String	W3C	= "http://validator.w3.org/check";

	public W3CValidator()
		{
		setHost( W3C );
		}

	public W3CValidator( final String html )
		{
		setHost( W3C ).validate( html );
		}

	public W3CValidator( final String host, final String html )
		{
		setHost( host ).validate( html );
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
		mc.add( new FormHttpMessageConverter() );
		mc.add( new StringHttpMessageConverter() );
		mc.add( new MappingJacksonHttpMessageConverter() );
		t.setMessageConverters( mc );

		// set the error handler
		t.setErrorHandler( hdr );

		// I'd like a response in JSON, please
		media.add( MediaType.APPLICATION_JSON );
		headers.setAccept( media );

		final MultiValueMap<String, Object> form = new LinkedMultiValueMap<String, Object>();

		// html5boilerplate suggests this tag be in html (or on web server),
		// the w3c boys hate it when in html.
		// since we know w3c will report an error, remove it now.
		form.add( "fragment", html.replace( "<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge,chrome=1\" >", "" ) );
		form.add( "output", "json" ); // and the validator site makes us send this param when json is requested

		final HttpEntity< Object >	req	= new HttpEntity< Object >( form, headers );
		try
			{
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

			this.headers	= resp.getHeaders();
			this.result	= resp.getBody();

			final String valid = resp.getHeaders().get( "X-W3C-Validator-Status" ).get( 0 );

			this.valid	= "Valid".equalsIgnoreCase( valid );
			this.errors	= Integer.parseInt( resp.getHeaders().get( "X-W3C-Validator-Errors" ).get( 0 ) );
			this.warnings	= Integer.parseInt( resp.getHeaders().get( "X-W3C-Validator-Warnings" ).get( 0 ) );
			}
		catch ( final Exception e )
			{
			log.info( e.getMessage() );
			this.valid		= false;
			this.errors		= 1;
			this.result		= new Response();
			}
		}

	}
