package com.smartlearning.dao;

import java.util.List;

import com.smartlearning.model.TestPaper;
import com.smartlearning.model.TestPaperCategory;
import com.smartlearning.model.TestPaperExtend;
import com.smartlearning.model.TestPaperQuestion;



/**
 * 用户登录接口
 * @author Administrator
 *
 */
public interface ITestPaperService {

	/**
	 * 获得试卷集合
	 * @param page 默认1,  rows 默认10
	 * @return
	 */
	public List<TestPaperExtend> getTestPaperByPage(String serverIP,int page, int rows);
	
	/**
	 * 获得试卷列表
	 * @return
	 */
	public List<TestPaper> getAllTestPaperList(String serverIP, int categoryId, int page, int rows, int classId);
	
	/**
	 * 获得分类列表
	 * @return
	 */
	public List<TestPaperCategory> getAllTestPaperCategoryList(String serverIP);
	
	/**
	 * 获得试卷列表
	 * @return
	 */
	public List<TestPaperQuestion> getAllTestPaperQuestionList(String serverIP,  int testPaperId);
	
	/**
	 * 得到用于分页的数据源的总条数，一般要配合getByPager()方法共同使用 
	 * @param pageSize
	 * @param condtion 1=1 代表无条件
	 * @return
	 */
	public int getByPageCount(int pageSize,String condtion);
	
	public List<TestPaperQuestion> getByPager(int pageIndex,int pageSize,String condition);
	
	/**
	 * 获得所有试卷
	 * @param condtion
	 * @return
	 */
	public List<TestPaperQuestion> getByPagerAll(String condtion);
	
	/**
	 * 获得试卷信息
	 * @return
	 */
	public List<TestPaper> getTestPaperById(int id);
	
	public List<TestPaperQuestion> getQuestionById(String condition);
	
	/**
	 * 获得分类
	 * @return
	 */
	public List<TestPaperCategory> getAllTestPaperCategoryDB();
	
	/**
	 * 本地试卷
	 * @return
	 */
	public List<TestPaper> getAllTestPaperDB(int categoryId);
	
	public boolean isTestPaperQuestionList(String serverIP,  int testPaperId);
	
	/**
	 * 查看答案
	 * @return
	 */
	public TestPaperExtend getTestPaperByAnswer(String serverIP, int testPaperId)throws Exception;
	
	/**
	 * 状态
	 * @param TestPaperQuestion
	 */
	public void modifyPaperQuestion(TestPaperQuestion tpq) throws Exception;
	
	/**
	 * 获得联系人总数
	 */
	public int getCount(String condition);
	
	/**
	 * 通过条件获得试卷
	 * 
	 * @param condition
	 * @return
	 */
	public List<TestPaper> getTestPaperByCondition(String condition);
	
	/**
	 * 获得分组总数
	 */
	public int getCount();
	
	/**
	 * 
	 * @param condition
	 * @return
	 */
	public List<TestPaper> getTestPaperAll();
	
	/**
	 * 状态
	 * @param modifyTestPaper
	 */
	public void modifyTestPaper(int stauts, int testPaperId) throws Exception;
}
