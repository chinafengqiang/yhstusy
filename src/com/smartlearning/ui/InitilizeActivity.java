package com.smartlearning.ui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;

import com.smartlearning.R;
import com.smartlearning.constant.Global;
import com.smartlearning.ext.ActionGetVersion;
import com.smartlearning.ext.dialog.DialogFactory;
import com.smartlearning.ext.util.OnActionListener;
import com.smartlearning.ext.util.ResponseParam;
import com.smartlearning.utils.NetUtil;

/**
 * 初始化页面
 * @author 
 * @since jdk7 sdk19
 * @version 1.0
 */
public class InitilizeActivity extends Activity implements OnActionListener {
	
	private ActionGetVersion action;
	private Dialog dialog;
	private SharedPreferences sharedPreferences;
	private int downLoadFileSize;
	private ProgressDialog mpDialog;
	private int fileSize;
	String serverIp = "";
	private Context context;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_initilize);
		context = this;
		sharedPreferences = getSharedPreferences("userInfo", MODE_PRIVATE);
		String ip = sharedPreferences.getString("serverIp", null);
		if(null == ip || ip.equals("")){
			// 启动Login页面
			Intent intent = new Intent();
			intent.setClass(InitilizeActivity.this, LoginActivity.class);
			startActivity(intent);
			// 关闭Initilize页面
			finish();
		} else {
			serverIp = "http://"+ ip +":"+Global.Common_Port;
			// 实例化Action
			action = new ActionGetVersion(Global.ACTION_ID_GET_VERSION, serverIp);
			action.setDevice(ActionGetVersion.DEVICE_TYPE_ANDROID);
			action.setOnActionListener(this);
			
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (null != dialog && dialog.isShowing()) {
			dialog.dismiss();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (NetUtil.isConnected(this)) {
			// 启动版本检查
			action.startAction();
		} else {
			if (null != dialog && dialog.isShowing()) {
				dialog.dismiss();
			}
			dialog = DialogFactory.showNetworkSettingFailed(this);
		}
	}

	@Override
	public void onActionSuccess(int actionId, ResponseParam ret) {

		//getAppVersionName
		
		if (actionId == Global.ACTION_ID_GET_VERSION) {
		//	int server = Integer.parseInt(ActionGetVersion.getServerVersion(ret));
			Double client = (Double) Double.parseDouble(ActionGetVersion.getClientVersion(ret));
			String local_VersionInfo = getAppVersionName(context);	//本地程序版本号
			Double localVersion = Double.parseDouble(local_VersionInfo);
			Log.i("localVersion", "localVersion===ssss"+localVersion);
			if (localVersion < client) {
				// 有新版本
				if (null != dialog && dialog.isShowing()) {
					dialog.dismiss();
				}
				dialog = DialogFactory.showNewVersion(this, new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						
						mpDialog = new ProgressDialog(InitilizeActivity.this);
						mpDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);// 设置风格为长进度条
						mpDialog.setIndeterminate(false);// 设置进度条是否为不明
						mpDialog.setCancelable(true);// 设置进度条是否可以按退回键取消
						mpDialog.setProgress(0);
						mpDialog.incrementProgressBy(1); // 增加和减少进度，这个属性必须的
						mpDialog.show();
						downLoadFile();
					}
					
				});
			} else {
				
				if (null != dialog && dialog.isShowing()) {
					dialog.dismiss();
				}
				DialogFactory.showNoVersion(this,  new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// 启动Login页面
						Intent intent = new Intent();
						intent.setClass(InitilizeActivity.this, MainActivity.class);
						startActivity(intent);
						// 关闭Initilize页面
						finish();
					}
					
				});
			
			}
		}
	}

	@Override
	public void onActionFailed(int actionId, int httpStatus) {
		if (null != dialog && dialog.isShowing()) {
			dialog.dismiss();
		}
		dialog = DialogFactory.showConnectError(this);
	}

	@Override
	public void onActionException(int actionId, String exception) {
		if (NetUtil.isConnected(this)) {
			// 当前请求错误未知的情况
			if (null != dialog && dialog.isShowing()) {
				dialog.dismiss();
			}
			try{
			dialog = DialogFactory.showConnectException(this);
			} catch(Exception e){
				
			}
		} else {
			// 当前网络断掉的情况
			if (null != dialog && dialog.isShowing()) {
				dialog.dismiss();
			}
			dialog = DialogFactory.showNetworkSettingFailed(this);
		}
	}
	
	private void sendMsg(int flag) {
		Message msg = Message.obtain();
		msg.what = flag;
		handler.sendMessage(msg);
	}
	
	private final Handler handler = new Handler(Looper.getMainLooper()) {
		public void handleMessage(Message msg) {
			if (!Thread.currentThread().isInterrupted()) {
				Log.i("msg what", String.valueOf(msg.what));
				switch (msg.what) {
				case 0:
					mpDialog.setMax(100);
					break;
				case 1:
					int result = downLoadFileSize * 100 / fileSize;
					mpDialog.setProgress(result);
					break;
				case 2:
					openFile();
					mpDialog.cancel();
					break;
				case -1:
					String error = msg.getData().getString("error");
					mpDialog.setMessage(error);
					break;
				default:
					break;
				}
			} 
			super.handleMessage(msg);
		}
	};
	
	private void openFile() {
		File file = new File(Environment.getExternalStorageDirectory() + "/"
				+ Global.App_Name); //下载的apk文件保存地址
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(android.content.Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(file),"application/vnd.android.package-archive");
		startActivity(intent);
	}
	
	
	/**
	 * 下载apk
	 */
	private void downLoadFile() {
		new Thread() {
			public void run() {
				String update_url = serverIp +"/tools/"+ Global.App_Name;
				URL url = null;
				try {
					url = new URL(update_url);
					HttpURLConnection con = (HttpURLConnection) url
							.openConnection();
					InputStream in = con.getInputStream();
					fileSize = con.getContentLength();
					File fileOut = new File(Environment.getExternalStorageDirectory() + "/"+ Global.App_Name);
					FileOutputStream out = new FileOutputStream(fileOut);
					byte[] bytes = new byte[1024];
					downLoadFileSize = 0;
					sendMsg(0);
					int c;
					while ((c = in.read(bytes)) != -1) {
						out.write(bytes, 0, c);
						downLoadFileSize += c;
						sendMsg(1);// 更新进度条
					}
					in.close();
					out.close();
				} catch (Exception e) {
				}
				sendMsg(2);// 下载完成
				try {

				} catch (Exception e) {
				}
			}
		}.start();
	}
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (null != dialog && dialog.isShowing()) {
			dialog.dismiss();
		}
		return super.onKeyDown(keyCode, event);
	}
	
	public String getAppVersionName(Context context) {
		String versionName = "";
		try {
			// ---get the package info---
			PackageManager pm = context.getPackageManager();
			// 这里的context.getPackageName()可以换成你要查看的程序的包名
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
			versionName = pi.versionName;
			if (versionName == null || versionName.length() <= 0) {
				return "";
			}
		} catch (Exception e) {
		}
		return versionName;
	}

}
