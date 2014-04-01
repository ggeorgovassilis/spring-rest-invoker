package com.github.ggeorgovassilis.springjsonmapper;

import java.lang.annotation.Annotation;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Discovers parameter names declared with {@link RequestParam} and {@link PathVariable} annotations
 * @author george georgovassilis
 *
 */
public class PathVariableAnnotationParameterNameDiscoverer extends
	RequestParamAnnotationParameterNameDiscoverer {

    @Override
    public boolean canHandleAnnotation(Annotation annotation) {
	return super.canHandleAnnotation(annotation)
		|| PathVariable.class.isAssignableFrom(annotation
			.annotationType());
    }

    @Override
    public String getParameterNameFromAnnotation(Annotation annotation) {
	if (annotation instanceof PathVariable) {
	    PathVariable pathVariable = ((PathVariable) annotation);
	    return pathVariable.value();
	} else
	    return super.getParameterNameFromAnnotation(annotation);
    }

}
