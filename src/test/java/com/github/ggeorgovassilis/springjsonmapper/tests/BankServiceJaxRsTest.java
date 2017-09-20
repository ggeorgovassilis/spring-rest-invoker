package com.github.ggeorgovassilis.springjsonmapper.tests;

import org.springframework.test.context.ContextConfiguration;

import com.github.ggeorgovassilis.springjsonmapper.services.jaxrs.BankServiceJaxRs;

/**
 * Tests a more complex scenario with recorded HTTP requests and responses using
 * the {@link JaxRsInvokerProxyFactoryBean}
 * 
 * @author george georgovassilis
 * 
 */
@ContextConfiguration("classpath:test-context-bank-jaxrs.xml")
public class BankServiceJaxRsTest extends AbstractBankServiceTest {

	@Override
	protected String getExpectedServiceName() {
		return BankServiceJaxRs.class.getName();
	}

}
