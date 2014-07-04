package com.github.ggeorgovassilis.springjsonmapper.spring;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.github.ggeorgovassilis.springjsonmapper.BaseHttpJsonInvokerFactoryProxyBean;

/**
 * Test that various error checks work
 * @author George Georgovassilis
 *
 */
@RunWith(value = SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:test-context-errorchecks.xml")
public class ErrorChecksTest {

    @Autowired
    BaseHttpJsonInvokerFactoryProxyBean httpProxyFactory;

    @Autowired
    InterfaceWithErrors iwe;
    
    @Test(expected=IllegalArgumentException.class)
    public void testAmibiguousRequestBody() {
	iwe.methodWithTwoAnonymousRequestBodies(new byte[0], new byte[0]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testNamedAndUnnamedRequestBody() {
	iwe.methodWithNamedAndAnonymousRequestBodies(new byte[0], new byte[0]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIncompleteParameterAnnotations() {
	iwe.methodWithIncompleteParameterAnnotations("s1", "s2");
    }
}
