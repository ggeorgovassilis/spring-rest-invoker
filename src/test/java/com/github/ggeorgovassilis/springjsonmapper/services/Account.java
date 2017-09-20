package com.github.ggeorgovassilis.springjsonmapper.services;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Models a bank account
 * 
 * @author george georgovassilis
 *
 */
@JsonPropertyOrder({ "accountNumber", "balance", "owner" })
public class Account implements Serializable {

	private static final long serialVersionUID = 3338920622026973343L;
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
