package com.smartlearning.ui;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.smartlearning.R;
import com.smartlearning.adapter.MyEVideoAdapter;
import com.smartlearning.biz.AdviodManager;
import com.smartlearning.biz.BookManager;
import com.smartlearning.constant.Global;
import com.smartlearning.model.EBook;
import com.smartlearning.model.EVideo;
import com.smartlearning.model.Video;
import com.smartlearning.task.AsyncTaskImageLoad;
import com.smartlearning.task.VideoTask;
import com.smartlearning.ui.LearningFileByUseActivity.EbookRemoreList;
import com.smartlearning.utils.CommonUtil;
import com.smartlearning.utils.DateUtil;
import com.smartlearning.utils.FileUtil;
import com.smartlearning.utils.HttpUtil;
import com.smartlearning.utils.ImageTools;

/**
 *视频文件加载
 */
public class VideoCenterActivity extends Activity {

	private static final String TAG = "VideoCenterActivity";
	ProgressDialog pd = null;
	private SharedPreferences sharedPreferences;
	private Context context;
	String ip = "";
	//private Button return_btn = null;
	private TextView title = null;
	private ListView listView = null;
	private Button bottom_recommend = null;
	private Button main_bottom_download = null;
	private AdviodManager videoManager = null;
	private LayoutInflater inflater = null;
	private VideoAdapter adapter = null;
	private VideoTask videoTask = null;
	private List<EVideo> videos = null;
	private static final int PAGESIZE = 10; // 每次取几条记录
	private int pageIndex = 0; // 用于保存当前是第几页,0代表第一页
	Long classId;
	private String serverIp;
	private String userId = "0";
	private long loadVideoDate;
	
	private String loadVideosKey;
	
	private MyEVideoAdapter myEVideoAdapter;
	
	private ImageView imgCreate = null;
	private LinearLayout ll_refresh; 
	private ImageView comeBack;
	private ArrayList<String> videoNameList = new ArrayList<String>();
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
	
	@Override
	protected void onDestroy() {
		hideProgressDialog();
		super.onDestroy();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.video_center);
		
		context = this;
		
