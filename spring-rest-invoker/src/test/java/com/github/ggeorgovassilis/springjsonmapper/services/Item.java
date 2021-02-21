package com.github.ggeorgovassilis.springjsonmapper.services;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * 
 * @author george georgovassilis
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Item implements Serializable {

	private static final long serialVersionUID = -6854695261165137027L;
	String id;
	String selfLink;
	VolumeInfo volumeInfo;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSelfLink() {
		return selfLink;
	}

	public void setSelfLink(String selfLink) {
		this.selfLink = selfLink;
	}

	public VolumeInfo getVolumeInfo() {
		return volumeInfo;
	}

	public void setVolumeInfo(VolumeInfo volumeInfo) {
		this.volumeInfo = volumeInfo;
	}
}
