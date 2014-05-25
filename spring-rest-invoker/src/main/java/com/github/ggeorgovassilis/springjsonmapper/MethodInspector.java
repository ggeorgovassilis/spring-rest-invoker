package com.github.ggeorgovassilis.springjsonmapper;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;

import com.github.ggeorgovassilis.springjsonmapper.MethodParameterDescriptor.Type;

/**
 * Looks at methods and extracts {@link RequestParam}, {@link PathVariable}, {@link RequestBody} and {@link RequestMapping} annotations
 * @author george georgovassilis
 *
 */
public class MethodInspector {

    public UrlMapping inspect(Method method, Object[] args) {
	UrlMapping urlMapping = new UrlMapping();
	urlMapping.setRequestMapping(AnnotationUtils.findAnnotation(method,
		RequestMapping.class));
	if (urlMapping.getRequestMapping() == null)
	    throw new IllegalArgumentException(
		    "Missing @RequestMapping on method " + method);
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
	    for (Annotation annotation : annotations) {
		if (PathVariable.class.isAssignableFrom(annotation
			.annotationType())) {
		    PathVariable pv = (PathVariable) annotation;
		    parameterName = pv.value();
		    urlMapping.addDescriptor(new MethodParameterDescriptor(
			    Type.pathVariable, parameterName, value));
		}
		if (RequestParam.class.isAssignableFrom(annotation
			.annotationType())) {
		    RequestParam pv = (RequestParam) annotation;
		    parameterName = pv.value();
		    urlMapping.addDescriptor(new MethodParameterDescriptor(
			    Type.httpParameter, parameterName, value));
		}
		if (RequestBody.class.isAssignableFrom(annotation
			.annotationType())) {
		    parameterType = Type.requestBody;
		}
		if (RequestPart.class.isAssignableFrom(annotation
			.annotationType())) {
		    parameterType = Type.requestPart;
		}
	    }
	    if (parameterType!=null) {
		urlMapping.addDescriptor(new MethodParameterDescriptor(parameterType, parameterName, value));
	    }
	}
	return urlMapping;
    }
}
