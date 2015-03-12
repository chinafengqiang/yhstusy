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
import android.util.Log;
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
import com.smartlearning.biz.UserManager;
import com.smartlearning.constant.Global;
import com.smartlearning.model.SysMessage;

/**
 *教学通知
 */
public class TeachingMessageActivity extends Activity {

	/**
	 *业务对象 
	 */
	private UserManager userManager;
	
	/**
	 * 通知列表
	 */
	private List<SysMessage> sysMessages = new ArrayList<SysMessage>();
	ProgressDialog pd = null;
	ImageView inform_unread_img;
	ListView inform_list;
	Button inform_back, inform_refresh;
	int last_message_id;
	MyAdapter adapter;
	String ip = "";
	private Context context;
	private SharedPreferences sharedPreferences;
	private boolean isFirst = true;
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
		setContentView(R.layout.activity_user_message);
		
		context = this;
		
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
		inform_unread_img = (ImageView) findViewById(R.id.inform_unread_img);
	}
	
	/**
	 * 数据初始化加载
	 */
	private void loadData(){
		String serverIp = "http://"+ ip +":"+Global.Common_Port;
		LoadMessageList listItem = new LoadMessageList(serverIp, classId);
		
		Thread thread = new Thread(listItem);
		thread.start();
		
	}
	
	/**
	 * 刷新方法
	 */
	private void RefreshData(){
		String serverIp = "http://"+ ip +":"+Global.Common_Port;
		LoadNewMessageList listItem = new LoadNewMessageList(serverIp,classId);
		
		Thread thread = new Thread(listItem);
		thread.start();
	}
	
	
	/**
	 *绑定事件 
	 */
	private void bindClickListener() {
		
		inform_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				int flashback = sysMessages.size() - position - 1;
				String serverIp = "http://"+ ip +":"+Global.Common_Port;
				SysMessage sysMessage = sysMessages.get(flashback);
				
				sysMessage.setIsRead(1);
				
				try {
					userManager.updataSysMessage(serverIp,sysMessage);
				} catch (Exception e) {
				}
			//	RefreshData();
				adapter.notifyDataSetChanged();
				
				Intent intent = new Intent();
				intent.setClass(TeachingMessageActivity.this, MessageShowActivity.class);
				intent.putExtra("message_id", sysMessage.get_id());
				intent.putExtra("name", sysMessage.getName());
				intent.putExtra("content", sysMessage.getContent());
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
				
				Toast.makeText(TeachingMessageActivity.this, "刷新成功", Toast.LENGTH_SHORT).show();
			}
		});
		

	}
	
	private Handler handleSysMessages = new Handler(){
		public void handleMessage(android.os.Message msg) {
			
			sysMessages = (ArrayList<SysMessage>)msg.obj;
			if (sysMessages == null) {
				Toast.makeText(TeachingMessageActivity.this, "对不起，教学通知还没公布", Toast.LENGTH_SHORT).show();
			}else{
				pd.setMessage("数据已获取,界面绑定中...");
				
				adapter = new MyAdapter(sysMessages, TeachingMessageActivity.this);
				inform_list.setAdapter(adapter);
				pd.setMessage("数据获取中,请稍候...");
				hideProgressDialog();
				
			}	
		};
	};
	
	private Handler handleNewSysMessages = new Handler(){
		public void handleMessage(android.os.Message msg) {
			
			sysMessages = (ArrayList<SysMessage>)msg.obj;
			if (sysMessages == null) {
				Toast.makeText(TeachingMessageActivity.this, "对不起，无最新教学通知", Toast.LENGTH_SHORT).show();
			}else{
				pd.setMessage("数据已获取,界面绑定中...");
				
				adapter = new MyAdapter(sysMessages, TeachingMessageActivity.this);
				inform_list.setAdapter(adapter);
				pd.setMessage("数据获取中,请稍候...");
				hideProgressDialog();
			}	
		};
	};
	
	class LoadMessageList implements Runnable{
		List<SysMessage> sysMessages = null;
		private String serverIp = "";
		private Long classId;
			
	    public LoadMessageList(String serverIp, Long classId){
	    	this.serverIp = serverIp;
	    	this.classId = classId;
			showProgressDialog();
		}
	    
		@Override
		public void run() {
			userManager = new UserManager(TeachingMessageActivity.this);
			try{
				sysMessages = userManager.getAllSysMessageList(serverIp, 1, 1000, classId);
				last_message_id = userManager.getLastSysMessageId(classId);
				
			} catch(Exception e){
			}
			Message message = Message.obtain();
			message.obj = sysMessages;
			handleSysMessages.sendMessage(message);
		}
	}
	
	
	class LoadNewMessageList implements Runnable{
		List<SysMessage> messages = null;
		private Long classId;
		private String serverIp = "";
		
	    public LoadNewMessageList(String serverIp, Long classId){
	    	this.serverIp = serverIp;
	    	this.classId = classId;
			showProgressDialog();
		}
	    
		@Override
		public void run() {
			try{
				messages = userManager.getRefreshSysMessageList(serverIp, 1, 1000, classId);
			} catch(Exception e){
			}
			Message message = Message.obtain();
			message.obj = messages;
			handleNewSysMessages.sendMessage(message);
		}
	}
	
