package com.smartlearning.biz;

import java.util.HashMap;
import java.util.List;

import com.smartlearning.dao.ILesson;
import com.smartlearning.dao.impl.LessonService;
import com.smartlearning.model.LessonExtend;
import com.smartlearning.model.LessonVO;


/**
 * 课程表集合管理
 * @author Administrator
 *
 */
public class LessonManager {
	
	ILesson lessonService ;
	
	/**
	 *初始化对象 
	 */
	public LessonManager() {
		lessonService = new LessonService();
	}
	
	/**
	 * 获得课程表集合
	 * @param classId
	 * @return
	 */
	public List<LessonExtend> getLessonsByClass(String serverIP,Long classId)throws Exception {
		return lessonService.getLessonsByClassId(serverIP,classId);
	}
	
	public List<LessonVO> getPermLessons(String serverIP,Long classId){
		try {
			return lessonService.getPermLessons(serverIP, classId);
		} catch (Exception e) {
			return null;
		}
	}
	
	public List<LessonVO> getPermLessons(String serverIP,Long classId,HashMap<String,Integer> hasTempMap){
		try {
			return lessonService.getPermLessons(serverIP, classId,hasTempMap);
		} catch (Exception e) {
			return null;
		}
	}
	
	public List<LessonVO> getPermLessonsTemp(String serverIP,int lessonId){
		try {
			return lessonService.getPermLessonsTemp(serverIP, lessonId);
		} catch (Exception e) {
			return null;
		}
	}
	
}
