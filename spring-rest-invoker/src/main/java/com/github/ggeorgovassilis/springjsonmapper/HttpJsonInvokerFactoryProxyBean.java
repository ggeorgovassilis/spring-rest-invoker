package com.github.ggeorgovassilis.springjsonmapper;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import com.github.ggeorgovassilis.springjsonmapper.MethodParameterDescriptor.Type;

/**
 * Binds a java interface to a remote REST service. Provide the interface class
 * ({@link #setRemoteServiceInterfaceClass(Class)} or
 * {@link #setRemoteServiceInterfaceClassName(String)}) and the base url of the
 * REST service ({@link #setBaseUrl(String)}). If instantiating this class
 * programmatically (i.e. not in a spring application context) then make sure to
 * invoke the {@link #initialize()} method before using this class.
 * 
 * Any service methods are accessed under the base URL. Take for example the
 * google book api which can be bound to the interface "BookService".
 * <p>
 * <code>
 * 	&nbsp;&lt;bean id="RemoteBookService" class="com.github.ggeorgovassilis.springjsonmapper.HttpJsonInvokerFactoryProxyBean"&gt;<br>
 * 		&nbsp;&nbsp;&lt;property name="baseUrl" value="https://www.googleapis.com/books/v1" /&gt;<br>
 * 		&nbsp;&nbsp;&lt;property name="remoteServiceInterfaceClass" value="com.github.ggeorgovassilis.springjsonmapper.BookService"/&gt;<br>
 * 	&nbsp;&lt;/bean&gt;<br>
 * </code>
 * </p>
 * Every method on the BookService interface is mapped to some URL below
 * https://www.googleapis.com/books/v1. The exact mapping is defined in the
 * BookService interface via {@link RequestMapping}, {@link RequestParam} and
 * {@link PathVariable} annotations. Please note that only a small subset of
 * Spring's MVC capabilities are implemented here. <br>
 * <br>
 * <code> 
 * public interface BookService {<br>
 * <br>
 * 
 * @RequestMapping("/volumes")<br> QueryResult
 *                                 findBooksByTitle(@RequestParam("q") String
 *                                 q);<br> <br>
 * @RequestMapping("/volumes/{id ")<br> Item findBookById(@PathVariable("id")
 *                               String id);<br> }<br><br> </code>
 * @author george georgovassilis
 * @author Maxime Guennec
 * 
 */
public class HttpJsonInvokerFactoryProxyBean implements FactoryBean<Object>, InvocationHandler {

	protected Class<?> remoteServiceInterfaceClass;
	protected String remoteServiceInterfaceClassName;
	protected Object remoteProxy;
	protected String baseUrl;
	protected RestOperations restTemplate;
	protected MethodInspector methodInspector;

	protected RestOperations getRestTemplate() {
		return restTemplate;
	}

	protected RestTemplate constructDefaultRestTemplate() {
		RestTemplate restTemplate = new RestTemplate();
		List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();
		MappingJacksonHttpMessageConverter jsonConverter = new MappingJacksonHttpMessageConverter();
		messageConverters.add(jsonConverter);
		restTemplate.setMessageConverters(messageConverters);
		// List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
		// interceptors.add(new ClientHttpRequestInterceptor() {
		//
		// @Override
		// public ClientHttpResponse intercept(HttpRequest request, byte[] body,
		// ClientHttpRequestExecution execution)
		// throws IOException {
		// System.out.println("+++ request +++");
		// System.out.println(new String(body));
		// ClientHttpResponse response = execution.execute(request, body);
		// InputStream is = response.getBody();
		// System.out.println("+++ response +++");
		// for (int i = is.read(); i != -1; i = is.read())
		// System.out.print((char) i);
		// return response;
		// }
		// });
		// restTemplate.setInterceptors(interceptors);
		return restTemplate;
	}

	protected MethodInspector constructDefaultMethodInspector() {
		return new MethodInspector();
	}

	public MethodInspector getMethodInspector() {
		return methodInspector;
	}

	public void setMethodInspector(MethodInspector methodInspector) {
		this.methodInspector = methodInspector;
	}

	/**
	 * If instantiating this object programmatically then, after setting any dependencies, 
	 * call this method to finish object initialization. Spring will normally do that in an application context.
	 */
	@PostConstruct
	public void initialize() {
		if (remoteServiceInterfaceClass == null) {
			try {
				remoteServiceInterfaceClass = getClass().getClassLoader().loadClass(remoteServiceInterfaceClassName);
			} catch (ClassNotFoundException e) {
				throw new RuntimeException(e);
			}
		}
		if (methodInspector == null)
			methodInspector = constructDefaultMethodInspector();
		if (restTemplate == null) {
			restTemplate = constructDefaultRestTemplate();
		}
	}