//	@Override
//	protected void onResume() {
//		if (!isFirst) {
//		//	Toast.makeText(this, "ddddddddddddddddddddddfafa", Toast.LENGTH_SHORT).show();
//			RefreshData();
//		}
//		isFirst = false;
//		super.onResume();
//	}
	
	/**
	 *适配器
	 */
	class MyAdapter extends BaseAdapter {

		private LayoutInflater inflater = null;
		Context context;
		private List<SysMessage> sysMessages = null;
		
		public MyAdapter(Context context) {
			this.context = context;
			inflater = LayoutInflater.from(context);
		}
		
		public MyAdapter(List<SysMessage> sysMessages, Context context){
			this.sysMessages = sysMessages;
			this.context = context;
			inflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return sysMessages.size();
		}

		@Override
		public Object getItem(int position) {
			return sysMessages.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			int flashback = sysMessages.size() - position - 1;
			ImageView message_list_item_image;
			TextView inform_list_item_title, inform_list_item_content, inform_list_item_time, inform_creator;
			convertView = inflater.inflate(R.layout.activity_message_list_item, null);

			message_list_item_image = (ImageView) convertView.findViewById(R.id.inform_list_item_image);
			inform_list_item_title = (TextView) convertView.findViewById(R.id.inform_list_item_title);
			inform_list_item_content = (TextView) convertView.findViewById(R.id.inform_list_item_content);
			inform_list_item_time = (TextView) convertView.findViewById(R.id.inform_list_item_time);
			inform_creator = (TextView) convertView.findViewById(R.id.inform_creator);
			inform_unread_img = (ImageView) convertView.findViewById(R.id.inform_unread_img);

			message_list_item_image.setBackgroundDrawable(TeachingMessageActivity.this.getResources().getDrawable(R.drawable.class1));

			if (sysMessages.get(flashback).getIsRead() == 0) {
				inform_unread_img.setImageResource(R.drawable.unread_new_inform);
			} else {
				inform_unread_img.setImageResource(R.drawable.read_img);
			}
			
			TextPaint paint = inform_list_item_title.getPaint();
			paint.setFakeBoldText(true);
			inform_list_item_title.setText(sysMessages.get(flashback).getName());

			String content = sysMessages.get(flashback).getContent();
			if (content.length() > 50)
				content = content.substring(0, 20) + "...";

			inform_list_item_content.setText(content);
			inform_list_item_time.setText(sysMessages.get(flashback).getTime());
			inform_creator.setText(sysMessages.get(flashback).getCreatorName());

			return convertView;
		}


	}

}
