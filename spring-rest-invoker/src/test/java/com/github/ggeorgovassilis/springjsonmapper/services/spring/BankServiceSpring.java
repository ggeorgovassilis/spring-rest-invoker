package com.github.ggeorgovassilis.springjsonmapper.services.spring;

import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;

import com.github.ggeorgovassilis.springjsonmapper.model.Header;
import com.github.ggeorgovassilis.springjsonmapper.services.Account;
import com.github.ggeorgovassilis.springjsonmapper.services.BankService;
import com.github.ggeorgovassilis.springjsonmapper.services.Customer;

/**
 * Mapping to a hypothetical bank service REST API using Spring annotations
 * @author george georgovassilis
 *
 */
public interface BankServiceSpring extends BankService{

    	@Override
	@RequestMapping(value = "/transfer", method = RequestMethod.POST)
	Account transfer(@RequestBody @RequestParam("fromAccount") Account fromAccount, @RequestBody @RequestParam("actor") Customer actor,
			@RequestBody @RequestParam("toAccount") Account toAccount, @RequestBody @RequestParam("amount") int amount,
			@RequestParam("sendConfirmationSms") boolean sendConfirmationSms);

    	@Override
	@RequestMapping(value = "/verify", method = RequestMethod.POST)
	Boolean checkAccount(@RequestBody Account account);

    	@Override
	@RequestMapping(value = "/photo", method = RequestMethod.POST, consumes = { "image/gif","image/jpeg","image/png" }, produces = { "image/jpeg"})
	byte[] updatePhoto(@RequestParam("name") String name, @RequestBody byte[] photo);

    	@Override
	@RequestMapping(value = "/join-accounts", method = RequestMethod.POST)
	Account joinAccounts(@RequestPart @RequestParam("account1") Account account1, @RequestPart @RequestParam("account2") Account account2);
	
    	@Override
	@RequestMapping(value = "/authenticate", method = RequestMethod.POST)
	Customer authenticate(@RequestPart @RequestParam("name") String name, @RequestPart @RequestParam("password") String password, @CookieValue("sid") String sessionId);

    	@Override
	@RequestMapping(value = "/accounts/{id}")
    	Account getAccount(@PathVariable("id") int id);
    	
    	@Override
	@RequestMapping(value = "/session/check")
    	boolean isSessionAlive(@Header(	"X-SessionId") String sid);
}
