package com.smartlearning.utils;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 图片流工具类
 * @author Administrator
 */
public class ImageService {
	
	public static byte[] getImage(String path) throws Exception {
		URL url = new URL(path);
		HttpURLConnection conn = (HttpURLConnection)url.openConnection();
		conn.setRequestMethod("GET");
		conn.setConnectTimeout(5 * 1000);
		InputStream inStream = conn.getInputStream();//通过输入流获取图片数据
		return StreamTool.readInputStream(inStream);//得到图片的二进制数据
	}
	
	public static InputStream getInputStream(String path) throws Exception {
		URL url = new URL(path);
		HttpURLConnection conn = (HttpURLConnection)url.openConnection();
		conn.setRequestMethod("GET");
		conn.setConnectTimeout(5 * 1000);
		InputStream inStream = conn.getInputStream();//通过输入流获取图片数据
		return inStream;
	}

}
