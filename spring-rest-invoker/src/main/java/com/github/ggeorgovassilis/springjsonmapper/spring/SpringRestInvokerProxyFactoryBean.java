package com.github.ggeorgovassilis.springjsonmapper.spring;

import com.github.ggeorgovassilis.springjsonmapper.model.MappingDeclarationException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;

import com.github.ggeorgovassilis.springjsonmapper.BaseRestInvokerProxyFactoryBean;
import com.github.ggeorgovassilis.springjsonmapper.MethodInspector;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.Exchanger;

/**
 * Binds a java interface to a remote REST service. Provide the interface class
 * ({@link #setRemoteServiceInterfaceClass(Class)} or
 * {@link #setRemoteServiceInterfaceClassName(String)}) and the base url of the
 * REST service ({@link #setBaseUrl(String)}). If instantiating this class
 * programmatically (i.e. not in a spring application context) then make sure to
 * invoke the {@link #initialize()} method before using this class.
 * 
 * Any service methods are accessed under the base URL. Take for example the
 * google book api which can be bound to the interface "BookServiceSpring".
 * <p>
 * <code>
 * 	&nbsp;&lt;bean id="RemoteBookService" class="com.github.ggeorgovassilis.springjsonmapper.spring.SpringAnnoationsHttpJsonInvokerFactoryProxyBean"&gt;<br>
 * 		&nbsp;&nbsp;&lt;property name="baseUrl" value="https://www.googleapis.com/books/v1" /&gt;<br>
 * 		&nbsp;&nbsp;&lt;property name="remoteServiceInterfaceClass" value="com.github.ggeorgovassilis.springjsonmapper.suppert.BookServiceSpring"/&gt;<br>
 * 	&nbsp;&lt;/bean&gt;<br>
 * </code>
 * </p>
 * Every method on the BookServiceSpring interface is mapped to some URL below
 * https://www.googleapis.com/books/v1. The exact mapping is defined in the
 * BookServiceSpring interface via {@link RequestMapping}, {@link RequestParam}
 * and {@link PathVariable} annotations. Please note that only a subset of
 * Spring's URL mapping annotations are implemented here.<br>
 * <br>
 * <code> 
 * public interface BookServiceSpring {<br>
 * <br>
 * 
 * &#064;RequestMapping("/volumes")<br> QueryResult
 *                                 findBooksByTitle(&#064;RequestParam("q") String
 *                                 q);<br> <br>
 * &#064;RequestMapping("/volumes/{id ")<br> Item findBookById(&#064;PathVariable("id")
 *                               String id);<br> }</code> <br>
 * <br>
 * Annotations understood are:
 * 
 * <ul>
 * <li>{@link RequestMapping} which specifies the relative URL to contact, the
 * HTTP method to use and content types
 * <li>{@link RequestParam} which specifies a name for the method argument. This
 * name can be used as a URL query parameter, a multipart form parameter or a
 * field name in a JSON document
 * <li>{@link RequestBody} which specifies that the parameter value is to be
 * encoded as JSON. If it's the only such annotated parameter, then the
 * generated JSON will be the entire body submitted during the request. If there
 * are multiple annotated parameters, then a {@link RequestParam} must be used
 * to distinguish them.
 * <li>{@link RequestPart} which specifies that the parameter is to be encoded
 * as part of a multipart form request
 * <li>{@link PathVariable} which specifies that the parameter value is to be
 * encoded into the URL as part of the path. Use a notation like
 * <code>/users/{name}/details</code> in the URL of {@link RequestMapping} to
 * escape the parameter name <code>&#064;PathVariable("name")</code>
 * </ul>
 * 
 * @author george georgovassilis
 * @author Maxime Guennec
 * 
 */
public class SpringRestInvokerProxyFactoryBean extends BaseRestInvokerProxyFactoryBean {

	private RestOperations restTemplate;

	@Override
	protected MethodInspector constructDefaultMethodInspector() {
		SpringAnnotationMethodInspector inspector = new SpringAnnotationMethodInspector();
		inspector.setEmbeddedValueResolver(expressionResolver);
		return inspector;
	}

	protected Object executeRequest(Map<String, Object> dataObjects, Method method, String url,
									HttpMethod httpMethod, ParameterizedTypeReference<?> returnType, Map<String, Object> parameters,
									MultiValueMap<String, Object> formObjects, Map<String, String> cookies,
									MultiValueMap<String, String> headers) {
		HttpEntity<?> requestEntity = null;
		Object dataObject = dataObjects.get("");
		if (dataObjects.size() > 1 && dataObject != null) {
			throw new MappingDeclarationException(
				String.format("Found both named and anonymous arguments on method %s, that's ambiguous.",
					method.toGenericString()),
				method, null, -1);
		}
		if (dataObject == null) {
			dataObject = formObjects.isEmpty() ? dataObjects : formObjects;
		}

		LinkedMultiValueMap<String, String> finalHeaders = new LinkedMultiValueMap<String, String>(headers);
		augmentHeadersWithCookies(finalHeaders, cookies);
		boolean hasBody = !HttpMethod.GET.equals(httpMethod);

		if (hasBody) {
			requestEntity = new HttpEntity<>(dataObject, finalHeaders);
		} else {
			requestEntity = new HttpEntity<>(headers);
		}
		ResponseEntity<?> responseEntity = getRestTemplate().exchange(url, httpMethod, requestEntity, returnType, parameters);
		return responseEntity.getBody();
	}

	@PostConstruct
	private void init() {
		if (restTemplate == null) {
			restTemplate = new RestTemplate();
		}
	}

	public void setRestTemplate(RestOperations restTemplate) {
		this.restTemplate = restTemplate;
	}

	private RestOperations getRestTemplate() {
		return restTemplate;
	}
}
