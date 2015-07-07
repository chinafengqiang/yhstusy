package com.feng.fragment;

import java.util.ArrayList;
import java.util.List;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.Request.Method;
import com.feng.adapter.BookResAdapter;
import com.feng.adapter.LessonMessageAdapter;
import com.feng.vo.BookResListVO;
import com.feng.vo.LessonMessageListVO;
import com.feng.vo.LessonMessageVO;
import com.feng.volley.FRestClient;
import com.feng.volley.FastJsonRequest;
import com.smartlearning.R;
import com.smartlearning.constant.Global;
import com.smartlearning.utils.CommonUtil;
import com.smartlearning.utils.SpUtil;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

public class LessonMessageFragment extends Fragment{
	private View mBaseView;
	private Context context = null;
	private String ip = "";
	private String serverIp = "";
	private Long classId;
	
	private ListView listView;
	private LessonMessageAdapter msgAdapter;
	
	
	private SharedPreferences sharedPreferences;
	private ProgressDialog pd = null;
	private List<LessonMessageVO> msgList = new ArrayList<LessonMessageVO>();
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		context = getActivity();
		mBaseView = inflater.inflate(R.layout.f_lesson_message, null);
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
		listView = (ListView)mBaseView.findViewById(R.id.listView);
	}
	
	private void setListener(){
		
	}
	
	private void processLogic() {
		final ProgressDialog pDialog = new ProgressDialog(context);
		pDialog.setMessage("Loading...");
		pDialog.show(); 
		
		String tag_json_obj = "json_obj_req";
		String url = serverIp+"/api/getLessonMessage.html?classId="+classId;
		
		FastJsonRequest<LessonMessageListVO>   fastRequest = new FastJsonRequest<LessonMessageListVO>(Method.GET,url, LessonMessageListVO.class,null, new Response.Listener<LessonMessageListVO>() {

			@Override
			public void onResponse(LessonMessageListVO resVO) {
				pDialog.dismiss();
				if(resVO != null && resVO.getLessonMessageList().size() > 0){
					msgList.clear();
					msgList.addAll(resVO.getLessonMessageList());
					msgAdapter = new LessonMessageAdapter(context,msgList);
					listView.setAdapter(msgAdapter);
				}else{
					CommonUtil.showToast(context, "暂无教学通知",Toast.LENGTH_LONG);
				}
			}
		},
		new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				 pDialog.dismiss();
				 CommonUtil.showToast(context, "获取教学通知失败",Toast.LENGTH_LONG);
			}
		}
	    );
		
		FRestClient.getInstance(context).addToRequestQueue(fastRequest,tag_json_obj);
	}
	

}
