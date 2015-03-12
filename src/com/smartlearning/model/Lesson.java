package com.smartlearning.model;
/**
 * 课程安排类
 * 一个Lesson的实例对应 哪个班（className）在星期几（day）的第几节课（theTime）是什么课（subjectName）
 * _id : 课程编号
 * className:班级名称
 * subjectName : 专业名称
 * day : 星期几
 * theTime : 第几节课
 */
public class Lesson {
	private int id;
	private String className;
	private String subjectName;
	private int day;
	private int theTime;

	@Override
	public String toString() {
		return "Lesson [_id=" + id + ", className=" + className
				+ ", subjectName=" + subjectName + ", day=" + day
				+ ", thetime=" + theTime + "]";
	}
	
	
	public Lesson(){
		
	}

	public Lesson(int id, String className, String subjectName, int day,
			int thetime) {
		super();
		this.className = className;
		this.subjectName = subjectName;
		this.day = day;
		this.theTime = thetime;
	}


	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getSubjectName() {
		return subjectName;
	}

	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public int getThetime() {
		return theTime;
	}

	public void setThetime(int thetime) {
		this.theTime = thetime;
	}

}
