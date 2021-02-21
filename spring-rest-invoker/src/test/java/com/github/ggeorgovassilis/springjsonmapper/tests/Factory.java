package com.github.ggeorgovassilis.springjsonmapper.tests;

import com.github.ggeorgovassilis.springjsonmapper.services.Account;
import com.github.ggeorgovassilis.springjsonmapper.services.Customer;

/**
 * Helper class for constructing test objects
 * 
 * @author george georgovassilis
 *
 */
public class Factory {

	public static Account account(String accountNumber, int balance, Customer owner) {
		Account a = new Account();
		a.setAccountNumber(accountNumber);
		a.setBalance(balance);
		a.setOwner(owner);
		return a;
	}

	public static Customer customer(String name) {
		Customer c = new Customer();
		c.setName(name);
		return c;
	}
}
