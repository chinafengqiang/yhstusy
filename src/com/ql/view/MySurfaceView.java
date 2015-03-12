package com.ql.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceView;

public class MySurfaceView extends SurfaceView {

	// private final int W_Flag = 0;

	private String TAG = "MySurfaceView";

	public enum WH_Flag {
		W_Flag, H_Flag
	}

	public enum VideoDiaplaySize_Flag {
		ViewFullSreen, ViewMax, ViewCustom
	}

	// 全屏显示的区域
	private int ViewFullSreenW = 800;
	private int ViewFullSreenH = 640;

	// 当前能够显示的最大区域
	private int ViewMaxW = 800;
	private int ViewMaxH = 640;

	// 指定显示区域
	private int ViewCustomW = 800;
	private int ViewCustomH = 640;

	private Enum<VideoDiaplaySize_Flag> VideoDisplaySize;

	private Paint paint = null;

	private void InitPara(Context context, AttributeSet attrs, int defStyle) {
		VideoDisplaySize = VideoDiaplaySize_Flag.ViewFullSreen;

		// Canvas canvas = this.getHolder().lockCanvas();
		// onDraw(canvas);
		// this.getHolder().unlockCanvasAndPost(canvas);
	}

	public MySurfaceView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub

		InitPara(context, null, 0);
	}

	public MySurfaceView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		InitPara(context, attrs, defStyle);
	}

	public MySurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub

		InitPara(context, attrs, 0);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		Log.i(TAG,
				"onMeasure - widthMeasureSpec = "
						+ String.valueOf(widthMeasureSpec));
		Log.i(TAG,
				"onMeasure - heightMeasureSpec = "
						+ String.valueOf(heightMeasureSpec));

		Log.i(TAG, "measure - widthMeasureSpec:");
		VideoMeasure(widthMeasureSpec, WH_Flag.W_Flag);

		Log.i(TAG, "measure - heightMeasureSpec:");
		VideoMeasure(heightMeasureSpec, WH_Flag.H_Flag);

		if (VideoDisplaySize == VideoDiaplaySize_Flag.ViewFullSreen) // 全屏显示
		{

			Log.i(TAG,
					"onMeasure - ViewFullSreenW = "
							+ String.valueOf(ViewFullSreenW));
			Log.i(TAG,
					"onMeasure - ViewFullSreenH = "
							+ String.valueOf(ViewFullSreenH));

			setMeasuredDimension(ViewFullSreenW, ViewFullSreenH);
		} else if (VideoDisplaySize == VideoDiaplaySize_Flag.ViewMax) // 【最大显示】
		{

			Log.i(TAG, "onMeasure - ViewMaxW = " + String.valueOf(ViewMaxW));
			Log.i(TAG, "onMeasure - ViewMaxH = " + String.valueOf(ViewMaxH));

			setMeasuredDimension(ViewMaxW, ViewMaxH);

		} else // 自定义显示
		{

			Log.i(TAG,
					"onMeasure - ViewCustomW = " + String.valueOf(ViewCustomW));
			Log.i(TAG,
					"onMeasure - ViewCustomH = " + String.valueOf(ViewCustomH));

			setMeasuredDimension(ViewCustomW, ViewCustomH);
		}
	}

	private int VideoMeasure(int measureSpec, Enum<WH_Flag> Flag) {

		/*
		 * 一个MeasureSpec封装了父布局传递给子布局的布局要求，每个MeasureSpec代表了一组宽度和高度的要求。
		 * 一个MeasureSpec由大小和模式组成
		 * 。它有三种模式：UNSPECIFIED(未指定),父元素部队自元素施加任何束缚，子元素可以得到任意想要的大小
		 * ；EXACTLY(完全)，父元素决定自元素的确切大小
		 * ，子元素将被限定在给定的边界里而忽略它本身大小；AT_MOST(至多)，子元素至多达到指定大小的值。
		 */
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		switch (specMode) {
		// 全屏显示区域
		case MeasureSpec.AT_MOST:

			Log.i(TAG, "Mode: MeasureSpec.AT_MOST...");

			if (Flag == WH_Flag.W_Flag) {
				ViewFullSreenW = specSize;
			} else if (Flag == WH_Flag.H_Flag) {
				ViewFullSreenH = specSize;
			} else {

			}

			break;

		// 最大可显示区域
		case MeasureSpec.EXACTLY:

			Log.i(TAG, "Mode: MeasureSpec.EXACTLY...");
			if (Flag == WH_Flag.W_Flag) {
				ViewMaxW = specSize;
			} else if (Flag == WH_Flag.H_Flag) {
				ViewMaxH = specSize;
			} else {

			}

			break;

		case MeasureSpec.UNSPECIFIED:
			Log.i(TAG, "Mode: MeasureSpec.UNSPECIFIED...");
			break;

		}

		Log.i(TAG, "measure - specSize = " + String.valueOf(specSize));
		return specSize;

	}

	/**
	 * 
	 * @param MaxSizeFlag
	 *            true:最大化
	 * @param VideoDisplayW
	 *            MaxSizeFlag为false时，以下这两个参数有用
	 * @param VideoDisplayH
	 */
	public void setVideoDisplaySize(boolean MaxSizeFlag, int VideoDisplayW,
			int VideoDisplayH) {
		if (MaxSizeFlag) {

			Log.i(TAG, "VideoDiaplaySize_Flag.ViewMax ---> ");

			VideoDisplaySize = VideoDiaplaySize_Flag.ViewMax;

			ViewMaxW = VideoDisplayW;
			ViewMaxH = VideoDisplayH;

			getHolder().setFixedSize(ViewMaxW, ViewMaxH);

		} else {
			Log.i(TAG, "VideoDiaplaySize_Flag.ViewCustom ---> ");

			VideoDisplaySize = VideoDiaplaySize_Flag.ViewCustom;

			ViewCustomW = VideoDisplayW;
			ViewCustomH = VideoDisplayH;

			getHolder().setFixedSize(ViewCustomW, ViewCustomH);
		}

		Log.i(TAG,
				"setVideoDisplaySize - VideoDisplayW = "
						+ String.valueOf(VideoDisplayW));
		Log.i(TAG,
				"setVideoDisplaySize - VideoDisplayH = "
						+ String.valueOf(VideoDisplayH));

	}

	public void SetVideoDraw(ViewfinderView viewfinderView) {
		// TODO Auto-generated method stub

		viewfinderView.SetDisplayRect(ViewMaxW, ViewMaxH);
		viewfinderView.drawViewfinder();
	}

}
