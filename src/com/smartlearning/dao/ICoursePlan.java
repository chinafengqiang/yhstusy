package com.smartlearning.dao;

import com.feng.vo.CoursePlanVO;

public interface ICoursePlan {
	public void insertCoursePlan(int userId,int type,String category,CoursePlanVO plan);
}
