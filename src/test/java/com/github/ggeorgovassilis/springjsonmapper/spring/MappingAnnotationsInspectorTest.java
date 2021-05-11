package com.github.ggeorgovassilis.springjsonmapper.spring;

import com.github.ggeorgovassilis.springjsonmapper.model.UrlMapping;
import com.github.ggeorgovassilis.springjsonmapper.spring.mapping.MappingAnnotationResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.reflect.Method;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MappingAnnotationsInspectorTest {

	private static final Object[] NO_ARGS = new Object[0];
	private MappingAnnotationResolver<?> resolver;
	private MappingAnnotationsInspector inspector;

	@BeforeEach
	void setUp() {
		resolver = mock(MappingAnnotationResolver.class);
		inspector = new MappingAnnotationsInspector(Collections.singletonList(resolver));
	}

	@Test
	void testClassLevelOnlyAnnotation() {
		UrlMapping expected = new UrlMapping();
		when(resolver.supported(any())).thenReturn(true);
		when(resolver.resolve(any())).thenReturn(expected);
		UrlMapping actual = inspector.inspect(getMethod(ClassLevelOnly.class, "method"), NO_ARGS);
		assertEquals(expected, actual);
	}

	@Test
	void testMethodLevelOnlyAnnotation() {
		UrlMapping expected = new UrlMapping();
		when(resolver.supported(any())).thenReturn(true);
		when(resolver.resolve(any())).thenReturn(expected);
		UrlMapping actual = inspector.inspect(getMethod(MethodLevelOnly.class, "method"), NO_ARGS);
		assertEquals(expected, actual);
	}

	@Test
	void testNoLevelOnlyAnnotation() {
		when(resolver.supported(any())).thenReturn(true);
		when(resolver.resolve(any())).thenReturn(null);
		UrlMapping actual = inspector.inspect(getMethod(NoAtAnyLevel.class, "method"), NO_ARGS);
		assertNotNull(actual);
		assertEquals(HttpMethod.GET, actual.getHttpMethod());
		assertEquals("/", actual.getUrl());
	}

	@Test
	void testBothLevelsAnnotation() {
		when(resolver.supported(any())).thenReturn(true);
		when(resolver.resolve(any())).thenAnswer(i -> {
			RequestMapping annotation = i.getArgument(0, RequestMapping.class);
			UrlMapping urlMapping = new UrlMapping();
			urlMapping.setUrl(annotation.value()[0]);
			urlMapping.setHttpMethod(HttpMethod.valueOf(annotation.method()[0].name()));
			return urlMapping;
		});
		UrlMapping actual = inspector.inspect(getMethod(BothLevel.class, "method"), NO_ARGS);
		assertNotNull(actual);
		assertEquals(HttpMethod.PATCH, actual.getHttpMethod());
		assertEquals("aaabbb", actual.getUrl());
	}

	private Method getMethod(Class<?> clazz, String methodName) {
		try {
			return clazz.getMethod(methodName, null);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@RequestMapping(value = "aaa", method = RequestMethod.GET)
	interface ClassLevelOnly {
		void method();
	}

	@RequestMapping(value = "aaa", method = RequestMethod.POST)
	interface BothLevel {
		@RequestMapping(path = "/bbb", method = RequestMethod.PATCH)
		void method();
	}

	interface NoAtAnyLevel {
		void method();
	}

	interface MethodLevelOnly {
		@RequestMapping(path = "bbb", method = RequestMethod.GET)
		void method();
	}

}