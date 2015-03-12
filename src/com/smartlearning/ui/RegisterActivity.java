package com.smartlearning.ui;

import com.smartlearning.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class RegisterActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);  
		//ȥ��Activity�����״̬��
		getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN , WindowManager.LayoutParams. FLAG_FULLSCREEN);  
		setContentView(R.layout.activity_register);
		
		Button btnSave = (Button)findViewById(R.id.btnSave);
		btnSave.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				try {
					Intent intent = new Intent(RegisterActivity.this,
							MainActivity.class);					
					startActivityForResult(intent, 1);
					
				} catch (Exception ex) {
					System.out.println(ex.getMessage());
				}
			}
		});
		
		Button btnCancel = (Button)findViewById(R.id.btnCancel);
		btnCancel.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				try {
					Intent intent = new Intent(RegisterActivity.this,
							LoginActivity.class);					
					startActivityForResult(intent, 1);
					
				} catch (Exception ex) {
					System.out.println(ex.getMessage());
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
