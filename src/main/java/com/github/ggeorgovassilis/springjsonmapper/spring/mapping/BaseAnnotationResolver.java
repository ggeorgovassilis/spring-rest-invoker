package com.github.ggeorgovassilis.springjsonmapper.spring.mapping;

import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

/**
 * A base mapping annotation resolver with common login for all mapping annotations
 *
 * @param <T> A mapping annotation type
 * @author minasgull
 */
public abstract class BaseAnnotationResolver<T extends Annotation> implements MappingAnnotationResolver<T> {

	protected BaseAnnotationResolver() {
	}

	protected String getPath(String[] paths) {
		if (paths.length > 1) {
			throw new AmbiguousMappingException("Declared multiple paths " + Arrays.toString(paths));
		} else if (paths.length == 0) {
			return "/";
		}
		String result = paths[0];
		result = result.endsWith("/") ? result : result + "/";
		return result;
	}

	protected RequestMethod getMethod(RequestMethod[] methods) {
		Set<RequestMethod> uniqueMethods = new TreeSet<>(Arrays.asList(methods));
		if (uniqueMethods.size() > 1) {
			throw new AmbiguousMappingException("Declared multiple methods " + Arrays.toString(methods));
		} else if (uniqueMethods.size() == 0) {
			return RequestMethod.GET;
		} else {
			return uniqueMethods.iterator().next();
		}
	}

}
