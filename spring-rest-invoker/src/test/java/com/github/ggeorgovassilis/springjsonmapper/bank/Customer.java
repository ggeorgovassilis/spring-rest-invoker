package com.github.ggeorgovassilis.springjsonmapper.bank;

import java.io.Serializable;

/**
 * 
 * @author geogeo
 *
 */
public class Customer implements Serializable{

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    
}
