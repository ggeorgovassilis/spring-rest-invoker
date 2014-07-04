package com.github.ggeorgovassilis.springjsonmapper.spring;

import java.lang.reflect.Method;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.github.ggeorgovassilis.springjsonmapper.BaseHttpJsonInvokerFactoryProxyBean;
import com.github.ggeorgovassilis.springjsonmapper.MethodInspector;
import com.github.ggeorgovassilis.springjsonmapper.model.UrlMapping;

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
 * BookServiceSpring interface via {@link RequestMapping}, {@link RequestParam} and
 * {@link PathVariable} annotations. Please note that only a subset of
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
 *                               String id);<br> }<br><br> </code>
 * @author george georgovassilis
 * @author Maxime Guennec
 * 
 */
public class SpringAnnotationsHttpJsonInvokerFactoryProxyBean extends
	BaseHttpJsonInvokerFactoryProxyBean {

    @Override
    protected UrlMapping getRequestMapping(Method method) {
	org.springframework.web.bind.annotation.RequestMapping annotation = AnnotationUtils
		.findAnnotation(
			method,
			org.springframework.web.bind.annotation.RequestMapping.class);
	if (annotation == null)
	    return null;
	UrlMapping mapping = new UrlMapping();
	mapping.setUrl(annotation.value()[0]);
	mapping.setConsumes(annotation.consumes());
	mapping.setHeaders(annotation.headers());
	mapping.setProduces(annotation.produces());
	if (annotation.method() == null || annotation.method().length == 0) {
	    mapping.setHttpMethod(HttpMethod.GET.name());
	} else if (annotation.method().length > 1) {
	    throw new IllegalArgumentException(
		    String.format("Can't handle more than one request methods on annotation %s on method %s",
			    annotation, method));
	} else {
	    mapping.setHttpMethod(annotation.method()[0].name());
	}
	return mapping;
    }

    @Override
    protected String getRequestBodyAnnotationNameDisplayText() {
	return "@RequestBody";
    }

    @Override
    protected String getRequestParamAnnotationNameDisplayText() {
	return "@RequestParam";
    }

    @Override
    protected MethodInspector constructDefaultMethodInspector() {
	return new SpringAnnotationMethodInspector();
    }

}
