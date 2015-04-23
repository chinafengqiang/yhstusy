package com.smartlearning.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.smartlearning.R;
import com.smartlearning.biz.TestPaperManager;
import com.smartlearning.biz.UserTestPaperManager;
import com.smartlearning.constant.Global;
import com.smartlearning.factory.TestPaperFactory;
import com.smartlearning.factory.UTestPaperFactory;
import com.smartlearning.model.TestPaper;
import com.smartlearning.model.TestPaperCategory;
import com.smartlearning.model.TestPaperExtend;
import com.smartlearning.model.TestPaperStautsEnum;
import com.smartlearning.model.UserTestPaper;
import com.smartlearning.utils.SpUtil;
import com.smartlearning.utils.UploadUtil;
import com.smartlearning.utils.UploadUtil.OnUploadProcessListener;
import com.smartlearningclient.tree.TreeElement;

/**
 *试卷
 */
public class TestPaperActivity extends Activity implements OnUploadProcessListener{

	/**
	 * requestCode
	 */
	public final static int GROUP_TO_ADD_CONTACT = 0;// 由分组菜单项跳转到新建联系人界面
	public final static int CTREAT_TO_ADDNEW = 1; // 点击右上角新建跳转到新建联系人界面
	public final static int GROUPCONTACT_MOVETO = 2;// 全组成员移动跳转到组别选择
	public final static int CONTACT_MOVETO = 4;// 单人移动跳转到组别选择
	public final static int EDIT_CONTACT = 11;// 点击编辑跳转到修改联系人界面
	public final static int MARK_MOVE_TO = 5;// 点击标记移动..跳转到组别选择
	public final static int INFO = 6;// 点击查看详情

	private ExpandableListAdapter adapter;
	private ExpandableListView expandableListView;
	private Context context;
	private TestPaperManager manager;
//	private GroupManager groupManager;
//	private StudentManager contactManager;

	private ImageView imgContacts;// 左上角切换到联系人列表的ImageView
	private ImageView imgCreate;// 右上角切换到新建联系人的ImageView
	private RelativeLayout rl_mark;

	private boolean[] isOpen;// 用于处理expandableListView箭头小图标拉伸的
	private int moveToGroupId = -1;// 移动操作的目标分组ID
	private int moveReGroupId;// 移动操作的原分组ID
	private int moveReContactId;// 移动操作的联系人ID

	private boolean isMarkedState = false;// 是否是标记状态
	private boolean isFirst = true;// 为了第一进入onResume而不执行其内的操作
	private List<TestPaperCategory> list;// 存放所有的group
	private SharedPreferences sharedPreferences;
	Long classId;

	private GridView bottomMenuGrid;// 屏幕下方主菜单的布局
	
	ProgressDialog pd = null;
	String ip = "";
	// 为MyExpandableListAdapter建立的数据源
	private List<String> groupItem = new ArrayList<String>();// 存放所有的组别名称
	private List<List<TestPaper>> testPapers = new ArrayList<List<TestPaper>>();// 按组别存放contact
	private List<Integer> groupCount = new ArrayList<Integer>();// 存放各分组内contact的数量
	int testPaperId = 0;
	String userId = "";
	private String[] is = { "打开", "上传作业", "查看作业答案", "取消" };
	/**
	 * 上传初始化
	 */
	private static final int UPLOAD_INIT_PROCESS = 4;
	/**
	 * 上传中
	 */
	private static final int UPLOAD_IN_PROCESS = 5;
	
	/**
	 * 去上传文件
	 */
	protected static final int TO_UPLOAD_FILE = 1;  
	/**
	 * 上传文件响应
	 */
	protected static final int UPLOAD_FILE_DONE = 2;  //
	
	private UTestPaperFactory uTestPaperFactory = null;
	
	private TestPaperFactory testPaperFactory = null;

	/**
	 * 提标框
	 */
	private void showProgressDialog() {
		if (pd == null) {
			pd = new ProgressDialog(this);
			pd.setTitle("系统提示");
			pd.setMessage("数据获取中,请稍候...");
		}
		pd.show();
	}

	/**
	 * 隐藏
	 */
	private void hideProgressDialog() {
		if (pd != null)
			pd.dismiss();
	}

