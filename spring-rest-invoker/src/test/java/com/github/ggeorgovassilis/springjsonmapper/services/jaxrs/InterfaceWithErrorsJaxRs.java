package com.github.ggeorgovassilis.springjsonmapper.services.jaxrs;

import javax.ws.rs.BeanParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import com.github.ggeorgovassilis.springjsonmapper.services.InterfaceWithErrors;

/**
 * "Implementation" with jax-rs annotations
 * 
 * @author George Georgovassilis
 *
 */
public interface InterfaceWithErrorsJaxRs extends InterfaceWithErrors {

	@Override
	@Path("/someurl")
	@POST
	byte[] methodWithTwoAnonymousRequestBodies(@BeanParam byte[] b1, @BeanParam byte[] b2);

	@Override
	@Path("/someurl")
	@POST
	byte[] methodWithNamedAndAnonymousRequestBodies(@BeanParam @QueryParam("b1") byte[] b1, @BeanParam byte[] b2);

	@Override
	@Path("/someurl")
	String methodWithIncompleteParameterAnnotations(@QueryParam("s1") String s1, String s2);

	@Override
	@Path("/someurl")
	String methodWithDuplicateParameterAnnotations(@QueryParam("s1") String s1, @QueryParam("s1") String s2);

	@Override
	@Path("/someurl")
	@POST
	@PUT
	public String methodWithAmbiguousHttpMethod();
}
