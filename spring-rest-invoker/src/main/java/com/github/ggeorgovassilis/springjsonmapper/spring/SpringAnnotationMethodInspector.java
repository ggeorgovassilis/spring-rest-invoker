package com.github.ggeorgovassilis.springjsonmapper.spring;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;

import com.github.ggeorgovassilis.springjsonmapper.MethodInspector;
import com.github.ggeorgovassilis.springjsonmapper.model.MethodParameterDescriptor;
import com.github.ggeorgovassilis.springjsonmapper.model.UrlMapping;
import com.github.ggeorgovassilis.springjsonmapper.model.MethodParameterDescriptor.Type;

/**
 * Looks at methods and extracts {@link RequestParam}, {@link PathVariable}, {@link RequestBody} and {@link RequestMapping} annotations
 * @author george georgovassilis
 *
 */
public class SpringAnnotationMethodInspector implements MethodInspector{

    @Override
    public UrlMapping inspect(Method method, Object[] args) {
	UrlMapping urlMapping = new UrlMapping();
	RequestMapping rm = AnnotationUtils.findAnnotation(method,
		RequestMapping.class);
	if (rm == null || rm.value() == null || rm.value().length!=1)
	    throw new IllegalArgumentException(
		    "Missing @RequestMapping on method " + method+" or more than one values");
	urlMapping.setUrl(rm.value()[0]);
	urlMapping.setHeaders(rm.headers());
	urlMapping.setConsumes(rm.consumes());
	urlMapping.setProduces(rm.produces());
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
		if (PathVariable.class.isAssignableFrom(annotation
			.annotationType())) {
		    PathVariable pv = (PathVariable) annotation;
		    parameterName = pv.value();
		    urlMapping.addDescriptor(new MethodParameterDescriptor(
			    Type.pathVariable, parameterName, value));
		    parameterFound = true;
		}
		if (RequestParam.class.isAssignableFrom(annotation
			.annotationType())) {
		    RequestParam pv = (RequestParam) annotation;
		    parameterName = pv.value();
		    urlMapping.addDescriptor(new MethodParameterDescriptor(
			    Type.httpParameter, parameterName, value));
		    parameterFound = true;
		}
		if (RequestBody.class.isAssignableFrom(annotation
			.annotationType())) {
		    parameterType = Type.requestBody;
		    parameterFound = true;
		}
		if (RequestPart.class.isAssignableFrom(annotation
			.annotationType())) {
		    parameterType = Type.requestPart;
		    parameterFound = true;
		}
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
