package com.github.ggeorgovassilis.springjsonmapper.jaxrs;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Supplement missing JAX-RS annotation that models both name and value
 * 
 * 
 * @author George Georgovassilis
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Headers {

	/**
	 * The name and values of the HTTP header to bind the method to.
	 * List as {"header1=value1","header2=value2",...}
	 * @return supplied values
	 */
	String[] value();


}
