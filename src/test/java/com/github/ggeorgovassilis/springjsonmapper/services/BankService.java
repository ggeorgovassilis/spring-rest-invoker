package com.github.ggeorgovassilis.springjsonmapper.services;

import com.github.ggeorgovassilis.springjsonmapper.services.Account;
import com.github.ggeorgovassilis.springjsonmapper.services.Customer;

import java.util.List;

/**
 * Functional interface for a bank service. Tests will extend this interface and
 * redefine methods with their respective framework annotations. This functional
 * interface is kept clean of annotations so that the test code can be reused
 * for various implementations such as spring annotations and jax-rs annotations
 * 
 * @author george georgovassilis
 *
 */
public interface BankService {

	Account transfer(Account fromAccount, Customer actor, Account toAccount, int amount, boolean sendConfirmationSms);

	Boolean checkAccount(Account account);

	byte[] updatePhoto(String name, byte[] photo);

	Account joinAccounts(Account account1, Account account2);

	Customer authenticate(String name, String password, String sessionId);

	Account getAccount(int id);

	boolean isSessionAlive(String sid);

	boolean doesCustomerExist(String name);

	boolean doesCustomerExist2(String name);

	List<Account> getAllAccounts();

}
