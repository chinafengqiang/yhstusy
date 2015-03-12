package com.smartlearning.biz;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.smartlearning.dao.IAdviod;
import com.smartlearning.dao.impl.AdviodService;
import com.smartlearning.model.Adviod;
import com.smartlearning.model.CoursePlan;
import com.smartlearning.model.CoursewareNode;
import com.smartlearning.model.EBook;
import com.smartlearning.model.EVideo;
import com.smartlearning.model.Video;
import com.smartlearning.utils.FastJsonTools;


/**
 * 视频集合管理
 * @author Administrator
 *
 */
public class AdviodManager {
	public static final String TAG = "AdviodManager";
	
	IAdviod adviodService ;
	
	/**
	 *初始化对象 
	 */
	public AdviodManager(Context context) {
		adviodService = new AdviodService(context);
	}
	
	/**
	 * 获得视频集合
	 * @param page 默认1,  rows 默认10
	 * @return
	 */
	public List<Adviod> geAdviodByPage(String serverIP,int page, int rows) {
		return adviodService.geAdviodByPage(serverIP,page, rows);
	}
	
	/**
	 *  传递url返回结果集
	 * @param jsonString
	 * @return
	 */
	public List<Adviod> transJsonToList(String jsonString) {
		try {
			String obj = "";
			JSONObject item;
			try {
				item = new JSONObject(jsonString);
				obj = item.getString("rows");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			List<Adviod> videos = FastJsonTools.getObects(obj, Adviod.class);
			return videos;
		} catch (Exception ex) {
			return null;
		}
	}
	
//	/**
//	 * 获得视频集合
//	 * @param 
//	 * @return
//	 */
//	public List<Video> getVideosByAll(String serverIP, int classId){
//		return adviodService.getVideosByAll(serverIP, classId);
//	}
//	
	/**
	 * 获得视频编号
	 * @param 
	 * @return
	 */
	public Video getVideosById(String serverIP,int id){
		return adviodService.getVideosById(serverIP,id);
	}
	
	/**
	 *  传递url返回结果集
	 * @param jsonString
	 * @return
	 */
	public List<Video> transJsonToLists(String jsonString) {
		try {
//			String obj = "";
//			JSONObject item;
//			try {
//				item = new JSONObject(jsonString);
//				obj = item.getString("rows");
//			} catch (JSONException e) {
//				e.printStackTrace();
//			}
			
			List<Video> videos = FastJsonTools.getObects(jsonString, Video.class);
			return videos;
		} catch (Exception ex) {
			return null;
		}
	}
	
	/**
	 * 获得课程教学计划
	 * @param 
	 * @return
	 */
	public List<CoursePlan> getPlanByAll(String serverIP) {
		return adviodService.getPlanByAll(serverIP);
	}
	
	public List<CoursePlan> getPlanByAll(String serverIP,long gradeId) {
		return adviodService.getPlanByAll(serverIP,gradeId);
	}
	
	/**
	 * 获得课程分类
	 * @param 
	 * @return
	 * @throws Exception 
	 */
	public List<CoursewareNode> getCategoryByAll(String serverIP) {
		return adviodService.getCategoryByAll(serverIP);
	}
	
	/**
	 * 删除
	 * @param id
	 */
	public void removeEVideo(int id) throws Exception{
		adviodService.removeEVideo(id);
	}
	
	/**
	 * 分页存取数据
	 * params:
	 * condtion: 如果无条件 condition 传入 1=1
	 * pageIndex:代表是第几页(首页从0开始)
	 * pageSize:代表每页多少条记录
	 * return:返回的就是分页之后的数据
	 */
	
	public List<EVideo> getByPager(int pageIndex, int pageSize,String condtion) {
		return adviodService.getByPager(pageIndex, pageSize, condtion);
	}
	
	/**
	 * 得到用于分页的数据源的总条数，一般要配合getByPager()方法共同使用 
	 * @param pageSize
	 * @param condtion 1=1 代表无条件
	 * @return
	 */
	public int getByPageCount(int pageSize,String condtion){
		return adviodService.getByPageCount(pageSize, condtion);
	}
	
	/**
	 * 获得电子书
	 * @param serverIP服务地址
	 * @return
	 */
	public List<EVideo> getAllEVideoList(String serverIP, int classId) throws Exception{
		return adviodService.getAllEVideoList(serverIP, classId);
	}
	
	/**
	 * 本地视频
	 * @return
	 */
	public EVideo getEVideoById(int id) {
		EVideo ev = null;
		List<EVideo> list = adviodService.getEVideoById(id);
		if(list.size() > 0){
			ev = list.get(0);
		}
		return ev;
		
	}
	/**
	 * 更新
	 * @param modifyEVideo
	 */
	public void modifyEVideo(int id, String videoSize) throws Exception{
		adviodService.modifyEVideo(id, videoSize);
	}
	
	public  List<EVideo> getPermVideos(String serverIP,int userId,long classId){
		
		List<EVideo> resList = null;
		try {
			resList = adviodService.getPermVideos(serverIP, userId, classId);
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG, e.getLocalizedMessage());
			
			resList = getUserEVideoDB(userId);
		}
		return resList;

	}
	
	
	public List<EVideo> getUserEVideoDB(long userId) {
		return adviodService.getUserEVideoDB(userId);
	}
}
