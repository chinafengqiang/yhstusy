package com.smartlearning.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnConnectionPNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;





import com.smartlearning.R;
import com.smartlearning.model.Request;
import com.smartlearning.model.Response;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.util.TreeMap;


/**
 * Created by FQ on 14-3-14.
 */
public class HttpUtil {

	//连接超时时间30秒
	private static final int TIMEOUT = 30000;
    /**
     * post
     * @param request
     * @return
     */
    public static void post(Request request,Response res)  {
        Object obj = null;
        DefaultHttpClient client = new DefaultHttpClient();
        client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, TIMEOUT);
        client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,TIMEOUT);
        String requestUrl = request.context.getString(request.requestUrl);
        HttpPost post = new HttpPost(request.serverPath.concat("?method="+requestUrl));
        TreeMap<String,String> map = getRequestParams(request);
        try {
            ArrayList<BasicNameValuePair> pairList = new ArrayList<BasicNameValuePair>();
            for(Map.Entry<String,String> entry:map.entrySet()){
                BasicNameValuePair pair = new BasicNameValuePair(entry.getKey(), entry.getValue());
                pairList.add(pair);
            }
            HttpEntity entity = new UrlEncodedFormEntity(pairList,"UTF-8");
            post.setEntity(entity);

            HttpResponse response = client.execute(post);
            if(response.getStatusLine().getStatusCode()==HttpStatus.SC_OK){
                String result = EntityUtils.toString(response.getEntity(),"UTF-8");
                try {
                    obj = request.jsonParser.parseJSON(result);
                } catch (JSONException e) {
                    Log.e(HttpUtil.class.getSimpleName(), e.getLocalizedMessage(),e);
                }
                res.status = HttpStatus.SC_OK;
                res.result = obj;
            }
        } catch (ClientProtocolException e) {
        	res.status = 400;
            Log.e(HttpUtil.class.getSimpleName(), e.getLocalizedMessage(),e);
        } catch (IOException e) {
        	res.status = 503;//连接服务器异常、服务器不可用
            Log.e(HttpUtil.class.getSimpleName(), e.getLocalizedMessage(),e);
        }
    }
    
    public static Object post(Request request)  {
        Object obj = null;
        DefaultHttpClient client = new DefaultHttpClient();
        client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, TIMEOUT);
        client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,TIMEOUT);
        HttpPost post = new HttpPost(request.serverPath);
        TreeMap<String,String> map = getRequestParams(request);
        try {
            ArrayList<BasicNameValuePair> pairList = new ArrayList<BasicNameValuePair>();
            for(Map.Entry<String,String> entry:map.entrySet()){
                BasicNameValuePair pair = new BasicNameValuePair(entry.getKey(), entry.getValue());
                pairList.add(pair);
            }
            HttpEntity entity = new UrlEncodedFormEntity(pairList,"UTF-8");
            post.setEntity(entity);

            HttpResponse response = client.execute(post);
            if(response.getStatusLine().getStatusCode()==HttpStatus.SC_OK){
                String result = EntityUtils.toString(response.getEntity(),"UTF-8");
                try {
                    obj = request.jsonParser.parseJSON(result);
                } catch (JSONException e) {
                    Log.e(HttpUtil.class.getSimpleName(), e.getLocalizedMessage(),e);
                }
                return obj;
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            Log.e(HttpUtil.class.getSimpleName(), e.getLocalizedMessage(),e);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(HttpUtil.class.getSimpleName(), e.getLocalizedMessage(),e);
        }
        return null;
    }

    /*public static Object get(Request request){
        DefaultHttpClient client = new DefaultHttpClient();
        client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, TIMEOUT);
        client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,TIMEOUT);
        TreeMap<String,String> map = getRequestParams(request);
        String requestParams = ApiUtil.getRequestParams(map);
        HttpGet get = new HttpGet(appHost.concat("?").concat(requestParams));
        Object obj = null;
        try {
            HttpResponse response = client.execute(get);
            if(response.getStatusLine().getStatusCode()==HttpStatus.SC_OK){
                String result = EntityUtils.toString(response.getEntity(),"UTF-8");
                Log.e(HttpUtil.class.getSimpleName(), result);
                try {
                    obj = request.jsonParser.parseJSON(result);
                } catch (JSONException e) {
                    Log.e(HttpUtil.class.getSimpleName(), e.getLocalizedMessage(),e);
                }
                return obj;
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }*/

    public static TreeMap<String,String> getRequestParams(Request request){
        TreeMap<String,String> map = request.requestDataMap;
        /*if(map == null)
            map = new TreeMap<String, String>();
        //获取api的sign
        map.put("method",request.context.getString(request.requestUrl));
        map.put("timestamp",CommonUtil.getSignTimestamp());
        String userid = request.context.getString(R.string.api_request_uid);
        String pass = request.context.getString(R.string.api_request_pass);
        String sign = ApiUtil.getSign(map,userid,pass);
        map.put("sign",sign);*/
        return map;
    }

    /**
     * 获得网络连接是否可用
     * @param context
     * @return
     */
    public static boolean hasNetwork(Context context){
        ConnectivityManager con = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo workinfo = con.getActiveNetworkInfo();
        if(workinfo == null || !workinfo.isAvailable())
        {
        	/*try{
        		Toast.makeText(context, R.string.net_error, Toast.LENGTH_SHORT).show();
        	}catch(Exception e){
        		
        	}*/
            return false;
        }
        return true;
    }
    
    
//判断WIFI网络是否可用 
public static boolean isWifiConnected(Context context) { 
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mWiFiNetworkInfo = mConnectivityManager
					.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			if (mWiFiNetworkInfo != null) {
				return mWiFiNetworkInfo.isAvailable();
			}
		}
		return false;
}


public static boolean netWorkIsOK(Context context){
	return hasNetwork(context)&&isWifiConnected(context);
}


}
