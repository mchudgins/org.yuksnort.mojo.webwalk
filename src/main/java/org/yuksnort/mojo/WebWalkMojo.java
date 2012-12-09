/**
 * $Id$
 * 
 * 
 */

package org.yuksnort.mojo;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.yuksnort.web.util.SimpleUrlFlinger;
import org.yuksnort.web.util.validator.Validator;
import org.yuksnort.web.util.validator.ValidatorFactory;

/**
 * @author Mike Hudgins <mchudgins@dstsystems.com>
 * 
 * @phase prepare-package
 * @goal walk
 * @threadSafe
 */

public class WebWalkMojo extends AbstractMojo
	{

	/**
	 * target URL where walk begins
	 * 
	 * @parameter description="root URL where walk commences"
	 * @required
	 */
	private String		url;

	/**
	 * @parameter expression="${webappDirectory}"
	 *            default-value="${project.build.directory}/${project.build.finalName}"
	 */
	private String		webapp;

	/**
	 * @parameter default-value="${project.build.directory}"
	 */
	private String		buildDir;

	private Validator	htmlValidator	= null;
	
	private Set< String >	visited		= new HashSet< String >();
	private Set< URL >	links		= new HashSet< URL >();
	private Map< URL, PageInfo  > pageInfo	= new HashMap< URL, PageInfo >();

	public WebWalkMojo() throws Exception
		{
		this.htmlValidator = new ValidatorFactory().getObject();
		}

	/*
	 * (non-Javadoc)
	 * @see org.apache.maven.plugin.Mojo#execute()
	 */
	public void execute() throws MojoExecutionException, MojoFailureException
		{
		this.getLog().warn( "executing mojo" );

		this.getLog().info( "target URL:  " + this.url );
		
		try
			{
			URL target	= new URL( this.url );
			getLog().info( target.getHost() );
			getLog().info( target.getPath() );
			getLog().info( target.getRef() );
			}
		catch ( MalformedURLException e )
			{
			// TODO Auto-generated catch block
			e.printStackTrace();
			}

		SimpleUrlFlinger< String > webpage = new SimpleUrlFlinger< String >( this.url,
				HttpMethod.GET, MediaType.TEXT_HTML, String.class );

		this.htmlValidator.validate( webpage.getResponse() );
		if ( this.htmlValidator.isValid() )
			{
			this.getLog().info( "looks good." );
			}
		else
			{
			this.getLog().info( webpage.getResponse().toString() );
			this.getLog().info( this.htmlValidator.getResponse().toString() );
			}

		if ( webpage.getResponseCode() == HttpStatus.OK )
			{
			this.getLog().info( "webpage content length:  " + Long.toString( webpage.getResponseHeaders().getContentLength() ) );
			this.getLog().info( "webpage.length:  " + Integer.toString( webpage.getResponse().length() ) );

			SimpleUrlFlinger< String > compressed = new SimpleUrlFlinger< String >( this.url,
					HttpMethod.GET, MediaType.TEXT_HTML, String.class, true );
			this.getLog().info( "Content Length:  " + Long.toString( compressed.getResponseHeaders().getContentLength() ) );
			this.getLog().info( "Response Length:  " + Integer.toString( compressed.getResponse().length() ) );

			SimpleUrlFlinger< String > head = new SimpleUrlFlinger< String >( this.url,
				HttpMethod.HEAD, MediaType.TEXT_HTML, String.class );
			this.getLog().info( head.getResponseCode().toString() );
			this.getLog().info( Long.toString( head.getResponseHeaders().getContentLength() ) );
			
			getMediaTypeResponse( new MediaType[] { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML },
				webpage.getResponse() );
			
			getRestResponse( HttpMethod.OPTIONS, MediaType.TEXT_HTML );
			getRestResponse( HttpMethod.TRACE, MediaType.TEXT_HTML );
			getRestResponse( HttpMethod.DELETE, MediaType.TEXT_HTML );
			getRestResponse( HttpMethod.POST, MediaType.TEXT_HTML );
			getRestResponse( HttpMethod.PUT, MediaType.TEXT_HTML );

			try
				{
				URL	target	= new URL( this.url );
				PageInfo pi	= walkpage( target, webpage );
				this.pageInfo.put( target, pi );
				}
			catch ( MalformedURLException e )
				{
				// TODO Auto-generated catch block
				e.printStackTrace();
				}
			
			for ( String url : UrlXRef.getFromLinks( this.url ) )
				{
				this.getLog().info( url );
				}
			
			getLog().info( "*************** Links **************" );
			for ( URL url : this.links )
				{
				getLog().info( url.toString()
						+ ( this.visited.contains( urlToString( url ) )
							? " (visited)" : "" ) );
				}
			
			try {
			getLog().info( "*************** Info **************" );
			for ( URL url : this.pageInfo.keySet() )
				{
				if ( url == null )
					getLog().info( "null key in pageInfo keySet!" );
				getLog().info( "======================" );
				
				final PageInfo	pi	= this.pageInfo.get( url );
				getLog().info( url.toString()
					+ (String) ( ( pi != null )
							? ( " -- " + pi.toString() )
							: "n/a" ) );
				
				Set< String > links;
				
				links	= UrlXRef.getFromLinks( urlToString( url ) );
				if ( links != null )
					for ( String from : links )
						{
						getLog().info( "points to:  " + from );
						}
				links	= UrlXRef.getToLinks( urlToString( url ) ); 
				if ( links != null )
					for ( String from : links )
						{
						getLog().info( "pointed to by:  " + from );
						}
				}
			} catch ( Throwable x ) {
				getLog().info( x.getClass().getName() + " -- " + x.getMessage() );
			}
			}
		}
	
	protected String urlToString( final URL url )
		{
		StringBuffer	buf	= new StringBuffer();
		
		buf.append( url.getProtocol() );
		if ( url.getProtocol().equals( "http" )
			|| url.getProtocol().equals( "https" ) )
			buf.append( "://" );
		buf.append( url.getHost() );
		
		String 		path	= url.getPath();
		if ( path.endsWith( "/" ) )
			buf.append( path.substring( 0, path.length() - 1 ) );
		else
			buf.append( path );
		
		return( buf.toString() );
		}
	
	protected PageInfo walkpage( final URL url, final SimpleUrlFlinger< String > page )
		{
		getLog().info( "\nAnalyzing page " + url.toString() );
		getLog().info( "response code:  " + page.getResponseCode() );
		
		PageInfo	rc	= new PageInfo( System.currentTimeMillis() );
		rc.addRestMethodInfo( page );

		if ( page.getResponseCode() != HttpStatus.OK )
			{
			getLog().info( url + " returned " + page.getResponseCode() );
			return( rc );
			}
		
		final Document	doc	= Jsoup.parse( page.getResponse().toString() );
		
		// find all anchors with an href
		walkAnchorElements( url, page, doc );
		
		walkLinkElements( url, page, doc );
		
		walkImageElements( url, page, doc );
		
		walkScriptElements( url, page, doc );
		
		return( rc );
		}
	
	protected void walkAnchorElements( final URL url, final SimpleUrlFlinger< String > page, final Document doc )
		{
		final Elements	links	= doc.select( "a[href]" );
		for ( Element e : links )
			{
			final String	href	= e.attr( "href" );
			
			getLog().info( e.outerHtml() );
			getLog().info( href );
			
			try
				{
				URL parent	= url;
				URL target	= new URL( parent, href );

				this.links.add( target );
				
				getLog().info( target.toString() + "/" + urlToString( target ) );
				UrlXRef.addXRef( urlToString( parent ), urlToString( target ) );
				this.visited.add( urlToString( parent ) );
				if ( parent.getHost().equalsIgnoreCase( target.getHost() )
					&& parent.getProtocol().equals( target.getProtocol() )
					&& ! this.visited.contains( urlToString( target ) )
					&& ! urlToString( target ).toLowerCase().endsWith( ".pdf" )
					&& ! urlToString( target ).toLowerCase().endsWith( ".xml" )
					&& ! urlToString( target ).toLowerCase().endsWith( ".xsd" )
					&& ! urlToString( target ).toLowerCase().endsWith( ".mp3" ) )
					{
					getLog().info( "same host" );
					SimpleUrlFlinger< String > newPage = new SimpleUrlFlinger< String >( target.toString(),
							HttpMethod.GET, MediaType.TEXT_HTML, String.class );

					PageInfo pi	= walkpage( target, newPage );
					this.pageInfo.put( target, pi );
					getLog().info( "putting " + target );
					this.visited.add( urlToString( target ) );
					}
				}
			catch ( MalformedURLException x )
				{
				// TODO Auto-generated catch block
				x.printStackTrace();
				}
			catch ( Throwable x )
				{
				getLog().warn( x.getClass().getName() + ":" );
				getLog().warn( x.getMessage() );
				}
			}
		
		}
	
	protected void walkLinkElements( final URL url, final SimpleUrlFlinger< String > page, final Document doc )
		{
		final Elements links	= doc.select( "link[href]" );
		for ( Element e : links )
			{
			final String	href	= e.attr( "href" );
			if ( href == null )
				continue;
			
			final String	type	= e.attr( "type" );
			
			getLog().info( e.outerHtml() );
			getLog().info( href );
			getLog().info( type );
			
			try
				{
				URL parent	= url;
				URL target	= new URL( parent, href );

				this.links.add( target );
				
				getLog().info( target.toString() + "/" + urlToString( target ) );
				UrlXRef.addXRef( urlToString( parent ), urlToString( target ) );
				this.visited.add( urlToString( parent ) );
				if ( parent.getHost().equalsIgnoreCase( target.getHost() )
					&& parent.getProtocol().equals( target.getProtocol() )
					&& ! this.visited.contains( urlToString( target ) )
					&& ! urlToString( target ).toLowerCase().endsWith( ".pdf" )
					&& ! urlToString( target ).toLowerCase().endsWith( ".xml" )
					&& ! urlToString( target ).toLowerCase().endsWith( ".xsd" )
					&& ! urlToString( target ).toLowerCase().endsWith( ".mp3" ) )
					{
					getLog().info( "same host" );
					SimpleUrlFlinger< String > newPage = new SimpleUrlFlinger< String >( target.toString(),
							HttpMethod.GET,
							MediaType.parseMediaType( type ),
							String.class );

					PageInfo	rc	= new PageInfo( System.currentTimeMillis() );
					rc.addRestMethodInfo( newPage );
//					PageInfo pi	= walkpage( target, newPage );
					
					this.pageInfo.put( target, rc );
					getLog().info( "putting " + target );
					this.visited.add( urlToString( target ) );
					}
				}
			catch ( MalformedURLException x )
				{
				// TODO Auto-generated catch block
				x.printStackTrace();
				}
			catch ( Throwable x )
				{
				getLog().warn( x.getClass().getName() + ":" );
				getLog().warn( x.getMessage() );
				}
			}
		
		}
	
	protected void walkImageElements( final URL url, final SimpleUrlFlinger< String > page, final Document doc )
		{
		final Elements links	= doc.select( "img[src]" );
		for ( Element e : links )
			{
			final String	href	= e.attr( "src" );
			if ( href == null )
				continue;
			
			getLog().info( e.outerHtml() );
			getLog().info( href );
			
			try
				{
				URL parent	= url;
				URL target	= new URL( parent, href );

				this.links.add( target );
				
				getLog().info( target.toString() + "/" + urlToString( target ) );
				UrlXRef.addXRef( urlToString( parent ), urlToString( target ) );
				this.visited.add( urlToString( parent ) );
				if ( parent.getHost().equalsIgnoreCase( target.getHost() )
					&& parent.getProtocol().equals( target.getProtocol() )
					&& ! this.visited.contains( urlToString( target ) )
					&& ! urlToString( target ).toLowerCase().endsWith( ".pdf" )
					&& ! urlToString( target ).toLowerCase().endsWith( ".xml" )
					&& ! urlToString( target ).toLowerCase().endsWith( ".xsd" )
					&& ! urlToString( target ).toLowerCase().endsWith( ".mp3" ) )
					{
					getLog().info( "same host" );
					SimpleUrlFlinger< String > newPage = new SimpleUrlFlinger< String >( target.toString(),
							HttpMethod.GET,
							MediaType.IMAGE_JPEG,
							String.class );

					PageInfo	rc	= new PageInfo( System.currentTimeMillis() );
					rc.addRestMethodInfo( newPage );
//					PageInfo pi	= walkpage( target, newPage );
					
					this.pageInfo.put( target, rc );
					getLog().info( "putting " + target );
					this.visited.add( urlToString( target ) );
					}
				}
			catch ( MalformedURLException x )
				{
				// TODO Auto-generated catch block
				x.printStackTrace();
				}
			catch ( Throwable x )
				{
				getLog().warn( x.getClass().getName() + ":" );
				getLog().warn( x.getMessage() );
				}
			}
		
		}
	
	protected void walkScriptElements( final URL url, final SimpleUrlFlinger< String > page, final Document doc )
		{
		final Elements links	= doc.select( "script[src]" );
		for ( Element e : links )
			{
			final String	href	= e.attr( "src" );
			if ( href == null )
				continue;
			
			getLog().info( e.outerHtml() );
			getLog().info( href );
			
			try
				{
				URL parent	= url;
				URL target	= new URL( parent, href );

				this.links.add( target );
				
				getLog().info( target.toString() + "/" + urlToString( target ) );
				UrlXRef.addXRef( urlToString( parent ), urlToString( target ) );
				this.visited.add( urlToString( parent ) );
				if ( parent.getHost().equalsIgnoreCase( target.getHost() )
					&& parent.getProtocol().equals( target.getProtocol() )
					&& ! this.visited.contains( urlToString( target ) )
					&& ! urlToString( target ).toLowerCase().endsWith( ".pdf" )
					&& ! urlToString( target ).toLowerCase().endsWith( ".xml" )
					&& ! urlToString( target ).toLowerCase().endsWith( ".xsd" )
					&& ! urlToString( target ).toLowerCase().endsWith( ".mp3" ) )
					{
					getLog().info( "same host" );
					SimpleUrlFlinger< String > newPage = new SimpleUrlFlinger< String >( target.toString(),
							HttpMethod.GET,
							MediaType.ALL,
							String.class );

					PageInfo	rc	= new PageInfo( System.currentTimeMillis() );
					rc.addRestMethodInfo( newPage );
//					PageInfo pi	= walkpage( target, newPage );
					
					this.pageInfo.put( target, rc );
					getLog().info( "putting " + target );
					this.visited.add( urlToString( target ) );
					}
				}
			catch ( MalformedURLException x )
				{
				// TODO Auto-generated catch block
				x.printStackTrace();
				}
			catch ( Throwable x )
				{
				getLog().warn( x.getClass().getName() + ":" );
				getLog().warn( x.getMessage() );
				}
			}
		
		}
	
	protected void getMediaTypeResponse( final MediaType[] mtArray, String originalResponse )
		{
		for ( MediaType mt : mtArray )
			{
			SimpleUrlFlinger< String > req = new SimpleUrlFlinger< String >( this.url,
					HttpMethod.GET, mt, String.class );
			this.getLog().info( mt + ":  " + req.getResponseCode().toString() );
			this.getLog().info( mt + ":  " + Long.toString( req.getResponseHeaders().getContentLength() ) );
			if ( ! originalResponse.equalsIgnoreCase( req.getResponse() ) )
				this.getLog().info( req.getResponse() );
			}
		}
	
	protected void getRestResponse( final HttpMethod m, MediaType mt )
		{
		final SimpleUrlFlinger< String > req = new SimpleUrlFlinger< String >( this.url,
				m, mt, String.class );
		this.getLog().info( m + ":  " + req.getResponseCode().toString() );
		this.getLog().info( m + ":  " + Long.toString( req.getResponseHeaders().getContentLength() ) );
		}

	}
