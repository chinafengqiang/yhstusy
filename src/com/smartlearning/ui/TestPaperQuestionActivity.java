package com.smartlearning.ui;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.smartlearning.R;
import com.smartlearning.biz.TestPaperManager;
import com.smartlearning.model.QuestionTypeEnum;
import com.smartlearning.model.TestPaperQuestion;
import com.smartlearning.utils.ImageTools;

/**
 *试卷题目
 */
public class TestPaperQuestionActivity extends Activity {

	/**
	 *业务对象 
	 */
	private TestPaperManager testPaperManager = null;
	
	ProgressDialog pd = null;
	ImageView inform_unread_img;
	ListView inform_list;
	Button inform_back, inform_refresh, addInform;
	int last_message_id;
	MyAdapter adapter;
	private SharedPreferences sharedPreferences;
	String ip = "";
	Long classId;
	private static final int PAGESIZE = 1; // 每次取几条记录
	private int pageIndex = 0; // 用于保存当前是第几页,0代表第一页
	
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
		setContentView(R.layout.activity_fourms);
		
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
	}
	
	/**
	 * 数据初始化加载
	 */
	private void loadData(){
		String serverIp = "http://"+ ip +":8082";
		Log.i("TestPaperQuestion", "serverIp=="+serverIp);
		LoadTestPaperQuestionList listItem = new LoadTestPaperQuestionList(serverIp, classId);
		
		Thread thread = new Thread(listItem);
		thread.start();
		
	}
	
	/**
	 * 刷新方法
	 */
	private void RefreshData(){
		String serverIp = "http://"+ ip +":8082";
		RemoteTestPaperQuestionList listItem = new RemoteTestPaperQuestionList(serverIp, classId);
		Thread thread = new Thread(listItem);
		thread.start();

	}
	
	/**
	 *绑定事件 
	 */
	private void bindClickListener() {
		
//		inform_list.setOnItemClickListener(new OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//				
//				if (id != -1) {
//					
////					int flashback = forums.size() - position - 1;
////					
////					OnlineForum onlineForum = forums.get(flashback);
////		
////					Intent intent = new Intent();
////					intent.setClass(OnlineForumActivity.this, ForumShowActivity.class);
////					intent.putExtra("forum_id", onlineForum.getId().toString());
////					intent.putExtra("name", onlineForum.getName());
////					intent.putExtra("content", onlineForum.getQuestion());
////					startActivity(intent);
//				}
//			}
//		});

		
		/**
		 * 刷新信息
		 */
		inform_refresh.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				RefreshData();
				
			}
		});

	}
	
	private Handler handleQuestions = new Handler(){
		public void handleMessage(android.os.Message msg) {
			
			List<TestPaperQuestion> testPaperQuestions = (ArrayList<TestPaperQuestion>)msg.obj;
			if (testPaperQuestions == null) {
				Toast.makeText(TestPaperQuestionActivity.this, "对不起, 在线试卷还没公布", Toast.LENGTH_SHORT).show();
			}else{
				pd.setMessage("数据已获取,界面绑定中...");
				adapter = new MyAdapter(testPaperQuestions, TestPaperQuestionActivity.this);
				inform_list.setAdapter(adapter);
				pd.setMessage("数据获取中,请稍候...");
				hideProgressDialog();
				
			}	
		};
	};
	
	
	class LoadTestPaperQuestionList implements Runnable{
		List<TestPaperQuestion> testPaperQuestions = null;
		private String serverIp = "";
		private Long classId;
			
	    public LoadTestPaperQuestionList(String serverIp,Long classId){
	    	this.serverIp = serverIp;
	    	this.classId = classId;
			showProgressDialog();
		}
	    
		@Override
		public void run() {
			testPaperManager = new TestPaperManager(TestPaperQuestionActivity.this);
			try{
			//	testPaperQuestions = testPaperManager.getAllTestPaperQuestionList(this.serverIp, 8);
				testPaperQuestions = testPaperManager.getByPager(pageIndex, PAGESIZE, " test_paper_id = 8 ");
				
			} catch(Exception e){
			}
			Message message = new Message();
			message.obj = testPaperQuestions;
			handleQuestions.sendMessage(message);
		}
	}
	
	class RemoteTestPaperQuestionList implements Runnable{
		List<TestPaperQuestion> testPaperQuestions = null;
		private String serverIp = "";
		private Long classId;
			
	    public RemoteTestPaperQuestionList(String serverIp,Long classId){
	    	this.serverIp = serverIp;
	    	this.classId = classId;
			showProgressDialog();
		}
	    
		@Override
		public void run() {
			testPaperManager = new TestPaperManager(TestPaperQuestionActivity.this);
			try{
				testPaperQuestions = testPaperManager.getAllTestPaperQuestionList(this.serverIp, 7);
				//testPaperQuestions = testPaperManager.getByPager(pageIndex, PAGESIZE, " test_paper_id = 7 ");
				
			} catch(Exception e){
			}
//			Message message = new Message();
//			message.obj = testPaperQuestions;
//			handleQuestions.sendMessage(message);
		}
	}
	
	
	/*用于获取用于显示的分页信息*/
	private String getPagerInfo(){
		String pagerInfo = "第{0}页 ,共{1}页";
		int totalPageCount = testPaperManager.getByPageCount(PAGESIZE, " test_paper_id = 8 ");
		return MessageFormat.format(pagerInfo, pageIndex+1, totalPageCount);
	}
	
	/**
	 *适配器
	 */
	class MyAdapter extends BaseAdapter {

		private LayoutInflater inflater = null;
		Context context;
		private List<TestPaperQuestion> testPaperQuestions = null;
		private int item_layout_res = R.layout.activity_question_list_item;
		
		public MyAdapter(Context context) {
			this.context = context;
			inflater = LayoutInflater.from(context);
		}
		
		public MyAdapter(List<TestPaperQuestion> testPaperQuestions, Context context){
			this.testPaperQuestions = testPaperQuestions;
			this.context = context;
			inflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return testPaperQuestions.size() + 2;
		}

		@Override
		public Object getItem(int position) {
			return testPaperQuestions.get(position - 1);
		}

		@Override
		public long getItemId(int position) {
			if (position == 0)// 选中第一项
			{
				return -1;// 代表点击的是第一项
			} else if (position > 0 && (position < this.getCount() - 1)) {
				return testPaperQuestions.get(position - 1).getId();// 如果用户选中了中间项
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
				convertView = inflater.inflate(R.layout.moreitemsview, null);
				TextView txtPagerInfo = (TextView) convertView.findViewById(R.id.txtPagerInfo);
				txtPagerInfo.setText(getPagerInfo());
				ImageButton btnFirst = (ImageButton) convertView.findViewById(R.id.btnFirst);
				btnFirst.setOnClickListener(new View.OnClickListener() {
					
					public void onClick(View v) {
						pageIndex=0;
						loadData();
						Toast.makeText(TestPaperQuestionActivity.this, "第"+(pageIndex+1)+"页", Toast.LENGTH_SHORT).show();
					}
				 });
							
							
				ImageButton btnPrev = (ImageButton) convertView.findViewById(R.id.btnPrev);
				btnPrev.setOnClickListener(new View.OnClickListener() {
					
					public void onClick(View v) {
						if (pageIndex>0){
							 pageIndex--;
							 loadData();
						     Toast.makeText(TestPaperQuestionActivity.this, "第"+(pageIndex+1)+"页", Toast.LENGTH_SHORT).show();
						}else{
							 Toast.makeText(TestPaperQuestionActivity.this, "已经是第一页了", Toast.LENGTH_SHORT).show();
						}
						
					}
				 });
							
				ImageButton btnNext = (ImageButton) convertView.findViewById(R.id.btnNext);
				btnNext.setOnClickListener(new View.OnClickListener() {
					
					public void onClick(View v) {
						int totalPageCount = testPaperManager.getByPageCount(PAGESIZE, " test_paper_id = 8 ");
						if (pageIndex < totalPageCount-1){
						   pageIndex++;
						   loadData();
						   Toast.makeText(TestPaperQuestionActivity.this, "第"+(pageIndex+1)+"页", Toast.LENGTH_SHORT).show();
						}else{
						   Toast.makeText(TestPaperQuestionActivity.this, "已经是第一页了", Toast.LENGTH_SHORT).show();
						}
					}
				});
							
				ImageButton btnLast = (ImageButton) convertView.findViewById(R.id.btnLast);
				btnLast.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						int totalPageCount = testPaperManager.getByPageCount(PAGESIZE, " test_paper_id = 8 ");
						pageIndex = totalPageCount-1;
						loadData();
						Toast.makeText(TestPaperQuestionActivity.this, "第"+(pageIndex+1)+"页", Toast.LENGTH_SHORT).show();
					}
				});
							
				return convertView;
			}
			
			
			ViewHolder holder = null;
			
			if(convertView == null 
					|| convertView.findViewById(R.id.addproduct) != null 
					|| convertView.findViewById(R.id.linemore) != null){
			
				holder = new ViewHolder();
				
				convertView = inflater.inflate(this.item_layout_res, parent, false);
				
				holder.selectName = (TextView) convertView.findViewById(R.id.selectName);
				holder.inform_unread_img = (ImageView) convertView.findViewById(R.id.inform_unread_img);
				holder.group1 = (RadioGroup) convertView.findViewById(R.id.group1);
				holder.group2 = (RadioGroup) convertView.findViewById(R.id.group2);
				holder.item_layout_OptionCheckeBoxs = (LinearLayout) convertView.findViewById(R.id.optionCheckeBox);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			int flashback = position - 1;
			Integer questionType = testPaperQuestions.get(flashback).getQuestionType();
			
			if(QuestionTypeEnum.Judge.toValue() == questionType){
				holder.selectName.setText("三、 " + QuestionTypeEnum.Judge.toName());
				byte[] result = testPaperQuestions.get(flashback).getNote();
				Bitmap bitmap = ImageTools.getBitmapFromByte(result);
				holder.inform_unread_img.setImageBitmap(bitmap);
				holder.group2.setVisibility(View.VISIBLE);
			}
			
			if(QuestionTypeEnum.MultiSelect.toValue() == questionType){
				holder.selectName.setText("二、 " + QuestionTypeEnum.MultiSelect.toName());
				byte[] result = testPaperQuestions.get(flashback).getNote();
				Bitmap bitmap = ImageTools.getBitmapFromByte(result);
				holder.inform_unread_img.setImageBitmap(bitmap);
				holder.item_layout_OptionCheckeBoxs.setVisibility(View.VISIBLE);
			}
			
			if(QuestionTypeEnum.SingleSelect.toValue() == questionType){
				holder.selectName.setText("一、 " + QuestionTypeEnum.SingleSelect.toName());
				byte[] result = testPaperQuestions.get(flashback).getNote();
				Bitmap bitmap = ImageTools.getBitmapFromByte(result);
				holder.inform_unread_img.setImageBitmap(bitmap);
				holder.group1.setVisibility(View.VISIBLE);
			}
			
			return convertView;
		}

	}
	
	public final class ViewHolder {
		public TextView selectName;
		public ImageView inform_unread_img;
		public RadioGroup group1;
		public RadioGroup group2;
		public LinearLayout item_layout_OptionCheckeBoxs;
	}

}
