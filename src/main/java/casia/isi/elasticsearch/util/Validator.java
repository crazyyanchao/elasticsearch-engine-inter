package casia.isi.elasticsearch.util;

import java.util.Collection;
import java.util.Map;

public class Validator {
	public static boolean check(String string) {
		return (string == null || "".equals(string.trim())) ? false : true;
	}

	public static boolean check(String[] string) {
		return (string == null || string.length == 0) ? false : true;
	}

	public static boolean check(Collection collection) {
		return (collection == null || collection.isEmpty()) ? false : true;
	}

	public static boolean check(Map map) {
		return (map == null || map.isEmpty()) ? false : true;
	}

	public static boolean check(Object o) {
		return o == null ? false : true;
	}

	public static void main(String[] args) {
	}
}
