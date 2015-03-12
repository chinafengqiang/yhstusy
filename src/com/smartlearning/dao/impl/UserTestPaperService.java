package com.smartlearning.dao.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.smartlearning.common.HttpUtil;
import com.smartlearning.constant.ServerIP;
import com.smartlearning.dao.IUserTestPaperService;
import com.smartlearning.db.DB;
import com.smartlearning.db.DB.TABLES.USER_TEST_PAPER;
import com.smartlearning.db.DBHelper;
import com.smartlearning.model.Advise;
import com.smartlearning.model.UserTestPaper;


/**
 * 试卷方法
 * @author Administrator
 */
public class UserTestPaperService implements IUserTestPaperService{
	
	private DBHelper helper = null;
	
	public UserTestPaperService(Context context) {
		helper = new DBHelper(context);
	}

	/**
	 * 保存试卷信息
	 * @param testPaper
	 */
	public void InsertUserTestPaper(UserTestPaper userTestPaper) {

		ContentValues values = new ContentValues();
		values.put(USER_TEST_PAPER.FIELDS.USER_ID, userTestPaper.getUserId());
		values.put(USER_TEST_PAPER.FIELDS.QUESTION_ID, userTestPaper.getQuestionId());
		values.put(USER_TEST_PAPER.FIELDS.TIME, userTestPaper.getTime());
		values.put(USER_TEST_PAPER.FIELDS.SCORE, userTestPaper.getScore());
		values.put(USER_TEST_PAPER.FIELDS.IS_CORRECT, userTestPaper.getIsCorrect());
		values.put(USER_TEST_PAPER.FIELDS.TEST_PAPER_ID, userTestPaper.getTestPaperId());
		values.put(USER_TEST_PAPER.FIELDS.NO_SELECT_ANSWER, userTestPaper.getNoSelectAnswer());
		
		this.helper.insert(USER_TEST_PAPER.TABLENAME, values);
	}
	
	/**
	 * 获得分数
	 * @return
	 */
	public double getSumScore(int testPaperId) {
		String sql = "Select sum(score) From user_test_paper where test_paper_id = "+testPaperId;
		double result = 0;
		try {
			result = helper.rawQuerySingle(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Log.i("TestPaperQuestion", "getSumScore result=" + result);
		return result;
	}
	
	
	/**
	 * 获得是否存在用户记录数据
	 * @return
	 */
	public boolean profileExist(int testPaperId) {
		List<UserTestPaper> userTestPapers = getUserTestPaperById(testPaperId);
		if(userTestPapers.size() > 0){
			return true;
		}
		return false;
	}

	public List<UserTestPaper> getUserTestPaperById(int testPaperId) {
		String condition = " test_paper_id = "+ testPaperId +" order by id";

		String sql = String.format(DB.TABLES.USER_TEST_PAPER.SQL.SELECT, condition);
		
		Log.i("TestPaperQuestion", "sql888 condition==="+sql);
		
		List<UserTestPaper> userTestPapers = new ArrayList<UserTestPaper>();
		
		try {
			Cursor cursor = helper.SELECT(sql);
			while (cursor.moveToNext()) {
				UserTestPaper userTestPaper = new UserTestPaper();
				userTestPaper.setId(cursor.getInt(cursor.getColumnIndex(USER_TEST_PAPER.FIELDS.ID)));
				userTestPaper.setUserId(cursor.getInt(cursor.getColumnIndex(USER_TEST_PAPER.FIELDS.USER_ID)));
				userTestPaper.setTime(cursor.getString(cursor.getColumnIndex(USER_TEST_PAPER.FIELDS.TIME)));
				userTestPaper.setScore(cursor.getString(cursor.getColumnIndex(USER_TEST_PAPER.FIELDS.SCORE)));
				userTestPaper.setNoSelectAnswer(cursor.getString(cursor.getColumnIndex(USER_TEST_PAPER.FIELDS.NO_SELECT_ANSWER)));
				userTestPaper.setIsCorrect(cursor.getString(cursor.getColumnIndex(USER_TEST_PAPER.FIELDS.IS_CORRECT)));
				userTestPaper.setQuestionId(cursor.getInt(cursor.getColumnIndex(USER_TEST_PAPER.FIELDS.QUESTION_ID)));
				userTestPaper.setTestPaperId(cursor.getInt(cursor.getColumnIndex(USER_TEST_PAPER.FIELDS.TEST_PAPER_ID)));
				
				userTestPapers.add(userTestPaper);
			}
			
			cursor.close();
			return userTestPapers;
		} catch (Exception e) {
			return null;
		} finally {
			helper.closeDataBase();
		}
	}
	
	/**
	 * 取得考卷对错矩阵
	 * 
	 * @param id
	 * @return
	 */
	public  boolean[] getRwMatrix(int testPaperId) {
	
		List<UserTestPaper> userTestPapers = getUserTestPaperById(testPaperId);
		if (userTestPapers != null) {
			
			boolean[] rwMatrix = new boolean[userTestPapers.size()];
			for(int i = 0; i < userTestPapers.size(); i++){
				UserTestPaper userTestPaper = userTestPapers.get(i);
				if(userTestPaper.getIsCorrect().equals("R")){
					rwMatrix[i] = true;
				} else {
					rwMatrix[i] = false;
				}
			}
			return rwMatrix;
		}
		return null;
	}
	
	/**
	 * 创建用户试卷记录
	 * @param userTestPaper
	 * @throws Exception 
	 */
	public boolean saveUserTestPaper(String serverIP, UserTestPaper userTestPaper) throws Exception{
		String path = serverIP + ServerIP.SERVLET_USER_TESETPAPER;
		String success = "";
		String params = "userId="+userTestPaper.getUserId()+"&questionId="+userTestPaper.getQuestionId() +
				"&score="+userTestPaper.getScore() + "&isCorrect="+userTestPaper.getIsCorrect() +
				"&testPaperId="+userTestPaper.getTestPaperId()+"&converSelectAnswer="+userTestPaper.getNoSelectAnswer();
		
		byte[] data = HttpUtil.getDataFromUrl(path,params);
		String jsonString = new String(data);
		if(jsonString == null || jsonString.equals("")){
			throw new Exception();
		}
		Log.i("learning json==", jsonString);
		
		try {
			JSONObject item = new JSONObject(jsonString);
			success = item.getString("success");
			
			if("true".equals(success)){
				return true; 
			} else {
				throw new Exception("添加错误！"); 
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return false;
		
	}
	
}
