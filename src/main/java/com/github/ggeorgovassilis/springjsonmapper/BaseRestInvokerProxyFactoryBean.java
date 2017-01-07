package com.github.ggeorgovassilis.springjsonmapper;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringValueResolver;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import com.github.ggeorgovassilis.springjsonmapper.jaxrs.JaxRsInvokerProxyFactoryBean;
import com.github.ggeorgovassilis.springjsonmapper.model.MappingDeclarationException;
import com.github.ggeorgovassilis.springjsonmapper.model.MethodParameterDescriptor;
import com.github.ggeorgovassilis.springjsonmapper.model.MethodParameterDescriptor.Type;
import com.github.ggeorgovassilis.springjsonmapper.model.UrlMapping;
import com.github.ggeorgovassilis.springjsonmapper.spring.SpringRestInvokerProxyFactoryBean;
import com.github.ggeorgovassilis.springjsonmapper.utils.CglibProxyFactory;
import com.github.ggeorgovassilis.springjsonmapper.utils.DynamicJavaProxyFactory;
import com.github.ggeorgovassilis.springjsonmapper.utils.ProxyFactory;

/**
 * Base component for proxy factories that bind java interfaces to a remote REST
 * service. For more information look up
 * {@link SpringRestInvokerProxyFactoryBean} and {@link JaxRsInvokerProxyFactoryBean}
 * 
 * Will generate by default dynamic java proxies. Use {@link #setProxyTargetClass(ClassLoader, Class)} or {@link #setProxyTargetClass(Class)}
 * in order to generate proxies extending a concrete class.
 * @see JaxRsInvokerProxyFactoryBean
 * @see SpringRestInvokerProxyFactoryBean
 * @author george georgovassilis
 * @author Maxime Guennec
 * 
 */
