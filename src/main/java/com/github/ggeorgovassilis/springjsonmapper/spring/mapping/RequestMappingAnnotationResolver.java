package com.github.ggeorgovassilis.springjsonmapper.spring.mapping;

import com.github.ggeorgovassilis.springjsonmapper.model.UrlMapping;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Looks at class and method and extract mapping annotations such as
 * {@link RequestMapping}
 *
 * @author minasgull
 */
public class RequestMappingAnnotationResolver extends BaseAnnotationResolver<RequestMapping> {

	@Override
	public UrlMapping resolve(RequestMapping ann) {
		UrlMapping urlMapping = new UrlMapping();
		String path = getPath(ann.path());
		RequestMethod method = getMethod(ann.method());
		urlMapping.setUrl(path);
		urlMapping.setHttpMethod(HttpMethod.valueOf(method.name()));
		urlMapping.setHeaders(ann.headers());
		urlMapping.setConsumes(ann.consumes());
		urlMapping.setProduces(ann.produces());
		return urlMapping;
	}

	@Override
	public boolean supported(Class<RequestMapping> annotation) {
		return RequestMapping.class.isAssignableFrom(annotation);
	}

}
