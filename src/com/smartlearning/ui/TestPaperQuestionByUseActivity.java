package com.smartlearning.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.smartlearning.R;
import com.smartlearning.biz.TestPaperManager;
import com.smartlearning.constant.Global;
import com.smartlearning.crop.CropMainActivity;
import com.smartlearning.db.DBAccess;
import com.smartlearning.factory.TestPaperFactory;
import com.smartlearning.factory.UTestPaperFactory;
import com.smartlearning.model.QuestionTypeEnum;
import com.smartlearning.model.TestPaper;
import com.smartlearning.model.TestPaperQuestion;
import com.smartlearning.model.TestPaperStautsEnum;
import com.smartlearning.model.UserTestPaper;
import com.smartlearning.utils.ImageTools;
import com.smartlearning.utils.Tool;

/**
 *试卷题目
 */
public class TestPaperQuestionByUseActivity extends Activity{

	/**
	 *业务对象 
	 */
	private TestPaperManager testPaperManager = null;
	private UTestPaperFactory uTestPaperFactory = null;
	ProgressDialog pd = null;
	ImageView inform_unread_img;
	ListView inform_list;
	Button inform_back, inform_refresh, addInform,noDoQuestion;
	int last_message_id;
	MyAdapter adapter;
	private SharedPreferences sharedPreferences;
	String ip = "";
	Long classId;
	private static final int PAGESIZE = 1; // 每次取几条记录
	private int pageIndex = 0; // 用于保存当前是第几页,0代表第一页
	/**
	 * 震动效果
	 */
	private Vibrator vibrator;
	private Chronometer chronometer = null;
	private ProgressBar progressBar = null;
	private long baseLine = 0L;
	private int totalSubjectCount = 0;
	private int iconOffset = 20;
	private long elapsedTime = 0L;
	private boolean hadChosenAnswer = false;
	int testPaperId = 0;
	public static Map<Integer, Integer> isSelected;
	private List<TestPaperQuestion> testPaperQuestions = null;
	private int currentSubjectLocation = 0;
	private String ken;
	private TestPaperFactory testPaperFactory = null;
	private TestPaper testPaper = null;
	int qIndex = 0;
	public static final int NONE = 0;
    public static final int PHOTOHRAPH = 1;// 拍照
    public static final int PHOTOZOOM = 2; // 缩放
    public static final int PHOTORESOULT = 3;// 结果
    public static final String IMAGE_UNSPECIFIED = "image/*";
    public String imageIndex= "";
    private String userId = "";
    Button answerButton;
    TextView titleTextView, totalTimeTextView,totalScoreTextView;
    private Button imgBack;// 返回键
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
		setContentView(R.layout.process);
		
		sharedPreferences = getSharedPreferences("userInfo", MODE_PRIVATE);
		ip = sharedPreferences.getString("serverIp", null);
		classId = sharedPreferences.getLong("classId", 0);
		userId = sharedPreferences.getString("user", "");
		vibrator = (Vibrator)getSystemService(ContextWrapper.VIBRATOR_SERVICE);
		
		getParamValue();
		initView();
		fillHeader();
		
		loadData();
		
