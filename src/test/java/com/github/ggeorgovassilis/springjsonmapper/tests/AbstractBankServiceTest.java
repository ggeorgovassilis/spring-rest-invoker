package com.github.ggeorgovassilis.springjsonmapper.tests;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import com.github.ggeorgovassilis.springjsonmapper.BaseRestInvokerProxyFactoryBean;
import com.github.ggeorgovassilis.springjsonmapper.services.Account;
import com.github.ggeorgovassilis.springjsonmapper.services.BankService;
import com.github.ggeorgovassilis.springjsonmapper.services.Customer;
import com.github.ggeorgovassilis.springjsonmapper.spring.SpringRestInvokerProxyFactoryBean;
import com.github.ggeorgovassilis.springjsonmapper.support.MockRequestFactory;
import com.github.ggeorgovassilis.springjsonmapper.support.MockRequestFactory.MockRequest;
import com.github.ggeorgovassilis.springjsonmapper.support.MockRequestFactory.MockResponse;

import static org.junit.Assert.*;
import static com.github.ggeorgovassilis.springjsonmapper.support.Utils.*;
import static com.github.ggeorgovassilis.springjsonmapper.tests.Factory.*;

/**
 * Tests a more complex scenario with recorded HTTP requests and responses using
 * the {@link SpringRestInvokerProxyFactoryBean}
 * 
 * @author george georgovassilis
 * 
 */
@RunWith(value = SpringJUnit4ClassRunner.class)
public abstract class AbstractBankServiceTest {

	@Autowired
	protected BankService bankService;

	@Resource(name = "&RemoteBankService")
	protected BaseRestInvokerProxyFactoryBean httpProxyFactory;

	protected MockRequestFactory requestFactory;

	protected MockResponse response(String classPathResource) throws Exception {
		MockResponse response = requestFactory.createResponse();
		response.setBody(get(classPathResource));
		return response;
	}

	@Before
	public void setup() {
		requestFactory = new MockRequestFactory();
		RestTemplate restTemplate = new RestTemplate(requestFactory);
		httpProxyFactory.setRestTemplate(restTemplate);
		requestFactory.createResponse();
	}

	@Test
	public void testRequestBody() throws Exception {
		// setup test
		Customer customer1 = customer("Customer 1");
		Customer customer2 = customer("Customer 2");
		Account account1 = account("account 1", 1000, customer1);
		Account account2 = account("account 2", 0, customer2);

		response("recordedmessages/transfer_response.txt");

		// execute test
		Account result = bankService.transfer(account1, customer1, account2, 1, true);

		// verify results
		assertEquals("account 1", result.getAccountNumber());
		assertEquals(999, result.getBalance());
		assertEquals("Customer 1", result.getOwner().getName());

		// verify http request
		MockRequest request = requestFactory.getLastRequest();
		assertEquals(sget("recordedmessages/transfer_request.txt"), request.serializeToString());

	}

	@Test
	public void testPost() throws Exception {
		// setup test
		Customer customer1 = customer("Customer 1");
		Account account1 = account("account 1", 1000, customer1);

		MockResponse response = requestFactory.createResponse();
		response.setBody("true".getBytes());

		// execute test
		boolean result = bankService.checkAccount(account1);

		// verify results
		assertTrue(result);

		// verify http request
		MockRequest request = requestFactory.getLastRequest();
		assertEquals(sget("recordedmessages/checkaccount_request.txt"), request.serializeToString());
	}

	/**
	 * Tests http headers
	 */
	@Test
	public void testHeaders() throws Exception {
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
	public void testRequestParts() throws Exception {
		// setup test
		Customer customer1 = customer("Customer 1");
		Customer customer2 = customer("Customer 2");
		Account account1 = account("account 1", 1000, customer1);
		Account account2 = account("account 2", 100, customer2);

		response("recordedmessages/joinaccounts_response.txt");

		// execute test
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

		httpString = httpString.replace(b1, "BOUNDARY");
		// The expected response contains a mix of line delimiters: some are \n
		// and others are \r\n.
		// Someone will sooner or later trip over it, so we're encoding the \r
		// as ~ and replacing it at runtime.
		String expected = sget("recordedmessages/joinaccounts_request.txt").replace('~', '\r');

		assertEquals(expected, httpString);
	}

	protected abstract String getExpectedServiceName();

	/**
	 * Tests that hashcode, equals and toString are not mapped to a remote service
	 */
	@Test
	public void testNonMappedMethods() {
		assertTrue(bankService.equals(bankService));
		assertFalse(bankService.equals(this));
		assertEquals(bankService.hashCode(), bankService.hashCode());
		assertTrue(bankService.toString().contains(getExpectedServiceName()));
	}

	/**
	 * Test mapping arguments to cookies
	 */
	@Test
	public void testCookieParams() throws Exception {
		// setup test
		response("recordedmessages/authenticate_response.txt");

		// execute test
		Customer customer = bankService.authenticate("Customer 1", "password", "1234");
		assertEquals("Customer 1", customer.getName());
		MockRequest request = requestFactory.getLastRequest();
		assertEquals(sget("recordedmessages/authenticate_request.txt"), request.serializeToString());
	}

	/**
	 * Test a simple http GET
	 */
	@Test
	public void testPathVariables() throws Exception {
		// setup test
		response("recordedmessages/getaccount_response.txt");

		// execute test
		Account account = bankService.getAccount(1234);
		assertEquals("1234", account.getAccountNumber());
		assertEquals("Customer 1", account.getOwner().getName());

		// validate request
		MockRequest request = requestFactory.getLastRequest();
		assertEquals(sget("recordedmessages/getaccount_request.txt"), request.serializeToString());

	}

	/**
	 * Tests that parameters can be passed as headers
	 * 
	 * @throws Exception
	 */
	@Test
	public void testArgumentToHeaders() throws Exception {
		// setup test
		response("recordedmessages/issessionalive_response.txt");

		// execute test
		boolean value = bankService.isSessionAlive("56789");
		assertEquals(true, value);

		// validate request
		MockRequest request = requestFactory.getLastRequest();
		assertEquals(sget("recordedmessages/issessionalive_request.txt"), request.serializeToString());

	}

	/**
	 * Checks whether SPEL is resolved properly
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSPEL() throws Exception {
		// setup test
		response("recordedmessages/doescustomerexist_response.txt");

		// execute test
		boolean value = bankService.doesCustomerExist("56789");
		assertEquals(true, value);

		// validate request
		MockRequest request = requestFactory.getLastRequest();
		assertEquals(sget("recordedmessages/doescustomerexist_request.txt"), request.serializeToString());

	}

	/**
	 * Checks Parameterized response type are handled properly
	 * 
	 * @throws Exception
	 */
	@Test
	public void testParameterizedResponseType() throws Exception {
		// setup test
		response("recordedmessages/getallaccounts_response.txt");

		// execute test
		List<Account> results = bankService.getAllAccounts();
		assertEquals(2, results.size());
		assertTrue("Item is expected to be an instance of Account", results.get(0) instanceof Account);
		assertEquals("41", results.get(0).getAccountNumber());

		// validate request
		MockRequest request = requestFactory.getLastRequest();
		assertEquals(sget("recordedmessages/getallaccounts_request.txt"), request.serializeToString());

	}
}
