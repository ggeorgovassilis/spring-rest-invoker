package com.github.ggeorgovassilis.springjsonmapper.tests;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.github.ggeorgovassilis.springjsonmapper.BaseRestInvokerProxyFactoryBean;
import com.github.ggeorgovassilis.springjsonmapper.model.MappingDeclarationException;
import com.github.ggeorgovassilis.springjsonmapper.services.InterfaceWithErrors;

/**
 * Test that various error checks work
 * 
 * @author George Georgovassilis
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
public abstract class AbstractErrorChecksTest {

	@Autowired
	BaseRestInvokerProxyFactoryBean httpProxyFactory;

	@Autowired
	InterfaceWithErrors iwe;

	@Test(expected = MappingDeclarationException.class)
	public void testAmibiguousRequestBody() {
		iwe.methodWithTwoAnonymousRequestBodies(new byte[0], new byte[0]);
	}

	@Test(expected = MappingDeclarationException.class)
	public void testNamedAndUnnamedRequestBody() {
		iwe.methodWithNamedAndAnonymousRequestBodies(new byte[0], new byte[0]);
	}

	@Test(expected = MappingDeclarationException.class)
	public void testIncompleteParameterAnnotations() {
		iwe.methodWithIncompleteParameterAnnotations("s1", "s2");
	}

	@Test(expected = MappingDeclarationException.class)
	public void testDuplicateParameterAnnotations() {
		iwe.methodWithDuplicateParameterAnnotations("s1", "s2");
	}

	@Test(expected = MappingDeclarationException.class)
	public void testAmbigiousRequestMethods() {
		iwe.methodWithAmbiguousHttpMethod();
	}
}
