/*
 * $Id$
 */
package org.yuksnort.web.util.validator;

import static org.junit.Assert.assertTrue;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.yuksnort.web.util.validator.Validator;
import org.yuksnort.web.util.validator.W3CValidator;

/**
 * one line description.
 *
 * NB:  The jetty code is broke starting with version 8!
 * It is known to work with 7.0.2.v20100331
 *
 * @author	Mike Hudgins <mchudgins@dstsystems.com>
 * @version	Nov 14, 2012
 *
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration( locations = { "/testContext.xml" } )
public class W3CValidatorTest
	{
	private static final Logger	log		= Logger.getLogger( W3CValidatorTest.class );
	private	static final String	simplestHtml	= "<!doctype html><html><head><title></title></head><body></body></html>";
	private static final String	simpleBadHtml	= "<!fubar><html><body></body></html>";
	
	private Validator		validator			= new W3CValidator();
	

	@Test
	public void warning()
		{
		log.warn( "W3CValidatorTest is disabled!" );
		}

	@Test
	public void handlesBadJson()
		{
		this.validator.validate( "" );

		assertTrue( "unexpected json response not handled gracefully", this.validator.isValid() == false );
		log.info( validator.getHttpStatus() );
		}

	@Test
	public void	testSimplestHtml()
		{
		this.validator.validate( simplestHtml );

		assertTrue( "w3c says uh-oh!", this.validator.getHttpStatus() == HttpStatus.OK );
		assertTrue( "w3c says its bad html", this.validator.isValid() );
		}

	@Test
	public void	testSimpleBadHtml()
		{
		this.validator.validate( simpleBadHtml );

		assertTrue( "w3c says uh-oh!", this.validator.getHttpStatus() == HttpStatus.OK );
		assertTrue( "w3c says its not bad html!", this.validator.getErrorCount() != 0 );
		assertTrue( "w3c says its valid!", this.validator.isValid() == false );
//		log.trace( this.validator );
		}

	}
