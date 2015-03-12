/**
 *   MyViewFilpper
 *   Copyright (C) 2011  CFuture . Chen Yong Jia
 *   
 * 	 This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.

 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.

 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *   
 */
package com.smartlearning.common;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ViewFlipper;

public class MyViewFilpper extends ViewFlipper implements OnTouchListener, OnGestureListener {
	//手势识别类
	private GestureDetector gestureDetector;
	//当前显示的项目的序号
	private int currentItemIndex = 0;  
	private int totalItemCounts = 0;
	//滑动时最小像素距离
    private static final int FlingMinDistance = 100;   
    private static final int FlingMinVelocity = 200;
    //是否开启滑动效果
    private boolean filpping = true;
    private FilpperCallback callback;
    
	public MyViewFilpper(Context context, AttributeSet attrs) {
		super(context, attrs);
		gestureDetector = new GestureDetector(this);
		this.setOnTouchListener(this);
		this.setLongClickable(true);  
	}
	
	public void setCallback(FilpperCallback fc){
		callback = fc;
		callback(currentItemIndex);
	}

	public void setTotalItemCounts(int count){
		totalItemCounts = count ;
	}
	/**  
     * 定义从右侧进入的动画效果  
     * @return  
     */  
    protected Animation inFromRightAnimation() {  
        Animation inFromRight = new TranslateAnimation(  
                Animation.RELATIVE_TO_PARENT, +1.0f,  
                Animation.RELATIVE_TO_PARENT, 0.0f,  
                Animation.RELATIVE_TO_PARENT, 0.0f,  
                Animation.RELATIVE_TO_PARENT, 0.0f);  
        inFromRight.setDuration(200);  
        inFromRight.setInterpolator(new AccelerateInterpolator());  
        return inFromRight;  
    }  
   
    /**  
     * 定义从左侧退出的动画效果  
     * @return  
     */  
    protected Animation outToLeftAnimation() {  
        Animation outtoLeft = new TranslateAnimation(  
                Animation.RELATIVE_TO_PARENT, 0.0f,  
                Animation.RELATIVE_TO_PARENT, -1.0f,  
                Animation.RELATIVE_TO_PARENT, 0.0f,  
                Animation.RELATIVE_TO_PARENT, 0.0f);  
        outtoLeft.setDuration(200);  
        outtoLeft.setInterpolator(new AccelerateInterpolator());  
        return outtoLeft;  
    }  
   
    /**  
     * 定义从左侧进入的动画效果  
     * @return  
     */  
    protected Animation inFromLeftAnimation() {  
        Animation inFromLeft = new TranslateAnimation(  
                Animation.RELATIVE_TO_PARENT, -1.0f,  
                Animation.RELATIVE_TO_PARENT, 0.0f,  
                Animation.RELATIVE_TO_PARENT, 0.0f,  
                Animation.RELATIVE_TO_PARENT, 0.0f);  
        inFromLeft.setDuration(200);  
        inFromLeft.setInterpolator(new AccelerateInterpolator());  
        return inFromLeft;  
    }  
   
    /**  
     * 定义从右侧退出时的动画效果  
     * @return  
     */  
    protected Animation outToRightAnimation() {  
        Animation outtoRight = new TranslateAnimation(  
                Animation.RELATIVE_TO_PARENT, 0.0f,  
                Animation.RELATIVE_TO_PARENT, +1.0f,  
                Animation.RELATIVE_TO_PARENT, 0.0f,  
                Animation.RELATIVE_TO_PARENT, 0.0f);  
        outtoRight.setDuration(200);  
        outtoRight.setInterpolator(new AccelerateInterpolator());  
        return outtoRight;  
    }  
	@Override
	public boolean onDown(MotionEvent e) {
		return false;
	}
	
	@Override
	public void showNext() {
		this.setInAnimation(inFromRightAnimation());  
	    this.setOutAnimation(outToLeftAnimation()); 
		super.showNext();
		++currentItemIndex;
		if(currentItemIndex >= totalItemCounts) currentItemIndex = 0;
		callback(currentItemIndex); 
	}
	
	@Override
	public void showPrevious(){
		this.setInAnimation(inFromLeftAnimation());  
	    this.setOutAnimation(outToRightAnimation()); 
		super.showPrevious();
		currentItemIndex--;
		if(currentItemIndex < 0)currentItemIndex=totalItemCounts-1;
		callback(currentItemIndex); 
	}
	
	private void callback(int index){
		final int rs = totalItemCounts == 0 ? 0 : index % totalItemCounts;
		callback.subjectChange(rs);
	}
	
	
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		if(filpping){
			if (e1.getX() - e2.getX() > FlingMinDistance && Math.abs(velocityX) > FlingMinVelocity) {  
	            this.showNext();  
	        } else if (e2.getX() - e1.getX() > FlingMinDistance && Math.abs(velocityX) > FlingMinVelocity) {  
	        	this.showPrevious();  
	        }  
		}
        return !filpping;
	}
	
	 /**  
     * 指定跳转到某个页面  
     * @param index  
     */  
    public void switchTo(int index) {  
        while (currentItemIndex != index) {  
            if (currentItemIndex > index) {  
                currentItemIndex--;  
                this.setInAnimation(inFromLeftAnimation());  
                this.setOutAnimation(outToRightAnimation());  
                this.showPrevious();  
            } else {  
                currentItemIndex++;  
                this.setInAnimation(inFromRightAnimation());  
                this.setOutAnimation(outToLeftAnimation());  
                this.showNext();  
            }  
        }  
        callback.subjectChange(currentItemIndex);  
    }  
    

	@Override
	public void onLongPress(MotionEvent e) {

	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return gestureDetector.onTouchEvent(event);
	}

}