	@Override
	protected void onDestroy() {
		hideProgressDialog();
		super.onDestroy();
	}
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//是否全屏判断
		isMakeFull();
		
		setContentView(R.layout.activity_test_paper);

		context = this;
//		groupManager = new GroupManager(context);
//		contactManager = new StudentManager(context);
		manager = new TestPaperManager(context);

		// 初始化标记状态，false:全部未标记；true:全部标记
//		contactManager.initIsMark("false");

		getId();
		
		//sharedPreferences = getSharedPreferences("userInfo", MODE_PRIVATE);
		sharedPreferences = SpUtil.getSharePerference(context);
		
		ip = sharedPreferences.getString("serverIp", null);
		classId = sharedPreferences.getLong("classId", 0);

		expandableListView.setGroupIndicator(null);// 箭头小图标，不用自带的了
		expandableListView.setCacheColorHint(0);// 防止expandableListView点击变黑

		// 为expandableListView注册上下文菜单SETP.一
		expandableListView.setOnCreateContextMenuListener(this);

		// 绑定数据，刷新界面
		bindView();
		// 头像选择监听

		// Group点击监听
		expandableListView.setOnGroupClickListener(new OnGroupClickListener() {
			public boolean onGroupClick(ExpandableListView mExpandableList,
					View arg1, final int groupPosition, long id) {
				return false;
			}

		});

