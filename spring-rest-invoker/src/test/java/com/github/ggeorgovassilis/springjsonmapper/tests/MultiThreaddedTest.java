package com.github.ggeorgovassilis.springjsonmapper.tests;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import com.github.ggeorgovassilis.springjsonmapper.BaseHttpJsonInvokerFactoryProxyBean;
import com.github.ggeorgovassilis.springjsonmapper.services.Account;
import com.github.ggeorgovassilis.springjsonmapper.services.BankService;
import com.github.ggeorgovassilis.springjsonmapper.services.Customer;
import com.github.ggeorgovassilis.springjsonmapper.support.MockRequestFactory;
import com.github.ggeorgovassilis.springjsonmapper.support.MockRequestFactory.MockResponse;

@ContextConfiguration("classpath:test-context-bank-spring.xml")
@RunWith(value = SpringJUnit4ClassRunner.class)
public class MultiThreaddedTest {
    
    final long TEST_DURATION_MS = 2000;
    final int THREADS = 4;

    @Autowired
    protected BankService bankService;

    @Resource(name = "&RemoteBankService")
    protected BaseHttpJsonInvokerFactoryProxyBean httpProxyFactory;

    protected MockRequestFactory requestFactory;

    @Before
    public void setup() {
	requestFactory = new MockRequestFactory();
	RestTemplate restTemplate = new RestTemplate(requestFactory);
	httpProxyFactory.setRestTemplate(restTemplate);
	requestFactory.createResponse();
    }

    void executeTest() throws Exception {
	
	// setup test
	Customer customer1 = new Customer();
	customer1.setName("Customer 1");

	Account account1 = new Account();
	account1.setAccountNumber("account 1");
	account1.setBalance(1000);
	account1.setOwner(customer1);

	MockResponse response = requestFactory.createResponse();
	response.setBody("true".getBytes());

	// execute test
	boolean result = bankService.checkAccount(account1);

	// verify results
	assertTrue(result);
    }
    
    @Test
    public void testMultithreaddedInvocation() throws Exception{
	ExecutorService executorService = Executors.newFixedThreadPool(THREADS);
	List<Future<Void>> results = new ArrayList<Future<Void>>();
	long start = System.currentTimeMillis();
	while (start+TEST_DURATION_MS > System.currentTimeMillis()){
	    Future<Void> f = executorService.submit(new Callable<Void>() {

		@Override
		public Void call() throws Exception {
		    executeTest();
		    return null;
		}
	    });
	    results.add(f);
	    if (results.size()>THREADS){
		for (Future<Void> f1:results){
		    f1.get();
		}
		results.clear();
	    }
	}
	executorService.shutdown();
    }

}
