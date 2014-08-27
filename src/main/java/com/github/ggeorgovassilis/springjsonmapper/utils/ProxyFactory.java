package com.github.ggeorgovassilis.springjsonmapper.utils;

import java.lang.reflect.InvocationHandler;

/**
 * Interface for implementations that can create proxies. This is necessary because some use cases
 * might not work well with the default java dynamic proxies but might require opaque proxies (i.e. cglib).
 * @author george georgovassilis
 *
 */
public interface ProxyFactory {
    
    /**
     * Creates a proxy where each method invocation is passed to 'callback'
     * @param classLoader
     * @param interfaces
     * @param callback
     * @return
     */
    Object createProxy(ClassLoader classLoader, Class<?>[] interfaces, InvocationHandler callback);

}
