package com.github.ggeorgovassilis.springjsonmapper.jaxrs;

import java.lang.reflect.Method;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.RequestBody;

import com.github.ggeorgovassilis.springjsonmapper.BaseHttpJsonInvokerFactoryProxyBean;
import com.github.ggeorgovassilis.springjsonmapper.MethodInspector;
import com.github.ggeorgovassilis.springjsonmapper.model.UrlMapping;
import com.github.ggeorgovassilis.springjsonmapper.spring.SpringAnnotationsHttpJsonInvokerFactoryProxyBean;
import com.github.ggeorgovassilis.springjsonmapper.support.Account;
import com.github.ggeorgovassilis.springjsonmapper.support.Customer;

/**
 * Similarly to {@link SpringAnnotationsHttpJsonInvokerFactoryProxyBean}, this factory binds a remote REST service to a local java interface and forwards method
 * invocations on the proxy to the remote REST service. As opposed to the {@link SpringAnnotationsHttpJsonInvokerFactoryProxyBean}, this implementation uses
 * JAX-RS annotations such as {@link Path} and {@link QueryParam}.
 * 
 * Annotations understood by this implementation are:
 * 
 * {@link Path} which maps a method to a REST URL
 * {@link QueryParam} which maps a method argument to a URL query parameter
 * {@link FormParam} which maps a method argument to a JSON field. For a plain request body use an empty string as the parameter name. This is similar to {@link RequestBody} of
 * the {@link SpringAnnotationsHttpJsonInvokerFactoryProxyBean} and doesn't require a {@link QueryParam}
 * {@link Consumes} and {@link Produces}
 * 
 * <p>
 * <code><pre>
	&lt;bean id=&quot;RemoteBankServiceJaxRs&quot;
		class=&quot;com.github.ggeorgovassilis.springjsonmapper.jaxrs.JaxRsAnnotationsHttpJsonInvokerFactoryProxyBean&quot;&gt;
		&lt;property name=&quot;baseUrl&quot; value=&quot;http://localhost/bankservice&quot; /&gt;
		&lt;property name=&quot;remoteServiceInterfaceClass&quot; value=&quot;com.github.ggeorgovassilis.springjsonmapper.jaxrs.BankServiceJaxRs&quot;/&gt;
	&lt;/bean&gt;
 * </pre></code>
 * </p>
 * <p>
 * An example of the annotated service interface:
 * </p>
 * <br>
 * <code><pre> 
 * interface BankServiceJaxRs {
 *
 *	&#064;Path("/transfer")
 *	&#064;POST
 *	Account transfer(&#064;FormParam("fromAccount") Account fromAccount, &#064;FormParam("actor") Customer actor,
 *			&#064;FormParam("toAccount") Account toAccount, &#064;FormParam("amount") int amount,
 *			&#064;QueryParam("sendConfirmationSms") boolean sendConfirmationSms);
 *
 * }
 *                               
 *                               </pre></code>
 * @author george georgovassilis
 * 
 */
public class JaxRsAnnotationsHttpJsonInvokerFactoryProxyBean extends
	BaseHttpJsonInvokerFactoryProxyBean {

    @Override
    protected UrlMapping getRequestMapping(Method method) {
	Path path = AnnotationUtils.findAnnotation(method, Path.class);
	if (path == null)
	    return null;
	UrlMapping mapping = new UrlMapping();
	mapping.setUrl(path.value());

	Consumes consumes = AnnotationUtils.findAnnotation(method,
		Consumes.class);
	if (consumes != null)
	    mapping.setConsumes(consumes.value());

	// TODO: how to handle headers?
	Produces produces = AnnotationUtils.findAnnotation(method,
		Produces.class);
	if (produces != null)
	    mapping.setProduces(produces.value());

	mapping.setHttpMethod(HttpMethod.GET.name());
	if (AnnotationUtils.findAnnotation(method, POST.class) != null)
	    mapping.setHttpMethod(HttpMethod.POST.name());
	if (AnnotationUtils.findAnnotation(method, PUT.class) != null)
	    mapping.setHttpMethod(HttpMethod.PUT.name());
	if (AnnotationUtils.findAnnotation(method, DELETE.class) != null)
	    mapping.setHttpMethod(HttpMethod.DELETE.name());

	return mapping;
    }

    @Override
    protected String getRequestBodyAnnotationNameDisplayText() {
	return "@RequestBody";
    }

    @Override
    protected String getRequestParamAnnotationNameDisplayText() {
	return "@RequestParam";
    }

    @Override
    protected MethodInspector constructDefaultMethodInspector() {
	return new JaxRsAnnotationMethodInspector();
    }

}
