package com.smartlearning.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.smartlearning.R;

/**
 *内容显示
 */
public class MessageShowActivity extends Activity {

	TextView inform_show_title, inform_show_content;
	Button inform_show_back;

	String content;// 获取内容
	String name;
	
	int message_id = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_message_show);
		
		Intent it = getIntent();
		Bundle bundle = it.getExtras();
		message_id = bundle.getInt("message_id");
		name = bundle.getString("name");
		content = bundle.getString("content");

		inform_show_title = (TextView) findViewById(R.id.inform_show_title);
		inform_show_content = (TextView) findViewById(R.id.inform_show_content);
		inform_show_back = (Button) findViewById(R.id.inform_show_back);

		inform_show_title.setText(name);
		inform_show_content.setText(content);

		/**
		 * 返回
		 */
		inform_show_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();

			}
		});
		
	}

}
