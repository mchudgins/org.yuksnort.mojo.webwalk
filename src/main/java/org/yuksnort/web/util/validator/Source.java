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
public class Source
	{
	@SuppressWarnings("unused")
	private static final Logger	log	= Logger.getLogger( Source.class );

	private	String	encoding;
	private	String	type;
	private String	subType;

	public void setEncoding( final String enc )
		{
		this.encoding	= enc;
		}
	public	String getEncoding()
		{
		return( this.encoding );
		}

	public void setType( final String type )
		{
		this.type	= type;
		}
	public String getType()
		{
		return( this.type );
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
	}

