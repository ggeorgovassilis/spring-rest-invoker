package com.github.ggeorgovassilis.springjsonmapper.spring.mapping;

import com.github.ggeorgovassilis.springjsonmapper.model.UrlMapping;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.PatchMapping;

/**
 * Looks at class and method and extract mapping annotations such as
 * {@link PatchMapping}
 *
 * @author minasgull
 */
public class PatchMappingAnnotationResolver extends BaseAnnotationResolver<PatchMapping> {

	@Override
	public UrlMapping resolve(PatchMapping ann) {
		UrlMapping urlMapping = new UrlMapping();
		String path = getPath(ann.path());
		urlMapping.setUrl(path);
		urlMapping.setHttpMethod(HttpMethod.PATCH);
		urlMapping.setHeaders(ann.headers());
		urlMapping.setConsumes(ann.consumes());
		urlMapping.setProduces(ann.produces());
		return urlMapping;
	}

	@Override
	public boolean supported(Class<PatchMapping> annotation) {
		return PatchMapping.class.isAssignableFrom(annotation);
	}

}
