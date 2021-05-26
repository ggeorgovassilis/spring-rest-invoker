package com.github.ggeorgovassilis.springjsonmapper.spring.mapping;

import com.github.ggeorgovassilis.springjsonmapper.model.UrlMapping;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.PutMapping;

/**
 * Looks at class and method and extract mapping annotations such as
 * {@link PutMapping}
 *
 * @author minasgull
 */
public class PutMappingAnnotationResolver extends BaseAnnotationResolver<PutMapping> {

	@Override
	public UrlMapping resolve(PutMapping ann) {
		UrlMapping urlMapping = new UrlMapping();
		String path = getPath(ann.path());
		urlMapping.setUrl(path);
		urlMapping.setHttpMethod(HttpMethod.PUT);
		urlMapping.setHeaders(ann.headers());
		urlMapping.setConsumes(ann.consumes());
		urlMapping.setProduces(ann.produces());
		return urlMapping;
	}

	@Override
	public boolean supported(Class<PutMapping> annotation) {
		return PutMapping.class.isAssignableFrom(annotation);
	}

}
