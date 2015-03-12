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
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
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
import com.smartlearning.model.TestPaperExtend;
import com.smartlearning.model.UserTestPaper;
import com.smartlearning.ui.TestPaperActivity.SubmitUserResult;
import com.smartlearning.utils.UploadUtil;
import com.smartlearning.utils.UploadUtil.OnUploadProcessListener;

public class AllTestPaperShowActivity  extends Activity implements OnUploadProcessListener{
	public final static int EDIT_TO_UPDATE_ALL = 20;// 点击编辑菜单项跳转到编辑界面
	public final static int CONTACT_MOVETO_All = 30;// 点击移动..菜单项跳转到组别选择
	public final static int MARK_MOVE_TO_ALL = 40;// 点击移动标记..菜单项跳转到组别选择
	public final static int ADD_NEW_ALL = 50;// 点击右上角新建跳转到添加联系人界面
	public final static int INFO_ALL = 60;// 点击查看详情

	private ListView listView;
	private AllListAdapeter adapter;
	private Context context;
	private List<TestPaper> list;
	private TestPaperManager manager;
	private boolean isMarkedState = false;// 是否是标记状态
	private int totalcount;// 联系人总数

	private int moveReContactId;// 移动操作的联系人ID
	private int moveToGroupId = -1;// 移动操作的目标分组ID

	private EditText edtSearch;// 搜索编辑框
	private ImageView imgDelInput;// 用于删除搜框的输入
	private ImageView imgBack;// 返回键
	private ImageView imgCreate;// 添加联系人
	private RelativeLayout rl_mark;
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
	private SharedPreferences sharedPreferences;
	ProgressDialog pd = null;
	String ip = "";
	Long classId;

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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		isMakeFull();
		
		setContentView(R.layout.alltestpapser_show_view);
		context = this;
//		contactManager = new ContactManager(context);
		manager  = new TestPaperManager(context);
		sharedPreferences = getSharedPreferences("userInfo", MODE_PRIVATE);
		ip = sharedPreferences.getString("serverIp", null);
		classId = sharedPreferences.getLong("classId", 0);
		//总数
//		getTestPaperCount();
		totalcount = manager.getCount("1=1");
		listView = (ListView) findViewById(R.id.listView);
		edtSearch = (EditText) findViewById(R.id.edtSearch);
		imgDelInput = (ImageView) findViewById(R.id.imgDelInput);
		imgBack = (ImageView) findViewById(R.id.imgBack);
		imgCreate = (ImageView) findViewById(R.id.imgCreate);
		rl_mark = (RelativeLayout) findViewById(R.id.rl_mark);
		
		//获得所有列表
		getTestPaperAll(); 

//		imgCreate.setOnClickListener(new View.OnClickListener() {
//
//			public void onClick(View v) {
////				Intent intent = new Intent(context, AddNewContactActivity.class);
////				intent.putExtra("requestCode", ADD_NEW_ALL);
////				startActivity(intent);
//
//			}
//		});

		// 为listView注册上下文菜单SETP.一
		listView.setOnCreateContextMenuListener(this);
		listView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View v, int position,
					long arg3) {
				 final TestPaper testPaper = list.get(position);
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
			}
		});

		edtSearch.setHint("共有" + totalcount + "个作业");
		edtSearch.addTextChangedListener(new TextWatcher() {

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (edtSearch.getText().toString().equals("")) {

					imgDelInput.setVisibility(View.GONE);

					edtSearch.setHint("共有" + totalcount + "个作业");

				} else {

					imgDelInput.setVisibility(View.VISIBLE);

				}
				String condition = edtSearch.getText().toString();
				getTestPaperAll(condition); 
				
//				List<TestPaper> list = manager.getTestPaperByConditions(condition);
//				Toast.makeText(context, "list=="+list.size(), Toast.LENGTH_SHORT).show();
//				if (list != null)
//					bindView(list);
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			public void afterTextChanged(Editable s) {

			}
		});

		imgDelInput.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				edtSearch.setText("");
