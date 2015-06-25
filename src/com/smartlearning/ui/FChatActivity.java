package com.smartlearning.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.Request.Method;
import com.feng.adapter.BookPartAdapter;
import com.feng.adapter.ChatMsgAdapter;
import com.feng.util.Utils;
import com.feng.vo.BookPartListVO;
import com.feng.vo.ChatMsgEntity;
import com.feng.vo.MemberInfo;
import com.feng.vo.MessageInfo;
import com.feng.vo.TearchResListVO;
import com.feng.volley.FRestClient;
import com.feng.volley.FastJsonRequest;
import com.feng.view.*;
import com.smartlearning.R;
import com.smartlearning.constant.Global;
import com.smartlearning.utils.CommonUtil;
import com.smartlearning.utils.SpUtil;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 0代表正在发送
 * 1代表发送成功
 * 00代表发送是失败
 * @author xy
 *
 */
public class FChatActivity extends Activity {
	EditText et_sendmessage;
	//LinearLayout more_operate;
	private ListView mListView;
	private ChatMsgAdapter mAdapter;
	private final String PREFERENCES_NAME = "cloudTeam_userinfo";
	private SharedPreferences preferences ;
	
	private List<ChatMsgEntity> mDataArrays = new ArrayList<ChatMsgEntity>();
	private List<ChatMsgEntity> unSendmDataArrays = new ArrayList<ChatMsgEntity>();

	private List<MessageInfo> msgInfo=new ArrayList<MessageInfo>();
	
	
	RelativeLayout ll_facechoose;
	//ImageButton btn_face;

	InputMethodManager imm;
	MemberInfo memberInfo;//聊天对象

	closeHintThread ct=null;

	private Spinner spinner;
	private ArrayAdapter<String> adapter;
	private Context mContext;
	private LinearLayout title;
	private TextView titleText;
	private RelativeLayout rl_chat;
	
	private String serverIp;
	private long classId;
	
	private LinearLayout title_cfm;
	
	@Override  
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mContext = this;
		
