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
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

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
 * 
 */
public class HttpJsonInvokerFactoryProxyBean implements FactoryBean<Object>,
	InvocationHandler {

    private Class<?> remoteServiceInterfaceClass;
    private String remoteServiceInterfaceClassName;
    private Object remoteProxy;
    private String baseUrl;
    private RestTemplate restTemplate;
    private RequestParamAnnotationParameterNameDiscoverer parameterNameDiscoverer;
    private PathVariableAnnotationParameterNameDiscoverer pathVariableNameDiscoverer;

    protected RestTemplate getRestTemplate() {
	return restTemplate;
    }

    @PostConstruct
    public void initialize() {
	parameterNameDiscoverer = new RequestParamAnnotationParameterNameDiscoverer();
	pathVariableNameDiscoverer = new PathVariableAnnotationParameterNameDiscoverer();
	if (remoteServiceInterfaceClass == null) {
	    try {
		remoteServiceInterfaceClass = getClass().getClassLoader()
			.loadClass(remoteServiceInterfaceClassName);
	    } catch (ClassNotFoundException e) {
		throw new RuntimeException(e);
	    }
	}
	if (restTemplate == null) {
	    restTemplate = new RestTemplate();
	    List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();
	    MappingJacksonHttpMessageConverter jsonConverter = new MappingJacksonHttpMessageConverter();
	    messageConverters.add(jsonConverter);
	    restTemplate.setMessageConverters(messageConverters);
	}
    }

    /**
     * Optionally provide a {@link RestTemplate} if you need to handle http
     * yourself (proxies? BASIC auth?)
     * 
     * @param restTemplate
     */
    public void setRestTemplate(RestTemplate restTemplate) {
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

    public synchronized Object getObject() throws Exception {
	if (remoteProxy == null) {
	    remoteProxy = Proxy.newProxyInstance(getClass().getClassLoader(),
		    new Class[] { getRemoteServiceInterfaceClass() }, this);
	}
	return remoteProxy;
    }

    public Class<?> getObjectType() {
	return getRemoteServiceInterfaceClass();
    }

    public boolean isSingleton() {
	return true;
    }

    RequestMapping getRequestMapping(Method method) {
	return AnnotationUtils.findAnnotation(method, RequestMapping.class);
    }

    RequestMethod getMethod(RequestMapping rm) {
	if (rm.method() == null || rm.method().length == 0)
	    return RequestMethod.GET;
	if (rm.method().length != 1)
	    throw new IllegalArgumentException(
		    "Request mapping should not specify more than one methods");
	return rm.method()[0];
    }

    public Object invoke(Object proxy, Method method, Object[] args)
	    throws Throwable {

	String url = baseUrl;
	Object result = null;
	Map<String, Object> parameters = new HashMap<String, Object>();
	RestTemplate rest = getRestTemplate();
	String[] allVariables = null;

	RequestMapping requestMapping = getRequestMapping(method);
	if (requestMapping == null) {
	    return method.invoke(this, args);
	}
	allVariables = pathVariableNameDiscoverer.getParameterNames(method);
	if (allVariables != null) {
	    String requestMappingFragment = requestMapping.value()[0];
	    for (int i = 0; i < allVariables.length; i++) {
		String variableName = allVariables[i];
		Object value = args[i];
		String stringValue = (value == null) ? "" : value.toString();
		requestMappingFragment = requestMappingFragment.replaceAll(
			"\\{" + variableName + "\\}", stringValue);
	    }
	    url += requestMappingFragment;
	}
	String[] argNames = parameterNameDiscoverer.getParameterNames(method);
	if (argNames != null)
	    for (int i = 0; i < argNames.length; i++) {
		parameters.put(argNames[i], args[i]);
	    }
	String prefix = url.contains("?") ? "&" : "?";
	for (String parameter : parameters.keySet()) {
	    url = url + prefix + parameter + "={" + parameter + "}";
	    prefix = "&";
	}

	RequestMethod httpMethod = getMethod(requestMapping);
	if (RequestMethod.GET.equals(httpMethod)) {
	    result = rest.getForObject(url, method.getReturnType(), parameters);
	} else if (RequestMethod.POST.equals(httpMethod)) {
	    if (args.length > 1)
		throw new IllegalArgumentException(
			"Can't currently post more than a single parameter");
	    result = rest.postForObject(url, args[0], method.getReturnType(),
		    parameters);
	} else
	    throw new IllegalArgumentException("Method "
		    + requestMapping.method() + " not implemented");
	return result;
    }
}