		sharedPreferences = getSharedPreferences("userInfo", MODE_PRIVATE);
		ip = sharedPreferences.getString("serverIp", null);
		classId = sharedPreferences.getLong("classId", 0);
		userId = sharedPreferences.getString("user","0");
		loadVideosKey = userId+"_load_video_date";
		loadVideoDate = sharedPreferences.getLong(loadVideosKey,0);
		initView();
		loadData();
		bindClickListener();
	}
	
	public void initView() {
		title = (TextView) findViewById(R.id.txtTopTitle);
		listView = (ListView) findViewById(R.id.listView);
		bottom_recommend = (Button) findViewById(R.id.bottom_recommend);
		
		main_bottom_download = (Button) findViewById(R.id.main_bottom_download);
		main_bottom_download.setOnClickListener(onClickListenerView);
		bottom_recommend.setOnClickListener(onClickListenerView);
		//return_btn = (Button) findViewById(R.id.return_btn);
		//return_btn.setOnClickListener(onClickListenerView);
		
		comeBack = (ImageView)findViewById(R.id.return_btn);
		comeBack.setOnClickListener(onClickListenerView);
		ll_refresh = (LinearLayout)findViewById(R.id.ll_refresh);
		imgCreate = (ImageView) findViewById(R.id.imgCreate);
		ll_refresh.setOnClickListener(onClickListenerView);
		imgCreate.setOnClickListener(onClickListenerView);
		//bottom_recommend.setOnClickListener(onClickListenerView);
		context = this;
		inflater = LayoutInflater.from(context);
	}
	
	/**
	 * 数据初始化加载
	 */
	private void loadData(){
		serverIp = "http://"+ ip +":"+Global.Common_Port;
		/*VideoList listItem = new VideoList(serverIp);
		Thread thread = new Thread(listItem);
		thread.start();*/
		boolean loadDataFormServer = false;
		long nowDate = DateUtil.date2Long(new Date());
		if(loadVideoDate == 0){
			loadDataFormServer = true;
		}else{
			if(nowDate > loadVideoDate){
				loadDataFormServer = true;
			}
		}
		new GetCourseTask().execute(loadDataFormServer);
	}
	
	//得到当前下载的文件名
	private String getDownLoadFileName(String videoURL){
		if (videoURL!=null){
			String url = videoURL;
			int lastdotIndex = url.lastIndexOf("/");
			String fileName = url.substring(lastdotIndex+1, url.length());
			return fileName;
		}else{
			return "";
		}
	}
	
	/**
	 *绑定事件 
	 */
	private void bindClickListener() {
		
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				//int flashback = position - 1;
				int flashback = position;
				Intent intent = new Intent(context, DetailActivity.class);
				Integer ids = videos.get(flashback).get_id();
				intent.putExtra("videoid", ids);
				String vurl = videos.get(flashback).getVideoUrl();
				intent.putExtra("vurl", vurl);
				startActivity(intent);
			}
		});
	}
	
	private View.OnClickListener onClickListenerView = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.main_bottom_download:
				Intent intent1 = new Intent(context, MyVideoActivity.class);
				intent1.putStringArrayListExtra("video.filename.list", videoNameList);
				startActivity(intent1);
				
				//refreshData();
				
				break;
			case R.id.return_btn:
				Intent intent = new Intent(context, MainActivity.class);
				startActivity(intent);
				finish();
				break;	
			case R.id.ll_refresh:
				refreshData();
				break;
			case R.id.imgCreate:
				refreshData();
				break;
			case R.id.bottom_recommend:
				refreshData();
				break;
			}	
		}
	};
	
	/**
	 * 数据初始化加载
	 */
	private void refreshData(){
		/*String serverIp = "http://"+ ip +":"+Global.Common_Port;
		EVideoRemoreList listItem = null;
		
		try{
			listItem = new EVideoRemoreList(serverIp);
		} catch(Exception ex){
			Toast.makeText(context, "网络连接出现问题，请检查！", Toast.LENGTH_LONG).show();
		}
		
		Thread thread = new Thread(listItem);
		thread.start();*/
		new GetCourseTask().execute(true);
		
	}
	
	private Handler handlebookRemoreList = new Handler(){
		public void handleMessage(android.os.Message msg) {
			
			List<EVideo> eBookt = (ArrayList<EVideo>)msg.obj;
			if (eBookt == null) {
				Toast.makeText(context, "对不起，最新视频还没公布", Toast.LENGTH_SHORT).show();
				hideProgressDialog();
			}else{
				loadData();
				hideProgressDialog();
			}	
		};
	};
	
	class EVideoRemoreList implements Runnable{
		List<EVideo> videoRemoreList = null;
		String 	serverIP;
	    public EVideoRemoreList(String serverIP){
	    	this.serverIP = serverIP;
			showProgressDialog();
		}
	    
		@Override
		public void run() {
			videoManager = new AdviodManager(context);
			try{
				videoRemoreList = videoManager.getAllEVideoList(serverIP, Integer.parseInt(classId.toString()));
				
			} catch(Exception e){
			}
			Message message = Message.obtain();
			message.obj = videoRemoreList;
			handlebookRemoreList.sendMessage(message);
		}
	}
	
	
	private Handler handleVideoList = new Handler(){
		public void handleMessage(android.os.Message msg) {
			
			videos = (ArrayList<EVideo>)msg.obj;
			if (videos == null) {
				Toast.makeText(VideoCenterActivity.this, "对不起，最新视频还没公布", Toast.LENGTH_SHORT).show();
				hideProgressDialog();
			}else{
				pd.setMessage("数据已获取,界面绑定中...");
				
				adapter = new VideoAdapter(videos, VideoCenterActivity.this);
				listView.setAdapter(adapter);
				pd.setMessage("数据获取中,请稍候...");
				hideProgressDialog();
				
			}	
		};
	};
	
	/*用于获取用于显示的分页信息*/
	private String getPagerInfo(){
		String pagerInfo = "第{0}页 ,共{1}页";
		int totalPageCount = videoManager.getByPageCount(PAGESIZE, "1=1 order by time desc");
		return MessageFormat.format(pagerInfo, pageIndex+1, totalPageCount);
	}
	
	class VideoList implements Runnable{
		List<EVideo> videoList = null;
		private String serverIp = "";
			
	    public VideoList(String serverIp){
	    	this.serverIp = serverIp;
			showProgressDialog();
		}
	    
		@Override
		public void run() {
			videoManager = new AdviodManager(context);
			try{
			//	videoList =	videoManager.getVideosByAll(serverIp, Integer.parseInt(classId.toString()));
		//		videoList =	videoManager.getVideosByAll(serverIp, 2);
				
				videoList =	videoManager.getByPager(pageIndex, PAGESIZE, "1=1 order by time");
				
			} catch(Exception e){
			}
//			Message message = new Message();
			Message message = Message.obtain();
			message.obj = videoList;
			handleVideoList.sendMessage(message);
		}
	}
	
	class VideoAdapter extends BaseAdapter {
		private Context context = null;
		private List<EVideo> list = null;

		public VideoAdapter(Context context) {
			this.context = context;
			inflater = LayoutInflater.from(context);
		}
		
		public VideoAdapter(List<EVideo> list, Context context){
			this.list = list;
			this.context = context;
			inflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return list.size() + 2;
		}

		@Override
		public Object getItem(int position) {
			return list.get(position-1);
		}

		@Override
		public long getItemId(int position) {
			//return position;
			if (position == 0)// 选中第一项
			{
				return -1;// 代表点击的是第一项
			} else if (position > 0 && (position < this.getCount() - 1)) {
				return list.get(position - 1).get_id();// 如果用户选中了中间项
			} else {
				return -2;// 表示用户选中最后一项
			}
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			// 说明是第一项
			if (position == 0) {
				convertView = inflater.inflate(R.layout.test_paper_question_item, null);
				return convertView;
			}
			
			// 说明是最后一项
			if (position == this.getCount() - 1 ) {
				convertView = inflater.inflate(R.layout.ebookmoreitemsview, null);
				TextView txtPagerInfo = (TextView) convertView.findViewById(R.id.txtPagerInfo);
				txtPagerInfo.setText(getPagerInfo());
				Button btnFirst = (Button) convertView.findViewById(R.id.btnFirst);
				btnFirst.setOnClickListener(new View.OnClickListener() {
					
					public void onClick(View v) {
						pageIndex=0;
						loadData();
					}
				 });
							
							
				Button btnPrev = (Button) convertView.findViewById(R.id.btnPrev);
				btnPrev.setOnClickListener(new View.OnClickListener() {
					
					public void onClick(View v) {
						if (pageIndex>0){
							 pageIndex--;
							 loadData();
						     Toast.makeText(context, "第"+(pageIndex+1)+"页", Toast.LENGTH_SHORT).show();
						}else{
							 Toast.makeText(context, "已经是第一页了", Toast.LENGTH_SHORT).show();
						}
						
					}
				 });
							
				Button btnNext = (Button) convertView.findViewById(R.id.btnNext);
				btnNext.setOnClickListener(new View.OnClickListener() {
					
					public void onClick(View v) {
						int totalPageCount = videoManager.getByPageCount(PAGESIZE, "1=1 order by time desc");
						if (pageIndex < totalPageCount-1){
							pageIndex++;
						    loadData();
						}else{
						   Toast.makeText(context, "已经最后一页了", Toast.LENGTH_SHORT).show();
						}
					}
				});
							
				Button btnLast = (Button) convertView.findViewById(R.id.btnLast);
				btnLast.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						int totalPageCount = videoManager.getByPageCount(PAGESIZE, "1=1 order by time desc");
						pageIndex = totalPageCount-1;
						loadData();
					}
				});
							
				return convertView;
			}
						
			
			ViewHolder holder = null;
			
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = inflater.inflate(R.layout.videocenter_list_item,null);
				
				holder.videoImage = (ImageView) convertView.findViewById(R.id.image);
				holder.name = (TextView) convertView.findViewById(R.id.videoName);
				holder.category = (TextView) convertView.findViewById(R.id.videoCategory);
				holder.attention = (TextView) convertView.findViewById(R.id.lectuer);
				
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
		
			int flashback = position-1;
			byte[] result = list.get(flashback).getImageUrl();
			if(result != null){
				Bitmap bitmap = ImageTools.getBitmapFromByte(result);
				if(bitmap!= null){
					holder.videoImage.setImageBitmap(bitmap);
				} 
			}
			
			holder.name.setText(list.get(flashback).getName());
			holder.category.setText("分类 ：" + list.get(flashback).getCategoryName());
			holder.attention.setText("主讲人：" + list.get(flashback).getLectuer());
			
			return convertView;
		}

	}
	
