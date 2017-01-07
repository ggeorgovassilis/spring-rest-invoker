package com.github.ggeorgovassilis.springjsonmapper.services;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


/**
 * 
 * @author george georgovassilis
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class QueryResult implements Serializable {

    private int totalItems;
    List<Item> items;

    public int getTotalItems() {
	return totalItems;
    }

    public void setTotalItems(int totalItems) {
	this.totalItems = totalItems;
    }

    public List<Item> getItems() {
	return items;
    }

    public void setItems(List<Item> items) {
	this.items = items;
    }

}
