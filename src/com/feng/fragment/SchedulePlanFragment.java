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
import com.feng.view.XListView;
import com.feng.view.XListView.IXListViewListener;
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

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
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
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class SchedulePlanFragment extends Fragment implements IXListViewListener{
private View mBaseView;
	
	private Context context = null;
	private String ip = "";
	private String serverIp = "";
	private Long classId;
	private int userId = 0;
	private SharedPreferences sharedPreferences;
	private XListView mListView;
	
	private SampleListAdapter mAdapter; 
	private List<CoursePlanVO> planList = new ArrayList<CoursePlanVO>();
	
	private int offset = 0;
	private final int defaultSize = 10;
	private final int pagesize = 5;
	private int total = 0;
	
	private String fileName = "";
	private CoursePlanVO curCoursePlan;
	private CoursePlanManager manager;
	
	private CoursePlanVO seleCoursePlan = null;
	private String[] is = { "删除", "取消" };
	String rootPath = Environment.getExternalStorageDirectory().getPath()
			+ "/myCoursePlan";
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		context = getActivity();
		
		initSp();
		
		mBaseView = inflater.inflate(R.layout.f_schedule_plan, null);
		mListView = (XListView) mBaseView.findViewById(R.id.xListView);
		mListView.setPullLoadEnable(true);
		// 设置初期数据  
		planList.clear();
		getPlan(false);
		
     
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
		mListView.setXListViewListener(this);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
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
		
		mListView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					final int position, long arg3) {
				CoursePlanVO selePlan = planList.get(position-1);
				if(selePlan == null)
					return true;
				final int planId = selePlan.getId();
				final boolean isLocal = selePlan.isLocal();
				final String planUrl = selePlan.getFileUrl();
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setTitle("请选择操作");
				builder.setItems(is, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case 0:
							if (isLocal) {
								fileName = getDownLoadFileName(planUrl);
								if(manager == null)
									manager = new CoursePlanManager(context);
				        		try {
									manager.removeCoursePlan(planId,0);
								} catch (Exception e) {
									break;
								}
								File f = new File(rootPath + "/" + fileName);
								deleteFile(f);
								
								planList.remove(position-1);

								if(mAdapter != null){
									mAdapter.notifyDataSetChanged();
								}
								
								CommonUtil.showToast(context,is[which],Toast.LENGTH_LONG);
							}else{
								CommonUtil.showToast(context, "非本地文件不能进行删除操作",Toast.LENGTH_LONG);
							}
							break;
						case 1:
							CommonUtil.showToast(context,is[which],Toast.LENGTH_LONG);
							break;
						default:
							break;
						}
					}
				}).create().show();
				return true;
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
   

  
    
    private void getPlan(final boolean isRefresh){
    	int pagesize = defaultSize;
    	if(isRefresh){
    		pagesize = this.pagesize;
    	}
		final ProgressDialog pDialog = new ProgressDialog(context);
		pDialog.setMessage("Loading...");
		pDialog.show(); 
    	String tag_json_obj = "json_obj_req";
		String url = serverIp+"/api/getCoursePlan.html?classId="+classId+"&offset="+offset+"&pagesize="+pagesize;
		offset += pagesize;
		FastJsonRequest<CoursePlanListVO>   fastRequest = new FastJsonRequest<CoursePlanListVO>(Method.GET,url, CoursePlanListVO.class,null, new Response.Listener<CoursePlanListVO>() {

			@Override
			public void onResponse(CoursePlanListVO resVO) {
				pDialog.dismiss();
				if(resVO != null&&resVO.getPlanCount() > 0){
					total = (int)resVO.getPlanCount();
					planList.addAll(resVO.getPlanList());
					if(isRefresh){
						mAdapter.notifyDataSetChanged();
					}else{
				        mAdapter = new SampleListAdapter();  
				        mListView.setAdapter(mAdapter);
					}

				}
				onLoad();
			}
		},
		new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				pDialog.dismiss();
				onLoad();
			}
		}
	    );
		
		FRestClient.getInstance(context).addToRequestQueue(fastRequest,tag_json_obj);
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
			public void onRefresh() {
				planList.clear();
				offset = 0;
				getPlan(false);
			}

			@Override
			public void onLoadMore() {
				if(offset >= total){
					CommonUtil.showToast(context, "已经全部加载完成",Toast.LENGTH_LONG);
					onLoad();
					return;
				}else{
					getPlan(true);
				}
			}
			
			private void onLoad() {
				mListView.stopRefresh();
				mListView.stopLoadMore();
				mListView.setRefreshTime(getString(R.string.xlistview_refesh_time));
			}
			
			/**
			 * 删除文件
			 * 
			 * @param file
			 */
			private void deleteFile(File file) {
				// File file = new File(filePath);
				if (Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
					if (file.exists()) {
						if (file.isFile()) {
							file.delete();
						}
						// 如果它是一个目录
						else if (file.isDirectory()) {
							// 声明目录下所有的文件 files[];
							File files[] = file.listFiles();
							for (int i = 0; i < files.length; i++) { // 遍历目录下所有的文件
								deleteFile(files[i]); // 把每个文件 用这个方法进行迭代
							}
						}
						file.delete();
					}
				}
			}
}
