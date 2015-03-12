package com.smartlearning.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import com.smartlearning.R;




/**
 * Created by FQ on 14-3-14.
 */
public class CommonUtil {
	protected static final String TAG = "CommonUtil";
	
	public static Dialog m_dialog=null;
	//public static AlertDialog.Builder localBuilder=null;
    public static void showInfoDialog(Context context, String message){
        showInfoDialog(context, message, R.string.comm_dialog_title,R.string.comm_dialog_positive,null);
    }

    public static void showInfoDialog(Context context, String message, String titleStr, String positiveStr,
                                      DialogInterface.OnClickListener onClickListener){
    	try{
    	if(m_dialog!=null&&m_dialog.isShowing()){
    		m_dialog.dismiss();
    		m_dialog=null;
    	}
    	
    
    	AlertDialog.Builder localBuilder = new AlertDialog.Builder(context);
        localBuilder.setTitle(titleStr);
        localBuilder.setMessage(message);
        if(onClickListener == null)
            onClickListener = new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }};
        localBuilder.setPositiveButton(positiveStr, onClickListener);
        m_dialog = localBuilder.create();
        m_dialog.show();
    	}catch(Exception e){
    		Log.e(TAG,e.getLocalizedMessage());
    	}
    }

    public static void showInfoDialog(Context context, String message, int title, int positive,
                                      DialogInterface.OnClickListener onClickListener){
    	try{
    		if(m_dialog!=null&&m_dialog.isShowing()){
        		m_dialog.dismiss();
        		m_dialog=null;
        	}
    	
    	/*Dialog dialog =new Dialog(context);
    	dialog.show();*/
    	AlertDialog.Builder localBuilder = new AlertDialog.Builder(context);
        localBuilder.setTitle(title);
        localBuilder.setMessage(message);
        
        
        if(onClickListener == null)
            onClickListener = new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }};
        localBuilder.setPositiveButton(positive, onClickListener);
        m_dialog = localBuilder.create();
        
        Window window = m_dialog.getWindow();  
        WindowManager.LayoutParams lp = window.getAttributes();  
        // 设置透明度 
        lp.alpha = 0.6f; 
        window.setAttributes(lp); 
        m_dialog.show();
    	}catch(Exception e){
    		Log.e(TAG,e.getLocalizedMessage());
    	}
    }

    public static String getSignTimestamp(){
        long time = new Date().getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMHHmmssdd");
        return format.format(new Date(time));
    }


    public static boolean isValidMobiNumber(String paramString) {
        String regex = "^1[3,5,7,8]\\d{9}$";
        if (paramString.matches(regex)) {
            return true;
        }
        return false;
    }

    public synchronized static String getRandomCode(int length){
        String sRand = "";
        for (int i = 0;i < length; i++) {
            Random random = new Random();
            String rand = String.valueOf(random.nextInt(10));
            sRand += rand;
        }
        return sRand;
    }


    public static Date stringTime2DateNotss(String date){
        long longDate = dateTimeString2LongNotss(date);
        return new Date(longDate);
    }

    public static Long dateTimeString2LongNotss(String date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date thisDate = null;
        try {
            thisDate = format.parse(date);
        } catch (ParseException e) {
            return null;
        }
        return new Long(thisDate.getTime());
    }

    public static String dateTime2StringNotS(Date date){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String thisDate = "";
        try {
            thisDate = format.format(date);
        } catch (Exception e) {
            return "";
        }
        return thisDate;
    }

    public static Date getEndValidDate4Day(Date date,int step){
        Calendar end = Calendar.getInstance();
        end.setTime(date);
        end.add(Calendar.DATE,step);
        return end.getTime();
    }

    
    public static String getPhoneNumber(Context context){
    	TelephonyManager phoneManager=(TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
    	String mobile =  phoneManager.getLine1Number();
    	if(mobile!=null&&!"".equals(mobile)){
    		if(mobile.startsWith("+86")){
    			mobile = mobile.substring(mobile.length() - 11);
    		}
    	}
    	return mobile;
    }

    /*
     * 设置Toast、可扩展成自定义Toast
     */
    public static void showToast(Context context,int resId,int duration){
    	Toast toast = Toast.makeText(context,
    		     resId,duration);
    	toast.setGravity(Gravity.CENTER, 0, 0);
    	toast.show();
    }

    public static void showToast(Context context,String info,int duration){
    	Toast toast = Toast.makeText(context,
    		     info,duration);
    	toast.setGravity(Gravity.CENTER, 0, 0);
    	toast.show();
    }
    

}
