package com.smartlearning.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
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
import com.smartlearning.factory.TestPaperFactory;
import com.smartlearning.model.TestPaper;
import com.smartlearning.model.TestPaperCategory;

public class myFavortActivity extends Activity {

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
		
		setContentView(R.layout.activity_favor);

		context = this;
//		groupManager = new GroupManager(context);
//		contactManager = new StudentManager(context);
		manager = new TestPaperManager(context);

		// 初始化标记状态，false:全部未标记；true:全部标记
//		contactManager.initIsMark("false");

		getId();
		
		sharedPreferences = getSharedPreferences("serverIpObj", MODE_PRIVATE);
		ip = sharedPreferences.getString("serverIP", "");
		classId = sharedPreferences.getLong("classId", 0);
		userId = sharedPreferences.getString("user", "");
		
		if (getIntent().getIntExtra("categoryId", 0)!=0)
			testPaperId = getIntent().getIntExtra("categoryId", 0);

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

				TestPaper testPaper = testPapers.get(groupPosition).get(childPosition);
				if (isMarkedState) {

//					ImageView img = (ImageView) v.findViewById(R.id.imgChk);
//					if (img.getVisibility() == View.GONE) {
//						contactManager.initIsMarkById("true", contact.getId());
//						img.setVisibility(View.VISIBLE);
//					} else {
//						contactManager.initIsMarkById("false", contact.getId());
//
//						img.setVisibility(View.GONE);
//					}
				} else {
//					Intent intent = new Intent(context,
//							AddNewContactActivity.class);
//					Bundle bundle = new Bundle();
//					bundle.putInt("requestCode", INFO);
//					bundle.putInt("id", contact.getId());
//					intent.putExtras(bundle);
//					startActivity(intent);

					// Toast.makeText(context, contact.toString(),
					// Toast.LENGTH_LONG).show();
				}
				return false;
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
//		imgContacts.setOnClickListener(new View.OnClickListener() {
//
//			public void onClick(View v) {
////				Intent intent = new Intent(context,AllContactShowActivity.class);
////				intent.putExtra("categoryId", testPaperId);
////				startActivity(intent);
//
//			}
//		});
		// 新建联系人
//		imgCreate.setOnClickListener(new View.OnClickListener() {
//
//			public void onClick(View v) {
//				String serverIp = "http://"+ ip +":8080";
//				loadStudentList(serverIp, classId);
//			}
//		});

	}
	
	
	//用于接受loadTestPaperList线程结束后传来的列表，并加以显示
//	Handler handlerStudent = new Handler() {
//		@SuppressWarnings("unchecked")
//		public void handleMessage(android.os.Message msg) {
//			List<TestPaper> sList = (ArrayList<TestPaper>)msg.obj;
//			if (sList == null) {
//				Toast.makeText(myFavortActivity.this, "对不起, 无数据请下载。", Toast.LENGTH_SHORT).show();
//			}else{
//				pd.setMessage("数据已获取,界面绑定中...");
//				bindView();
//				pd.setMessage("数据获取中,请稍候...");
//				hideProgressDialog();
//			}	
//			
//		}
//	};
	
	//创建新线程从本地试卷列表
//	public void loadStudentList(final String serverIP, final Long classId) {
//		
//		showProgressDialog();
//		Thread thread = new Thread() {
//			@Override
//			public void run() {
//				List<TestPaper> sList = contactManager.getAllStudentList(serverIP, classId);
//				Message message = new Message();
//				message.obj = sList;
//				handlerStudent.sendMessage(message);
//			}
//		};
//		
//		thread.start();
//		thread = null;
//
//	}

	private void isMakeFull() {
		SharedPreferences	sp = getSharedPreferences("parameter", Context.MODE_PRIVATE);
		boolean isFull = sp.getBoolean("isFull", false);
		if(isFull){
			getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN , WindowManager.LayoutParams. FLAG_FULLSCREEN); 
		}
	}

	private void getId() {
		rl_mark = (RelativeLayout) findViewById(R.id.rl_mark);
//		imgContacts = (ImageView) findViewById(R.id.imgContacts);
//		imgCreate = (ImageView) findViewById(R.id.imgCreate);
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
		if (keyCode == KeyEvent.KEYCODE_MENU) {

			// rl_mark.setVisibility(View.VISIBLE);
	//		loadBottomMenu();
	//		Toasttool.MyToast(context, "menu");
			if (bottomMenuGrid.getVisibility() == View.VISIBLE) {
				bottomMenuGrid.setVisibility(View.GONE);
			} else {
				bottomMenuGrid.setVisibility(View.VISIBLE);
			}

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
			Bitmap bt = BitmapFactory.decodeResource(mcontext.getResources(),R.drawable.widget_icon_notify);
			imgPhoto.setImageBitmap(bt);
		//	imgPhoto.setImageResource(R.drawable.bg_photo_default);
			childName.setTextColor(Color.BLACK);
			childName.setText(testPapers.get(childPosition).getName());

			ImageView imgChk = (ImageView) convertView.findViewById(R.id.imgChk);
			id = testPapers.get(childPosition).getId();
//			if (contactManager.getContactById(id).getIsMark().equals("true"))
//				imgChk.setVisibility(View.VISIBLE);

			ImageView popImg = (ImageView) convertView.findViewById(R.id.popImg);
			popImg.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					
					int totalPageCount = TestPaperFactory.createTestPaper(context).getByPageCount(1, "favStauts=1 and test_paper_id = "+ testPapers.get(childPosition).get_id() +" order by 0 + name");
					if(totalPageCount == 0){
						Toast.makeText(context, "当前收藏为空！", Toast.LENGTH_SHORT).show();
					} else {
						Intent intent = new Intent(context, MyTestPaperQuestionByFavorActivity.class);
						intent.putExtra("categoryId", testPapers.get(childPosition).get_id());
						startActivity(intent);
					}
					
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

}
