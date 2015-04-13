package com.feng.fragment;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.Request.Method;
import com.feng.adapter.BookCategoryAdapter;
import com.feng.vo.BookCategoryListVO;
import com.feng.volley.FRestClient;
import com.feng.volley.FastJsonRequest;
import com.smartlearning.R;
import com.smartlearning.constant.Global;
import com.smartlearning.utils.CommonUtil;
import com.smartlearning.utils.SpUtil;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;

public class OnlineBookCategoryFragment extends Fragment{
	public static final String TAG = OnlineBookCategoryFragment.class.getSimpleName();
	private View mBaseView;
	private Context mContext;
	private SharedPreferences sp;
	private String serverIp;
	private long classId;
	private GridView bookCategoryGrid;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContext = getActivity();
		mBaseView = inflater.inflate(R.layout.f_book_category_fragment, null);
		
		initSp();
		
		findView();
		
		initGrid();
		
		return mBaseView;
	}
	
	private void initSp(){
		sp = SpUtil.getSharePerference(mContext);
		serverIp = sp.getString("serverIp","");
		classId = sp.getLong("classId",0);
	}
	
	private void findView(){
		bookCategoryGrid = (GridView)mBaseView.findViewById(R.id.bookCategoryGrid);
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
					bookCategoryGrid.setAdapter(adapter);
				}
					
			}

			
		},
		new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				// TODO Auto-generated method stub
				 VolleyLog.d(TAG, "Error: " + error.getMessage());
				 //获取本地分类
				 CommonUtil.showToast(mContext, getString(R.string.get_online_category_fail),Toast.LENGTH_LONG);
				 pDialog.dismiss();
			}
			
		}
	    );
		
		FRestClient.getInstance(mContext).addToRequestQueue(fastRequest,tag_json_obj);

	}
}