		inform_refresh.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				
				RefreshData();
			}
		});
		
		noDoQuestion.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				showProgressDialog();
				Intent intent = new Intent();
				intent.putExtra("questionNO", totalSubjectCount);
				intent.putExtra("categoryId", testPaperId);
				intent.setClass(TestPaperQuestionByUseActivity.this, examSummary.class);
				startActivity(intent);
				hideProgressDialog();
			}
		});
		
		imgBack.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(TestPaperQuestionByUseActivity.this, TestPaperActivity.class);
				startActivity(intent);

			}
		});
		
		try {
			testPaperFactory.createTestPaper(TestPaperQuestionByUseActivity.this).modifyTestPaper(TestPaperStautsEnum.finishDoing.toValue(), testPaperId);
		} catch (Exception e) {
		}
	//	DBAccess.deleteProfile(TestPaperQuestionByUseActivity.this, String.valueOf(testPaperId));
		
	}
	

	private void getParamValue() {
		if (getIntent().getIntExtra("categoryId", 0)!=0)
		testPaperId = getIntent().getIntExtra("categoryId", 0);
		if (getIntent().getIntExtra("questionIndex", 0)!=0) {
			qIndex  = getIntent().getIntExtra("questionIndex", 0);
			pageIndex = qIndex - 1;
		}
	}
	 	
	
	//用于接受loadUserTestPaperList线程结束后传来的列表，并加以显示
	Handler handlerTestPaperRecord = new Handler() {
		public void handleMessage(android.os.Message msg) {
			List<TestPaper> testPaperList  = (ArrayList<TestPaper>) msg.obj;
			if(testPaperList != null){
				try {
					TestPaper testPaper = null;
					for(int i = 0; i < testPaperList.size(); i++){
						testPaper = testPaperList.get(0);
					}
					titleTextView.setText(String.format(getResources().getString(R.string.titleTag), testPaper.getName()));
					totalTimeTextView.setText(getResources().getString(R.string.infoTotalTimeTag) + testPaper.getPassScore());
					totalScoreTextView.setText(getResources().getString(R.string.infoTotalScoreTag) + testPaper.getTotalScore());
					chronometer.setFormat(getResources().getString(R.string.infoElapsedTimeTag)+"%s");
					baseLine = SystemClock.elapsedRealtime();
				//	progressBar.setProgress(0);
					chronometer.setBase(baseLine);
					chronometer.start();
				} catch (Exception e) {
					Toast.makeText(TestPaperQuestionByUseActivity.this, "对不起, 加载失败", Toast.LENGTH_SHORT).show();
				}
			} 
		}
	};
	
	//创建新线程从本地试卷列表
	public void loadTestPaperList() {
		
		Thread thread = new Thread() {
			@Override
			public void run() {
				List<TestPaper> testPaperList = testPaperFactory.createTestPaper(TestPaperQuestionByUseActivity.this).getTestPaperById(testPaperId);
				
				Message message = new Message();
				message.obj = testPaperList;
				handlerTestPaperRecord.sendMessage(message);
			}
		};
		
		thread.start();
		thread = null;

	}
	
	
	//用于接受loadUserTestPaperList线程结束后传来的列表，并加以显示
	Handler handlerTestPaperCount = new Handler() {
		public void handleMessage(android.os.Message msg) {
			int  totalCount  = (Integer) msg.obj;
			if(totalCount != 0){
				boolean flag = DBAccess.profileExist(TestPaperQuestionByUseActivity.this,String.valueOf(testPaperId));
				if(!flag){
					DBAccess.createProfile(TestPaperQuestionByUseActivity.this, String.valueOf(testPaperId), totalCount);
				}
			}
		}
	};
	
	//创建新线程从本地试卷列表
	public void loadTestPaperCount() {
		
		Thread thread = new Thread() {
			@Override
			public void run() {
				int totalCount = TestPaperFactory.createTestPaper(TestPaperQuestionByUseActivity.this).getByPageCount(PAGESIZE, " test_paper_id = "+ testPaperId +" order by 0 + name");
				Message message = new Message();
				message.obj = totalCount;
				handlerTestPaperCount.sendMessage(message);
			}
		};
		
		thread.start();
		thread = null;

	}
	
	private void fillHeader() {
		
		loadTestPaperList();
		loadTestPaperCount();
//		List<TestPaper> testPaperList = testPaperFactory.createTestPaper(TestPaperQuestionByUseActivity.this).getTestPaperById(testPaperId);
//		
//		if(testPaperList.size() > 0){
//			testPaper = testPaperList.get(0);
//		} 

//		final TextView titleTextView = (TextView)findViewById(R.id.textpaperTitle);
//		final TextView totalTimeTextView = (TextView)findViewById(R.id.testpaperInfoTotalTime);
//		chronometer = (Chronometer)findViewById(R.id.testpaperInfoElapsedTime);
//		final TextView totalScoreTextView = (TextView)findViewById(R.id.testpaperInfoTotalScore);
//		progressBar = (ProgressBar)findViewById(R.id.testProgressBar);
//		final Button answerButton = (Button)findViewById(R.id.submitButton);
//		try {
//			
//			titleTextView.setText(String.format(getResources().getString(R.string.titleTag), testPaper.getName()));
//			totalTimeTextView.setText(getResources().getString(R.string.infoTotalTimeTag) + testPaper.getPassScore());
//			totalScoreTextView.setText(getResources().getString(R.string.infoTotalScoreTag) + testPaper.getTotalScore());
//			chronometer.setFormat(getResources().getString(R.string.infoElapsedTimeTag)+"%s");
//			baseLine = SystemClock.elapsedRealtime();
//			progressBar.setProgress(30);
//			chronometer.setBase(baseLine);
//			chronometer.start();
//		} catch (Exception e) {
//			Toast.makeText(TestPaperQuestionByUseActivity.this, "对不起, 加载失败", Toast.LENGTH_SHORT).show();
//		}
//		DBAccess.deleteProfile(TestPaperQuestionByUseActivity.this, String.valueOf(testPaperId));
//		int totalCount = testPaperFactory.createTestPaper(TestPaperQuestionByUseActivity.this).getByPageCount(PAGESIZE, " test_paper_id = "+ testPaperId +" order by 0 + name");
//		
//		if(totalCount != 0){
//			boolean flag = DBAccess.profileExist(this,String.valueOf(testPaperId));
//			if(!flag){
//				DBAccess.createProfile(TestPaperQuestionByUseActivity.this, String.valueOf(testPaperId), totalCount);
//			}
//		}
		//确定
		answerButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						
						int totalNum = DBAccess.getCountMatrix(TestPaperQuestionByUseActivity.this,String.valueOf(testPaperId));
						final List<TestPaperQuestion> testPaperQuestions = testPaperFactory.createTestPaper(TestPaperQuestionByUseActivity.this).getByPagerAll("test_paper_id = "+ testPaperId +" order by 0 + name");
						String str = "";
						if(testPaperQuestions.size() == totalNum){
							str = "试题已做完，您确认交卷吗?";
						} else {
							int fNum = testPaperQuestions.size() - totalNum;
							str = "您当前还有 "+ fNum +"未做，确认交卷吗?";
						}
					    AlertDialog.Builder localBuilder = new AlertDialog.Builder(TestPaperQuestionByUseActivity.this);
					    localBuilder.setTitle("确认")
					    .setMessage(str)
					    .setPositiveButton("确定", new DialogInterface.OnClickListener(){
							@Override
							public void onClick(DialogInterface dialog, int which) {
							String isCorrect = "";
							double score = 0;
							int questionId = 0;
							String noSelectAnswer = null;
							String[] rcValue = DBAccess.getCheckMatrix(TestPaperQuestionByUseActivity.this,String.valueOf(testPaperId));
							for(TestPaperQuestion tpq : testPaperQuestions){
								
								questionId = tpq.getQuestionId();
								//单选
								if(QuestionTypeEnum.SingleSelect.toValue() == tpq.getQuestionType()){
									String  pIndex = "";
									//选中
									if(rcValue != null){
										pIndex = rcValue[Integer.parseInt(tpq.getName()) - 1];
									}
									
									String userAnswer = Tool.numberToString(Integer.parseInt(pIndex)).toUpperCase();
									String answer = tpq.getAnswer().toUpperCase();
									if(userAnswer.equals(answer)){
										isCorrect = "R";
										score = tpq.getScore();
									} else {
										isCorrect = "W";
										score = 0;
									}
								}
								//多选
								if(QuestionTypeEnum.MultiSelect.toValue() == tpq.getQuestionType()){
									String userAnswer = "";
									//选中
									if(rcValue != null){
										String  pIndex = rcValue[Integer.parseInt(tpq.getName()) - 1];
										char[] cIndex = pIndex.toCharArray();
										for(int j = 0; j < cIndex.length; j++){
											userAnswer += Tool.numberToString(Integer.parseInt(String.valueOf(cIndex[j]))).toUpperCase().toString();
										}
								    }
								
									String answer = tpq.getAnswer().toUpperCase();
									if(userAnswer.equals(answer)){
										isCorrect = "R";
										score = tpq.getScore();
									} else {
										isCorrect = "W";
										score = 0;
									}
							
							  }
							 //判断
							 if(QuestionTypeEnum.Judge.toValue() == tpq.getQuestionType()){
								 String  pIndex = "";
								//选中
								if(rcValue != null){
									pIndex = rcValue[Integer.parseInt(tpq.getName()) - 1];
								}
						
								String userAnswer = String.valueOf(Integer.parseInt(pIndex));
								String answer = tpq.getAnswer();

								if(userAnswer.equals(answer)){
									isCorrect = "W";
									score = 0;
								} else {
									isCorrect = "R";
									score = tpq.getScore();
								}
								
							 }
							 
							 //非选题
							 if(QuestionTypeEnum.NoSelect.toValue() == tpq.getQuestionType()){
								 String  pIndex = "";
								//选中
								if(rcValue != null){
									
									pIndex = rcValue[Integer.parseInt(tpq.getName()) - 1];
									
									if(pIndex.equals("9999")){
										noSelectAnswer = "";
									} else{
										String answerP = pIndex;
										String url = Environment.getExternalStorageDirectory() + "/elearningDB/"+answerP+".jpg";
										Log.i("DBAccess", "url=="+url);
										File f = new File(url);
										if(f.exists()){
											noSelectAnswer = url;
										}
									}
								}
							 }
							 
							//保存数据
							saveUserTestPaper(isCorrect, score, questionId, testPaperId, noSelectAnswer);
							}
							
							try {
								testPaperFactory.createTestPaper(TestPaperQuestionByUseActivity.this).modifyTestPaper(TestPaperStautsEnum.finish.toValue(), testPaperId);
							} catch (Exception e) {
							}
							//删除数据
							DBAccess.deleteProfile(TestPaperQuestionByUseActivity.this, String.valueOf(testPaperId));
							Toast.makeText(TestPaperQuestionByUseActivity.this, "试卷提交成功。", Toast.LENGTH_SHORT).show();
							Intent intent = new Intent(TestPaperQuestionByUseActivity.this, TestPaperActivity.class);
							startActivityForResult(intent, 1);
							}
					    	 
					     }).setNegativeButton("取消", null).create().show();
					   }
				});
		
	}

	public void initView() {
		inform_refresh = (Button) findViewById(R.id.inform_refresh);
		inform_list = (ListView) findViewById(R.id.message_list);
		noDoQuestion = (Button) findViewById(R.id.noDoQuestion);
		titleTextView = (TextView)findViewById(R.id.textpaperTitle);
		totalTimeTextView = (TextView)findViewById(R.id.testpaperInfoTotalTime);
		chronometer = (Chronometer)findViewById(R.id.testpaperInfoElapsedTime);
		totalScoreTextView = (TextView)findViewById(R.id.testpaperInfoTotalScore);
		progressBar = (ProgressBar)findViewById(R.id.testProgressBar);
		answerButton = (Button)findViewById(R.id.submitButton);
		imgBack = (Button) findViewById(R.id.imgBack);
	}
	
	/**
	 * 数据初始化加载
	 */
	private void loadData(){
		String serverIp = "http://"+ ip +":"+Global.TestPaper_Port;
		LoadTestPaperQuestionList listItem = new LoadTestPaperQuestionList(serverIp, classId);
		
		Thread thread = new Thread(listItem);
		thread.start();
		
	}
	
	private void RefreshData(){
		String serverIp = "http://"+ ip +":"+Global.TestPaper_Port;
		RefreshTestPaperQuestionList listItem = new RefreshTestPaperQuestionList(serverIp, classId);
		
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
				
				if (id != -1) {
					
//					int flashback = forums.size() - position - 1;
//					
//					OnlineForum onlineForum = forums.get(flashback);
//		
//					Intent intent = new Intent();
//					intent.setClass(OnlineForumActivity.this, ForumShowActivity.class);
//					intent.putExtra("forum_id", onlineForum.getId().toString());
//					intent.putExtra("name", onlineForum.getName());
//					intent.putExtra("content", onlineForum.getQuestion());
//					startActivity(intent);
				}
			}
		});

		
		/**
		 * 刷新信息
		 */
		inform_refresh.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
			//	RefreshData();
			//	Toast.makeText(OnlineForumActivity.this, "刷新成功", Toast.LENGTH_SHORT).show();
			}
		});
		
		/**
		 * 添加
		 */
		addInform.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
