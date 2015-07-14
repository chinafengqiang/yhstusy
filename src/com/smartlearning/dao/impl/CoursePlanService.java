package com.smartlearning.dao.impl;

import android.content.ContentValues;
import android.content.Context;

import com.feng.vo.CategoryPlanVO;
import com.feng.vo.CoursePlanVO;
import com.smartlearning.dao.ICoursePlan;
import com.smartlearning.db.DB.TABLES.COURSEPLAN;
import com.smartlearning.db.DB;
import com.smartlearning.db.DBHelper;

public class CoursePlanService implements ICoursePlan{
	private DBHelper helper = null;

	public CoursePlanService(Context context) {
		helper = new DBHelper(context);
	}

	@Override
	public void insertCoursePlan(int userId, int type, String category,
			CoursePlanVO plan) {
		ContentValues values = new ContentValues();
		values.put(COURSEPLAN.FIELDS._ID,plan.getId());
		values.put(COURSEPLAN.FIELDS.NAME,plan.getName());
		values.put(COURSEPLAN.FIELDS.FILE_URL,plan.getFileUrl());
		values.put(COURSEPLAN.FIELDS.START_DATE,plan.getStartDate());
		values.put(COURSEPLAN.FIELDS.END_DATE,plan.getEndDate());
		values.put(COURSEPLAN.FIELDS.TYPE,type);
		values.put(COURSEPLAN.FIELDS.USER_ID,userId);
		values.put(COURSEPLAN.FIELDS.CATEGORY_NAME,category);
		this.helper.insert(COURSEPLAN.TABLENAME, values);
		
	}

	@Override
	public void removeCoursePlan(int id, int type) {
		String condition = "_id ="+id+" and type = "+type;
		String sql = String.format(DB.TABLES.COURSEPLAN.SQL.DELETE, condition);
		helper.ExecuteSQL(sql);
	}

	@Override
	public void insertCategoryPlan(int userId, int type,
			CategoryPlanVO plan) {
		ContentValues values = new ContentValues();
		values.put(COURSEPLAN.FIELDS._ID,plan.getId());
		values.put(COURSEPLAN.FIELDS.NAME,plan.getName());
		values.put(COURSEPLAN.FIELDS.FILE_URL,plan.getFileUrl());
		values.put(COURSEPLAN.FIELDS.START_DATE,plan.getStartDate());
		values.put(COURSEPLAN.FIELDS.END_DATE,plan.getEndDate());
		values.put(COURSEPLAN.FIELDS.TYPE,type);
		values.put(COURSEPLAN.FIELDS.USER_ID,userId);
		values.put(COURSEPLAN.FIELDS.CATEGORY_NAME,plan.getCategoryName());
		this.helper.insert(COURSEPLAN.TABLENAME, values);
	}
	
	

	
	
	
}
