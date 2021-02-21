package com.github.ggeorgovassilis.springjsonmapper.services.jaxrs;

import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import com.github.ggeorgovassilis.springjsonmapper.jaxrs.Headers;
import com.github.ggeorgovassilis.springjsonmapper.services.Account;
import com.github.ggeorgovassilis.springjsonmapper.services.BankService;
import com.github.ggeorgovassilis.springjsonmapper.services.Customer;

import java.util.List;

/**
 * Mapping to a hypothetical bank service REST API using JAX-RS annotations
 * 
 * @author george georgovassilis
 *
 */
public interface BankServiceJaxRs extends BankService {

	@Override
	@POST
	@Path("/transfer")
	Account transfer(@BeanParam @QueryParam("fromAccount") Account fromAccount,
			@BeanParam @QueryParam("actor") Customer actor, @BeanParam @QueryParam("toAccount") Account toAccount,
			@BeanParam @QueryParam("amount") int amount,
			@QueryParam("sendConfirmationSms") boolean sendConfirmationSms);

	@Override
	@POST
	@Path("/verify")
	Boolean checkAccount(@BeanParam Account account);

	@Override
	@POST
	@Path("/photo")
	@Consumes({ "image/gif", "image/jpeg", "image/png" })
	@Produces({ "image/jpeg" })
	byte[] updatePhoto(@QueryParam("name") String name, @BeanParam byte[] photo);

	@Override
	@POST
	@Path("/join-accounts")
	Account joinAccounts(@FormParam("") @QueryParam("account1") Account account1,
			@FormParam("") @QueryParam("account2") Account account2);

	@Override
	@POST
	@Path("/authenticate")
	Customer authenticate(@FormParam("") @QueryParam("name") String name,
			@FormParam("") @QueryParam("password") String password, @CookieParam("sid") String sessionId);

	@Override
	@Path("/accounts/{id}")
	Account getAccount(@PathParam("id") int id);

	@Override
	@Path("/session/check")
	boolean isSessionAlive(@HeaderParam("X-SessionId") String sid);

	@Override
	@Path("/${domain}/customer/{name}")
	boolean doesCustomerExist(@PathParam("name") String name);

	@Override
	@Path("/${domain}/customer/{name}")
	@Headers({"X-header-1=value1","X-header-2=value2"})
	boolean doesCustomerExist2(@PathParam("name") String name);

	@Override
	@Path(value = "/accounts")
	List<Account> getAllAccounts();
}
