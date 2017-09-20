package com.github.ggeorgovassilis.springjsonmapper.services;

import java.io.Serializable;

/**
 * 
 * @author george georgovassilis
 *
 */
public class Customer implements Serializable {

	private static final long serialVersionUID = 5450750236224806988L;
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
