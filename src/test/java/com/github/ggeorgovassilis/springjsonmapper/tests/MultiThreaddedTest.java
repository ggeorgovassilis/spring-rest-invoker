package com.github.ggeorgovassilis.springjsonmapper.tests;

import com.github.ggeorgovassilis.springjsonmapper.services.Account;
import com.github.ggeorgovassilis.springjsonmapper.services.BankService;
import com.github.ggeorgovassilis.springjsonmapper.services.Customer;
import com.github.ggeorgovassilis.springjsonmapper.services.spring.BankServiceSpring;
import com.github.ggeorgovassilis.springjsonmapper.spring.SpringRestInvokerProxyFactoryBean;
import com.github.ggeorgovassilis.springjsonmapper.support.MockRequestFactory;
import com.github.ggeorgovassilis.springjsonmapper.support.MockRequestFactory.MockResponse;
import com.github.ggeorgovassilis.springjsonmapper.utils.CglibProxyFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static com.github.ggeorgovassilis.springjsonmapper.tests.Factory.account;
import static com.github.ggeorgovassilis.springjsonmapper.tests.Factory.customer;
import static org.junit.Assert.assertTrue;

/**
 * Runs the entire chain through concurrent invocations and asserts that nothing
 * breaks
 *
 * @author george georgovassilis
 */
//@ContextConfiguration("classpath:test-context-bank-spring.xml")
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
@RunWith(value = SpringJUnit4ClassRunner.class)
public class MultiThreaddedTest {

	final long TEST_DURATION_MS = 3000;
	final int THREADS = 4;

	@Configuration
	@PropertySource("classpath:config.properties")
	static class ContextConfiguration {

		@Bean
		public SpringRestInvokerProxyFactoryBean normalProxyFactoryBean() {

			SpringRestInvokerProxyFactoryBean factory = new SpringRestInvokerProxyFactoryBean();
			factory.setBaseUrl("http://localhost/bankservice");
			factory.setRemoteServiceInterfaceClass(BankServiceSpring.class);
			return factory;
		}

		@Bean
		public BankService bankService() {

			try {
				return (BankService) normalProxyFactoryBean().getObject();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		@Bean
		public SpringRestInvokerProxyFactoryBean opaqueProxyFactoryBean() {

			SpringRestInvokerProxyFactoryBean factory = new SpringRestInvokerProxyFactoryBean();
			factory.setBaseUrl("http://localhost/bankservice");
			factory.setRemoteServiceInterfaceClass(BankServiceSpring.class);
			factory.setProxyFactory(new CglibProxyFactory());
			return factory;
		}

		@Bean
		public BankService bankServiceOpaque() {

			// set properties, etc.
			try {
				return (BankService) opaqueProxyFactoryBean().getObject();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	@Autowired
	protected BankService bankService;

	@Autowired
	protected BankService bankServiceOpaque;

	@Autowired
	protected List<SpringRestInvokerProxyFactoryBean> factories;

	protected MockRequestFactory requestFactory;

	@Before
	public void setup() {
		requestFactory = new MockRequestFactory();
		RestTemplate restTemplate = new RestTemplate(requestFactory);
		for (SpringRestInvokerProxyFactoryBean factory : factories) {
			factory.setRestTemplate(restTemplate);
		}
		requestFactory.createResponse();
	}

	void executeTest(BankService bankService) {

		// setup test
		Customer customer1 = customer("Customer 1");
		Account account1 = account("account 1", 1000, customer1);

		MockResponse response = requestFactory.createResponse();
		response.setBody("true".getBytes());

		// execute test
		boolean result = bankService.checkAccount(account1);

		// verify results
		assertTrue(result);
	}

	void runMultiThreaddedTest(final BankService service) throws Exception {
		ExecutorService executorService = Executors.newFixedThreadPool(THREADS);
		List<Future<Void>> results = new ArrayList<Future<Void>>();
		long start = System.currentTimeMillis();
		while (start + TEST_DURATION_MS > System.currentTimeMillis()) {
			Future<Void> f = executorService.submit(new Callable<Void>() {

				@Override
				public Void call() throws Exception {
					executeTest(service);
					return null;
				}
			});
			results.add(f);
			if (results.size() > THREADS) {
				for (Future<Void> f1 : results) {
					f1.get();
				}
				results.clear();
			}
		}
		executorService.shutdown();
	}

	@Test
	public void testMultithreaddedInvocation() throws Exception {
		runMultiThreaddedTest(bankService);
	}

	@Test
	public void testMultithreaddedInvocationOnOpaqueService() throws Exception {
		runMultiThreaddedTest(bankServiceOpaque);
	}

}
