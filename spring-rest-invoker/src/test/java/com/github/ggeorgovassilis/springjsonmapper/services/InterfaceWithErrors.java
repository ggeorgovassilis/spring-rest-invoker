package com.github.ggeorgovassilis.springjsonmapper.services;

/**
 * Interface that declares erroneous mappings. Used in tests to verify that
 * validity checks work.
 * 
 * @author George Georgovassilis
 *
 */
public interface InterfaceWithErrors {

	byte[] methodWithTwoAnonymousRequestBodies(byte[] b1, byte[] b2);

	byte[] methodWithNamedAndAnonymousRequestBodies(byte[] b1, byte[] b2);

	String methodWithIncompleteParameterAnnotations(String s1, String s2);

	String methodWithDuplicateParameterAnnotations(String s1, String s2);

	String methodWithAmbiguousHttpMethod();

}
