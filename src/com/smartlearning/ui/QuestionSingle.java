package com.smartlearning.ui;

import java.util.List;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.ParseException;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.smartlearning.R;
import com.smartlearning.biz.UserTestPaperManager;
import com.smartlearning.factory.TestPaperFactory;
import com.smartlearning.model.QuestionTypeEnum;
import com.smartlearning.model.TestPaperQuestion;
import com.smartlearning.model.UserTestPaper;
import com.smartlearning.utils.ImageTools;

public class QuestionSingle extends Activity {
	
	private TestPaperFactory testPaperFactory = null;
	Button myFavor;
	TestPaperQuestion testPaperQuestion = null;
	int testPaperId = 0;
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.question_single);
		myFavor = (Button) findViewById(R.id.myFavor);
		int subjectLocation = getIntent().getIntExtra("location", 0);
		if (getIntent().getIntExtra("testPaperId", 0)!=0)
			testPaperId = getIntent().getIntExtra("testPaperId", 0);
		try {
			updateSingle(subjectLocation);
		} catch (ParseException e) {
			Toast.makeText(this,getResources().getString(R.string.parseError),Toast.LENGTH_SHORT).show();
		}
		
		myFavor.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				
				if (testPaperQuestion.getFavStauts() == 0){
					Drawable f2 = getResources().getDrawable(R.drawable.icon_favor2);
					myFavor.setCompoundDrawablesWithIntrinsicBounds(null, f2, null, null);
					myFavor.setText("取消收藏");
					TestPaperQuestion tpq = new TestPaperQuestion();
				//	tpq.set_id(testPaperQuestion.get_id());
					tpq.setQuestionId(testPaperQuestion.getQuestionId());
					Log.i("eeeeeeeeeeeeeeeeeeee", "testPaperQuestiontestPaperQuestion="+testPaperQuestion.getQuestionId());
					tpq.setFavStauts(1);
					tpq.setTestPaperId(testPaperId);
					testPaperQuestion.setFavStauts(1);
					
					try {
						modifyTestPaperList(tpq);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}else{
					Drawable f1 = getResources().getDrawable(R.drawable.icon_favor1);
					myFavor.setCompoundDrawablesWithIntrinsicBounds(null, f1, null, null);
					myFavor.setText("收藏此题");
					TestPaperQuestion tpq = new TestPaperQuestion();
					tpq.setQuestionId(testPaperQuestion.getQuestionId());
					Log.i("eeeeeeeeeeeeeeeeeee1111e", "testPaperQuestiontestPaperQuestion="+testPaperQuestion.getQuestionId());
					//tpq.set_id(testPaperQuestion.get_id());
					tpq.setFavStauts(0);
					tpq.setTestPaperId(testPaperId);
					testPaperQuestion.setFavStauts(0);
					try {
						modifyTestPaperList(tpq);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
	}
	
	
	
	//创建新线程从本地试卷列表
	public void modifyTestPaperList(final TestPaperQuestion  tpq) {

		Thread thread = new Thread() {
			@Override
			public void run() {
				try {
					testPaperFactory.createTestPaper(QuestionSingle.this).modifyPaperQuestion(tpq);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		
		thread.start();
		thread = null;

	}
	
	/**
	 * 更新
	 * @throws ParseException 
	 */
	private void updateSingle(int location) throws ParseException{
		
		String condition = " question_id=" +location;
		List<TestPaperQuestion> TqpList = testPaperFactory.createTestPaper(QuestionSingle.this).getQuestionById(condition);
		
		if(TqpList.size() > 0){
			testPaperQuestion = TqpList.get(0);
		}
		
		if (testPaperQuestion.getFavStauts() == 1){
			Drawable f2 = getResources().getDrawable(R.drawable.icon_favor2);
			myFavor.setCompoundDrawablesWithIntrinsicBounds(null, f2, null, null);
			myFavor.setText("取消收藏");
		}else{
			Drawable f1 = getResources().getDrawable(R.drawable.icon_favor1);
			myFavor.setCompoundDrawablesWithIntrinsicBounds(null, f1, null, null);
			myFavor.setText("收藏此题");
		}
		final TextView singleIndexView = (TextView)findViewById(R.id.singleIndex);
		final ImageView singleContentView = (ImageView)findViewById(R.id.singleContent);
		final LinearLayout singleAnswersLayout = (LinearLayout)findViewById(R.id.singleAnswerLayout);
		final TextView singleAnswerAnalysis = (TextView)findViewById(R.id.singleAnswerAnalysis);
		
		singleIndexView.setText(String.format(getResources().getString(R.string.subjectIndex), testPaperQuestion.getName()));
		
		byte[] result = testPaperQuestion.getNote();
		Bitmap bitmap = ImageTools.getBitmapFromByte(result);
		singleContentView.setImageBitmap(bitmap);
		
		RadioGroup rg = new RadioGroup(this);
		String[] str = {"A","B","C","D"};
		for(int i=0; i<str.length; i++){
			final RadioButton rb = new RadioButton(this);
			rb.setButtonDrawable(R.drawable.true_uncheck);
			rb.setText(str[i]);
			rb.setPadding(5, 0, 40, 0);
			rb.setTextSize(18);
			rb.setClickable(false);
			if(testPaperQuestion.getQuestionType() == QuestionTypeEnum.SingleSelect.toValue()){
				if(testPaperQuestion.getAnswer().equals(str[i])){
					rb.setTextColor(R.color.correctAnswerColor);
					rb.setButtonDrawable(R.drawable.true_check);
				}
				rg.setOrientation(LinearLayout.HORIZONTAL);
				rg.addView(rb);
			} else if(testPaperQuestion.getQuestionType() == QuestionTypeEnum.MultiSelect.toValue()){
				char[] cAnswer = testPaperQuestion.getAnswer().toCharArray();
				for(int j =0; j < cAnswer.length; j++){
					String uAnswer = String.valueOf(cAnswer[j]);
					
					if(str[i].equals(uAnswer)){
						rb.setTextColor(R.color.correctAnswerColor);
						rb.setButtonDrawable(R.drawable.true_check);
					}
				}
				
				rg.setOrientation(LinearLayout.HORIZONTAL);
				rg.addView(rb);
			}
		}
		if(testPaperQuestion.getQuestionType() == QuestionTypeEnum.Judge.toValue()){
				final RadioButton rb = new RadioButton(this);
				rb.setButtonDrawable(R.drawable.true_uncheck);
				rb.setPadding(5, 0, 40, 0);
				rb.setTextSize(18);
				rb.setClickable(false);
				String uAnswer = testPaperQuestion.getAnswer();
				if("0".equals(uAnswer)){
					rb.setText("错");
					rb.setTextColor(R.color.correctAnswerColor);
					rb.setButtonDrawable(R.drawable.true_check);
				} else if("1".equals(uAnswer)){
					rb.setText("对");
					rb.setTextColor(R.color.correctAnswerColor);
					rb.setButtonDrawable(R.drawable.true_check);
				}
				rg.setOrientation(LinearLayout.HORIZONTAL);
				rg.addView(rb);
			}
		singleAnswersLayout.addView(rg);
	}
	
}