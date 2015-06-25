package com.smartlearning.ui;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.feng.view.ImageZoomView;
import com.feng.view.SimpleZoomListener;
import com.feng.view.ZoomState;
import com.feng.volley.FRestClient;
import com.smartlearning.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.ZoomControls;

public class ShowImageActivity extends Activity  
{  
    /** Called when the activity is first created. */  
    private ImageZoomView mZoomView;  
    private ZoomState mZoomState;  
    private Bitmap mBitmap;  
    private SimpleZoomListener mZoomListener;  
    private ProgressBar progressBar;  
    private String localImagePath = "";
    private String netImagePath = "";
    private Context mContext;
    
//    private Handler handler = new Handler()  
//    {  
//        @Override  
//        public void handleMessage(Message msg)  
//        {  
//            progressBar.setVisibility(View.GONE);  
//            mZoomView.setImage(mBitmap);  
//            mZoomState = new ZoomState();  
//            mZoomView.setZoomState(mZoomState);  
//            mZoomListener = new SimpleZoomListener();  
//            mZoomListener.setZoomState(mZoomState);  
//            mZoomView.setOnTouchListener(mZoomListener);  
//            resetZoomState();  
//        }  
//    };  
  
    @Override  
    public void onCreate(Bundle savedInstanceState)  
    {  
        super.onCreate(savedInstanceState);  
          
        mContext = this;
        
        // 隐藏顶部程序名称 写在setContentView(R.layout.xxxx);之前，不然报错  
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);          
        requestWindowFeature(Window.FEATURE_NO_TITLE);  
          
        // 隐藏状态栏  
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,  
                WindowManager.LayoutParams.FLAG_FULLSCREEN);  
          
        setContentView(R.layout.show_image);  
        mZoomView = (ImageZoomView) findViewById(R.id.zoomView);  
        progressBar = (ProgressBar) findViewById(R.id.progress_large);  
        progressBar.setVisibility(View.VISIBLE);  
          
        localImagePath = getIntent().getStringExtra("localImagePath");   
        netImagePath = getIntent().getStringExtra("netImagePath");   
          
        loadImage();
        
        
//        Thread thread = new Thread(new Runnable()  
//        {  
//            @Override  
//            public void run()  
//            {  
//                /* 
//                 * 加载网络图片 load form url 
//                 * 
//                 */  
//                // mBitmap =  
//                // ImageDownloader.getInstance().getBitmap(url);  
//                mBitmap = BitmapFactory.decodeResource(ShowImageActivity.this.getResources(), R.drawable.chat_bg_default);  
//                handler.sendEmptyMessage(0);  
//            }  
//        });  
//        thread.start();
  
        ZoomControls zoomCtrl = (ZoomControls) findViewById(R.id.zoomCtrl);  
        zoomCtrl.setOnZoomInClickListener(new View.OnClickListener()  
        {  
            @Override  
            public void onClick(View v)  
            {  
                float z = mZoomState.getZoom() + 0.25f;  
                mZoomState.setZoom(z);  
                mZoomState.notifyObservers();  
            }  
        });  
        zoomCtrl.setOnZoomOutClickListener(new View.OnClickListener()  
        {  
  
            @Override  
            public void onClick(View v)  
            {  
                float z = mZoomState.getZoom() - 0.25f;  
                mZoomState.setZoom(z);  
                mZoomState.notifyObservers();  
            }  
        });  
    }  
  
    @Override  
    protected void onDestroy()  
    {  
        super.onDestroy();  
        if (mBitmap != null && (!mBitmap.isRecycled())) {
            //mBitmap.recycle();  
            mBitmap = null;
        }
  
    }  
  
    private void resetZoomState()  
    {  
        mZoomState.setPanX(0.5f);  
        mZoomState.setPanY(0.5f);  
        mZoomState.setZoom(1f);  
        mZoomState.notifyObservers();  
    }  
    
    private void loadImage(){
    	ImageLoader imageLoader = FRestClient.getInstance(mContext).getImageLoader();
    	
    	imageLoader.get(netImagePath, new ImageLoader.ImageListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onResponse(ImageContainer imageContainer, boolean arg1) {
				mBitmap = imageContainer.getBitmap();
	            progressBar.setVisibility(View.GONE);  
	            mZoomView.setImage(mBitmap);  
	            mZoomState = new ZoomState();  
	            mZoomView.setZoomState(mZoomState);  
	            mZoomListener = new SimpleZoomListener();  
	            mZoomListener.setZoomState(mZoomState);  
	            mZoomView.setOnTouchListener(mZoomListener);  
	            resetZoomState(); 
			}
		});
    }
}
