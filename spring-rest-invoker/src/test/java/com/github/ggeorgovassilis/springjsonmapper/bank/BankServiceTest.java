package com.github.ggeorgovassilis.springjsonmapper.bank;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestOperations;

import com.github.ggeorgovassilis.springjsonmapper.HttpJsonInvokerFactoryProxyBean;

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
	
	final MultiValueMap<String, String> noHeaders = new LinkedMultiValueMap<>();

	@Before
	public void setup() {
		restTemplate = mock(RestOperations.class);
		httpProxyFactory.setRestTemplate(restTemplate);
	}

	public HttpEntity<?> httpEntityMatcher(final Object content, final MultiValueMap<String, String> headers) {
		return argThat(new ArgumentMatcher<HttpEntity<Object>>() {

			@Override
			public boolean matches(Object argument) {
				HttpEntity<?> e = (HttpEntity<?>) argument;
				boolean c = e.getBody().equals(content);
				c=c&headers.size()==e.getHeaders().size();
				for (String h:headers.keySet()) {
					List<String> expectedValues = headers.get(h);
					List<String> values = e.getHeaders().get(h);
					c=c&expectedValues.size()==values.size();
					for (String v:expectedValues) {
						c&=values.contains(v);
					}
				}
				return c;
			}
		});
	}

	@SuppressWarnings("unchecked")
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

		final MultiValueMap<String, Object> expectedDataObjects = new LinkedMultiValueMap<>();
		expectedDataObjects.add("fromAccount", account1);
		expectedDataObjects.add("toAccount", account2);
		expectedDataObjects.add("actor", customer1);
		expectedDataObjects.add("amount", 1);

		Map<String, Object> expectedParameters = new HashMap<>();
		expectedParameters.put("sendConfirmationSms", true);
		ResponseEntity<Account> responeEntity = new ResponseEntity<>(HttpStatus.OK);

		when(restTemplate.exchange(any(String.class), any(HttpMethod.class), any(HttpEntity.class), eq(Account.class), any(Map.class)))
				.thenReturn(responeEntity);

		// rest.exchange(url, map(httpMethod), requestEntity, returnType,
		// parameters);

		bankService.transfer(account1, customer1, account2, 1, true);

		verify(restTemplate).exchange(eq("http://localhost/bankservice/transfer?sendConfirmationSms={sendConfirmationSms}"),
				eq(HttpMethod.POST), httpEntityMatcher(expectedDataObjects, noHeaders), eq(Account.class), eq(expectedParameters));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testBankService_verify() {
		// setup test
		Customer customer1 = new Customer();
		customer1.setName("Customer 1");

		Account account1 = new Account();
		account1.setAccountNumber("account 1");
		account1.setBalance(1000);
		account1.setOwner(customer1);

		ResponseEntity<Account> responeEntity = new ResponseEntity<>(HttpStatus.OK);

		// expected values
		Map<String, Object> expectedParameters = new HashMap<>();

		when(restTemplate.exchange(any(String.class), any(HttpMethod.class), any(HttpEntity.class), eq(Boolean.class), any(Map.class)))
				.thenReturn(responeEntity);

		bankService.checkAccount(account1);

		verify(restTemplate).exchange(eq("http://localhost/bankservice/verify"), eq(HttpMethod.POST), httpEntityMatcher(account1, noHeaders),
				eq(Boolean.class), eq(expectedParameters));

	}

	@SuppressWarnings("unchecked")
	@Test
	public void testBankService_photo() {
		// setup test
		byte[] photo = {1,2,3,4,5};
		ResponseEntity<Account> responeEntity = new ResponseEntity<>(HttpStatus.OK);

		// expected values
		Map<String, Object> expectedParameters = new HashMap<>();
		expectedParameters.put("name", "customer 1");

		when(restTemplate.exchange(any(String.class), any(HttpMethod.class), any(HttpEntity.class), eq(byte[].class), any(Map.class)))
				.thenReturn(responeEntity);

		byte[] result = bankService.updatePhoto("customer 1", photo);
		MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
		headers.put("Accept", Arrays.asList("image/jpeg"));
		headers.put("Content-Type", Arrays.asList("image/gif","image/jpeg","image/png"));

		verify(restTemplate).exchange(eq("http://localhost/bankservice/photo?name={name}"), eq(HttpMethod.POST), httpEntityMatcher(photo, headers),
				eq(byte[].class), eq(expectedParameters));

	}
}
