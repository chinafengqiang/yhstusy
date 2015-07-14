package com.feng.fragment;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.feng.util.StringUtils;
import com.feng.vo.CategoryPlanListVO;
import com.feng.vo.CategoryPlanVO;
import com.feng.vo.CoursePlanListVO;
import com.feng.volley.FRestClient;
import com.feng.volley.FastJsonRequest;
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
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

import java.util.List;
public class CategoryPlanFragment  extends Fragment{
	private View mBaseView;
	private Context context = null;
	private String ip = "";
	private String serverIp = "";
	private Long classId;
	private int userId = 0;
	private SharedPreferences sharedPreferences;
	
	private String fileName = "";
	private CoursePlanManager manager;
	private CategoryPlanVO curCategoryPlan;
	
	private String[] is = { "删除", "取消" };
	String rootPath = Environment.getExternalStorageDirectory().getPath()
			+ "/myCoursePlan";
	
	private ListView listView;
	private SampleListAdapter mAdapter; 
	private List<CategoryPlanVO> planList = new ArrayList<CategoryPlanVO>();
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		context = getActivity();
		initSp();
		mBaseView = inflater.inflate(R.layout.f_category_plan, null);
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
		listView = (ListView)mBaseView.findViewById(R.id.listView);
	}
	
	private void setListener(){
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				String fileUrl = "";
				try {
					curCategoryPlan = planList.get(position);
					if(curCategoryPlan == null)
						return;
					String fileurl = curCategoryPlan.getFileUrl();
					if(StringUtils.isBlank(fileurl)){
						CommonUtil.showToast(context, "文件地址错误",Toast.LENGTH_LONG);
						return;
					}
					fileName = getDownLoadFileName(curCategoryPlan.getFileUrl());
					fileUrl = URLEncoder.encode(fileName,"UTF-8");
				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();
				}
				
				String url = serverIp + "/uploadFile/file/" + fileUrl;
				downloadVideos(url);
			}
		});

		listView.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					final int position, long arg3) {
				CategoryPlanVO selePlan = planList.get(position);
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
									manager.removeCoursePlan(planId,1);
								} catch (Exception e) {
									break;
								}
								File f = new File(rootPath + "/" + fileName);
								deleteFile(f);
								
								planList.remove(position);

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
		// 设置初期数据  
		planList.clear();
		getPlan();
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
        	CategoryPlanVO plan = planList.get(index);
    		String fileName = getDownLoadFileName(plan.getFileUrl());
    		if (FileUtil.isExists("/sdcard/myCoursePlan/"+ fileName)) {
    			plan.setLocal(true);
    		}else{
    			plan.setLocal(false);
    		}
            if(view == null){  
            	view = LayoutInflater.from(context).inflate(R.layout.f_category_plan_item, null);
                //LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
               // view = inflater.inflate(R.layout.f_schedule_plan_item, null);  
            }  
            TextView name = (TextView)view.findViewById(R.id.planName); 
            TextView time = (TextView)view.findViewById(R.id.time); 
            TextView planStatus = (TextView)view.findViewById(R.id.planStatus); 
            TextView category = (TextView)view.findViewById(R.id.category); 
            if(plan.isLocal()){
            	planStatus.setText("已下载");
            }
            name.setText(plan.getName()); 
            time.setText("时间："+plan.getStartDate()+"--"+plan.getEndDate());
            category.setText("学科："+plan.getCategoryName());
            return view;  
        }  
    } 
	
	 private void getPlan(){
			final ProgressDialog pDialog = new ProgressDialog(context);
			pDialog.setMessage("Loading...");
			pDialog.show(); 
	    	String tag_json_obj = "json_obj_req";
			String url = serverIp+"/api/getCategoryPlan.html?classId="+classId;
			FastJsonRequest<CategoryPlanListVO>   fastRequest = new FastJsonRequest<CategoryPlanListVO>(Method.GET,url, CategoryPlanListVO.class,null, new Response.Listener<CategoryPlanListVO>() {

				@Override
				public void onResponse(CategoryPlanListVO resVO) {
					pDialog.dismiss();
					if(resVO != null&&resVO.getCategoryPlanList().size()>0){
						planList.addAll(resVO.getCategoryPlanList());
						mAdapter = new SampleListAdapter(); 
						listView.setAdapter(mAdapter);
					}else{
						CommonUtil.showToast(context, "无学科计划存在",Toast.LENGTH_LONG);
					}
					
				}
			},
			new Response.ErrorListener() {

				@Override
				public void onErrorResponse(VolleyError error) {
					pDialog.dismiss();
					CommonUtil.showToast(context, "获取学科计划失败",Toast.LENGTH_LONG);
				}
			}
		    );
			
			FRestClient.getInstance(context).addToRequestQueue(fastRequest,tag_json_obj);
	    }
	    
	


private Handler handler = new Handler(){
	public void handleMessage(Message msg) {
		final String fileName = msg.obj.toString();
		String extName = FileUtil.getExtName(fileName);
		if (extName.equalsIgnoreCase(".pdf") ) {
			curCategoryPlan.setLocal(true);
			if(mAdapter != null)
				mAdapter.notifyDataSetChanged();
			if(manager == null)
				manager = new CoursePlanManager(context);
			manager.insertCategoryPlan(userId, 1, curCategoryPlan);
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
