package com.github.ggeorgovassilis.springjsonmapper.spring.mapping;

import com.github.ggeorgovassilis.springjsonmapper.model.UrlMapping;

import java.lang.annotation.Annotation;

/**
 * A mapping annotation resolver
 *
 * @param <T> A mapping annotation type
 * @author minasgull
 */
public interface MappingAnnotationResolver<T extends Annotation> {
	UrlMapping resolve(T mappingAnnotation);

	boolean supported(Class<T> annotation);
}
