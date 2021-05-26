package com.github.ggeorgovassilis.springjsonmapper.spring.mapping;

import com.github.ggeorgovassilis.springjsonmapper.model.UrlMapping;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.DeleteMapping;

/**
 * Looks at class and method and extract mapping annotations such as
 * {@link DeleteMapping}
 *
 * @author minasgull
 */
public class DeleteMappingAnnotationResolver extends BaseAnnotationResolver<DeleteMapping> {

	@Override
	public UrlMapping resolve(DeleteMapping ann) {
		UrlMapping urlMapping = new UrlMapping();
		String path = getPath(ann.path());
		urlMapping.setUrl(path);
		urlMapping.setHttpMethod(HttpMethod.DELETE);
		urlMapping.setHeaders(ann.headers());
		urlMapping.setConsumes(ann.consumes());
		urlMapping.setProduces(ann.produces());
		return urlMapping;
	}

	@Override
	public boolean supported(Class<DeleteMapping> annotation) {
		return DeleteMapping.class.isAssignableFrom(annotation);
	}

}
