package com.github.ggeorgovassilis.springjsonmapper.jaxrs;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpMethod;

import com.github.ggeorgovassilis.springjsonmapper.MethodInspector;
import com.github.ggeorgovassilis.springjsonmapper.model.MethodParameterDescriptor;
import com.github.ggeorgovassilis.springjsonmapper.model.UrlMapping;
import com.github.ggeorgovassilis.springjsonmapper.model.MethodParameterDescriptor.Type;

/**
 * Looks at methods and extracts {@link Path}, {@link GET}, {@link QueryParam} etc annotations
 * @author george georgovassilis
 *
 */
public class JaxRsAnnotationMethodInspector implements MethodInspector{

    @Override
    public UrlMapping inspect(Method method, Object[] args) {
	UrlMapping urlMapping = new UrlMapping();
	Path path = AnnotationUtils.findAnnotation(method,Path.class);
	urlMapping.setUrl(path.value());
	if (urlMapping.getUrl() == null)
	    throw new IllegalArgumentException(
		    "Missing @RequestMapping on method " + method);

	urlMapping.setHttpMethod(HttpMethod.GET.name());
	if (AnnotationUtils.findAnnotation(method, POST.class)!=null)
	    urlMapping.setHttpMethod(HttpMethod.POST.name());
	if (AnnotationUtils.findAnnotation(method, PUT.class)!=null)
	    urlMapping.setHttpMethod(HttpMethod.PUT.name());
	if (AnnotationUtils.findAnnotation(method, DELETE.class)!=null)
	    urlMapping.setHttpMethod(HttpMethod.DELETE.name());
	
	Produces produces = AnnotationUtils.findAnnotation(method, Produces.class);
	if (produces!=null)
	    urlMapping.setProduces(produces.value());
	
	Consumes consumes = AnnotationUtils.findAnnotation(method, Consumes.class);
	if (consumes!=null)
	    urlMapping.setConsumes(consumes.value());

	Annotation[][] parameterAnnotations = method.getParameterAnnotations();
	if (parameterAnnotations.length != method.getParameterTypes().length)
	    throw new IllegalArgumentException(
		    String.format(
			    "Annotation mismatch: method has %d parameters but %d have been annotated on %s",
			    parameterAnnotations.length,
			    method.getParameterTypes().length,
			    method.toString()));
	int i = 0;
	for (Annotation[] annotations : parameterAnnotations) {
	    Object value = args[i];
	    i++;
	    String parameterName = "";
	    Type parameterType = null;
	    boolean parameterFound = false;
	    for (Annotation annotation : annotations) {
		if (PathParam.class.isAssignableFrom(annotation
			.annotationType())) {
		    PathParam pv = (PathParam) annotation;
		    parameterName = pv.value();
		    urlMapping.addDescriptor(new MethodParameterDescriptor(
			    Type.pathVariable, parameterName, value));
		    parameterFound = true;
		}
		if (QueryParam.class.isAssignableFrom(annotation
			.annotationType())) {
		    QueryParam pv = (QueryParam) annotation;
		    parameterName = pv.value();
		    urlMapping.addDescriptor(new MethodParameterDescriptor(
			    Type.httpParameter, parameterName, value));
		    parameterFound = true;
		}
		if (FormParam.class.isAssignableFrom(annotation
			.annotationType())) {
		    FormParam pv = (FormParam)annotation;
		    parameterType = Type.requestBody;
		    parameterFound = true;
		    parameterName = pv.value();
		}
//TODO: how to model request parts?
//		if (RequestPart.class.isAssignableFrom(annotation
//			.annotationType())) {
//		    parameterType = Type.requestPart;
//		    parameterFound = true;
//		}
	    }
	    if (!parameterFound)
		throw new IllegalArgumentException(String.format("Couldn't find mapping annotation on parameter %d of method %s", i, method.toGenericString()));
	    if (parameterType!=null) {
		urlMapping.addDescriptor(new MethodParameterDescriptor(parameterType, parameterName, value));
	    }
	}
	return urlMapping;
    }
}
