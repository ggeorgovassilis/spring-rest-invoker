package com.github.ggeorgovassilis.springjsonmapper.bank;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import com.github.ggeorgovassilis.springjsonmapper.HttpJsonInvokerFactoryProxyBean;
import com.github.ggeorgovassilis.springjsonmapper.support.MockRequestFactory;
import com.github.ggeorgovassilis.springjsonmapper.support.MockRequestFactory.MockRequest;
import com.github.ggeorgovassilis.springjsonmapper.support.MockRequestFactory.MockResponse;

import static org.junit.Assert.*;

/**
 * Tests a more complex scenario with a mocked HTTP transfer mechanism
 * 
 * @author george georgovassilis
 * 
 */
@RunWith(value = SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:test-context-bank.xml")
public class BankServiceTest {

    @Autowired
    BankService bankService;

    @Autowired
    HttpJsonInvokerFactoryProxyBean httpProxyFactory;

    MockRequestFactory requestFactory;

    @Before
    public void setup() {
	requestFactory = new MockRequestFactory();
	RestTemplate restTemplate = new RestTemplate(requestFactory);
	httpProxyFactory.setRestTemplate(restTemplate);
	requestFactory.createResponse();
    }

    @Test
    public void testBankService_transfer() {
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
	response.setBody("{\"accountNumber\":\"account 1\",\"balance\":999,\"owner\":{\"name\":\"Customer 1\"}}"
		.getBytes());

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
		"POST http://localhost/bankservice/transfer?sendConfirmationSms=true\nAccept=application/json, application/*+json\nContent-Type=application/json;charset=UTF-8\n\n{\"amount\":1,\"toAccount\":{\"accountNumber\":\"account 2\",\"balance\":0,\"owner\":{\"name\":\"Customer 2\"}},\"fromAccount\":{\"accountNumber\":\"account 1\",\"balance\":1000,\"owner\":{\"name\":\"Customer 1\"}},\"actor\":{\"name\":\"Customer 1\"}}",
		request.serializeToString());

    }

    @Test
    public void testBankService_checkAccount() {
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
	assertEquals(
		"POST http://localhost/bankservice/verify\nAccept=application/json, application/*+json\nContent-Type=application/json;charset=UTF-8\n\n{\"accountNumber\":\"account 1\",\"balance\":1000,\"owner\":{\"name\":\"Customer 1\"}}",
		request.serializeToString());
    }

    /**
     * Tests http headers
     */
    @Test
    public void testBankService_photo() {
	// setup test
	byte[] photo = { 1, 2, 3, 4, 5 };
	MockResponse response = requestFactory.createResponse();
	response.setBody(photo);

	// execute test
	byte[] result = bankService.updatePhoto("customer 1", photo);
	assertArrayEquals(photo, result);
	// verify http request
	MockRequest request = requestFactory.getLastRequest();
	assertEquals(
		"POST http://localhost/bankservice/photo?name=customer%201\nAccept=image/jpeg\nContent-Length=5\nContent-Type=image/gif,image/jpeg,image/png\n\n"
			+ new String(photo), request.serializeToString());
    }
    
    /**
     * Tests @RequestPart implementation
     */
    @Test
    public void testBankService_joinAccounts() {
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
	response.setBody("{\"accountNumber\":\"account 1+2\",\"balance\":1100,\"owner\":{\"name\":\"Customer 1\"}}".getBytes());

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
	String expected = "POST http://localhost/bankservice/join-accounts\nAccept=application/json, application/*+json\nContent-Type=multipart/form-data;boundary=BOUNDARY\n\n--BOUNDARY\r\nContent-Disposition: form-data; name=\"account1\"\r\nContent-Type: application/json;charset=UTF-8\r\n\r\n{\"accountNumber\":\"account 1\",\"balance\":1000,\"owner\":{\"name\":\"Customer 1\"}}\r\n--BOUNDARY\r\nContent-Disposition: form-data; name=\"account2\"\r\nContent-Type: application/json;charset=UTF-8\r\n\r\n{\"accountNumber\":\"account 2\",\"balance\":100,\"owner\":{\"name\":\"Customer 2\"}}\r\n--BOUNDARY--\r\n";

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
	assertTrue(bankService.toString().contains("BankService"));
    }
}
