package com.smartlearning.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;

import com.feng.adapter.BookCategoryAdapter;
import com.smartlearning.R;

public class FBookCategoryActivity  extends Activity{
	private Context mContext;
	@InjectView(R.id.MainActivityGrid) GridView gridView;
	@InjectView(R.id.title_back) LinearLayout title;
	@InjectView(R.id.title_text) TextView titleText;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		
		setContentView(R.layout.f_book_category);
		
		ButterKnife.inject(this);
		
		initTitle();
		
		initGrid();
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
		BookCategoryAdapter adapter = new BookCategoryAdapter(mContext);
		gridView.setAdapter(adapter);
	}

}
