package com.github.ggeorgovassilis.springjsonmapper.spring;

import java.lang.reflect.Method;

import org.junit.jupiter.api.Test;
import org.springframework.util.StringValueResolver;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.github.ggeorgovassilis.springjsonmapper.model.MappingDeclarationException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/**
 * Tests a few code paths in {@link SpringAnnotationMethodInspector} that are
 * not covered by other tests.
 * 
 * @author george georgovassilis
 *
 */
public class TestSpringAnnotationMethodInspector {

	@RequestMapping
	public void someMethod1() {
	}

	@RequestMapping("/someMethod")
	public void someMethod2(String p1, @RequestParam int p2, String p3) {
	}

	@Test
	public void testEmptyRequestMapping() throws Exception {
		assertThrows(MappingDeclarationException.class, () -> {
			SpringAnnotationMethodInspector inspector = new SpringAnnotationMethodInspector();
			Method someMethod1 = getClass().getMethod("someMethod1");
			inspector.inspect(someMethod1, null);

		});
	}

	@Test
	public void testMissingParameters() throws Exception {
		assertThrows(MappingDeclarationException.class, () -> {
			SpringAnnotationMethodInspector inspector = new SpringAnnotationMethodInspector();
			StringValueResolver resolver = mock(StringValueResolver.class);
			inspector.setEmbeddedValueResolver(resolver);

			Method someMethod2 = getClass().getMethod("someMethod2", String.class, int.class, String.class);
			inspector.inspect(someMethod2, new Object[] { "a", 2, "c" });

		});
	}
}
