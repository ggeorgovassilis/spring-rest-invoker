package com.github.ggeorgovassilis.springjsonmapper.bank;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestOperations;

import com.github.ggeorgovassilis.springjsonmapper.HttpJsonInvokerFactoryProxyBean;

import static org.mockito.Mockito.*;

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

    RestOperations restTemplate;

    @Before
    public void setup() {
	restTemplate = mock(RestOperations.class);
	httpProxyFactory.setRestTemplate(restTemplate);
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
	
	// expected values
	
	Map<String, Object> expectedDataObjects = new HashMap<>();
	expectedDataObjects.put("fromAccount", account1);
	expectedDataObjects.put("toAccount", account2);
	expectedDataObjects.put("actor", customer1);
	expectedDataObjects.put("amount", 1);

	Map<String, Object> expectedParameters = new HashMap<>();
	expectedParameters.put("sendConfirmationSms", true);
	
	bankService.transfer(account1, customer1, account2, 1, true);

	verify(restTemplate).postForObject(eq("http://localhost/bankservice/transfer?sendConfirmationSms={sendConfirmationSms}"), eq(expectedDataObjects), eq(Account.class),
		eq(expectedParameters));
    }


    @Test
    public void testBankService_verify() {
	// setup test
	Customer customer1 = new Customer();
	customer1.setName("Customer 1");

	Account account1 = new Account();
	account1.setAccountNumber("account 1");
	account1.setBalance(1000);
	account1.setOwner(customer1);

	// expected values
	Map<String, Object> expectedParameters = new HashMap<>();
	
	bankService.checkAccount(account1);

	verify(restTemplate).postForObject(eq("http://localhost/bankservice/verify"), eq(account1), eq(Boolean.class),
		eq(expectedParameters));


    }
}