//				startActivity(intent2);

			}
		});

	}
	
	private Handler handleQuestions = new Handler(){
		public void handleMessage(android.os.Message msg) {
			
			testPaperQuestions = (ArrayList<TestPaperQuestion>)msg.obj;
			if (testPaperQuestions == null) {
				Toast.makeText(TestPaperQuestionByUseActivity.this, "无作业试题，请同步作业", Toast.LENGTH_SHORT).show();
				hideProgressDialog();
			}else{
				pd.setMessage("数据已获取,界面绑定中...");
				adapter = new MyAdapter(testPaperQuestions, TestPaperQuestionByUseActivity.this);
				inform_list.setAdapter(adapter);
				
				pd.setMessage("数据获取中,请稍候...");
				hideProgressDialog();
				
			}	
		}

	
	};
	
	
	private Handler handleISQuestions = new Handler(){
		public void handleMessage(android.os.Message msg) {
			
			boolean isflag = (Boolean)msg.obj;
			if (isflag == false) {
				Toast.makeText(TestPaperQuestionByUseActivity.this, "数据同步失败", Toast.LENGTH_SHORT).show();
			}else{
				pd.setMessage("数据已获取,界面绑定中...");
				Toast.makeText(TestPaperQuestionByUseActivity.this, "数据同步成功", Toast.LENGTH_SHORT).show();
				fillHeader();
				loadData();
				pd.setMessage("数据获取中,请稍候...");
				hideProgressDialog();
			}	
		}

	
	};
	
	
	/**
	 * 根据题目的类型，创建不同的答案。
	 * @param ll 答案区域的LinearLayout
	 * @param type 答案类型
	 */
	private void createAnswerView(LinearLayout ll,int type, String index){
		
		if(QuestionTypeEnum.SingleSelect.toValue() == type){
			createRadioGroup(ll, index);
		} else if(QuestionTypeEnum.MultiSelect.toValue() == type){
			createMutilChecked(ll, index);
		} else if(QuestionTypeEnum.Judge.toValue() == type){
			createTrueOrFalse(ll, index);
		}else if(QuestionTypeEnum.NoSelect.toValue() == type){
			createNoSelect(ll, index);
		}
	}
	
	private void createNoSelect(LinearLayout ll, final String index) {
		Button btn = new Button(TestPaperQuestionByUseActivity.this);
		btn.setText("拍照");
		btn.setCompoundDrawablesWithIntrinsicBounds(0, R.anim.icon_share, 0, 0);
		btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				//调用系统的拍照功能
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                imageIndex = userId + "_" + testPaperId + "_" + index;
                
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/elearningDB/", imageIndex+".jpg")));
                startActivityForResult(intent, PHOTOHRAPH);
                DBAccess.updateProfile(TestPaperQuestionByUseActivity.this, String.valueOf(testPaperId),
						0, imageIndex, Integer.parseInt(index), true);
			}
		});
		Button btn1 = new Button(TestPaperQuestionByUseActivity.this);
		btn1.setText("查看");
		btn1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				imageIndex = userId + "_" + testPaperId + "_" + index;
				String url = Environment.getExternalStorageDirectory() + "/elearningDB/"+ imageIndex+".jpg";
				
				Intent intent = new Intent();
				intent.setClass(TestPaperQuestionByUseActivity.this, CropMainActivity.class);
				intent.putExtra("url", url);
				startActivity(intent);
