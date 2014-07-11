package com.github.ggeorgovassilis.springjsonmapper.model;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;


public class MappingDeclarationException extends RuntimeException{
    protected Method method;
    protected Annotation annotation;
    protected int parameterIndex;
    
    public MappingDeclarationException(String message, Method method, Annotation annotation, int parameterIndex) {
	super(message);
	this.method = method;
	this.annotation = annotation;
	this.parameterIndex = parameterIndex;
    }
}
