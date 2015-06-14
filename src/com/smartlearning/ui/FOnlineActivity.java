package com.smartlearning.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.feng.adapter.CommentAdapter;
import com.feng.adapter.CommentReplyAdapter;
import com.feng.util.BitmapUtils;
import com.feng.util.FormFile;
import com.feng.util.SocketHttpRequester;
import com.feng.util.StringUtils;
import com.feng.util.UploadUtil;
import com.feng.util.Utils;
import com.feng.view.FaceRelativeLayout;
import com.feng.vo.ChatMsgEntity;
import com.feng.vo.MessageInfo;
import com.smartlearning.R;
import com.smartlearning.constant.Global;
import com.smartlearning.utils.SpUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.gesture.Prediction;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Toast;


public class FOnlineActivity extends Activity implements OnClickListener{
	private ListView lv_user_comments;
	private LinearLayout ll_comment;
	private Button btn_comment, btn_reply;
	private EditText edt_comment_content, edt_reply;
	private TextView tv_reply;

	private CommentAdapter commentAdapter;
	private CommentReplyAdapter commentReplyAdapter;

	private static final int ONE_COMMENT_CODE = -1;
	private static final int TWO_COMMENT_CODE = 2;

	private List<HashMap<String, Object>> list_comment; // 一级评论数据
	private List<List<HashMap<String, Object>>> list_comment_child; // 二级评论数据
	
	private LinearLayout title;
	private TextView titleText;
	
