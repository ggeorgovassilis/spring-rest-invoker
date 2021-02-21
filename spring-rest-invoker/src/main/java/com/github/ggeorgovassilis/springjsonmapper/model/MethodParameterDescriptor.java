package com.github.ggeorgovassilis.springjsonmapper.model;

import java.lang.reflect.Method;

/**
 * Models the relation of a method parameter with an url (fragment) such as part
 * in a path or parameter name
 * 
 * @author george georgovassilis
 *
 */
public class MethodParameterDescriptor {

	public enum Type {
		httpParameter, pathVariable, requestBody, requestPart, cookie, httpHeader
	};

	protected Type type = Type.httpParameter;
	protected String name;
	protected Object value;
	protected Method method;
	protected int parameterOrdinal;

	public MethodParameterDescriptor() {
	}

	public MethodParameterDescriptor(Type type, String name, Object value, Method method, int parameterOrdinal) {
		setType(type);
		setName(name);
		setValue(value);
		this.method = method;
		this.parameterOrdinal = parameterOrdinal;
	}

	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}

	public int getParameterOrdinal() {
		return parameterOrdinal;
	}

	public void setParameterOrdinal(int parameterOrdinal) {
		this.parameterOrdinal = parameterOrdinal;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

}
