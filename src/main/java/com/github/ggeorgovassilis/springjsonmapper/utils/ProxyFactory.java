package com.github.ggeorgovassilis.springjsonmapper.utils;

import java.lang.reflect.InvocationHandler;

/**
 * Interface for implementations that can create proxies. This is necessary
 * because some use cases might not work well with the default java dynamic
 * proxies but might require opaque proxies (i.e. cglib).
 * 
 * @author george georgovassilis
 *
 */
public interface ProxyFactory {

	/**
	 * Creates a proxy where each method invocation is passed to 'callback'
	 * 
	 * @param classLoader
	 *            Class loader to use
	 * @param interfaces
	 *            Interfaces to proxy
	 * @param callback
	 *            Handler to call for each method invocation on the proxied
	 *            interfaces
	 * @return Proxy
	 */
	Object createProxy(ClassLoader classLoader, Class<?>[] interfaces, InvocationHandler callback);
	
	/**
	 * Generated proxies will extend the class "c". Not all implementations support proxying
	 * classes (e.g. DynamicJavaProxyFactory)
	 * @param classLoader
	 */
	void setProxyTargetClass(Class<?> c);
	
	/**
	 * Proxies will use this classloader for the proxied class
	 * @param classLoader
	 */
	void setProxyTargetClassLoader(ClassLoader classLoader);
}
