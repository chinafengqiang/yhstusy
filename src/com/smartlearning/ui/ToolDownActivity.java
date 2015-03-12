package com.smartlearning.ui;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.smartlearning.R;
import com.smartlearning.constant.Global;
import com.smartlearning.utils.FileService;
import com.smartlearning.utils.FileUtil;
import com.smartlearning.utils.NetWorkTool;
import com.smartlearning.utils.Tool;

public class ToolDownActivity extends Activity {
	
	String serverIp = "";
	private SharedPreferences sharedPreferences;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);  
		getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN , WindowManager.LayoutParams. FLAG_FULLSCREEN);  
		setContentView(R.layout.activity_tools);
		sharedPreferences = getSharedPreferences("userInfo", MODE_PRIVATE);
		String ip = sharedPreferences.getString("serverIp", null);
		serverIp = "http://"+ ip +":8080";
		Button downTool = (Button) findViewById(R.id.downTool);
		final String update_url = serverIp +"/tools/"+ Global.PDF_Name;
		downTool.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				new MyTask(ToolDownActivity.this,handler).execute(update_url);
			}
		});
	
	}
	
	private Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			File file = new File(Environment.getExternalStorageDirectory() + "/"
					+ Global.PDF_Name); //下载的apk文件保存地址
			Intent intent = new Intent();
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setAction(android.content.Intent.ACTION_VIEW);
			intent.setDataAndType(Uri.fromFile(file),"application/vnd.android.package-archive");
			startActivity(intent);
		}
	};
	
class MyTask extends AsyncTask<String, Integer, String> {

	ProgressDialog pdialog;//用于显示进度的Dialog
	Handler handler;  //用于下载完成之后的客户端回调
	Context context;  //上下文对象
	String fileName = ""; //要下载资源名称
	FileService fileService =null;  //工具类
	private boolean isFinished = false; //用于标志是否下载完成

	@SuppressWarnings("deprecation")
	public MyTask(Context context, Handler handler) {
		this.context = context;
		this.handler = handler;
		fileService = new FileService(context);
		this.fileName = fileService.getSdCardDirectory()+ "/"+ Global.PDF_Name;
		
		pdialog = new ProgressDialog(context);
		pdialog.setButton("cancel", new DialogInterface.OnClickListener() {
			public void onClick(final DialogInterface dialog, final int i) {
				dialog.cancel();
				MyTask.this.cancel(true);
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
			MyTask.this.cancel(true);
			return;
		}

	}

	@Override
	protected void onPostExecute(String result) {
		if (this.handler != null) {
			if (result != null) {
				Message msg = new Message();
				msg.obj = this.fileName;
				handler.sendMessage(msg);
				this.isFinished = true;
				Tool.ShowMessage(context, "成功下载到SDCard中！");
			   
			}else{
				Tool.ShowMessage(context, "下载失败！");
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
		Tool.ShowMessage(context, "您已取消了的下载...");
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
			Log.d("DownFileTask", "Lenght of file: " + lenghtOfFile);
			InputStream input = new BufferedInputStream(url.openStream());
			Log.i("filename=======================", this.fileName);
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
}
}