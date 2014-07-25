package com.github.ggeorgovassilis.springjsonmapper.services.jaxrs;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import com.github.ggeorgovassilis.springjsonmapper.services.BookService;
import com.github.ggeorgovassilis.springjsonmapper.services.Item;
import com.github.ggeorgovassilis.springjsonmapper.services.QueryResult;

/**
 * Mapping to the google books API by using JAX-RS annotations
 * https://developers.google.com/books/
 * 
 * @author george georgovassilis
 *
 */
public interface BookServiceJaxRs extends BookService{

    @Override
    @Path("/volumes")
    QueryResult findBooksByTitle(@QueryParam("q") String q);
    
    @Override
    @Path("/volumes/{id}")
    Item findBookById(@PathParam("id") String id);
    
}
