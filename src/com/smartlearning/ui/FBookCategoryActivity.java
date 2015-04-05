package com.smartlearning.ui;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import com.feng.vo.BookCategory;
import com.feng.vo.BookCategoryListVO;
import com.feng.volley.FRestClient;
import com.feng.volley.FastJsonRequest;
import com.smartlearning.R;
import com.smartlearning.constant.Global;
import com.smartlearning.utils.CommonUtil;
import com.smartlearning.utils.SpUtil;

public class FBookCategoryActivity  extends Activity{
	private static final String TAG = FBookCategoryActivity.class.getSimpleName();
	private Context mContext;
	private SharedPreferences sp;
	private String serverIp;
	private long classId;
	@InjectView(R.id.bookCategoryGrid) GridView gridView;
	@InjectView(R.id.title_back) LinearLayout title;
	@InjectView(R.id.title_text) TextView titleText;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		
		setContentView(R.layout.f_book_category);
		
		ButterKnife.inject(this);
		
		initTitle();
		
		initSp();
		
		initGrid();
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
	}
	
	private void initGrid(){
		final ProgressDialog pDialog = new ProgressDialog(mContext);
		pDialog.setMessage("Loading...");
		pDialog.show(); 
		
		String tag_json_obj = "json_obj_req";
		String url = "http://"+ serverIp +":"+Global.Common_Port+"/api/getBookCategory.html?classId="+classId;
		
		
		
		FastJsonRequest<BookCategoryListVO>   fastRequest = new FastJsonRequest<BookCategoryListVO>(Method.GET,url, BookCategoryListVO.class,null, new Response.Listener<BookCategoryListVO>() {

			@Override
			public void onResponse(BookCategoryListVO categoryVO) {
				pDialog.dismiss();
				if(categoryVO != null){
					BookCategoryAdapter adapter = new BookCategoryAdapter(mContext,categoryVO.getBookCategoryList());
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
