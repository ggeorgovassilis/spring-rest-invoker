package com.github.ggeorgovassilis.springjsonmapper.spring;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Interface that declares erroneous mappings. Used in tests to verify that validity checks work.
 * @author George Georgovassilis
 *
 */
public interface InterfaceWithErrors {

    @RequestMapping(value="/someurl", method=RequestMethod.POST)
    byte[] methodWithTwoAnonymousRequestBodies(@RequestBody byte[] b1, @RequestBody byte[] b2);

    @RequestMapping(value="/someurl", method=RequestMethod.POST)
    byte[] methodWithNamedAndAnonymousRequestBodies(@RequestBody @RequestParam("b1") byte[] b1, @RequestBody byte[] b2);

    @RequestMapping(value="/someurl")
    String methodWithIncompleteParameterAnnotations(@RequestParam("s1") String s1, String s2);

}
