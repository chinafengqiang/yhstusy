package com.feng.fragment;

import java.util.ArrayList;
import java.util.List;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.Request.Method;
import com.feng.adapter.BookResAdapter;
import com.feng.vo.BookResListVO;
import com.feng.vo.CoursePlanListVO;
import com.feng.vo.CoursePlanVO;
import com.feng.volley.FRestClient;
import com.feng.volley.FastJsonRequest;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.smartlearning.R;
import com.smartlearning.constant.Global;
import com.smartlearning.utils.CommonUtil;
import com.smartlearning.utils.SpUtil;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SchedulePlanFragment extends Fragment{
private View mBaseView;
	
	private Context context = null;
	private String ip = "";
	private String serverIp = "";
	private Long classId;
	private SharedPreferences sharedPreferences;
	private PullToRefreshListView mPullRefreshListView; 
	
	private ArrayList<String> mListItems = new ArrayList<String>();  
	private SampleListAdapter mAdapter; 
	private List<CoursePlanVO> planList = new ArrayList<CoursePlanVO>();
	
	private int offset = 0;
	private final int defaultSize = 10;
	private final int pagesize = 5;
	private int total = 0;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		context = getActivity();
		
		initSp();
		
		mBaseView = inflater.inflate(R.layout.f_schedule_plan, null);
		mPullRefreshListView = (PullToRefreshListView)mBaseView.findViewById(R.id.list_view);
		mPullRefreshListView.setMode(Mode.PULL_FROM_END);
		
		// 设置初期数据  
		getPlan(false);
		
        mAdapter = new SampleListAdapter();  
        mPullRefreshListView.setAdapter(mAdapter);
        
		mPullRefreshListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				if(offset >= total){
					CommonUtil.showToast(context, "已经全部加载完成",Toast.LENGTH_LONG);
					return;
				}else{
					offset += pagesize;
					getPlan(true);
				}
			}
			
		});
		findView();
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
		
	}
	
	private void setListener(){
		
	}
	
	private void processLogic() {
		
	}
	
	private class SampleListAdapter extends BaseAdapter {  
        
        @Override  
        public int getCount() {  
            return mListItems.size();  
        }  
   
        @Override  
        public Object getItem(int index) {  
            return mListItems.get(index);  
        }  
   
        @Override  
        public long getItemId(int index) {  
            return index;  
        }  
   
        @Override  
        public View getView(int index, View view, ViewGroup arg2) {  
            if(view == null){  
                LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
                view = inflater.inflate(R.layout.test_list_item, null);  
            }  
            TextView textView = (TextView)view.findViewById(R.id.listItemText);  
            textView.setText(mListItems.get(index));  
            return view;  
        }  
    }  
   
    private class FinishRefresh extends AsyncTask<Void, Void, Void>{  
        @Override  
        protected Void doInBackground(Void... params) {  
            return null;  
        }  
   
        @Override  
        protected void onPostExecute(Void result){  
        	mPullRefreshListView.onRefreshComplete();  
        }  
    }  
    
    private void getPlan(boolean isRefresh){
    	int pagesize = defaultSize;
    	if(isRefresh){
    		pagesize = this.pagesize;
    	}
    	String tag_json_obj = "json_obj_req";
		String url = serverIp+"/api/getCoursePlan.html?classId="+classId+"&offset="+offset+"&pagesize="+pagesize;
		
		FastJsonRequest<CoursePlanListVO>   fastRequest = new FastJsonRequest<CoursePlanListVO>(Method.GET,url, CoursePlanListVO.class,null, new Response.Listener<CoursePlanListVO>() {

			@Override
			public void onResponse(CoursePlanListVO resVO) {
				if(resVO != null&&resVO.getPlanCount() > 0){
					total = (int)resVO.getPlanCount();
					planList = resVO.getPlanList();
					for(CoursePlanVO plan : planList){
	            		mListItems.add(plan.getName());
					}
				}
				mPullRefreshListView.onRefreshComplete(); 
			}
		},
		new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				mPullRefreshListView.onRefreshComplete();  
			}
		}
	    );
		
		FRestClient.getInstance(context).addToRequestQueue(fastRequest,tag_json_obj);
    }
}
