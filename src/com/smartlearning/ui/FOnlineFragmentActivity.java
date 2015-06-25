package com.smartlearning.ui;

import com.smartlearning.R;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FOnlineFragmentActivity extends FragmentActivity{
	private LinearLayout title;
	private TextView titleText;
	private Context mContext;
	private LinearLayout title_cfm;
	private LinearLayout pre_ll;
	private LinearLayout next_ll;
	private TextView preTv;
	private TextView nextTv;
	private Button btn_send;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.f_online);
		
		this.mContext = this;
		
		initView();
		
		initTitle();

		setCurPoint(0);
	}
	
	private void initView() {
		
		btn_send = (Button) this.findViewById(R.id.btn_send);
		pre_ll = (LinearLayout)findViewById(R.id.pre_ll);
		next_ll = (LinearLayout)findViewById(R.id.next_ll);
		preTv = (TextView)findViewById(R.id.preTv);
		nextTv = (TextView)findViewById(R.id.nextTv);
	}
	
	private void initTitle(){
		title = (LinearLayout)findViewById(R.id.title_back);
		titleText = (TextView)findViewById(R.id.title_text);
		title_cfm = (LinearLayout)findViewById(R.id.title_cfm);
		titleText.setText(R.string.online_title);
		title.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				FOnlineFragmentActivity.this.finish();
			}
		});
		
		title_cfm.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				btn_send.setText(R.string.send_msg);
			}
		});
		
	}
	
	private void setCurPoint(int index){
		if(index == 0){
			pre_ll.setEnabled(false);
			next_ll.setEnabled(true);
			preTv.setTextColor(0xff228B22);
			nextTv.setTextColor(Color.BLACK);
		}else{
			pre_ll.setEnabled(true);
			next_ll.setEnabled(false);
			preTv.setTextColor(Color.BLACK);
			nextTv.setTextColor(0xff228B22);
		}
	}
}
