package com.github.ggeorgovassilis.springjsonmapper.jaxrs;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpMethod;

import com.github.ggeorgovassilis.springjsonmapper.model.MappingDeclarationException;
import com.github.ggeorgovassilis.springjsonmapper.model.MethodParameterDescriptor;
import com.github.ggeorgovassilis.springjsonmapper.model.UrlMapping;
import com.github.ggeorgovassilis.springjsonmapper.model.MethodParameterDescriptor.Type;
import com.github.ggeorgovassilis.springjsonmapper.spring.BaseAnnotationMethodInspector;

/**
 * Looks at methods and extracts {@link Path}, {@link GET}, {@link QueryParam}
 * etc annotations
 * 
 * @author george georgovassilis
 * 
 */
public class JaxRsAnnotationMethodInspector extends BaseAnnotationMethodInspector {

    /*
     * RequestMapping -> PATH, GET, POST RequestParam -> QueryParam PathVariable
     * -> PathParam RequestBody -> BeanParam RequestPart -> FormParam
     */
    @Override
    public UrlMapping inspect(Method method, Object[] args) {
	UrlMapping urlMapping = new UrlMapping();
	Path path = AnnotationUtils.findAnnotation(method, Path.class);
	if (path == null || path.value() == null)
	    return null;
	urlMapping.setUrl(resolveExpression(path.value()));
	if (urlMapping.getUrl() == null)
	    throw new MappingDeclarationException(
		    "Missing @Path on method " + method, method, path, -1);
	Set<HttpMethod> httpMethods = new HashSet<HttpMethod>();
	if (AnnotationUtils.findAnnotation(method, POST.class) != null)
	    httpMethods.add(HttpMethod.POST);
	if (AnnotationUtils.findAnnotation(method, PUT.class) != null)
	    httpMethods.add(HttpMethod.PUT);
	if (AnnotationUtils.findAnnotation(method, DELETE.class) != null)
	    httpMethods.add(HttpMethod.DELETE);
	if (httpMethods.size()>1)
	    throw new MappingDeclarationException("Multiple HTTP methods specified on "+method.toGenericString(), method, null, -1);
	if (httpMethods.size()==1)
	    urlMapping.setHttpMethod(httpMethods.iterator().next());
	else
	    urlMapping.setHttpMethod(HttpMethod.GET);
	Produces produces = AnnotationUtils.findAnnotation(method,
		Produces.class);
	if (produces != null)
	    urlMapping.setProduces(produces.value());

	Consumes consumes = AnnotationUtils.findAnnotation(method,
		Consumes.class);
	if (consumes != null)
	    urlMapping.setConsumes(consumes.value());

	Annotation[][] parameterAnnotations = method.getParameterAnnotations();
	if (parameterAnnotations.length != method.getParameterTypes().length)
	    throw new MappingDeclarationException(
		    String.format(
			    "Annotation mismatch: method has %d parameters but %d have been annotated on %s",
			    parameterAnnotations.length,
			    method.getParameterTypes().length,
			    method.toString()),method, null, -1);
	int i = 0;
	for (Annotation[] annotations : parameterAnnotations) {
	    Object value = args[i];
	    i++;
	    String parameterName = "";
	    Type parameterType = null;
	    boolean parameterFound = false;
	    for (Annotation annotation : annotations) {
		if (PathParam.class.isAssignableFrom(annotation.annotationType())) {
		    PathParam pv = (PathParam) annotation;
		    parameterName = pv.value();
		    urlMapping.addDescriptor(new MethodParameterDescriptor(
			    Type.pathVariable, parameterName, value, method, i));
		    parameterFound = true;
		}
		if (QueryParam.class.isAssignableFrom(annotation
			.annotationType())) {
		    QueryParam pv = (QueryParam) annotation;
		    parameterName = pv.value();
		    urlMapping.addDescriptor(new MethodParameterDescriptor(
			    Type.httpParameter, parameterName, value, method, i));
		    parameterFound = true;
		}
		if (HeaderParam.class.isAssignableFrom(annotation
			.annotationType())) {
		    HeaderParam pv = (HeaderParam) annotation;
		    parameterName = pv.value();
		    parameterFound = true;
		    parameterType = Type.httpHeader;
		}
		if (BeanParam.class.isAssignableFrom(annotation
			.annotationType())) {
		    parameterType = Type.requestBody;
		    parameterFound = true;
		}
		if (FormParam.class.isAssignableFrom(annotation
			.annotationType())) {
		    parameterType = Type.requestPart;
		    parameterFound = true;
		}
		if (CookieParam.class.isAssignableFrom(annotation
			.annotationType())) {
		    parameterType = Type.cookie;
		    parameterFound = true;
		    CookieParam cv = (CookieParam) annotation;
		    parameterName = cv.value();
		}
	    }
	    if (!parameterFound)
		throw new MappingDeclarationException(
			String.format(
				"Couldn't find mapping annotation on parameter %d of method %s",
				i, method.toGenericString()), method, null, i);
	    if (parameterType != null) {
		urlMapping.addDescriptor(new MethodParameterDescriptor(
			parameterType, parameterName, value, method, i));
	    }
	}
	return urlMapping;
    }
}
