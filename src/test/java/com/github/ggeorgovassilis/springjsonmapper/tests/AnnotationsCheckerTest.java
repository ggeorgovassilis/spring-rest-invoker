package com.github.ggeorgovassilis.springjsonmapper.tests;

import com.github.ggeorgovassilis.springjsonmapper.services.BookService;
import com.github.ggeorgovassilis.springjsonmapper.support.BaseProxyClass;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * Verifies that annotations are correctly preserved on proxies
 * 
 * @author george georgovassilis
 *
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration("classpath:test-context-annotations.xml")
public class AnnotationsCheckerTest {

	@Resource(name = "BookService_DynamicProxy")
	protected BookService dynamicProxy;

	@Resource(name = "BookService_OpaqueProxy")
	protected BookService opaqueProxy;

	protected Method findMethodOnDynamicProxy(String name, Class<?> c, Class<?>[] args) {
		Method method = ReflectionUtils.findMethod(c, name, args);
		if (method == null) {
			Class<?>[] classes = c.getInterfaces();
			if (classes != null)
				for (Class<?> c1 : classes) {
					method = findMethodOnDynamicProxy(name, c1, args);
					if (method != null)
						break;
				}
		}
		return method;
	}

	/**
	 * Determines whether on a given method of a dynamic proxy whether there is
	 * somewhere (= class hierarchy, interfaces) annotations preserved.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testAnnotationsOnDynamicProxy() throws Exception {
		Method method = findMethodOnDynamicProxy("findBooksByTitle", dynamicProxy.getClass(),
				new Class[] { String.class });
		assertNotNull(method);
		RequestMapping annotation = AnnotationUtils.findAnnotation(method, RequestMapping.class);
		assertNotNull(annotation);
	}

	/**
	 * Determines whether a given method of an opaque proxy declares directly an
	 * annotation
	 * 
	 * @throws Exception
	 */
	@Test
	public void testAnnotationsOnOpaqueProxy() throws Exception {
		assertTrue(opaqueProxy instanceof BaseProxyClass);
		Method method = ReflectionUtils.findMethod(opaqueProxy.getClass(), "findBooksByTitle",
				new Class[] { String.class });
		assertNotNull(method);
		RequestMapping annotation = AnnotationUtils.findAnnotation(method, RequestMapping.class);
		assertNotNull(annotation);
	}
}
