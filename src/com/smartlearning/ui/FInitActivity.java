package com.smartlearning.ui;

import com.smartlearning.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class FInitActivity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.f_init);
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Intent intent		=new Intent();
				intent.setClass(getApplicationContext(), FMainActivity.class);
				startActivity(intent);
			}
		}).start();
		
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		this.finish();
	}
	
	
}
