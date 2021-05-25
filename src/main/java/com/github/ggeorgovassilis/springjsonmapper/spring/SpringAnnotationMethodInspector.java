package com.github.ggeorgovassilis.springjsonmapper.spring;

import com.github.ggeorgovassilis.springjsonmapper.MethodInspector;
import com.github.ggeorgovassilis.springjsonmapper.model.Header;
import com.github.ggeorgovassilis.springjsonmapper.model.MappingDeclarationException;
import com.github.ggeorgovassilis.springjsonmapper.model.MethodParameterDescriptor;
import com.github.ggeorgovassilis.springjsonmapper.model.MethodParameterDescriptor.Type;
import com.github.ggeorgovassilis.springjsonmapper.model.UrlMapping;
import com.github.ggeorgovassilis.springjsonmapper.utils.Utils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * Looks at methods and extracts {@link RequestParam}, {@link PathVariable},
 * {@link RequestBody} and request mapping via provided {@link MappingAnnotationsInspector}
 *
 * @author george georgovassilis
 * @author minasgull
 */
public class SpringAnnotationMethodInspector extends BaseAnnotationMethodInspector {

	private final MethodInspector mappingAnnotationsInspection;

	public SpringAnnotationMethodInspector(MethodInspector mappingAnnotationsInspection) {
		this.mappingAnnotationsInspection = mappingAnnotationsInspection;
	}

	@Override
	public UrlMapping inspect(Method method, Object[] args) {
		UrlMapping urlMapping;
		try {
			urlMapping = mappingAnnotationsInspection.inspect(method, args);
		} catch (Exception e) {
			throw new MappingDeclarationException("Request mapping declaration parsing error", method, e);
		}
		urlMapping.setUrl(resolveExpression(urlMapping.getUrl()));
		Annotation[][] parameterAnnotations = method.getParameterAnnotations();
		if (parameterAnnotations.length != method.getParameterTypes().length)
			throw new MappingDeclarationException(
					String.format("Annotation mismatch: method has %d parameters but %d have been annotated on %s",
							parameterAnnotations.length, method.getParameterTypes().length, method),
					method);
		int i = 0;
		for (Annotation[] annotations : parameterAnnotations) {
			Object value = args[i];
			i++;
			String parameterName = "";
			Type parameterType = null;
			boolean parameterFound = false;
			boolean parameterNameRequired = false;
			for (Annotation annotation : annotations) {
				if (PathVariable.class.isAssignableFrom(annotation.annotationType())) {
					PathVariable pv = (PathVariable) annotation;
					parameterName = pv.value();
					parameterType = Type.pathVariable;
					parameterFound = true;
					parameterNameRequired = true;
				}
				if (RequestParam.class.isAssignableFrom(annotation.annotationType())) {
					RequestParam pv = (RequestParam) annotation;
					parameterName = pv.value();
					urlMapping.addDescriptor(
							new MethodParameterDescriptor(Type.httpParameter, parameterName, value, method, i));
					parameterFound = true;
					parameterNameRequired = true;
				}
				if (Header.class.isAssignableFrom(annotation.annotationType())) {
					Header h = (Header) annotation;
					parameterName = h.value();
					parameterType = Type.httpHeader;
					parameterFound = true;
					parameterNameRequired = true;
				}
				if (RequestBody.class.isAssignableFrom(annotation.annotationType())) {
					parameterType = Type.requestBody;
					parameterFound = true;
				}
				if (RequestPart.class.isAssignableFrom(annotation.annotationType())) {
					parameterType = Type.requestPart;
					parameterFound = true;
				}
				if (CookieValue.class.isAssignableFrom(annotation.annotationType())) {
					parameterType = Type.cookie;
					parameterFound = true;
					CookieValue cv = (CookieValue) annotation;
					parameterName = cv.value();
					parameterNameRequired = true;
				}
			}
			if (!parameterFound)
				throw new MappingDeclarationException(
						String.format("Couldn't find mapping annotation on parameter %d of method %s", i,
								method.toGenericString()),
						method, null, i);
			if (parameterType != null) {
				if (parameterNameRequired & !Utils.hasValue(parameterName))
					throw new MappingDeclarationException(String
							.format("No name specified for parameter %d on method %s", i, method.toGenericString()),
							method, null, i);
				urlMapping.addDescriptor(new MethodParameterDescriptor(parameterType, parameterName, value, method, i));
			}
		}
		return urlMapping;
	}
}