//				list = contactManager.getAllContacts();
//				if (list != null)
//					bindView(list);

			};
		});

		imgBack.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				finish();

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
							uploadUtil.setOnUploadProcessListener(AllTestPaperShowActivity.this);
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
	private Handler handlerTpCount = new Handler(){
		public void handleMessage(android.os.Message msg) {
			int gettotalcount = (Integer)msg.obj;
			
			Toast.makeText(context, "gettotalcount=="+gettotalcount, Toast.LENGTH_SHORT).show();
			totalcount = gettotalcount;
		};
	};
	
	public void getTestPaperCount() {

		Thread thread = new Thread() {
			@Override
			public void run() {
				int totalcount = manager.getCount("1=1");
				Message message = Message.obtain();
				message.obj = totalcount;
				handlerTpCount.sendMessage(message);
			}
		};
		
		thread.start();
		thread = null;

	}
	
	
	private Handler handlerTpList = new Handler(){
		public void handleMessage(android.os.Message msg) {
			list = (ArrayList<TestPaper>) msg.obj;
			if (list != null)
			 bindView(list);
		};
	};
	
	/**
	 * 获得所有试卷
	 */
	public void getTestPaperAll() {

		Thread thread = new Thread() {
			@Override
			public void run() {
				List<TestPaper> list = manager.getTestPaperAll();
				Message message = Message.obtain();
				message.obj = list;
				handlerTpList.sendMessage(message);
			}
		};
		
		thread.start();
		thread = null;

	}
	
	private Handler handlerTpCList = new Handler(){
		public void handleMessage(android.os.Message msg) {
			List<TestPaper> list = (ArrayList<TestPaper>) msg.obj;
			if (list != null)
			 bindView(list);
		};
	};
	
	/**
	 * 条件获得试卷
	 */
	public void getTestPaperAll(final String condition) {

		Thread thread = new Thread() {
			@Override
			public void run() {
				List<TestPaper> list = manager.getTestPaperByConditions(condition);
				Message message = Message.obtain();
				message.obj = list;
				handlerTpCList.sendMessage(message);
			}
		};
		
		thread.start();
		thread = null;

	}
	
	private void isMakeFull() {
		SharedPreferences	sp = getSharedPreferences("parameter", Context.MODE_PRIVATE);
		boolean isFull = sp.getBoolean("isFull", false);
		if(isFull){
			getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN ,                
                    WindowManager.LayoutParams. FLAG_FULLSCREEN); }
	}

	private void bindView(List<TestPaper> list) {

		adapter = new AllListAdapeter(context, list);
		listView.setAdapter(adapter);
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		if (resultCode == 30) {// 单人移动..跳转到组别选择的回
//			moveToGroupId = data.getExtras().getInt("groupId");
//			contactManager.changeGroupByCotact(moveReContactId, moveToGroupId);
//			contactManager.initIsMarkById("false", moveReContactId);
//		} else if (resultCode == 40) {// 标记移动..跳转到组别选择的回
//			moveToGroupId = data.getExtras().getInt("groupId");
//			contactManager.changeGroupByMark(moveToGroupId);
//			contactManager.initIsMark("false");
//
//		}
		super.onActivityResult(requestCode, resultCode, data);
	}

//	@Override
//	protected void onResume() {
////		list = contactManager.getAllContacts();
////		if (list != null)
////			bindView(list);
////		super.onResume();
//	}
	

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

class AllListAdapeter extends BaseAdapter {
	private LayoutInflater mInflater;
	private Context mcontext;
	private List<TestPaper> list;
	private boolean check = true;
	public PopupWindow popupWindow;
	private View popView;

	public AllListAdapeter(Context context, List<TestPaper> list) {
		this.mInflater = LayoutInflater.from(context);
		this.mcontext = context;
		this.list = list;

	}

	public int getCount() {
		return list.size();
	}

	public Object getItem(int position) {

		return list.get(position);
	}

	public long getItemId(int position) {

		return list.get(position).getId();
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = mInflater.inflate(R.layout.child, null);
		final TestPaper contact = (TestPaper) getItem(position);

		ImageView imgPhoto = (ImageView) convertView
				.findViewById(R.id.imgContact);
		TextView txtName = (TextView) convertView.findViewById(R.id.childName);
//		TextView txtPhone = (TextView) convertView
//				.findViewById(R.id.childPhone);
		// Log.i("testGroupService", contact.toString());
		Bitmap bt = BitmapFactory.decodeResource(mcontext.getResources(),R.drawable.home_sxlx_n);
		imgPhoto.setImageBitmap(bt);
		ImageView imgChk = (ImageView) convertView.findViewById(R.id.imgChk);

		txtName.setText(contact.getName());

		ImageView popImg = (ImageView) convertView.findViewById(R.id.popImg);
//		popImg.setOnClickListener(new View.OnClickListener() {

//			public void onClick(View v) {
//				// if (check) {
//				// check = false;
//				if (popupWindow == null) {
//					popView = mInflater.inflate(R.layout.popupwindow_view,
//							null, false);
//					popupWindow = new PopupWindow(popView,
//							LayoutParams.WRAP_CONTENT,
//							LayoutParams.WRAP_CONTENT, true);
//
//					popupWindow.setBackgroundDrawable(new BitmapDrawable());
//
//				}
//				// if (popupWindow.isShowing())
//				// popupWindow.dismiss();
//				// Toasttool.MyToast(mcontext, "pop");
//				popupWindow.showAsDropDown(v);
//
//				ImageButton imgCall = (ImageButton) popView
//						.findViewById(R.id.imgCall);
//				ImageButton imgMsg = (ImageButton) popView
//						.findViewById(R.id.imgMsg);
//				ImageButton imgEmail = (ImageButton) popView
//						.findViewById(R.id.imgEmail);
//
//				imgCall.setOnClickListener(new View.OnClickListener() {
//
//					public void onClick(View v) {
//
//						Toasttool.MyToast(mcontext, "call");
//
//						((AllContactShowActivity) mcontext).makeCall(contact
//								.getPhone());
//
//						popupWindow.dismiss();
//					}
//
//				});
//				imgMsg.setOnClickListener(new View.OnClickListener() {
//
//					public void onClick(View v) {
//						Toasttool.MyToast(mcontext, "Msg");
//						((AllContactShowActivity) mcontext).sendMsg(contact
//								.getPhone());
//
//						popupWindow.dismiss();
//
//					}
//				});
//				imgEmail.setOnClickListener(new View.OnClickListener() {
//
//					public void onClick(View v) {
//						Toasttool.MyToast(mcontext, "Email");
//						((AllContactShowActivity) mcontext).sendEmail(contact
//								.getE_mail().toString());
//
//						popupWindow.dismiss();
//
//					}
//				});
//
//			}
//		});

		return convertView;
	}

}
