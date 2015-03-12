package com.smartlearning.utils;


import com.smartlearning.common.HttpUtil;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

/**
 * 异步任务解析 
 * @author Administrator
 */
public class ClientAsync extends AsyncTask<String, Integer, String>  {
	private Context context = null;
	private Handler handler = null;
	
	private ProgressDialog pd = null;
	
	public ClientAsync(Context context,Handler handler,String url){
		this.context = context;
		this.handler = handler;
	}
	
	private void showProgressDialog(){
		if (pd==null){
			pd = new ProgressDialog(context);
			pd.setMessage("Loading...");
		}
		pd.show();
	}
	
	private void hideProgressDialog(){
		if (pd!=null){
			pd.dismiss();
		}
	}
	
	@Override
	protected void onPreExecute() {
		showProgressDialog();
		super.onPreExecute();
	}
	
	@Override
	protected void onProgressUpdate(Integer... values) {
		// TODO Auto-generated method stub
		super.onProgressUpdate(values);
	}
	
	@Override
	protected void onCancelled() {
		Toast.makeText(context, "任务已取消！", Toast.LENGTH_LONG).show();
		if (pd!=null && pd.isShowing()) pd.dismiss();
		super.onCancelled();
	}
	
    @Override
    protected void onPostExecute(String result) {
        hideProgressDialog();
        if (handler!=null){
        	Message message = new Message();
        	message.obj = result;
        	handler.sendMessage(message);
        }
        
    	super.onPostExecute(result);
    }

	@Override
	protected String doInBackground(String... params) {
		String url = params[0];
		byte[] data = HttpUtil.getDataFromUrl(url);
		String result =new String(data);
		return result;
	}

}
