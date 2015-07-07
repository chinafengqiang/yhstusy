package com.smartlearning.ui;

import com.feng.fragment.LessonMessageFragment;
import com.feng.fragment.ScheduleFragment;
import com.smartlearning.R;
import com.smartlearning.utils.SpUtil;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FScheduleActivity extends FragmentActivity{
	private Context mContext;
	private SharedPreferences sp;
	private String serverIp;
	private long classId;
	
	private LinearLayout title;
	private TextView titleText;
	private TextView kc,jd,xqjh,jxtz;
	
	
	private View currentButton;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		
		setContentView(R.layout.f_schedule_main);
		
		findView();
		
		initSp();
		
		initTitle();
		
		setListener();
	}
	
	private void findView(){
		title = (LinearLayout)findViewById(R.id.title_back);
		titleText = (TextView)findViewById(R.id.title_text);
		
		kc = (TextView)findViewById(R.id.kc);
		jd = (TextView)findViewById(R.id.jd);
		xqjh = (TextView)findViewById(R.id.xqjh);
		jxtz = (TextView)findViewById(R.id.jxtz);
	}
	
	private void initSp(){
		sp = SpUtil.getSharePerference(mContext);
		serverIp = sp.getString("serverIp","");
		classId = sp.getLong("classId",0);
	}
	
	private void initTitle(){
		titleText.setText(R.string.sch_title);
		
		title.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//onBackPressed();
				Intent intent = new Intent(mContext,FMainActivity.class);
				startActivity(intent);
			}
		});
	}
	
	private void setListener(){
		kc.setOnClickListener(kcListener);
		kc.performClick();
		jd.setOnClickListener(jdListener);
		xqjh.setOnClickListener(xqjhListener);
		jxtz.setOnClickListener(jxtzListener);
	}
	
	private OnClickListener kcListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			setButton(v);
			FragmentManager fm = getSupportFragmentManager();
			FragmentTransaction ft = fm.beginTransaction();
			ScheduleFragment schFragment = new ScheduleFragment();
			ft.replace(R.id.schedule_content,schFragment,"ScheduleFragment");
			ft.addToBackStack(null);
			ft.commit();
		}
	};
	
	private OnClickListener jdListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			setButton(v);
		}
	};
	
	private OnClickListener xqjhListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			setButton(v);
		}
	};
	
	private OnClickListener jxtzListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			setButton(v);
			FragmentManager fm = getSupportFragmentManager();
			FragmentTransaction ft = fm.beginTransaction();
			LessonMessageFragment lessonMessageFragment = new LessonMessageFragment();
			ft.replace(R.id.schedule_content,lessonMessageFragment,"LessonMessageFragment");
			ft.addToBackStack(null);
			ft.commit();
		}
	};
	
	private void setButton(View v){
		if(currentButton!=null&&currentButton.getId()!=v.getId()){
			currentButton.setEnabled(true);
			TextView currentTextView = (TextView)currentButton;
			currentTextView.setTextColor(Color.BLACK);
		}
		v.setEnabled(false);
		TextView textView = (TextView)v;
		textView.setTextColor(0xff228B22);
		currentButton=v;
	}
}
