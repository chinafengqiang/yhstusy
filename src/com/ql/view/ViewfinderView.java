/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ql.view;

 

import android.content.Context;
import android.content.res.Resources; 
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.Collection;
import java.util.HashSet;

 

 
public final class ViewfinderView extends View {

  private  String TAG =  "ViewfinderView";
	
  
  
 
  
// 当前能够显示的最大区域
	private  int ViewMaxW  = 800;
	private  int ViewMaxH  = 640; 

  // This constructor is used when the class is built from an XML resource.
  public ViewfinderView(Context context, AttributeSet attrs) {
    super(context, attrs);

    
 
  }
  
  
  public  void SetDisplayRect(int ViewW, int ViewH)
  {
	  ViewMaxW = ViewW;  
	  ViewMaxH = ViewH;  
  }

  
  public void drawViewfinder() {
	    
	     invalidate();
  }
  
  
  @Override
  public void onDraw(Canvas canvas) {
	  

		
	  Log.i(TAG, "onDraw - onDraw = " );
	
	  
	  int offset = 0;
	  
	  int width   = ViewMaxW;
	  int height  = ViewMaxH;
	  
	  
	
	  Paint  paint   = new Paint(); 
	  Rect   frame   = new Rect();
	  
	  offset = 5;
	  frame.top    = offset;
	  frame.left   = offset;
	  frame.right  = ViewMaxW - offset;
	  frame.bottom = ViewMaxH - offset; 
	  
      
  //    paint.setARGB(255, 255, 0, 0);
      canvas.drawRect(0, 0, width, frame.top, paint);
      canvas.drawRect(0, frame.top, frame.left, frame.bottom, paint);   
      canvas.drawRect(width - offset, frame.top, width, frame.bottom, paint);
      canvas.drawRect(0, frame.bottom, width, height, paint);
      
      
      offset = 2;
	  frame.top    = offset;
	  frame.left   = offset;
	  frame.right  = ViewMaxW - offset;
	  frame.bottom = ViewMaxH - offset;
	  
	  paint.setARGB(255, 100, 120, 200);
      canvas.drawRect(0, 0, width, frame.top, paint);
      canvas.drawRect(0, frame.top, frame.left, frame.bottom, paint);   
      canvas.drawRect(width - offset, frame.top, width, frame.bottom, paint);
      canvas.drawRect(0, frame.bottom, width, height, paint);
       
        
      
      paint.setARGB(200, 0, 0, 200);  
      paint.setTextSize(40);
 
      String [] diaplay = {"1"};
      
//      String [] diaplay =  {"如果这一切都是真实,",
//							"为何我抓不住你眼角的泪滴!",
//							"如果这一切并不真实,",
//							"为何我走不出这里再一次与你相遇..."};
      Path path = null;
      int count = 0;
//      while(count < 4)
//      {
	      path = new Path();   
	      
	      path.moveTo(120,   frame.top + (count+1)*90 + 50);	      
	      path.lineTo(width, frame.top + (count+1)*40);  
	      
	      path.close();   
	      
//	      canvas.drawTextOnPath(diaplay[count ++], path, 0, 0, paint);   
	      
	      path = null;
//      } 
      
      paint = null;
     
  }


 
 
}
