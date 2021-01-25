package com.github.ggeorgovassilis.springjsonmapper.tests;

import com.github.ggeorgovassilis.springjsonmapper.BaseRestInvokerProxyFactoryBean;
import com.github.ggeorgovassilis.springjsonmapper.services.Account;
import com.github.ggeorgovassilis.springjsonmapper.services.BankService;
import com.github.ggeorgovassilis.springjsonmapper.services.Customer;
import com.github.ggeorgovassilis.springjsonmapper.support.MockRequestFactory;
import com.github.ggeorgovassilis.springjsonmapper.support.MockRequestFactory.MockResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static com.github.ggeorgovassilis.springjsonmapper.tests.Factory.account;
import static com.github.ggeorgovassilis.springjsonmapper.tests.Factory.customer;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Runs the entire chain through concurrent invocations and asserts that nothing
 * breaks
 * 
 * @author george georgovassilis
 *
 */
@ContextConfiguration("classpath:test-context-bank-spring.xml")
@ExtendWith(SpringExtension.class)
public class MultiThreaddedTest {

	final long TEST_DURATION_MS = 3000;
	final int THREADS = 4;

	@Resource(name="BankService")
	protected BankService bankService;

	@Resource(name="BankServiceOpaque")
	protected BankService bankServiceOpaque;

	@Resource(name = "&BankService")
	protected BaseRestInvokerProxyFactoryBean httpProxyFactory;

	@Resource(name = "&BankServiceOpaque")
	protected BaseRestInvokerProxyFactoryBean opaqueHttpProxyFactory;

	protected MockRequestFactory requestFactory;

	@BeforeEach
	public void setup() {
		requestFactory = new MockRequestFactory();
		RestTemplate restTemplate = new RestTemplate(requestFactory);
		httpProxyFactory.setRestTemplate(restTemplate);
		opaqueHttpProxyFactory.setRestTemplate(restTemplate);
		requestFactory.createResponse();
	}

	void executeTest(BankService bankService) throws Exception {

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
	
	void runMultiThreaddedTest(final BankService service) throws Exception{
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
