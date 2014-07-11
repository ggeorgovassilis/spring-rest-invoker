package com.github.ggeorgovassilis.springjsonmapper.utils;

/**
 * Utilities
 * @author George Georgovassilis
 *
 */
public class Utils {

    /**
     * Returns true if arr!=null and arr.length > 0
     * @param arr
     * @return
     */
    public static boolean hasValue(Object[] arr) {
	return arr!=null && arr.length>0;
    }

    public static boolean hasValue(String s) {
	return s!=null && !s.isEmpty();
    }
}
