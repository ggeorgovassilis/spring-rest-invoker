package com.github.ggeorgovassilis.springjsonmapper.services.spring;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.github.ggeorgovassilis.springjsonmapper.services.InterfaceWithErrors;

/**
 * Interface that declares erroneous mappings. Used in tests to verify that
 * validity checks work. Uses spring annotations.
 * 
 * @author George Georgovassilis
 *
 */
public interface InterfaceWithErrorsSpring extends InterfaceWithErrors {

	@Override
	@RequestMapping(value = "/someurl", method = RequestMethod.POST)
	byte[] methodWithTwoAnonymousRequestBodies(@RequestBody byte[] b1, @RequestBody byte[] b2);

	@Override
	@RequestMapping(value = "/someurl", method = RequestMethod.POST)
	byte[] methodWithNamedAndAnonymousRequestBodies(@RequestBody @RequestParam("b1") byte[] b1, @RequestBody byte[] b2);

	@Override
	@RequestMapping(value = "/someurl")
	String methodWithIncompleteParameterAnnotations(@RequestParam("s1") String s1, String s2);

	@Override
	@RequestMapping(value = "/someurl")
	String methodWithDuplicateParameterAnnotations(@RequestParam("s1") String s1, @RequestParam("s1") String s2);

	@Override
	@RequestMapping(value = "/someurl", method = { RequestMethod.PUT, RequestMethod.POST })
	public String methodWithAmbiguousHttpMethod();

}