//				Intent intent = new Intent("android.intent.action.VIEW");
//				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//				Uri uri = Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/elearningDB/", imageIndex+".jpg"));
////				Toast.makeText(TestPaperQuestionByUseActivity.this, "uri-=="+uri, Toast.LENGTH_SHORT).show();
//				intent.setDataAndType(uri, "image/*");
////				Intent intent = new Intent(TestPaperQuestionByUseActivity.this, ShowUserAnswerActivity.class);
////				intent.putExtra("questionId", questionId);
//				startActivity(intent);
			}
		});
		ll.addView(btn1);
		ll.addView(btn);
	}

	private void createTrueOrFalse(LinearLayout ll, final String index) {
		RadioGroup rg = new RadioGroup(TestPaperQuestionByUseActivity.this);
		RadioButton rBtn = null;
		RadioButton wBtn = null;
		String[] str = {"对","错"};
		String[] rcValue = DBAccess.getCheckMatrix(TestPaperQuestionByUseActivity.this,String.valueOf(testPaperId));
		for(int i=0;i<str.length;i++){
			final RadioButton rb = new RadioButton(this);
			rb.setTag(i);
			rb.setPadding(55,15,0,20);
			//rb.setText(answers.get(i).getContent());
			rb.setTextSize(18);
			if(str[i].toString().equals("对")){
				rBtn = rb;
				rb.setButtonDrawable(R.anim.radiobutton_yes);
			}else{
				wBtn = rb;
				rb.setButtonDrawable(R.anim.radiobutton_no);
			}
			
			//选中
			if(rcValue != null){
				String  pIndex = rcValue[Integer.parseInt(index) - 1];
				
				if(str[i].toString().equals("对") && Integer.parseInt(rb.getTag().toString()) == Integer.parseInt(pIndex)) {
					rBtn.setChecked(true);
				} 
				
				if(str[i].toString().equals("错") && Integer.parseInt(rb.getTag().toString()) == Integer.parseInt(pIndex)) {
					wBtn.setChecked(true);
				} 
			}
			
			rb.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					
					if(rb.isChecked()){
						DBAccess.updateProfile(TestPaperQuestionByUseActivity.this, String.valueOf(testPaperId),
								0, rb.getTag().toString(), Integer.parseInt(index), true);
						loadData();
					}
				}
			});
		}
		rg.addView(rBtn);
		rg.addView(wBtn);
		rg.setOrientation(LinearLayout.HORIZONTAL);
		ll.addView(rg);
	}

	private void createMutilChecked(LinearLayout ll, final String index) {
		ll.setOrientation(LinearLayout.HORIZONTAL);
		String[] str = {"A","B","C","D"};
		String[] rcValue = DBAccess.getCheckMatrix(TestPaperQuestionByUseActivity.this,String.valueOf(testPaperId));
		for(int i=0; i<str.length; i++){
			final CheckBox item = new CheckBox(this);
			item.setText(str[i]);
			item.setTag(i);
			item.setButtonDrawable(R.anim.mycheck);
			item.setPadding(5, 0, iconOffset, 0);
			item.setTextSize(18);
			item.setTextColor(R.color.black);
			
			//选中
			if(rcValue != null){
				String  pIndex = rcValue[Integer.parseInt(index) - 1];
				Log.i("pIndexpIndexpIndexpIndex", "dddd=="+pIndex);
				char[] cIndex = pIndex.toCharArray();
				for(int j = 0; j < cIndex.length; j++){
					
					if(Integer.parseInt(item.getTag().toString()) == Integer.parseInt(String.valueOf(cIndex[j]))) {
						item.setChecked(true);
					}
				}
			}
			
			item.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
				if(item.isChecked()){
						String checkValue = item.getTag().toString();
						char cValue = checkValue.charAt(0);
						DBAccess.updateProfile(TestPaperQuestionByUseActivity.this, String.valueOf(testPaperId), 0, 
								cValue, Integer.parseInt(index), true, Integer.parseInt(item.getTag().toString()));
						loadData();
					} else {
						//String checkValue = item.getTag().toString();
						char cValue = "9".charAt(0);
						Log.i("pIndexpIndexpIndexpIndex", "cValue=="+cValue);
						DBAccess.updateProfile(TestPaperQuestionByUseActivity.this, String.valueOf(testPaperId), 0, 
								cValue, Integer.parseInt(index), true, Integer.parseInt(item.getTag().toString()));
						loadData();
					}
				}
				
			});
			
			ll.addView(item);
		}
		
	}

	private void createRadioGroup(LinearLayout ll, final String index) {
		RadioGroup rg = new RadioGroup(TestPaperQuestionByUseActivity.this);
		String[] str = {"A","B","C","D"};
		
		isSelected = new HashMap<Integer, Integer>();
		String[] rcValue = DBAccess.getCheckMatrix(TestPaperQuestionByUseActivity.this,String.valueOf(testPaperId));
		
		for(int i=0; i<str.length; i++){
			final RadioButton aItem = new RadioButton(TestPaperQuestionByUseActivity.this);
			aItem.setText(str[i]);
			aItem.setTag(i);
			aItem.setButtonDrawable(R.anim.myradio);
			aItem.setPadding(5, 0, iconOffset, 0);
			aItem.setTextSize(18);
			aItem.setTextColor(R.color.black);
			
			//选中
			if(rcValue != null){
				String  pIndex = rcValue[Integer.parseInt(index) - 1];
				if(Integer.parseInt(aItem.getTag().toString()) == Integer.parseInt(pIndex)) {
					aItem.setChecked(true);
				}
			}
			
			aItem.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if(aItem.isChecked()){
						DBAccess.updateProfile(TestPaperQuestionByUseActivity.this, String.valueOf(testPaperId),
								0, aItem.getTag().toString(), Integer.parseInt(index), true);
						loadData();
					}
				}
			});
			
			rg.setOrientation(LinearLayout.HORIZONTAL);
			rg.addView(aItem);
		}
		ll.addView(rg);
	}
	
	

	/**
	 * 更新进度条
	 * @param progress
	 */
	private void updateProgress(int progress){
		progressBar.setProgress(Math.round((float)progress/totalSubjectCount*100));
	}
	
	/**
	 * 当Activity失去焦点时，此方法被调用。
	 * 在此方法中，把计时暂停。
	 */
	@Override
	protected void onPause() {
		super.onPause();
		elapsedTime += SystemClock.elapsedRealtime() - baseLine;
		baseLine = SystemClock.elapsedRealtime();
		chronometer.stop();
	}
	
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
			testPaperManager = new TestPaperManager(TestPaperQuestionByUseActivity.this);
			try{
			//	testPaperQuestions = testPaperManager.getAllTestPaperQuestionList(this.serverIp, testPaperId);
				testPaperQuestions = testPaperManager.getByPager(pageIndex, PAGESIZE, " test_paper_id = "+ testPaperId +" order by 0 + name");
				
			} catch(Exception e){
			}
			Message message = Message.obtain();
			message.obj = testPaperQuestions;
			handleQuestions.sendMessage(message);
		}
	}
	
	
	
	class RefreshTestPaperQuestionList implements Runnable{
		boolean testPaperQuestions = false;
		private String serverIp = "";
		private Long classId;
			
	    public RefreshTestPaperQuestionList(String serverIp,Long classId){
	    	this.serverIp = serverIp;
	    	this.classId = classId;
			showProgressDialog();
		}
	    
		@Override
		public void run() {
			testPaperManager = new TestPaperManager(TestPaperQuestionByUseActivity.this);
			try{
				//testPaperQuestions = testPaperManager.getAllTestPaperQuestionList(this.serverIp, testPaperId);
			//	testPaperQuestions = testPaperManager.getByPager(pageIndex, PAGESIZE, " test_paper_id = "+ testPaperId +" order by 0 + name");
				testPaperQuestions = testPaperManager.isTestPaperQuestionList(this.serverIp, testPaperId);
				
			} catch(Exception e){
			}
			Message message = Message.obtain();
			message.obj = testPaperQuestions;
			handleISQuestions.sendMessage(message);
		}
	}
	
	
	/*用于获取用于显示的分页信息*/
	private String getPagerInfo(){
		String pagerInfo = "第{0}页 ,共{1}页";
		int totalPageCount = testPaperManager.getByPageCount(PAGESIZE, " test_paper_id = "+ testPaperId +" order by 0 + name");
		totalSubjectCount = totalPageCount;
		return MessageFormat.format(pagerInfo, pageIndex+1, totalPageCount);
	}
	
	private void saveUserTestPaper(String isCorrect, double score,
			int questionId, int testPaperId, String noSelectAnswer) {
		
		UserTestPaper userTestPaper = new UserTestPaper();
		
		userTestPaper.setUserId(Integer.parseInt(userId));
		userTestPaper.setQuestionId(questionId);
		userTestPaper.setScore(String.valueOf(score));
		userTestPaper.setIsCorrect(isCorrect);
		userTestPaper.setTestPaperId(testPaperId);
		userTestPaper.setNoSelectAnswer(noSelectAnswer);
		
		uTestPaperFactory.createUTestPaper(TestPaperQuestionByUseActivity.this).InsertUserTestPaper(userTestPaper);
		
	}

	/**
	 *适配器
	 */
	class MyAdapter extends BaseAdapter {

		private LayoutInflater inflater = null;
		Context context;
		private List<TestPaperQuestion> testPaperQuestions = null;
		private int item_layout_res = R.layout.processitem;
		
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
				Button btnFirst = (Button) convertView.findViewById(R.id.btnFirst);
				btnFirst.setOnClickListener(new View.OnClickListener() {
					
					public void onClick(View v) {
						pageIndex=0;
						updateProgress(pageIndex+1);
						loadData();
					//	Toast.makeText(TestPaperQuestionByUseActivity.this, "第"+(pageIndex+1)+"页", Toast.LENGTH_SHORT).show();
					}
				 });
							
							
				Button btnPrev = (Button) convertView.findViewById(R.id.btnPrev);
				btnPrev.setOnClickListener(new View.OnClickListener() {
					
					public void onClick(View v) {
						if (pageIndex>0){
							 pageIndex--;
							 updateProgress(pageIndex+1);
							 loadData();
					//	     Toast.makeText(TestPaperQuestionByUseActivity.this, "第"+(pageIndex+1)+"页", Toast.LENGTH_SHORT).show();
						}else{
							 Toast.makeText(TestPaperQuestionByUseActivity.this, "已经是第一页了", Toast.LENGTH_SHORT).show();
						}
						
					}
				 });
							
				Button btnNext = (Button) convertView.findViewById(R.id.btnNext);
				btnNext.setOnClickListener(new View.OnClickListener() {
					
					public void onClick(View v) {
						int totalPageCount = testPaperManager.getByPageCount(PAGESIZE, " test_paper_id = "+ testPaperId +" order by 0 + name");
						if (pageIndex < totalPageCount-1){
							pageIndex++;
							updateProgress(pageIndex+1);
						    loadData();
						 //  Toast.makeText(TestPaperQuestionByUseActivity.this, "第"+(pageIndex+1)+"页", Toast.LENGTH_SHORT).show();
						}else{
						   Toast.makeText(TestPaperQuestionByUseActivity.this, "已经最后一页了", Toast.LENGTH_SHORT).show();
						}
					}
				});
							
				Button btnLast = (Button) convertView.findViewById(R.id.btnLast);
				btnLast.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						int totalPageCount = testPaperManager.getByPageCount(PAGESIZE, " test_paper_id = "+ testPaperId +" order by 0 + name");
						pageIndex = totalPageCount-1;
						updateProgress(pageIndex+1);
						loadData();
					//	Toast.makeText(TestPaperQuestionByUseActivity.this, "第"+(pageIndex+1)+"页", Toast.LENGTH_SHORT).show();
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
				
				holder.sIndexView = (TextView) convertView.findViewById(R.id.subjectIndex);
				holder.inform_unread_img = (ImageView) convertView.findViewById(R.id.inform_unread_img);
				holder.group1 = (RadioGroup) convertView.findViewById(R.id.group1);
				holder.group2 = (RadioGroup) convertView.findViewById(R.id.group2);
				holder.item_layout_OptionCheckeBoxs = (LinearLayout) convertView.findViewById(R.id.optionCheckeBox);
				holder.answerLayout = (LinearLayout) convertView.findViewById(R.id.answerLayout);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			hadChosenAnswer = false;
			int flashback = position - 1;
			currentSubjectLocation = flashback;
			
			Integer questionType = testPaperQuestions.get(flashback).getQuestionType();
			holder.sIndexView.setText(String.format(getResources().getString(R.string.subjectIndex),
					testPaperQuestions.get(flashback).getName()) +  QuestionTypeEnum.valueOf(testPaperQuestions.get(flashback).getQuestionType()).toName() + ":");
			byte[] result = testPaperQuestions.get(flashback).getNote();
			Bitmap bitmap = ImageTools.getBitmapFromByte(result);
			holder.inform_unread_img.setImageBitmap(bitmap);
//			holder.inform_unread_img.setOnClickListener(new View.OnClickListener() {
//				public void onClick(View v) {
//					Toast.makeText(TestPaperQuestionByUseActivity.this, "ddddvv", Toast.LENGTH_SHORT).show();
//				}
//			});
			
			createAnswerView(holder.answerLayout, questionType, testPaperQuestions.get(flashback).getName());;

			return convertView;
		}

	}
	
	public final class ViewHolder {
		public TextView sIndexView;
		public ImageView inform_unread_img;
		public RadioGroup group1;
		public RadioGroup group2;
		public LinearLayout item_layout_OptionCheckeBoxs;
		public LinearLayout answerLayout;
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	 //调用startActivityResult，返回之后的回调函数
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == NONE)
            return;
        // 拍照
        if (requestCode == PHOTOHRAPH) {
            //设置文件保存路径这里放在跟目录下
        	
          //  File picture = new File(Environment.getExternalStorageDirectory() + "/elearningDB/"+imageIndex+".jpg");
          //  startPhotoZoom(Uri.fromFile(picture));
            
			String url = Environment.getExternalStorageDirectory() + "/elearningDB/"+ imageIndex+".jpg";
			
			Intent intent = new Intent();
			intent.setClass(TestPaperQuestionByUseActivity.this, CropMainActivity.class);
			intent.putExtra("url", url);
			startActivity(intent);
            
        }
         
        if (data == null)
            return;
         
        // 读取相册缩放图片
        if (requestCode == PHOTOZOOM) {
         //   startPhotoZoom(data.getData());
        }
        // 处理结果PHOTO_REQUEST_CUT
        if (requestCode == PHOTORESOULT) {
            Bundle extras = data.getExtras();
            if (extras != null) {
                Bitmap photo = extras.getParcelable("data");
                saveMyBitmap(photo);
//                ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                photo.compress(Bitmap.CompressFormat.JPEG, 75, stream);// (0 - 100)压缩文件
            }
 
        }
 
        super.onActivityResult(requestCode, resultCode, data);
    }
 
//    public void startPhotoZoom(Uri uri) {
//        Intent intent = new Intent("com.android.camera.action.CROP");
//        intent.setDataAndType(uri, IMAGE_UNSPECIFIED);
//        intent.putExtra("crop", "true");
//        // aspectX aspectY 是宽高的比例
//        intent.putExtra("aspectX", 1);
//        intent.putExtra("aspectY", 1);
//        // outputX outputY 是裁剪图片宽高
//        intent.putExtra("outputX", 350);
//        intent.putExtra("outputY", 350);
//        intent.putExtra("return-data", true);
//        startActivityForResult(intent, PHOTORESOULT);
//    }
    
    /**
     * 保存图片
     * @param mBitmap
     */
    public void saveMyBitmap(Bitmap mBitmap)  {
        File f = new File(Environment.getExternalStorageDirectory() + "/elearningDB/"+imageIndex+".jpg");
        FileOutputStream fOut = null;
        try {
                fOut = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
                e.printStackTrace();
        }
        mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
        try {
                fOut.flush();
        } catch (IOException e) {
                e.printStackTrace();
        }
        try {
                fOut.close();
        } catch (IOException e) {
                e.printStackTrace();
        }
  }
    
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

}
