package com.github.ggeorgovassilis.springjsonmapper.utils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import net.sf.cglib.proxy.Enhancer;

/**
 * Implements proxies with cglib
 * @author george georgovassilis
 *
 */
public class CglibProxyFactory implements ProxyFactory{
    
    protected Class<?> baseClass;
    protected ClassLoader classLoader;
    
    public CglibProxyFactory(ClassLoader classLoader, Class<?> baseClass) {
	this.baseClass = baseClass;
	this.classLoader = classLoader;
    }

    @Override
    public Object createProxy(final ClassLoader classLoader, final Class<?>[] interfaces,
	    final InvocationHandler callback) {
	Enhancer enhancer = new Enhancer();
	enhancer.setSuperclass(Object.class);
	enhancer.setClassLoader(classLoader);
	enhancer.setCallback(new net.sf.cglib.proxy.InvocationHandler() {
	    
	    @Override
	    public Object invoke(Object proxy, Method method, Object[] args)
		    throws Throwable {
		return callback.invoke(proxy, method, args);
	    }
	});
	enhancer.setInterfaces(interfaces);
	return enhancer.create();
    }

}
