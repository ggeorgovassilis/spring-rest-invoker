package com.github.ggeorgovassilis.springjsonmapper.jaxrs;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import com.github.ggeorgovassilis.springjsonmapper.support.Account;
import com.github.ggeorgovassilis.springjsonmapper.support.Customer;

/**
 * Mapping to a hypothetical bank service REST API using JAX-RS annotations
 * @author george georgovassilis
 *
 */
public interface BankServiceJaxRs {

	@Path("/transfer")
	@POST
	Account transfer(@FormParam("fromAccount") Account fromAccount, @FormParam("actor") Customer actor,
			@FormParam("toAccount") Account toAccount, @FormParam("amount") int amount,
			@QueryParam("sendConfirmationSms") boolean sendConfirmationSms);

	@Path("/verify")
	@POST
	Boolean checkAccount(@FormParam("") Account account);

	@POST
	@Path("/photo")
	@Consumes({ "image/gif","image/jpeg","image/png" })
	@Produces("image/jpeg")
	byte[] updatePhoto(@QueryParam("name") String name, @FormParam("") byte[] photo);

	@Path("/join-accounts")
	@POST
	Account joinAccounts(@FormParam("account1") Account account1, @FormParam ("account2") Account account2);
	
}
