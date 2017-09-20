package com.github.ggeorgovassilis.springjsonmapper.support;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;

/**
 * Factory for requests and responses that can be inspected and instrumented for
 * tests
 * 
 * @author George Georgovassilis
 *
 */
public class MockRequestFactory implements ClientHttpRequestFactory {

	public class MockResponse implements ClientHttpResponse {

		ByteArrayInputStream bais = new ByteArrayInputStream(new byte[0]);
		HttpHeaders headers = new HttpHeaders();
		HttpStatus status = HttpStatus.OK;

		{
			headers.add("Content-Type", "application/json");
		}

		public void setStatus(HttpStatus status) {
			this.status = status;
		}

		public void setBody(byte[] bytes) {
			bais = new ByteArrayInputStream(bytes);
		}

		@Override
		public InputStream getBody() throws IOException {
			return bais;
		}

		@Override
		public HttpHeaders getHeaders() {
			return headers;
		}

		@Override
		public HttpStatus getStatusCode() throws IOException {
			return status;
		}

		@Override
		public int getRawStatusCode() throws IOException {
			return status.value();
		}

		@Override
		public String getStatusText() throws IOException {
			return status.name();
		}

		@Override
		public void close() {
			try {
				bais.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

	}

	public class MockRequest implements ClientHttpRequest {

		private URI uri;
		private HttpMethod method;
		private HttpHeaders headers = new HttpHeaders();
		private ByteArrayOutputStream baos = new ByteArrayOutputStream();
		private MockResponse response;

		public MockRequest(URI uri, HttpMethod method) {
			this.uri = uri;
			this.method = method;
		}

		public void setRespose(MockResponse response) {
			this.response = response;
		}

		@Override
		public HttpMethod getMethod() {
			return method;
		}

		@Override
		public URI getURI() {
			return uri;
		}

		@Override
		public HttpHeaders getHeaders() {
			return headers;
		}

		@Override
		public OutputStream getBody() throws IOException {
			return baos;
		}

		@Override
		public ClientHttpResponse execute() throws IOException {
			return response;
		}

		public String serializeToString() {
			StringBuffer sb = new StringBuffer();
			sb.append(method.name() + " " + uri.toASCIIString() + "\n");
			List<String> headerNames = new ArrayList<String>(headers.keySet());
			Collections.sort(headerNames);
			for (String header : headerNames) {
				sb.append(header + "=");
				String prefix = "";
				for (String value : headers.get(header)) {
					sb.append(prefix + value);
					prefix = ",";
				}
				sb.append("\n");
			}
			sb.append("\n");
			try {
				sb.append(baos.toString("UTF-8"));
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException(e);
			}
			return sb.toString();
		}

	}

	private ThreadLocal<MockRequest> lastRequest = new ThreadLocal<MockRequestFactory.MockRequest>();
	private ThreadLocal<MockResponse> response = new ThreadLocal<MockRequestFactory.MockResponse>();

	@Override
	public ClientHttpRequest createRequest(URI uri, HttpMethod httpMethod) throws IOException {
		lastRequest.set(new MockRequest(uri, httpMethod));
		lastRequest.get().setRespose(response.get());
		return lastRequest.get();
	}

	public void setResponse(MockResponse response) {
		this.response.set(response);
	}

	public MockRequest getLastRequest() {
		return lastRequest.get();
	}

	public MockResponse createResponse() {
		response.set(new MockResponse());
		return response.get();
	}

}
