package com.smartlearning.utils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.UUID;

/**
 * 读取流工具
 * @author Administrator
 *
 */
public class StreamTool {
	
	/**
	 * 返回UUID
	 */
	public static String createUUID() {
		
		return UUID.randomUUID().toString();
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
