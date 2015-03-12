package com.smartlearning.player;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Handler;
import android.os.Message;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
  
public class Player implements OnBufferingUpdateListener, 
                               OnCompletionListener, 
                               MediaPlayer.OnPreparedListener,
                               SurfaceHolder.Callback {
	
	
	public static final String TAG  =  "MyPlayer";
	
	private TextView hasPlayedTV;
	private TextView durationTV;
	
    private int videoWidth;  
    private int videoHeight;  
    
    public MediaPlayer mediaPlayer;  
    private SurfaceHolder surfaceHolder;  
    
    private SeekBar skbProgress;  
    
    private ProgressBar videoProgressBar = null;
    
    private Timer   mTimer  = null;  
    private boolean isOL    = false;
    private  int duration=0;
    private  int position=0; 
    
    private MiniPlayer mMiniPlayer     = null;   
    public  boolean IsHave_MediaPlayer = false;
    
    
    public  int  BufferingUpdateOld   = 0;
    public  int  BufferingUpdateCount = 0;
     
    
    public Player(SurfaceView surfaceView,
    		      SeekBar     skbProgress,
    		      TextView    hasPlayedTV,
    		      TextView    durationTV,
    		      MiniPlayer  mMiniPlayer)  
    {  
    	
    	MyDebug.MyDebugLogI(TAG, "Player!");
    	
    	if   (
    			  (hasPlayedTV == null)
    			||(surfaceView == null)  
    			||(skbProgress == null)
    			||(durationTV  == null)
    			||(mMiniPlayer  == null)
    		 )
    	{
    		
    		MyDebug.MyDebugLogI(TAG,"Player  constructor  error !");
    		MyDebug.MyDebugLogI(TAG,"hasPlayedTV = " + hasPlayedTV.toString());
    		MyDebug.MyDebugLogI(TAG,"surfaceView = " + surfaceView.toString());
    		MyDebug.MyDebugLogI(TAG,"skbProgress = " + skbProgress.toString());
    		MyDebug.MyDebugLogI(TAG,"durationTV  = " + durationTV.toString());
    		MyDebug.MyDebugLogI(TAG,"mMiniPlayer = " + mMiniPlayer.toString());
    		
    		return; 
    	}
    	
    	
    	this.hasPlayedTV = hasPlayedTV;
    	this.durationTV  = durationTV;
        this.skbProgress = skbProgress;  
        this.mMiniPlayer = mMiniPlayer;
        
         
        surfaceHolder = surfaceView.getHolder();  
        surfaceHolder.addCallback(this);  
        
        // SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS 原生数据
        // 这种情况下，不能使用      lockCanvas 来获取Canvas
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);  
        
        
        /******播放器是否处于使用状态，默认为空闲状态********/
        IsHave_MediaPlayer = false;   
        
        mTimer = new Timer();
        mTimer.schedule(mTimerTask, 0, 500);  
    }  
      
    /******************************************************* 
     * 通过定时器和Handler来更新进度条 
     ******************************************************/  
    TimerTask mTimerTask = new TimerTask() {  
        @Override  
        public void run() 
        {  
        	  
            	MyDebug.MyDebugLogI(TAG, "IsHave_MediaPlayer =  " + String.valueOf(IsHave_MediaPlayer)); 
        	    if(mediaPlayer == null)   return;   
               
        	 //    IsHave_MediaPlayer = mediaPlayer.isPlaying();
        	    
	            if (mediaPlayer.isPlaying() && skbProgress.isPressed() == false)
	            {  
	                handleProgress.sendEmptyMessage(0);  
	            }  
        }  
    };
    
      //Handler的消息处理
    Handler handleProgress = new Handler() 
    {   
		public void handleMessage(Message msg) 
		{  
            if(!IsHave_MediaPlayer) return;
           
            position = mediaPlayer.getCurrentPosition();  
            if (duration > 0)
            {  
                long pos = skbProgress.getMax() * position / duration;  
                skbProgress.setProgress((int) pos);
                hasPlayedTV.setText(getVideoTime(position));
            }  
        };  
    };  
    
    
    public  void  PlayerDestroyed()
    {
    
    	MyDebug.MyDebugLogI(TAG, "PlayerDestroyed ---->"); 
    	 
    	 if(mTimer != null)
    	 {
    		 mTimerTask.cancel();
    		 mTimer.cancel();
    	 }
    	
    }
    
    public  boolean GetIsPlay()
    {
    	MyDebug.MyDebugLogI(TAG, "GetIsPlay ---->"); 
    	 boolean  IsPLAY = false; 
    	 
    	  if(mediaPlayer != null)
    	  {  	   
    		   IsPLAY  =  (mediaPlayer.isPlaying() == IsHave_MediaPlayer) ? 
    				       mediaPlayer.isPlaying() : IsHave_MediaPlayer;
    	  }
    	 
		return IsPLAY; 
    }
    //*****************************************************   
      
    public void play()  
    {  
    	 
    	videoWidth  = mediaPlayer.getVideoWidth();
        videoHeight = mediaPlayer.getVideoHeight();
       
        if (videoHeight != 0 && videoWidth != 0) {
        	mediaPlayer.start();  
        }  
        
    }  
    public void playUrl(String videoUrl)  
    {  
    	
    	MyDebug.MyDebugLogI(TAG, "playUrl---->");

        try {  
        	if(mediaPlayer == null)
        	{
        		MyDebug.MyDebugLogI(TAG, "MediaPlayer have not!");
        		return;
        	}
        	
        	
             BufferingUpdateOld   = 0;
             BufferingUpdateCount = 0;
            
             IsHave_MediaPlayer = false;
             mediaPlayer.reset(); 
             mediaPlayer.setDataSource(videoUrl);  
             
             
             
             //异步播放视屏
             mediaPlayer.prepareAsync(); 
             
             
             MyDebug.MyDebugLogI(TAG, "MediaPlayer Start  play!");

               
        } catch (IllegalArgumentException e) {  
            // TODO Auto-generated catch block   
            e.printStackTrace();
            
            MyDebug.MyDebugLogE(TAG, "MediaPlayer Start  play  ---> IllegalArgumentException!");
            
            mediaPlayer.stop();
        	mediaPlayer.release();
            IsHave_MediaPlayer = false;
            
            
        } catch (IllegalStateException e) {  
            // TODO Auto-generated catch block   
            e.printStackTrace();  
            
            MyDebug.MyDebugLogE(TAG, "MediaPlayer Start  play  ---> IllegalStateException!");
            
            mediaPlayer.stop();
        	mediaPlayer.release();
            IsHave_MediaPlayer = false;
            
        } catch (IOException e) {  
            // TODO Auto-generated catch block   
            e.printStackTrace();  
            
            mediaPlayer.stop();
        	mediaPlayer.release();
        	
        	IsHave_MediaPlayer = false;
        	
        	MyDebug.MyDebugLogE(TAG, "MediaPlayer Start  play  ---> IOException!");
        	
        	
        	
        }finally
        {
        	
        	
        }  
       
    }  
  
  
      
    public void pause()  
    {  
    	
    	MyDebug.MyDebugLogI(TAG, "pause ---->"); 
    	
    	if(!IsHave_MediaPlayer) return; 
    	
    	if(mediaPlayer != null)
    	{ 
            mediaPlayer.pause();   
    	}
    }  
      
    public void stop()  
    {  
    	
    	MyDebug.MyDebugLogI(TAG, "stop ---->"); 
    	
    	if(!IsHave_MediaPlayer) return;
    	
        if (mediaPlayer != null) 
        {   
        	 
            mediaPlayer.stop();  
            mediaPlayer.release();   
            mediaPlayer = null;  
            IsHave_MediaPlayer = false;   //设置为空闲状态
        }   
    }  
    
    public void seekTo(int msec)  
    {  
    	MyDebug.MyDebugLogI(TAG, "seekTo ---->"); 
    	
    	if(!IsHave_MediaPlayer) return; 
    	
        if (mediaPlayer != null) 
        {   
            mediaPlayer.seekTo(msec);  
        }   
    }
    
    
    public int getCurrentPosition()  
    {  
    	MyDebug.MyDebugLogI(TAG, "getCurrentPosition ---->"); 
    	
        int result = -1;
    	if (mediaPlayer != null) 
        {   
        	if(IsHave_MediaPlayer) 
        	{ 
        		result = mediaPlayer.getCurrentPosition(); 
        	}
        }  
    	return result;
    }
    
    
    public int getDuration()  
    {  
    	MyDebug.MyDebugLogI(TAG, "getDuration ---->"); 
    	
        int result = -1;
    	if (mediaPlayer != null) 
        {   
        	if(IsHave_MediaPlayer) 
        	{ 
        		result = mediaPlayer.getDuration(); 
        	}
        }  
    	return result;
    }
    
    
    public int getBufferingUpdatePosition()  
    {    
    	
    	MyDebug.MyDebugLogI(TAG, "getBufferingUpdatePosition ---->" ); 
    	
        int result = -1;
    	if (mediaPlayer != null) 
        {   
        	if(IsHave_MediaPlayer) 
        	{  
        		result = BufferingUpdateOld * mediaPlayer.getDuration()/100;  
        	}
        } 
    	
    	MyDebug.MyDebugLogI(TAG, "getBufferingUpdatePosition ---->" + String.valueOf(result)); 
    
    	return result;
    }
    
    
    
    public int getVideoWidth()  
    {  
    	
    	MyDebug.MyDebugLogI(TAG, "getVideoWidth ---->"); 
    	
        int result = -1;
    	if (mediaPlayer != null) 
        {   
        	if(IsHave_MediaPlayer) 
        	{ 
        		result = mediaPlayer.getVideoWidth(); 
        	}
        }  
    	return result;
    }
    
    
    public int getVideoHeight()  
    {  
    	
    	
    	MyDebug.MyDebugLogI(TAG, "getVideoHeight ---->");
    	
        int result = -1;
    	if (mediaPlayer != null) 
        {   
        	if(IsHave_MediaPlayer) 
        	{ 
        		result = mediaPlayer.getVideoHeight(); 
        	}
        }  
    	return result;
    }
      
    
  /*  public boolean IsCurrentPositionAndgDuration(int flag)
    {
    	
    	  boolean  IsDaLing = false;
	  	      
	  		
	  		 IsDaLing  =   ( 
	  				            (getCurrentPosition() > 0) 
	  				        &&  (getDuration() > 0)
	  				        
	  				        ) ?  true : false;
	  	  
	  	 
		 return IsDaLing;  
    }*/
 
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {  
    	MyDebug.MyDebugLogI(TAG, "surface changed...");  
    }  
  
   
    public void surfaceCreated(SurfaceHolder arg0) 
    {  
        try {  
            
        	MyDebug.MyDebugLogI(TAG, "surfaceCreated...");
            
            mediaPlayer = null; 
            
            mediaPlayer = new MediaPlayer();   
            
            mediaPlayer.setDisplay(surfaceHolder);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);  
            
            mediaPlayer.setOnBufferingUpdateListener(this);  
            mediaPlayer.setOnPreparedListener(this);  
            mediaPlayer.setOnCompletionListener(this);
            

        } catch (Exception e) 
        {  
        	MyDebug.MyDebugLogI(TAG, "mediaPlayer  error"); 
        }
        finally
        {
        	
        }
        
        MyDebug.MyDebugLogI(TAG, "surface created   finish..");  
    }  
  

    public void surfaceDestroyed(SurfaceHolder arg0) {  
        
    	MyDebug.MyDebugLogI(TAG, "surface destroyed   finish..");  
    }  
   
    
    private String  Int2Str(int time, int count)
    { 
    	
    	StringBuffer  str = new StringBuffer();  
    	str.delete(0, str.length());
    	str.append(time); 
    	if(str.length() < count)
	    	{ 	
	    		str.insert(0, 0);
	    	} 
		return str.toString(); 
    }
    

    private String getVideoTime(int duration){
    	
         int h = duration/1000/3600; 
         int m = duration/1000%3600/60;
         int s = duration/1000%3600%60;   
         return (Int2Str(h,2) + ":" + Int2Str(m,2) + ":" + Int2Str(s,2)); 
    }
    
    
    public void onPrepared(MediaPlayer arg0) 
    {  
    	 
    	MyDebug.MyDebugLogI(TAG, "mediaPlayer onPrepared");  
    	 
    	IsHave_MediaPlayer = true;
    	
        videoWidth  = mediaPlayer.getVideoWidth();  
        videoHeight = mediaPlayer.getVideoHeight();   
        duration    = mediaPlayer.getDuration();  
        
        if (videoHeight != 0 && videoWidth != 0)
        {  
            arg0.start();   
			//durationTV.setText(getVideoTime( arg0.getDuration()));
			durationTV.setText(getVideoTime(duration));
            
        }  
        
        
       
    }  
  
    public boolean getPlayStatus(){
    	 
    	MyDebug.MyDebugLogI(TAG, ""+mediaPlayer.isPlaying()); 
    	
		return mediaPlayer.isPlaying();
    	
    }
    public void onCompletion(MediaPlayer arg0) {  
        // TODO Auto-generated method stub   
    	
    	MyDebug.MyDebugLogI(TAG, "MediaPlayer onCompletion"); 
    	 
    	if(arg0 != null)
    	{  
    	    
    	   
    	   //arg0.seekTo(0);  
    	  // arg0.stop();
    	  // arg0.release();
    	   
    	  //  mMiniPlayer.SetBmp_VideoControl(R.id.bt_pause, false);
    	 //  mMiniPlayer.onBackPressed();
    	   
    	  // IsHave_MediaPlayer = false;
    	}
    }  
  
 
    public void onBufferingUpdate(MediaPlayer arg0, int bufferingProgress) 
    {  
    	  
    	   MyDebug.MyDebugLogI(TAG, "onBufferingUpdate");
    	
    	  if(!IsHave_MediaPlayer) return; 
    	 
    	   int currentProgress = 0; 
           currentProgress = skbProgress.getMax()* mediaPlayer.getCurrentPosition()/mediaPlayer.getDuration();
         
           MyDebug.MyDebugLogI(TAG, String.valueOf(currentProgress)+"% play!--->" + String.valueOf(bufferingProgress) + "% buffer.");
    	 
    	 if(BufferingUpdateOld == bufferingProgress)
    	 {
    		
    		 // 如果100次相同，则不设置进度条
    		 if(BufferingUpdateCount <= 99)
    		 {
    			 BufferingUpdateCount++;
    			 BufferingUpdateOld = bufferingProgress;
    			 
    			 return;
    		 }
    		
    	 }
    	 
    	 
    	 BufferingUpdateCount = 0;	
    	 
    	 BufferingUpdateOld   = bufferingProgress;    	
    	 
    	 skbProgress.setSecondaryProgress(bufferingProgress); 
    }  
  
}  
