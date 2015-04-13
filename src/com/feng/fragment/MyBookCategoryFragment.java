package com.feng.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.Request.Method;
import com.feng.adapter.BookCategoryAdapter;
import com.feng.vo.BookCategory;
import com.feng.vo.BookCategoryListVO;
import com.feng.vo.BookPart;
import com.feng.vo.BookRes;
import com.feng.volley.FRestClient;
import com.feng.volley.FastJsonRequest;
import com.smartlearning.R;
import com.smartlearning.biz.BookManager;
import com.smartlearning.constant.Global;
import com.smartlearning.utils.CommonUtil;
import com.smartlearning.utils.SpUtil;

public class MyBookCategoryFragment extends Fragment{
	public static final String TAG = OnlineBookCategoryFragment.class.getSimpleName();
	private View mBaseView;
	private Context mContext;
	private SharedPreferences sp;
	private int userId;
	private GridView bookCategoryGrid;
	private ProgressDialog pd;
	private BookManager bookManager = null;
	
	private List<BookCategory> categoryList = null;
	
	
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
		String strUserId = sp.getString("user","0");
		userId = Integer.parseInt(strUserId);
	}
	
	private void findView(){
		bookCategoryGrid = (GridView)mBaseView.findViewById(R.id.bookCategoryGrid);
	}
	
	private void initGrid(){
		new GetLocalBookRes().execute(true);
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
	
	private class GetLocalBookRes extends AsyncTask<Boolean,Integer,Boolean>{

		@Override
		protected Boolean doInBackground(Boolean... params) {
			bookManager = new BookManager(mContext);
			categoryList = bookManager.getBookCategory(userId);
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
			if(categoryList == null||categoryList.size() == 0){
				CommonUtil.showToast(mContext, getString(R.string.local_book_res_isnull), Toast.LENGTH_LONG);
			}else{
				BookCategoryAdapter adapter = new BookCategoryAdapter(mContext,categoryList);
				bookCategoryGrid.setAdapter(adapter);
			}
		}
		
	}
	
	


	
}