	private Spinner spinner;
	private EditText et_sendmessage;
	private ArrayAdapter<String> adapter;
	private Context mContext;
	private InputMethodManager imm;
	private ImageButton chat_picture;
	private String sendToObj = "";//指定发送给的用户
	private String sendImagePath = "";//发送图片的地址
	private SharedPreferences preferences ;
	private String serverIp;
	private long classId;
	private int userId;
	private String truename = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.f_online);
		
		this.mContext = this;
		
		initTitle();
		
		initSpinner();
		
		initSp();
		
		initList();
		initView();
		initCommentData();
	}
	
	private void initSp(){
		//初始化preferences
		preferences = SpUtil.getSharePerference(mContext);
		serverIp = preferences.getString("serverIp","");
		classId = preferences.getLong("classId",0);
		String uid = preferences.getString("user","0");
		userId = Integer.parseInt(uid);
		truename = preferences.getString("truename","my");
	}

	private void initCommentData() {
		//TODO 服务器获取列表
		commentReplyAdapter = null;
		commentAdapter = new CommentAdapter(this, list_comment,
				list_comment_child, myOnitemcListener, commentReplyAdapter);
		lv_user_comments.setAdapter(commentAdapter);
	}

	private void initList() {
		list_comment = new ArrayList<HashMap<String, Object>>();
		list_comment_child = new ArrayList<List<HashMap<String, Object>>>();
	}

	private void initView() {
		lv_user_comments = (ListView) this.findViewById(R.id.lv_comments);
		//btn_comment = (Button) this.findViewById(R.id.btn_main_send);
		tv_reply = (TextView) this.findViewById(R.id.tv_user_reply);
		
		chat_picture = (ImageButton)this.findViewById(R.id.chat_picture);

		et_sendmessage = (EditText) findViewById(R.id.et_sendmessage);
		
		//btn_comment.setOnClickListener(this);
		
		Utils.handlerInput=new Handler(){
			@Override
			public void handleMessage(Message msg) {
				if(msg.what==Utils.CLOSE_INPUT){//关闭软键盘
					imm.hideSoftInputFromWindow(FOnlineActivity.this.getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
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

	}

	private OnClickListener myOnitemcListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			int position = (Integer) v.getTag();
			
		}
	};


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		/*case R.id.btn_main_send:
			extracted();
			break;*/

		default:
			break;
		}
	}

	private void extracted() {
		showDialog(ONE_COMMENT_CODE);
	}

	
	private void initTitle(){
		title = (LinearLayout)findViewById(R.id.title_back);
		titleText = (TextView)findViewById(R.id.title_text);
		titleText.setText(R.string.online_title);
		title.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				FOnlineActivity.this.finish();
			}
		});
	}
	
	private static final String[] m={"全体人员","数学","语文","英语","政治","历史","地理","物理","化学","生物"};
	private void initSpinner(){
		sendToObj = "";
		
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
				sendToObj = m[position];
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
		});
         
        //设置默认值
        spinner.setVisibility(View.VISIBLE);
	    
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
			Utils.handlerInput.sendEmptyMessage(Utils.CLOSE_INPUT);
			String sendMsg=et_sendmessage.getText().toString();
			sendMessage("0", sendMsg,sendImagePath,sendToObj,true);
			
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
			FOnlineActivity.this.startActivityForResult(intent, Utils.GET_CAMERA);
			break;
		case R.id.chat_picture:
			intent=new Intent(mContext,FScaleImageFromSdcardActivity.class);
			FOnlineActivity.this.startActivityForResult(intent, Utils.SHOW_ALL_PICTURE);
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
			   File file = new File("/sdcard/myPic/");
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
			   Intent intent=new Intent(mContext,FCameraActivity.class);
			   intent.putExtra("camera", filename);
			   FOnlineActivity.this.startActivityForResult(intent, Utils.SHOW_CAMERA);

			   
		}else if(requestCode==Utils.SHOW_CAMERA&&resultCode==Utils.SHOW_CAMERA_RESULT){
			Bundle bundle = data.getExtras();
			Object camera=bundle.get("imgUrl");
			Log.d("TAG", "需要发送照相的图片到服务器"+camera.toString());
			//将图片发送到聊天界面
			if(camera.toString().length()>0){
				//TODO sendMessage("0","["+camera.toString()+"]",true);
			}
			//将图片异步发送到服务器，并且把发送的图片显示到聊天框中
			
		}else if(requestCode==Utils.SHOW_ALL_PICTURE&&resultCode==Utils.SHOW_PICTURE_RESULT){
			Log.d("TAG", "需要将选择的图片发送到服务器");
			List<String> bmpUrls=new ArrayList<String>();
			
			Bundle bundle = data.getExtras();
			Object[] selectPictures=(Object[]) bundle.get("selectPicture");
			if(selectPictures != null && selectPictures.length > 0){
				for (int i = 0; i < selectPictures.length; i++) {
					Log.d("TAG", "selectPictures[i]"+selectPictures[i]);
					String bmpUrl=FScaleImageFromSdcardActivity.map.get(Integer.parseInt(selectPictures[i].toString()));
					bmpUrls.add(bmpUrl);
					Log.d("TAG", "bmp"+bmpUrl);
					//TODO sendMessage("0","["+bmpUrl+"]",true);
					Bitmap bm = BitmapFactory.decodeFile(bmpUrl);
					Bitmap bmz = BitmapUtils.zoomImage(bm,50.0,50.0);
					chat_picture.setImageBitmap(bmz);
					
					sendImagePath = bmpUrl;
					
				}
				
			}else{
				sendImagePath = "";
				chat_picture.setImageDrawable(getResources().getDrawable(R.drawable.chat_op_picture));
			}


			//将图片发送到服务器
			
			
			
		}else if(requestCode==1001&&resultCode==10001){
			Log.d("TAG", "需要将选择的文件发送到服务器");
			Bundle bundle = data.getExtras();
			String filePath=bundle.getString("filePath");
			Toast.makeText(mContext, "filePath"+filePath, Toast.LENGTH_LONG).show();
		}
	}
	
	
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& ((FaceRelativeLayout)findViewById(R.id.FaceRelativeLayout)).hideFaceView()) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	/**
	 * 发送消息
	 */
	public void sendMessage(String id,String sendMsg,String imagePath,String sendToObj,final boolean isNew) {
		String contString =sendMsg ;
		if (contString.length() > 0 || StringUtils.isNotBlank(imagePath)) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("content",sendMsg);
			map.put("imagePath",imagePath);
			map.put("sendToObj", sendToObj);
			map.put("fromUser",truename);
			list_comment.add(map);
			list_comment_child.add(new ArrayList<HashMap<String, Object>>());
			
			if(commentAdapter != null){
				commentAdapter.clearList();
				commentAdapter.updateList(list_comment,
						list_comment_child);
				commentAdapter.notifyDataSetChanged();
			}
			
			et_sendmessage.setText("");
			sendImagePath = "";
			chat_picture.setImageDrawable(getResources().getDrawable(R.drawable.chat_op_picture));
			
			//聊天信息的发送到服务器
			sendDataToServer(map);
		}
		
		
		
	}
	
	private void sendDataToServer(HashMap<String, Object> message){
		String content = (String)message.get("content");
		String imagePath = (String)message.get("imagePath");
		if(StringUtils.isNotBlank(imagePath)){
			new UploadMessage(content, imagePath).start();
		}
	}
	
	
	class UploadMessage extends Thread{
		private String message;
		private String imagePath;
		String url = "http://"+ serverIp +":"+Global.Common_Port+"/api/saveMessage.html";
		public UploadMessage(String message,String imagePath){
			this.message = message;
			this.imagePath = imagePath;
		}
		
		@Override
		public void run() {
			super.run();
			HashMap<String,String> params = new HashMap<String,String>();
			params.put("msg",message);
			params.put("mTime",new Date().getTime()+"");
			params.put("src",userId+"");
			params.put("srcName",truename);
			params.put("objectName",sendToObj);
			params.put("classId",classId+"");
			try {
				File file = new File(imagePath);
				Map<String,File> files = new HashMap<String, File>();
				files.put("imageFile",file);
				String res = UploadUtil.post(url, params, files);
				System.out.println(res);
				if(Integer.parseInt(res) > 0){
					//TODO 
				}
			} catch (IOException e) {
				// TODO: handle exception
				if( e != null && e.getLocalizedMessage() != null )
					Log.e("FOnline",e.getLocalizedMessage());
			}
		}
		
	}

	
}
