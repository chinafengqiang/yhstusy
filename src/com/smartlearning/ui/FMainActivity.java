package com.smartlearning.ui;

import butterknife.ButterKnife;
import butterknife.InjectView;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import com.feng.adapter.ViewPagerAdapter;
import com.feng.myinterface.HomeClickListener;
import com.feng.util.BitmapUtils;
import com.feng.util.HomeButton;
import com.smartlearning.R;
import com.smartlearning.utils.SpUtil;

public class FMainActivity extends Activity implements OnClickListener,OnPageChangeListener{

	private Context mContext;
	
	private SharedPreferences sp;
	
	private String userId = "";
	
	@InjectView(R.id.viewpager) ViewPager vp;
	
	private ViewPagerAdapter vpAdapter;
	private List<View> views;
	private int width;
	private int height;
	
	@InjectView(R.id.relative) RelativeLayout relativeLayout;
	
	// 引导图片资源
	private static final int[] pics = { R.drawable.home2, R.drawable.home3,
			R.drawable.home2, R.drawable.home3 };

	// 底部小店图片
	private ImageView[] dots;

	// 记录当前选中位置
	private int currentIndex;
	private View view;
	
	@InjectView(R.id.info) HomeButton infoBtn;
	@InjectView(R.id.jxzl) HomeButton jxzlBtn;
	@InjectView(R.id.ktzy) HomeButton ktzyBtn;
	@InjectView(R.id.xxda) HomeButton xxdaBtn;
	@InjectView(R.id.zycs) HomeButton zycsBtn;
	//@InjectView(R.id.userSetting) HomeButton userSettingBtn;
	@InjectView(R.id.othermore) HomeButton othermoreBtn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.f_index);
		
		mContext = this;
		
		ButterKnife.inject(this);
		
		initLogin();
		
		setListener();
		
		init();
	}
	
	private void initLogin(){
		sp = SpUtil.getSharePerference(mContext);
		userId = sp.getString("user","0");
		if(userId.equals("0")){
			Intent intent = new Intent(this,LoginActivity.class);
			startActivity(intent);
			finish();
		}
	}

	private void setListener(){
		infoBtn.setOnHomeClick(new HomeClickListener() {
			@Override
			public void onclick() {
				Toast.makeText(mContext,"click",Toast.LENGTH_LONG).show();
			}
		});
	}
	
	
	private void init(){

		//等到屏幕的大小
	
		 width	= getWindowManager().getDefaultDisplay().getWidth();
		 //以670*240的图片为例,正常中不要这样用
		 height	=width*240/670;
		 
		 LinearLayout.LayoutParams layoutParams	= (LinearLayout.LayoutParams) relativeLayout.getLayoutParams();
		 layoutParams.width			=width;
		 layoutParams.height		=height;
		 relativeLayout.setLayoutParams(layoutParams);
		 
		views = new ArrayList<View>();

		LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);

		// 初始化引导图片列表
		for (int i = 0; i < pics.length; i++) {
			ImageView iv = new ImageView(mContext);
			iv.setLayoutParams(mParams);
			//改变大小
			//iv.setImageResource(pics[i]);
			iv.setImageBitmap(BitmapUtils.zoomImage(BitmapFactory.decodeResource(getResources(), pics[i]), width, height));
			views.add(iv);
		}
		
		RelativeLayout.LayoutParams params		= new RelativeLayout.LayoutParams(width, height);
		params.leftMargin=6;
		params.topMargin=2;
		params.rightMargin=6;
		vp.setLayoutParams(params);
		
		// 初始化Adapter
		vpAdapter = new ViewPagerAdapter(views);
		vp.setAdapter(vpAdapter);
		// 绑定回调
		vp.setOnPageChangeListener(this);
		
		initDots();
	}
	

	private void initDots() {
		LinearLayout ll = (LinearLayout)findViewById(R.id.ll);
		dots = new ImageView[pics.length];

		// 循环取得小点图片
		for (int i = 0; i < pics.length; i++) {
			dots[i] = (ImageView) ll.getChildAt(i);
			dots[i].setEnabled(true);// 都设为灰色
			dots[i].setOnClickListener(this);
			dots[i].setTag(i);// 设置位置tag，方便取出与当前位置对应
		}

		currentIndex = 0;
		dots[currentIndex].setEnabled(false);// 设置为白色，即选中状态
	}

	/**
	 * 设置当前的引导页
	 */
	private void setCurView(int position) {
		if (position < 0 || position >= pics.length) {
			return;
		}

		vp.setCurrentItem(position);
	}

	/**
	 * 这只当前引导小点的选中
	 */
	private void setCurDot(int positon) {
		if (positon < 0 || positon > pics.length - 1 || currentIndex == positon) {
			return;
		}

		dots[positon].setEnabled(false);
		dots[currentIndex].setEnabled(true);

		currentIndex = positon;
	}

	// 当滑动状态改变时调用
	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	// 当当前页面被滑动时调用
	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

		vp.requestDisallowInterceptTouchEvent(true);
	}


	// 当新的页面被选中时调用
	@Override
	public void onPageSelected(int arg0) {
		// 设置底部小点选中状态
		setCurDot(arg0);
	}

	@Override
	public void onClick(View view) {
		int position = (Integer) view.getTag();
		setCurView(position);
		setCurDot(position);
	}

	
}
