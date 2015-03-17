package com.feng.util;

public class StringUtils {
	public static boolean isBlank(String s){
		if(s == null || s.length() <= 0){
			return true;
		}
		return false;
	}
	
	public static boolean isNotBlank(String s){
		return !isBlank(s);
	}
}