//   private void LoadImage(ImageView img, String path) {
//        //异步加载图片资源
//        AsyncTaskImageLoad async=new AsyncTaskImageLoad(img);
//        //执行异步加载，并把图片的路径传送过去
//        async.execute(path);
//	        
//	 } 
	
	public final class ViewHolder {
		public ImageView videoImage;
		public TextView name;
		public TextView category;
		public TextView attention;
	}
	
	
	
private class GetCourseTask extends AsyncTask<Boolean,Integer,Integer>{
		
		@Override
		protected Integer doInBackground(Boolean... params) {
			videoManager = new AdviodManager(context);
			//List<EBook> eBooks = null;
			boolean loadDataFromServer = params[0];
			if(HttpUtil.netWorkIsOK(context) && loadDataFromServer){//读取远程数据
				videos = videoManager.getPermVideos(serverIp, Integer.parseInt(userId), classId);
				if(loadDataFromServer){//设置已经从服务器获取
					SharedPreferences.Editor editor = sharedPreferences.edit();
					editor.putLong(loadVideosKey, DateUtil.date2Long(new Date()));
					editor.commit();
				}
			}else{//读取本地数据
				Log.i(TAG,"网络不可用或已经缓存数据，获取本地视频资料...");
				videos = videoManager.getUserEVideoDB(Long.parseLong(userId));
			}
			
			return 1;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showProgressDialog();
			if(videos != null)
				videos.clear();
		}

		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			if (videos == null) {
				CommonUtil.showToast(context, "对不起，最新视频还没公布", Toast.LENGTH_LONG);
			} /*else if(result.size() == 0){
				//listView.addFooterView(noEbookView);
				CommonUtil.showToast(context, "不存在您有权限的电子书", Toast.LENGTH_LONG);
			}*/
			else{
				pd.setMessage("数据已获取,界面绑定中...");
				//adapter = new EBookAdapter(eBooks, context);
				groupByEVideos(videos);
				myEVideoAdapter = new MyEVideoAdapter(videos, context);
				int count = 0;
				if(videos!=null){
					count = videos.size();
				}
				title.setText("视频资料(共："+count+"个)");
				listView.setAdapter(myEVideoAdapter);
				pd.setMessage("数据获取中,请稍候...");
			}	
			hideProgressDialog();
		}
		
	}
	

private void groupByEVideos(List<EVideo> eVideos){
	Map<String,List<EVideo>> tempMap = new HashMap<String,List<EVideo>>();
	if(eVideos != null && eVideos.size() > 0){
		for( EVideo video : eVideos){
			String ctgname = video.getCategoryName();
			
			String fileName = getDownLoadFileName(video.getVideoUrl());
			if(FileUtil.isExists("/sdcard/myVideo/"+fileName)){
				video.setLocalFile(true);
			}	
			videoNameList.add(fileName);
			
			List<EVideo> tempList = tempMap.get(ctgname);
			if(tempList != null && tempList.size() > 0){
				tempList.add(video);
			}else{
				tempList = new ArrayList<EVideo>();
				tempList.add(video);
				tempMap.put(ctgname, tempList);
			}
		}
	}
	eVideos.clear();
	Iterator<Entry<String,List<EVideo>>> iter = tempMap.entrySet().iterator();
	while(iter.hasNext()){
		Entry<String,List<EVideo>> entry = iter.next();
		eVideos.addAll(entry.getValue());
	}
}

}
