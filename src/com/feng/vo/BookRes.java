package com.feng.vo;

public class BookRes {
	private int resId;
	private String resName;
	private String resUrl;
	private String resCreateTime;
	
	
	private boolean isLocalFile = false;
	
	private String allIds;
	private String allNames;
	
	public String getAllIds() {
		return allIds;
	}
	public void setAllIds(String allIds) {
		this.allIds = allIds;
	}
	public String getAllNames() {
		return allNames;
	}
	public void setAllNames(String allNames) {
		this.allNames = allNames;
	}
	public boolean isLocalFile() {
		return isLocalFile;
	}
	public void setLocalFile(boolean isLocalFile) {
		this.isLocalFile = isLocalFile;
	}
	
	public int getResId() {
		return resId;
	}
	public void setResId(int resId) {
		this.resId = resId;
	}
	public String getResName() {
		return resName;
	}
	public void setResName(String resName) {
		this.resName = resName;
	}
	public String getResUrl() {
		return resUrl;
	}
	public void setResUrl(String resUrl) {
		this.resUrl = resUrl;
	}
	public String getResCreateTime() {
		return resCreateTime;
	}
	public void setResCreateTime(String resCreateTime) {
		this.resCreateTime = resCreateTime;
	}
	
}
