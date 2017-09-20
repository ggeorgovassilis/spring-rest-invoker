package com.github.ggeorgovassilis.springjsonmapper.utils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * Generates standard dynamic java proxies
 * 
 * @author george georgovassilis
 *
 */
public class DynamicJavaProxyFactory implements ProxyFactory {

	@Override
	public Object createProxy(ClassLoader classLoader, Class<?>[] interfaces, InvocationHandler callback) {
		return Proxy.newProxyInstance(classLoader, interfaces, callback);
	}

}
