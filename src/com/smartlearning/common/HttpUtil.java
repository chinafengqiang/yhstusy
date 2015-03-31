package com.smartlearning.common;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import android.util.Log;

import com.smartlearning.utils.StreamTool;

/**
 * URL网络解析
 * @author Administrator
 */
public class HttpUtil {
	
  private static final int CONNECT_TIME_OUT = 15000;
  private static final int READ_TIME_OUT = 10000;
  /**
   * get方法传递参数
 * @param path
 * @return
 */
public static byte[] getDataFromUrl(String path){
	
		URL url = null;

		try {
			url = new URL(path);
		} catch (MalformedURLException e) {
			Log.i("learning", "获取URL错误");
		//	e.printStackTrace();
		}
		HttpURLConnection conn = null;
		try {
			conn = (HttpURLConnection) url.openConnection();
		} catch (IOException e) {
			Log.i("learning", "获取连接错误");
		//	e.printStackTrace();
		}
		conn.setConnectTimeout(CONNECT_TIME_OUT);//设置连接主机超时（单位：毫秒）
		conn.setReadTimeout(READ_TIME_OUT);//设置从主机读取数据超时（单位：毫秒）
		try {
			conn.setRequestMethod("GET");
		} catch (ProtocolException e) {
		//	e.printStackTrace();
		}

		InputStream inputStream = null;
		try {
			inputStream = conn.getInputStream();
		} catch (Exception e) {
			Log.i("learning", "获取输入流错误");
			e.printStackTrace();
		}
		byte[] data = null;
		try {
			data = StreamTool.readInputStream(inputStream);
		} catch (Exception e) {
		//	e.printStackTrace();
		}
		
		return data;
  }
  
  
  /**
   * psot方法传弟参数
 * @param path
 * @param params
 * @return
 */
public static byte[] getDataFromUrl(String path, String params){
		
	  	byte[] data = params.getBytes();
	  	byte[] returndata = null;
		URL url = null;
		try {
			url = new URL(path);
		} catch (MalformedURLException e) {
			Log.i("learning", "获取URL错误");
		//	e.printStackTrace();
		}
		HttpURLConnection conn = null;
		try {
			conn = (HttpURLConnection) url.openConnection();
		} catch (IOException e) {
			Log.i("learning", "获取连接错误");
		//	e.printStackTrace();
		}
		conn.setDoOutput(true); // 允许对外发送请求参数
		conn.setUseCaches(false); // 不进行缓存
		conn.setConnectTimeout(5 * 1000);
		
		try {
			conn.setRequestMethod("POST");
		} catch (ProtocolException e) {
		//	e.printStackTrace();
		}

		conn.setRequestProperty("Content-Type",
				"application/x-www-form-urlencoded");
		conn.setRequestProperty("Content-Length", String.valueOf(data.length));
		conn.setRequestProperty(
				"Accept",
				"image/gif, image/jpeg, image/pjpeg,image/pjpeg, application/x-shockwave-flash, application/xaml+xml, application/x-ms-xbap, application/vnd.ms-excel,  */*");
		conn.setRequestProperty("Accept-Language", "zh-CN");
		conn.setRequestProperty(
				"User-Agent",
				"Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)");
		conn.setRequestProperty("Connection", "Keep-Alive");
		
		// 发送参数
		DataOutputStream outStream;
		try {
			outStream = new DataOutputStream(conn.getOutputStream());
			outStream.write(data); // 把参数发送出去
			outStream.flush();
			outStream.close();
		} catch (Exception e) {
			//e.printStackTrace();
		}
		
		try {
			if (conn.getResponseCode() == 200) {
				try {
					returndata = StreamTool.readInputStream(conn.getInputStream());
				} catch (Exception e) {
					//e.printStackTrace();
				}
			} 
		} catch (IOException e) {
			//e.printStackTrace();
		}
		
		return returndata;
}

	public static byte[] readInputStream(InputStream inStream) throws Exception {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		while ((len = inStream.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
		}
		inStream.close();
		return outStream.toByteArray();
	}
}