		// Contact点击监听
		expandableListView.setOnChildClickListener(new OnChildClickListener() {
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {

			    final TestPaper testPaper = testPapers.get(groupPosition).get(childPosition);
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setTitle("请选择操作");
				builder.setItems(is, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case 0:
							boolean isExist = UTestPaperFactory.createUTestPaper(context).profileExist(testPaper.get_id());
							if(isExist){
								Toast.makeText(context, "您作业已提交！", Toast.LENGTH_SHORT).show();
							}else{
								Intent intent = new Intent(context, TestPaperQuestionByUseActivity.class);
								intent.putExtra("categoryId", testPaper.get_id());
								startActivity(intent);
							}
							break;
						case 1:
							loadUserTestPaperList(testPaper.get_id());
							break;
						case 2:
							boolean isExist1 = UTestPaperFactory.createUTestPaper(context).profileExist(testPaper.get_id());
							if(!isExist1){
								Toast.makeText(context, "您作业未提交！", Toast.LENGTH_SHORT).show();
							} else{
								String serverIp = "http://"+ ip +":"+Global.TestPaper_Port;
								viewTestPaperAnswer(serverIp, testPaper.get_id());
							}
							break;
						default:
							break;
						}
					}
				}).create().show();
				return isFirst;
				
			}
		});

		// 处理expandableListView自定义箭头小图标被拉伸SETP.1
		expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

					public void onGroupCollapse(int groupPosition) {
						isOpen[groupPosition] = false;

					}
				});

		// SETP.2
		expandableListView
				.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

					public void onGroupExpand(int groupPosition) {
						isOpen[groupPosition] = true;

					}
				});

		// 切换到联系人视图
		imgContacts.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				Intent intent = new Intent(context,AllTestPaperShowActivity.class);
				intent.putExtra("categoryId", testPaperId);
				startActivity(intent);

			}
		});
		// 
		imgCreate.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				refreshData();
			}
		});

	}
	
	//用于接受loadUserTestPaperList线程结束后传来的列表，并加以显示
		Handler handlerUserRecord = new Handler() {
			@SuppressWarnings("unchecked")
			public void handleMessage(android.os.Message msg) {
				List<UserTestPaper> list = (ArrayList<UserTestPaper>) msg.obj;
				for(UserTestPaper utp : list){
					
					UserTestPaper ut = new UserTestPaper();
					ut.setUserId(utp.getUserId());
					ut.setQuestionId(utp.getQuestionId());
					ut.setScore(utp.getScore());
					ut.setIsCorrect(utp.getIsCorrect());
					ut.setTestPaperId(utp.getTestPaperId());
					
					
					if(null != utp.getNoSelectAnswer()  && !utp.getNoSelectAnswer().equals("")){
						String fileKey = "img";
						String RequestURL = "http://"+ ip +":"+Global.Common_Port+"/sysKeyController/upload.html";
						UploadUtil uploadUtil = UploadUtil.getInstance();
						Map<String, String> params = new HashMap<String, String>();
						params.put("userId", String.valueOf(utp.getUserId()));
						params.put("questionId", String.valueOf(utp.getQuestionId()));
						params.put("score", String.valueOf(utp.getScore()));
						params.put("isCorrect", String.valueOf(utp.getIsCorrect()));
						params.put("testPaperId", String.valueOf(utp.getTestPaperId()));
						uploadUtil.setOnUploadProcessListener(TestPaperActivity.this);
						uploadUtil.uploadFile(utp.getNoSelectAnswer(), fileKey, RequestURL, params);
					} else {
						String serverIp = "http://"+ ip +":"+Global.Common_Port;
						SubmitUserResult sr = new SubmitUserResult(serverIp, ut);
						Thread thread = new Thread(sr);
						thread.start();
					}
				}
				
			}
		};
		
	//创建新线程从本地试卷列表
	public void loadUserTestPaperList(final int testPaperId) {

		Thread thread = new Thread() {
			@Override
			public void run() {
				UserTestPaperManager uManager = new UserTestPaperManager(context);
				List<UserTestPaper> list = uManager.getUserTestPaperById(testPaperId);
				
				Message message = Message.obtain();
				message.obj = list;
				handlerUserRecord.sendMessage(message);
			}
		};
		
		thread.start();
		thread = null;

	}
	
	private Handler handleSubmit = new Handler(){
		public void handleMessage(android.os.Message msg) {
			hideProgressDialog();
			boolean result = (Boolean)msg.obj;
			if (result){
				
				Toast.makeText(context, "已成功提交到服务器",Toast.LENGTH_SHORT).show();
			}else{
				Toast.makeText(context, "上传服务器失败！！",Toast.LENGTH_SHORT).show();
			}
		};
	};
	
	class SubmitUserResult implements Runnable{
		private UserTestPaper userTestPaper=null;
		private String serverIp = "";
		
		public SubmitUserResult(String serverIp, UserTestPaper userTestPaper){
			this.serverIp = serverIp;
			this.userTestPaper = userTestPaper;
		}

		@Override
		public void run() {
			boolean result = false;
			try {
				result = uTestPaperFactory.createUTestPaper(context).saveUserTestPaper(serverIp, userTestPaper);
			} catch (Exception e) {
				e.printStackTrace();
			}
			Message msg = Message.obtain();
			msg.obj = result;
			handleSubmit.sendMessage(msg);
			
		}
		
	}
	
	private Handler handlerAnswer = new Handler(){
		public void handleMessage(android.os.Message msg) {
			hideProgressDialog();
			TestPaperExtend result = (TestPaperExtend)msg.obj;
			if (result != null){
				if(result.getCanQueryAnswer()){
					
					Intent intent = new Intent(context, UserTestPaperResult.class);
					intent.putExtra("categoryId", Integer.parseInt(result.getId().toString()));
					startActivity(intent);
				} else {
					Toast.makeText(context, "不允许查看答案！",Toast.LENGTH_SHORT).show();
				}
			}else{
				Toast.makeText(context, "网络连接出现问题，请检查！",Toast.LENGTH_SHORT).show();
			}
		};
	};
	
	//创建新线程从本地试卷列表
	public void viewTestPaperAnswer(final String serverIP, final int testPaperId) {
		showProgressDialog();
		Thread thread = new Thread() {
			@Override
			public void run() {
				TestPaperExtend tpe = null;
				try {
					tpe = TestPaperFactory.createTestPaper(context).
							getTestPaperByAnswer(serverIP, testPaperId);
				} catch (Exception e) {
				}
				
				Message message = Message.obtain();
				message.obj = tpe;
				handlerAnswer.sendMessage(message);
			}
		};
		
		thread.start();
		thread = null;

	}
	
	/**
	 * 数据初始化加载
	 */
	private void refreshData(){
		String serverIp = "http://"+ ip +":"+Global.TestPaper_Port;
		TestPaperCategoryRemoteList listItem = null;
		
		try{
			listItem = new TestPaperCategoryRemoteList(serverIp);
		} catch(Exception ex){
			Toast.makeText(context, "网络连接出现问题，请检查！", Toast.LENGTH_LONG).show();
		}
		
		Thread thread = new Thread(listItem);
		thread.start();
		
	}
	
	/**
	  * 取出分类数据
	 */
	 class TestPaperCategoryRemoteList implements Runnable{
		    
		   List<TreeElement> lvCategoryData = new ArrayList<TreeElement>();
		   private String serverIp = "";
			
		    public TestPaperCategoryRemoteList(String serverIp){
		    	this.serverIp = serverIp;
				showProgressDialog();
			}
			
		    @Override
			public void run() {
		    	TestPaperManager manager = new TestPaperManager(context);
					try {
						List<TestPaperCategory> categoryList = manager.getAllTestPaperCategoryList(this.serverIp);
						//List<TestPaperCategory> categoryList = manager.getAllTestPaperCategoryDB();
						
						for(TestPaperCategory rootItem : categoryList){
							
							TreeElement root = new TreeElement();
							root.setId(String.valueOf(rootItem.get_id()));
							root.setLevel(1);
							root.setTitle(rootItem.getName());
							root.setFold(false);
							root.setHasChild(true);
							root.setHasParent(false);
							root.setParentId(null);
							lvCategoryData.add(root);
							
							List<TestPaper> childList = manager.getAllTestPaperList(this.serverIp, rootItem.get_id(), 1, 12000,
									Integer.parseInt(classId.toString()));
					//		List<TestPaper> childList = manager.getAllTestPaperDB(rootItem.get_id());
							
							for(TestPaper childItem : childList){
								
								TreeElement childData = new TreeElement();
								childData.setId(String.valueOf(childItem.get_id()));
								childData.setLevel(2);
								childData.setTitle(childItem.getName());
								childData.setFold(false);
								childData.setHasChild(false);
								childData.setHasParent(true);
								childData.setParentId(String.valueOf(childItem.getParent_id()));
								lvCategoryData.add(childData);
							}
							
						}
						
						
					} catch (Exception e) {
					}
					Message message = Message.obtain();
					message.obj = lvCategoryData;
					handleCourses.sendMessage(message);
			}
		}
	 
		private Handler handleCourses= new Handler(){
			public void handleMessage(android.os.Message msg) {
			
				ArrayList<TreeElement> lvCategoryData = (ArrayList<TreeElement>)msg.obj;
				if (lvCategoryData == null) {
					Toast.makeText(context, "对不起，无数据", Toast.LENGTH_SHORT).show();
				}else{
					pd.setMessage("数据已获取,界面绑定中...");
					bindView();
					pd.setMessage("数据获取中,请稍候...");
					hideProgressDialog();
				}	
			};
		};

	private void isMakeFull() {
		SharedPreferences	sp = getSharedPreferences("parameter", Context.MODE_PRIVATE);
		boolean isFull = sp.getBoolean("isFull", false);
		if(isFull){
			getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN , WindowManager.LayoutParams. FLAG_FULLSCREEN); 
		}
	}

	private void getId() {
		rl_mark = (RelativeLayout) findViewById(R.id.rl_mark);
		imgContacts = (ImageView) findViewById(R.id.imgContacts);
		imgCreate = (ImageView) findViewById(R.id.imgCreate);
		expandableListView = (ExpandableListView) findViewById(R.id.expandableListView);
	}

	// 调用系统发送EMAIL
	public void sendEmail(String Email) {
		Uri uri = Uri.parse("mailto:" + Email);
		Intent it = new Intent(Intent.ACTION_SENDTO, uri);
		startActivity(it);

	}

	// 调用系统发短信
	public void sendMsg(String phone) {
		if (phone.equals("")) {

		} else {
			Uri uri = Uri.parse("smsto:" + phone);

			Intent it = new Intent(Intent.ACTION_SENDTO, uri);

			it.putExtra("sms_body", "");

			startActivity(it);
		}

	}

	// 调用系统打电话
	public void makeCall(String phone) {
		if (phone.equals("")) {

		} else {
			Intent intent = new Intent(Intent.ACTION_CALL);
			intent.setData(Uri.parse("tel:" + phone));
			startActivity(intent);
		}
	}

	private void bindView() {
		getData();
		if (testPapers != null) {
			adapter = new MyExpandableListAdapter(groupCount, groupItem, testPapers, isOpen, context);
			expandableListView.setAdapter(adapter);
		}
	}

	private void getData() {
		groupItem.clear();
		testPapers.clear();
		groupCount.clear();
		list = manager.getAllTestPaperCategoryDB();
		Log.i("Group", "1111111111");

		for (TestPaperCategory p : list) {
			groupItem.add(p.getName());
			String condition = "parent_id = " + p.get_id();
			groupCount.add(manager.getCount(condition));
			testPapers.add(manager.getTestPaperByCondition(p.get_id()));
		}

		isOpen = new boolean[manager.getCount()];
	}


	@Override
	protected void onResume() {
		if (!isFirst) {
			bindView();
		}
		isFirst = false;
		super.onResume();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.i("group", "resultCode:"+resultCode);

//		if (resultCode == 2 && data != null) {// 全组成员移动..跳转到组别选择的回
//			moveToGroupId = data.getExtras().getInt("groupId");
//			contactManager.changeGroupByGroup(moveReGroupId, moveToGroupId);
//		} else if (resultCode == 4) {// 单人移动..跳转到组别选择的回
//			moveToGroupId = data.getExtras().getInt("groupId");
//			contactManager.changeGroupByCotact(moveReContactId, moveToGroupId);
//
//		} else if (resultCode == 5) {// 标记移动..跳转到组别选择的回
//			moveToGroupId = data.getExtras().getInt("groupId");
//
//			contactManager.changeGroupByMark(moveToGroupId);
//			contactManager.initIsMark("false");
//
//		} else if (resultCode == 500) {// 从手机导入的回
//
//			getData();
//
//			
//		} else if (resultCode == 600 || resultCode==700) {// 从SIM卡导入或SDCard还原的
//
//		}
//		else if(resultCode ==1000){//设置全屏的回
//			getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN ,                
//                    WindowManager.LayoutParams. FLAG_FULLSCREEN);    
//			
//		}
		
		super.onActivityResult(requestCode, resultCode, data);
	}
	

	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		if (keyCode == KeyEvent.KEYCODE_BACK) {
				Intent intent = new Intent();
				intent.setClass(TestPaperActivity.this, FMainActivity.class);
				startActivity(intent);
		}
		return super.onKeyDown(keyCode, event);
	}

	
