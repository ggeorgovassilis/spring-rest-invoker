package com.github.ggeorgovassilis.springjsonmapper.spring;

import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.util.StringValueResolver;

import com.github.ggeorgovassilis.springjsonmapper.MethodInspector;

/**
 * Base class for annotation method inspectors
 * @author George Georgovassilis
 *
 */
public abstract class BaseAnnotationMethodInspector implements MethodInspector, EmbeddedValueResolverAware{

    protected StringValueResolver valueResolver;

    @Override
    public void setEmbeddedValueResolver(StringValueResolver resolver) {
	this.valueResolver = resolver;
    }
    
    protected String resolveExpression(String expression){
	return valueResolver.resolveStringValue(expression);
    }
}
