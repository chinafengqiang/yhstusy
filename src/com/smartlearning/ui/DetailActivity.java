package com.smartlearning.ui;


import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
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
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.smartlearning.R;
import com.smartlearning.biz.AdviodManager;
import com.smartlearning.constant.Global;
import com.smartlearning.model.EVideo;
import com.smartlearning.model.Video;
import com.smartlearning.task.DownFileTask;
import com.smartlearning.task.DownFileVideoTask;
import com.smartlearning.utils.CommonUtil;
import com.smartlearning.utils.DateUtil;
import com.smartlearning.utils.FileUtil;
import com.smartlearning.utils.ImageService;
import com.smartlearning.utils.ImageTools;
import com.smartlearning.utils.Tool;

public class DetailActivity extends Activity{
	
	private static final String TAG = "DetailActivity";
	
	private Context context = null;
	private AdviodManager videoManager = null;
	private List<Video> videos = null;
	private EVideo video = null;
	private Bitmap bitmap = null;
	//private ImageView detail_image = null;
	private TextView detail_video_name = null;
	private TextView category = null;
	private TextView teacher = null;
	private TextView director = null;
	private TextView country = null;
	private TextView abstruce = null;
	//private Button detail_return_btn = null;
	//private Button detail_home_btn = null;
	private Button detail_download = null;//下载
	private Button download = null;
	private String videoURL = null; //电影视频路径
	private List<Video> syncVideoList =new ArrayList<Video>();
	private final String pathName = "myVideo";
	String ip = "";
	String serverIp = "";
	private SharedPreferences sharedPreferences;
	ProgressDialog pd = null;
	private String fileName = "";
	String fileUrl = "";
	int videoId = 0;
	private ImageView return_btn;
	
	String rootPath = Environment.getExternalStorageDirectory().getPath() + "/myVideo/";
	public void showProgressDialog(){
		if (pd==null){
			pd = new ProgressDialog(this);
			pd.setTitle("系统提示");
			pd.setMessage("数据加载中。。。");
		}
		
		pd.show();
	}
	
	public void hideProgressDialog(){
		if (pd!=null) pd.dismiss();
	}
	
