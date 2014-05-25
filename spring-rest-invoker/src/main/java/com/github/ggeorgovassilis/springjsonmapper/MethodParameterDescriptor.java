package com.github.ggeorgovassilis.springjsonmapper;

/**
 * Maps the relation of a method parameter with an url (fragment) such as part in a path or parameter name
 * @author george georgovassilis
 *
 */
public class MethodParameterDescriptor {

    public enum Type {
	httpParameter, pathVariable, requestBody, requestPart
    };

    protected Type type = Type.httpParameter;
    protected String name;
    protected Object value;

    public MethodParameterDescriptor(){
	
    }
    
    public MethodParameterDescriptor(Type type, String name, Object value){
	setType(type);
	setName(name);
	setValue(value);
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
