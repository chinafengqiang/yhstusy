package com.smartlearning.dao.impl;

import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.smartlearning.common.HttpUtil;
import com.smartlearning.constant.ServerIP;
import com.smartlearning.dao.ITestPaperService;
import com.smartlearning.db.DB;
import com.smartlearning.db.DB.TABLES.TESTPAPER;
import com.smartlearning.db.DB.TABLES.TESTPAPER_CATEGORY;
import com.smartlearning.db.DB.TABLES.TEST_PAPER_QUESTION;
import com.smartlearning.db.DBHelper;
import com.smartlearning.model.TestPaper;
import com.smartlearning.model.TestPaperCategory;
import com.smartlearning.model.TestPaperExtend;
import com.smartlearning.model.TestPaperQuestion;
import com.smartlearning.model.TestPaperQuestionExtend;
import com.smartlearning.utils.FastJsonTools;
import com.smartlearning.utils.ImageService;
import com.smartlearning.utils.ImageTools;


/**
 * 用户登录接口
 * @author Administrator
 */
public class TestPaperService implements ITestPaperService{
	
	private DBHelper helper = null;
	
	public TestPaperService(Context context) {
		helper = new DBHelper(context);
	}

	/**
	 * 获得试卷集合
	 * @param page 默认1,  rows 默认10
	 * @return
	 */
	public List<TestPaperExtend> getTestPaperByPage(String serverIP,int page, int rows) {
		
		String path = String.format(serverIP + ServerIP.SERVLET_TESTPAPER, page, rows);
		byte[] data = HttpUtil.getDataFromUrl(path);
		String jsonString = new String(data);
		Log.i("TestPaper json", jsonString);
		String obj = "";
		JSONObject item;
		try {
			
			JSONObject jObject = new JSONObject(jsonString);
			String obj1 = jObject.getString("data");
			
			item = new JSONObject(obj1);
			obj = item.getString("list");
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		List<TestPaperExtend> testPapers = FastJsonTools.getObects(obj, TestPaperExtend.class);
		
		return testPapers;
	}
	
	
	/**
	 * 获得试卷集合
	 * @param page 默认1,  rows 默认10
	 * @return
	 */
	private void getTestPaperAll(String serverIP, int last_id, int page, int rows, int classId) {
		
		String path = String.format(serverIP + ServerIP.SERVLET_TESTPAPER, page, rows, classId);
		byte[] data = HttpUtil.getDataFromUrl(path);
		String jsonString = new String(data);
		Log.i("TestPaper json", jsonString);
		String obj = "";
		JSONObject item;
		try {
			
			JSONObject jObject = new JSONObject(jsonString);
			String obj1 = jObject.getString("data");
			
			item = new JSONObject(obj1);
			obj = item.getString("list");
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		List<TestPaperExtend> testPapers = FastJsonTools.getObects(obj, TestPaperExtend.class);
		
		for(TestPaperExtend tpExtend : testPapers){
			TestPaper testPaper = new TestPaper();
			
			testPaper.set_id(Integer.parseInt(tpExtend.getId().toString()));
			testPaper.setName(tpExtend.getName());
			testPaper.setParent_id(Integer.parseInt(tpExtend.getCategory()));
			testPaper.setPassScore(tpExtend.getPassScore().toString());
			testPaper.setTotalScore(tpExtend.getTotalScore().toString());
			testPaper.setClassId(Integer.parseInt(tpExtend.getDescription()));
			testPaper.setStauts(0);
//			if (testPaper.get_id() > last_id) {
//				InsertTestPaper(testPaper);
//			}
			
			//插入
			if(getTestPaperCount(testPaper.get_id()) == 0){
				InsertTestPaper(testPaper);
			}
			
		}
		
	}
	
	/**
	 * 获得分类列表
	 * @return
	 */
	public List<TestPaperCategory> getAllTestPaperCategoryList(String serverIP){
		
		Log.i("TestPaperCategory", "初始化数据开始");

		int last_id = this.getCatetoryLastId();
		getTestPaperCategory(serverIP, last_id);
		Log.i("TestPaperCategory", "初始化数据结束");
		return getAllTestPaperCategoryDB();
	}
	

	/**
	 * 获得试卷列表
	 * @return
	 */
	public List<TestPaper> getAllTestPaperList(String serverIP, int categoryId, int page, int rows, int classId){
		Log.i("TestPaper", "初始化数据开始");
		int last_id = this.getTestPaperLastId();
		getTestPaperAll(serverIP, last_id, page, rows, classId);
		Log.i("TestPaper", "初始化数据结束");
		return getAllTestPaperDB(categoryId);
	}
	
	/**
	 * 获得试卷列表
	 * @return
	 */
	public List<TestPaperQuestion> getAllTestPaperQuestionList(String serverIP,  int testPaperId){
		
		Log.i("TestPaperQuestion", "初始化数据开始");

		int last_id = this.getTestPaperQuestionLastId();
		Log.i("TestPaperQuestion", "last_id="+last_id);
		getTestPaperQuestion(serverIP, last_id, testPaperId);
		Log.i("TestPaperQuestion", "初始化数据结束");
		
		return getAllTestPaperQuestionDB(testPaperId);
	}
	
	public boolean isTestPaperQuestionList(String serverIP,  int testPaperId){
		
		boolean flag = false;
		int last_id = this.getTestPaperQuestionLastId();
		Log.i("TestPaperQuestion", "last_id="+last_id);
		getTestPaperQuestion(serverIP, last_id, testPaperId);
		Log.i("TestPaperQuestion", "初始化数据结束");
		flag = true;
		return flag;
	}
	
	
	/**
	 * 获得试卷集合
	 * @param page 默认1,  rows 默认10
	 * @return
	 */
	private void getTestPaperCategory(String serverIP, int last_id) {
		
		String path = String.format(serverIP + ServerIP.SERVLET_TESTPAPER_CATEGORY);
		byte[] data = HttpUtil.getDataFromUrl(path);
		String jsonString = new String(data);
		Log.i("getTestPaperCategory json", jsonString);
		
		try {
			JSONObject jObject = new JSONObject(jsonString);
			String obj1 = jObject.getString("data");
			String[] str = obj1.split(";");
			if(str.length > 0){
				
				for(int i = 0; i < str.length; i++){
					TestPaperCategory testPaperCategory = new TestPaperCategory();
					testPaperCategory.set_id(i);
					testPaperCategory.setName(str[i]);
					
//					if (testPaperCategory.get_id() > last_id) {
//						this.InsertCategory(testPaperCategory);
//					}
					
					//插入
					if(getTestPaperCategoryCount(testPaperCategory.get_id()) == 0){
						this.InsertCategory(testPaperCategory);
					}
					
				}
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	}
	
	
	/**
	 * 插入通知对象
	 * @param testPaper
	 */
	private void InsertTestPaper(TestPaper testPaper) {

		Log.i("testPaper","testPaper===" );
		
		ContentValues values = new ContentValues();
		values.put(TESTPAPER.FIELDS._ID, testPaper.get_id());
		values.put(TESTPAPER.FIELDS.NAME, testPaper.getName());
		values.put(TESTPAPER.FIELDS.PARENT_ID, testPaper.getParent_id());
		values.put(TESTPAPER.FIELDS.PASS_SCORE, testPaper.getPassScore());
		values.put(TESTPAPER.FIELDS.TOTAL_SCORE, testPaper.getTotalScore());
		values.put(TESTPAPER.FIELDS.STAUTS, testPaper.getStauts());
		
		this.helper.insert(TESTPAPER.TABLENAME, values);
	}
	
	/**
	 * 插入通知对象
	 * @param testPaper
	 */
	private void InsertTestPaperQuestion(TestPaperQuestion testPaperQuestion) {

		Log.i("testPaperQuestion","testPaperQuestion===" );
		
		ContentValues values = new ContentValues();
		values.put(TEST_PAPER_QUESTION.FIELDS._ID, testPaperQuestion.get_id());
		values.put(TEST_PAPER_QUESTION.FIELDS.NAME, testPaperQuestion.getName());
		values.put(TEST_PAPER_QUESTION.FIELDS.TEST_PAPER_ID, testPaperQuestion.getTestPaperId());
		values.put(TEST_PAPER_QUESTION.FIELDS.QUESTION_ID, testPaperQuestion.getQuestionId());
		values.put(TEST_PAPER_QUESTION.FIELDS.QUESTION_TYPE, testPaperQuestion.getQuestionType());
		values.put(TEST_PAPER_QUESTION.FIELDS.OPTIONS, testPaperQuestion.getOptions());
		values.put(TEST_PAPER_QUESTION.FIELDS.ANSWER, testPaperQuestion.getAnswer());
		values.put(TEST_PAPER_QUESTION.FIELDS.KEN, testPaperQuestion.getKen());
		values.put(TEST_PAPER_QUESTION.FIELDS.DIFFICULTY, testPaperQuestion.getDifficulty());
		values.put(TEST_PAPER_QUESTION.FIELDS.SCORE, testPaperQuestion.getScore());
		values.put(TEST_PAPER_QUESTION.FIELDS.NOTE, testPaperQuestion.getNote());
		values.put(TEST_PAPER_QUESTION.FIELDS.favStauts, testPaperQuestion.getFavStauts());
		
		this.helper.insert(TEST_PAPER_QUESTION.TABLENAME, values);
	}
	
	/**
	 * 插入通知对象
	 * @param testPaperCategory
	 */
	private void InsertCategory(TestPaperCategory testPaperCategory) {

		Log.i("testPaperCategory","testPaperCategory===" );
		
		ContentValues values = new ContentValues();
		values.put(TESTPAPER_CATEGORY.FIELDS._ID, testPaperCategory.get_id());
		values.put(TESTPAPER_CATEGORY.FIELDS.NAME, testPaperCategory.getName());
		
		this.helper.insert(TESTPAPER_CATEGORY.TABLENAME, values);
	}
	
	
	/**
	 * 获得分类
	 * @return
	 */
	public List<TestPaperCategory> getAllTestPaperCategoryDB() {
		String condition = " 1=1 order by id";

		String sql = String.format(DB.TABLES.TESTPAPER_CATEGORY.SQL.SELECT, condition);
		List<TestPaperCategory> testPaperCategorys = new ArrayList<TestPaperCategory>();
		
		try {
			Cursor cursor = helper.SELECT(sql);
			while (cursor.moveToNext()) {
				TestPaperCategory testPaperCategory = new TestPaperCategory();
				testPaperCategory.set_id(cursor.getInt(cursor.getColumnIndex(TESTPAPER_CATEGORY.FIELDS._ID)));
				testPaperCategory.setName(cursor.getString(cursor.getColumnIndex(TESTPAPER_CATEGORY.FIELDS.NAME)));
				
				testPaperCategorys.add(testPaperCategory);
			}
			cursor.close();
			return testPaperCategorys;
		} catch (Exception e) {
			return null;
		} finally {
			helper.closeDataBase();
		}
	}
	
	/**
	 * 本地试卷
	 * @return
	 */
	public List<TestPaper> getAllTestPaperDB(int categoryId) {
		String condition = " parent_id = "+ categoryId +" order by id";

		String sql = String.format(DB.TABLES.TESTPAPER.SQL.SELECT, condition);
		
		Log.i("sql", "sql==="+sql);
		
		List<TestPaper> testPapers = new ArrayList<TestPaper>();
		
		try {
			Cursor cursor = helper.SELECT(sql);
			while (cursor.moveToNext()) {
				TestPaper testPaper = new TestPaper();
				testPaper.set_id(cursor.getInt(cursor.getColumnIndex(TESTPAPER.FIELDS._ID)));
				testPaper.setName(cursor.getString(cursor.getColumnIndex(TESTPAPER.FIELDS.NAME)));
				testPaper.setParent_id(cursor.getInt(cursor.getColumnIndex(TESTPAPER.FIELDS.PARENT_ID)));
				testPapers.add(testPaper);
			}
			cursor.close();
			return testPapers;
		} catch (Exception e) {
			return null;
		} finally {
			helper.closeDataBase();
		}
	}
	
	/**
	 * 获得试卷分类最后值
	 * @return
	 */
	public int getCatetoryLastId() {
		String sql = "select _id from testpaper_catetory order by _id desc";
		int last_id = 0;
		try {
			Cursor cursor = helper.SELECT(sql);
			if (cursor.moveToFirst()) {
				last_id = cursor.getInt(cursor.getColumnIndex(TESTPAPER_CATEGORY.FIELDS._ID));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		Log.i("sq", "getLastCatetoryId last_id=" + last_id);
		return last_id;
	}
	
	/**
	 * 获得试卷最后值
	 * @return
	 */
	public int getTestPaperLastId() {
		String sql = "select _id from testpaper order by _id desc";
		int last_id = 0;
		try {
			Cursor cursor = helper.SELECT(sql);
			if (cursor.moveToFirst()) {
				last_id = cursor.getInt(cursor.getColumnIndex(TESTPAPER.FIELDS._ID));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		Log.i("sq", "getTestPaperLastId last_id=" + last_id);
		return last_id;
	}
	
	
	/**
	 * 获得试卷题目集合
	 * @param page 默认1,  rows 默认10
	 * @return
	 */
	private void getTestPaperQuestion(String serverIP, int last_id, int testPaperId) {
		
		String path = String.format(serverIP + ServerIP.SERVLET_TESTPAPER_QUESTION, testPaperId);
		Log.i("TestPaperQuestion", "path=="+path);
		byte[] data = HttpUtil.getDataFromUrl(path);
		String jsonString = new String(data);
		Log.i("TestPaperQuestion", "jsonString=="+jsonString);
		String obj = "";
		JSONObject item;
		try {
			
			JSONObject jObject = new JSONObject(jsonString);
			String obj1 = jObject.getString("data");
			
			item = new JSONObject(obj1);
			obj = item.getString("questionList");
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		List<TestPaperQuestionExtend> testPaperQuestionExtends = FastJsonTools.getObects(obj, TestPaperQuestionExtend.class);
		
		Log.i("TestPaperQuestion", "总数=="+testPaperQuestionExtends.size());
		
		for(TestPaperQuestionExtend tpExtend : testPaperQuestionExtends){
			TestPaperQuestion testPaperQuestion = new TestPaperQuestion();
			
			testPaperQuestion.set_id(Integer.parseInt(tpExtend.getId().toString()));
			testPaperQuestion.setName(tpExtend.getName());
			testPaperQuestion.setTestPaperId(Integer.parseInt(tpExtend.getTestPaperId().toString()));
			testPaperQuestion.setQuestionId(Integer.parseInt(tpExtend.getQuestionId().toString()));
			testPaperQuestion.setQuestionType(Integer.parseInt(tpExtend.getQuestionType().toString()));
			testPaperQuestion.setOptions(tpExtend.getOptions());
			testPaperQuestion.setAnswer(tpExtend.getAnswer());
			testPaperQuestion.setKen(tpExtend.getKen());
			testPaperQuestion.setDifficulty(Integer.parseInt(tpExtend.getDifficulty().toString()));
			testPaperQuestion.setScore(tpExtend.getScore());
			testPaperQuestion.setFavStauts(0);
			
			String imageNote = serverIP + tpExtend.getNote();
			Log.i("TestPaperQuestion", "imageNote="+imageNote);
			byte[] result = null;
			try {
				InputStream is  = ImageService.getInputStream(imageNote);
				Bitmap  bitmap = BitmapFactory.decodeStream(is);
				if(bitmap == null){
					return;
				}
				
				result = ImageTools.getByteFromBitmap(bitmap);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			testPaperQuestion.setNote(result);
			
			//题目内容
			if(testPaperQuestion.getNote() != null){
			
				//插入
//				if (testPaperQuestion.get_id() > last_id) {
//					InsertTestPaperQuestion(testPaperQuestion);
//				}
				
			if(getQuestionCount(testPaperQuestion.get_id()) == 0){
				InsertTestPaperQuestion(testPaperQuestion);
			}
			
			}
		}
	}
	
	
	/**
	 * 本地试卷题目数据
	 * @return
	 */
	public List<TestPaperQuestion> getAllTestPaperQuestionDB(int testPaperId) {
		String condition = " test_paper_id = "+ testPaperId +" order by _id desc";

		String sql = String.format(DB.TABLES.TEST_PAPER_QUESTION.SQL.SELECT, condition);
		
		Log.i("TestPaperQuestion", "sql==="+sql);
		
		List<TestPaperQuestion> testPaperQuestions = new ArrayList<TestPaperQuestion>();
		
		try {
			Cursor cursor = helper.SELECT(sql);
			while (cursor.moveToNext()) {
				TestPaperQuestion testPaperQuestion = new TestPaperQuestion();
				testPaperQuestion.set_id(cursor.getInt(cursor.getColumnIndex(TEST_PAPER_QUESTION.FIELDS._ID)));
				testPaperQuestion.setName(cursor.getString(cursor.getColumnIndex(TEST_PAPER_QUESTION.FIELDS.NAME)));
				testPaperQuestion.setQuestionType(cursor.getInt(cursor.getColumnIndex(TEST_PAPER_QUESTION.FIELDS.QUESTION_TYPE)));
				testPaperQuestion.setNote(cursor.getBlob(cursor.getColumnIndex(TEST_PAPER_QUESTION.FIELDS.NOTE)));
				
				testPaperQuestions.add(testPaperQuestion);
			}
			cursor.close();
			return testPaperQuestions;
		} catch (Exception e) {
			return null;
		} finally {
			helper.closeDataBase();
		}
	}
	
	/**
	 * 本地试卷题目数据
	 * @return
	 */
	public List<TestPaperQuestion> getProductsBySQL(String sql) {

		//String sql = String.format(DB.TABLES.TEST_PAPER_QUESTION.SQL.SELECT, condition);
		Log.i("TestPaperQuestion", "sql==="+sql);
		List<TestPaperQuestion> testPaperQuestions = new ArrayList<TestPaperQuestion>();
		try {
			Cursor cursor = helper.SELECT(sql);
			while (cursor.moveToNext()) {
				TestPaperQuestion testPaperQuestion = new TestPaperQuestion();
				testPaperQuestion.set_id(cursor.getInt(cursor.getColumnIndex(TEST_PAPER_QUESTION.FIELDS._ID)));
				testPaperQuestion.setName(cursor.getString(cursor.getColumnIndex(TEST_PAPER_QUESTION.FIELDS.NAME)));
				testPaperQuestion.setQuestionType(cursor.getInt(cursor.getColumnIndex(TEST_PAPER_QUESTION.FIELDS.QUESTION_TYPE)));
				testPaperQuestion.setNote(cursor.getBlob(cursor.getColumnIndex(TEST_PAPER_QUESTION.FIELDS.NOTE)));
				testPaperQuestion.setKen(cursor.getString(cursor.getColumnIndex(TEST_PAPER_QUESTION.FIELDS.KEN)));
				testPaperQuestion.setScore(cursor.getDouble(cursor.getColumnIndex(TEST_PAPER_QUESTION.FIELDS.SCORE)));
				testPaperQuestion.setAnswer(cursor.getString(cursor.getColumnIndex(TEST_PAPER_QUESTION.FIELDS.ANSWER)));
				testPaperQuestion.setQuestionId(cursor.getInt(cursor.getColumnIndex(TEST_PAPER_QUESTION.FIELDS.QUESTION_ID)));
				
				testPaperQuestions.add(testPaperQuestion);
			}
			cursor.close();
			return testPaperQuestions;
		} catch (Exception e) {
			return null;
		} finally {
			helper.closeDataBase();
		}
	}
	
	/**
	 * 本地试卷题目数据
	 * @return
	 */
	public List<TestPaperQuestion> getProductsMinBySQL(String sql) {

		//String sql = String.format(DB.TABLES.TEST_PAPER_QUESTION.SQL.SELECT, condition);
		Log.i("TestPaperQuestion", "sql==="+sql);
		List<TestPaperQuestion> testPaperQuestions = new ArrayList<TestPaperQuestion>();
		try {
			Cursor cursor = helper.SELECT(sql);
			while (cursor.moveToNext()) {
				TestPaperQuestion testPaperQuestion = new TestPaperQuestion();
				testPaperQuestion.set_id(cursor.getInt(cursor.getColumnIndex(TEST_PAPER_QUESTION.FIELDS._ID)));
				testPaperQuestion.setName(cursor.getString(cursor.getColumnIndex(TEST_PAPER_QUESTION.FIELDS.NAME)));
				testPaperQuestion.setQuestionType(cursor.getInt(cursor.getColumnIndex(TEST_PAPER_QUESTION.FIELDS.QUESTION_TYPE)));
				testPaperQuestion.setScore(cursor.getDouble(cursor.getColumnIndex(TEST_PAPER_QUESTION.FIELDS.SCORE)));
				testPaperQuestion.setAnswer(cursor.getString(cursor.getColumnIndex(TEST_PAPER_QUESTION.FIELDS.ANSWER)));
				testPaperQuestion.setQuestionId(cursor.getInt(cursor.getColumnIndex(TEST_PAPER_QUESTION.FIELDS.QUESTION_ID)));
				
				testPaperQuestions.add(testPaperQuestion);
			}
			cursor.close();
			return testPaperQuestions;
		} catch (Exception e) {
			return null;
		} finally {
			helper.closeDataBase();
		}
	}
	
	
	/**
	 * 获得试卷分类最后值
	 * @return
	 */
	public int getTestPaperQuestionLastId() {
		String sql = "select _id from test_paper_question order by _id desc";
		int last_id = 0;
		try {
			Cursor cursor = helper.SELECT(sql);
			if (cursor.moveToFirst()) {
				last_id = cursor.getInt(cursor.getColumnIndex(TEST_PAPER_QUESTION.FIELDS._ID));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		Log.i("sq", "getTestPaperQuestionLastId last_id=" + last_id);
		return last_id;
	}
	
	/**
	 * 得到用于分页的数据源的总条数，一般要配合getByPager()方法共同使用 
	 * @param pageSize
	 * @param condtion 1=1 代表无条件
	 * @return
	 */
	public int getByPageCount(int pageSize,String condtion){
		String sql_count = MessageFormat.format(DB.TABLES.TEST_PAPER_QUESTION.SQL.SELECT_COUNT,condtion);
		
		int totalCount = (int) helper.rawQuerySingle(sql_count);  //总条数
		int pageCount=1; //总页数
		if (totalCount % pageSize==0){
			pageCount = totalCount/pageSize;
		}else{
			pageCount = (totalCount/pageSize)+1;
		}
		return pageCount;

	}
	
	public int getQuestionCount(int id){
		String condtion = "_id="+id;
		String sql_count = MessageFormat.format(DB.TABLES.TEST_PAPER_QUESTION.SQL.SELECT_COUNT,condtion);
		int totalCount = (int) helper.rawQuerySingle(sql_count);  //总条数
		
		return totalCount;
	}
	
	public int getTestPaperCategoryCount(int id){
		String condtion = "_id="+id;
		String sql_count = MessageFormat.format(DB.TABLES.TESTPAPER_CATEGORY.SQL.SELECT_COUNT,condtion);
		int totalCount = (int) helper.rawQuerySingle(sql_count);  //总条数
		
		return totalCount;
	}
	
	public int getTestPaperCount(int id){
		String condtion = "_id="+id;
		String sql_count = MessageFormat.format(DB.TABLES.TESTPAPER.SQL.SELECT_COUNT,condtion);
		int totalCount = (int) helper.rawQuerySingle(sql_count);  //总条数
		
		return totalCount;
	}
	
	/**
	 * 分页存取数据
	 * params:
	 * condtion: 如果无条件 condition 传入 1=1
	 * pageIndex:代表是第几页(首页从0开始)
	 * pageSize:代表每页多少条记录
	 * return:返回的就是分页之后的数据
	 */
	
	public List<TestPaperQuestion> getByPager(int pageIndex, int pageSize,String condtion) {
		
		int pageCount = this.getByPageCount(pageSize,condtion);
		
		if (pageIndex>pageCount-1)//说明是最后一页
		{
			return null;
		}
		
		int startIndex = pageIndex*pageSize;
		String condition = condtion+ " limit "+startIndex+","+pageSize;
		
		String sql = MessageFormat.format(DB.TABLES.TEST_PAPER_QUESTION.SQL.SELECT, condition);
		
		Log.i("TestPaperQuestion", "sql=="+sql);
		
		return this.getProductsBySQL(sql);
		
	}
	
	/**
	 * 获得所有试卷
	 * @param condtion
	 * @return
	 */
	public List<TestPaperQuestion> getByPagerAll(String condtion) {
		
		String sql = MessageFormat.format(DB.TABLES.TEST_PAPER_QUESTION.SQL.SELECT, condtion);
		
		Log.i("TestPaperQuestion", "sql=="+sql);
		
		return this.getProductsMinBySQL(sql);
		
	}
	
	
	/**
	 * 获得试卷信息
	 * @return
	 */
	public List<TestPaper> getTestPaperById(int id) {
		String condition = " _id = "+ id +" order by id";

		String sql = String.format(DB.TABLES.TESTPAPER.SQL.SELECT, condition);
		
		Log.i("sql", "sql==="+sql);
		
		List<TestPaper> testPapers = new ArrayList<TestPaper>();
		
		try {
			Cursor cursor = helper.SELECT(sql);
			while (cursor.moveToNext()) {
				TestPaper testPaper = new TestPaper();
				testPaper.set_id(cursor.getInt(cursor.getColumnIndex(TESTPAPER.FIELDS._ID)));
				testPaper.setName(cursor.getString(cursor.getColumnIndex(TESTPAPER.FIELDS.NAME)));
				testPaper.setParent_id(cursor.getInt(cursor.getColumnIndex(TESTPAPER.FIELDS.PARENT_ID)));
				testPaper.setPassScore(cursor.getString(cursor.getColumnIndex(TESTPAPER.FIELDS.PASS_SCORE)));
				testPaper.setTotalScore(cursor.getString(cursor.getColumnIndex(TESTPAPER.FIELDS.TOTAL_SCORE)));
				testPapers.add(testPaper);
			}
			cursor.close();
			return testPapers;
		} catch (Exception e) {
			return null;
		} finally {
			helper.closeDataBase();
		}
	}
	
	public List<TestPaperQuestion> getQuestionById(String condition) {
		
		String sql = MessageFormat.format(DB.TABLES.TEST_PAPER_QUESTION.SQL.SELECT, condition);
		
		Log.i("TestPaperQuestion", "sql=="+sql);
		
		return this.getProductsBySQL(sql);
		
	}
	
	
	/**
	 * 状态
	 * @param TestPaperQuestion
	 */
	public void modifyPaperQuestion(TestPaperQuestion tpq) throws Exception{
		String sql = String.format(DB.TABLES.TEST_PAPER_QUESTION.SQL.UPDATE, tpq.getFavStauts(), tpq.getQuestionId(), tpq.getTestPaperId());
		Log.i("TestPaper json", sql);
		helper.ExecuteSQL(sql);
	}
	
	/**
	 * 查看答案
	 * @return
	 */
	public TestPaperExtend getTestPaperByAnswer(String serverIP, int testPaperId) throws Exception {
		
		String path = String.format(serverIP + ServerIP.SERVLET_VIEW_ANSWER, testPaperId);
		byte[] data = HttpUtil.getDataFromUrl(path);
		String jsonString = new String(data);
		if(jsonString == null || jsonString.equals("")){
			throw new Exception();
		}
		Log.i("TestPaper json", jsonString);
		String obj = "";
		JSONObject item;
		try {
			
			JSONObject jObject = new JSONObject(jsonString);
			obj = jObject.getString("data");
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		TestPaperExtend tpe = FastJsonTools.getObect(obj, TestPaperExtend.class);
		
		return tpe;
	}
	
	/**
	 * 获得联系人总数
	 */
	public int getCount(String condition) {
		try {
			
			String sql_count = String.format(TESTPAPER.SQL.COUNT, condition);
			Log.i("TestPaper", "sql_count=="+sql_count);
			
			Cursor cursor = helper.SELECT(sql_count);
			System.out.println(sql_count);
			int totalCount = -1;
			if (cursor.moveToNext()) {
				totalCount = (int) cursor.getDouble(0);
			}
			cursor.close();
			return totalCount;
		} finally {
			helper.closeDataBase();
		}

	}
	
	/**
	 * 通过条件获得试卷
	 * 
	 * @param condition
	 * @return
	 */
	public List<TestPaper> getTestPaperByCondition(String condition) {
		
		try {
			String sql = String.format(TESTPAPER.SQL.SELECT,  condition );
			Log.i("TestPaper", "TestPapersql=="+sql);
			return this.selectExtend(sql);
		} finally {
			helper.closeDataBase();
		}
	}
	
	public List<TestPaper> selectExtend(String sql) throws SQLException {
		List<TestPaper> list_contact = new ArrayList<TestPaper>();

		try {
			Cursor cursor = helper.SELECT(sql);

			while (cursor.moveToNext()) {

				TestPaper testPaper = new TestPaper();
				testPaper.set_id(cursor.getInt(cursor.getColumnIndex(TESTPAPER.FIELDS._ID)));
				testPaper.setName(cursor.getString(cursor.getColumnIndex(TESTPAPER.FIELDS.NAME)));
				testPaper.setParent_id(cursor.getInt(cursor.getColumnIndex(TESTPAPER.FIELDS.PARENT_ID)));
				testPaper.setPassScore(cursor.getString(cursor.getColumnIndex(TESTPAPER.FIELDS.PASS_SCORE)));
				testPaper.setTotalScore(cursor.getString(cursor.getColumnIndex(TESTPAPER.FIELDS.TOTAL_SCORE)));
				testPaper.setStauts(cursor.getInt(cursor.getColumnIndex(TESTPAPER.FIELDS.STAUTS)));

				list_contact.add(testPaper);
			}
			cursor.close();

			return list_contact;
		} catch (Exception e) {
			return null;
		} finally {
			helper.closeDataBase();
		}

	}
	
	/**
	 * 获得分组总数
	 */
	public int getCount() {
		try {
			String sql_count = TESTPAPER_CATEGORY.SQL.COUNT;
			Cursor cursor = helper.SELECT(sql_count);
			int totalCount = -1;
			if (cursor.moveToNext()) {
				totalCount = (int) cursor.getDouble(0);

			}
			cursor.close();
			return totalCount;
		} finally {
			helper.closeDataBase();
		}

	}
	
	/**
	 * 
	 * @param condition
	 * @return
	 */
	public List<TestPaper> getTestPaperAll() {
		
		try {
			String sql = String.format(TESTPAPER.SQL.SELECT,  "1=1" );
		
			return this.selectExtend(sql);
		} finally {
			helper.closeDataBase();
		}
	}
	
	/**
	 * 状态
	 * @param modifyTestPaper
	 */
	public void modifyTestPaper(int stauts, int testPaperId) throws Exception{
		String sql = String.format(DB.TABLES.TESTPAPER.SQL.UPDATE, stauts, testPaperId);
		Log.i("modifyTestPaper json", sql);
		helper.ExecuteSQL(sql);
	}
	
}
