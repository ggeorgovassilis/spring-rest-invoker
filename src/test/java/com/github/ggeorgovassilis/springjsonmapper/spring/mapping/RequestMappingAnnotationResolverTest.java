package com.github.ggeorgovassilis.springjsonmapper.spring.mapping;

import com.github.ggeorgovassilis.springjsonmapper.model.UrlMapping;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RequestMappingAnnotationResolverTest {

	private RequestMappingAnnotationResolver resolver;

	@BeforeEach
	void setUp() {
		resolver = new RequestMappingAnnotationResolver();
	}

	@Test
	void testAmbiguousMethod() {
		assertThrows(AmbiguousMappingException.class, () -> {
			resolver.resolve(getAnnotation(AmbiguousMethod.class, "method"));
		});
	}

	@Test
	void testAmbiguousPath() {
		assertThrows(AmbiguousMappingException.class, () -> {
			resolver.resolve(getAnnotation(AmbiguousPath.class, "method"));
		});
	}

	@Test
	void testDuplicatedMethod() {
		UrlMapping actual = resolver.resolve(getAnnotation(DuplicatedMethod.class, "method"));
		assertNotNull(actual);
		assertEquals("/bbb", actual.getUrl());
		assertEquals(HttpMethod.PATCH, actual.getHttpMethod());
	}

	@Test
	void testNoMethod() {
		UrlMapping actual = resolver.resolve(getAnnotation(NoMethod.class, "method"));
		assertNotNull(actual);
		assertEquals("/", actual.getUrl());
		assertEquals(HttpMethod.GET, actual.getHttpMethod());
	}

	@Test
	void testNoPath() {
		UrlMapping actual = resolver.resolve(getAnnotation(NoPath.class, "method"));
		assertNotNull(actual);
		assertEquals("/", actual.getUrl());
		assertEquals(HttpMethod.DELETE, actual.getHttpMethod());
	}

	private RequestMapping getAnnotation(Class<?> clazz, String methodName) {
		try {
			return AnnotatedElementUtils.getMergedAnnotation(clazz.getMethod(methodName, null), RequestMapping.class);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	interface AmbiguousMethod {
		@RequestMapping(path = "bbb", method = {RequestMethod.GET, RequestMethod.POST})
		void method();
	}

	interface AmbiguousPath {
		@RequestMapping(path = {"bbb", "ccc"}, method = RequestMethod.GET)
		void method();
	}

	interface DuplicatedMethod {
		@RequestMapping(path = "bbb", method = {RequestMethod.PATCH, RequestMethod.PATCH})
		void method();
	}

	interface NoMethod {
		@RequestMapping(value = "/")
		void method();
	}

	interface NoPath {
		@RequestMapping(method = RequestMethod.DELETE)
		void method();
	}
}