class MyExpandableListAdapter extends BaseExpandableListAdapter {
		public PopupWindow popupWindow;
		private Context mcontext = null;
		private boolean[] isOpen = null;
		private LayoutInflater mInflater;
		private TestPaperManager manager;
		private boolean check = true;
		private View popView;
		private int id;
		private List<Integer> contactCountByGroup;
		private List<String> groupItem = new ArrayList<String>();
		private List<List<TestPaper>> testPapers = new ArrayList<List<TestPaper>>();

		

		public MyExpandableListAdapter(List<Integer> contactCountByGroup,List<String> groupItem,
				List<List<TestPaper>> testPapers, boolean[] isOpen, Context context) {
			this.contactCountByGroup = contactCountByGroup;
			this.isOpen = isOpen;
			this.mcontext = context;
			this.groupItem = groupItem;
			this.testPapers = testPapers;
			this.mInflater = LayoutInflater.from(mcontext);
			manager = new TestPaperManager(context);
		}

		public MyExpandableListAdapter() {

		}

		public void refresh() {
			this.notifyDataSetChanged();
		}

		public Object getChild(int groupPosition, int childPosition) {
			return testPapers.get(groupPosition);
		}

		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		public int getChildrenCount(int groupPosition) {
			return testPapers.get(groupPosition).size();
		}

