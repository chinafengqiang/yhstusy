package com.smartlearning.dao.impl;

import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.smartlearning.common.HttpUtil;
import com.smartlearning.constant.ServerIP;
import com.smartlearning.dao.ILesson;
import com.smartlearning.model.LessonExtend;
import com.smartlearning.model.LessonVO;
import com.smartlearning.utils.FastJsonTools;


/**
 * 教学课程表安排业务类
 * @author Administrator
 */
public class LessonService implements ILesson{

	/**
	 * 获得课程表集合
	 * @param classId
	 * @return
	 * @throws Exception 
	 */
	public List<LessonExtend> getLessonsByClassId(String serverIP,Long classId) throws Exception {
		
		String path = String.format(serverIP+ServerIP.SERVLET_LESSON, classId);
		byte[] data = HttpUtil.getDataFromUrl(path);
		String jsonString = new String(data);
		if(jsonString == null || jsonString.equals("")){
			throw new Exception();
		}
		Log.i("learning json", jsonString);
		List<LessonExtend> lessons = FastJsonTools.getObects(jsonString, LessonExtend.class);
		
		return lessons;
	}

	@Override
	public List<LessonVO> getPermLessons(String serverIP, Long classId)
			throws Exception {
		String path = String.format(serverIP+ServerIP.SERVLET_PERM_LESSON, classId);
		byte[] data = HttpUtil.getDataFromUrl(path);
		String jsonString = new String(data);
		if(jsonString == null || jsonString.equals("")){
			throw new Exception();
		}
		String obj = "";
		JSONObject item = new JSONObject(jsonString);
		obj = item.getString("info");
		Log.i("learning json", jsonString);
		List<LessonVO> lessons = FastJsonTools.getObects(obj, LessonVO.class);
		return lessons;
	}

	@Override
	public List<LessonVO> getPermLessons(String serverIP, Long classId,
			HashMap<String, Integer> hasTempMap) throws Exception {
		String path = String.format(serverIP+ServerIP.SERVLET_PERM_LESSON, classId);
		byte[] data = HttpUtil.getDataFromUrl(path);
		String jsonString = new String(data);
		if(jsonString == null || jsonString.equals("")){
			throw new Exception();
		}
		String obj = "";
		JSONObject item = new JSONObject(jsonString);
		obj = item.getString("info");
		Log.i("learning json", jsonString);
		List<LessonVO> lessons = FastJsonTools.getObects(obj, LessonVO.class);
		int tempId = item.getInt("tempId");
		hasTempMap.put("tempId", tempId);
		return lessons;
	}

	@Override
	public List<LessonVO> getPermLessonsTemp(String serverIP, int lessonId)
			throws Exception {
		String path = String.format(serverIP+ServerIP.SERVLET_PERM_LESSON_TEMP, lessonId);
		byte[] data = HttpUtil.getDataFromUrl(path);
		String jsonString = new String(data);
		if(jsonString == null || jsonString.equals("")){
			throw new Exception();
		}
		String obj = "";
		JSONObject item = new JSONObject(jsonString);
		obj = item.getString("info");
		List<LessonVO> lessons = FastJsonTools.getObects(obj, LessonVO.class);
		return lessons;
	}
	
	
	
}
