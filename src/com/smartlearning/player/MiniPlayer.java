package com.smartlearning.player;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.ql.view.MySurfaceView;
import com.ql.view.ViewfinderView;
import com.smartlearning.R;
import com.smartlearning.ui.DetailActivity;
import com.smartlearning.utils.Net;
import com.smartlearning.utils.SystemHandler;

public class MiniPlayer extends Activity {
	
	public static final String TAG  =  "MiniPlayer";
	
	private TextView hasPlayedTV=null;
	private TextView durationTV=null;
	
	private MySurfaceView    miniMP         = null;
	private ViewfinderView   viewfinderView = null; 
	
	private PopupWindow controlPW=null;
	private PopupWindow batteryInfoPW=null;
	
	private Player player=null;
	private SeekBar skbProgress=null;
	private View controlView=null;
	private View batteryView=null;
 
	private ProgressBar videoProgressBar=null;
	
	private TextView batteryInfoTextView=null;
	private TextView currentTimeTextView=null;
	private ImageView batteryImageView=null;
	
	private int BatteryN=0;
	
	private Timer PWMenuTimer = null; 
	private Timer Timerresh   = null;
	
	private ImageButton bt_pause = null;
	private ImageButton bt_BackWard = null;
	private ImageButton bt_UpWard = null;
	private ImageButton bt_beep = null;
	private ImageButton bt_fullscreen = null;
	
	private String  videoUrl = null; 
	private boolean saveorientationflag = false;
	
	private Videocontrl mVideocontrlClickListener = null;
	
	private   boolean        beepstop = true;
	private   boolean        fullscreenstop = true;  
	
    private   DisplayMetrics dm = null;
    
