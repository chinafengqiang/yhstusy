package com.smartlearning.constant;

public class Global {
	/** 客户端版本号 */
	public final static int VERSION = 1;
	/** 获取客户端版本号操作ID */
	public final static int ACTION_ID_GET_VERSION = 0x10;
	/** 登入操作ID */
	public final static int ACTION_ID_DO_LOGIN = 0x20;
	/** 获取图片URL操作 */
	public final static int ACTION_ID_GET_PICS = 0x30;
	/** 获取图片URL操作 */
	public final static int ACTION_ID_GET_QUESTS = 0x40;
	/** 下载操作ID */
	public final static int ACTION_ID_DOWNLOAD = 0x50;
	
	/** 当前登入用户的ID */
	public static String userId;
	
	public final static String App_Name = "SmartLearningClient.apk";
	
	public final static String PDF_Name = "FoxitPDF.apk";
	
	public final static String Common_Port = "8080";
	public final static String TestPaper_Port = "8082";
	
	public final static int SUCCESS = 1;
	public final static int NET_FAILED = 2;
	public final static int TIME_OUT = 3;
}
