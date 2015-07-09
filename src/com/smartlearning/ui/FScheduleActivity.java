package com.smartlearning.ui;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.Request.Method;
import com.feng.adapter.VideoResAdapter;
import com.feng.fragment.LessonMessageFragment;
import com.feng.fragment.ScheduleFragment;
import com.feng.fragment.SchedulePlanFragment;
import com.feng.util.BadgeView;
import com.feng.vo.LessonMsgCountVO;
import com.feng.vo.VideoResListVO;
import com.feng.volley.FRestClient;
import com.feng.volley.FastJsonRequest;
import com.smartlearning.R;
import com.smartlearning.constant.Global;
import com.smartlearning.utils.CommonUtil;
import com.smartlearning.utils.SpUtil;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.BounceInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
		
		processLogic();
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
	
	private void processLogic(){
		setLessonMessageBagdeView();
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
			setButton(v);
			FragmentManager fm = getSupportFragmentManager();
			FragmentTransaction ft = fm.beginTransaction();
			SchedulePlanFragment schedulePlanFragment = new SchedulePlanFragment();
			ft.replace(R.id.schedule_content,schedulePlanFragment,"SchedulePlanFragment");
			ft.addToBackStack(null);
			ft.commit();
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
			
			if(badge != null){
				badge.hide();
			}
			
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
	
	private void setLessonMessageBagdeView(){
		String tag_json_obj = "json_obj_req";
		String url = "http://" + serverIp + ":" + Global.Common_Port+"/api/getLessonMessageCount.html?classId="+classId;
		FastJsonRequest<LessonMsgCountVO>   fastRequest = new FastJsonRequest<LessonMsgCountVO>(Method.GET,url, LessonMsgCountVO.class,null, new Response.Listener<LessonMsgCountVO>() {

			@Override
			public void onResponse(LessonMsgCountVO resVO) {
				if(resVO != null && resVO.getLessonMessageCount() > 0){
					setBadgeView(jxtz,resVO.getLessonMessageCount()+"");
				}

			}
		},
		new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				
			}
		}
	    );
		
		FRestClient.getInstance(mContext).addToRequestQueue(fastRequest,tag_json_obj);
	}
	
	BadgeView badge = null;
	private void setBadgeView(View obj,String text){
		 badge = new BadgeView(mContext, obj);
		 badge.setText(text);
		 badge.setTextColor(Color.WHITE);
		 badge.setTextSize(20);
		 badge.setBadgeBackgroundColor(Color.RED);
		 badge.setBadgePosition(BadgeView.POSITION_CENTER);
		 /*TranslateAnimation anim = new TranslateAnimation(-100, 0, 0, 0);
	     anim.setInterpolator(new BounceInterpolator());
	     anim.setDuration(1000);
		 badge.show(anim);*/
		 badge.show();
	}
}
