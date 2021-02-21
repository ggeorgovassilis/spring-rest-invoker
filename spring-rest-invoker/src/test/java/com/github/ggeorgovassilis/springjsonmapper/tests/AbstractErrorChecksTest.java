package com.github.ggeorgovassilis.springjsonmapper.tests;

import com.github.ggeorgovassilis.springjsonmapper.BaseRestInvokerProxyFactoryBean;
import com.github.ggeorgovassilis.springjsonmapper.model.MappingDeclarationException;
import com.github.ggeorgovassilis.springjsonmapper.services.InterfaceWithErrors;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Test that various error checks work
 * 
 * @author George Georgovassilis
 *
 */
@ExtendWith(SpringExtension.class)
public abstract class AbstractErrorChecksTest {

	@Autowired
	BaseRestInvokerProxyFactoryBean httpProxyFactory;

	@Autowired
	InterfaceWithErrors iwe;

	@Test
	public void testAmbiguousRequestBody() {
		assertThrows(MappingDeclarationException.class, () ->
			iwe.methodWithTwoAnonymousRequestBodies(new byte[0], new byte[0])
		);
	}

	@Test
	public void testNamedAndUnnamedRequestBody() {
		assertThrows(MappingDeclarationException.class, () ->
			iwe.methodWithNamedAndAnonymousRequestBodies(new byte[0], new byte[0])
		);
	}

	@Test
	public void testIncompleteParameterAnnotations() {
		assertThrows(MappingDeclarationException.class, () ->
			iwe.methodWithIncompleteParameterAnnotations("s1", "s2")
		);
	}

	@Test
	public void testDuplicateParameterAnnotations() {
		assertThrows(MappingDeclarationException.class, () ->
			iwe.methodWithDuplicateParameterAnnotations("s1", "s2")
		);
	}

	@Test
	public void testAmbigiousRequestMethods() {
		assertThrows(MappingDeclarationException.class, () ->
			iwe.methodWithAmbiguousHttpMethod()
		);
	}
}
