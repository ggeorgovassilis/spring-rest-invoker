package com.github.ggeorgovassilis.springjsonmapper.utils;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.FileCopyUtils;

/**
 * Utility for logging
 * 
 * @author george georgovassilis
 *
 */
public class LoggingRequestInterceptor implements ClientHttpRequestInterceptor {

	private static final Log requestLog = LogFactory.getLog("com.github.ggeorgovassilis.springjsonmapper.Request");

	private static final Log responseLog = LogFactory.getLog("com.github.ggeorgovassilis.springjsonmapper.Response");

	@Override
	public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
			throws IOException {

		ClientHttpResponse response = execution.execute(request, body);

		log(request, body, response);

		return response;
	}

	private void log(HttpRequest request, byte[] body, ClientHttpResponse response) throws IOException {
		if (requestLog.isDebugEnabled()) {
			String s = new String(body);
			requestLog.debug(request.getURI().toString() + " - " + s);
		}
		if (responseLog.isDebugEnabled()) {
			byte[] b = FileCopyUtils.copyToByteArray(response.getBody());
			String s = new String(b);
			responseLog.debug(request.getURI().toString() + " - " + s);
		}
	}
}