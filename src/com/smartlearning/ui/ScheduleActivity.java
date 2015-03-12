package com.smartlearning.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.smartlearning.R;
import com.smartlearning.biz.BookManager;
import com.smartlearning.biz.LessonManager;
import com.smartlearning.constant.Global;
import com.smartlearning.model.LessonVO;

public class ScheduleActivity extends Activity {
	private ProgressDialog pd = null;
	private ListView lv = null;
	private ArrayList<HashMap<String, String>> list;
	private Button detail_return_btn = null;
	private Button detail_home_btn = null;
	private Context context = null;
	String ip = "";
	String serverIp = "";
	Long classId;
	private SharedPreferences sharedPreferences;
	private List<LessonVO> lessonList = new ArrayList<LessonVO>();
	private LessonManager lessonManager;
	TableView table = null;
	LinearLayout layout;
	private TextView txtTopTitle;
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_schedule);
		context = this;
		detail_return_btn = (Button) this.findViewById(R.id.detail_return_btn);
		detail_home_btn = (Button) this.findViewById(R.id.detail_home_btn);
		
		sharedPreferences = getSharedPreferences("userInfo", MODE_PRIVATE);
		ip = sharedPreferences.getString("serverIp", null);
		classId = sharedPreferences.getLong("classId", 0);
		serverIp = "http://"+ ip +":"+Global.Common_Port;
		
		layout = (LinearLayout) findViewById(R.id.schduleLayout); 
		txtTopTitle = (TextView)findViewById(R.id.txtTopTitle);
		//title上的返回按钮
	    detail_return_btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
				
			}
		});
	    //title上的回家按钮
	    detail_home_btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context,CoursePlanActivity.class);
				startActivity(intent);
				
			}
		});
	    
//	    try{
//	    	table = new TableView(this, 9, 6, classId, serverIp);
//	    }catch(Exception ex){
//			Toast.makeText(this, "网络连接出现问题，请检查！", Toast.LENGTH_LONG).show();
//		}
//	
//		layout.addView(table);	
		
		new GetSchduleTask().execute(1);
	}
	
	private class GetSchduleTask extends AsyncTask<Integer, Integer, Integer>{

		String title = "";
		@Override
		protected Integer doInBackground(Integer... params) {
			lessonList.clear();
			lessonManager = new LessonManager();
			List<LessonVO> list = lessonManager.getPermLessons(serverIp, classId);
			if(list != null){
				title = list.get(0).getLname();
				lessonList.addAll(list);
			}
			return 1;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showProgressDialog();
		}

		@Override
		protected void onPostExecute(Integer result) {
			hideProgressDialog();
			 if(title != null && !"".equals(title))
				 txtTopTitle.setText(title);
			 table = new TableView(context, 9, 6, lessonList);
			 layout = (LinearLayout) findViewById(R.id.schduleLayout); 
			 layout.addView(table);	
		}
		
	}

	
	@Override
	protected void onDestroy() {
		hideProgressDialog();
		super.onDestroy();
	}
	
	/**
	 * 提标框
	 */
	private void  showProgressDialog(){
		if (pd==null){
			pd = new ProgressDialog(this);
			pd.setTitle("系统提示");
			pd.setMessage("数据获取中,请稍候...");
		}
		pd.show();
	}
	
	/**
	 * 隐藏
	 */
	private void hideProgressDialog(){
		if (pd!=null) pd.dismiss();
	}
	
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		return super.onCreateOptionsMenu(menu);
	}

}