	/**
	 * Optionally provide a {@link RestTemplate} if you need to handle http
	 * yourself (proxies? BASIC auth?)
	 * 
	 * @param restTemplate
	 */
	public void setRestTemplate(RestOperations restTemplate) {
		this.restTemplate = restTemplate;
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	protected Class<?> getRemoteServiceInterfaceClass() {
		return remoteServiceInterfaceClass;
	}

	public void setRemoteServiceInterfaceClass(Class<?> c) {
		remoteServiceInterfaceClass = c;
	}

	public void setRemoteServiceInterfaceClassName(String className) {
		this.remoteServiceInterfaceClassName = className;
	}

	@Override
	public synchronized Object getObject() throws Exception {
		if (remoteProxy == null) {
			remoteProxy = Proxy.newProxyInstance(getClass().getClassLoader(), new Class[] { getRemoteServiceInterfaceClass() }, this);
		}
		return remoteProxy;
	}

	@Override
	public Class<?> getObjectType() {
		return getRemoteServiceInterfaceClass();
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	protected RequestMapping getRequestMapping(Method method) {
		return AnnotationUtils.findAnnotation(method, RequestMapping.class);
	}

	protected RequestMethod getMethod(RequestMapping rm) {
		if (rm.method() == null || rm.method().length == 0)
			return RequestMethod.GET;
		if (rm.method().length != 1)
			throw new IllegalArgumentException("Request mapping should not specify more than one methods");
		return rm.method()[0];
	}

	protected HttpMethod map(RequestMethod method) {
		return HttpMethod.valueOf(method.name());
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

		String url = baseUrl;
		Object result = null;
		Map<String, Object> parameters = new HashMap<>();
		MultiValueMap<String, Object> formObjects = new LinkedMultiValueMap<>();
		Map<String, Object> dataObjects = new HashMap<>();
		RestOperations rest = getRestTemplate();
		UrlMapping urlMapping = null;
		RequestMapping requestMapping = getRequestMapping(method);
		RequestMethod httpMethod;
		Class<?> returnType;

		// no request mapping on method means the programmer either forgot it
		// (happens), or we're calling a method that's not meant to be exposed
		// (equals, hashcode etc)
		if (requestMapping == null) {
			return method.invoke(this, args);
		} else {
			httpMethod = getMethod(requestMapping);
			returnType = method.getReturnType();
			url += requestMapping.value()[0];
		}
		urlMapping = methodInspector.inspect(method, args);

		for (MethodParameterDescriptor descriptor : urlMapping.getParameters()) {
			if (descriptor.getType().equals(Type.httpParameter) && !urlMapping.hasRequestBody(descriptor.getName())) {
				parameters.put(descriptor.getName(), descriptor.getValue());
				if (url.contains("?"))
					url += "&";
				else
					url += "?";
				url += descriptor.getName() + "={" + descriptor.getName() + "}";
			} else if (descriptor.getType().equals(Type.pathVariable)) {
				url = url.replaceAll("\\{" + descriptor.getName() + "\\}", "" + descriptor.getValue());
			} else if (descriptor.getType().equals(Type.requestBody)) {
				dataObjects.put(descriptor.getName(), descriptor.getValue());
			} else if (descriptor.getType().equals(Type.requestPart)) {
				formObjects.add(descriptor.getName(), descriptor.getValue());
			}
		}

		if (RequestMethod.GET.equals(httpMethod)) {
			result = rest.getForObject(url, method.getReturnType(), parameters);
		} else {
			Object dataObject = dataObjects.get("");
			if (dataObjects.size() > 1 && dataObject != null)
				throw new IllegalArgumentException(
						"Found both named and anonymous @RequestBody arguments on method. Use either a single, anonymous, method parameter or annotate every @RequestBody parameter together with @RequestParam on "
								+ method);
			if (dataObject == null)
				dataObject = formObjects.isEmpty() ? dataObjects : formObjects;
			final MultiValueMap<String, String> headers = getHeaders(requestMapping);
			final HttpEntity<?> requestEntity = new HttpEntity<Object>(dataObject, headers);
			ResponseEntity<?> responseEntity = rest.exchange(url, map(httpMethod), requestEntity, returnType, parameters);
			result = responseEntity.getBody();
		}
		return result;
	}

	private MultiValueMap<String, String> getHeaders(final RequestMapping requestMapping) {
		MultiValueMap<String, String> result = new LinkedMultiValueMap<String, String>();
		for (String header : requestMapping.headers()) {
			final String[] split = header.split("=");
			if (split.length > 1) {
				result.add(split[0], split[1]);
			}
		}
		for (String consume : requestMapping.consumes()) {
			result.add("Content-Type", consume);
		}
		for (String produce : requestMapping.produces()) {
			result.add("Accept", produce);
		}
		return result;
	}
}