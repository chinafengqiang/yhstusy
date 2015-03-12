package com.smartlearning.model;

import android.content.Context;

import java.util.TreeMap;

import com.smartlearning.parser.BaseParser;

//封装请求数据的模型
public class Request {
	public String serverPath;
    public int requestUrl;//请求地址
    public Context context;//上下文
    public TreeMap<String, String> requestDataMap;//发送请求的数据Map
    public BaseParser<?> jsonParser;//解析返回Json数据的parser
}
