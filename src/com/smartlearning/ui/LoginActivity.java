package com.smartlearning.ui;

import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.feng.util.StringUtils;
import com.smartlearning.R;
import com.smartlearning.biz.UserManager;
import com.smartlearning.constant.Global;
import com.smartlearning.model.Response;
import com.smartlearning.model.SysMessage;
import com.smartlearning.model.User;
import com.smartlearning.model.UserInfo;
import com.smartlearning.utils.CommonUtil;
import com.smartlearning.utils.HttpUtil;

public class LoginActivity extends Activity {

	private static final String ENCODING_UTF_8 = "utf-8";
	TextView lblMSG;
	private SharedPreferences sp = null;// 记录登陆状态的存储
	public static final int REM = 1; // 表示记录已登录状态
	public static final int NOREM = 2;// 表示未记录
	private EditText txtStudnetNo, txtPassword, txtServerIp;
	private Context mContext;
	// 进度条
	private ProgressDialog progressDialog = null;
	String webPath = "";
	String serverIp = "";
	private UserManager manager = null; 
	private String name;
	private String password;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		// 设置无标题
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 设置全屏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_login);

		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
				.detectDiskReads().detectDiskWrites().detectNetwork()
				.penaltyLog().build());
		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().penaltyLog()
				.penaltyDeath().build());

		lblMSG = (TextView) findViewById(R.id.lblMSG);
		Button btnLogin = (Button) findViewById(R.id.btnLogin);
		Button bottom_about = (Button) findViewById(R.id.bottom_about);
		txtStudnetNo = (EditText) findViewById(R.id.txtStudnetNo);
		txtPassword = (EditText) findViewById(R.id.txtPassword);
		txtServerIp = (EditText) findViewById(R.id.serverIp);
		
		
		sp = getSharedPreferences("userInfo", MODE_PRIVATE);// sp存储
		// 跳转界面
		txtStudnetNo.setText(sp.getString("name", ""));
		txtServerIp.setText(sp.getString("serverIp", ""));

		btnLogin.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {

				name = txtStudnetNo.getText().toString().trim();
				password = txtPassword.getText().toString().trim();
				serverIp = txtServerIp.getText().toString();
				webPath = "http://"+ serverIp +":"+Global.Common_Port;
				if(StringUtils.isBlank(name)){
					CommonUtil.showToast(mContext, getString(R.string.prompt_student_no), Toast.LENGTH_LONG);
					return;
				}
				if(StringUtils.isBlank(password)){
					CommonUtil.showToast(mContext, getString(R.string.prompt_password), Toast.LENGTH_LONG);
					return;
				}
				if(StringUtils.isBlank(serverIp)){
					CommonUtil.showToast(mContext, getString(R.string.prompt_serverIP), Toast.LENGTH_LONG);
					return;
				}
				login(name, password);
			}
		});
		
		
		
		bottom_about.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {

				Intent intent = new Intent(LoginActivity.this, AboutActivity.class);
				startActivityForResult(intent, 1);
			}
		});

	}

	@Override
	protected void onDestroy() {
		if (progressDialog != null) {
			progressDialog.dismiss();
		}
		super.onDestroy();
	}
	
	private ProgressDialog createProgressDialog() {
		if (progressDialog == null) {
			progressDialog = new ProgressDialog(this);
			progressDialog.setTitle("系统提示");
			progressDialog.setMessage("登录中...");
		}

		return progressDialog;
	}

	
	private void closeDialog(){
		if (progressDialog != null) {
			progressDialog.dismiss();
		}
	}
	/**
	 * 登录
	 * 
	 * @param username
	 *            用户名
	 * @param password
	 *            密码
	 */
	private void login(String username, String password) {
		progressDialog = createProgressDialog();
		progressDialog.show();
		try {
			if(HttpUtil.netWorkIsOK(mContext)){
				loginSystem(username, password);
			}else{
				CommonUtil.showToast(mContext, "网络不可用，已经采用本地登陆！", Toast.LENGTH_LONG);
				loginLocal(username,password);
			}
		} catch (Exception e) {
			closeDialog();
		}
		
	}
	
	
	
	private void loginLocal(String username, String password){
		
		manager = new UserManager(LoginActivity.this);
		String condition = "user_name='" +username + "'";
		int userRecord = manager.getUserCount(condition);
		
		if(userRecord == 0){
			List<User> users = manager.getAllUserFromLocal();
			if(users.size() > 0){
				CommonUtil.showToast(mContext,"只能允许一个帐号登录本机！", Toast.LENGTH_LONG);
			} else {
				//loginSystem(username, password);
			}
		} else {
			Log.i("DBAccess", "本地加载。。");
			List<User> users = manager.getAllUserFromLocal();
			if(users != null && users.size() > 0){
			User user = users.get(0);
			if(user != null){
				
				if(username.equals(user.getUser_name()) && password.equals(user.getUser_password())){
					
			//		String ip =	sp.getString("serverIp", "");
					SharedPreferences.Editor editor = sp.edit();
					
					editor.putString("serverIp", serverIp);
					editor.putString("user", user.getUser_id());
					editor.putString("name", user.getUser_name());
					editor.putLong("classId", user.getClassId());
					
					editor.commit();
					
					Intent intent = new Intent(LoginActivity.this, MainActivity.class);
					startActivity(intent);
					closeDialog();
					finish();
				} else {
					CommonUtil.showToast(mContext,"密码错误！", Toast.LENGTH_LONG);
				}
				
			}
			}
		}
		
		closeDialog();
	}

	private void loginSystem(final String username, final String password) {

		Runnable runable = new Runnable() {

			@Override
			public void run() {
				manager = new UserManager(LoginActivity.this);
				UserInfo userInfo = null;
				try {
					userInfo = manager.login(webPath, username, password);
				} catch (Exception e) {
					Log.i("userInfouserInfo", "userInfo = "+userInfo);
				}

				Message msg = Message.obtain();
				msg.obj = userInfo;
				handleLogin.sendMessage(msg);

			}
		};

		Thread thread = new Thread(runable);
		thread.start();
	}

	// 异常返回结果消息
	Handler handleLogin = new Handler() {
		public void handleMessage(android.os.Message msg) {
			closeDialog();
			UserInfo userInfo = (UserInfo) msg.obj;

			if (progressDialog != null)
				progressDialog.hide();

			if (userInfo != null) {
				if(userInfo.getId()>0){
					//保存状态
					saveUserStauts(userInfo);
					//本地数据
					localInsert(userInfo);
	
					Intent intent = new Intent(LoginActivity.this, MainActivity.class);
					startActivity(intent);
					finish();
				}else{
					CommonUtil.showToast(mContext, userInfo.getName(), Toast.LENGTH_LONG);
					return;
				}
			} else {
				CommonUtil.showToast(mContext, "连接服务器失败，已经采用本地登陆！", Toast.LENGTH_LONG);
				loginLocal(name,password);
			}

		}

		private void saveUserStauts(UserInfo userInfo) {
			SharedPreferences.Editor editor = sp.edit();
			editor.putString("serverIp", serverIp);
			editor.putString("user", userInfo.getId().toString());
			editor.putString("name", userInfo.getName());
			editor.putLong("classId", userInfo.getClassId());
			
			editor.commit();
		}

		private void localInsert(UserInfo userInfo) {
				
				manager.deleteAllUser();//删除以前缓存的用户
				
				User user = new User();
				user.setUser_id(userInfo.getId().toString());
				user.setUser_name(userInfo.getName());
				user.setUser_password(userInfo.getPassword());
				user.setClassId(userInfo.getClassId());
				
				manager.insert(user);
		};
	};
	

}
