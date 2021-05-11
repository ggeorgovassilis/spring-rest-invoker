package com.github.ggeorgovassilis.springjsonmapper.spring;

import com.github.ggeorgovassilis.springjsonmapper.model.MappingDeclarationException;
import com.github.ggeorgovassilis.springjsonmapper.model.UrlMapping;
import com.github.ggeorgovassilis.springjsonmapper.spring.mapping.MappingAnnotationResolver;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Looks at class and method and extract mapping annotations such as
 * {@link DeleteMapping}, {@link GetMapping}, {@link PatchMapping}, {@link PostMapping},
 * {@link PutMapping}, {@link RequestMapping}
 * <p>
 * In case if annotation exists on both levels it will be merged following the spring-web rules
 *
 * @author minasgull
 */
public class MappingAnnotationsInspector extends BaseAnnotationMethodInspector {

	private final List<MappingAnnotationResolver<? extends Annotation>> mappingAnnotationResolvers;

	public MappingAnnotationsInspector(List<MappingAnnotationResolver<? extends Annotation>> mappingAnnotationResolvers) {
		this.mappingAnnotationResolvers = mappingAnnotationResolvers;
	}

	@Override
	public UrlMapping inspect(Method method, Object[] args) {
		UrlMapping methodUrlMapping = resolveFor(method);
		UrlMapping classUrlMapping = resolveFor(method.getDeclaringClass());
		if (classUrlMapping == null) {
			if (methodUrlMapping == null) {
				return new UrlMapping();
			} else {
				return methodUrlMapping;
			}
		}
		if (methodUrlMapping == null) {
			return classUrlMapping;
		}
		String path = classUrlMapping.getUrl() +
				(methodUrlMapping.getUrl().startsWith("/")
						? methodUrlMapping.getUrl().substring(1)
						: methodUrlMapping.getUrl());
		methodUrlMapping.setUrl(path);
		if (methodUrlMapping.getConsumes().length == 0) {
			methodUrlMapping.setConsumes(classUrlMapping.getConsumes());
		}
		if (methodUrlMapping.getProduces().length == 0) {
			methodUrlMapping.setProduces(classUrlMapping.getProduces());
		}
		if (methodUrlMapping.getHeaders().length == 0) {
			methodUrlMapping.setHeaders(classUrlMapping.getHeaders());
		}
		return methodUrlMapping;
	}

	private UrlMapping resolveFor(AnnotatedElement annotatedElement) {
		return Stream.of(
				AnnotatedElementUtils.getMergedAnnotation(annotatedElement, RequestMapping.class),
				AnnotatedElementUtils.getMergedAnnotation(annotatedElement, GetMapping.class),
				AnnotatedElementUtils.getMergedAnnotation(annotatedElement, PostMapping.class),
				AnnotatedElementUtils.getMergedAnnotation(annotatedElement, PutMapping.class),
				AnnotatedElementUtils.getMergedAnnotation(annotatedElement, PatchMapping.class),
				AnnotatedElementUtils.getMergedAnnotation(annotatedElement, DeleteMapping.class))
				.filter(Objects::nonNull)
				.findFirst()
				.map(a -> getResolver(a).resolve(a))
				.orElse(null);
	}

	private MappingAnnotationResolver getResolver(Annotation annotation) {
		for (MappingAnnotationResolver candidate : mappingAnnotationResolvers) {
			if (candidate.supported(annotation.getClass())) {
				return candidate;
			}
		}
		throw new MappingDeclarationException("Not implemented resolver for annotation ", null, annotation, -1);
	}

}
