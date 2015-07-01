package com.smartlearning.ui;

import java.util.ArrayList;
import java.util.List;

import com.smartlearning.R;
import com.smartlearning.biz.LessonManager;
import com.smartlearning.constant.Global;
import com.smartlearning.model.LessonVO;
import com.smartlearning.utils.SpUtil;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FScheduleTempActivity extends Activity{
	private ProgressDialog pd = null;
	private Context mContext;
	private SharedPreferences sp;
	private String serverIp;
	private long classId;
	
	private LinearLayout title;
	private TextView titleText;
	private int tempId = 0;
	private List<LessonVO> lessonList = new ArrayList<LessonVO>();
	private LessonManager lessonManager;
	private TableView table = null;
	private LinearLayout layout;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		
		setContentView(R.layout.f_schedule_temp);
		
		findView();
		
		initSp();
		
		initTitle();
		
		setListener();
	}

	private void findView(){
		title = (LinearLayout)findViewById(R.id.title_back);
		titleText = (TextView)findViewById(R.id.title_text);
		layout = (LinearLayout) findViewById(R.id.schduleLayout); 
	}
	
	private void initSp(){
		sp = SpUtil.getSharePerference(mContext);
		String ip = serverIp = sp.getString("serverIp","");
		serverIp = "http://"+ ip +":"+Global.Common_Port;
		classId = sp.getLong("classId",0);
		
		tempId = getIntent().getIntExtra("tempId",0);
	}
	
	private void initTitle(){
		titleText.setText(R.string.sch_temp_title);
		
		title.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
	}
	
	private void setListener(){
		new GetSchduleTempTask().execute(tempId);
	}
	
	
	private class GetSchduleTempTask extends AsyncTask<Integer, Integer, Integer>{

		String title = "";
		@Override
		protected Integer doInBackground(Integer... params) {
			lessonList.clear();
			lessonManager = new LessonManager();
			List<LessonVO> list = lessonManager.getPermLessonsTemp(serverIp, params[0]);
			if(list != null&&list.size()>0){
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
				 titleText.setText(title);
			 table = new TableView(mContext, 9, 6, lessonList);
			 table.setServerIp(serverIp);
			 table.setIsTemp(true);
			 layout.addView(table);	
		}
		
	}

	
	/**
	 * 提标框
	 */
	private void  showProgressDialog(){
		if (pd==null){
			pd = new ProgressDialog(mContext);
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
	
}
