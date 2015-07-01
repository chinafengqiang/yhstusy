package com.feng.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.feng.util.BadgeView;
import com.smartlearning.R;
import com.smartlearning.biz.LessonManager;
import com.smartlearning.constant.Global;
import com.smartlearning.model.LessonVO;
import com.smartlearning.ui.FScheduleTempActivity;
import com.smartlearning.ui.TableView;
import com.smartlearning.utils.CommonUtil;
import com.smartlearning.utils.SpUtil;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ScheduleFragment extends Fragment{
	private View mBaseView;
	
	private Context context = null;
	private String ip = "";
	private String serverIp = "";
	private Long classId;
	private SharedPreferences sharedPreferences;
	private TableView table = null;
	private LinearLayout layout;
	private TextView txtTopTitle;
	private ProgressDialog pd = null;
	
	private List<LessonVO> lessonList = new ArrayList<LessonVO>();
	private LessonManager lessonManager;
	private HashMap<String,Integer> hasTempMap = new HashMap<String,Integer>();
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		context = getActivity();
		mBaseView = inflater.inflate(R.layout.f_schedule, null);
		findView();
		initSp();
		setListener();
		processLogic();
		return mBaseView;
	}
	
	private void initSp(){
		sharedPreferences = SpUtil.getSharePerference(context);
		ip = sharedPreferences.getString("serverIp", null);
		classId = sharedPreferences.getLong("classId", 0);
		serverIp = "http://"+ ip +":"+Global.Common_Port;
	}
	
	private void findView() {
		layout = (LinearLayout) mBaseView.findViewById(R.id.schduleLayout); 
		txtTopTitle = (TextView)mBaseView.findViewById(R.id.txtTopTitle);
	}
	
	private void setListener(){
		
	}
	
	private void processLogic() {
		new GetSchduleTask().execute(1);
	}
	
	private class GetSchduleTask extends AsyncTask<Integer, Integer, Integer>{

		String title = "";
		@Override
		protected Integer doInBackground(Integer... params) {
			lessonList.clear();
			lessonManager = new LessonManager();
			//List<LessonVO> list = lessonManager.getPermLessons(serverIp, classId);
			hasTempMap.clear();
			List<LessonVO> list = lessonManager.getPermLessons(serverIp, classId,hasTempMap);
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
				 txtTopTitle.setText(title);
			 table = new TableView(context, 9, 6, lessonList);
			 table.setServerIp(serverIp);
			 layout.addView(table);	
			 setBadge();
		}
		
	}

	
	private void setBadge(){
		 Integer tempId = hasTempMap.get("tempId");
		 BadgeView badge = null;
		 if(tempId != null && tempId > 0){
			 badge = new BadgeView(context, txtTopTitle);
			 badge.setText(R.string.has_temp);
			 badge.setTextColor(Color.WHITE);
			 badge.setTextSize(20);
			 badge.setBadgeBackgroundColor(Color.RED);
			 badge.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
			 TranslateAnimation anim = new TranslateAnimation(-100, 0, 0, 0);
		     anim.setInterpolator(new BounceInterpolator());
		     anim.setDuration(1000);
			 badge.show(anim);
			 badge.setOnClickListener(badgeListener);
		 }
	}
	
	private OnClickListener badgeListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			 Integer tempId = hasTempMap.get("tempId");
			 if(tempId != null && tempId > 0){
				 Intent intent = new Intent(context,FScheduleTempActivity.class);
				 intent.putExtra("tempId",tempId);
				 getActivity().startActivity(intent);
				 getActivity().overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);  	
			 }
		}
	};
	
	
	private class GetSchduleTempTask extends AsyncTask<Integer, Integer, Integer>{
		String title = "";
		@Override
		protected Integer doInBackground(Integer... params) {
			lessonList.clear();
			lessonManager = new LessonManager();
			hasTempMap.clear();
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
				 txtTopTitle.setText(title);
			 table = new TableView(context, 9, 6, lessonList);
			 table.setServerIp(serverIp);
			 layout.addView(table);	
		}
		
	}

	
	/**
	 * 提标框
	 */
	private void  showProgressDialog(){
		if (pd==null){
			pd = new ProgressDialog(context);
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
