package com.smartlearning.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.text.format.Time;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;

public  class SystemHandler {
	
	
	/**** Auth:
	 * ���·��������Activity�е��ã����Ҵ��뵱ǰ��Activity �� ***/

	
	
	/****  ����ȫ����ʾ ****/
	
	public static  void SetdisplayFullsreen(Activity activity){
	
		 
		activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		activity.getWindow()
		        .setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
		        		  WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		activity.getWindow()
		        .setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		        		  WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}
	 
	
	/******  ����ϵͳ����  ******/
	
	public static  void SetSystemSound(Activity activity,boolean beep){
	
		 AudioManager sound = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);  
    	 sound.setStreamMute(AudioManager.STREAM_MUSIC, beep);  
	}
	
	
	/****  ��ȡ��ǰ����ֱ���	 ****/
	
	
	public static  DisplayMetrics GetSystemUiMetrics(Activity activity)
	{ 
		return GetSystemUiMetrics(activity,true); 
	}
	
	public static  DisplayMetrics GetSystemUiMetrics(Activity activity, boolean Display)
	{
	
		DisplayMetrics dm = new DisplayMetrics();
		
		if(Display)
		{ 
    	   activity.getWindowManager().getDefaultDisplay().getMetrics(dm);  
		}
		else
		{
			dm.widthPixels  = 800;
			dm.heightPixels = 640; 
		}
		
		return dm;
    	 
	}
	
	
	/****  ��ȡϵͳ���ں�ʱ�䣬Ĭ�ϵĸ�ʽ��2012-06-12 12 : 12 : 19 ****/
	
	public static String getCurrentDateAndTime(Activity activity)
	{  
	    return  getCurrentDateAndTime(activity,null);
	}
	
	/***  ָ��ת���ĸ�ʽ,format *****/
	public static String getCurrentDateAndTime(Activity activity,String format)
	{
    
	    Calendar calendar = Calendar.getInstance(); 
	    Date date = (Date) calendar.getTime();  
	    
	    SimpleDateFormat formatmn = null;
	    if(format == null)
	    {
	          formatmn = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    }
	    else
	    {
	    	  formatmn = new SimpleDateFormat(format);
	    } 
	    
	    return formatmn.format(date).toString();   
	}
	
	
	/****  ��ȡϵͳ��ǰ���� ��Ĭ�ϵĸ�ʽ�� 2012-06-12 ****/
	
	public static String getCurrentDate(Activity activity)
	{  
	    return  getCurrentDate(activity,null);
	}
	
	/***  ָ��ת���ĸ�ʽ,format ****/
	public static String getCurrentDate(Activity activity,String format)
	{ 
		
        String  str = getCurrentDateAndTime(activity);
        
        int      len  = "yyyy-MM-dd".length(); 
        String   datastr = str.substring(0, len).trim();
        
        
        if(format == null)
        { 
        	return datastr; 
        }
        
        StringBuffer DatastrBuffer = new StringBuffer(); 
        DatastrBuffer.delete(0, DatastrBuffer.length()); 
        DatastrBuffer.append(datastr); 
        
        /*** �ַ��滻 **/
        DatastrBuffer.setCharAt(4, format.charAt(4)); 
        DatastrBuffer.setCharAt(7, format.charAt(7));
        
        return DatastrBuffer.toString();  
	}
	
	
	/*** *  ��ȡϵͳ��ǰʱ�� ��Ĭ�ϵĸ�ʽ�� 12 : 12 : 19  **/
	
	public static String getCurrentTime(Activity activity)
	{  
	    return  getCurrentTime(activity,null);
	}
	
	/***  ָ��ת���ĸ�ʽ,format ***/
	public static String getCurrentTime(Activity activity,String format)
	{ 
		
        String  datastr = getCurrentDateAndTime(activity).trim();
        
        int len = "HH:mm:ss".length();
    	int datastrstart = datastr.length() - len;   
    	String TimeStr = datastr.substring(datastrstart, datastr.length());
        
        if(format == null)
        {   
        	return TimeStr; 
        }
        
        StringBuffer DatastrBuffer = new StringBuffer(); 
        DatastrBuffer.delete(0, DatastrBuffer.length()); 
        DatastrBuffer.append(TimeStr); 
        
        /*** �ַ��滻 **/
        DatastrBuffer.setCharAt(2, format.charAt(2)); 
        DatastrBuffer.setCharAt(5, format.charAt(5));
        
        return DatastrBuffer.toString().trim();  
	}
	

}
