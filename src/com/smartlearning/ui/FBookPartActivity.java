package com.smartlearning.ui;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.Request.Method;
import com.feng.adapter.BookCategoryAdapter;
import com.feng.adapter.BookPartAdapter;
import com.feng.vo.BookCategoryListVO;
import com.feng.vo.BookPart;
import com.feng.vo.BookPartListVO;
import com.feng.volley.FRestClient;
import com.feng.volley.FastJsonRequest;
import com.smartlearning.R;
import com.smartlearning.biz.BookManager;
import com.smartlearning.constant.Global;
import com.smartlearning.utils.CommonUtil;
import com.smartlearning.utils.SpUtil;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
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
	private int islocal = 0;
	
	private BookManager bookManager = null;
	List<BookPart> partList = null;
	
	private ProgressDialog pd;
	
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
		islocal = sp.getInt("book_is_local",0);
		
		categoryId = getIntent().getIntExtra("categoryId",0);
		categoryName = getIntent().getStringExtra("categoryName");
	}
	
	private void initTitle(){
		if(islocal == 1){
			titleText.setText(getString(R.string.local_book_index)+categoryName);
		}else{
			titleText.setText(categoryName);
		}
		title.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
	}
	
	
	/**
	 * 提标框
	 */
	private void showProgressDialog() {
		if (pd == null) {
			pd = new ProgressDialog(mContext);
			pd.setMessage("Loading...");
		}
		pd.show();
	}
	
	/**
	 * 隐藏
	 */
	private void hideProgressDialog() {
		if (pd != null)
			pd.dismiss();
	}
	
	private void initGrid(){
		if(islocal == 1){
			new GetLocalBookPart().execute(true);
		}else{
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
						BookPartAdapter adapter = new BookPartAdapter(mContext, partVO.getBookPartList(),categoryId,categoryName);
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
	
	
	private class GetLocalBookPart extends AsyncTask<Boolean,Integer,Boolean>{

		@Override
		protected Boolean doInBackground(Boolean... params) {
			bookManager = new BookManager(mContext);
			partList = bookManager.getBookPart(categoryId);
			return true;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showProgressDialog();
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			hideProgressDialog();
			if(partList == null || partList.size() == 0){
				CommonUtil.showToast(mContext, getString(R.string.local_book_part_isnull), Toast.LENGTH_LONG);
			}else{
				BookPartAdapter adapter = new BookPartAdapter(mContext,partList,categoryId,categoryName);
				gridView.setAdapter(adapter);
			}
		}
		
	}
}
