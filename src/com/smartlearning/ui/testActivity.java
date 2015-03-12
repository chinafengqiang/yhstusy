package com.smartlearning.ui;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.smartlearning.R;
import com.smartlearning.task.ImageControl;
import com.smartlearning.task.ImageControl.ICustomMethod;

public class testActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.common_image_view);
		findView();
	}

	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		init();
	}

	ImageControl imgControl;
	LinearLayout llTitle;
	TextView tvTitle;

	private void findView() {
		imgControl = (ImageControl) findViewById(R.id.common_imageview_imageControl1);
		llTitle = (LinearLayout) findViewById(R.id.common_imageview_llTitle);
		tvTitle = (TextView) findViewById(R.id.common_imageview_title);
	}

	private void init() {
		tvTitle.setText("图片测试");
		imgControl.setBackgroundColor(R.drawable.sx_hot);
		// 这里可以为imgcontrol的图片路径动态赋值
		// ............
		
		Bitmap bmp;
		if (imgControl.getDrawingCache() != null) {
			bmp = Bitmap.createBitmap(imgControl.getDrawingCache());
		} else {
			bmp = ((BitmapDrawable) imgControl.getDrawable()).getBitmap();
		}
		Rect frame = new Rect();
		getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
		int statusBarHeight = frame.top;
		int screenW = this.getWindowManager().getDefaultDisplay().getWidth();
		int screenH = this.getWindowManager().getDefaultDisplay().getHeight()
				- statusBarHeight;
		
		if (bmp != null) {
			imgControl.imageInit(bmp, screenW, screenH, statusBarHeight,
					new ICustomMethod() {
                      
						@Override
						public void customMethod(Boolean currentStatus) {
							// 当图片处于放大或缩小状态时，控制标题是否显示
							if (currentStatus) {
								llTitle.setVisibility(View.GONE);
							} else {
								llTitle.setVisibility(View.VISIBLE);
							}
						}
					});
		}
		else
		{
			Toast.makeText(testActivity.this, "图片加载失败，请稍候再试！", Toast.LENGTH_SHORT)
					.show();
		}

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			imgControl.mouseDown(event);			
			break;

		/**
		 * 非第一个点按下
		 */
		case MotionEvent.ACTION_POINTER_DOWN:
		
				imgControl.mousePointDown(event);
		
			break;
		case MotionEvent.ACTION_MOVE:
				imgControl.mouseMove(event);
			
			break;

		case MotionEvent.ACTION_UP:
			imgControl.mouseUp();
			break;

		}

		return super.onTouchEvent(event);
	}
}
