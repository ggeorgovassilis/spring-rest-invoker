package com.github.ggeorgovassilis.springjsonmapper;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;

import com.github.ggeorgovassilis.springjsonmapper.MethodParameterDescriptor.Type;

/**
 * A collection of {@link MethodParameterDescriptor}s which describe the mapping of a method to a REST URL
 * @author george georgovassilis
 *
 */
public class UrlMapping {

    protected RequestMapping requestMapping;
    protected List<MethodParameterDescriptor> parameters = new ArrayList<>();

    public RequestMapping getRequestMapping() {
	return requestMapping;
    }

    public void setRequestMapping(RequestMapping requestMapping) {
	this.requestMapping = requestMapping;
    }

    public List<MethodParameterDescriptor> getParameters() {
	return parameters;
    }

    public void setParameters(List<MethodParameterDescriptor> parameters) {
	this.parameters = parameters;
    }

    public void addDescriptor(MethodParameterDescriptor descriptor) {
	parameters.add(descriptor);
    }
    
    public boolean hasRequestBody(String parameter){
	for (MethodParameterDescriptor descriptor:parameters)
			if (parameter.equals(descriptor.getName()) && (descriptor.getType().equals(Type.requestBody) || descriptor.getType().equals(Type.requestPart)))
		return true;
	return false;
    }
}
