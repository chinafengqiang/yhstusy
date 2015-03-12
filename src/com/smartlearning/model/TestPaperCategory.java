package com.smartlearning.model;

/**
 *试卷分类
 */
public class TestPaperCategory {

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
	}

	private int id;
	
	private int _id;
	
	private String name;
}
