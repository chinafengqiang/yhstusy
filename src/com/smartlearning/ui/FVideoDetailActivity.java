package com.smartlearning.ui;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import butterknife.ButterKnife;
import butterknife.InjectView;

import com.feng.vo.VideoRes;
import com.smartlearning.R;
import com.smartlearning.biz.BookManager;
import com.smartlearning.constant.Global;
import com.smartlearning.task.DownFileVideoTask;
import com.smartlearning.utils.CommonUtil;
import com.smartlearning.utils.FileUtil;
import com.smartlearning.utils.SpUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class FVideoDetailActivity extends Activity{

	private Context mContext;
	
	private String fileName = "";
	private String fileUrl = "";
	private SharedPreferences sp;
	private String serverIp;
	private String videoURL;
	private final String pathName = "myVideo";
	private int videoId = 0;
	private boolean isLocalFile = false;
	
	int userId = 0;
	
	private VideoRes video;
	
	@InjectView(R.id.title_back) LinearLayout title;
	@InjectView(R.id.title_text) TextView titleText;
	@InjectView(R.id.name) TextView name;
	@InjectView(R.id.category) TextView category;
	@InjectView(R.id.lect) TextView lect;
	@InjectView(R.id.createTime) TextView createTime;
	@InjectView(R.id.online) Button onlineBtn;
	@InjectView(R.id.download) Button downloadBtn;
	
	String rootPath = Environment.getExternalStorageDirectory().getPath() + "/myVideo/";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.mContext = this;
		
		setContentView(R.layout.f_video_detail);
		
		ButterKnife.inject(this);
		
		initTitle();
		
		initSp();
		
		initData();
	}
	
	private void initTitle(){

		titleText.setText(R.string.zy_info);
	
		title.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//onBackPressed();
				setResult();
			}
		});
	}
	
	private void initSp(){
		sp = SpUtil.getSharePerference(mContext);
		serverIp = "http://"+sp.getString("serverIp","")+":"+Global.Common_Port;
		String strUserId = sp.getString("user","0");
		userId = Integer.parseInt(strUserId);
	}
	
	private void initData(){
		video = (VideoRes)getIntent().getSerializableExtra("videoRes");
	
		name.setText(video.getResName());
		category.setText(video.getCategoryName());
		createTime.setText(video.getResCreateTime());
		lect.setText(video.getResLectuer());
		videoURL = video.getResUrl();
		videoId = video.getResId();
		
		fileName = getDownLoadFileName();
		if(video.isLocalFile() || FileUtil.isExists("/sdcard/myVideo/" + fileName)){
			isLocalFile = true;
		}
		
		if(videoURL.endsWith(".iac")){
			onlineBtn.setVisibility(View.INVISIBLE);
		}
		
		onlineBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onlineBtn.setFocusableInTouchMode(true);
				if(isLocalFile){
					offlineVideo(rootPath+fileName);
				}else{
					Intent intent = new Intent(mContext, VideoViewPlayingActivity.class);
					intent.setData(Uri.parse(serverIp+videoURL));
					startActivity(intent);
				}
				
			}
		});
		
		downloadBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				try {
					fileUrl = URLEncoder.encode(fileName,"UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				String url = serverIp + "/uploadFile/file/" + fileUrl;
				downloadVideos(url);
			}
		});
	}
	
	
	//得到当前下载的文件名
		private String getDownLoadFileName(){
			if (videoURL!=null){
				String url = videoURL;
				int lastdotIndex = url.lastIndexOf("/");
				String fileName = url.substring(lastdotIndex+1, url.length());
				return fileName;
			}else{
				return "";
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
		
		private void downloadVideos(final String url){
			if(FileUtil.isExists("/sdcard/myVideo/"+fileName)){
				
				String path = "/sdcard/myVideo/"+fileName;
				File file = new File(path);
				if (file.isDirectory()) {
				} else {
					offlineVideo(rootPath+fileName);
				}
				

			}else{
				DownFileVideoTask task = new DownFileVideoTask(mContext,handler,fileName, pathName, videoId);
				task.execute(url);
			}
			
		}

		
		private void offlineVideo(String videoUrl){
			File file = new File(videoUrl);

			if (file.isDirectory()) {
				/* 如果是文件夹就运行getFileDir() */
				//getFileDir(url);
			} else {
				/* 如果是文件调用fileHandle() */
				
				String t = getMIMEType(file);
				

				if(t.equals("video")){
					Intent intent = new Intent(this, VideoViewPlayingActivity.class);
					intent.setData(Uri.parse(videoUrl));
					startActivity(intent);
				}else if(t.equals("iac")){
					//TODO 调用iac播放器 
					try {
						Intent iacIntent = getAllIntent(videoUrl);
						startActivity(iacIntent);
					} catch (Exception e) {
						CommonUtil.showToast(this,"打开iac格式文件失败", Toast.LENGTH_LONG);
					}
				}
				
				else{
					CommonUtil.showToast(this,"文件格式错误", Toast.LENGTH_LONG);
				}
			}
		}
		
		public static Intent getAllIntent( String param ) {  
	        Intent intent = new Intent();    
	        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);    
	        intent.setAction(android.content.Intent.ACTION_VIEW);    
	        Uri uri = Uri.fromFile(new File(param ));  
	        intent.setDataAndType(uri,"iac/*");   
	        return intent;  
	    }  
		
		
		private Handler handler = new Handler(){
			public void handleMessage(Message msg) {
				final String fileName = msg.obj.toString();
				
				video.setLocalFile(true);
				
				BookManager bookManage = new BookManager(mContext);
				
				bookManage.insertVideo(userId, video);
				
				offlineVideo(fileName);
			};
		};
		
		
	    public boolean onKeyDown(int keyCode, KeyEvent event) {
	        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
	        	setResult();
	        }
	        return true;
	    }
		
		private void setResult(){
			Intent intent = new Intent();
			intent.putExtra("isLocalFile",video.isLocalFile());
			setResult(1,intent);
			finish();
		}
}
