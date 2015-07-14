package com.smartlearning.biz;

import android.content.Context;

import com.feng.vo.CategoryPlanVO;
import com.feng.vo.CoursePlanVO;
import com.smartlearning.dao.ICoursePlan;
import com.smartlearning.dao.impl.CoursePlanService;

public class CoursePlanManager {
	ICoursePlan coursePlan;
	
	public CoursePlanManager(Context context){
		coursePlan = new CoursePlanService(context);
	}
	
	public void insertCoursePlan(int userId,int type,String category,CoursePlanVO plan){
		coursePlan.insertCoursePlan(userId,type,category,plan);
	}
	
	public void removeCoursePlan(int id,int type){
		coursePlan.removeCoursePlan(id, type);
	}
	
	
	public void insertCategoryPlan(int userId,int type,CategoryPlanVO plan){
		coursePlan.insertCategoryPlan(userId,type,plan);
	}
}
