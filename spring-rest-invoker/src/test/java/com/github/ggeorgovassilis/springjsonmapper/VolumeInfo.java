package com.github.ggeorgovassilis.springjsonmapper;

import java.io.Serializable;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VolumeInfo implements Serializable {
    String title;
    List<String> authors;
    String publisher;
    String publishedDate;
    String description;
    List<String> categories;

    public String getTitle() {
	return title;
    }

    public void setTitle(String title) {
	this.title = title;
    }

    public List<String> getAuthors() {
	return authors;
    }

    public void setAuthors(List<String> authors) {
	this.authors = authors;
    }

    public String getPublisher() {
	return publisher;
    }

    public void setPublisher(String publisher) {
	this.publisher = publisher;
    }

    public String getPublishedDate() {
	return publishedDate;
    }

    public void setPublishedDate(String publishedDate) {
	this.publishedDate = publishedDate;
    }

    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
    }

    public List<String> getCategories() {
	return categories;
    }

    public void setCategories(List<String> categories) {
	this.categories = categories;
    }
}
