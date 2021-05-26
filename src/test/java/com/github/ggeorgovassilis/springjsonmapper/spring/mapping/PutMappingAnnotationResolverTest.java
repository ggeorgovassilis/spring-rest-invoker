package com.github.ggeorgovassilis.springjsonmapper.spring.mapping;

import com.github.ggeorgovassilis.springjsonmapper.model.UrlMapping;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.PutMapping;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PutMappingAnnotationResolverTest {

	private PutMappingAnnotationResolver resolver;

	@BeforeEach
	void setUp() {
		resolver = new PutMappingAnnotationResolver();
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
		assertEquals(HttpMethod.PUT, actual.getHttpMethod());
	}

	private PutMapping getAnnotation(Class<?> clazz, String methodName) {
		try {
			return AnnotatedElementUtils.getMergedAnnotation(clazz.getMethod(methodName, null), PutMapping.class);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	interface AmbiguousPath {
		@PutMapping(path = {"bbb", "ccc"})
		void method();
	}

	interface NoPath {
		@PutMapping
		void method();
	}
}