		public View getChildView(int groupPosition, final int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			convertView = mInflater.inflate(R.layout.child, null);
			final List<TestPaper> testPapers = (List<TestPaper>) getChild(groupPosition, childPosition);
			ImageView imgPhoto = (ImageView) convertView.findViewById(R.id.imgContact);
			TextView childName = (TextView) convertView.findViewById(R.id.childName);
			TextView testPaperStauts = (TextView) convertView.findViewById(R.id.testPaperStauts);
			Bitmap bt = BitmapFactory.decodeResource(mcontext.getResources(),R.drawable.home_sxlx_n);
			imgPhoto.setImageBitmap(bt);
		//	imgPhoto.setImageResource(R.drawable.bg_photo_default);
			childName.setTextColor(Color.BLACK);
			childName.setText(testPapers.get(childPosition).getName());
			
		//	Log.i("testPaperStauts", "testPaperStautstestPaperStauts="+testPapers.get(childPosition).getStauts());
			
			testPaperStauts.setText(TestPaperStautsEnum.valueOf(testPapers.get(childPosition).getStauts()).toName());
			ImageView imgChk = (ImageView) convertView.findViewById(R.id.imgChk);
			id = testPapers.get(childPosition).getId();
//			if (contactManager.getContactById(id).getIsMark().equals("true"))
//				imgChk.setVisibility(View.VISIBLE);

			ImageView popImg = (ImageView) convertView.findViewById(R.id.popImg);
			popImg.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					
//					int totalPageCount = TestPaperFactory.createTestPaper(context).getByPageCount(1, "favStauts=1 and test_paper_id = "+ testPapers.get(childPosition).get_id() +" order by 0 + name");
//					if(totalPageCount == 0){
//						Toast.makeText(context, "当前收藏为空！", Toast.LENGTH_SHORT).show();
//					} else {
//						Intent intent = new Intent(context, MyTestPaperQuestionByFavorActivity.class);
//						intent.putExtra("categoryId", testPapers.get(childPosition).get_id());
//						startActivity(intent);
//					}
					
				}
			});

			return convertView;
		}

		public Object getGroup(int groupPosition) {
			return groupItem.get(groupPosition);
		}

		public int getGroupCount() {
			return groupItem.size();
		}

		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			convertView = mInflater.inflate(R.layout.group, null);
			TextView textView = (TextView) convertView.findViewById(R.id.groupto);
			TextView textCount = (TextView) convertView.findViewById(R.id.txtCount);
		
			textCount.setText("("+contactCountByGroup.get(groupPosition).toString()+")");

			textView.setTextColor(Color.BLACK);
			textView.setText(getGroup(groupPosition).toString());
			Log.i("hubin", "groupPosition" + groupPosition);
			if (isOpen[groupPosition]) {
				Drawable leftDrawable = mcontext.getResources().getDrawable(R.drawable.expend);
				leftDrawable.setBounds(0, 0, leftDrawable.getMinimumWidth(),leftDrawable.getMinimumHeight());
				textView.setCompoundDrawables(leftDrawable, null, null, null);
			} else {
				Drawable leftDrawable = mcontext.getResources().getDrawable(R.drawable.unexpend);
				leftDrawable.setBounds(0, 0, leftDrawable.getMinimumWidth(),leftDrawable.getMinimumHeight());
				textView.setCompoundDrawables(leftDrawable, null, null, null);
			}

			return convertView;
		}

		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}

		public boolean hasStableIds() {
			return true;
		}

	}


