package com.smartlearning.utils;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;

/**
 * 完成FastJson解析
 * @author Administrator
 */
public class FastJsonTools {

	/**
	 * 完成单个javabean解析
	 * @param jsonString
	 * @param cls
	 * @return
	 */
	public static <T> T getObect(String jsonString, Class<T> cls){
		T t = null;
		try{
			t = JSON.parseObject(jsonString, cls);
		} catch (Exception e){
			e.printStackTrace();
		}
		return t;
	}
	
	/**
	 * 完成list解析
	 * @param jsonString
	 * @param cls
	 * @return
	 */
	public static <T> List<T> getObects(String jsonString, Class<T> cls){
		List<T> list = new ArrayList<T>();
		try{
			list = JSON.parseArray(jsonString, cls);
		} catch (Exception e){
			e.printStackTrace();
		}
		return list;
	}
	
}
