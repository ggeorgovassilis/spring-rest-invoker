package com.github.ggeorgovassilis.springjsonmapper.utils;

/**
 * Utilities
 * 
 * @author George Georgovassilis
 *
 */
public class Utils {

	/**
	 * Returns true if arr!=null and arr.length &gt; 0
	 * 
	 * @param arr Array to check
	 * @return result of check
	 */
	public static boolean hasValue(Object[] arr) {
		return arr != null && arr.length > 0;
	}

	/**
	 * Returns true when s is neither null nor an empty string
	 * 
	 * @param s
	 *            Text to check
	 * @return true when s is neither null nor an empty string
	 */
	public static boolean hasValue(String s) {
		return s != null && !s.isEmpty();
	}
}
