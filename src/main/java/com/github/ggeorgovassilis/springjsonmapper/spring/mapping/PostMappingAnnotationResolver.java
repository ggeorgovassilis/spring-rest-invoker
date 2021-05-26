package com.github.ggeorgovassilis.springjsonmapper.spring.mapping;

import com.github.ggeorgovassilis.springjsonmapper.model.UrlMapping;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Looks at class and method and extract mapping annotations such as
 * {@link PostMapping}
 *
 * @author minasgull
 */
public class PostMappingAnnotationResolver extends BaseAnnotationResolver<PostMapping> {

	@Override
	public UrlMapping resolve(PostMapping ann) {
		UrlMapping urlMapping = new UrlMapping();
		String path = getPath(ann.path());
		urlMapping.setUrl(path);
		urlMapping.setHttpMethod(HttpMethod.POST);
		urlMapping.setHeaders(ann.headers());
		urlMapping.setConsumes(ann.consumes());
		urlMapping.setProduces(ann.produces());
		return urlMapping;
	}

	@Override
	public boolean supported(Class<PostMapping> annotation) {
		return PostMapping.class.isAssignableFrom(annotation);
	}

}
