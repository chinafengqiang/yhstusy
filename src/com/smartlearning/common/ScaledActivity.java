package com.smartlearning.common;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

import com.smartlearning.utils.RelayoutTool;

/**
 * ui
 * @author user
 */
public class ScaledActivity extends Activity {

		//缩放比例
	    private static float scale = 0;
	    //标准UI设计宽度（px）
	    private static final float UI_DESIGN_WIDTH = 640.0f;
	 
	    @Override
	    public void setContentView(int layoutResID) {
	        View view = View.inflate(this, layoutResID, null);
	        this.setContentView(view);
	    }
	 
	    @Override
	    public void setContentView(View view) {
	        if(scale == 0){
	            initScreenScale();
	        }
	        RelayoutTool.relayoutViewHierarchy(view, scale);
	        super.setContentView(view);
	    }
	    @Override
	    public void setContentView(View view, LayoutParams params) {
	        if(scale == 0){
	            initScreenScale();
	        }
	        RelayoutTool.relayoutViewHierarchy(view, scale);
	        RelayoutTool.scaleLayoutParams(params, scale);
	        super.setContentView(view, params);
	    }
	     
	    /**
	     * 按屏幕宽度与设计宽度初始化缩放比例
	     */
	    private void initScreenScale(){
	        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
	        scale = displayMetrics.widthPixels / UI_DESIGN_WIDTH;
	    }
	}

