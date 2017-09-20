package com.github.ggeorgovassilis.springjsonmapper.spring;

import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.util.StringValueResolver;

import com.github.ggeorgovassilis.springjsonmapper.MethodInspector;

/**
 * Base class for annotation method inspectors
 * 
 * @author George Georgovassilis
 *
 */
public abstract class BaseAnnotationMethodInspector implements MethodInspector, EmbeddedValueResolverAware {

	protected StringValueResolver valueResolver;

	@Override
	public void setEmbeddedValueResolver(StringValueResolver resolver) {
		this.valueResolver = resolver;
	}

	/**
	 * Will replace property placeholders with their values, e.g.
	 * ${serverUrl}/customer with http://example.com/js/customer if serverUrl is a
	 * property that the application context resolves to http://example.com.js
	 * 
	 * @param expression
	 * @return
	 */
	protected String resolveExpression(String expression) {
		return valueResolver.resolveStringValue(expression);
	}
}
