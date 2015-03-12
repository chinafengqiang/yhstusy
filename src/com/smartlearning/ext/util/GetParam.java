package com.smartlearning.ext.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public final class GetParam {
	/** 参数表 */
	private Map<String, String> params;
	/**
	 * 添加一个参数
	 * @param key 参数的键
	 * @param value 参数的值
	 */
	public void addParam(String key, String value) {
		if (null == params) {
			params = new HashMap<String, String>();
		}
		params.put(key, value);
	}
	@Override
	public String toString() {
		// 拼接参数并返回
		if (null != params) {
			StringBuffer sb = new StringBuffer();
			sb.append('?');
			Iterator<String> it = params.keySet().iterator();
			while (it.hasNext()) {
				String key = it.next();
				sb.append(key);
				sb.append('=');
				sb.append(params.get(key));
				if (it.hasNext()) {
					sb.append('&');
				}
			}
			return sb.toString();
		}
		return "";
	}
	
}
