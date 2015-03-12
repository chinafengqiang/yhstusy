package com.smartlearning.dao;

import java.util.List;

import com.smartlearning.model.Adviod;
import com.smartlearning.model.CoursePlan;
import com.smartlearning.model.CoursewareNode;
import com.smartlearning.model.EVideo;
import com.smartlearning.model.Video;

/**
 * 视频接口
 * @author Administrator
 */
public interface IAdviod {

	/**
	 * 获得视频集合
	 * @param page 默认1,  rows 默认10
	 * @return
	 */
	public List<Adviod> geAdviodByPage(String serverIP,int page, int rows);
	
//	/**
//	 * 获得视频集合
//	 * @param 
//	 * @return
//	 */
//	public List<Video> getVideosByAll(String serverIP, int classId);
	
	/**
	 * 获得电子书
	 * @param serverIP服务地址
	 * @return
	 */
	public List<EVideo> getAllEVideoList(String serverIP, int classId) throws Exception;
	
	/**
	 * 获得视频编号
	 * @param 
	 * @return
	 */
	public Video getVideosById(String serverIP,int id);
	
	/**
	 * 获得课程教学计划
	 * @param 
	 * @return
	 */
	public List<CoursePlan> getPlanByAll(String serverIP);
	
	public List<CoursePlan> getPlanByAll(String serverIP,long gradeId);
	
	/**
	 * 获得课程分类
	 * @param 
	 * @return
	 * @throws Exception 
	 */
	public List<CoursewareNode> getCategoryByAll(String serverIP);
	
	/**
	 * 本地视频
	 * @return
	 */
	public List<EVideo> getAllEVideoDB();
	
	/**
	 * 得到用于分页的数据源的总条数，一般要配合getByPager()方法共同使用 
	 * @param pageSize
	 * @param condtion 1=1 代表无条件
	 * @return
	 */
	public int getByPageCount(int pageSize,String condtion);
	
	/**
	 * 分页存取数据
	 * params:
	 * condtion: 如果无条件 condition 传入 1=1
	 * pageIndex:代表是第几页(首页从0开始)
	 * pageSize:代表每页多少条记录
	 * return:返回的就是分页之后的数据
	 */
	
	public List<EVideo> getByPager(int pageIndex, int pageSize,String condtion);
	
	/**
	 * 删除
	 * @param id
	 */
	public void removeEVideo(int id) throws Exception;
	
	/**
	 * 本地视频
	 * @return
	 */
	public List<EVideo> getEVideoById(int id);
	
	
	/**
	 * 更新
	 * @param modifyEVideo
	 */
	public void modifyEVideo(int id, String videoSize) throws Exception;
	
	public List<EVideo> getUserEVideoDB(long userId);
	
	public List<EVideo> getPermVideos(String serverIP, int userId, long classId)throws Exception;
	
}
