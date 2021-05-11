package com.github.ggeorgovassilis.springjsonmapper.model;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import com.github.ggeorgovassilis.springjsonmapper.MethodInspector;

/**
 * Exception thrown by the a {@link MethodInspector} implementation when mapping
 * errors are detected on a service method or its arguments.
 * 
 * @author george georgovassilis
 *
 */
public class MappingDeclarationException extends RuntimeException {
	protected Method method;
	protected Annotation annotation;
	protected int parameterIndex;

	public MappingDeclarationException(String message, Method method, Annotation annotation, int parameterIndex) {
		super(message);
		this.method = method;
		this.annotation = annotation;
		this.parameterIndex = parameterIndex;
	}

	public MappingDeclarationException(String message, Method method, Throwable throwable) {
		super(message, throwable);
		this.method = method;
	}

	public MappingDeclarationException(String message, Method method) {
		super(message);
		this.method = method;
	}
}
