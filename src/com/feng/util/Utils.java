package com.feng.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class Utils {
	public static final int GET_CAMERA =0x11;//启动相机
	public static final int SHOW_CAMERA = 0x12;//显示照相的图片
	public static final int SHOW_CAMERA_RESULT = 0x13;//返回相片查看后的操作
	public static final int SHOW_ALL_PICTURE = 0x14;//查看图片
	public static final int SHOW_PICTURE_RESULT = 0x15;//查看图片返回
	public static final int SHOW_SELECT_PICTURE = 0x16;//查看选择图片
	public static final int SHOW_SELECT_PICTURE_RESULT = 0x17;//查看选择后确定发送图片返回
	
	public static final int CLOSE_INPUT = 0x01;//关闭软键盘
	public static final int CLOSE_MORE_OPERATE = 0x02;//关闭聊天框下方的其他操作的界面
	public static final int UPDATA_MSG = 0x03;//关闭聊天框下方的其他操作的界面
	public static final int CLOSE_MSG_HINT = 0x04;//关闭聊天框中消息提示
	public static final int PERSONAL_MSG_HINT = 0x05;//一个人的消息提示
	public static final int MULTI_MSG_HINT = 0x06;//多个人的消息提示
	public static final int UNREAD_MULTI_MSG_HINT = 0x07;//未读信息多个人的消息提示
	
	public static final int ONLINE_MESSAGE_PAGESIZE = 5;//在线交流每页显示数
		
	public static Handler handler;//用于关闭表情
	public static Handler handlerInput;//用于软键盘
	public static Handler updataMsg;//用于更新聊天界面的最新消息
	public static Handler notifyMegAdapter;
	public static Handler handlerInputAndView;//用于软键盘和聊天框
	
	public static final int CHANGED = 100;//跟新UI
	public static  String CHAT_UID ;//当前聊天对象的uid
	// 获取AppKey
    public static String getMetaValue(Context context, String metaKey) {
        Bundle metaData = null;
        String apiKey = null;
        if (context == null || metaKey == null) {
        	return null;
        }
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(
                    context.getPackageName(), PackageManager.GET_META_DATA);
            if (null != ai) {
                metaData = ai.metaData;
            }
            if (null != metaData) {
            	apiKey = metaData.getString(metaKey);
            }
        } catch (NameNotFoundException e) {
        }
        return apiKey;
    }
  
	/**
	 * toast的封装
	 * @param ctx Context
	 * @param text show Info
	 * @param show show Time
	 * @return
	 */
	public static Toast showToast(Context ctx,String text,int show){
		Toast t=Toast.makeText(ctx, text, show);
		return t;
	}
	/*
	 * toast的封装
	 */
	public static Toast showToast(Context ctx,String text,int show,int gravity, int xOffset, int yOffset){
		Toast t=Toast.makeText(ctx, text, show);
		t.setGravity(gravity, xOffset, yOffset);
		
		return t;
	}
	/**
	 * 获取系统当前时间
	 * @return
	 */
	public static String getNowDate(){
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String nowTime=sdf.format(System.currentTimeMillis());
		return nowTime;
	}
	/**
	 * 获取系统当前详细时间
	 * @return
	 */
	public static String getNowDateDetial(){
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String nowTime=sdf.format(System.currentTimeMillis());
		return nowTime;
	}

	
	/**
	  * 判断当前日期是星期几
	  * 
	  * @param  pTime     设置的需要判断的时间  //格式如2012-09-08
	  *  

	  * @return dayForWeek 判断结果
	  * @Exception 发生异常
	  */

	//  String pTime = "2012-03-12";
	 public static String getWeek(String pTime) {

	  
	  String Week = "";


	  SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
	  Calendar c = Calendar.getInstance();
	  try {

	   c.setTime(format.parse(pTime));

	  } catch (ParseException e) {
	   // TODO Auto-generated catch block
	   e.printStackTrace();
	  }
	  if (c.get(Calendar.DAY_OF_WEEK) == 1) {
	   Week += "星期日";
	  }
	  if (c.get(Calendar.DAY_OF_WEEK) == 2) {
	   Week += "星期一";
	  }
	  if (c.get(Calendar.DAY_OF_WEEK) == 3) {
	   Week += "星期二";
	  }
	  if (c.get(Calendar.DAY_OF_WEEK) == 4) {
	   Week += "星期三";
	  }
	  if (c.get(Calendar.DAY_OF_WEEK) == 5) {
	   Week += "星期四";
	  }
	  if (c.get(Calendar.DAY_OF_WEEK) == 6) {
	   Week += "星期五";
	  }
	  if (c.get(Calendar.DAY_OF_WEEK) == 7) {
	   Week += "星期六";
	  }

	 

	  return Week;
	 }
}
