package com.github.ggeorgovassilis.springjsonmapper.jaxrs;

import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;

import com.github.ggeorgovassilis.springjsonmapper.BaseRestInvokerProxyFactoryBean;
import com.github.ggeorgovassilis.springjsonmapper.MethodInspector;
import com.github.ggeorgovassilis.springjsonmapper.spring.SpringRestInvokerProxyFactoryBean;

/**
 * Similarly to {@link SpringRestInvokerProxyFactoryBean}, this factory binds a remote REST service to a local java interface and forwards method
 * invocations on the proxy to the remote REST service. As opposed to the {@link SpringRestInvokerProxyFactoryBean}, this implementation uses
 * JAX-RS annotations such as {@link Path} and {@link QueryParam}.
 * 
 * Annotations understood by this implementation are:
 * 
 * <ul>
 * <li>{@link Path} which maps a method to a REST URL
 * <li>{@link QueryParam} which maps a method argument to a URL query parameter
 * <li>{@link BeanParam} which must be used together with a {@link QueryParam} means that the object is to be encoded as a JSON field with the name
 * denoted by {@link QueryParam} in the request body. Similar to {@link RequestBody} from the {@link SpringRestInvokerProxyFactoryBean}
 * <li>{@link FormParam} which means that the parameter is to be encoded as part of a multi-form request. Similar to {@link RequestPart} from the {@link SpringRestInvokerProxyFactoryBean}
 * <li>{@link Consumes} and {@link Produces}
 * </ul>
 * <p>
 * <code><pre>
	&lt;bean id=&quot;RemoteBankServiceJaxRs&quot;
		class=&quot;com.github.ggeorgovassilis.springjsonmapper.jaxrs.JaxRsInvokerProxyFactoryBean&quot;&gt;
		&lt;property name=&quot;baseUrl&quot; value=&quot;http://localhost/bankservice&quot; /&gt;
		&lt;property name=&quot;remoteServiceInterfaceClass&quot; value=&quot;com.github.ggeorgovassilis.springjsonmapper.jaxrs.BankServiceJaxRs&quot;/&gt;
	&lt;/bean&gt;
 * </pre></code>
 * </p>
 * <p>
 * An example of the annotated service interface:
 * </p>
 * <code><pre> 
 * interface BankServiceJaxRs {
 *
 *	&#064;POST
 *	&#064;Path("/transfer")
 *	Account transfer(&#064;BeanParam &#064;QueryParam("fromAccount") Account fromAccount, &#064;BeanParam &#064;QueryParam("actor") Customer actor,
 *			&#064;BeanParam &#064;QueryParam("toAccount") Account toAccount, &#064;BeanParam &#064;QueryParam("amount") int amount,
 *			&#064;QueryParam("sendConfirmationSms") boolean sendConfirmationSms);
 *
 * }
 *                               
 * </pre></code>
 * 
 * This will result in a HTTP request similar to:
 * <code><pre>
 * 
 * POST http://localhost/bankservice/transfer?sendConfirmationSms=true
 * Accept=application/json, application/*+json
 * Content-Type=application/json;charset=UTF-8
 *
 * {"amount":1,"toAccount":{"accountNumber":"account 2","balance":0,"owner":{"name":"Customer 2"}},"fromAccount":{"accountNumber":"account 1","balance":1000,"owner":{"name":"Customer 1"}},"actor":{"name":"Customer 1"}}
 *
 * </pre>
 * </code>
 *
 * @author george georgovassilis
 * 
 */
public class JaxRsInvokerProxyFactoryBean extends BaseRestInvokerProxyFactoryBean {

    @Override
    protected MethodInspector constructDefaultMethodInspector() {
	JaxRsAnnotationMethodInspector inspector =  new JaxRsAnnotationMethodInspector();
	inspector.setEmbeddedValueResolver(expressionResolver);
	return inspector;
    }

}
