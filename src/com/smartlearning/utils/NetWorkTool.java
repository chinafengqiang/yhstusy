package com.smartlearning.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.smartlearning.constant.ServerIP;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

public class NetWorkTool {
	/**
	 * 判断网络是否连结
	 * @param context
	 * @return
	 */
	public static boolean IsHttpConnect(Context context) {

		ConnectivityManager mConnectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo info = mConnectivity.getActiveNetworkInfo();
		NetworkInfo infomobile = mConnectivity.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

		if (info != null && infomobile != null) {
			return true;
		}

		Toast.makeText(context, "网络连接出现问题，请检查! ", Toast.LENGTH_SHORT).show();
		return false;
	}

	/**
	 * 从网络获取资源（字符串）
	 * @param webPath  web url
	 * @return 获取字符串
	 * @throws IOException 
	 */
	public static String getStringResourceFromWeb(String webPath) throws IOException {
		InputStream is = null;
		HttpURLConnection conn = null;
		String path = String.format(webPath, 1, 10000);
		try {
			URL url = new URL(path);
			Log.i("webPath=====", path);
			
			conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(5 * 1000);
			conn.setRequestMethod("GET");
			is = conn.getInputStream();
			String s = null;
			if (is != null) {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				byte[] buf = new byte[128];
				int ch = -1;
				while ((ch = is.read(buf)) != -1) {
					baos.write(buf, 0, ch);
				}
				s = new String(baos.toByteArray());
			}
			return s;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}finally{
			is.close();
			conn.disconnect();
		}
	}
	
	/**
	 * 从网络获取资源（字符串）
	 * @param webPath  web url
	 * @return 获取字符串
	 * @throws IOException 
	 */
	public static String getStringResourceFromWeb(String webPath, String parm) throws IOException {
		InputStream is = null;
		HttpURLConnection conn = null;
		String path = String.format(webPath, parm);
		try {
			URL url = new URL(path);
			Log.i("webPath=====", path);
			
			conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(5 * 1000);
			conn.setRequestMethod("GET");
			is = conn.getInputStream();
			String s = null;
			if (is != null) {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				byte[] buf = new byte[128];
				int ch = -1;
				while ((ch = is.read(buf)) != -1) {
					baos.write(buf, 0, ch);
				}
				s = new String(baos.toByteArray());
			}
			return s;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}finally{
			is.close();
			conn.disconnect();
		}
	}

	/**
	 * 从网络获取资源（字符串）
	 * @param webPath  web url
	 * @return 获取字符串
	 * @throws IOException 
	 */
	public static String getStringResourceFromWeb(String webPath, int num) throws IOException {
		InputStream is = null;
		HttpURLConnection conn = null;
		String path = String.format(webPath);
		try {
			URL url = new URL(path);
			Log.i("webPath=====", path);
			
			conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(5 * 1000);
			conn.setRequestMethod("GET");
			is = conn.getInputStream();
			String s = null;
			if (is != null) {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				byte[] buf = new byte[128];
				int ch = -1;
				while ((ch = is.read(buf)) != -1) {
					baos.write(buf, 0, ch);
				}
				s = new String(baos.toByteArray());
			}
			return s;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}finally{
			is.close();
			conn.disconnect();
		}
	}
	/**
	 * 从网络获取资源（图片资源）
	 * 
	 * @param path
	 *            web url
	 * @return 二进制数据
	 * @throws Exception
	 */
	public static byte[] getImageResourceFromWeb(String path) throws Exception {
		InputStream inStream = null;
		HttpURLConnection conn = null;
		try {
			URL url = new URL(path);
		    conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(5 * 1000);
			inStream = conn.getInputStream();// 通过输入流获取图片数据
			return StreamTool.readInputStream(inStream);// 得到图片的二进制数据
		} finally {
			inStream.close();
			conn.disconnect();
		}
	}

}
