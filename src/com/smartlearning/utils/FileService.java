package com.smartlearning.utils;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

public class FileService {
	private Context context;

	public FileService(Context context) {
		this.context = context;
	}

	/**
	 * * 保存内容到sd卡中（以私有文件形式保存） * @param filename 文件名称 *
	 * 
	 * @param content
	 *            文件内容 *
	 * @throws Exception
	 */
	public void save2sdCard(String fileName, String content) throws Exception {
		File file = new File(Environment.getExternalStorageDirectory(),
				fileName);
		FileOutputStream outputStream = new FileOutputStream(file);
		outputStream.write(content.getBytes());
		outputStream.close();
	}
	/** 
     * 保存文件 
     * @param bm 
     * @param fileName 
     * @throws IOException 
     */  
    public void save2sdCard(String fileName,Bitmap bm ) throws IOException {  
    	String sdCardDir = this.getSdCardDirectory();
        File dirFile = new File(sdCardDir);  
        if(!dirFile.exists()){  
            dirFile.mkdir();  
        }  
        File myCaptureFile = new File(sdCardDir ,fileName);  
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));  
        bm.compress(Bitmap.CompressFormat.JPEG, 100, bos);  
        bos.flush();  
        bos.close();  
    }  
    
    /**
     * 保存文件至手机Cache目录  
     * @param subPath  Cache下的子目录
     * @param fileName 要保存的文件名 (不带路径)
     * @param bm  //要保存的图片资源
     * @throws IOException
     */
    public void save2CacheDir(String subPath,String fileName,Bitmap bm ) throws IOException {  
    	String cacheDir = this.getCacheDirectory();
        File dirFile = new File(cacheDir+subPath);  
        if(!dirFile.exists()){  
            dirFile.mkdirs();  
        }  
        File myCaptureFile = new File(dirFile ,fileName);  
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));  
        bm.compress(Bitmap.CompressFormat.JPEG, 100, bos);  
        bos.flush();  
        bos.close();  
    } 
    /**
     * 保存文件至手机Cache目录  
     * @param subPath  Cache下的子目录
     * @param fileName 要保存的文件名 (不带路径)
     * @param content  //要保存的文字
     * @throws IOException
     */
    public void save2CacheDir(String subPath,String fileName,String content ) throws IOException {  
    	String cacheDir = this.getCacheDirectory();
        File dirFile = new File(cacheDir+subPath);  
        if(!dirFile.exists()){  
            dirFile.mkdirs();  
        }
        File myCaptureFile = new File(dirFile ,fileName);  
        FileOutputStream outputStream = new FileOutputStream(myCaptureFile); //context.openFileOutput(dirFile.getAbsolutePath()+"/"+fileName,
				//Context.MODE_PRIVATE);
		outputStream.write(content.getBytes());
		outputStream.close();
    }
    /**
     * 将缓存中指定的文件转换为Bitmap格式
     * @param fileName
     * @return
     */
    public Bitmap getBitmap2Cache(String fileName){
    	byte[] data;
		try {
			data = this.read(fileName);
			return this.getBitmapFromByte(data);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
    	
    }
    
    public String getString2Cache(String fileName){
    	byte[] data;
		try {
			File f = new File(fileName);
			data = this.read(f);
			return new String(data);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
    	
    }

	/**
	 * * 保存内容（以私有文件形式保存） * @param filename 文件名称 *
	 * 
	 * @param content
	 *            文件内容 *
	 * @throws Exception
	 */
	public void save(String fileName, String content) throws Exception {
		FileOutputStream outputStream = context.openFileOutput(fileName,
				Context.MODE_PRIVATE);
		outputStream.write(content.getBytes());
		outputStream.close();
	}

	/**
	 * * 保存内容（以私有文件形式保存+追加） *
	 * 
	 * @param filename
	 *            文件名称 *
	 * 
	 * @param content
	 *            文件内容 *
	 * @throws Exception
	 */
	public void saveAppend(String fileName, String content) throws Exception {
		FileOutputStream outputStream = context.openFileOutput(fileName,
				Context.MODE_APPEND);
		outputStream.write(content.getBytes());
		outputStream.close();
	}

	/**
	 * 保存内容（允许其他应用读取）
	 * 
	 * @param filename
	 *            文件名称
	 * @param content
	 *            文件内容
	 * @throws Exception
	 */
	public void saveReadAble(String fileName, String content) throws Exception {
		FileOutputStream outputStream = context.openFileOutput(fileName,
				Context.MODE_WORLD_READABLE);
		outputStream.write(content.getBytes());
		outputStream.close();
	}

	/**
	 * 文件读取
	 * 
	 * @param fileName
	 * @return
	 * @throws Exception
	 */
	public byte[] read(String fileName) throws Exception {
		FileInputStream inputStream = context.openFileInput(fileName);
		byte[] buffer = new byte[1024];
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		int len = -1;
		while ((len = inputStream.read(buffer)) != -1) {// 读取文件到缓冲区
			byteArrayOutputStream.write(buffer, 0, len);// 写入到内存
		}
		byte[] data = byteArrayOutputStream.toByteArray();// 将内存中的二进制数据转为数组
		byteArrayOutputStream.close();
		inputStream.close();
		return data;
	}
	
	public byte[] read(File f) throws Exception {
		FileInputStream inputStream =  new FileInputStream(f);//context.openFileInput(fileName);
		byte[] buffer = new byte[1024];
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		int len = -1;
		while ((len = inputStream.read(buffer)) != -1) {// 读取文件到缓冲区
			byteArrayOutputStream.write(buffer, 0, len);// 写入到内存
		}
		byte[] data = byteArrayOutputStream.toByteArray();// 将内存中的二进制数据转为数组
		byteArrayOutputStream.close();
		inputStream.close();
		return data;
	}

	
	/**
	 * 测试外部存储器是否可用
	 */
	public boolean isExternalStorageAvailable() {
		boolean state = false;
		String extStorageState = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
			state = true;
		}
		return state;
	}

	/**
	 * 测试外部存储器是否只可读
	 */
	public boolean isExternalStorageReadOnly() {
		boolean state = false;
		String extStorageState = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
			state = true;
		}
		return state;
	}
	/**
	 * 获取手机缓存路径
	 * @return
	 */
	public  String getCacheDirectory() {
		String cacheDirPath = null;

		File cacheDir = this.context.getCacheDir();// getCacheDir();
		if (cacheDir != null) {
			cacheDirPath = cacheDir.getAbsolutePath();
		}
		return cacheDirPath;
	}

	/**
	 * 获取手机SdCard路径
	 * @return
	 */
	public String getSdCardDirectory() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			File sdCardDir = Environment.getExternalStorageDirectory();// 获取SDCard目录
			
			return sdCardDir.getAbsolutePath();
		}
		return "";
	}
	
	// 得到存储在数据库中的图片
	// eg imageView.setImageBitmap(bitmapobj);
	public  Bitmap getBitmapFromByte(byte[] temp) {
		if (temp != null) {
			Bitmap bitmap = BitmapFactory.decodeByteArray(temp, 0, temp.length);
			return bitmap;
		} else {
			// Bitmap bitmap=BitmapFactory.decodeResource(getResources(),
			// R.drawable.contact_add_icon);
			return null;
		}
	}
}
