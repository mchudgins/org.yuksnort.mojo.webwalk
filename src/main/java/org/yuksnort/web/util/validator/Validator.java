package org.yuksnort.web.util.validator;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

public interface Validator {
	
	void		validate( String html );
	
	HttpStatus	getHttpStatus();
	
	Response 	getResponse();

	boolean		isValid();

	HttpHeaders getResponseHeaders();

	int			getErrorCount();

	int			getWarningCount();

}
