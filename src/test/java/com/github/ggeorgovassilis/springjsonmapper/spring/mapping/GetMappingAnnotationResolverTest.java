package com.github.ggeorgovassilis.springjsonmapper.spring.mapping;

import com.github.ggeorgovassilis.springjsonmapper.model.UrlMapping;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.GetMapping;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GetMappingAnnotationResolverTest {

	private GetMappingAnnotationResolver resolver;

	@BeforeEach
	void setUp() {
		resolver = new GetMappingAnnotationResolver();
	}

	@Test
	void testAmbiguousPath() {
		assertThrows(AmbiguousMappingException.class, () -> {
			resolver.resolve(getAnnotation(AmbiguousPath.class, "method"));
		});
	}

	@Test
	void testNoPath() {
		UrlMapping actual = resolver.resolve(getAnnotation(NoPath.class, "method"));
		assertNotNull(actual);
		assertEquals("/", actual.getUrl());
		assertEquals(HttpMethod.GET, actual.getHttpMethod());
	}

	private GetMapping getAnnotation(Class<?> clazz, String methodName) {
		try {
			return AnnotatedElementUtils.getMergedAnnotation(clazz.getMethod(methodName, null), GetMapping.class);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	interface AmbiguousPath {
		@GetMapping(path = {"bbb", "ccc"})
		void method();
	}

	interface NoPath {
		@GetMapping
		void method();
	}
}