		// 设置没有title
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_chat);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		//Utils.db = FinalDb.create(this, "mydb");
		
		initTitle();
		
		init();
		
		//接受新消息
		Utils.updataMsg=new Handler(){
			public void handleMessage(Message msg) {
				if(msg.what==Utils.UPDATA_MSG){//更新聊天信息
					MessageInfo msgInfo=(MessageInfo) msg.obj;
					Log.d("TAG", "来的新消息.."+msgInfo.toString());
					//Utils.db.save(msgInfo);//将消息保存到数据库
					if(msgInfo.getMsg_from().equals(Utils.CHAT_UID)){
						mAdapter.addNewMsg(msgInfo);
					}else{
						//如果不是发给当前聊天对象发来的消息就在上方的提示栏中提示
						/*MemberInfo m=Utils.db.findById(msgInfo.getMsg_from(), MemberInfo.class);
						if(null!=m){
							String str=m.getNickname()+"发来消息";
							msgHint.setText(str);
							msgHint.setVisibility(View.VISIBLE);
							ct=new closeHintThread();
							ct.start();
						}*/
					}
				}
			};
		};
		
		initSp();
		
		initSpinner();
	}
	
	private void initSp(){
		//初始化preferences
		preferences = SpUtil.getSharePerference(mContext);
		serverIp = preferences.getString("serverIp","");
		classId = preferences.getLong("classId",0);
	}

	private void initTitle(){
		title = (LinearLayout)findViewById(R.id.title_back);
		titleText = (TextView)findViewById(R.id.title_text);
		title_cfm = (LinearLayout)findViewById(R.id.title_cfm);
		titleText.setText(R.string.online_title);
		title.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				FChatActivity.this.finish();
				//退出时移除当前的聊天对象
				Utils.CHAT_UID="";
			}
		});
		
		title_cfm.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setChatEdit();
			}
		});
		
	}

	/**
	 * 初始化数据
	 */
	private void init() {
		Utils.handlerInput=new Handler(){
			@Override
			public void handleMessage(Message msg) {
				if(msg.what==Utils.CLOSE_INPUT){//关闭软键盘
					imm.hideSoftInputFromWindow(FChatActivity.this.getCurrentFocus().getWindowToken(),  
	                         InputMethodManager.HIDE_NOT_ALWAYS);
				}else if(msg.what==Utils.CLOSE_MSG_HINT){
					//隐藏消息提示
					//msgHint.setVisibility(View.GONE);
				}
			}
		};
		
		imm= (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		// 启动activity时不自动弹出软键盘
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		et_sendmessage = (EditText) findViewById(R.id.et_sendmessage);
		mListView=(ListView) findViewById(R.id.listview);
		ll_facechoose=(RelativeLayout) findViewById(R.id.ll_facechoose);
		//btn_face=(ImageButton) findViewById(R.id.btn_face);
		
		rl_chat = (RelativeLayout)findViewById(R.id.rl_chat);

		
		mAdapter = new ChatMsgAdapter(this, mDataArrays);
		mListView.setAdapter(mAdapter);
		mListView.setSelection(mListView.getCount() - 1);
		
	}
	
	
	public int clickPosition=-1;
	
	/**
	 * 对按钮添加点击事件处理
	 * @param v
	 *     View
	 */
	public void btnClick(View v) {
		switch (v.getId()) {
		/*case R.id.btn_back:
			FChatActivity.this.finish();
			//退出时移除当前的聊天对象
			Utils.CHAT_UID="";
			break;*/
		case R.id.btn_send:
			// 发送消息
			String sendMsg=et_sendmessage.getText().toString();
			if(sendMsg.length()>0){
				sendMessage("0",sendMsg,true);
			}
			break;
		case R.id.chatView:
			// 关闭表情,点击标题栏时
			Utils.handler.sendEmptyMessage(1);
			break;
		case R.id.chat_camera:
			//启动相机
			Intent intent = new Intent();
			Intent intent_camera = getPackageManager()
					.getLaunchIntentForPackage("com.android.camera");
			if (intent_camera != null) {
				intent.setPackage("com.android.camera");
			}
			intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
			FChatActivity.this.startActivityForResult(intent, Utils.GET_CAMERA);
			break;
		case R.id.chat_picture:
			intent=new Intent(FChatActivity.this,FScaleImageFromSdcardActivity.class);
			FChatActivity.this.startActivityForResult(intent, Utils.SHOW_ALL_PICTURE);
			//设置切换动画，从右边进入，左边退出
			overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);  				
			
			break;
//		case R.id.chat_file:
//			//intent=new Intent(ChatActivity.this,LocationFileLiabraryActivity.class);
////			intent=new Intent(ChatActivity.this,FileLibraryActivity.class);
////			ChatActivity.this.startActivityForResult(intent, 1001);
////			//设置切换动画，从右边进入，左边退出
////			overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);  				
//			
//			break;
//		case R.id.chat_person:
//			
//			break;
		default:
			break;
		}
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		Log.d("TAG", "requestCode"+requestCode+" ,resultCode"+resultCode);
		if(requestCode ==Utils.GET_CAMERA && resultCode == Activity.RESULT_OK && null != data){
			   String sdState=Environment.getExternalStorageState();
			   if(!sdState.equals(Environment.MEDIA_MOUNTED)){
				   Toast t=Utils.showToast(getApplicationContext(), "未找到SDK", Toast.LENGTH_LONG);
				   t.show();
			    Log.d("TAG", "sd card unmount");
			    return;
			   }
			   new DateFormat();
			   String name= DateFormat.format("yyyyMMdd_hhmmss", Calendar.getInstance(Locale.CHINA))+".jpg";
			   Bundle bundle = data.getExtras();
			   //获取相机返回的数据，并转换为图片格式
			   Bitmap bitmap;
			   String filename = null;
			   bitmap = (Bitmap)bundle.get("data");
			   FileOutputStream fout = null;
			   //定义文件存储路径
			   File file = new File("/sdcard/cloudteam/");
			   if(!file.exists()){
				   file.mkdirs();
			   }
			   filename=file.getPath()+"/"+name;
			   try {
			    fout = new FileOutputStream(filename);
			    //对图片进行压缩
			    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fout);
			   } catch (FileNotFoundException e) {
			    e.printStackTrace();
			   }finally{
			    try {
			     fout.flush();
			     fout.close();
			    } catch (IOException e) {
			     e.printStackTrace();
			    }
			   }
			   Log.d("TAG", "相片路径"+filename);
			   //去另一个页面查看图片
			   Intent intent=new Intent(FChatActivity.this,FCameraActivity.class);
			   intent.putExtra("camera", filename);
			   FChatActivity.this.startActivityForResult(intent, Utils.SHOW_CAMERA);

			   
		}else if(requestCode==Utils.SHOW_CAMERA&&resultCode==Utils.SHOW_CAMERA_RESULT){
			Bundle bundle = data.getExtras();
			Object camera=bundle.get("imgUrl");
			Log.d("TAG", "需要发送照相的图片到服务器"+camera.toString());
			//将图片发送到聊天界面
			if(camera.toString().length()>0){
				sendMessage("0","["+camera.toString()+"]",true);
			}
			//将图片异步发送到服务器，并且把发送的图片显示到聊天框中
			
		}else if(requestCode==Utils.SHOW_ALL_PICTURE&&resultCode==Utils.SHOW_PICTURE_RESULT){
			Log.d("TAG", "需要将选择的图片发送到服务器");
			List<String> bmpUrls=new ArrayList<String>();
			
			Bundle bundle = data.getExtras();
			Object[] selectPictures=(Object[]) bundle.get("selectPicture");
			for (int i = 0; i < selectPictures.length; i++) {
				Log.d("TAG", "selectPictures[i]"+selectPictures[i]);
				String bmpUrl=FScaleImageFromSdcardActivity.map.get(Integer.parseInt(selectPictures[i].toString()));
				bmpUrls.add(bmpUrl);
				Log.d("TAG", "bmp"+bmpUrl);
				sendMessage("0","["+bmpUrl+"]",true);
			}
			Log.d("TAG", "bmps.size()"+bmpUrls.size());
			Toast.makeText(FChatActivity.this, "选择图片数"+selectPictures.length, Toast.LENGTH_LONG).show();
			//将图片发送到服务器
			
			
			
		}else if(requestCode==1001&&resultCode==10001){
			Log.d("TAG", "需要将选择的文件发送到服务器");
			Bundle bundle = data.getExtras();
			String filePath=bundle.getString("filePath");
			Toast.makeText(FChatActivity.this, "filePath"+filePath, Toast.LENGTH_LONG).show();
		}
	}
	
	
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& ((FaceRelativeLayout) findViewById(R.id.FaceRelativeLayout))
						.hideFaceView()) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	/**
	 * 发送消息
	 */
	public void sendMessage(String id,String sendMsg,final boolean isNew) {
		String contString =sendMsg ;
		//聊天信息的保存
		final MessageInfo msg=new MessageInfo();
		if(!(id.endsWith("0"))){//如果有id就是从数据库中取出的数据
			msg.set_id(Integer.parseInt(id));
		}
		msg.setMsg_content(contString); 
		//msg.setMsg_from(preferences.getString("USERID", ""));
		//msg.setMsg_to(memberInfo.getUid());
		msg.setMsg_acceptTime(Utils.getNowDate());
		
//		if(msg.getMsg_from().equals(msg.getMsg_to())){//如果是自己给自己发消息，就去个人信息界面
//			msg.setMsg_from("");
//		}
		Log.d("TAG", "msg发送的信息..."+msg.toString());
		if (contString.length() > 0) {
			ChatMsgEntity entity = new ChatMsgEntity();
			entity.setDb_id(msg.get_id()+"");
			entity.setDate(Utils.getNowDate());
			//entity.setName(preferences.getString("USERID", ""));
			entity.setMsgType(false);
			entity.setText(contString);
			entity.setStatus("0");
			if(isNew){
				mDataArrays.add(entity);
			}
			mAdapter.notifyDataSetChanged();
			et_sendmessage.setText("");
			mListView.setSelection(mListView.getCount() - 1);
		}
		
		//聊天信息的发送到服务器
		
	}
	 
	

	/**
	 * 隐藏提示消息
	 *
	 */
	class closeHintThread extends Thread {

		@Override
		public void run() {
			super.run();
			try {
				sleep(3000);
				Utils.handlerInput.sendEmptyMessage(Utils.CLOSE_MSG_HINT);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	@Override
	protected void onDestroy() {
		//退出的时候将没有发送出去的消息保存到数据库中
//		for (int i = 0; i < unSendmDataArrays.size(); i++) {
//			Utils.db.save(unSendmDataArrays.get(i));
//		}
		if(null!=ct&&ct.isAlive()){
			ct.interrupt();
		}
		super.onDestroy();
	}
	
	
	private static final String[] m={"全体人员","数学","语文","英语","政治","历史","地理","物理","化学","生物"};
	private void initSpinner(){
		
	  	spinner = (Spinner) findViewById(R.id.Spinner);
        //将可选内容与ArrayAdapter连接起来
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,m);
         
        //设置下拉列表的风格
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
         
        //将adapter 添加到spinner中
        spinner.setAdapter(adapter);
         
        //添加事件Spinner事件监听  
        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				//Toast.makeText(mContext, m[position],Toast.LENGTH_LONG).show();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
		});
         
        //设置默认值
        spinner.setVisibility(View.VISIBLE);
	    
	}
	
	private void setChatEdit(){
		int visibility = rl_chat.getVisibility();
		if(visibility == View.GONE)
			rl_chat.setVisibility(View.VISIBLE);
		else
			rl_chat.setVisibility(View.GONE);
	}


}
