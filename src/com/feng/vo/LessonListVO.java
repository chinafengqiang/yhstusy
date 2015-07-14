package com.feng.vo;
import java.util.List;

import com.smartlearning.model.LessonVO;
public class LessonListVO {
	private int code;
	private int tempId;
	private List<LessonVO> info;
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public int getTempId() {
		return tempId;
	}
	public void setTempId(int tempId) {
		this.tempId = tempId;
	}
	public List<LessonVO> getInfo() {
		return info;
	}
	public void setInfo(List<LessonVO> info) {
		this.info = info;
	}
	
	
}
