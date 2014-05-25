package com.github.ggeorgovassilis.springjsonmapper.bank;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;

/**
 * Mapping to a hypothetical bank service REST API
 * @author george georgovassilis
 *
 */
public interface BankService {

	@RequestMapping(value = "/transfer", method = RequestMethod.POST)
	Account transfer(@RequestBody @RequestParam("fromAccount") Account fromAccount, @RequestBody @RequestParam("actor") Customer actor,
			@RequestBody @RequestParam("toAccount") Account toAccount, @RequestBody @RequestParam("amount") int amount,
			@RequestParam("sendConfirmationSms") boolean sendConfirmationSms);

	@RequestMapping(value = "/verify", method = RequestMethod.POST)
	Boolean checkAccount(@RequestBody Account account);

	@RequestMapping(value = "/photo", method = RequestMethod.POST, consumes = { "image/gif","image/jpeg","image/png" }, produces = { "image/jpeg"})
	byte[] updatePhoto(@RequestParam("name") String name, @RequestBody byte[] photo);

	@RequestMapping(value = "/join-accounts", method = RequestMethod.POST)
	Account joinAccounts(@RequestPart @RequestParam("account1") Account account1, @RequestPart @RequestParam("account2") Account account2);
	
}
