package com.feng.view;

import com.smartlearning.R;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class SearchBarView extends RelativeLayout{
	
	private Context mContext;
	private EditText etSearch;
	private Button btnSearch;
	private ImageView ivDeleteText;
	private RelativeLayout deleteTextRl;
	
	public SearchBarView(Context context){
		super(context);
		mContext=context;
		initView();
	}
	
	public SearchBarView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext=context;
		initView();
	}
	
	private void initView(){
		LayoutInflater.from(mContext).inflate(R.layout.f_search, this);
		etSearch = (EditText)findViewById(R.id.etSearch);
		btnSearch = (Button)findViewById(R.id.btnSearch);
		ivDeleteText = (ImageView)findViewById(R.id.ivDeleteText);
		deleteTextRl = (RelativeLayout)findViewById(R.id.deleteTextRl);
	   ivDeleteText.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				etSearch.setText("");
			}
		});
	   
	   deleteTextRl.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				etSearch.setText("");
			}
		});
	   
	   etSearch.addTextChangedListener(new TextWatcher() {
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void afterTextChanged(Editable s) {
			if (s.length() == 0) {
				ivDeleteText.setVisibility(View.GONE);
			} else {
				ivDeleteText.setVisibility(View.VISIBLE);
			}
		}
	});
	   
	}
	
	 public void setBtnSearchOnClickListener(OnClickListener listener){
		 btnSearch.setOnClickListener(listener);
	 }
	 

	 
	 public String getSearchValue(){
		 return etSearch.getText().toString().trim();
	 }
	
}
