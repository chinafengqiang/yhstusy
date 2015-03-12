package com.smartlearning.ui;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.smartlearning.R;
import com.smartlearning.common.LineGridView;
import com.smartlearning.db.DBAccess;
import com.smartlearning.model.TestPaperQuestion;

public class examSummary extends Activity {

	private LineGridView gv = null;
	ProgressDialog pd = null;
	ArrayList<TestPaperQuestion> listObj = null;
	private String[] list;
	int testPaperId = 0;
	private SharedPreferences sharedPreferences;
	private ImageView imgBack;// 返回键
	/**
	 * 提标框
	 */
	private void showProgressDialog() {
		if (pd == null) {
			pd = new ProgressDialog(this);
			pd.setTitle("系统提示");
			pd.setMessage("数据获取中,请稍候...");
		}
		pd.show();
	}

	/**
	 * 隐藏
	 */
	private void hideProgressDialog() {
		if (pd != null)
			pd.dismiss();
	}

	@Override
	protected void onDestroy() {
		hideProgressDialog();
		super.onDestroy();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.exam_summary);
		sharedPreferences = getSharedPreferences("userInfo", MODE_PRIVATE);
		initData();
		
		imgBack = (ImageView) findViewById(R.id.imgBack);
		imgBack.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				finish();

			}
		});
	}

	private void initData() {
		gv = (LineGridView) this.findViewById(R.id.exam_summary_grid);
		int qNO = 0;
		if (getIntent().getIntExtra("questionNO", 0)!=0)
			qNO  = getIntent().getIntExtra("questionNO", 0);
		if (getIntent().getIntExtra("categoryId", 0)!=0)
			testPaperId = getIntent().getIntExtra("categoryId", 0);
		boolean[] rcValue = DBAccess.getRwMatrix(examSummary.this,String.valueOf(testPaperId));
		Log.i("DBAccess", "DBAccess --rcValue"+rcValue);
		gv.setAdapter(new BaseInfoAdapter(this, qNO, rcValue));
		// 添加消息处理
		gv.setOnItemClickListener(new ItemClickListener());
		
		
	}

	//当AdapterView被单击(触摸屏或者键盘)，则返回的Item单击事件
	class ItemClickListener implements OnItemClickListener {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				
				int qIndex = Integer.parseInt(list[arg2]);
				Intent intent = new Intent();
				intent.putExtra("questionIndex", qIndex);
				intent.putExtra("categoryId", testPaperId);
				intent.setClass(examSummary.this, TestPaperQuestionByUseActivity.class);
				startActivity(intent);
				
			}
	}
		
	class BaseInfoAdapter extends BaseAdapter {

		Context context;
		private LayoutInflater mInflater;
		private int questionNO;
		private boolean[] rcValue;

		public BaseInfoAdapter(Context context, int qNO, boolean[] rcValue) {
			this.context = context;
			list = new String[qNO];
			this.questionNO = qNO;
			this.rcValue = rcValue;
			
			for(int i = 0; i < qNO ; i ++){
				list[i] = i + 1 + "";
			}
			
			mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return questionNO;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolder holder;

			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(
						R.layout.exam_summary_grid_item, null);
				holder.tv = (TextView) convertView.findViewById(R.id.name);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			if(rcValue[position]){
				holder.tv.setBackgroundColor(Color.parseColor("#ffadadad"));
			}else{
				holder.tv.setBackgroundColor(Color.parseColor("#ffffff"));
			}
			
			holder.tv.setText(list[position]);

			return convertView;
		}

		public final class ViewHolder {
			public TextView tv;
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

}