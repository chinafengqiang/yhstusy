package com.smartlearning.dao;

import java.util.List;

import com.smartlearning.model.LessonExtend;
import com.smartlearning.model.LessonVO;

/**
 * 课程安排信息接口
 * 该接口主要用于课程表模块获取数据
 * 从此接口可以：
 * 获得课程安排集合
 * @author Administrator
 */
public interface ILesson {

	/**
	 * 获得课程表集合
	 * @param classId
	 * @return
	 */
	public List<LessonExtend> getLessonsByClassId(String serverIP,Long classId)throws Exception;
	
	public List<LessonVO> getPermLessons(String serverIP,Long classId)throws Exception;
}
