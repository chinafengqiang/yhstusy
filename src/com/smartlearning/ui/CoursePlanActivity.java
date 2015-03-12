package com.smartlearning.ui;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.Toast;

import com.smartlearning.R;
import com.smartlearning.biz.AdviodManager;
import com.smartlearning.constant.Global;
import com.smartlearning.model.CoursePlan;
import com.smartlearning.task.DownFileTask;
import com.smartlearning.utils.FileUtil;
import com.smartlearning.utils.Tool;

/**
 * 课程教学计划
 * @author user
 */
public class CoursePlanActivity extends Activity {
	private ProgressDialog pd = null;
	private List<CoursePlan> list = null;
	private Context context = null;
	private Button detail_return_btn = null,refsh_plan;
	private Button detail_home_btn = null;
	private String fileName = "";
	private Button main_bottom_download = null;
	private final String pathName = "myCoursePlan";
	private SharedPreferences sharedPreferences;
	String ip = "";
	String serverIp = "";
	private Long classId;
	private GridView gridview;
	ArrayList<HashMap<String, Object>> lstImageItem = new ArrayList<HashMap<String, Object>>();
	ArrayList<String> filenameList = new ArrayList<String>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 去掉Activity上面的状态栏
					getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
							WindowManager.LayoutParams.FLAG_FULLSCREEN);
					setContentView(R.layout.activity_course_plan);
					
					gridview = (GridView) findViewById(R.id.gridview1);
					context = this;
					detail_return_btn = (Button) this.findViewById(R.id.detail_return_btn);
					detail_home_btn = (Button) this.findViewById(R.id.detail_home_btn);
					main_bottom_download = (Button) this.findViewById(R.id.main_bottom_download1);
					refsh_plan = (Button) this.findViewById(R.id.refsh_plan);
					sharedPreferences = getSharedPreferences("userInfo", MODE_PRIVATE);
					ip = sharedPreferences.getString("serverIp", null);
					classId = sharedPreferences.getLong("classId", 0);
					serverIp = "http://"+ ip +":"+Global.Common_Port;
					
					main_bottom_download.setOnClickListener(onClickListenerView);
					refsh_plan.setOnClickListener(onClickListenerView);
					
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
							Intent intent = new Intent(context,MainActivity.class);
							startActivity(intent);
							
						}
					});
		
				  new PlanGetTask().execute(1);
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
	
	
	private class PlanGetTask extends AsyncTask<Integer,Integer,Integer>{

		@Override
		protected Integer doInBackground(Integer... params) {
			 if(list!=null){
				 list.clear();
			 }
			 lstImageItem.clear();
			 filenameList.clear();
			 
			 AdviodManager manager = new AdviodManager(context);
			 //list = manager.getPlanByAll(serverIp);
			 list = manager.getPlanByAll(serverIp, classId);
			 if(list!= null){
				 for(int i = 0; i < list.size(); i ++){
						CoursePlan coursePlan = list.get(i);
						HashMap<String, Object> map = new HashMap<String, Object>();
						String url = coursePlan.getImageUrl();
						String filename = getFileName(url);
						if(filename!=null&&!"".equals(filename))
							filenameList.add(filename);
						Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.kechejidu);
						map.put("ItemImage", bitmap);
						map.put("ItemText", coursePlan.getName());
						lstImageItem.add(map);
				}	
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
			
			// 生成适配器的ImageItem <====> 动态数组的元素，两者一一对应
			SimpleAdapter saImageItems = new SimpleAdapter(context, // 没什么解释
					lstImageItem,// 数据来源
					R.layout.image_item,// night_item的XML实现
	
					// 动态数组与ImageItem对应的子项
					new String[] { "ItemImage", "ItemText" },
	
					// ImageItem的XML文件里面的一个ImageView,两个TextView ID
					new int[] { R.id.ItemImage, R.id.ItemText });
			
			saImageItems.setViewBinder(new ViewBinder() { 
		            
		            public boolean setViewValue(View view, Object data, String textRepresentation) { 
		                //判断是否为我们要处理的对象  
		                if (view instanceof ImageView)  {
		                	if(data != null){       
                                   if(data instanceof Bitmap )
                                     {
                                             view.setVisibility(View.VISIBLE);
                                             ImageView iv = (ImageView) view;
                                             iv.setImageBitmap((Bitmap) data);
                                     }
                                     else 
                                     {
                                             view.setVisibility(View.VISIBLE);
                                             ((ImageView) view).setImageResource((Integer) data);
                                     }
                                }
                                else
                                {
                                        view.setVisibility(View.GONE);
                                }
                                return true;
                        }
                        else
                        {
                                return false;
                        }
		            }  
		        }); 
			
			// 添加并且显示
			gridview.setAdapter(saImageItems);
			// 添加消息处理
			gridview.setOnItemClickListener(new ItemClickListener());
		}
		
	}
	
	private String getFileName(String urls){
		if (urls != null){
			String url = urls;
			int lastdotIndex = url.lastIndexOf("/");
			String fileName = url.substring(lastdotIndex+1, url.length());
			return fileName;
		}else{
			return "";
		}
	}
	
	
	private View.OnClickListener onClickListenerView = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.main_bottom_download1:
				Intent intent3 = new Intent(context,MyCourselistActivity.class);
				intent3.putStringArrayListExtra("plan.file.list", filenameList);
				startActivity(intent3);
				break;
			 //new PlanGetTask().execute(1);
			case R.id.refsh_plan:
				new PlanGetTask().execute(1);
				break;
			}
		}
	};

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
				//String urls = list.get(arg2).getUrl();
				fileName = getDownLoadFileName(list.get(arg2).getImageUrl());
				fileUrl = URLEncoder.encode(fileName,"UTF-8");
		    //		fileUrl = new String(getDownLoadFileName(urls).getBytes(),"UTF-8");
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}
			
			Log.i("fileUrl", "fileUrlfile:==================== " + fileUrl);
			String url = serverIp + "/uploadFile/file/" + fileUrl;
			
