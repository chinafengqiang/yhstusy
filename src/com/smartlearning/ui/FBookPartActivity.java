package com.smartlearning.ui;

import butterknife.ButterKnife;
import butterknife.InjectView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.Request.Method;
import com.feng.adapter.BookCategoryAdapter;
import com.feng.adapter.BookPartAdapter;
import com.feng.vo.BookCategoryListVO;
import com.feng.vo.BookPartListVO;
import com.feng.volley.FRestClient;
import com.feng.volley.FastJsonRequest;
import com.smartlearning.R;
import com.smartlearning.constant.Global;
import com.smartlearning.utils.CommonUtil;
import com.smartlearning.utils.SpUtil;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class FBookPartActivity extends Activity{
	private static final String TAG = FBookPartActivity.class.getSimpleName();
	private Context mContext;
	private SharedPreferences sp;
	private String serverIp;
	private long classId;
	private int categoryId;
	private String categoryName;
	
	@InjectView(R.id.bookPartGrid) GridView gridView;
	@InjectView(R.id.title_back) LinearLayout title;
	@InjectView(R.id.title_text) TextView titleText;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mContext = this;
		
		setContentView(R.layout.f_book_part);
		
		ButterKnife.inject(this);
		
		initSp();
		
		initTitle();
		
		initGrid();
	}
	
	private void initSp(){
		sp = SpUtil.getSharePerference(mContext);
		serverIp = sp.getString("serverIp","");
		classId = sp.getLong("classId",0);
		
		categoryId = getIntent().getIntExtra("categoryId",0);
		categoryName = getIntent().getStringExtra("categoryName");
	}
	
	private void initTitle(){
		titleText.setText(categoryName);
		title.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
	}
	
	private void initGrid(){
		final ProgressDialog pDialog = new ProgressDialog(mContext);
		pDialog.setMessage("Loading...");
		pDialog.show(); 
		
		String tag_json_obj = "json_obj_req";
		String url = "http://"+ serverIp +":"+Global.Common_Port+"/api/getBookPart.html?classId="+classId+"&categoryId="+categoryId;

		FastJsonRequest<BookPartListVO>   fastRequest = new FastJsonRequest<BookPartListVO>(Method.GET,url, BookPartListVO.class,null, new Response.Listener<BookPartListVO>() {

			@Override
			public void onResponse(BookPartListVO partVO) {
				pDialog.dismiss();
				if(partVO != null){
					BookPartAdapter adapter = new BookPartAdapter(mContext, partVO.getBookPartList());
					gridView.setAdapter(adapter);
				}
			}
		},
		new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				// TODO Auto-generated method stub
				 VolleyLog.d(TAG, "Error: " + error.getMessage());
				 //获取本地分类
				 CommonUtil.showToast(mContext, "local category",Toast.LENGTH_SHORT);
				 pDialog.dismiss();
			}
		}
	    );
		
		FRestClient.getInstance(mContext).addToRequestQueue(fastRequest,tag_json_obj);
	}
}
