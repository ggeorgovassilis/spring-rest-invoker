package com.github.ggeorgovassilis.springjsonmapper.bank;

import java.io.Serializable;

/**
 * Models a bank account
 * @author george georgovassilis
 *
 */
public class Account implements Serializable {

    private String accountNumber;
    private int balance;
    private Customer owner;

    public String getAccountNumber() {
	return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
	this.accountNumber = accountNumber;
    }

    public int getBalance() {
	return balance;
    }

    public void setBalance(int balance) {
	this.balance = balance;
    }

    public Customer getOwner() {
	return owner;
    }

    public void setOwner(Customer owner) {
	this.owner = owner;
    }
    
}
