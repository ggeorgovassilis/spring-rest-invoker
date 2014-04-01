package com.github.ggeorgovassilis.springjsonmapper;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.springframework.core.ParameterNameDiscoverer;
/**
 * Determines method argument names by looking at annotations
 * @author george georgovassilis
 *
 */
public abstract class AbstractAnnotationParameterNameDiscoverer implements ParameterNameDiscoverer{

    public abstract boolean canHandleAnnotation(Annotation annotation);
    public abstract String getParameterNameFromAnnotation(Annotation annotation);
    
    @Override
    public String[] getParameterNames(Method method) {
	Annotation[][] parameterAnnotations = method.getParameterAnnotations();
	if (parameterAnnotations.length!=method.getParameterTypes().length)
	    throw new IllegalArgumentException(String.format("Annotation mismatch: method has %d parameters but %d have been annotated on %s",parameterAnnotations.length,method.getParameterTypes().length, method.toString()));
	List<String> paramNames = new ArrayList<>();
	for (Annotation[] annotations:parameterAnnotations)
	for (Annotation annotation:annotations)
	    if (canHandleAnnotation(annotation)){
		String paramName = getParameterNameFromAnnotation(annotation);
		paramNames.add(paramName);
		break;
	    }
	if (paramNames.isEmpty())
	    return null;
	return paramNames.toArray(new String[0]);
    }

    @Override
    public String[] getParameterNames(Constructor<?> ctor) {
	throw new RuntimeException("Not implemented");
    }

}
