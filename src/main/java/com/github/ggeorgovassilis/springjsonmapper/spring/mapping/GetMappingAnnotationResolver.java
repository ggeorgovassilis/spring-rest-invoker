package com.github.ggeorgovassilis.springjsonmapper.spring.mapping;

import com.github.ggeorgovassilis.springjsonmapper.model.UrlMapping;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Looks at class and method and extract mapping annotations such as
 * {@link GetMapping}
 *
 * @author minasgull
 */
public class GetMappingAnnotationResolver extends BaseAnnotationResolver<GetMapping> {

	@Override
	public UrlMapping resolve(GetMapping ann) {
		UrlMapping urlMapping = new UrlMapping();
		String path = getPath(ann.path());
		urlMapping.setUrl(path);
		urlMapping.setHttpMethod(HttpMethod.GET);
		urlMapping.setHeaders(ann.headers());
		urlMapping.setConsumes(ann.consumes());
		urlMapping.setProduces(ann.produces());
		return urlMapping;
	}

	@Override
	public boolean supported(Class<GetMapping> annotation) {
		return GetMapping.class.isAssignableFrom(annotation);
	}

}
