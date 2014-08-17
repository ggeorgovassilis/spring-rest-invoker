package com.github.ggeorgovassilis.springjsonmapper.tests;

import org.springframework.test.context.ContextConfiguration;

import com.github.ggeorgovassilis.springjsonmapper.services.spring.BankServiceSpring;
import com.github.ggeorgovassilis.springjsonmapper.spring.SpringRestInvokerProxyFactoryBean;

/**
 * Tests a more complex scenario with recorded HTTP requests and responses using the {@link SpringRestInvokerProxyFactoryBean}
 * 
 * @author george georgovassilis
 * 
 */
@ContextConfiguration("classpath:test-context-bank-spring.xml")
public class BankServiceSpringTest extends AbstractBankServiceTest{

    @Override
    protected String getExpectedServiceName() {
	return BankServiceSpring.class.getName();
    }
}
