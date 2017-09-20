package com.github.ggeorgovassilis.springjsonmapper.integrationtests;

import org.springframework.test.context.ContextConfiguration;

/**
 * Integration test with the google books API using the
 * {@link SpringRestInvokerProxyFactoryBean}
 * 
 * @author george georgovassilis
 */
@ContextConfiguration("classpath:test-context-googlebooks-spring.xml")
public class GoogleBooksApiSpringTest extends AbstractGoogleBooksApiTest {
}