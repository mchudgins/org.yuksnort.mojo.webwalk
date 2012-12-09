/**
 * $Id$
 * 
 * 
 */

package org.yuksnort.it.webwalk;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * @author Mike Hudgins <mchudgins@dstsystems.com>
 * 
 */

@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration(locations = { "/testContext.xml" })
public class TestRestWalker
	{
	private static final Logger	log	= Logger.getLogger( TestRestWalker.class );
	
	@Test
	public void testAlarmHomePage()
			throws FailingHttpStatusCodeException,
				MalformedURLException, IOException
		{
		final WebClient webClient = new WebClient();
		final HtmlPage	page	= webClient.getPage( "http://www.dstresearch.com/" );
		
		log.info( page.getTitleText() );
		
		List< HtmlAnchor > anchors	= page.getAnchors();
		
		for ( HtmlAnchor a : anchors )
			{
			log.info( a.getHrefAttribute() );
			}
		}

	}
