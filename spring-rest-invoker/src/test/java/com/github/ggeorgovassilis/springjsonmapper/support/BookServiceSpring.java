package com.github.ggeorgovassilis.springjsonmapper.support;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Mapping to the google books API 
 * https://developers.google.com/books/
 * 
 * @author george georgovassilis
 *
 */
public interface BookServiceSpring {

    @RequestMapping("/volumes")
    QueryResult findBooksByTitle(@RequestParam("q") String q);
    
    @RequestMapping("/volumes/{id}")
    Item findBookById(@PathVariable("id") String id);
}
