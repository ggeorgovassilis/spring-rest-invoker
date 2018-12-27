package com.github.ggeorgovassilis.springjsonmapper.tests;

import com.github.ggeorgovassilis.springjsonmapper.services.spring.BankServiceShortcutSpring;
import com.github.ggeorgovassilis.springjsonmapper.spring.SpringRestInvokerProxyFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = BankServiceShortcutSpringAnnotationTest.class)
@Configuration
@PropertySource("classpath:config.properties")
public class BankServiceShortcutSpringAnnotationTest extends AbstractBankServiceTest {

	@Bean
	SpringRestInvokerProxyFactoryBean BankService() {
		SpringRestInvokerProxyFactoryBean proxyFactory = new SpringRestInvokerProxyFactoryBean();
		proxyFactory.setBaseUrl("http://localhost/bankservice");
		proxyFactory.setRemoteServiceInterfaceClass(BankServiceShortcutSpring.class);
		return proxyFactory;
	}

	@Override
	protected String getExpectedServiceName() {
		return BankServiceShortcutSpring.class.getName();
	}
}
