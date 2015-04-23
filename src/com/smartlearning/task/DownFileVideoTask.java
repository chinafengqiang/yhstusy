package com.smartlearning.task;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.smartlearning.R;
import com.smartlearning.biz.AdviodManager;
import com.smartlearning.utils.CommonUtil;
import com.smartlearning.utils.FileService;
import com.smartlearning.utils.FileUtil;
import com.smartlearning.utils.NetWorkTool;
import com.smartlearning.utils.Tool;

public class DownFileVideoTask extends AsyncTask<String, Integer, String> {

	ProgressDialog pdialog;//用于显示进度的Dialog
	Handler handler;  //用于下载完成之后的客户端回调
	Context context;  //上下文对象
	String fileName = ""; //要下载资源名称
	FileService fileService =null;  //工具类
	private boolean isFinished = false; //用于标志是否下载完成
	int id = 0;
	String videoSize = "";
	int lenghtOfFile = 0;
	private AdviodManager manager = null;
	String rootPath = Environment.getExternalStorageDirectory().getPath() + "/myVideo/";
	public String downloadResult;
	
	
	public DownFileVideoTask(Context context, Handler handler, String fileName, String pathName, int id) {
		this.context = context;
		this.handler = handler;
		this.id = id;
		fileService = new FileService(context);
		this.fileName = fileService.getSdCardDirectory()+"/"+pathName+"/"+ fileName;
		
		pdialog = new ProgressDialog(context);
		pdialog.setButton("cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int i) {
				dialog.cancel();
				DownFileVideoTask.this.cancel(true);
			}
		});

		pdialog.setIcon(R.drawable.flag);
		pdialog.setTitle(fileName+" 下载中....");
		pdialog.setCancelable(false);
		pdialog.setMax(100);
		pdialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pdialog.show();
	}

	@Override
	protected void onPreExecute() {
		boolean isNet = NetWorkTool.IsHttpConnect(context);
		if (!isNet) {
			pdialog.cancel();
			DownFileVideoTask.this.cancel(true);
			return;
		}

	}

	@Override
	protected void onPostExecute(String result) {
		downloadResult = result;
		if (this.handler != null) {
			if (result != null) {
				Message msg = new Message();
				msg.obj = this.fileName;
				handler.sendMessage(msg);
				this.isFinished = true;
				
				/*String videoSize = size2string(this.lenghtOfFile);
				manager = new AdviodManager(context);
				try {
					manager.modifyEVideo(id, videoSize);
				} catch (Exception e) {
				}*/
				
				//detailActivity.offlinePlayVideo(fileName);
			}else{
				CommonUtil.showToast(context, "下载失败！",Toast.LENGTH_LONG);
			}
		}
		pdialog.dismiss();
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		pdialog.setProgress(values[0]);
	}

	@Override
	protected void onCancelled() {
		CommonUtil.showToast(context, "您已取消了的下载...",Toast.LENGTH_LONG);
		if (!this.isFinished){
		   FileUtil.delFile(this.fileName);
		}
		super.onCancelled();
	}

	@Override
	protected String doInBackground(String... params) {
		try {
			int count;
			URL url = new URL(params[0]);
			URLConnection conn = url.openConnection();
			conn.setReadTimeout(10*1000);
			//conn.connect();

			int lenghtOfFile = conn.getContentLength();
			this.lenghtOfFile = lenghtOfFile;
			Log.d("DownFileTask", "Lenght of file: " + lenghtOfFile);
			InputStream input = new BufferedInputStream(url.openStream());
			OutputStream output = new FileOutputStream(this.fileName);
			byte data[] = new byte[4096];
			long total = 0;
			while ((count = input.read(data)) != -1) {
				total += count;
				publishProgress((int) ((total * 100) / lenghtOfFile));
				output.write(data, 0, count);
			}
			output.flush();
			output.close();
			input.close();
			return "ok";

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		// return null;
	}
	
	private String size2string(long size) {
		DecimalFormat df = new DecimalFormat("0.00");
		String mysize = "";
		if (size > 1024 * 1024) {
			mysize = df.format(size / 1024f / 1024f) + "M";
		} else if (size > 1024) {
			mysize = df.format(size / 1024f) + "K";
		} else {
			mysize = size + "B";
		}
		return mysize;
	}

}