	private View.OnClickListener clickListener  = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.detail_download:
				detail_download.setFocusableInTouchMode(true);
				/*Intent intent = new Intent(context,PlayVideo.class);
				Bundle bundle = new Bundle();
				bundle.putString("videourl", videoURL);
				intent.putExtras(bundle);
				startActivity(intent);*/
				Intent intent = new Intent(context, VideoViewPlayingActivity.class);
				intent.setData(Uri.parse(videoURL));
				startActivity(intent);
				break;
			case R.id.download:
				Log.d(TAG, videoURL);
				fileName = getDownLoadFileName();
				try {
					fileUrl = URLEncoder.encode(fileName,"UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				String url = serverIp + "/uploadFile/file/" + fileUrl;
				downloadVideos(url);
				break;
			default:
				break;
			}
			
		}
	};

	private Handler handler = new Handler(){
		public void handleMessage(Message msg) {
		//	Tool.ShowMessage(context, msg.obj.toString());
			final String fileName = msg.obj.toString();
			//Looper.prepare();
//			new Thread(new Runnable() {
//
//				@Override
//				public void run() {
//					Tool.ShowMessage(context, "正在更新MediaProvider...");
//					Log.d("TestThread", "start");
//				//	Tool.sdCardScan(context);
//					Tool.ShowMessage(context, "更新MediaProvider完毕！");
//					
//					Tool.scanSdCard(context,fileName);
//					Log.d("TestThread", "end");
//				}
//			}).start();
			

		};
	};
	
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
	
	public void offlinePlayVideo(String videoUrl){
		File file = new File(videoUrl);
		
		//String vSize = size2string(file.length());
		
		if (file.isDirectory()) {
			/* 如果是文件夹就运行getFileDir() */
			//getFileDir(url);
		} else {
			/* 如果是文件调用fileHandle() */
			String t = getMIMEType(file);
			
			//video = videoManager.getEVideoById(videoId);
			
			if (t.equals("video") /*&& vSize.equals(video.getVideoSize())*/) {
				Intent intent = new Intent(DetailActivity.this, PlayVideo.class);
				Bundle bundle = new Bundle();
				
				Log.i("offlineVideo", "offlineVideo==="+videoUrl);
				
				bundle.putString("videourl",videoUrl);
				intent.putExtras(bundle);
				startActivity(intent);
			}else{
				CommonUtil.showToast(this,"文件格式错误", Toast.LENGTH_LONG);
			}
		}
	}
	
	private void offlineVideo(String videoUrl){
		File file = new File(videoUrl);
		
		String vSize = size2string(file.length());
		
		if (file.isDirectory()) {
			/* 如果是文件夹就运行getFileDir() */
			//getFileDir(url);
		} else {
			/* 如果是文件调用fileHandle() */
			
			String t = getMIMEType(file);
			
			/*video = videoManager.getEVideoById(videoId);
			Log.i("video.getVideoSize()", "video.getVideoSize()======"+video.getVideoSize());
			
			if (t.equals("video") && vSize.equals(video.getVideoSize())) {
				Intent intent = new Intent(DetailActivity.this, PlayVideo.class);
				Bundle bundle = new Bundle();
				
				Log.i("offlineVideo", "offlineVideo==="+videoUrl);
				
				bundle.putString("videourl",videoUrl);
				intent.putExtras(bundle);
				startActivity(intent);*/
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
	
	private void downloadVideos(final String url){
		//final String fileName = getDownLoadFileName();
		
	//	Tool.ShowMessage(context, fileName);
		if(FileUtil.isExists("/sdcard/myVideo/"+fileName)){
			
			String path = "/sdcard/myVideo/"+fileName;
			File file = new File(path);
			if (file.isDirectory()) {
			} else {
				offlineVideo(rootPath+fileName);
			}
			
//		     AlertDialog.Builder builder = new AlertDialog.Builder(context);
//		     builder.setTitle("提示").setMessage("该视频文件已经存在于SDCard中，确定要覆盖下载吗？")
//		     .setPositiveButton("确定", new DialogInterface.OnClickListener(){
//
//				@Override
//				public void onClick(DialogInterface dialog, int which) {
//					DownFileTask task = new DownFileTask(context,handler,fileName, pathName);
//					task.execute(url);
//					offlineVideo(rootPath+fileUrl);
//				}
//		    	 
//		     }).setNegativeButton("取消", null).create().show();
		}else{
			
			DownFileVideoTask task = new DownFileVideoTask(context,handler,fileName, pathName, videoId);
//			DownFileTask task = new DownFileTask(context,handler,fileName, pathName);
			task.execute(url);
			//offlineVideo(rootPath+fileName);
//			String path = "/sdcard/myVideo/"+fileName;
//			File file = new File(path);
//			String videoSize = size2string(file.length());
//			
//			Log.i("videoSize", "videoSizevideoSizevideoSize==="+videoSize);
//			
//			AdviodManager manager = new AdviodManager(context);
//			try {
//				manager.modifyEVideo(videoId, videoSize);
//			} catch (Exception e) {
//			}
//			
			//initData();
			
		}
		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.video_detail);
		//接收另一个Activity跳转过来的信息
		context = this;
		int position = getIntent().getIntExtra("position", 0);
		videoManager = new AdviodManager(context);
		sharedPreferences = getSharedPreferences("userInfo", MODE_PRIVATE);
		ip = sharedPreferences.getString("serverIp", null);
	//	serverIp = "http://"+ ip +":8080";
		
		if (getIntent().getIntExtra("videoid", 0)!=0){
			videoId = getIntent().getIntExtra("videoid", 0);
	//		video = videoManager.getVideosById(serverIp, id);
			loadData(videoId);
		}
		

		
		initData();
		
		String vurl = getIntent().getStringExtra("vurl");
		if(vurl!=null&&!"".equals(vurl)&&vurl.endsWith(".iac")){
			detail_download.setVisibility(View.INVISIBLE);
		}
		detail_download.setOnClickListener(clickListener);
	    download.setOnClickListener(clickListener);
		
	    BindListen();
		
	}

	/**
	 * 数据初始化加载
	 */
	private void loadData(int id){
		serverIp = "http://"+ ip +":"+Global.Common_Port;
		DetailVideo listItem = new DetailVideo(serverIp, id);
		
		Thread thread = new Thread(listItem);
		thread.start();
		
	}
	
	/**
	 * 绑定监听
	 */
	private void BindListen() {
		//title上的返回按钮
	    /*detail_return_btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
				
			}
		});*/
	    //title上的回家按钮
	    /*detail_home_btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context,MainActivity.class);
				startActivity(intent);
				
			}
		});*/
		
		return_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
				
			}
		});
	}

	private void initData() {
		//detail_image = (ImageView) this.findViewById(R.id.detail_image);
		detail_video_name = (TextView) this.findViewById(R.id.detail_video_name);
		category = (TextView) this.findViewById(R.id.category);
		director = (TextView) this.findViewById(R.id.director);
		country = (TextView) this.findViewById(R.id.country);	
		abstruce = (TextView) this.findViewById(R.id.abstruct);
		teacher = (TextView) this.findViewById(R.id.teacher);
		return_btn = (ImageView)this.findViewById(R.id.return_btn);
		//detail_return_btn = (Button) this.findViewById(R.id.detail_return_btn);
		//detail_home_btn = (Button) this.findViewById(R.id.detail_home_btn);
		detail_download = (Button) this.findViewById(R.id.detail_download);
		download = (Button) this.findViewById(R.id.download);
		
		if(videoURL!=null&&!"".equals(videoURL)){
			if(videoURL.endsWith(".iac")){
				detail_download.setVisibility(View.INVISIBLE);
			}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}
	
	
	private Handler  handleVideos= new Handler(){
		public void handleMessage(android.os.Message msg) {
			
			video = (EVideo)msg.obj;
			if (video == null) {
				CommonUtil.showToast(DetailActivity.this, "对不起, 没有相关信息", Toast.LENGTH_SHORT);
			}else{
				pd.setMessage("数据已获取,界面绑定中...");
				/*byte[] result = video.getImageUrl();
				if(result != null){
					Bitmap bitmap = ImageTools.getBitmapFromByte(result);
					detail_image.setImageBitmap(bitmap);
				}*/
				
				detail_video_name.setText(video.getName());
				category.setText(video.getCategoryName());
				director.setText(video.getLectuer());
				country.setText(video.getTime());
				abstruce.setText(video.getDescription());
				teacher.setText(video.getTeacherDescription());
				
				videoURL = serverIp + video.getVideoUrl();
				
				pd.setMessage("数据获取中,请稍候...");
				hideProgressDialog();
				
			}	
		};
	};
	
	private Handler handleImageUrls = new Handler(){
		public void handleMessage(android.os.Message msg) {
			
			Bitmap bitmaps = (Bitmap)msg.obj;
			if (bitmaps == null) {
				Toast.makeText(DetailActivity.this, "对不起, 没有相关信息", Toast.LENGTH_SHORT).show();
			}else{
				pd.setMessage("数据已获取,界面绑定中...");
				
				//detail_image.setBackgroundDrawable(new BitmapDrawable(bitmaps));
			
				pd.setMessage("数据获取中,请稍候...");
				hideProgressDialog();
				
			}	
		};
	};
	
	/**
	 * 数据初始化加载
	 */
	private void loadDataImage(String url){
		String image_url = "http://"+ ip +":"+Global.Common_Port + url;
		DetailImage listItem = new DetailImage(image_url);
		
		Thread thread = new Thread(listItem);
		thread.start();
		
	}
	
	class  DetailVideo implements Runnable{
	    
		   EVideo video = null;
		   private String serverIp = "";
		   private int id;
			
		    public DetailVideo(String serverIp, int id){
		    	this.serverIp = serverIp;
		    	this.id = id;
				showProgressDialog();
			}
			
		    @Override
			public void run() {
		    	AdviodManager videoManager = new AdviodManager(context);
					try {
						//video = videoManager.getVideosById(serverIp, id);
						video = videoManager.getEVideoById(id);
					} catch (Exception e) {
					}
			//		Message message = new Message();
					Message message = Message.obtain();
					message.obj = video;
					handleVideos.sendMessage(message);
			}
		}
	
	
	class DetailImage implements Runnable{
	    
		   Bitmap bitmap = null;
		   private String image_url = "";
		
		    public DetailImage(String image_url){
		    	this.image_url = image_url;
				showProgressDialog();
			}
			
		    @Override
			public void run() {
		    		byte[] result;
					try {
						result = ImageService.getImage(image_url);
						bitmap = BitmapFactory.decodeByteArray(result, 0,result.length);// 生成图片
					} catch (Exception e) {
					}
			//		Message message = new Message();
					Message message = Message.obtain();
					message.obj = bitmap;
					handleImageUrls.sendMessage(message);
			}
		}
	
	private String size2string(long size) {
		DecimalFormat df = new DecimalFormat("0.00");
		String mysize = "";
		if (size > 1024 * 1024) {
			mysize = df.format(size / 1024f / 1024f) + "M";
		} else if (size > 1024) {
			mysize = df.format(size / 1024f) + "K";
		} else {
			mysize = size + "B";
		}
		return mysize;
	}

}
