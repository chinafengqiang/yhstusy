package com.smartlearning.utils;

 

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.smartlearning.player.MyDebug;
 

public class Net
{
	
	   private static final   String TAG = "Tool_Net"; 
	   private static boolean display_net_state   = true;
	   private static boolean IsHttpConnectEnable = false;

	

	  /*
	   * 判断网络是否接通。
	   */
	  
		public static boolean IsHttpConnect(Context context)
		{
			 
			
			if(!IsHttpConnectEnable){
				return true;
			}
			
			ConnectivityManager mConnectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			
			NetworkInfo info =  mConnectivity.getActiveNetworkInfo();
			NetworkInfo infomobile = mConnectivity.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			
			if(info != null && infomobile != null)
			{
				if(display_net_state)
				{
	          
	              MyDebug.ToastMessage(context, "当前网络 :   " +	info.getTypeName());
	              
	              display_net_state = false;
				}
	            return true;
			}
			 
			
			MyDebug.ToastMessage(context, "网络连接出现问题，请检查! ");
			
			return false;
		}
	
		/*
		 *  是否开通网络检查
		 */
		
		public  static boolean  SetIntnectCheckFlag(Context context,String path)
		{
			
			  if(path == null) 
				  {
				       IsHttpConnectEnable = false; 
				       
				        MyDebug.ToastMessage(context, "播放路径为空--->" + path);
				       
				       return  false;
				  }
			  
			  
			  String  substr = path.substring(0, 10); 
			  substr.toLowerCase();  
			  
			  if(
			          ( substr.indexOf("/sdcard/") != -1)
			       || ( substr.indexOf("/data/")   != -1)
			       || ( substr.indexOf("/mnt/")    != -1) 
		       )
			  {
				     IsHttpConnectEnable = false; 
			  }
			  else if(
			              ( substr.indexOf("http://")    != -1)
				       || ( substr.indexOf("ftp://")     != -1)
				       || ( substr.indexOf("rstp://")    != -1) 
				       || ( substr.indexOf("stp://")     != -1) 
			       )
			  {
				     IsHttpConnectEnable = true;
			  }
			  else
			  {
				     MyDebug.ToastMessage(context, "播放路径不正确--->" + path);
				    
				    return  false;
			  }
			  
			//  MyDebug.ToastMessage(context, "播放路径为 --->" + path);
			  
			  return IsHttpConnect(context);  
		}
		
		
		public  static boolean  getHttpConnectEnable(Context context)
		{
			return IsHttpConnectEnable;
		}

}
