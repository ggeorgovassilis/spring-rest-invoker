package com.github.ggeorgovassilis.springjsonmapper.spring;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;

import com.github.ggeorgovassilis.springjsonmapper.model.Header;
import com.github.ggeorgovassilis.springjsonmapper.model.MappingDeclarationException;
import com.github.ggeorgovassilis.springjsonmapper.model.MethodParameterDescriptor;
import com.github.ggeorgovassilis.springjsonmapper.model.UrlMapping;
import com.github.ggeorgovassilis.springjsonmapper.model.MethodParameterDescriptor.Type;
import com.github.ggeorgovassilis.springjsonmapper.utils.Utils;

/**
 * Looks at methods and extracts {@link RequestParam}, {@link PathVariable},
 * {@link RequestBody} and {@link RequestMapping} annotations
 * 
 * @author george georgovassilis
 * 
 */
public class SpringAnnotationMethodInspector extends BaseAnnotationMethodInspector{

    @Override
    public UrlMapping inspect(Method method, Object[] args) {
	UrlMapping urlMapping = new UrlMapping();
	RequestMapping rm = AnnotationUtils.findAnnotation(method,
		RequestMapping.class);
	if (rm == null)
	    return null;
	if (!Utils.hasValue(rm.value()))
	    throw new MappingDeclarationException("Path missing from @RequestMapping on "+method.toGenericString(), method, rm, -1);
	urlMapping.setUrl(resolveExpression(rm.value()[0]));
	urlMapping.setHeaders(rm.headers());
	urlMapping.setConsumes(rm.consumes());
	urlMapping.setProduces(rm.produces());
	if (Utils.hasValue(rm.method())) {
	    if (rm.method().length!=1)
		throw new MappingDeclarationException("Multiple HTTP methods on @RequestMapping on "+method.toGenericString(), method, rm, -1);
	    urlMapping.setHttpMethod(HttpMethod.valueOf(rm.method()[0].name()));
	}
	Annotation[][] parameterAnnotations = method.getParameterAnnotations();
	if (parameterAnnotations.length != method.getParameterTypes().length)
	    throw new MappingDeclarationException(
		    String.format(
			    "Annotation mismatch: method has %d parameters but %d have been annotated on %s",
			    parameterAnnotations.length,
			    method.getParameterTypes().length,
			    method.toString()), method, rm, -1);
	int i = 0;
	for (Annotation[] annotations : parameterAnnotations) {
	    Object value = args[i];
	    i++;
	    String parameterName = "";
	    Type parameterType = null;
	    boolean parameterFound = false;
	    boolean parameterNameRequired = false;
	    for (Annotation annotation : annotations) {
		if (PathVariable.class.isAssignableFrom(annotation
			.annotationType())) {
		    PathVariable pv = (PathVariable) annotation;
		    parameterName = pv.value();
		    parameterType = Type.pathVariable;
		    parameterFound = true;
		    parameterNameRequired = true;
		}
		if (RequestParam.class.isAssignableFrom(annotation
			.annotationType())) {
		    RequestParam pv = (RequestParam) annotation;
		    parameterName = pv.value();
		    urlMapping.addDescriptor(new MethodParameterDescriptor(
			    Type.httpParameter, parameterName, value, method, i));
		    parameterFound = true;
		    parameterNameRequired = true;
		}
		if (Header.class.isAssignableFrom(annotation
			.annotationType())) {
		    Header h = (Header) annotation;
		    parameterName = h.value();
		    parameterType = Type.httpHeader;
		    parameterFound = true;
		    parameterNameRequired = true;
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
		if (CookieValue.class.isAssignableFrom(annotation
			.annotationType())) {
		    parameterType = Type.cookie;
		    parameterFound = true;
		    CookieValue cv = (CookieValue) annotation;
		    parameterName = cv.value();
		    parameterNameRequired = true;
		}
	    }
	    if (!parameterFound)
		throw new MappingDeclarationException(
			String.format(
				"Couldn't find mapping annotation on parameter %d of method %s",
				i, method.toGenericString()), method, null, i);
	    if (parameterType != null) {
		if (parameterNameRequired & !Utils.hasValue(parameterName))
		    throw new MappingDeclarationException(String.format("No name specified for parameter %d on method %s", i, method.toGenericString()), method, null, i);
		urlMapping.addDescriptor(new MethodParameterDescriptor(
			parameterType, parameterName, value, method, i));
	    }
	}
	return urlMapping;
    }
}
