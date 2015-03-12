package com.smartlearning.ui;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract.Profile;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.smartlearning.R;
import com.smartlearning.factory.TestPaperFactory;
import com.smartlearning.factory.UTestPaperFactory;
import com.smartlearning.model.TestPaper;
import com.smartlearning.model.UserTestPaper;

public class UserTestPaperResult extends Activity {
	private int testPaperId = 0;
	private TestPaperFactory testPaperFactory = null;
	private TestPaper testPaper = null;
	
	/**
	 * 对错矩阵的最大行数列数
	 */
	private final int rowMax = 10;
	private final int colMax = 10;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test_paper_result);
		if (getIntent().getIntExtra("categoryId", 0)!=0)
			testPaperId = getIntent().getIntExtra("categoryId", 0);
		fillHeader();
		fillMatrix();
		updateScoreRand();
	}
	
	/**
	 * 填充考卷信息头
	 */
	private void fillHeader(){
		
		List<TestPaper> testPaperList = testPaperFactory.createTestPaper(UserTestPaperResult.this).getTestPaperById(testPaperId);
		
		if(testPaperList.size() > 0){
			testPaper = testPaperList.get(0);
		} 
		
		final TextView title = (TextView)findViewById(R.id.profileTitle);
		final TextView info = (TextView)findViewById(R.id.profileInfo);
		title.setText(String.format(getResources().getString(R.string.profileTitle),testPaper.getName()));
		
//		final int minute = (int)1000/60;
//		final int second = (int)13000%60;
//		final String MIN = minute < 10 ? "0"+minute : ""+minute;
//		final String SEC = second < 10 ? "0"+second : ""+second;
//		
//		info.setText(String.format(getResources().getString(R.string.profileInfo),
//				Integer.parseInt(testPaper.getPassScore()),
//				Integer.parseInt(testPaper.getTotalScore()),
//				MIN,SEC));
	}
	
	/**
	 * 更新得分勋章表
	 */
	private void updateScoreRand(){
		final TextView scroeView = (TextView)findViewById(R.id.scoreView);
		final ProgressBar sb = (ProgressBar)findViewById(R.id.scoreProgress);
		final double score = UTestPaperFactory.createUTestPaper(UserTestPaperResult.this).getSumScore(testPaperId);
		sb.setProgress(updateProgress((int)score, Integer.parseInt(testPaper.getTotalScore())));
		scroeView.setText(""+score);
		int rCount = 0;
		int wCount = 0;
		boolean[] rwMatrix = UTestPaperFactory.createUTestPaper(UserTestPaperResult.this).getRwMatrix(testPaperId);
		for(boolean b : rwMatrix){
			if(b) rCount++;
			else wCount++;
		}
		final TextView rView = (TextView)findViewById(R.id.scoreRightCount);
		final TextView wView = (TextView)findViewById(R.id.scoreWrongCount);
		rView.setText(""+rCount);
		wView.setText(""+wCount);
		
		final ProgressBar prb = (ProgressBar)findViewById(R.id.profileRightProgress);
		final ProgressBar pwb = (ProgressBar)findViewById(R.id.profileWrongProgress);
		final int totalCount = rwMatrix.length;
		prb.setProgress(updateProgress(rCount,totalCount));
		pwb.setProgress(updateProgress(wCount,totalCount));
	}
	
	/**
	 * 更新对错进度
	 * @param cur
	 * @param max
	 * @return
	 */
	private int updateProgress(int cur,int max){
		return (Math.round((float)cur/max*100));
	}
	
	/**
	 * 填充对错矩阵
	 */
	private void fillMatrix(){
		final LinearLayout matrix = (LinearLayout)findViewById(R.id.scoreMatrix);
		final LinearLayout.LayoutParams lpFW = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		final LinearLayout.LayoutParams lpWW = new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		for(int row = 0 ; row < rowMax ; row++){
			final LinearLayout ll = new LinearLayout(this);
			ll.setOrientation(LinearLayout.HORIZONTAL);
			final boolean[] rwMatrix = UTestPaperFactory.createUTestPaper(UserTestPaperResult.this).getRwMatrix(testPaperId);
			final List<UserTestPaper> uList = UTestPaperFactory.createUTestPaper(UserTestPaperResult.this).getUserTestPaperById(testPaperId);
			
			for(int col=0;col<colMax;col++){
				final int index = row * colMax + col;
				final ImageView item = new ImageView(this);
				item.setPadding(10, 10, 10, 10);
				//item.setTag(index);
				if(index < rwMatrix.length){
					item.setTag(uList.get(index).getQuestionId());
					item.setImageResource( rwMatrix[index] ? R.drawable.table_yes : R.drawable.table_no );
				}else{
					item.setImageResource(R.drawable.table_nop_m);
					item.setTag(-1);
				}
				item.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						final int position = (Integer)v.getTag();
						if( position != -1){
							final Intent intent = new Intent(UserTestPaperResult.this,QuestionSingle.class);
							intent.putExtra("location", position);
							intent.putExtra("testPaperId", testPaperId);
							startActivity(intent);
						}else{
							final String tip = getResources().getString(R.string.profileNotSubject);
							Toast.makeText(UserTestPaperResult.this, tip, Toast.LENGTH_SHORT).show();
						}
					}
				});
				ll.addView(item,lpWW);
			}
			matrix.addView(ll,lpFW);
		}
	}
}
