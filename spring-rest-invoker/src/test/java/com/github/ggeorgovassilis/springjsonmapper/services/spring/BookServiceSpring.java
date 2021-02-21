package com.github.ggeorgovassilis.springjsonmapper.services.spring;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.github.ggeorgovassilis.springjsonmapper.services.BookService;
import com.github.ggeorgovassilis.springjsonmapper.services.Item;
import com.github.ggeorgovassilis.springjsonmapper.services.QueryResult;

/**
 * Mapping to the google books API by using spring annotations
 * https://developers.google.com/books/
 * 
 * @author george georgovassilis
 *
 */
public interface BookServiceSpring extends BookService {

	@Override
	@RequestMapping("/volumes")
	QueryResult findBooksByTitle(@RequestParam("q") String q);

	@Override
	@RequestMapping("/volumes/{id}")
	Item findBookById(@PathVariable("id") String id);

}