//			String url = myApp.getServerIP() + list.get(arg2).getImageUrl();
			
//			Intent intent = new Intent(context, ZoomActivity.class);
//			intent.putExtra("picUrl", url);
//			startActivity(intent);
			
			downloadVideos(url);
		}
	}
	
	private void intentForward(final String fileName) {
		Intent intent = new Intent();
		intent.setAction(android.content.Intent.ACTION_VIEW);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		File file = new File(fileName);
		Uri path = Uri.fromFile(file);
		intent.setDataAndType(path,"application/pdf");
		startActivity(intent);
	};
	
	private Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			//Tool.ShowMessage(context, msg.obj.toString());
			final String fileName = msg.obj.toString();
			String extName = FileUtil.getExtName(fileName);
			if (extName.equalsIgnoreCase(".pdf") ) {
			//	intentForward(fileName);
			}else{
				Tool.ShowMessage(context, "非pdf格式");
			}
			
		//	intentForward(fileName);
		}

	};
	
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
		//	final String fileName = getDownLoadFileName(url);
			
			Tool.ShowMessage(context, fileName);
			String filepath = "/sdcard/myCoursePlan/" +fileName;
			if(FileUtil.isExists(filepath)){
				intentForward(filepath);
			    /* AlertDialog.Builder builder = new AlertDialog.Builder(context);
			     builder.setTitle("提示").setMessage("该视频文件已经存在于SDCard中，确定要覆盖下载吗？")
			     .setPositiveButton("确定", new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog, int which) {
						DownFileTask task = new DownFileTask(context,handler,fileName,"myCoursePlan");
						task.execute(url);
					}
			    	 
			     }).setNegativeButton("取消", null).create().show();*/
//			     }).setNegativeButton("取消", new DialogInterface.OnClickListener(){
//
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						String extName = FileUtil.getExtName(fileName);
//						if (extName.equalsIgnoreCase(".pdf") ) {
//							intentForward("/sdcard/" +pathName +"/" + fileName);
//						} else {
//							Tool.ShowMessage(context, "非pdf格式");
//						}
//						
//					}
//			    	 
//			     }).create().show();
			}else{
				
				DownFileTask task = new DownFileTask(context,handler,fileName, "myCoursePlan");
				task.execute(url);
				
			}
			
		}
}
