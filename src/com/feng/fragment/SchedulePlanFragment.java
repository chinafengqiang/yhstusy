package com.feng.fragment;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.Request.Method;
import com.feng.adapter.BookResAdapter;
import com.feng.util.StringUtils;
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
import com.smartlearning.biz.CoursePlanManager;
import com.smartlearning.constant.Global;
import com.smartlearning.task.DownFileTask;
import com.smartlearning.utils.CommonUtil;
import com.smartlearning.utils.FileUtil;
import com.smartlearning.utils.SpUtil;
import com.smartlearning.utils.Tool;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnCreateContextMenuListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class SchedulePlanFragment extends Fragment{
private View mBaseView;
	
	private Context context = null;
	private String ip = "";
	private String serverIp = "";
	private Long classId;
	private int userId = 0;
	private SharedPreferences sharedPreferences;
	private PullToRefreshListView mPullRefreshListView; 
	
	private SampleListAdapter mAdapter; 
	private List<CoursePlanVO> planList = new ArrayList<CoursePlanVO>();
	
	private int offset = 0;
	private final int defaultSize = 10;
	private final int pagesize = 5;
	private int total = 0;
	
	private String fileName = "";
	private CoursePlanVO curCoursePlan;
	private CoursePlanManager manager;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		context = getActivity();
		
		initSp();
		
		mBaseView = inflater.inflate(R.layout.f_schedule_plan, null);
		mPullRefreshListView = (PullToRefreshListView)mBaseView.findViewById(R.id.list_view);
		mPullRefreshListView.setMode(Mode.PULL_FROM_END);
		
		// 设置初期数据  
		planList.clear();
		getPlan(false);
		
        
		mPullRefreshListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				if(offset >= total){
					CommonUtil.showToast(context, "已经全部加载完成",Toast.LENGTH_LONG);
					new FinishRefresh().execute();
					return;
				}else{
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
		String strUserId = sharedPreferences.getString("user","0");
		userId = Integer.parseInt(strUserId);
	}
	
	private void findView() {
		
	}
	
	private void setListener(){
		mPullRefreshListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String fileUrl = "";
				try {
					curCoursePlan = planList.get(position-1);
					if(curCoursePlan == null)
						return;
					String fileurl = curCoursePlan.getFileUrl();
					if(StringUtils.isBlank(fileurl)){
						CommonUtil.showToast(context, "文件地址错误",Toast.LENGTH_LONG);
						return;
					}
					fileName = getDownLoadFileName(curCoursePlan.getFileUrl());
					fileUrl = URLEncoder.encode(fileName,"UTF-8");
				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();
				}
				
				String url = serverIp + "/uploadFile/file/" + fileUrl;
				downloadVideos(url);
			}
			
		});
		mPullRefreshListView.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
			@Override
			public void onCreateContextMenu(ContextMenu menu, View v,
					ContextMenuInfo menuInfo) {
				 menu.setHeaderTitle("操作");
				 menu.add(0, 0, 0, "删除");
			}
		});
	}
	
	private void processLogic() {
		
	}
	
	
	private class SampleListAdapter extends BaseAdapter {  
        
        @Override  
        public int getCount() {  
            return planList.size();  
        }  
   
        @Override  
        public Object getItem(int index) {  
            return planList.get(index);  
        }  
   
        @Override  
        public long getItemId(int index) {  
            return index;  
        }  
   
        @Override  
        public View getView(int index, View view, ViewGroup arg2) {  
        	CoursePlanVO plan = planList.get(index);
    		String fileName = getDownLoadFileName(plan.getFileUrl());
    		if (FileUtil.isExists("/sdcard/myCoursePlan/"+ fileName)) {
    			plan.setLocal(true);
    		}else{
    			plan.setLocal(false);
    		}
            if(view == null){  
            	view = LayoutInflater.from(context).inflate(R.layout.f_schedule_plan_item, null);
                //LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
               // view = inflater.inflate(R.layout.f_schedule_plan_item, null);  
            }  
            TextView name = (TextView)view.findViewById(R.id.planName); 
            TextView time = (TextView)view.findViewById(R.id.time); 
            TextView planStatus = (TextView)view.findViewById(R.id.planStatus); 
            if(plan.isLocal()){
            	planStatus.setText("已下载");
            }
            name.setText(plan.getName()); 
            time.setText("时间："+plan.getStartDate()+"--"+plan.getEndDate());
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
    
    private void getPlan(final boolean isRefresh){
    	int pagesize = defaultSize;
    	if(isRefresh){
    		pagesize = this.pagesize;
    	}
    	String tag_json_obj = "json_obj_req";
		String url = serverIp+"/api/getCoursePlan.html?classId="+classId+"&offset="+offset+"&pagesize="+pagesize;
		offset += pagesize;
		FastJsonRequest<CoursePlanListVO>   fastRequest = new FastJsonRequest<CoursePlanListVO>(Method.GET,url, CoursePlanListVO.class,null, new Response.Listener<CoursePlanListVO>() {

			@Override
			public void onResponse(CoursePlanListVO resVO) {
				if(resVO != null&&resVO.getPlanCount() > 0){
					total = (int)resVO.getPlanCount();
					planList.addAll(resVO.getPlanList());
					if(isRefresh){
						mAdapter.notifyDataSetChanged();
					}else{
				        mAdapter = new SampleListAdapter();  
				        mPullRefreshListView.setAdapter(mAdapter);
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
    
	// 当AdapterView被单击(触摸屏或者键盘)，则返回的Item单击事件
	class ItemClickListener implements OnItemClickListener {
		public void onItemClick(AdapterView<?> arg0,// The AdapterView where the
													// click happened
				View arg1,// The view within the AdapterView that was clicked
				int arg2,// The position of the view in the adapter
				long arg3// The row id of the item that was clicked
		) {
			String fileUrl = "";
			try {
				curCoursePlan = planList.get(arg2);
				String fileurl = curCoursePlan.getFileUrl();
				if(StringUtils.isBlank(fileurl)){
					CommonUtil.showToast(context, "文件地址错误",Toast.LENGTH_LONG);
					return;
				}
				fileName = getDownLoadFileName(curCoursePlan.getFileUrl());
				fileUrl = URLEncoder.encode(fileName,"UTF-8");
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}
			
			String url = serverIp + "/uploadFile/file/" + fileUrl;
			downloadVideos(url);
		}
	}
	
	//得到当前下载的文件名
			private String getDownLoadFileName(String urls){
				if (urls != null){
					String url = urls;
					int lastdotIndex = url.lastIndexOf("/");
					String fileName = url.substring(lastdotIndex+1, url.length());
					return fileName;
				}else{
					return "";
				}
			}
			
			private void downloadVideos(final String url){
				
				//Tool.ShowMessage(context, fileName);
				String filepath = "/sdcard/myCoursePlan/" +fileName;
				if(FileUtil.isExists(filepath)){
					intentForward(filepath);
				}else{
					DownFileTask task = new DownFileTask(context,handler,fileName, "myCoursePlan");
					task.execute(url);
					
				}
				
			}
			
			private Handler handler = new Handler(){
				public void handleMessage(Message msg) {
					final String fileName = msg.obj.toString();
					String extName = FileUtil.getExtName(fileName);
					if (extName.equalsIgnoreCase(".pdf") ) {
						curCoursePlan.setLocal(true);
						if(mAdapter != null)
							mAdapter.notifyDataSetChanged();
						if(manager == null)
							manager = new CoursePlanManager(context);
						manager.insertCoursePlan(userId, 0, "",curCoursePlan);
						//String filepath = "/sdcard/myCoursePlan/" +fileName;
						intentForward(fileName);
					}else{
						Tool.ShowMessage(context, "非法资料，非pdf格式");
					}
					
				}

			};
			
			private void intentForward(final String fileName) {
				Intent intent = new Intent();
				intent.setAction(android.content.Intent.ACTION_VIEW);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				File file = new File(fileName);
				Uri path = Uri.fromFile(file);
				intent.setDataAndType(path,"application/pdf");
				startActivity(intent);
			}

	
			@Override
			public boolean onContextItemSelected(MenuItem item) {
				switch (item.getItemId()) {
		        case 0:
		        	CommonUtil.showToast(context,"delete",Toast.LENGTH_LONG);
		        	break;
				}
				return super.onContextItemSelected(item);
			}
}
