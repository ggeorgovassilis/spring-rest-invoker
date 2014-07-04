package com.github.ggeorgovassilis.springjsonmapper;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import com.github.ggeorgovassilis.springjsonmapper.model.MethodParameterDescriptor;
import com.github.ggeorgovassilis.springjsonmapper.model.UrlMapping;
import com.github.ggeorgovassilis.springjsonmapper.model.MethodParameterDescriptor.Type;

/**
 * Base component for proxy factories that bind java interfaces to a remote REST
 * service. For more information look up {@link JaxRsAnnotationsHttpJsonInvokerFactoryProxyBean}
 * 
 * @see JaxRsAnnotationsHttpJsonInvokerFactoryProxyBean
 * @author george georgovassilis
 * @author Maxime Guennec
 * 
 */
public abstract class BaseHttpJsonInvokerFactoryProxyBean implements
	FactoryBean<Object>, InvocationHandler {

    protected Class<?> remoteServiceInterfaceClass;
    protected String remoteServiceInterfaceClassName;
    protected Object remoteProxy;
    protected String baseUrl;
    protected RestOperations restTemplate;
    protected MethodInspector methodInspector;

    /**
     * Return an implementation of a {@link MethodInspector} which can look at methods and return a {@link RequestMapping} describing
     * how that method is to be mapped to a URL of a remote REST service.
     * @return
     */
    protected abstract MethodInspector constructDefaultMethodInspector();

    /**
     * Implementations inspect a method and return the corresponding
     * {@link RequestMapping}.
     * 
     * @param method
     * @return Must always return a {@link RequestMapping}. If there's a problem
     *         then implementations must throw an exception instead of returning
     *         null.
     */
    protected abstract UrlMapping getRequestMapping(Method method);

    /**
     * Return the RequestBody annotation name for error messages.
     * 
     * @return
     */
    protected abstract String getRequestBodyAnnotationNameDisplayText();

    /**
     * Return the RequestParam annotation name for error messages.
     * 
     * @return
     */
    protected abstract String getRequestParamAnnotationNameDisplayText();

    public MethodInspector getMethodInspector() {
	return methodInspector;
    }

    /**
     * Override the default method inspector provided by the extending implementation.
     * @param methodInspector
     */
    public void setMethodInspector(MethodInspector methodInspector) {
	this.methodInspector = methodInspector;
    }

    protected RestOperations getRestTemplate() {
	return restTemplate;
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

    /**
     * Set the base URL of the remote REST service for HTTP requests. Further mappings specified on service interfaces are resolved
     * relatively to this URL.
     * @param baseUrl
     */
    public void setBaseUrl(String baseUrl) {
	this.baseUrl = baseUrl;
    }

    public Class<?> getRemoteServiceInterfaceClass() {
	return remoteServiceInterfaceClass;
    }

    /**
     * Set the class of the remote service interface. Use either this setter or {@link #setRemoteServiceInterfaceClassName(String)}
     * @param c
     */
    public void setRemoteServiceInterfaceClass(Class<?> c) {
	remoteServiceInterfaceClass = c;
    }

    /**
     * Set the absolute class name of the remote service interface to map to the remote REST service. Use either this setter or {@link #setRemoteServiceInterfaceClass(Class)}
     * @param className
     */
    public void setRemoteServiceInterfaceClassName(String className) {
	this.remoteServiceInterfaceClassName = className;
    }

    protected RestTemplate constructDefaultRestTemplate() {
	RestTemplate restTemplate = new RestTemplate();
	List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();
	MappingJacksonHttpMessageConverter jsonConverter = new MappingJacksonHttpMessageConverter();
	messageConverters.add(jsonConverter);
	restTemplate.setMessageConverters(messageConverters);
	return restTemplate;
    }

    /**
     * If instantiating this object programmatically then, after setting any
     * dependencies, call this method to finish object initialization. Spring
     * will normally do that in an application context.
     */
    @PostConstruct
    public void initialize() {
	if (remoteServiceInterfaceClass == null) {
	    if (remoteServiceInterfaceClassName == null)
		throw new IllegalArgumentException("Must provide the remote service interface class or classname.");
	    try {
		remoteServiceInterfaceClass = getClass().getClassLoader()
			.loadClass(remoteServiceInterfaceClassName);
	    } catch (ClassNotFoundException e) {
		throw new RuntimeException(e);
	    }
	}
	if (methodInspector == null)
	    methodInspector = constructDefaultMethodInspector();
	if (restTemplate == null)
	    restTemplate = constructDefaultRestTemplate();
    }

    @Override
    public synchronized Object getObject() throws Exception {
	if (remoteProxy == null)
	    remoteProxy = Proxy.newProxyInstance(getClass().getClassLoader(),
		    new Class[] { getRemoteServiceInterfaceClass() }, this);
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

    protected HttpMethod mapStringToHttpMethod(String method) {
	return HttpMethod.valueOf(method);
    }

    protected Object handleRemoteInvocation(Object proxy, Method method,
	    Object[] args, UrlMapping requestMapping) {
	Object result = null;
	Map<String, Object> parameters = new HashMap<>();
	Map<String, Object> dataObjects = new HashMap<>();
	MultiValueMap<String, Object> formObjects = new LinkedMultiValueMap<>();
	RestOperations rest = getRestTemplate();
	UrlMapping urlMapping = null;
	String httpMethod = null;
	Class<?> returnType = method.getReturnType();
	String url = baseUrl;
	url += requestMapping.getUrl();

	httpMethod = requestMapping.getHttpMethod();
	urlMapping = methodInspector.inspect(method, args);

	for (MethodParameterDescriptor descriptor : urlMapping.getParameters()) {
	    if (descriptor.getType().equals(Type.httpParameter)
		    && !urlMapping.hasRequestBody(descriptor.getName())) {
		parameters.put(descriptor.getName(), descriptor.getValue());
		url = appendDescriptorNameParameterToUrl(url, descriptor);
	    } else if (descriptor.getType().equals(Type.pathVariable)) {
		url = replacePathVariableDescriptorInUrl(url, descriptor);
	    } else if (descriptor.getType().equals(Type.requestBody)) {
		if (dataObjects.containsKey(descriptor.getName()))
		    throw new IllegalArgumentException(String.format("Duplicate requestBody with name '%s' on method %s", descriptor.getName(),method));
		dataObjects.put(descriptor.getName(), descriptor.getValue());
	    } else if (descriptor.getType().equals(Type.requestPart)) {
		formObjects.add(descriptor.getName(), descriptor.getValue());
	    }
	}

	if (HttpMethod.GET.equals(httpMethod)) {
	    result = rest.getForObject(url, returnType, parameters);
	} else {
	    result = doPost(dataObjects, method, requestMapping, rest, url,
		    httpMethod, returnType, parameters, formObjects);
	}
	return result;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args)
	    throws Throwable {
	// no request mapping on method -> call method directly on object
	UrlMapping requestMapping = getRequestMapping(method);
	Object result = null;
	if (requestMapping == null) {
	    result = handleSelfMethodInvocation(proxy, method, args);
	} else {
	    result = handleRemoteInvocation(proxy, method, args, requestMapping);
	}
	return result;
    }

    protected Object doPost(Map<String, Object> dataObjects, Method method,
	    UrlMapping requestMapping, RestOperations rest, String url,
	    String httpMethod, Class<?> returnType,
	    Map<String, Object> parameters,
	    MultiValueMap<String, Object> formObjects) {
	Object dataObject = dataObjects.get("");
	if (dataObjects.size() > 1 && dataObject != null)
	    throw new IllegalArgumentException(
		    String.format(
			    "Found both named and anonymous %s arguments on method. Use either a single, anonymous, method parameter or annotate every %s parameter together with %s on %s",
			    getRequestBodyAnnotationNameDisplayText(),
			    getRequestBodyAnnotationNameDisplayText(),
			    getRequestParamAnnotationNameDisplayText(),
			    method.toGenericString()));
	if (dataObject == null)
	    dataObject = formObjects.isEmpty() ? dataObjects : formObjects;
	MultiValueMap<String, String> headers = getHeaders(requestMapping);
	HttpEntity<?> requestEntity = new HttpEntity<Object>(dataObject,
		headers);
	ResponseEntity<?> responseEntity = rest.exchange(url,
		mapStringToHttpMethod(httpMethod), requestEntity, returnType,
		parameters);
	Object result = responseEntity.getBody();
	return result;
    }

    protected String appendDescriptorNameParameterToUrl(String url,
	    MethodParameterDescriptor descriptor) {
	if (url.contains("?"))
	    url += "&";
	else
	    url += "?";
	url += descriptor.getName() + "={" + descriptor.getName() + "}";
	return url;
    }

    protected String replacePathVariableDescriptorInUrl(String url,
	    MethodParameterDescriptor descriptor) {
	url = url.replaceAll("\\{" + descriptor.getName() + "\\}", ""
		+ descriptor.getValue());
	return url;
    }

    private MultiValueMap<String, String> getHeaders(UrlMapping requestMapping) {
	MultiValueMap<String, String> result = new LinkedMultiValueMap<String, String>();
	if (requestMapping.getHeaders()!=null)
	for (String header : requestMapping.getHeaders()) {
	    String[] split = header.split("=");
	    if (split.length > 1) {
		result.add(split[0], split[1]);
	    }
	}
	
	if (requestMapping.getConsumes()!=null)
	for (String consume : requestMapping.getConsumes()) {
	    result.add("Content-Type", consume);
	}
	if (requestMapping.getProduces()!=null)
	for (String produce : requestMapping.getProduces()) {
	    result.add("Accept", produce);
	}
	return result;
    }

    /**
     * Handles reflective method invocation, either invoking a method on the
     * proxy (equals or hashcode) or directly on the target. Implementation
     * copied from spring framework ServiceLocationInvocationHandler
     * 
     * @param proxy
     * @param method
     * @param args
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    protected Object handleSelfMethodInvocation(Object proxy, Method method,
	    Object[] args) throws InvocationTargetException,
	    IllegalAccessException {
	if (ReflectionUtils.isEqualsMethod(method)) {
	    // Only consider equal when proxies are identical.
	    return proxy == args[0];
	} else if (ReflectionUtils.isHashCodeMethod(method)) {
	    // Use hashCode of service locator proxy.
	    return System.identityHashCode(proxy);
	} else if (ReflectionUtils.isToStringMethod(method)) {
	    return remoteServiceInterfaceClass.getName() + "@"
		    + System.identityHashCode(proxy);
	} else {
	    return method.invoke(this, args);
	}
    }
}
