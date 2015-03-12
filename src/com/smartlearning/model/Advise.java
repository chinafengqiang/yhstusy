package com.smartlearning.model;

public class Advise {
	private String id;
	private String rootId;
	private String question;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public int getParent_id() {
		return parent_id;
	}

	public String getRootId() {
		return rootId;
	}

	public void setRootId(String rootId) {
		this.rootId = rootId;
	}

	public void setParent_id(int parentId) {
		parent_id = parentId;
	}

	public String getTheContent() {
		return theContent;
	}

	public void setTheContent(String theContent) {
		this.theContent = theContent;
	}

	public String getTheTime() {
		return theTime;
	}

	public void setTheTime(String theTime) {
		this.theTime = theTime;
	}

	private int parent_id;
	private String theContent;
	private String theTime;
}
