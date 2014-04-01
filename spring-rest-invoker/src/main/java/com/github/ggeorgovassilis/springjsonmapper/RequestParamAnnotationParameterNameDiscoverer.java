package com.github.ggeorgovassilis.springjsonmapper;

import java.lang.annotation.Annotation;

import org.springframework.web.bind.annotation.RequestParam;

/**
 * Discovers method parameter names declared by {@link RequestParam} annotations
 * @author george georgovassilis
 *
 */
public class RequestParamAnnotationParameterNameDiscoverer extends AbstractAnnotationParameterNameDiscoverer{

    @Override
    public boolean canHandleAnnotation(Annotation annotation) {
	return RequestParam.class.isAssignableFrom(annotation.annotationType());
    }

    @Override
    public String getParameterNameFromAnnotation(Annotation annotation) {
	RequestParam requestParam = ((RequestParam)annotation);
	return requestParam.value();
    }

}
