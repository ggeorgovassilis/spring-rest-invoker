package com.github.ggeorgovassilis.springjsonmapper.services;

/**
 * Functional interface for a book retrieval service. There is no annotation
 * mappings on purpose; instead, each implementation will extend this interface
 * and overwrite methods with implementation-specific annotations
 * 
 * @author george georgovassilis
 *
 */
public interface BookService {

	QueryResult findBooksByTitle(String q);

	Item findBookById(String id);

}
