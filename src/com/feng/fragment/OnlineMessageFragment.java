package com.feng.fragment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.feng.adapter.CommentAdapter;
import com.feng.adapter.CommentReplyAdapter;
import com.feng.util.BitmapUtils;
import com.feng.util.StringUtils;
import com.feng.util.UploadUtil;
import com.feng.util.Utils;
import com.feng.view.FaceRelativeLayout;
import com.feng.vo.MessageListVO;
import com.feng.vo.MessageReplyListVO;
import com.feng.vo.OnlineMessage;
import com.feng.vo.OnlineMessageReply;
import com.feng.volley.FRestClient;
import com.feng.volley.FastJsonRequest;
import com.smartlearning.R;
import com.smartlearning.constant.Global;
import com.smartlearning.ui.FCameraActivity;
import com.smartlearning.ui.FOnlineActivity;
import com.smartlearning.ui.FScaleImageFromSdcardActivity;
import com.smartlearning.utils.CommonUtil;
import com.smartlearning.utils.DateUtil;
import com.smartlearning.utils.SpUtil;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class OnlineMessageFragment extends Fragment implements OnClickListener{

	private View mBaseView;
	private ListView lv_user_comments;
	private LinearLayout ll_comment;
	private Button btn_comment, btn_reply,btn_send;
	private EditText edt_comment_content, edt_reply;
	private TextView tv_reply;

	private CommentAdapter commentAdapter;
	private CommentReplyAdapter commentReplyAdapter;

	private static final int ONE_COMMENT_CODE = -1;
	private static final int TWO_COMMENT_CODE = 2;

	private List<OnlineMessage> list_comment; // 一级评论数据
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
	
	private RelativeLayout rl_chat;
	private LinearLayout title_cfm;
	private int totals = 0;
	
	private int _msgId = 0;//全局消息Id
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContext = getActivity();
		mBaseView = inflater.inflate(R.layout.f_book_category_fragment, null);
		
		initSpinner();

		initSp();
		
		initList();
		initView();

		initCommentData();
		
		return mBaseView;
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
		final ProgressDialog pDialog = new ProgressDialog(mContext);
		pDialog.setMessage("Loading...");
		pDialog.show(); 
		
		String tag_json_obj = "json_obj_req";
		final String url = "http://"+ serverIp +":"+Global.Common_Port+"/api/getOnlineMessage.html?classId="+classId+"&userId="+userId;

		FastJsonRequest<MessageListVO>   fastRequest = new FastJsonRequest<MessageListVO>(Method.GET,url, MessageListVO.class,null, new Response.Listener<MessageListVO>() {

			@Override
			public void onResponse(MessageListVO msgVO) {
				pDialog.dismiss();
				if(msgVO != null){
					list_comment.addAll(msgVO.getMessageList());
					totals = msgVO.getTotals();
					commentReplyAdapter = null;
					commentAdapter = new CommentAdapter(mContext, list_comment,
							list_comment_child, mReplyOnClickListener, commentReplyAdapter
							,mShowReplyOnClickListener
							,"http://"+ serverIp +":"+Global.Common_Port);
					lv_user_comments.setAdapter(commentAdapter);
				}
			}
		},
		new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				 //获取本地分类
				 CommonUtil.showToast(mContext,getString(R.string.get_online_message_fail),Toast.LENGTH_SHORT);
				 pDialog.dismiss();
			}
		}
	    );
		
		FRestClient.getInstance(mContext).addToRequestQueue(fastRequest,tag_json_obj);
	}

	private void initList() {
		list_comment = new ArrayList<OnlineMessage>();
		list_comment_child = new ArrayList<List<HashMap<String, Object>>>();
	}

	private void initView() {
		lv_user_comments = (ListView) mBaseView.findViewById(R.id.lv_comments);
		
		btn_send = (Button) mBaseView.findViewById(R.id.btn_send);
		
		chat_picture = (ImageButton)mBaseView.findViewById(R.id.chat_picture);

		et_sendmessage = (EditText) mBaseView.findViewById(R.id.et_sendmessage);
		
		rl_chat = (RelativeLayout)mBaseView.findViewById(R.id.rl_chat);
		
		
		Utils.handlerInput=new Handler(){
			@Override
			public void handleMessage(Message msg) {
				if(msg.what==Utils.CLOSE_INPUT){//关闭软键盘
					imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
				}else if(msg.what==Utils.CLOSE_MSG_HINT){
					//隐藏消息提示
					//msgHint.setVisibility(View.GONE);
				}
			}
		};
		
		Utils.notifyMegAdapter = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				OnlineMessage onlineMsg = (OnlineMessage)msg.obj;
				if(onlineMsg != null){
					sendLocalMessage(onlineMsg);
				}
			}
			
		};
		
		imm= (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		// 启动activity时不自动弹出软键盘
		getActivity().getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

	}

	private OnClickListener myOnitemcListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			int position = (Integer) v.getTag();
			
		}
	};
	
	
	final private OnClickListener mReplyOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			int position = (Integer) v.getTag();
			if(position < list_comment.size()){
				OnlineMessage msg = list_comment.get(position);
				if(msg != null){
					btn_send.setText(R.string.reply);
					_msgId = msg.getId();
				}
			}
			setChatEdit();
		}
	};

	
	final private OnClickListener mShowReplyOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			int position = (Integer) v.getTag();
			if(position < list_comment.size()){
				OnlineMessage msg = list_comment.get(position);
				if(msg != null){
					boolean isClickReply = msg.isClickReply();
					if(!isClickReply){
						loadReplyMessage(msg);
					}else{
						msg.setReplyList(new ArrayList<OnlineMessageReply>());
						if(commentAdapter != null){
							commentAdapter.clearList();
							commentAdapter.updateList(list_comment,
									list_comment_child);
							commentAdapter.notifyDataSetChanged();
						}
					}
					msg.setClickReply(!isClickReply);
				}
			}
		}
	};
	
	private void loadReplyMessage(final OnlineMessage msg){
		final ProgressDialog pDialog = new ProgressDialog(mContext);
		pDialog.setMessage("Loading...");
		pDialog.show(); 
		
		int msgId = msg.getId();
		String tag_json_obj = "json_obj_req";
		final String url = "http://"+ serverIp +":"+Global.Common_Port+"/api/getOnlineReplyMessage.html?msgId="+msgId;

		FastJsonRequest<MessageReplyListVO>   fastRequest = new FastJsonRequest<MessageReplyListVO>(Method.GET,url, MessageReplyListVO.class,null, new Response.Listener<MessageReplyListVO>() {

			@Override
			public void onResponse(MessageReplyListVO msgVO) {
				pDialog.dismiss();
				if(msgVO != null){
					List<OnlineMessageReply> replyList = msgVO.getReplyList();
					if(replyList != null){
						msg.setReplyList(replyList);
						if(commentAdapter != null){
							commentAdapter.clearList();
							commentAdapter.updateList(list_comment,
									list_comment_child);
							commentAdapter.notifyDataSetChanged();
						}
					}
				}
			}
		},
		new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				 //获取本地分类
				 CommonUtil.showToast(mContext,getString(R.string.get_online_message_fail),Toast.LENGTH_SHORT);
				 pDialog.dismiss();
			}
		}
	    );
		
		FRestClient.getInstance(mContext).addToRequestQueue(fastRequest,tag_json_obj);
	}


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



	

	
	private static final String[] m={"全体人员","数学","语文","英语","政治","历史","地理","物理","化学","生物"};
	private void initSpinner(){
		sendToObj = "";
		
	  	spinner = (Spinner) mBaseView.findViewById(R.id.Spinner);
        //将可选内容与ArrayAdapter连接起来
        adapter = new ArrayAdapter<String>(mContext,android.R.layout.simple_spinner_item,m);
         
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
			int msgId = 0;
			if(_msgId > 0){
				msgId = _msgId;
				_msgId = 0;
			}else{
				_msgId = 0;
			}
			sendMessage(msgId+"", sendMsg,sendImagePath,sendToObj,true);
			setChatEdit();
			break;
		case R.id.chatView:
			// 关闭表情,点击标题栏时
			Utils.handler.sendEmptyMessage(1);
			break;
		case R.id.chat_camera:
			//启动相机
			Intent intent = new Intent();
			Intent intent_camera = getActivity().getPackageManager()
					.getLaunchIntentForPackage("com.android.camera");
			if (intent_camera != null) {
				intent.setPackage("com.android.camera");
			}
			intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
			getActivity().startActivityForResult(intent, Utils.GET_CAMERA);
			break;
		case R.id.chat_picture:
			intent=new Intent(mContext,FScaleImageFromSdcardActivity.class);
			getActivity().startActivityForResult(intent, Utils.SHOW_ALL_PICTURE);
			//设置切换动画，从右边进入，左边退出
			getActivity().overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);  				
			
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
	
	
	
	
	
	/**
	 * 发送消息
	 */
	public void sendMessage(String id,String sendMsg,String imagePath,String sendToObj,final boolean isNew) {
		String contString =sendMsg ;
		if (contString.length() > 0 || StringUtils.isNotBlank(imagePath)) {			
			OnlineMessage msg = new OnlineMessage();
			msg.setMsg(sendMsg);
			msg.setLocalImagePath(imagePath);
			msg.setSender(truename);
			et_sendmessage.setText("");
			sendImagePath = "";
			chat_picture.setImageDrawable(getResources().getDrawable(R.drawable.chat_op_picture));
			if(StringUtils.isNotBlank(id)){
				msg.setId(Integer.parseInt(id));
			}
			//聊天信息的发送到服务器
			sendDataToServer(msg);
		}
		
		
		
	}
	
	private void sendLocalMessage(OnlineMessage msg){
		list_comment.add(msg);
		list_comment_child.add(new ArrayList<HashMap<String, Object>>());
		
		if(commentAdapter != null){
			commentAdapter.clearList();
			commentAdapter.updateList(list_comment,
					list_comment_child);
			commentAdapter.notifyDataSetChanged();
		}
	}
	
	private void sendDataToServer(OnlineMessage message){
		
		if(message.getId() > 0){
			new ReplyUploadMessage(message).start();
		}else{
			String content = message.getMsg();
			String imagePath = message.getLocalImagePath();
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
			params.put("localImagePath",imagePath);
			try {
				Map<String,File> files = new HashMap<String, File>();
				if(StringUtils.isNotBlank(imagePath)){
					File file = new File(imagePath);
					files.put("imageFile",file);
				}
				String res = UploadUtil.post(url, params, files);
				System.out.println(res);
				if(Integer.parseInt(res) > 0){
					//TODO 
					OnlineMessage msg = new OnlineMessage();
					msg.setId(Integer.parseInt(res));
					msg.setMsg(message);
					msg.setLocalImagePath(imagePath);
					msg.setSender(truename);
					msg.setMsgtime(DateUtil.dateToString(new Date(),false));
					//sendLocalMessage(msg);
					Message message = new Message();
					message.obj = msg;
					Utils.notifyMegAdapter.sendMessage(message);
				}
			} catch (IOException e) {
				// TODO: handle exception
				if( e != null && e.getLocalizedMessage() != null )
					Log.e("FOnline",e.getLocalizedMessage());
			}
		}
		
	}
	
	class ReplyUploadMessage extends Thread{
		private OnlineMessage msg;
		String url = "http://"+ serverIp +":"+Global.Common_Port+"/api/saveReplyMessage.html";
		public ReplyUploadMessage(OnlineMessage msg){
			this.msg = msg;
		}
		
		@Override
		public void run() {
			super.run();
			HashMap<String,String> params = new HashMap<String,String>();
			String message = msg.getMsg();
			params.put("msg",message);
			params.put("mTime",new Date().getTime()+"");
			params.put("msgId",msg.getId()+"");
			params.put("rpUid",userId+"");
			params.put("rpName",truename);
			
			String imagePath = msg.getLocalImagePath();
			try {
				Map<String,File> files = new HashMap<String, File>();
				if(StringUtils.isNotBlank(imagePath)){
					File file = new File(imagePath);
					files.put("imageFile",file);
				}
				String res = UploadUtil.post(url, params, files);
				System.out.println(res);
				if(Integer.parseInt(res) > 0){
					//TODO 
//					OnlineMessage msg = new OnlineMessage();
//					msg.setMsg(message);
//					msg.setLocalImagePath(imagePath);
//					msg.setSender(truename);
//					msg.setMsgtime(DateUtil.dateToString(new Date(),false));
//					sendLocalMessage(msg);
				}
			} catch (IOException e) {
				// TODO: handle exception
				if( e != null && e.getLocalizedMessage() != null )
					Log.e("FOnline",e.getLocalizedMessage());
			}
		}
		
	}

	private void setChatEdit(){
		int visibility = rl_chat.getVisibility();
		if(visibility == View.GONE){
			rl_chat.setVisibility(View.VISIBLE);
		}else{
			rl_chat.setVisibility(View.GONE);
			_msgId = 0;
		}
	}
	

}