public abstract class BaseRestInvokerProxyFactoryBean implements
	FactoryBean<Object>, InvocationHandler, EmbeddedValueResolverAware {

    protected Class<?> remoteServiceInterfaceClass;
    protected String remoteServiceInterfaceClassName;
    protected Object remoteProxy;
    protected String baseUrl;
    protected RestOperations restTemplate;
    protected MethodInspector methodInspector;
    protected StringValueResolver expressionResolver;
    protected ProxyFactory proxyFactory = new DynamicJavaProxyFactory();

    /**
     * Return an implementation of a {@link MethodInspector} which can look at
     * methods and return a {@link RequestMapping} describing how that method is
     * to be mapped to a URL of a remote REST service.
     * 
     * @return
     */
    protected abstract MethodInspector constructDefaultMethodInspector();

    /**
     * Implementations inspect a method and return the corresponding
     * {@link RequestMapping}.
     * 
     * @param method
     * @param args
     *            method arguments
     * @return Must always return a {@link RequestMapping}. If there's a problem
     *         then implementations must throw an exception instead of returning
     *         null.
     */
    protected UrlMapping getRequestMapping(Method method, Object[] args) {
	return methodInspector.inspect(method, args);
    }

    /**
     * Specify the class to extend
     * 
     * @param classLoader
     *            Classloader to use
     * @param c
     *            Proxies will extend this base class
     */

    public void setProxyTargetClass(ClassLoader classLoader, Class<?> c) {
	proxyFactory = new CglibProxyFactory(classLoader, c);
    }

    /**
     * Specify class to derive proxies from
     * 
     * @param c
     *            Base class. Will use this class' classloader
     */
    public void setProxyTargetClass(Class<?> c) {
	setProxyTargetClass(c.getClassLoader(), c);
    }

    @Override
    public void setEmbeddedValueResolver(StringValueResolver resolver) {
	this.expressionResolver = resolver;
    }

    public MethodInspector getMethodInspector() {
	return methodInspector;
    }

    /**
     * Override the default method inspector provided by the extending
     * implementation.
     * 
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
     * Set the base URL of the remote REST service for HTTP requests. Further
     * mappings specified on service interfaces are resolved relatively to this
     * URL.
     * 
     * @param baseUrl
     */
    public void setBaseUrl(String baseUrl) {
	this.baseUrl = baseUrl;
    }

    public Class<?> getRemoteServiceInterfaceClass() {
	return remoteServiceInterfaceClass;
    }

    /**
     * Set the class of the remote service interface. Use either this setter or
     * {@link #setRemoteServiceInterfaceClassName(String)}
     * 
     * @param c
     */
    public void setRemoteServiceInterfaceClass(Class<?> c) {
	remoteServiceInterfaceClass = c;
    }

    /**
     * Set the absolute class name of the remote service interface to map to the
     * remote REST service. Use either this setter or
     * {@link #setRemoteServiceInterfaceClass(Class)}
     * 
     * @param className
     */
    public void setRemoteServiceInterfaceClassName(String className) {
	this.remoteServiceInterfaceClassName = className;
    }

    protected RestTemplate constructDefaultRestTemplate() {
	RestTemplate restTemplate = new RestTemplate();
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
		throw new IllegalArgumentException(
			"Must provide the remote service interface class or classname.");
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
	if (remoteProxy == null) {
	    remoteProxy = proxyFactory.createProxy(getClass().getClassLoader(),
		    new Class[] { getRemoteServiceInterfaceClass() }, this);
	    proxyFactory = null;
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

    protected HttpMethod mapStringToHttpMethod(String method) {
	return HttpMethod.valueOf(method);
    }

    protected Object handleRemoteInvocation(Object proxy, Method method,
	    Object[] args, UrlMapping requestMapping) {
	Object result = null;
	Map<String, Object> parameters = new LinkedHashMap<>();
	Map<String, Object> dataObjects = new LinkedHashMap<>();
	MultiValueMap<String, String> headers = getHeaders(requestMapping);
	Map<String, String> cookies = new LinkedHashMap<>();
	MultiValueMap<String, Object> formObjects = new LinkedMultiValueMap<>();
	RestOperations rest = getRestTemplate();

	Class<?> returnType = method.getReturnType();
	String url = baseUrl;
	url += requestMapping.getUrl();

	HttpMethod httpMethod = requestMapping.getHttpMethod();
	UrlMapping urlMapping = methodInspector.inspect(method, args);

	for (MethodParameterDescriptor descriptor : urlMapping.getParameters()) {
	    if (descriptor.getType().equals(Type.httpParameter)
		    && !urlMapping.hasRequestBody(descriptor.getName())) {
		if (parameters.containsKey(descriptor.getName()))
		    throw new MappingDeclarationException(
			    "Duplicate parameter " + descriptor.getName()
				    + " on " + method, method, null, -1);
		parameters.put(descriptor.getName(), descriptor.getValue());
		url = appendDescriptorNameParameterToUrl(url, descriptor);
	    } else if (descriptor.getType().equals(Type.cookie)) {
		cookies.put(descriptor.getName(),
			(String) descriptor.getValue());
	    } else if (descriptor.getType().equals(Type.pathVariable)) {
		url = replacePathVariableDescriptorInUrl(url, descriptor);
	    } else if (descriptor.getType().equals(Type.requestBody)) {
		if (dataObjects.containsKey(descriptor.getName()))
		    throw new MappingDeclarationException(
			    String.format(
				    "Duplicate requestBody with name '%s' on method %s",
				    descriptor.getName(), method), method,
			    null, -1);
		dataObjects.put(descriptor.getName(), descriptor.getValue());
	    } else if (descriptor.getType().equals(Type.requestPart)) {
		formObjects.add(descriptor.getName(), descriptor.getValue());
	    }
	}

	result = executeRequest(dataObjects, method, rest, url, httpMethod,
		returnType, parameters, formObjects, cookies, headers);
	return result;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args)
	    throws Throwable {
	// no request mapping on method -> call method directly on object
	UrlMapping requestMapping = getRequestMapping(method, args);
	Object result = null;
	if (requestMapping == null) {
	    result = handleSelfMethodInvocation(proxy, method, args);
	} else {
	    result = handleRemoteInvocation(proxy, method, args, requestMapping);
	}
	return result;
    }

    protected Object executeRequest(Map<String, Object> dataObjects,
	    Method method, RestOperations rest, String url,
	    HttpMethod httpMethod, Class<?> returnType,
	    Map<String, Object> parameters,
	    MultiValueMap<String, Object> formObjects,
	    Map<String, String> cookies, MultiValueMap<String, String> headers) {
	HttpEntity<?> requestEntity = null;
	Object dataObject = dataObjects.get("");
	if (dataObjects.size() > 1 && dataObject != null)
	    throw new MappingDeclarationException(
		    String.format(
			    "Found both named and anonymous arguments on method %s, that's ambiguous.",
			    method.toGenericString()), method, null, -1);
	if (dataObject == null)
	    dataObject = formObjects.isEmpty() ? dataObjects : formObjects;

	LinkedMultiValueMap<String, String> finalHeaders = new LinkedMultiValueMap<String, String>(
		headers);
	augmentHeadersWithCookies(finalHeaders, cookies);
	boolean hasBody = !HttpMethod.GET.equals(httpMethod);
	
	if (hasBody)
	    requestEntity = new HttpEntity<Object>(dataObject, finalHeaders);
	else
	    requestEntity = new HttpEntity<Object>(headers);
	ResponseEntity<?> responseEntity = rest.exchange(url, httpMethod,
		requestEntity, returnType, parameters);
	Object result = responseEntity.getBody();
	return result;
    }

    protected void augmentHeadersWithCookies(
	    LinkedMultiValueMap<String, String> headers,
	    Map<String, String> cookies) {
	if (cookies.isEmpty())
	    return;
	String cookieHeader = "";
	String prefix = "";
	for (String cookieName : cookies.keySet()) {
	    cookieHeader = cookieHeader + prefix + cookieName + "="
		    + cookies.get(cookieName);
	    prefix = "&";
	}
	headers.add("Cookie", cookieHeader);

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
	if (requestMapping.getHeaders() != null)
	    for (String header : requestMapping.getHeaders()) {
		int index = header.indexOf("=");
		if (index == -1)
		    throw new MappingDeclarationException(
			    "Missing equals sign in header annotation "
				    + header + ": must be like KEY=VALUE",
			    null, null, -1);
		String key = header.substring(0, index);
		String value = header.substring(index + 1);
		result.add(key, value);
	    }

	if (requestMapping.getConsumes() != null)
	    for (String consume : requestMapping.getConsumes()) {
		result.add("Content-Type", consume);
	    }
	if (requestMapping.getProduces() != null)
	    for (String produce : requestMapping.getProduces()) {
		result.add("Accept", produce);
	    }
	for (MethodParameterDescriptor mpd : requestMapping.getParameters())
	    if (mpd.getType().equals(Type.httpHeader)) {
		Object value = mpd.getValue();
		String stringValue = value == null ? "" : value.toString();
		result.add(mpd.getName(), stringValue);
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
