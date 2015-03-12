package com.smartlearning.ui;



import com.smartlearning.R;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

public class AsyncTaskBase extends AsyncTask<String,Integer, Integer>{

	protected ProgressDialog progressDialog;
	private Context mContext;
	public AsyncTaskBase(Context context){
		this.mContext=context;
	}
	
	@Override
	protected Integer doInBackground(String... params) {
		return null;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		showProgressDialog();
	}

	@Override
	protected void onPostExecute(Integer result) {
		super.onPostExecute(result);
		closeProgressDialog();
	}
	
	 protected void showProgressDialog() {
	    	closeProgressDialog();
	        if (this.progressDialog == null) {
	        	this.progressDialog = new ProgressDialog(mContext,R.style.dialog);
	        }
	        this.progressDialog.setMessage(mContext.getString(R.string.LoadContent));
	        this.progressDialog.show();
	    }

	    /**
	     * 关闭提示框
	     */
	    protected void closeProgressDialog() {
	        if (this.progressDialog != null){
	            this.progressDialog.dismiss();
	            this.progressDialog = null;
	        }
	    }
	
}
