package com.smartlearning.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.smartlearning.R;
import com.smartlearning.biz.BookManager;
import com.smartlearning.constant.Global;
import com.smartlearning.model.Advise;
import com.smartlearning.model.OnlineForum;

/**
 *内容显示
 */
public class ForumShowActivity extends Activity {

	TextView inform_show_title, inform_show_content;
	Button inform_show_back, inform_show_advice;
	EditText inform_show_edt_advice;
	String content;// 获取内容
	String name;
	String rootId;
	SharedPreferences sharedPreferences = null;
	ProgressDialog pd = null;
	String username = "";
	private Context context = null;
	private ListView pullToRefreshListView = null;
	private AllCommentAdapter adapter = null;
	private LayoutInflater inflater = null;
	String ip = "";
	
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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_forum_show);
		context = this;
		inflater = LayoutInflater.from(context);
		Intent it = getIntent();
		Bundle bundle = it.getExtras();
		rootId = bundle.getString("forum_id");
		name = bundle.getString("name");
		content = bundle.getString("content");

		inform_show_title = (TextView) findViewById(R.id.inform_show_title);
		inform_show_content = (TextView) findViewById(R.id.inform_show_content);
		inform_show_back = (Button) findViewById(R.id.inform_show_back);
		inform_show_advice = (Button) findViewById(R.id.inform_show_advice);
		inform_show_edt_advice = (EditText) findViewById(R.id.inform_show_edt_advice);

		inform_show_title.setText(name);
		inform_show_content.setText(content);
		
		sharedPreferences = getSharedPreferences("userInfo", MODE_PRIVATE);
		username = sharedPreferences.getString("user", null);
		ip = sharedPreferences.getString("serverIp", null);
		pullToRefreshListView = (ListView) this.findViewById(R.id.pulltorefreshlistview);
		
		
		loadData(Integer.parseInt(rootId));

		/**
		 * 返回
		 */
		inform_show_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();

			}
		});
		
		/**
		 * 提交评论
		 */
		inform_show_advice.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String get_adivce = null;
				get_adivce = inform_show_edt_advice.getText().toString();
				if (get_adivce == null) {

					Toast.makeText(ForumShowActivity.this, "请输入评论",Toast.LENGTH_SHORT).show();
				} else {
					showProgressDialog();
					Advise advise = new Advise();
					
					advise.setRootId(rootId);
					advise.setQuestion(inform_show_edt_advice.getText().toString());
					advise.setId(username);
					String serverIp = "http://"+ ip +":8080";
					SubmitAdvise submitAdvise = new SubmitAdvise(serverIp, advise);
					Thread thread = new Thread(submitAdvise);
					thread.start();
					
					
				}
			}
		});
		
	}
	
	/**
	 * 数据初始化加载
	 */
	private void loadData(int id){
		String serverIp = "http://"+ ip +":"+Global.Common_Port;
		ForumList listItem = new ForumList(serverIp, id);
		
		Thread thread = new Thread(listItem);
		thread.start();
		
	}
	
	private Handler handleForums = new Handler(){
		public void handleMessage(android.os.Message msg) {
			
			List<OnlineForum> forums = (ArrayList<OnlineForum>)msg.obj;
			if (forums == null) {
				Toast.makeText(ForumShowActivity.this, "对不起, 在线交流还没公布", Toast.LENGTH_SHORT).show();
			}else{
				pd.setMessage("数据已获取,界面绑定中...");
				
				adapter = new AllCommentAdapter(context, forums);
				pullToRefreshListView.setAdapter(adapter);
				
				pd.setMessage("数据获取中,请稍候...");
				hideProgressDialog();
				
			}	
		};
	};
	
	private Handler handleSubmit = new Handler(){
		public void handleMessage(android.os.Message msg) {
			hideProgressDialog();
			boolean result = (Boolean)msg.obj;
			if (result){
				inform_show_edt_advice.setText(null);
				loadData(Integer.parseInt(rootId));
				Toast.makeText(ForumShowActivity.this, "评论已成功提交到服务器！",Toast.LENGTH_SHORT).show();
			}else{
				Toast.makeText(ForumShowActivity.this, "评论提交失败！！",Toast.LENGTH_SHORT).show();
			}
		};
	};
	
	
	class SubmitAdvise implements Runnable{
		private Advise advise=null;
		private String serverIp = "";
		
		public SubmitAdvise(String serverIp, Advise advise){
			this.serverIp = serverIp;
			this.advise = advise;
		}

		@Override
		public void run() {
			BookManager adviseManager = new BookManager(context);
			boolean result = false;
			try {
				result = adviseManager.AddAdvise(serverIp,advise);
			} catch (Exception e) {
				e.printStackTrace();
			}
			Message msg = Message.obtain();
			msg.obj = result;
			handleSubmit.sendMessage(msg);
			
		}
		
	}
	
	/**
	 *回复列表
	 */
	class ForumList implements Runnable{
	    
		   List<OnlineForum> onlineForumList = null;
		   private String serverIp = "";
		   private int id;
			
		    public ForumList(String serverIp, int id){
		    	this.serverIp = serverIp;
		    	this.id = id;
				showProgressDialog();
			}
			
		    @Override
			public void run() {
		    	BookManager bookManager = new BookManager(context);
					try {
						onlineForumList = bookManager.getChildForumByPage(this.serverIp, id, 1, 120000);
					} catch (Exception e) {
					}
					Message message = new Message();
					message.obj = onlineForumList;
					handleForums.sendMessage(message);
			}
		}
	
	
	 class AllCommentAdapter extends BaseAdapter{	
	    	private List<OnlineForum> list = null;
			private Context context = null;
			
			public AllCommentAdapter(Context context,List<OnlineForum> list){
				this.context = context;
				this.list = list;
			}
			@Override
			public int getCount() {
				return list.size();
			}

			@Override
			public Object getItem(int position) {
				return list.get(position);
			}

			@Override
			public long getItemId(int position) {
				return position;
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				if(convertView == null){
					convertView= inflater.inflate(R.layout.comment_list_item, null);
				}
				TextView comment_user_name = (TextView) convertView.findViewById(R.id.comment_user_name);
				TextView comment_time = (TextView) convertView.findViewById(R.id.comment_time);
				TextView comment_content = (TextView) convertView.findViewById(R.id.comment_content);
				comment_user_name.setText(list.get(position).getCreator());  //使用者的名字
				
				if(list.get(position).getClassId() != null){
					comment_user_name.setTextColor(Color.parseColor("#f81b1b"));
				}
				
				comment_time.setText(list.get(position).getCreatedTime());
				comment_content.setText(list.get(position).getQuestion());
				return convertView;
			}
			
	    }

}