private Handler handler = new Handler(){
	@Override
	public void handleMessage(Message msg) {
		switch (msg.what) {
		case TO_UPLOAD_FILE:
		//	toUploadFile();
			break;
		
		case UPLOAD_INIT_PROCESS:
		//	Toast.makeText(context, msg.arg1,Toast.LENGTH_SHORT).show();
		//	progressBar.setMax(msg.arg1);
			break;
		case UPLOAD_IN_PROCESS:
		//	Toast.makeText(context, msg.arg1,Toast.LENGTH_SHORT).show();
			break;
		case UPLOAD_FILE_DONE:
			String result = "上传成功！耗时："+UploadUtil.getRequestTime()+"秒";
			Toast.makeText(context, result,Toast.LENGTH_SHORT).show();
			
			break;
		default:
			break;
		}
		super.handleMessage(msg);
	}
	
};

@Override
public void onUploadDone(int responseCode, String message) {
	Message msg = Message.obtain();
	msg.what = UPLOAD_FILE_DONE;
	msg.arg1 = responseCode;
	msg.obj = message;
	handler.sendMessage(msg);
	
}

@Override
public void onUploadProcess(int uploadSize) {
	Message msg = Message.obtain();
	msg.what = UPLOAD_IN_PROCESS;
	msg.arg1 = uploadSize;
	handler.sendMessage(msg );
	
}

@Override
public void initUpload(int fileSize) {
	Message msg = Message.obtain();
	msg.what = UPLOAD_INIT_PROCESS;
	msg.arg1 = fileSize;
	handler.sendMessage(msg );
	
}

}
