package com.smartlearning.dao;

import com.feng.vo.CategoryPlanVO;
import com.feng.vo.CoursePlanVO;

public interface ICoursePlan {
	public void insertCoursePlan(int userId,int type,String category,CoursePlanVO plan);
	
	public void removeCoursePlan(int id,int type);
	
	public void insertCategoryPlan(int userId,int type,CategoryPlanVO plan);
	
}
