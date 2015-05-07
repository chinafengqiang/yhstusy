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
import com.feng.adapter.VideoResAdapter;
import com.feng.fragment.listener.IResFragmentListener;
import com.feng.vo.BookRes;
import com.feng.vo.BookResListVO;
import com.feng.vo.VideoRes;
import com.feng.vo.VideoResListVO;
import com.feng.volley.FRestClient;
import com.feng.volley.FastJsonRequest;
import com.smartlearning.R;
import com.smartlearning.biz.BookManager;
import com.smartlearning.constant.Global;
import com.smartlearning.task.DownFileTask;
import com.smartlearning.ui.DetailActivity;
import com.smartlearning.ui.FVideoDetailActivity;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class VideoResListFragment extends Fragment implements IResFragmentListener{
	public static final String PART_ID = "partId";
	public static final String CATEGORY_ID = "categoryId";
	public static final String CATEGORY_NAME = "categoryName";
	public static final String ALL_IDS = "allIds";
	public static final String ALL_NAMES = "allNames";
	private View mBaseView;
	private Context mContext;
	private String serverIp;
	private String serverUrl;
	private SharedPreferences sp;
	
	private ListView listView;
	private LinearLayout book_none;
	
	private VideoResAdapter adapter = null;
	private String fileName = "";
	private String allIds = "";
	private String allNames = "";
	private String categoryName;
	private VideoRes nowDownloadVideo;
	private List<VideoRes> videoList = new ArrayList<VideoRes>();
	
	private BookManager bookManager = null;
	
	private int userId = 0;
	private int islocal = 0;
	
	private VideoRes nowVideo;
	
	int partId;
	int categoryId;
	
	private String[] is = { "删除", "取消" };
	String rootPath = Environment.getExternalStorageDirectory().getPath()
			+ "/myVideo";
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContext = getActivity();
		mBaseView = inflater.inflate(R.layout.f_video_res_center, null);
		
		partId = getArguments().getInt(PART_ID,0);
		categoryId = getArguments().getInt(CATEGORY_ID,0);
		categoryName = getArguments().getString(CATEGORY_NAME);
		allIds = getArguments().getString(ALL_IDS);
		allNames = getArguments().getString(ALL_NAMES);
				
		findView();
		
		initSp();
		
		setListener();
		
		loadData(partId, categoryId);
		
		return mBaseView;
	}
	
	public static Fragment newInstance(int partId,int categoryId,String categoryName,String allIds,String allNames){
		VideoResListFragment fragment = new VideoResListFragment();
		Bundle bundle = new Bundle();
		bundle.putInt(PART_ID,partId);
		bundle.putInt(CATEGORY_ID,categoryId);
		bundle.putString(CATEGORY_NAME,categoryName);
		bundle.putString(ALL_IDS,allIds);
		bundle.putString(ALL_NAMES,allNames);
		fragment.setArguments(bundle);
		return fragment;
	}
	
	private void findView(){
		listView = (ListView)mBaseView.findViewById(R.id.listView);
		book_none = (LinearLayout)mBaseView.findViewById(R.id.book_none);
	}
	
	public void downloadedNotify(){
		if(nowVideo != null){
			
		}
	}
	
	
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (resultCode) { 
		   case 1:
			   if(nowVideo != null){
				   boolean isLocal = data.getBooleanExtra("isLocalFile",false);
				   nowVideo.setLocalFile(isLocal);
			   }
			   if(adapter != null)
				   adapter.notifyDataSetChanged();
		    break;
		default:
		    break;
		    }
	}

	private void setListener(){
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				nowVideo = videoList.get(position);
				nowVideo.setCategoryName(categoryName);
				nowVideo.setAllIds(allIds);
				nowVideo.setAllNames(allNames);
				Intent intent = new Intent(mContext, FVideoDetailActivity.class);
				intent.putExtra("videoRes",nowVideo);
				
				startActivityForResult(intent, 0);
				//startActivity(intent);
			}
		});
		
		listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					final int position, long id) {
				AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
				builder.setTitle("请选择操作");
				builder.setItems(is, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case 0:
							VideoRes ebook = videoList.get(position);
							fileName = getDownLoadFileName(ebook.getResUrl());
							if (ebook.isLocalFile() || FileUtil.isExists("/sdcard/myVideo/" + fileName)) {
								File f = new File(rootPath + "/" + fileName);
								deleteFile(f);
								
								int id = ebook.getResId();
								bookManager = new BookManager(mContext);
								
								try {
									bookManager.removeVideo(id);
								} catch (Exception e) {
									
								}
								
								videoList.remove(position);
								
								if(adapter != null){
									adapter.notifyDataSetChanged();
								}
								
								CommonUtil.showToast(mContext,is[which],Toast.LENGTH_LONG);
							}else{
								CommonUtil.showToast(mContext, "非本地文件不能进行删除操作",Toast.LENGTH_LONG);
							}
							break;
						case 1:
							CommonUtil.showToast(mContext,is[which],Toast.LENGTH_LONG);
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
	
	private void initSp(){
		sp = SpUtil.getSharePerference(mContext);
		serverIp = sp.getString("serverIp","");
		serverUrl = "http://" + serverIp + ":" + Global.Common_Port;
		String strUserId = sp.getString("user","0");
		userId = Integer.parseInt(strUserId);
		islocal = sp.getInt("book_is_local",0);
	}
	
	private void loadData(final int partId,final int categoryId){
		if(videoList != null)
			videoList.clear();
		if(islocal == 1){
			class GetBookRes extends AsyncTask<Boolean,Integer,Boolean>{

				@Override
				protected Boolean doInBackground(Boolean... params) {
					BookManager bookManager = new BookManager(mContext);
					videoList = bookManager.getVideoRes(userId,partId+"#"+categoryId);
				    return true;
				}

				@Override
				protected void onPreExecute() {
					super.onPreExecute();
				}

				@Override
				protected void onPostExecute(Boolean result) {
					super.onPostExecute(result);
					 if(videoList == null || videoList.size() == 0){
					    	CommonUtil.showToast(mContext,mContext.getString(R.string.local_book_res_isnull), Toast.LENGTH_LONG);
					    	 book_none.setVisibility(View.VISIBLE);
					 }else{
						  book_none.setVisibility(View.GONE);
						  adapter = new VideoResAdapter(videoList, mContext,categoryName);
						  listView.setAdapter(adapter);
					 }
				}
				
			}
			
			new GetBookRes().execute(true);
		}else{
			final ProgressDialog pDialog = new ProgressDialog(mContext);
			pDialog.setMessage("Loading...");
			pDialog.show(); 
			
			String tag_json_obj = "json_obj_req";
			String url = "http://" + serverIp + ":" + Global.Common_Port+"/api/getVideoRes.html?partId="+partId+"&categoryId="+categoryId;
			
			FastJsonRequest<VideoResListVO>   fastRequest = new FastJsonRequest<VideoResListVO>(Method.GET,url, VideoResListVO.class,null, new Response.Listener<VideoResListVO>() {

				@Override
				public void onResponse(VideoResListVO resVO) {
					pDialog.dismiss();
					if (resVO == null || resVO.getVideoResList().size() <= 0) {
						CommonUtil.showToast(mContext, "对不起，最新视频还没公布", Toast.LENGTH_LONG);
						return;
					} 
					
					book_none.setVisibility(View.GONE);
					
					videoList.addAll(resVO.getVideoResList());
					adapter = new VideoResAdapter(videoList, mContext,categoryName);
					listView.setAdapter(adapter);
				}
			},
			new Response.ErrorListener() {

				@Override
				public void onErrorResponse(VolleyError error) {
					// TODO Auto-generated method stub
					 VolleyLog.d("BookResListFragment", "Error: " + error.getMessage());
					 pDialog.dismiss();
				}
			}
		    );
			
			FRestClient.getInstance(mContext).addToRequestQueue(fastRequest,tag_json_obj);
		}
	}
	
	private void downloadFiles(final String url) {
		String extName = FileUtil.getExtName(fileName);
		if (!extName.equalsIgnoreCase(".pdf") ){
			//Tool.ShowMessage(mContext, "非法资料，非pdf格式");
			CommonUtil.showToast(mContext,"非法资料，非pdf格式",Toast.LENGTH_SHORT);
			return;
		}
		if (FileUtil.isExists("/sdcard/myBook/" + fileName)) {
			String path = "/sdcard/myBook/" + fileName;
			File file = new File(path);
			if (file.isDirectory()) {
			} else {
				intentForward("/sdcard/myBook/" + fileName);
			}
		} else {
			DownFileTask task = new DownFileTask(mContext, handler, fileName,
					"myBook");
			task.execute(url);
		}

	}
	
	private void intentForward(final String fileName) {
		Intent intent = new Intent();
		intent.setAction(android.content.Intent.ACTION_VIEW);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		File file = new File(fileName);
		Uri path = Uri.fromFile(file);
		intent.setDataAndType(path, "application/pdf");
		startActivity(intent);
	};
	
	private Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			final String fileName = msg.obj.toString();
			String extName = FileUtil.getExtName(fileName);
			if (extName.equalsIgnoreCase(".pdf") ) {
				if(bookManager == null)
					bookManager = new BookManager(mContext);
				nowDownloadVideo.setLocalFile(true);
				if(adapter != null)
					adapter.notifyDataSetChanged();
				
				nowDownloadVideo.setAllIds(allIds);
				nowDownloadVideo.setAllNames(allNames);
				//bookManager.insertBook(userId,nowDownloadVideo);
				
			}else{
				Tool.ShowMessage(mContext, "非法资料，非pdf格式");
			}
		}

	};
	
	// 得到当前下载的文件名
	private String getDownLoadFileName(String urls) {
		if (urls != null) {
			String url = urls;
			int lastdotIndex = url.lastIndexOf("/");
			String fileName = url.substring(lastdotIndex + 1, url.length());
			return fileName;
		} else {
			return "";
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
	
	/* 判断文件MimeType的method */
	private String getMIMEType(File f) {
		String type = "";
		String fName = f.getName();
		/* 取得扩展名 */
		String end = fName
				.substring(fName.lastIndexOf(".") + 1, fName.length())
				.toLowerCase();

		/* 按扩展名的类型决定MimeType */
		if (end.equals("3gp") || end.equals("mp4")||end.equals("avi")||end.equals("wmv")||end.equals("flv")) {
			type = "video";
		}else if(end.equals("iac")){
			type = "iac";
		}
		else {
			type = "*";
		}
		return type;
	}

	@Override
	public void loadData() {
		this.loadData(partId, categoryId);
	}

	@Override
	public void searchRes(String value) {
		// TODO Auto-generated method stub
		
	}
	
	

}
