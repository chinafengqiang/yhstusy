package com.smartlearning.ui;

import android.app.ActivityGroup;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.smartlearning.R;
import com.smartlearning.common.SlidingMenuView;

/**
 * 关于下载
 * @author Administrator
 */
public class AboutActivity extends ActivityGroup {

	SlidingMenuView slidingMenuView;

	ViewGroup tabcontent;

	private Button btnReturnPage = null;
	private Context context = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about_file);
		slidingMenuView = (SlidingMenuView) findViewById(R.id.sliding_menu_view);
		context = this;
		tabcontent = (ViewGroup) slidingMenuView.findViewById(R.id.sliding_body);

//		btnReturnPage = (Button) this.findViewById(R.id.btnReturnPage);
//		btnReturnPage.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				Intent intent = new Intent(context, MainActivity.class);
//				startActivity(intent);
//			}
//		});

		showDefaultTab();
	}

//	public void hideMenu(View view) {
//		slidingMenuView.scrollRight();
//	}
//
//	public void showMenu(View view) {
//		slidingMenuView.scrollLeft();
//	}
//
//	public void changeTab1(View view) {
//		Intent i = new Intent(this, TeachingMessageActivity.class);
//		View v = getLocalActivityManager().startActivity(AboutActivity.class.getName(), i).getDecorView();
//		tabcontent.removeAllViews();
//		tabcontent.addView(v);
//	}
//
	public void changeTab2(View view) {
		Intent i = new Intent(this, VersionActivity.class);
		View v = getLocalActivityManager().startActivity(AboutActivity.class.getName(), i).getDecorView();
		tabcontent.removeAllViews();
		tabcontent.addView(v);
	}

	public void changeTab3(View view) {
		Intent i = new Intent(this, ToolDownActivity.class);
		View v = getLocalActivityManager().startActivity(AboutActivity.class.getName(), i).getDecorView();
		tabcontent.removeAllViews();
		tabcontent.addView(v);
	}

	void showDefaultTab() {
		Intent i = new Intent(this, ToolDownActivity.class);
		View v = getLocalActivityManager().startActivity(AboutActivity.class.getName(), i).getDecorView();
		tabcontent.removeAllViews();
		tabcontent.addView(v);
	}

}
