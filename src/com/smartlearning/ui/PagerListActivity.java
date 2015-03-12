package com.smartlearning.ui;

import com.smartlearning.R;
import com.smartlearning.fragment.BookFragment;
import com.smartlearning.fragment.BookFragment.PagerCallback;
import com.smartlearning.utils.CommonUtil;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class PagerListActivity extends FragmentActivity implements PagerCallback{
	private Context mContext;
	private BookCallback bookCallback;
	private long category;
	private String categoryName;
	private static final int PAGESIZE = 10; // 每次取几条记录
	private int pageIndex = 1; // 用于保存当前是第几页,0代表第一页
	private Button btnFirst,btnPrev,btnNext,btnLast;
	private int totalPage = 1;
	private boolean loadDataFromType = false;
	
	public interface BookCallback {
		public void setCategory(long category,String name);
		
		public void setCurrentPage(int pageIndex);
		
		public void setLoadDataType(boolean loadFromServer);
	}

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		mContext = this;
		setContentView(R.layout.book_list);
		findViewById();
		processLogic();
		setListener();
	}

	
	private void findViewById(){
		btnFirst = (Button)findViewById(R.id.btnFirst);
		btnPrev = (Button)findViewById(R.id.btnPrev);
		btnNext = (Button)findViewById(R.id.btnNext);
		btnLast = (Button)findViewById(R.id.btnLast);
	}
	
	private void setListener(){
		btnFirst.setOnClickListener(btnFirstListener);
		btnPrev.setOnClickListener(btnPrevListener);
		btnNext.setOnClickListener(btnNextListener);
		btnLast.setOnClickListener(btnLastListener);
	}

	private OnClickListener btnFirstListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if(pageIndex == 1){
				
			}else{
				pageIndex = 1;
				gotoBookFragment();
			}
			
		}
	};
	
	private OnClickListener btnPrevListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			pageIndex--;
			if(pageIndex <= 0){
				pageIndex = 1;
				CommonUtil.showToast(mContext,"已翻到首页",Toast.LENGTH_SHORT);
			}else{
				gotoBookFragment();
			}
		}
	};
	
	private OnClickListener btnNextListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			pageIndex++;
			if(pageIndex > totalPage){
				pageIndex = totalPage;
				CommonUtil.showToast(mContext,"已翻到尾页",Toast.LENGTH_SHORT);
			}else{
				gotoBookFragment();
			}
		}
	};
	
	private OnClickListener btnLastListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if(pageIndex == totalPage){
				
			}else{
				pageIndex = totalPage;
				gotoBookFragment();
			}
		}
	};
	
	private void processLogic(){
		category = getIntent().getLongExtra("category",0);
		categoryName = getIntent().getStringExtra("categoryName");
		gotoBookFragment();
		//bookCallback.setCategory(category);
	}
	
	private void gotoBookFragment(){
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		BookFragment bookFragment = new BookFragment();
		ft.replace(R.id.fl_content,bookFragment,"BookFragment");
		ft.commit();
	}
	
	@Override
	public void onAttachFragment(Fragment fragment) {
		try {
			bookCallback = (BookCallback) fragment;
			bookCallback.setCategory(category,categoryName);
			bookCallback.setCurrentPage(pageIndex);
			bookCallback.setLoadDataType(this.loadDataFromType);
		} catch (Exception e) {
			throw new ClassCastException(this.toString()
					+ " must implement BookCallback");
		}
		super.onAttachFragment(fragment);
	}


	@Override
	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}


	@Override
	public void setLoadDataType(boolean loadFromServer) {
		this.loadDataFromType = loadFromServer;
	}

	
	
}
