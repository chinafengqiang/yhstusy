package com.smartlearning.crop;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.smartlearning.R;
import com.smartlearning.common.Crop_Canvas;

public class CropMainActivity extends Activity {
	
	private Crop_Canvas canvas = null;
	private String url ="";
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.crop_activity_main);
        
        Intent it = getIntent();
		Bundle bundle = it.getExtras();
		url = bundle.getString("url");
        
        Log.i("url", "urlurlurlurl==="+url);
        
        this.init();
    }
    
    private void init(){
    	canvas = (Crop_Canvas)findViewById(R.id.myCanvas);
    	ImageButton savePic = (ImageButton)findViewById(R.id.btn1);
    	ImageButton exiitPic = (ImageButton)findViewById(R.id.btn2);
    	savePic.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				
				Bitmap bitmap = canvas.getSubsetBitmap();
				canvas.setBitmap(bitmap);
				saveMyBitmap(bitmap);
				finish();
				
			}
		});
    	exiitPic.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
    	Bitmap bitmap = BitmapFactory.decodeFile(url);
    //	Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.bag);
    	canvas.setBitmap(bitmap);
    }
    
    
    /**
     * 保存图片
     * @param mBitmap
     */
    public void saveMyBitmap(Bitmap mBitmap)  {
        File f = new File(url);
        Log.i("url", "saveMyBitmap=="+url);
        FileOutputStream fOut = null;
        try {
                fOut = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
                e.printStackTrace();
        }
        mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
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
    
    public void confirmFunction(View view){
    	canvas.setBitmap(canvas.getSubsetBitmap());
    }
    
    public void exitFunction(View view){
    	this.finish();
    }
}