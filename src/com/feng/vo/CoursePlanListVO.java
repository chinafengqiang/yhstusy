package com.feng.vo;

import java.util.List;

public class CoursePlanListVO {
	private long planCount;
	private List<CoursePlanVO> planList;
	public long getPlanCount() {
		return planCount;
	}
	public void setPlanCount(long planCount) {
		this.planCount = planCount;
	}
	public List<CoursePlanVO> getPlanList() {
		return planList;
	}
	public void setPlanList(List<CoursePlanVO> planList) {
		this.planList = planList;
	}
	
	
}
