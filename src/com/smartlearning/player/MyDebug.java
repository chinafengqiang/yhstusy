package com.smartlearning.player;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

 

public class MyDebug {
	public    static   int     DEBUG   = 0;
	public    static   String  TAG     = "QianServer" ;  
	private   static   boolean  DisplayFlag   =    DEBUG == 0? true : false;
	
	 
	public MyDebug( int DEBUG) {
		super();
		
		 this.DEBUG = DEBUG;
		
	}

	public static  void  MyDebugLogV(String tag, String str)
	{
		 
			
			if(DisplayFlag)
			{
				Log.v(tag, str); 
			}
		
	}
	
	public static  void   MyDebugLogI(String tag, String str)
	{ 
			
			
			if(DisplayFlag)
			{
				Log.i(tag, str); 
			}
			
	}
	
	public static  void   MyDebugLogE(String tag, String str)
	{
		  
			if(DisplayFlag)
			{
				Log.e(tag, str);  
			}
	}
	
	
	public static  void   MyDebugLogW(String tag, String str)
	{
		 	 
			
			if(DisplayFlag)
			{
				Log.w(tag, str);
			}
		 
	}
	
	public static  void  ToastMessage(Context context,String str)
	{
		if(DisplayFlag)
		{ 
		    Toast.makeText(context, str, Toast.LENGTH_SHORT ).show();
		}
	}

}