    private   boolean        CheckHttpFlag =  false; 
	
	
	private Handler controlPWHandler = new Handler() 
	{
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			if(msg.arg1==1)
			{
				String url = videoUrl;
				CheckHttpFlag =  false;
				if( !Net.SetIntnectCheckFlag(MiniPlayer.this,url))
				{
				    MyDebug.ToastMessage(MiniPlayer.this, "播放失败......");
					return;
				}
				
				CheckHttpFlag = Net.getHttpConnectEnable(MiniPlayer.this);

				if(player != null)
				{
				    player.playUrl(url); 
				}
				
				if(miniMP !=  null )
				{ 
			    	//showPWMenu(miniMP);
				}
				
				if(videoProgressBar != null)
				{ 
				    videoProgressBar.setVisibility(View.GONE); 
				}
				
				MyDebug.MyDebugLogI(TAG,"player play");
			}
			else if(msg.arg1==2)
			{
				  closePwMenu(null);
			}
			else if(msg.arg1 == 3)
			{
				  /*****定时 1 秒，处理以下事件*******/
				  
				  
			//	  CheckPlaying();//*********检查播放状态********/
				  
				  setbatteryInfo(); //用来显示时间及电池状态
				  
				//  ShowLoading();/*********显示加载条********/
			     
			      Timerresh = null; 
				  Timerresh  = new Timer(); 
				  Timerresh.schedule(new AutoTimerTask(AutoTimerTask.RemindTime), 1000);  
				
			}
		}
	};
	
	
	/*********显示加载条********/
	public void ShowLoading()
	{
		
		MyDebug.MyDebugLogI(TAG, "ShowLoading  --->");
		
		if(player !=  null )
		{  
			
			 if(player.getBufferingUpdatePosition() == player.getDuration()) 
			 {
				 videoProgressBar.setVisibility(View.GONE);
				 return;
			 }
			
			if ( player.getCurrentPosition()  >= (player.getBufferingUpdatePosition() - 2000))
			{
				 videoProgressBar.setVisibility(View.VISIBLE); 
			}
			else
			{
				 videoProgressBar.setVisibility(View.GONE); 
			}  
			 
		} 
	}
	
	
	/*********检查播放状态********/
	public void CheckPlaying()
	{
		
		MyDebug.MyDebugLogI(TAG, "CheckPlaying  --->");
		
		if(player !=  null )
		{    
			
			MyDebug.MyDebugLogI(TAG, "CheckPlaying-------------------------------------  --->===" +player);
		       	 if(player.getPlayStatus())
		       	    {  
						SetBmp_VideoControl(R.id.bt_pause,true); 
					}
				else{  
						SetBmp_VideoControl(R.id.bt_pause,false);
				    } 
		} 
	}
	
	
	
	/**
	 * 关闭PopWindows
	 */
	public void closePwMenu(View v)
	{
		MyDebug.MyDebugLogI(TAG, "closePwMenu  --->");
		
		if (controlPW == null || controlPW.isShowing())
		{
			controlPW.dismiss();
		}
		if (batteryInfoPW == null || batteryInfoPW.isShowing())
		{
			batteryInfoPW.dismiss();
		} 
		
	}
	/**
	 * 
	 * 显示PopWindows
	 * 
	 */
	public void showPWMenu(View v) 
	{
		MyDebug.MyDebugLogI(TAG, "showPWMenu  --->");
		
		if(PWMenuTimer != null)
		{
			PWMenuTimer.cancel();
			PWMenuTimer.purge();
		}
		
		PWMenuTimer = null;
		
		 
	    PWMenuTimer = new Timer();
	    PWMenuTimer.schedule(new AutoTimerTask(AutoTimerTask.Play_Close_Menu), 4000);
			
			
		
		setbatteryInfo();
		
		
		if (controlPW != null && !controlPW.isShowing()) {
			controlPW.showAtLocation(v, Gravity.BOTTOM, 0, 0);  //findViewById(R.id.miniplayer)
		}
		if (batteryInfoPW != null&& !batteryInfoPW.isShowing()) {
			batteryInfoPW.showAtLocation(v, Gravity.TOP, 0, 0);
		}
		

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		MyDebug.MyDebugLogI(TAG, "onCreate  --->");
		
		SystemHandler.SetdisplayFullsreen(this); //设置全屏
		
		setContentView(R.layout.miniplayer);	
		
		
		videoUrl = getIntent().getExtras().getString("videourl");  
	//	MyDebug.MyDebugLogI(TAG, videoUrl);
		
		registerBReceiver();//注册广播 
		
		initView();         //初始化播放界面
		
		initPopWindows();   //初始化播放控制界面和显示电池时间状态界面
		 
		
		bindListener();    //绑定控件的监听 
		
		
		CheckHttpFlag =  false;
		
		player = new Player(miniMP, skbProgress,hasPlayedTV,durationTV,this); 
	 
		
		//定时刷新,定时1秒钟
		Timerresh  = new Timer();
		Timerresh.schedule(new AutoTimerTask(AutoTimerTask.RemindTime), 1000);  
		
		
		//200毫秒后自动播放
		Timer timer  = new Timer();
		timer.schedule(new AutoTimerTask(AutoTimerTask.Play_Task), 200);  
	
	}
	
	/**
	 * 
	 * 注册广播
	 * 
	 */
	private void registerBReceiver()
	{
		MyDebug.MyDebugLogI(TAG, "registerBReceiver  ---> mBatInfoReceiver");
		
		registerReceiver(mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
	}
	
	
	private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			 String action = intent.getAction();  
	            /* 
	             * 如果捕捉到的action是ACTION_BATTERY_CHANGED， 就运行onBatteryInfoReceiver() 
	             */  
	            if (Intent.ACTION_BATTERY_CHANGED.equals(action))   
	            {  
	                BatteryN = intent.getIntExtra("level", 0);    //目前电量  
	               // BatteryV = intent.getIntExtra("voltage", 0);  //电池电压  
	                //BatteryT = intent.getIntExtra("temperature", 0);  //电池温度  
	                
	                MyDebug.MyDebugLogI(TAG, "BatteryN = "+String.valueOf(BatteryN));
	            }
		}
	};
	
	/**
	 * 
	 * @author qiuj
	 * seekbar事件监听
	 */
	class SeekBarChangeEvent implements SeekBar.OnSeekBarChangeListener {
		
		int progress = 0;
	
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) 
		{ 
			//progress = progress * player.mediaPlayer.getDuration() / seekBar.getMax();
		}
		public void onStartTrackingTouch(SeekBar seekBar)
		{

		}
		public void onStopTrackingTouch(SeekBar seekBar)
		{  
			
			     
			
			     int pp = player.getDuration()/100;
			     if(pp == -1) 
			    	 return;
		    	 int pm = seekBar.getProgress();  
		    	 int om = pp * pm; 
		    	 
		    	 player.seekTo(om);  
		          
		}
	}
	
	/**
	 * 
	 * 初始化播放控制界面和显示电池时间状态界面
	 * 
	 */
	private void initPopWindows()
	{
		 // mLayoutInfalter = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE); 
		  
		  controlView     = getLayoutInflater().inflate(R.layout.video_control, null); 
		  controlPW = new PopupWindow(controlView, LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT);
		  controlPW.setAnimationStyle(R.style.Animation_controlPW);

		  batteryView = getLayoutInflater().inflate(R.layout.video_batteryinfo, null); 
		  batteryInfoPW = new PopupWindow(batteryView, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		  batteryInfoPW.setAnimationStyle(R.style.Animation_batteryInfoPW); 
		  
		  skbProgress = (SeekBar) controlView.findViewById(R.id.seekbar);
		  skbProgress.setOnSeekBarChangeListener(new SeekBarChangeEvent());
		  
		  hasPlayedTV=(TextView) controlView.findViewById(R.id.has_played);
		  durationTV=(TextView) controlView.findViewById(R.id.duration);
		
		
		  bt_pause   =(ImageButton) controlView.findViewById(R.id.bt_pause);
		  bt_BackWard=(ImageButton)controlView.findViewById(R.id.bt_stepbackward);
		  bt_UpWard  =(ImageButton) controlView.findViewById(R.id.bt_stepforward);
		  bt_beep    = (ImageButton) controlView.findViewById(R.id.bt_soundcontrol); 
		  bt_fullscreen = (ImageButton) controlView.findViewById(R.id.bt_fullscreen);
		
		  batteryImageView   =(ImageView) batteryView.findViewById(R.id.battery_image);
		  batteryInfoTextView=(TextView) batteryView.findViewById(R.id.battery_rate);
		  currentTimeTextView=(TextView) batteryView.findViewById(R.id.current_time); 
		  
		  
		  MyDebug.MyDebugLogI(TAG, "initPopWindows  --->");
	}
	
	/**
	 * 
	 * 初始化播放界面
	 * 
	 */
	private void initView() 
	{
		    miniMP           = (MySurfaceView) findViewById(R.id.video_view); 
		    viewfinderView   = (ViewfinderView) findViewById(R.id.viewfinder_view); 
	        videoProgressBar = (ProgressBar) findViewById(R.id.video_load);   
	     
	        
	        
	        dm  = SystemHandler.GetSystemUiMetrics(this, true); //获得系统的屏慕资源
	        
	        Log.i("dm.widthPixels==", "dm.widthPixels=="+dm.widthPixels);
	        Log.i("dm.heightPixels==", "dm.heightPixels=="+dm.heightPixels);
	    //    Toast.makeText(this, "加载中..", Toast.LENGTH_SHORT).show();
	     //   Toast.makeText(this, "dm.heightPixels="+dm.heightPixels, Toast.LENGTH_SHORT).show();
	        
	        //设置全屏（后两个参数忽略）
		    miniMP.setVideoDisplaySize(fullscreenstop,dm.widthPixels,dm.heightPixels); 
		    //重绘
		    miniMP.SetVideoDraw(viewfinderView); 
		      
 
	}
	
	 
	
   /**
    * 
    * 绑定监听
    * 
    */
	private void bindListener() 
	{
		
		mVideocontrlClickListener =  new Videocontrl(); 
		
		// 播放界面
		miniMP.setOnClickListener(mVideocontrlClickListener);
		
		// 播放暂停按钮
		bt_pause.setOnClickListener(mVideocontrlClickListener);
		
		// 后退按钮
		bt_BackWard.setOnClickListener(mVideocontrlClickListener);
		
		// 前进按钮
		bt_UpWard.setOnClickListener(mVideocontrlClickListener); 
		
		// 声音
		bt_beep.setOnClickListener(mVideocontrlClickListener);
		
		// 全屏
		bt_fullscreen.setOnClickListener(mVideocontrlClickListener);
		
		
		 MyDebug.MyDebugLogI(TAG, "bindListener  --->");
		
	}
	
	
	class Videocontrl implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
			int progresstemp     =  0;
			int progressDuration =  0; 
			int offset   = 0;
			
			switch (v.getId())
			{
			        // 后退按钮
			
			         case  R.id.bt_stepbackward:
			        	 
			        	   
			        	 
			        	   progressDuration =  player.getDuration();  
			        	   offset  =  (progressDuration * 2)/100;
			        	   
			        	   progresstemp     =  player.getCurrentPosition();
			        	   
			        	   if(progresstemp > offset)
			        	   {
			        		   progresstemp  -= offset; 
			        	   }
			        	   else
			        	   {
			        		   progresstemp  = 0;
			        	   }
			        	   
			        	   player.mediaPlayer.seekTo(progresstemp);  
			        	   
			        	   MyDebug.MyDebugLogI(TAG, "R.id.bt_stepbackward  --->  stepbackward  = " + String.valueOf(progresstemp));
			        	   
			    	 break;
			    	// 播放暂停按钮
			         case  R.id.bt_pause: 
			        	 
			        	   MyDebug.MyDebugLogI(TAG, "R.id.bt_pause  --->  getPlayStatus = " + String.valueOf(player.getPlayStatus()));
			        	 
					        	 if(player.getPlayStatus())
					        	    {
										player.pause();  
									}
								else{
										player.play();  
								    }
					        	  
				     break;
				  // 前进按钮
			         case  R.id.bt_stepforward: 
			        	   progressDuration =  player.getDuration();  
			        	   offset  =  (progressDuration * 2)/100;
			        	   
			        	   
			        	   progresstemp = player.getCurrentPosition(); 
			        	   if(progresstemp  <  player.getDuration() - offset)
			        	   {
			        		   progresstemp  += offset; 
			        	   }
			        	   else
			        	   {
			        		   progresstemp  = player.getDuration();
			        	   }
			        	   
			        	   player.seekTo(progresstemp);   
			        	   
			        	   MyDebug.MyDebugLogI(TAG, "R.id.bt_stepforward  --->  stepforward = " + String.valueOf(progresstemp));
			        	   
				     break; 
				     
				     
				  // 声音
			         case R.id.bt_soundcontrol: 
			        	  SystemHandler.SetSystemSound(MiniPlayer.this,beepstop);
			        	  
			        	  SetBmp_VideoControl(R.id.bt_soundcontrol,beepstop);
			        	  
			        	  MyDebug.MyDebugLogI(TAG, "R.id.bt_soundcontrol  --->  SetSystemSound = " + String.valueOf(beepstop));
			        	  
                          beepstop  = !beepstop;
			         break;
			        
			         
			         
			      // 全屏
			         case R.id.bt_fullscreen:  
			         
			        	 
			        	  if(!player.getPlayStatus())return;
			        	 
			        	  fullscreenstop = !fullscreenstop;
			    	        
			    	       int  VideoW = dm.widthPixels;
			    	       int  VideoH = dm.heightPixels;
			    	        
			    	        
			    	        SetBmp_VideoControl(R.id.bt_fullscreen,fullscreenstop);
			    	       
			    			 if(!fullscreenstop)
			    			 {
			    				 VideoW = player.getVideoWidth();
			    				 VideoH = player.getVideoHeight(); 
			    				 
			    				 if((VideoW == -1) && (VideoH == -1))
			    				 {
			    					     VideoW = dm.widthPixels;
						    	         VideoH = dm.heightPixels;  
						    	         SetBmp_VideoControl(R.id.bt_fullscreen,fullscreenstop);
			    				 }
			    			 } 
			    	         
			 	 	         
			    			 miniMP.setVideoDisplaySize(fullscreenstop,VideoW,VideoH);
			    			 miniMP.SetVideoDraw(viewfinderView);   
			    			 
			    			 MyDebug.MyDebugLogI(TAG, "setVideoDisplaySize: VideoW = " + String.valueOf(VideoW) + "VideoH = " + String.valueOf(VideoH));
				        	  	 
			        	 
			         break;
			         
			         
			      // 播放界面
			         case R.id.video_view: 
			        		showPWMenu(v);
							Log.i(TAG,"onclick");
							
							MyDebug.MyDebugLogI(TAG, "R.id.video_view  --->  ");
				        	  
			         break;
				  
			}
			
		}
		
	}
	
	
	 
	
	public  void SetBmp_VideoControl(int ResId, boolean flag)
	{
		 
		MyDebug.MyDebugLogI(TAG, "SetBmp_VideoControl  --->  ");
		
		int[][] Video_Control_Drawable_TRUE = { 
						                       {R.id.bt_pause,         R.drawable.play}, 
						                       {R.id.bt_stepbackward,  R.drawable.stepbackward}, 
						                       {R.id.bt_fullscreen,    R.drawable.displaymode_fullscreen}, 
						                       {R.id.bt_soundcontrol,  R.drawable.sound_open},
						                        
		                                      };
		
		int[][] Video_Control_Drawable_FALSE = { 
							                
							                   {R.id.bt_pause,         R.drawable.pause}, 
							                   {R.id.bt_stepforward,   R.drawable.stepforward}, 
							                   {R.id.bt_fullscreen,    R.drawable.more_about_normal},  
							                   {R.id.bt_soundcontrol,  R.drawable.sound_qianclose}, 
          
                                               };
		
		
		ImageButton IButtom  = (ImageButton) controlView.findViewById(ResId);
		
		
	  
		int i = 0;
		
		int ArryLen = Video_Control_Drawable_TRUE.length;
		
		while(
			    (Video_Control_Drawable_TRUE[i][0] != ResId)
			  &&(i < ArryLen)
			  ) 
			
				{
					i++ ; 
				}
		
		if(i > ArryLen)
		{
			return;
		}
		
		
		if(!flag)
		{
			IButtom.setImageResource(Video_Control_Drawable_TRUE[i][1]);  
			 
		}
		else
		{
			IButtom.setImageResource(Video_Control_Drawable_FALSE[i][1]);
		} 
	}
	
	/**
	 * 
	 * 设置当前电量和时间
	 * 
	 */
	private void setbatteryInfo()
	{
		MyDebug.MyDebugLogI(TAG, "setbatteryInfo  --->  ");
		
		int[][] Battery_Time_Drawable = {  
					                       {0,   R.drawable.battery_10},
						                   {10,  R.drawable.battery_10},
						                   {20,  R.drawable.battery_20},
						                   {50,  R.drawable.battery_50},
						                   {80,  R.drawable.battery_80},
						                   {100, R.drawable.battery_100}, 
						                   {120, R.drawable.battery_100}, 
		                             };
		
		
		int ArryLen = Battery_Time_Drawable.length;
		 
		
		for(int i = 0; i < ArryLen; i++)
		{
			if(
					(BatteryN  >= Battery_Time_Drawable[i][0]) 
				 && (BatteryN  <  Battery_Time_Drawable[i+1][0])
			  )
				 { 
			    	batteryImageView.setImageResource(Battery_Time_Drawable[i][1]);
			    	break;
				 }
			
			
		} 
		
		batteryInfoTextView.setText(String.valueOf(BatteryN) + "%"); 
		
		currentTimeTextView.setText(SystemHandler.getCurrentTime(this));
	}
	
	 
	
	/**
	 * 定时任务
	 *  msg.arg1 = 1;  自动播放
	 *  msg.arg1 = 2;  自动关闭PWMenu
	 * @author qiuj
	 *
	 */
	private class AutoTimerTask extends TimerTask {
		
		static final  int  Play_Task = 1;
		static final  int  Play_Close_Menu = 2; 
		static final  int  RemindTime = 3; 
		
		int  MsgTask = 0;
		
		public AutoTimerTask(int Task) {
			super();
			// TODO Auto-generated constructor stub 
			MsgTask = Task;
			
			if(MsgTask == Play_Task)
			{
				MyDebug.MyDebugLogI(TAG, "Creat  Play  Task ....");
			}
			else if(MsgTask == Play_Close_Menu)
			{
				MyDebug.MyDebugLogI(TAG, "Creat  Play_Close_Menu Task....");
			}
			
			else if(MsgTask == RemindTime)
			{
				MyDebug.MyDebugLogI(TAG, "Creat  RemindTime Task....");
			}
			
			
		}

		@Override
		public void run() 
		{
			MyDebug.MyDebugLogI(TAG, "Start  Task...");
			Message msg = new Message();
			msg.arg1 = MsgTask;
			controlPWHandler.sendMessage(msg);
		}
	}
	
	
	

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();  
		
		MyDebug.MyDebugLogI(TAG, "onPause  --->  ");
		
		//还原声音系统
		SystemHandler.SetSystemSound(MiniPlayer.this,false); 
		
		//SavePara.Saveorientation(this); 
		
		saveorientationflag = true;
		 
		if(player.GetIsPlay())
		{
			player.stop(); 	
		}
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		
		MyDebug.MyDebugLogI(TAG, "onResume  --->  ");
		
		//还原声音系统
		SystemHandler.SetSystemSound(MiniPlayer.this,false);
		
		/*if(saveorientationflag)
		{
		   SavePara.Setorientation(this);
		}*/
	}
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		
		MyDebug.MyDebugLogI(TAG, "onStop  --->  ");
		
		closePwMenu(null);
		
		if(mBatInfoReceiver != null )
		{
		      unregisterReceiver(mBatInfoReceiver);
		      
		      MyDebug.MyDebugLogI(TAG, "unregisterReceiver  --->  mBatInfoReceiver");
		}
		
		if(Timerresh != null)
		{
			Timerresh.cancel(); 
			
			MyDebug.MyDebugLogI(TAG, "Timerresh    cancel--->  ");
		}
		
		if(PWMenuTimer != null)
		{
			PWMenuTimer.cancel(); 
			
			MyDebug.MyDebugLogI(TAG, "PWMenuTimer    cancel--->  ");
		}
		  
		 
		if(player != null) 
		{
			player.PlayerDestroyed();
			
			MyDebug.MyDebugLogI(TAG, "player    PlayerDestroyed--->  ");
		}
	}
	
	@Override
	public void onDetachedFromWindow() {
		// TODO Auto-generated method stub
		try {
			super.onDetachedFromWindow();
			}
			catch (IllegalArgumentException e) {
				player.stop();
			}
	}


	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			if (player != null) {  
	        	player.stop();  
	            //释放资源  
	        } 
			finish();
			Intent intent = new Intent();
			intent.setClass(MiniPlayer.this, DetailActivity.class);
			startActivity(intent);
			
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override  
    protected void onDestroy() {  
        if (player != null) {  
        	player.stop();  
            //释放资源  
        }  
        super.onDestroy();  
    }


	public void onConfigurationChanged(Configuration newConfig) {  
		 super.onConfigurationChanged(newConfig);
		 if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			// SystemHandler.SetdisplayFullsreen(this);
			 try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			 initView();
			 getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); 
		     //   Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();
	    } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
	    	// getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); 
	    	 try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} 
	    	initView();
	    	 getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); 
	        //Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
	    }
    }  
	
	
	
}
