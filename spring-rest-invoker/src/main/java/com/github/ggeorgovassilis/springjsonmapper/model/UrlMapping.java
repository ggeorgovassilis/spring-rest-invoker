package com.github.ggeorgovassilis.springjsonmapper.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.RequestMapping;

import static com.github.ggeorgovassilis.springjsonmapper.model.MethodParameterDescriptor.Type;

/**
 * A collection of {@link MethodParameterDescriptor}s which describe the mapping of a method to a REST URL
 * @author george georgovassilis
 *
 */
public class UrlMapping {

    protected String httpMethod;
    protected List<MethodParameterDescriptor> parameters = new ArrayList<>();
    protected String[] headers;
    protected String[] consumes;
    protected String[] produces;
    
    public String[] getConsumes() {
        return consumes;
    }

    public void setConsumes(String[] consumes) {
        this.consumes = consumes;
    }

    public String[] getProduces() {
        return produces;
    }

    public void setProduces(String[] produces) {
        this.produces = produces;
    }

    protected String url;
    
    public String[] getHeaders() {
        return headers;
    }

    public void setHeaders(String[] headers) {
        this.headers = headers;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
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
	    if (parameter.equals(descriptor.getName()) && (descriptor.getType().equals(Type.requestBody)||descriptor.getType().equals(Type.requestPart)))
		return true;
	return false;
    }
}
