package com.smartlearning.ui;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;

import com.android.volley.Request.Method;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.feng.adapter.BookCategoryAdapter;
import com.feng.fragment.MyBookCategoryFragment;
import com.feng.fragment.OnlineBookCategoryFragment;
import com.feng.vo.BookCategory;
import com.feng.vo.BookCategoryListVO;
import com.feng.volley.FRestClient;
import com.feng.volley.FastJsonRequest;
import com.smartlearning.R;
import com.smartlearning.constant.Global;
import com.smartlearning.utils.CommonUtil;
import com.smartlearning.utils.SpUtil;

public class FBookCategoryActivity  extends FragmentActivity{
	private static final String TAG = FBookCategoryActivity.class.getSimpleName();
	private Context mContext;
	private SharedPreferences sp;
	private String serverIp;
	private long classId;
	
	@InjectView(R.id.title_back) LinearLayout title;
	@InjectView(R.id.title_text) TextView titleText;
	@InjectView(R.id.myRes_ll) LinearLayout myRes_ll;
	@InjectView(R.id.onlineRes_ll) LinearLayout onlineRes_ll;
	@InjectView(R.id.onlineRes) TextView onlineRes;
	@InjectView(R.id.myRes) TextView myRes;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		
		setContentView(R.layout.f_book_category);
		
		ButterKnife.inject(this);
		
		initSp();
		
		initTitle();
		
		//initGrid();
	}
	
	private void initSp(){
		sp = SpUtil.getSharePerference(mContext);
		serverIp = sp.getString("serverIp","");
		classId = sp.getLong("classId",0);
	}
	
	private void initTitle(){
		titleText.setText(R.string.jxzl_book_category_title);
		title.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		
		setCurPoint(0);
		
		myRes_ll.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				setCurPoint(1);
				
				setStatus(1);
				
				FragmentManager fm = getSupportFragmentManager();
				FragmentTransaction ft = fm.beginTransaction();
				MyBookCategoryFragment mfg = new MyBookCategoryFragment();
				ft.replace(R.id.book_category_content,mfg,"MyBookCategoryFragment");
				ft.commit();
			}
		});
		
		onlineRes_ll.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				setCurPoint(0);
				
				setStatus(0);
				
				FragmentManager fm = getSupportFragmentManager();
				FragmentTransaction ft = fm.beginTransaction();
				OnlineBookCategoryFragment bookFragment = new OnlineBookCategoryFragment();
				ft.replace(R.id.book_category_content,bookFragment,"OnlineBookCategoryFragment");
				ft.commit();
			}
		});
		
		onlineRes_ll.performClick();
	}
	
	private void setCurPoint(int index){
		if(index == 0){
			onlineRes_ll.setEnabled(false);
			myRes_ll.setEnabled(true);
			onlineRes.setTextColor(0xff228B22);
			myRes.setTextColor(Color.BLACK);
		}else{
			myRes_ll.setEnabled(false);
			onlineRes_ll.setEnabled(true);
			myRes.setTextColor(0xff228B22);
			onlineRes.setTextColor(Color.BLACK);
		}
	}
	
	private void setStatus(int index){
		SharedPreferences.Editor editor = sp.edit();
		editor.putInt("book_is_local", index);
		editor.commit();
	}
	
}
