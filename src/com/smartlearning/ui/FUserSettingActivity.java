package com.smartlearning.ui;

import butterknife.ButterKnife;
import butterknife.InjectView;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.feng.vo.IntegerVO;
import com.feng.volley.FRestClient;
import com.feng.volley.FastJsonRequest;
import com.smartlearning.R;
import com.smartlearning.biz.UserManager;
import com.smartlearning.constant.Global;
import com.smartlearning.utils.CommonUtil;
import com.smartlearning.utils.SpUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class FUserSettingActivity extends Activity{

	private Context mContext;
	private SharedPreferences sp;
	
	private String name;
	private String truename;
	private int userId;
	private String serverIp;
	
	@InjectView(R.id.title_back) LinearLayout title;
	@InjectView(R.id.title_text) TextView titleText;
	@InjectView(R.id.username) TextView username;
	@InjectView(R.id.trueName) TextView trueName;
	@InjectView(R.id.version) TextView version;
	@InjectView(R.id.update_app_btn) Button update_app_btn;
	@InjectView(R.id.exit_rl) RelativeLayout exit_rl;
	@InjectView(R.id.old_password) EditText old_password;
	@InjectView(R.id.new_password) EditText new_password;
	@InjectView(R.id.cfg_new_password) EditText cfg_new_password;
	@InjectView(R.id.edit_pass_btn) Button edit_pass_btn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		
		mContext = this;
		
		setContentView(R.layout.f_user_setting);
		
		ButterKnife.inject(this);
		
		initTitle();
		
		initSp();
		
		initUserInfo();
		
		initVersion();
		
		setListener();
	}
	
	private void initTitle(){
		titleText.setText(getString(R.string.setting_user_title));
		title.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
	}
	
	private void initSp(){
		sp = SpUtil.getSharePerference(mContext);
		name = sp.getString("name","");
		truename = sp.getString("truename","");
		String userIdStr = sp.getString("user","0");
		userId = Integer.parseInt(userIdStr);
		serverIp = sp.getString("serverIp","");
	}
	
	private void initUserInfo(){
		username.setText(getString(R.string.username_index)+name);
		trueName.setText(getString(R.string.truename_index)+truename);
	}
	
	private void initVersion(){
		PackageManager pm = mContext.getPackageManager();
		String versionName = "";
		try {
			PackageInfo pi = pm.getPackageInfo(mContext.getPackageName(), 0);
			versionName = pi.versionName;
		} catch (Exception e) {
			// TODO: handle exception
		}
		version.setText(versionName);
		
	}
	
	private void setListener(){
		exit_rl.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder = new AlertDialog.Builder(mContext);  
				builder.setTitle(R.string.setting_exit).setMessage(getString(R.string.setting_exit_cfg)).setCancelable(false)
				.setPositiveButton(R.string.setting_exit_btn, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						SpUtil.removeSharedPerference(sp);
						Intent intent = new Intent(mContext,LoginActivity.class);
						startActivity(intent);
						finish();
					}
				}).setNegativeButton(R.string.contact_btn_cance,null).create().show();
			}
		});
		
		update_app_btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext,InitilizeActivity.class);
				startActivity(intent);
			}
		});
		
		edit_pass_btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String oldPass = old_password.getText().toString().trim();
				String newPass = new_password.getText().toString().trim();
				String cfgPass = cfg_new_password.getText().toString().trim();
				if(oldPass == null || oldPass.length() <= 0){
					CommonUtil.showToast(mContext,getString(R.string.old_pass_is_null),Toast.LENGTH_LONG);
					return;
				}
				if(newPass == null || newPass.length() <= 0){
					CommonUtil.showToast(mContext,getString(R.string.new_pass_is_null),Toast.LENGTH_LONG);
					return;
				}
				if(cfgPass == null || cfgPass.length() <= 0){
					CommonUtil.showToast(mContext,getString(R.string.cfg_pass_is_null),Toast.LENGTH_LONG);
					return;
				}
				if(!newPass.equals(cfgPass)){
					CommonUtil.showToast(mContext,getString(R.string.cfg_pass_is_error),Toast.LENGTH_LONG);
					return;
				}
				
				updatePass(oldPass, newPass);
			}
		});
	}
	
	private void updatePass(final String oldPass,final String newPass){
		final ProgressDialog pDialog = new ProgressDialog(mContext);
		pDialog.setMessage("Loading...");
		pDialog.show(); 
		
		String tag_json_obj = "json_obj_req";
		String url = "http://"+ serverIp +":"+Global.Common_Port+"/api/updateUserPass.html?userId="+userId+"&oldPass="+oldPass+"&newPass="+newPass;

		FastJsonRequest<IntegerVO>   fastRequest = new FastJsonRequest<IntegerVO>(Method.GET,url, IntegerVO.class,null, new Response.Listener<IntegerVO>() {

			@Override
			public void onResponse(IntegerVO vo) {
				pDialog.dismiss();
				if(vo != null){
					int status = vo.getStatus();
					if(200 == status){
						UserManager userManager = new UserManager(mContext);
						userManager.modifyPass(userId, newPass);
						CommonUtil.showToast(mContext,getString(R.string.update_pass_is_ok),Toast.LENGTH_SHORT);
					}
					else if(400 == status){
						CommonUtil.showToast(mContext,getString(R.string.user_not_exist),Toast.LENGTH_SHORT);
						return;
					}else if(401 == status){
						CommonUtil.showToast(mContext,getString(R.string.old_pass_error),Toast.LENGTH_SHORT);
						return;
					}else{
						CommonUtil.showToast(mContext,getString(R.string.update_pass_is_fail),Toast.LENGTH_SHORT);
						return;
					}
				}
			}
		},
		new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				 CommonUtil.showToast(mContext,getString(R.string.update_pass_is_fail),Toast.LENGTH_SHORT);
				 pDialog.dismiss();
			}
		}
	    );
		
		FRestClient.getInstance(mContext).addToRequestQueue(fastRequest,tag_json_obj);
	}

}
