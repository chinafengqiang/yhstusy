package com.smartlearning.ui;

import com.smartlearning.R;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

public class VedioPlayActivity extends Activity  implements
OnCompletionListener, OnErrorListener {
	private MyVideoView mVideoView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try{			
			super.onCreate(savedInstanceState);
	        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
	                WindowManager.LayoutParams.FLAG_FULLSCREEN);
	        requestWindowFeature(Window.FEATURE_NO_TITLE);
	        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	        setContentView(R.layout.activity_vedio_play);
	        Intent intent = getIntent();
	        //Bundle bundle = intent.getExtras();
	        //String movieUrl = bundle.getString("movieUrl");

	         DisplayMetrics dm = new DisplayMetrics();
	         this.getWindowManager().getDefaultDisplay().getMetrics(dm);
	         MyVideoView.WIDTH=dm.widthPixels;
	         MyVideoView.HEIGHT=dm.heightPixels;
	         Log.e("widthPixels", "widthPixels"+dm.widthPixels);
	         Log.e("heightPixels", "widthPixels"+dm.heightPixels);
	         mVideoView = (MyVideoView) findViewById(R.id.videoView1);
	         mVideoView.setMediaController(new MediaController(this));
	         mVideoView.setOnCompletionListener(this);
	         mVideoView.setOnErrorListener(this);
	         //mVideoView.setVideoPath("/sdcard/test.mp4");
	         //mVideoView.setVideoPath("http://192.168.0.20:8080/uploadFile/file/%E9%AB%98%E7%AD%89%E6%95%B0%E5%AD%A6.flv");
	         String url = getIntent().getStringExtra("url");
	         mVideoView.setVideoPath(url);
	         mVideoView.start();
	         mVideoView.requestFocus();
		}
		catch(Exception ex){
			Toast.makeText(this, "无法播放视频，请确定您的视频文件路径是否已上传成功。", Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.v("this is error", "onError method is called!!");

        if (what == MediaPlayer.MEDIA_ERROR_SERVER_DIED) {
            Log.v("this is error", "Media Error,Server Died" + extra);
        } else if (what == MediaPlayer.MEDIA_ERROR_UNKNOWN) {
            Log.v("this is error", "Media Error,Error Unknown" + extra);
        }
        Toast.makeText(VedioPlayActivity.this, "视频播放错误，请重新选择！",
                Toast.LENGTH_LONG).show();
        return false;
    }

	@Override
	public void onCompletion(MediaPlayer mp) {
        finish();
    }
	
	@Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
