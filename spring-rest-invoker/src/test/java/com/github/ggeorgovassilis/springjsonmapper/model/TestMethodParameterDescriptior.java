package com.github.ggeorgovassilis.springjsonmapper.model;

import com.github.ggeorgovassilis.springjsonmapper.model.MethodParameterDescriptor.Type;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test for {@link MethodParameterDescriptor}
 * 
 * @author george georgovassilis
 *
 */

public class TestMethodParameterDescriptior {

	@Test
	public void testSettersAndGetters() {
		MethodParameterDescriptor mpd = new MethodParameterDescriptor();
		Method method = mpd.getClass().getMethods()[0];

		mpd.setMethod(method);
		mpd.setName("someName");
		mpd.setParameterOrdinal(3);
		mpd.setType(Type.httpHeader);
		mpd.setValue(this);

		assertEquals(method, mpd.getMethod());
		assertEquals("someName", mpd.getName());
		assertEquals(3, mpd.getParameterOrdinal());
		assertEquals(Type.httpHeader, mpd.getType());
		assertEquals(this, mpd.getValue());

	}

}
