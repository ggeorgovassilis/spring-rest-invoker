package com.github.ggeorgovassilis.springjsonmapper.support;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

/**
 * Mapping to the google books API 
 * https://developers.google.com/books/
 * 
 * @author george georgovassilis
 *
 */
public interface BookServiceJaxRs {

    @Path("/volumes")
    QueryResult findBooksByTitle(@QueryParam("q") String q);
    
    @Path("/volumes/{id}")
    Item findBookById(@PathParam("id") String id);
}
