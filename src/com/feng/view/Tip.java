package com.feng.view;

import com.smartlearning.R;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

public class Tip {

    private ImageView image;
    private Dialog mDialog;
    
    public Tip(Context context,String info) {
        mDialog = new Dialog(context, R.style.dialog);
        Window window = mDialog.getWindow();
        window.setWindowAnimations(R.style.dialogWindowAnim); //设置窗口弹出动画
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.x = -30;
        wl.y = 20;
        window.setAttributes(wl);
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        //window.setGravity(Gravity.CENTER);
        window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        
        LayoutInflater inflater = LayoutInflater.from(context);  
		View view = inflater.inflate(R.layout.tip, null);  
		TextView textView = (TextView)view.findViewById(R.id.description);
		textView.setText(info);
		
        mDialog.setContentView(view);
        
        mDialog.setFeatureDrawableAlpha(Window.FEATURE_OPTIONS_PANEL, 0);
        
    }

    public void show() {
        mDialog.show();
    }

}