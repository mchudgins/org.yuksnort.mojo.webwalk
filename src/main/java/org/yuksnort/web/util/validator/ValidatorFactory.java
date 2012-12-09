package org.yuksnort.web.util.validator;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.FactoryBean;

public class ValidatorFactory implements FactoryBean< Validator > {
	private static final Logger	log	= Logger.getLogger( W3CValidator.class );

	private	String	proxy	= null;
	
	@Override
	public Validator getObject() throws Exception {
		log.info( "getting bean" );
		if ( proxy == null )
			return( new W3CValidator() );
		else
			return( new NuHtmlValidator() );
	}

	@Override
	public Class<?> getObjectType() {
		log.info( "checking bean type" );
		return Validator.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}
	
	public void setProxy( final String proxy ) {
		log.info( "Proxy:  " + proxy );
		this.proxy	= proxy;
	}

	
}
