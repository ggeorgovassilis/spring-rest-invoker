package com.github.ggeorgovassilis.springjsonmapper;

import java.lang.reflect.Method;

import com.github.ggeorgovassilis.springjsonmapper.model.UrlMapping;

/**
 * Looks at methods and extracts mappings to REST URLs
 * 
 * @author george georgovassilis
 *
 */
public interface MethodInspector {

	UrlMapping inspect(Method method, Object[] args);
}
