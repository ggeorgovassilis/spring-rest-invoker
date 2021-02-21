package com.github.ggeorgovassilis.springjsonmapper.tests;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ContextConfiguration;

import com.github.ggeorgovassilis.springjsonmapper.jaxrs.JaxRsInvokerProxyFactoryBean;
import com.github.ggeorgovassilis.springjsonmapper.services.jaxrs.BankServiceJaxRs;
import com.github.ggeorgovassilis.springjsonmapper.services.spring.BankServiceSpring;
import com.github.ggeorgovassilis.springjsonmapper.spring.SpringRestInvokerProxyFactoryBean;

/**
 * Version is {@link BankServiceSpringTest} test with annotations instead of xml.
 * Issue #33 https://github.com/ggeorgovassilis/spring-rest-invoker/issues/33
 * 
 * @author george georgovassilis
 * 
 */

@ContextConfiguration(classes=BankServiceSpringAnnotationTest.class)
@Configuration
@PropertySource("classpath:config.properties")
public class BankServiceSpringAnnotationTest extends AbstractBankServiceTest {

	@Bean
	SpringRestInvokerProxyFactoryBean BankService() {
		SpringRestInvokerProxyFactoryBean proxyFactory = new SpringRestInvokerProxyFactoryBean();
		proxyFactory.setBaseUrl("http://localhost/bankservice");
		proxyFactory.setRemoteServiceInterfaceClass(BankServiceSpring.class);
		return proxyFactory;
	}

	@Override
	protected String getExpectedServiceName() {
		return BankServiceSpring.class.getName();
	}
}
