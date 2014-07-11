package com.github.ggeorgovassilis.springjsonmapper.tests;


import org.springframework.test.context.ContextConfiguration;

/**
 * Integration test with the google books API using the {@link JaxRsAnnotationsHttpJsonInvokerFactoryProxyBean}
 * @author george georgovassilis
 */
@ContextConfiguration("classpath:test-context-googlebooks-jaxrs.xml")
public class GoogleBooksApiJaxRsTest extends AbstractGoogleBooksApiTest{

}
