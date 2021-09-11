package com.yakir.utils;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class Utils {

	public static Set<String> stringToSet(String s) {
		return stringToSet(s, ",", LinkedHashSet<String>::new);
	}
	
	public static Set<String> stringToSet(String s, String delimiter) {
		return stringToSet(s, delimiter, LinkedHashSet<String>::new);
	}
	
	public static Set<String> stringToSortedSet(String s) {
		return stringToSet(s, ",", TreeSet<String>::new);
	}
	
	public static Set<String> stringToSortedSet(String s, String delimiter) {
		return stringToSet(s, delimiter, TreeSet<String>::new);
	}
	
	public static Set<String> stringToSet(String s, String delimiter, SetType<String> setType) {
		s = Optional.ofNullable(s).orElse("");
		return Arrays.stream(s.split(delimiter)).map(String::trim).filter(str -> !str.isEmpty()).collect(Collectors.toCollection(setType::getNewSet));
	}
	
	public interface SetType <T> {
		Set<T> getNewSet();
	}
	
	public static void sleep(long ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
		}
	}
	
}
