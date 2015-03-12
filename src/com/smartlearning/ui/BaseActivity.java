package com.smartlearning.ui;

import com.smartlearning.R;
import com.smartlearning.constant.Global;
import com.smartlearning.model.Request;
import com.smartlearning.utils.CommonUtil;
import com.smartlearning.utils.HttpUtil;
import com.smartlearning.utils.ThreadPoolManager;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;


/**
 * Created by FQ 14-3-13.
 */
public abstract class BaseActivity extends Activity implements View.OnClickListener{

    protected Context context;
    protected ProgressDialog progressDialog;
    private ThreadPoolManager threadPoolManager;

    public BaseActivity(){
        threadPoolManager = ThreadPoolManager.getInstance();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        initView();
    }

    
    @Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		//MainActivity.m_STATICACTIVITY=this;
	}

	private void initView() {
        loadViewLayout();
        findViewById();
        setListener();
        processLogic();
    }

    //基础Handler
    class BaseHandler extends Handler {
        private Context context;
        private DataCallback callBack;
        private Request request;
        public BaseHandler(Context context,DataCallback callBack,
                           Request request){
            this.context = context;
            this.callBack = callBack;
            this.request = request;
        }

        public void handleMessage(Message msg) {
            closeProgressDialog();
            if(msg.what == Global.SUCCESS){
                if(msg.obj == null){
                    /*CommonUtil.showInfoDialog(context,getString(R.string.get_data_from_fail),
                            R.string.comm_dialog_title,R.string.comm_dialog_positive,
                            null
                            );*/
                	CommonUtil.showToast(context,"网络或服务器异常已采用加载本地数据",Toast.LENGTH_LONG);
                	callBack.processData(null,false);
                	
                }else{
                    callBack.processData(msg.obj,true);
                }
            }else if(msg.what == Global.NET_FAILED){
                /*CommonUtil.showInfoDialog(context,getString(R.string.net_error),
                        R.string.comm_dialog_title,R.string.comm_dialog_positive,
                        null
                );*/
            	CommonUtil.showToast(context,"网络或服务器异常已采用加载本地数据",Toast.LENGTH_LONG);
            	callBack.processData(null,false);
            }else{
                /*CommonUtil.showInfoDialog(context,getString(R.string.get_data_from_fail),
                        R.string.comm_dialog_title,R.string.comm_dialog_positive,
                        null
                );*/
            	CommonUtil.showToast(context,"网络或服务器异常已采用加载本地数据",Toast.LENGTH_LONG);
            	callBack.processData(null,false);
            }
        }
    }


    class BaseTask implements Runnable{
        private Context context;
        private Request request;
        private Handler handler;
        public BaseTask(Context context,Request request,Handler handler){
            this.context = context;
            this.request = request;
            this.handler = handler;
        }
        @Override
        public void run() {
            Object obj = null;
            //Message message = Message.obtain();
            Message message = new Message();
            if(HttpUtil.hasNetwork(context)){
                obj = HttpUtil.post(request);
                message.what = Global.SUCCESS;
                message.obj = obj;
            }else{
                message.what = Global.NET_FAILED;
                message.obj = null;
            }
            handler.sendMessage(message);

        }
    }

    //从服务器端获取到的数据然后回调实现此处理数据的方法
    public abstract interface DataCallback<T> {
        public abstract void processData(T paramObject,
                                         boolean paramBoolean);
    }

    /**
     * 从服务器上获取数据，并回调处理
     * @param request
     * @param callBack
     */
    protected void getDataFromServer(Request request,DataCallback callBack){
        showProgressDialog();
        BaseHandler handler = new BaseHandler(this,callBack,request);
        BaseTask task = new BaseTask(this,request,handler);
        threadPoolManager.addTask(task);
    }
    protected void getDataFromServer1(Request request,DataCallback callBack){
    	//showProgressDialog();
    	BaseHandler handler = new BaseHandler(this,callBack,request);
    	BaseTask task = new BaseTask(this,request,handler);
    	threadPoolManager.addTask(task);
    }

    /**
     * 显示提示框
     */
    protected void showProgressDialog() {
    	closeProgressDialog();
        if ((!isFinishing()) && (this.progressDialog == null)) {
            this.progressDialog = new ProgressDialog(this,R.style.dialog);
        }
        //this.progressDialog.setTitle(getString(R.string.loadTitle));
        this.progressDialog.setMessage(getString(R.string.LoadContent));
        this.progressDialog.show();
    }

    /**
     * 关闭提示框
     */
    protected void closeProgressDialog() {
        if (this.progressDialog != null){
            this.progressDialog.dismiss();
            this.progressDialog = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        closeProgressDialog();
        context = null;
        threadPoolManager = null;
    }

    /**
     *
     */
    protected abstract void findViewById();

    /**
     * 加载布局 setContentView()
     *
     */
    protected abstract void loadViewLayout();

    /**
     * 向后台请求数据
     */
    protected abstract void processLogic();

    /**
     * 设置事件
     */
    protected abstract void setListener();
    
    
    /*private long mExitTime;
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
                if ((System.currentTimeMillis() - mExitTime) > 2000) {
                        Toast.makeText(this, R.string.exist_app_ts, Toast.LENGTH_SHORT).show();
                        mExitTime = System.currentTimeMillis();

                } else {
                        finish();
                        System.exit(0);
                }
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }*/
}
