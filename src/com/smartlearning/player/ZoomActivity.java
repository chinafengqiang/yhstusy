package com.smartlearning.player;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ZoomControls;

import com.smartlearning.R;
import com.smartlearning.constant.ServerIP;
import com.smartlearning.utils.ImageService;


public class ZoomActivity extends Activity {
	private final int LOADING_IMAGE = 1;
	public static String KEY_IMAGEURI = "ImageUri";
	private ZoomControls zoom;
	private ImageView mImageView;
	private LinearLayout layoutImage;
	private int displayWidth;
	private int displayHeight;
	/** 图片资源 */
	private Bitmap bmp;
	/** 宽的缩放比例 */
	private float scaleWidth = 1;
	/** 高的缩放比例 */
	private float scaleHeight = 1;
	/** 用来计数放大+1 缩小-1 */
	private int zoomNumber = 0;
	/** 点击屏幕显示缩放按钮，三秒消失 */
	private int showTime = 3000;

	RelativeLayout rl;
	Handler mHandler = new Handler();
	private Runnable task = new Runnable() {
		public void run() {

			zoom.setVisibility(View.INVISIBLE);

		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image);
		// showDialog(LOADING_IMAGE);
		// 图片是从网络上获取的话，需要加入滚动条
		
		String url = getIntent().getStringExtra("picUrl");
		String image_url = ServerIP.VIDEO_INFO_URL + url; 
		byte[] result;	
		try {
			result = ImageService.getImage(image_url);
			bmp = BitmapFactory.decodeByteArray(result, 0,result.length);// 生成图片
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	//	bmp = BitmapFactory.decodeResource(getResources(), R.drawable.huoying);
		// removeDialog(LOADING_IMAGE);
		initZoom();
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case LOADING_IMAGE: {
			final ProgressDialog dialog = new ProgressDialog(this);
			dialog.setOnKeyListener(new OnKeyListener() {
				public boolean onKey(DialogInterface dialog, int keyCode,
						KeyEvent event) {
					if (keyCode == KeyEvent.KEYCODE_BACK) {
						finish();
					}
					return false;
				}
			});
			dialog.setMessage("正在加载图片请稍后...");
			dialog.setIndeterminate(true);
			dialog.setCancelable(true);
			return dialog;
		}
		}
		return null;
	}

	public void initZoom() {

		/* 取得屏幕分辨率大小 */
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		displayWidth = dm.widthPixels;
		displayHeight = dm.heightPixels;
		mImageView = (ImageView) findViewById(R.id.myImageView);
		mImageView.setImageBitmap(bmp);
		layoutImage = (LinearLayout) findViewById(R.id.layoutImage);
		mImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				/**
				 * 在图片上和整个view上同时添加点击监听捕捉屏幕 点击事件，来显示放大缩小按钮
				 * */
				zoom.setVisibility(View.VISIBLE);
				mHandler.removeCallbacks(task);
				mHandler.postDelayed(task, showTime);
			}
		});

		layoutImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				zoom.setVisibility(View.VISIBLE);
				mHandler.removeCallbacks(task);
				mHandler.postDelayed(task, showTime);
			}
		});

		zoom = (ZoomControls) findViewById(R.id.zoomcontrol);
		zoom.setIsZoomInEnabled(true);
		zoom.setIsZoomOutEnabled(true);
		// 图片放大
		zoom.setOnZoomInClickListener(new OnClickListener() {
			public void onClick(View v) {
				big();
			}
		});
		// 图片减小
		zoom.setOnZoomOutClickListener(new OnClickListener() {

			public void onClick(View v) {
				small();
			}

		});

		zoom.setVisibility(View.VISIBLE);
		mHandler.postDelayed(task, showTime);

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		/**
		 * 在图片上和整个view上同时添加点击监听捕捉屏幕 点击事件，来显示放大缩小按钮
		 * */
		zoom.setVisibility(View.VISIBLE);
		mHandler.removeCallbacks(task);
		mHandler.postDelayed(task, showTime);
		return false;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		super.onKeyDown(keyCode, event);

		return true;
	}

	/* 图片缩小的method */
	private void small() {

		--zoomNumber;
		int bmpWidth = bmp.getWidth();
		int bmpHeight = bmp.getHeight();

		Log.i("", "bmpWidth = " + bmpWidth + ", bmpHeight = " + bmpHeight);

		/* 设置图片缩小的比例 */
		double scale = 0.6;
		/* 计算出这次要缩小的比例 */
		scaleWidth = (float) (scaleWidth * scale);
		scaleHeight = (float) (scaleHeight * scale);
		/* 产生reSize后的Bitmap对象 */
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap resizeBmp = Bitmap.createBitmap(bmp, 0, 0, bmpWidth, bmpHeight,
				matrix, true);
		mImageView.setImageBitmap(resizeBmp);

		/* 限制缩小尺寸 */
		if ((scaleWidth * scale * bmpWidth < bmpWidth / 4
				|| scaleHeight * scale * bmpHeight > bmpWidth / 4
				|| scaleWidth * scale * bmpWidth > displayWidth / 5 || scaleHeight
				* scale * bmpHeight > displayHeight / 5)
				&& (zoomNumber == -1)) {

			zoom.setIsZoomOutEnabled(false);

		} else {

			zoom.setIsZoomOutEnabled(true);

		}

		zoom.setIsZoomInEnabled(true);
		System.gc();
	}

	/* 图片放大的method */
	private void big() {
		++zoomNumber;
		int bmpWidth = bmp.getWidth();
		int bmpHeight = bmp.getHeight();

		/* 设置图片放大的比例 */
		double scale = 1.25;
		/* 计算这次要放大的比例 */
		scaleWidth = (float) (scaleWidth * scale);
		scaleHeight = (float) (scaleHeight * scale);
		/* 产生reSize后的Bitmap对象 */
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap resizeBmp = Bitmap.createBitmap(bmp, 0, 0, bmpWidth, bmpHeight,
				matrix, true);
		mImageView.setImageBitmap(resizeBmp);
		/* 限制放大尺寸 */
		if (scaleWidth * scale * bmpWidth > bmpWidth * 4
				|| scaleHeight * scale * bmpHeight > bmpWidth * 4
				|| scaleWidth * scale * bmpWidth > displayWidth * 5
				|| scaleHeight * scale * bmpHeight > displayHeight * 5) {

			zoom.setIsZoomInEnabled(false);

		} else {

			zoom.setIsZoomInEnabled(true);

		}

		zoom.setIsZoomOutEnabled(true);

		System.gc();
	}

}
