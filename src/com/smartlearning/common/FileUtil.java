package com.smartlearning.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.smartlearning.constant.ServerIP;

public class FileUtil {
	public static String getEncodeUrl(String url) {
		try {
			String filename = url.substring(url.lastIndexOf("/") + 1);
			url = url.substring(1, url.lastIndexOf("/") + 1)
					+ URLEncoder.encode(filename, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return ServerIP.SERVER_ID + url;
	}

	public static Bitmap getBitMap(String url) {
		URL myFileUrl = null;
		Bitmap bitmap = null;
		try {
			myFileUrl = new URL(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		try {
			HttpURLConnection conn = (HttpURLConnection) myFileUrl
					.openConnection();
			conn.setDoInput(true);
			conn.connect();
			InputStream is = conn.getInputStream();
			bitmap = BitmapFactory.decodeStream(is);
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bitmap;
	}

	public static String readTxtFile(String filePath) {
		try {
			String content = "";
			// String encoding = "UTF-8";
			URL url = new URL(filePath);

			InputStreamReader read = new InputStreamReader(url.openStream());
			BufferedReader bufferedReader = new BufferedReader(read);
			String lineTxt = null;
			while ((lineTxt = bufferedReader.readLine()) != null) {
				content += lineTxt;
			}
			read.close();
			return content;

		} catch (Exception e) {
			System.out.println("读取文件内容出错。");
			e.printStackTrace();
		}
		return "";
	}
}
