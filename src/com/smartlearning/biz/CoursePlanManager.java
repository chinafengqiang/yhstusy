package com.smartlearning.biz;

import android.content.Context;

import com.feng.vo.CoursePlanVO;
import com.smartlearning.dao.ICoursePlan;
import com.smartlearning.dao.impl.CoursePlanService;

public class CoursePlanManager {
	ICoursePlan coursePlan;
	
	public CoursePlanManager(Context context){
		coursePlan = new CoursePlanService(context);
	}
	
	public void insertCoursePlan(int userId,int type,String category,CoursePlanVO plan){
		coursePlan.insertCoursePlan(userId,userId,category,plan);
	}
}
