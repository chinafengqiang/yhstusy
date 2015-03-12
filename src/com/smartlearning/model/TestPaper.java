package com.smartlearning.model;


/**
 * 试卷数据类
 */
public class TestPaper{

	private int id; 
	
	private int _id; 

	/**
	 * 名称
	 */
	private String name;
	
	private int parent_id;
	
	private String passScore;
	
	private String totalScore;
	
	private int classId;
	
	private int stauts;

	public int getStauts() {
		return stauts;
	}

	public void setStauts(int stauts) {
		this.stauts = stauts;
	}

	public int getClassId() {
		return classId;
	}

	public void setClassId(int classId) {
		this.classId = classId;
	}

	public String getPassScore() {
		return passScore;
	}

	public void setPassScore(String passScore) {
		this.passScore = passScore;
	}

	public String getTotalScore() {
		return totalScore;
	}

	public void setTotalScore(String totalScore) {
		this.totalScore = totalScore;
	}

	public int getParent_id() {
		return parent_id;
	}

	public void setParent_id(int parent_id) {
		this.parent_id = parent_id;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
}
