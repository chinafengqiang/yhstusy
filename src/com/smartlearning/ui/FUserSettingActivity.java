package com.smartlearning.ui;

import butterknife.ButterKnife;
import butterknife.InjectView;

import com.smartlearning.R;
import com.smartlearning.utils.SpUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class FUserSettingActivity extends Activity{

	private Context mContext;
	private SharedPreferences sp;
	
	private String name;
	private String truename;
	
	@InjectView(R.id.title_back) LinearLayout title;
	@InjectView(R.id.title_text) TextView titleText;
	@InjectView(R.id.username) TextView username;
	@InjectView(R.id.trueName) TextView trueName;
	@InjectView(R.id.exit_rl) RelativeLayout exit_rl;
	
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
	}
	
	private void initUserInfo(){
		username.setText(getString(R.string.username_index)+name);
		trueName.setText(getString(R.string.truename_index)+truename);
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
	}

}
