package com.ql.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.smartlearning.R;

public class TabSwitcher extends FrameLayout{

	private static final String tag="TabSwitcher";
	private Context context;
	private String[] texts;
	private int arrayId;
	private int selectedPosition=0;
	private int oldPosition=selectedPosition;
	private ImageView iv;
	private LinearLayout.LayoutParams params;
	private LinearLayout layout;
	private int iv_width;
	
	public TabSwitcher(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init();
	}
	public TabSwitcher(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init();
		TypedArray a=context.obtainStyledAttributes(attrs,R.styleable.custom);  
		arrayId=a.getResourceId(R.styleable.custom_arrayId, 0);
//		selectedPosition=a.getInt(R.styleable.custom_selectedPosition, 0);
        a.recycle();
	}
	private void init(){
		context=getContext();
		FrameLayout.LayoutParams params=new FrameLayout.LayoutParams(FrameLayout.LayoutParams.FILL_PARENT,FrameLayout.LayoutParams.WRAP_CONTENT);
		setLayoutParams(params);
		setBackgroundResource(R.drawable.tabswitcher_long);
		
	}
	
	@Override
	protected void onFinishInflate() {
		// TODO Auto-generated method stub
		super.onFinishInflate();
		Log.i(tag, "--------------onFinishInflate---------------------");
		if(arrayId!=0){
			texts=getResources().getStringArray(arrayId);
		}else{
			texts=new String[]{};
		}
		
	}

	OnClickListener listener = new OnClickListener(){

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			selectedPosition=(Integer)v.getTag();
			if(selectedPosition!=oldPosition){
				//
				doAnimation();
				oldPosition=selectedPosition;
				if(onItemClickLisener!=null){
					onItemClickLisener.onItemClickLisener(v, selectedPosition);
				}
			}
		}
		
	};
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// TODO Auto-generated method stub
		super.onSizeChanged(w, h, oldw, oldh);
		Log.i("tag", "---------------onSizeChanged--------------------");
		if(selectedPosition>texts.length-1){
			throw new IllegalArgumentException("The selectedPosition can't be > texts.length.");
		}
		layout=new LinearLayout(context);
		params=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,this.getMeasuredHeight());//为了居中显示文字
		params.weight=1;
		params.gravity=Gravity.CENTER_VERTICAL;
		for(int i=0;i<texts.length;i++){
			TextView child=new TextView(context);
			child.setTag(i);
			child.setText(texts[i]);
			child.setTextSize(16);
			child.setTextColor(Color.BLACK);
			child.setGravity(Gravity.CENTER);
			child.setOnClickListener(listener);
			
			layout.addView(child,params);
		}
		
		oldPosition=selectedPosition;
		
		iv_width=this.getMeasuredWidth()/texts.length;//计算ImageView的宽
//		LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(iv_width,LinearLayout.LayoutParams.FILL_PARENT);
		LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(iv_width,this.getMeasuredHeight());
//		p.leftMargin=selectedPosition*iv_width;//无效，因为FrameLayout必须对齐左上角。
		iv=new ImageView(context);
//		iv.setImageResource(R.drawable.tabswitcher_short);
//		iv.setScaleType(ScaleType.FIT_XY);
		iv.setBackgroundResource(R.drawable.tabswitcher_short); 
		this.addView(iv,p);
		this.addView(layout,params);
	}
	
	private void doAnimation(){
		TranslateAnimation animation = new TranslateAnimation(oldPosition*iv_width, selectedPosition*iv_width, 0, 0);  
		animation.setInterpolator(new LinearInterpolator());  
		animation.setDuration(400);  
		animation.setFillAfter(true);  
		iv.startAnimation(animation);  
	}
	
	private OnItemClickLisener onItemClickLisener;
	public void setOnItemClickLisener(OnItemClickLisener onItemClickLisener) {
		this.onItemClickLisener = onItemClickLisener;
	}
	public interface OnItemClickLisener{
		void onItemClickLisener(View view,int position);
	}
	
	public void setTexts(String[] texts) {
		this.texts = texts;
	}
	public void setSelectedPosition(int selectedPosition) {
		this.selectedPosition = selectedPosition;
	}
	
}
