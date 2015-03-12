package com.smartlearning.ui;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import com.smartlearning.R;
import com.smartlearning.biz.BookManager;
import com.smartlearning.constant.Global;
import com.smartlearning.model.Book;
import com.smartlearning.model.EBook;
import com.smartlearning.task.DownFileTask;
import com.smartlearning.task.ImageAndText;
import com.smartlearning.task.ImageAndTextListAdapter;
import com.smartlearning.ui.MyBookListActivity.MyAdapter;
import com.smartlearning.utils.FileUtil;
import com.smartlearning.utils.Tool;

public class LearningFileActivity extends Activity {
	
	private List<EBook> list = null;
	private Context context = null;
	private Button detail_return_btn = null;
	private Button detail_home_btn = null;
	private Button bottom_book = null;
//	private Button bottom_note = null;
	private Button main_bottom_download = null;
	private String fileName = "";
	private Button bottom_category = null;
	private SharedPreferences sharedPreferences;
	String ip = "";
	String serverIp = "";
	ProgressDialog pd = null;
	GridView gridview = null;
	Long classId;
	
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
	
	@Override
	protected void onDestroy() {
		hideProgressDialog();
		super.onDestroy();
	}

	/**
	 * 隐藏
	 */
	private void hideProgressDialog(){
		if (pd!=null) pd.dismiss();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		try
		{
			// 去掉Activity上面的状态栏
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN);
			setContentView(R.layout.activity_learning_file);
			gridview = (GridView) findViewById(R.id.gridview);
			context = this;
			detail_return_btn = (Button) this.findViewById(R.id.detail_return_btn);
			detail_home_btn = (Button) this.findViewById(R.id.detail_home_btn);
			
			bottom_book = (Button) this.findViewById(R.id.bottom_book);
			main_bottom_download = (Button) this.findViewById(R.id.main_bottom_download);
			
			bottom_book.setOnClickListener(onClickListenerView);
			main_bottom_download.setOnClickListener(onClickListenerView);
			
			bottom_category = (Button) this.findViewById(R.id.bottom_category);
			bottom_category.setOnClickListener(onClickListenerView);
			sharedPreferences = getSharedPreferences("userInfo", MODE_PRIVATE);
			ip = sharedPreferences.getString("serverIp", null);
			serverIp = "http://"+ ip +":"+Global.Common_Port;
			
		    //title上的返回按钮
			bottom_category.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent1 = new Intent(context, BookCategoryTreeActivity.class);
					startActivity(intent1);
					
				}
			});
			
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
//			BookManager manager = new BookManager();
//			list = manager.geBookByPage(serverIp,1, 12000);
//			if(list == null){
//				return;
//			}
			
			loadLessonList(serverIp);
			
			
		}
		catch(Exception ex){
			Toast.makeText(this, "网络连接出现问题，请检查！", Toast.LENGTH_LONG).show();
		}
	}

	Handler handlerLearning = new Handler() {
		public void handleMessage(android.os.Message msg) {
			@SuppressWarnings("unchecked")
			List<Book> list = (List<Book>) msg.obj;
			if (list == null) {
				Toast.makeText(LearningFileActivity.this, "对不起，无数据", Toast.LENGTH_SHORT).show();
			}else{
				pd.setMessage("数据已获取,界面绑定中...");
				
				List<ImageAndText> ilist = new ArrayList<ImageAndText>();
				for(Book bk : list){
					ImageAndText text = new ImageAndText(serverIp + bk.getPic(), bk.getName());
					ilist.add(text);
				}
				
				// 添加并且显示
				gridview.setAdapter(new ImageAndTextListAdapter(LearningFileActivity.this, ilist, gridview));
				// 添加消息处理
				gridview.setOnItemClickListener(new ItemClickListener());
				
				pd.setMessage("数据获取中,请稍候...");
				hideProgressDialog(); 
			}	
			
		}
	};
	
	//创建新线程从本地试卷列表
	public void loadLessonList(final String serverIP) {
		showProgressDialog();
		Thread thread = new Thread() {
			@Override
			public void run() {
				BookManager manager = new BookManager(context);
				try {
					list = manager.geBookByPage(serverIp, 1, 12000);
				} catch (Exception e) {
				}
				Message message = Message.obtain();
				message.obj = list;
				handlerLearning.sendMessage(message);
			}
		};
		
		thread.start();
		thread = null;

	}
	
	private View.OnClickListener onClickListenerView = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.bottom_book:
				Intent intent1 = new Intent(context,LearningFileActivity.class);
				startActivity(intent1);
				break;
//			case R.id.bottom_note:
//				Intent intent2 = new Intent(context,MyNoteActivity.class);
//				startActivity(intent2);
//				break;
			case R.id.main_bottom_download:
				Intent intent3 = new Intent(context,MyBookListActivity.class);
				startActivity(intent3);
				break;
			}	
		}
	};
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
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
				//String urls = list.get(arg2).getUrl();
				fileName = getDownLoadFileName(list.get(arg2).getPdfUrl());
				
				fileUrl = URLEncoder.encode(fileName,"UTF-8");
		    //		fileUrl = new String(getDownLoadFileName(urls).getBytes(),"UTF-8");
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}
			Log.i("fileUrl", "fileUrlfile:==================== " + fileUrl);
			String url = serverIp + "/uploadFile/file/" + fileUrl;
			
//			Intent intent = new Intent(context, TextReadActivity.class);
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
			
//			intentForward(fileName);
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
			
			Tool.ShowMessage(context, fileName);
			if(FileUtil.isExists("/sdcard/myBook/"+fileName)){
			     AlertDialog.Builder builder = new AlertDialog.Builder(context);
			     builder.setTitle("提示").setMessage("该视频文件已经存在于SDCard中，确定要覆盖下载吗？")
			     .setPositiveButton("确定", new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog, int which) {
						DownFileTask task = new DownFileTask(context,handler,fileName,"myBook");
						task.execute(url);
					}
			    	 
			     }).setNegativeButton("取消", null).create().show();
			}else{
				
				DownFileTask task = new DownFileTask(context,handler,fileName,"myBook");
				task.execute(url);
				
			}
			
		}
}
