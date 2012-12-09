/**
 * $Id$
 *
 * 
 */
package org.yuksnort.mojo;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;

/**
 * @author Mike Hudgins <mchudgins@dstsystems.com>
 *
 */
public class UrlXRef
	{
	@SuppressWarnings( "unused" )
	private static final Logger	log	= Logger.getLogger( UrlXRef.class );
	private static Map< String, Set< String > > toLinks // set of pages pointing to a specific URL
		= new HashMap< String, Set< String > >();
	private static Map< String, Set< String > > fromLinks
		= new HashMap< String, Set< String > >();
	
	private UrlXRef()
		{
		}
	
	public static synchronized void addXRef( String fromURL, String toURL )
		{
		Set< String >	to;
		Set< String >	from;
		
		if ( ! toLinks.containsKey( toURL ) )
			to	= new HashSet< String >();
		else
			to	= toLinks.get( toURL );
		
		if ( ! fromLinks.containsKey( fromURL ) )
			from	= new HashSet< String >();
		else
			from	= fromLinks.get( fromURL );
		
		to.add( fromURL );
		from.add( toURL );
		
		toLinks.put( toURL, to );
		fromLinks.put( fromURL, from );
		}
	
	public static Set< String > getFromLinks( String sourcePage )
		{
		if ( fromLinks.containsKey( sourcePage ) )
			return( new HashSet< String >( fromLinks.get( sourcePage ) ) );
		else
			return( new HashSet< String >() );
		}
	
	public static Set< String > getToLinks( String page )
		{
		if ( toLinks.containsKey( page ) )
			return( new HashSet< String >( toLinks.get( page ) ) );
		else
			return( new HashSet< String >() );
		}

	}
