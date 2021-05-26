package com.github.ggeorgovassilis.springjsonmapper.spring.mapping;

import com.github.ggeorgovassilis.springjsonmapper.model.UrlMapping;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.PatchMapping;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PatchMappingAnnotationResolverTest {

	private PatchMappingAnnotationResolver resolver;

	@BeforeEach
	void setUp() {
		resolver = new PatchMappingAnnotationResolver();
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
		assertEquals(HttpMethod.PATCH, actual.getHttpMethod());
	}

	private PatchMapping getAnnotation(Class<?> clazz, String methodName) {
		try {
			return AnnotatedElementUtils.getMergedAnnotation(clazz.getMethod(methodName, null), PatchMapping.class);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	interface AmbiguousPath {
		@PatchMapping(path = {"bbb", "ccc"})
		void method();
	}

	interface NoPath {
		@PatchMapping
		void method();
	}
}