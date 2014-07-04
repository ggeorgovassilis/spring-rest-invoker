package com.github.ggeorgovassilis.springjsonmapper.tests;

import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.client.RestTemplate;

import com.github.ggeorgovassilis.springjsonmapper.BaseHttpJsonInvokerFactoryProxyBean;
import com.github.ggeorgovassilis.springjsonmapper.jaxrs.BankServiceJaxRs;
import com.github.ggeorgovassilis.springjsonmapper.jaxrs.JaxRsAnnotationsHttpJsonInvokerFactoryProxyBean;
import com.github.ggeorgovassilis.springjsonmapper.support.Account;
import com.github.ggeorgovassilis.springjsonmapper.support.Customer;
import com.github.ggeorgovassilis.springjsonmapper.support.MockRequestFactory;
import com.github.ggeorgovassilis.springjsonmapper.support.MockRequestFactory.MockRequest;
import com.github.ggeorgovassilis.springjsonmapper.support.MockRequestFactory.MockResponse;

import static org.junit.Assert.*;

/**
 * Tests a more complex scenario with recorded HTTP requests and responses using the {@link JaxRsAnnotationsHttpJsonInvokerFactoryProxyBean}
 * 
 * @author george georgovassilis
 * 
 */
@RunWith(value = SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:test-context-bank.xml")
public class BankServiceJaxRsTest {

    @Autowired
    BankServiceJaxRs bankService;

    @Resource(name="&RemoteBankServiceJaxRs")
    BaseHttpJsonInvokerFactoryProxyBean httpProxyFactory;

    MockRequestFactory requestFactory;
    
    @Before
    public void setup() {
	requestFactory = new MockRequestFactory();
	RestTemplate restTemplate = new RestTemplate(requestFactory);
	httpProxyFactory.setRestTemplate(restTemplate);
	requestFactory.createResponse();
    }

    byte[] get(String classpathResource) throws Exception{
	ClassPathResource r = new ClassPathResource(classpathResource);
	InputStream in = r.getInputStream();
	byte[] b = FileCopyUtils.copyToByteArray(in);
	in.close();
	return b;
    }

    String sget(String classpathResource) throws Exception{
	return new String(get(classpathResource), "UTF-8");
    }
    
    @Test
    public void testBankService_transfer() throws Exception{
	// setup test
	Customer customer1 = new Customer();
	customer1.setName("Customer 1");

	Customer customer2 = new Customer();
	customer2.setName("Customer 2");

	Account account1 = new Account();
	account1.setAccountNumber("account 1");
	account1.setBalance(1000);
	account1.setOwner(customer1);

	Account account2 = new Account();
	account2.setAccountNumber("account 2");
	account2.setBalance(0);
	account2.setOwner(customer2);

	MockResponse response = requestFactory.createResponse();
	response.setBody(get("recordedmessages/transfer_response.txt"));

	// execute test
	Account result = bankService.transfer(account1, customer1, account2, 1,
		true);

	// verify results
	assertEquals("account 1", result.getAccountNumber());
	assertEquals(999, result.getBalance());
	assertEquals("Customer 1", result.getOwner().getName());

	// verify http request
	MockRequest request = requestFactory.getLastRequest();
	assertEquals(
		sget("recordedmessages/transfer_request.txt"),
		request.serializeToString());

    }

    @Test
    public void testBankService_checkAccount() throws Exception{
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

	// verify http request
	MockRequest request = requestFactory.getLastRequest();
	assertEquals(sget("recordedmessages/checkaccount_request.txt"),
		request.serializeToString());
    }

    /**
     * Tests http headers
     */
    @Test
    public void testBankService_photo() throws Exception{
	// setup test
	byte[] photo = { 1, 2, 3, 4, 5 };
	MockResponse response = requestFactory.createResponse();
	response.setBody(photo);

	// execute test
	byte[] result = bankService.updatePhoto("customer 1", photo);
	assertArrayEquals(photo, result);
	// verify http request
	MockRequest request = requestFactory.getLastRequest();
	String expectedRequest = sget("recordedmessages/photo_request.txt") + new String(photo);
	assertEquals(expectedRequest, request.serializeToString());
    }
    
    /**
     * Tests @RequestPart implementation
     */
    @Test
    @Ignore //RequestPart for JaxRs not implemented
    public void testBankService_joinAccounts() throws Exception{
	//setup test
	Customer customer1 = new Customer();
	customer1.setName("Customer 1");
	Account account1 = new Account();
	account1.setAccountNumber("account 1");
	account1.setBalance(1000);
	account1.setOwner(customer1);

	Customer customer2 = new Customer();
	customer2.setName("Customer 2");
	Account account2 = new Account();
	account2.setAccountNumber("account 2");
	account2.setBalance(100);
	account2.setOwner(customer2);

	MockResponse response = requestFactory.createResponse();
	response.setBody(get("recordedmessages/joinaccounts_response.txt"));

	//execute test
	Account joinedAccount = bankService.joinAccounts(account1, account2);
	assertEquals("account 1+2", joinedAccount.getAccountNumber());
	assertEquals(1100, joinedAccount.getBalance());
	assertEquals("Customer 1", joinedAccount.getOwner().getName());

	// verify http request
	MockRequest request = requestFactory.getLastRequest();
	String httpString = request.serializeToString();
	// find part names
	Matcher matcher = Pattern.compile("boundary=(.*)").matcher(httpString);
	assertTrue(matcher.find());
	String b1 = matcher.group(1);

	httpString = httpString.replace(b1,"BOUNDARY");
	//The expected response contains a mix of line delimiters: some are \n and others are \r\n.
	//Someone will sooner or later trip over it, so we're encoding the \r as ~ and replacing it at runtime.
	String expected = sget("recordedmessages/joinaccounts_request.txt").replace('~', '\r');

	assertEquals(expected, httpString);
    }

    /**
     * Tests that hashcode, equals and toString are not mapped to a remote
     * service
     */
    @Test
    public void testNonMappedMethods() {
	assertTrue(bankService.equals(bankService));
	assertFalse(bankService.equals(this));
	assertEquals(bankService.hashCode(), bankService.hashCode());
	assertTrue(bankService.toString().contains("BankServiceJaxRs"));
    }
}
