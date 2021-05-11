package com.github.ggeorgovassilis.springjsonmapper.spring;

import com.github.ggeorgovassilis.springjsonmapper.MethodInspector;
import com.github.ggeorgovassilis.springjsonmapper.model.MappingDeclarationException;
import com.github.ggeorgovassilis.springjsonmapper.model.UrlMapping;
import org.junit.jupiter.api.Test;
import org.springframework.util.StringValueResolver;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests a few code paths in {@link SpringAnnotationMethodInspector} that are
 * not covered by other tests.
 *
 * @author george georgovassilis
 */
@RequestMapping
public class TestSpringAnnotationMethodInspector {

	@RequestMapping
	public void someMethod1() {
	}

	@RequestMapping("/someMethod")
	public void someMethod2(String p1, @RequestParam int p2, String p3) {
	}

	@Test
	public void testMissingParameters() throws Exception {
		assertThrows(MappingDeclarationException.class, () -> {
			MethodInspector mappingInspector = mock(MethodInspector.class);
			when(mappingInspector.inspect(any(), any())).thenReturn(new UrlMapping());
			SpringAnnotationMethodInspector inspector = new SpringAnnotationMethodInspector(mappingInspector);
			StringValueResolver resolver = mock(StringValueResolver.class);
			inspector.setEmbeddedValueResolver(resolver);

			Method someMethod2 = getClass().getMethod("someMethod2", String.class, int.class, String.class);
			inspector.inspect(someMethod2, new Object[]{"a", 2, "c"});

		});
	}
}
