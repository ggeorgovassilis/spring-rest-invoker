package com.github.ggeorgovassilis.springjsonmapper.services.spring;

import com.github.ggeorgovassilis.springjsonmapper.model.Header;
import com.github.ggeorgovassilis.springjsonmapper.services.Account;
import com.github.ggeorgovassilis.springjsonmapper.services.BankService;
import com.github.ggeorgovassilis.springjsonmapper.services.Customer;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Mapping to a hypothetical bank service REST API using Spring annotations
 *
 * @author george georgovassilis
 */
public interface BankServiceShortcutSpring extends BankService {

	@Override
	@PostMapping("/transfer")
	Account transfer(@RequestBody @RequestParam("fromAccount") Account fromAccount,
	                 @RequestBody @RequestParam("actor") Customer actor,
	                 @RequestBody @RequestParam("toAccount") Account toAccount, @RequestBody @RequestParam("amount") int amount,
	                 @RequestParam("sendConfirmationSms") boolean sendConfirmationSms);

	@Override
	@PostMapping("/verify")
	Boolean checkAccount(@RequestBody Account account);

	@Override
	@PostMapping(value = "/photo", consumes = {"image/gif", "image/jpeg", "image/png"}, produces = {"image/jpeg"})
	byte[] updatePhoto(@RequestParam("name") String name, @RequestBody byte[] photo);

	@Override
	@PostMapping("/join-accounts")
	Account joinAccounts(@RequestPart @RequestParam("account1") Account account1,
	                     @RequestPart @RequestParam("account2") Account account2);

	@Override
	@PostMapping("/authenticate")
	Customer authenticate(@RequestPart @RequestParam("name") String name,
	                      @RequestPart @RequestParam("password") String password, @CookieValue("sid") String sessionId);

	@Override
	@GetMapping("/accounts/{id}")
	Account getAccount(@PathVariable("id") int id);

	@Override
	@GetMapping("/session/check")
	boolean isSessionAlive(@Header("X-SessionId") String sid);

	@Override
	@GetMapping("/${domain}/customer/{name}")
	boolean doesCustomerExist(@PathVariable("name") String name);

	@Override
	@GetMapping(value = "/${domain}/customer/{name}", headers = {"X-header-1=value1", "X-header-2=value2"})
	boolean doesCustomerExist2(@PathVariable("name") String name);

	@Override
	@GetMapping("/accounts")
	List<Account> getAllAccounts();

}
