package com.smartlearning.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.smartlearning.R;
import com.smartlearning.biz.BookManager;
import com.smartlearning.constant.Global;
import com.smartlearning.model.OnlineForum;

/**
 *在线交流
 */
public class OnlineForumActivity extends Activity {

	/**
	 *业务对象 
	 */
	private BookManager bookManager;
	
	/**
	 * 通知列表
	 */
	private List<OnlineForum> forums = new ArrayList<OnlineForum>();
	ProgressDialog pd = null;
	ImageView inform_unread_img;
	ListView inform_list;
	Button inform_back, inform_refresh;
	int last_message_id;
	MyAdapter adapter;
	private SharedPreferences sharedPreferences;
	String ip = "";
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
		setContentView(R.layout.activity_message);
		
		sharedPreferences = getSharedPreferences("userInfo", MODE_PRIVATE);
		ip = sharedPreferences.getString("serverIp", null);
		classId = sharedPreferences.getLong("classId", 0);
		
		initView();
		loadData();
		bindClickListener();
	}
	
	
	public void initView() {
		inform_refresh = (Button) findViewById(R.id.inform_refresh);
		inform_list = (ListView) findViewById(R.id.message_list);
	//	inform_unread_img = (ImageView) findViewById(R.id.inform_unread_img);
	}
	
	/**
	 * 数据初始化加载
	 */
	private void loadData(){
		String serverIp = "http://"+ ip +":"+Global.Common_Port;
		LoadForumList listItem = new LoadForumList(serverIp, classId);
		
		Thread thread = new Thread(listItem);
		thread.start();
		
	}
	
	/**
	 * 刷新方法
	 */
	private void RefreshData(){
		String serverIp = "http://"+ ip +":"+Global.Common_Port;
		LoadForumList listItem = new LoadForumList(serverIp, classId);
		
		Thread thread = new Thread(listItem);
		thread.start();
//		LoadNewMessageList listItem = new LoadNewMessageList(serverIp);
//		
//		Thread thread = new Thread(listItem);
//		thread.start();
	}
	
	
	/**
	 *绑定事件 
	 */
	private void bindClickListener() {
		
		inform_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				int flashback = forums.size() - position - 1;
				
				OnlineForum onlineForum = forums.get(flashback);

				Intent intent = new Intent();
				intent.setClass(OnlineForumActivity.this, ForumShowActivity.class);
				intent.putExtra("forum_id", onlineForum.getId().toString());
				intent.putExtra("name", onlineForum.getName());
				intent.putExtra("content", onlineForum.getQuestion());
				startActivity(intent);
			}
		});

		
		/**
		 * 刷新信息
		 */
		inform_refresh.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				RefreshData();
				Toast.makeText(OnlineForumActivity.this, "刷新成功", Toast.LENGTH_SHORT).show();
			}
		});

	}
	
	private Handler handleSysMessages = new Handler(){
		public void handleMessage(android.os.Message msg) {
			
			forums = (ArrayList<OnlineForum>)msg.obj;
			if (forums == null) {
				Toast.makeText(OnlineForumActivity.this, "对不起, 在线交流还没公布", Toast.LENGTH_SHORT).show();
			}else{
				pd.setMessage("数据已获取,界面绑定中...");
				adapter = new MyAdapter(forums, OnlineForumActivity.this);
				inform_list.setAdapter(adapter);
				pd.setMessage("数据获取中,请稍候...");
				hideProgressDialog();
				
			}	
		};
	};
	
	private Handler handleNewSysMessages = new Handler(){
		public void handleMessage(android.os.Message msg) {
			
			List<OnlineForum> list = (ArrayList<OnlineForum>)msg.obj;
			if (list == null) {
				Toast.makeText(OnlineForumActivity.this, "对不起，无最新教学通知", Toast.LENGTH_SHORT).show();
			}else{
				pd.setMessage("数据已获取,界面绑定中...");
				
				adapter = new MyAdapter(list, OnlineForumActivity.this);
				inform_list.setAdapter(adapter);
				pd.setMessage("数据获取中,请稍候...");
				hideProgressDialog();
			}	
		};
	};
	
	class LoadForumList implements Runnable{
		List<OnlineForum> forums = null;
		private String serverIp = "";
		private Long classId;
			
	    public LoadForumList(String serverIp,Long classId){
	    	this.serverIp = serverIp;
	    	this.classId = classId;
			showProgressDialog();
		}
	    
		@Override
		public void run() {
			bookManager = new BookManager(OnlineForumActivity.this);
			try{
				forums = bookManager.getOnlineForumByPage(serverIp, 1, 12000, classId);
				
			} catch(Exception e){
			}
			Message message = Message.obtain();
			message.obj = forums;
			handleSysMessages.sendMessage(message);
		}
	}
	
	
//	class LoadNewMessageList implements Runnable{
//		List<SysMessage> messages = null;
//		
//		private String serverIp = "";
//		
//	    public LoadNewMessageList(String serverIp){
//	    	this.serverIp = serverIp;
//			showProgressDialog();
//		}
//	    
//		@Override
//		public void run() {
//			try{
//				messages = userManager.getRefreshSysMessageList(serverIp);
//			} catch(Exception e){
//			}
//			Message message = new Message();
//			message.obj = messages;
//			handleNewSysMessages.sendMessage(message);
//		}
//	}
	
	/**
	 *适配器
	 */
	class MyAdapter extends BaseAdapter {

		private LayoutInflater inflater = null;
		Context context;
		private List<OnlineForum> onlineForums = null;
		
		public MyAdapter(Context context) {
			this.context = context;
			inflater = LayoutInflater.from(context);
		}
		
		public MyAdapter(List<OnlineForum> onlineForums, Context context){
			this.onlineForums = onlineForums;
			this.context = context;
			inflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return onlineForums.size();
		}

		@Override
		public Object getItem(int position) {
			return onlineForums.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			int flashback = onlineForums.size() - position - 1;
			TextView inform_list_item_title, inform_list_item_content, inform_list_item_time;
			convertView = inflater.inflate(R.layout.activity_forum_list_item, null);

			inform_list_item_title = (TextView) convertView.findViewById(R.id.inform_list_item_title);
			inform_list_item_content = (TextView) convertView.findViewById(R.id.inform_list_item_content);
			inform_list_item_time = (TextView) convertView.findViewById(R.id.inform_list_item_time);
//			inform_unread_img = (ImageView) convertView.findViewById(R.id.inform_unread_img);
//
//			inform_unread_img.setImageResource(R.drawable.unread_new_inform);
			
			TextPaint paint = inform_list_item_title.getPaint();
			paint.setFakeBoldText(true);
			inform_list_item_title.setText(onlineForums.get(flashback).getName());

			String content = onlineForums.get(flashback).getQuestion();
			if (content.length() > 20)
				content = content.substring(0, 20) + "...";

			inform_list_item_content.setText(content);
			inform_list_item_time.setText(onlineForums.get(flashback).getCreatedTime());

			return convertView;
		}


	}

}
