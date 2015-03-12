package com.smartlearning.model;

public class EBook {

	 private int _id;
	 private String name;
	 private String time;
	 private String pdfUrl;
	 private byte[] imageUrl;
	 private String categoryName;
	 private int class_id;
	 private boolean isLocalFile = false;
	 
	 
	public boolean isLocalFile() {
		return isLocalFile;
	}
	public void setLocalFile(boolean isLocalFile) {
		this.isLocalFile = isLocalFile;
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
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getPdfUrl() {
		return pdfUrl;
	}
	public void setPdfUrl(String pdfUrl) {
		this.pdfUrl = pdfUrl;
	}
	public byte[] getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(byte[] imageUrl) {
		this.imageUrl = imageUrl;
	}
	public String getCategoryName() {
		return categoryName;
	}
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	public int getClass_id() {
		return class_id;
	}
	public void setClass_id(int class_id) {
		this.class_id = class_id;
	}
	 
	 
}
