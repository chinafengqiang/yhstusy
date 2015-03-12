package com.smartlearning.ui;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.smartlearning.R;

public class VersionActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);  
		getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN , WindowManager.LayoutParams. FLAG_FULLSCREEN);  
		setContentView(R.layout.activity_version);
		
		TextView txtNo = (TextView) findViewById(R.id.versionNo);
		txtNo.setText("V"+getVersion());
	
	}
		

	/**
	 * 获取版本号
	 * @return 当前应用的版本号
	 */
	public String getVersion() {
	    try {
	        PackageManager manager = this.getPackageManager();
	        PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
	        String version = info.versionName;
	        Log.i("version", "version==="+version);
	        return version;
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
		return "";
	}
}
