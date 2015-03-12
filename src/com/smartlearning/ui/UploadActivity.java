package com.smartlearning.ui;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.smartlearning.R;
import com.smartlearning.factory.UTestPaperFactory;
import com.smartlearning.model.UserTestPaper;
import com.smartlearning.ui.TestPaperTreeActivity.SubmitUserResult;
import com.smartlearning.utils.UploadUtil;
import com.smartlearning.utils.UploadUtil.OnUploadProcessListener;


/**
 * 
 * 说明：主要用于选择文件和上传文件操作
 */
public class UploadActivity extends Activity implements OnClickListener,OnUploadProcessListener{
	private static final String TAG = "uploadImage";
	
	/**
	 * 去上传文件
	 */
	protected static final int TO_UPLOAD_FILE = 1;  
	/**
	 * 上传文件响应
	 */
	protected static final int UPLOAD_FILE_DONE = 2;  //
	/**
	 * 选择文件
	 */
	public static final int TO_SELECT_PHOTO = 3;
	/**
	 * 上传初始化
	 */
	private static final int UPLOAD_INIT_PROCESS = 4;
	/**
	 * 上传中
	 */
	private static final int UPLOAD_IN_PROCESS = 5;
	/***
	 * 这里的这个URL是我服务器的javaEE环境URL
	 */
	//private static String requestURL = "http://172.27.35.1:8080/sysKeyController/upload.html";
	private String requestURL = "";
	private Button selectButton,uploadButton;
	private ImageView imageView;
	private TextView uploadImageResult;
	private ProgressBar progressBar;
	
	private String picPath = null;
	private ProgressDialog progressDialog;
	private SharedPreferences sharedPreferences;
	String ip = "";
	private int testPaperId = 0;
	private UTestPaperFactory uTestPaperFactory = null;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.uoload_activity);
        
        sharedPreferences = getSharedPreferences("userInfo", MODE_PRIVATE);
        ip = sharedPreferences.getString("serverIp", null);
		if (getIntent().getIntExtra("categoryId", 0)!=0)
			testPaperId = getIntent().getIntExtra("testPaperId", 0);
        requestURL = "http://"+ ip +":8080/sysKeyController/upload.html";
   //     picPath = Environment.getExternalStorageDirectory() + "/elearningDB/9_10.jpg";
        initView();
    }
    
    /**
     * 初始化数据
     */
	private void initView() {
        selectButton = (Button) this.findViewById(R.id.selectImage);
        uploadButton = (Button) this.findViewById(R.id.uploadImage);
        selectButton.setOnClickListener(this);
        uploadButton.setOnClickListener(this);
        imageView = (ImageView) this.findViewById(R.id.imageView);
		uploadImageResult = (TextView) findViewById(R.id.uploadImageResult);
        progressDialog = new ProgressDialog(this);
        progressBar = (ProgressBar) findViewById(R.id.progressBar1);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.selectImage:
			Intent intent = new Intent(this,SelectPicActivity.class);
			startActivityForResult(intent, TO_SELECT_PHOTO);
			break;
		case R.id.uploadImage:
			if(picPath!=null)
			{
				handler.sendEmptyMessage(TO_UPLOAD_FILE);
			}else{
				Toast.makeText(this, "上传的文件路径出错", Toast.LENGTH_LONG).show();
			}
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode==Activity.RESULT_OK && requestCode == TO_SELECT_PHOTO)
		{
			picPath = data.getStringExtra(SelectPicActivity.KEY_PHOTO_PATH);
			Log.i(TAG, "最终选择的图片="+picPath);
			Bitmap bm = BitmapFactory.decodeFile(picPath);
			imageView.setImageBitmap(bm);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	

	/**
	 * 上传服务器响应回调
	 */
	@Override
	public void onUploadDone(int responseCode, String message) {
		progressDialog.dismiss();
		Message msg = Message.obtain();
		msg.what = UPLOAD_FILE_DONE;
		msg.arg1 = responseCode;
		msg.obj = message;
		handler.sendMessage(msg);
	}
	
	
	private void getData(){
		List<UserTestPaper> list = uTestPaperFactory.createUTestPaper(UploadActivity.this).getUserTestPaperById(testPaperId);
		for(UserTestPaper utp : list){
			
			UserTestPaper ut = new UserTestPaper();
			ut.setUserId(utp.getUserId());
			ut.setQuestionId(utp.getQuestionId());
			ut.setScore(utp.getScore());
			ut.setIsCorrect(utp.getIsCorrect());
			ut.setTestPaperId(utp.getTestPaperId());
			ut.setNoSelectAnswer(utp.getNoSelectAnswer());
			
		}
	}
	
	private void toUploadFile()
	{
		uploadImageResult.setText("正在上传中...");
		progressDialog.setMessage("正在上传文件...");
		progressDialog.show();
		Log.i(TAG, "requestURLeeeeeeeee="+requestURL);
		String fileKey = "img";
		UploadUtil uploadUtil = UploadUtil.getInstance();;
		uploadUtil.setOnUploadProcessListener(this);  //设置监听器监听上传状态
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("orderId", "dddddxu");
		
		Log.i(TAG, "requestURL="+requestURL);
		uploadUtil.uploadFile( picPath,fileKey, requestURL,params);
	}
	
	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case TO_UPLOAD_FILE:
				toUploadFile();
				break;
			
			case UPLOAD_INIT_PROCESS:
				progressBar.setMax(msg.arg1);
				break;
			case UPLOAD_IN_PROCESS:
				progressBar.setProgress(msg.arg1);
				break;
			case UPLOAD_FILE_DONE:
				String result = "响应码："+msg.arg1+"\n响应信息："+msg.obj+"\n耗时："+UploadUtil.getRequestTime()+"秒";
				uploadImageResult.setText(result);
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
		
	};

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