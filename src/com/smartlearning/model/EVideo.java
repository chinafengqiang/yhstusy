package com.smartlearning.model;




public class EVideo implements java.io.Serializable {
	
	private static final long serialVersionUID = 2758820714104235202L;

	 private long id;
	 private int _id;
	 private String name;
	 private String time;
	 private String categoryName;
	 private String videoUrl;
	 private byte[] imageUrl;
	 private String lectuer;
	 private Integer hour;
	 private String description;
	 private String teacherDescription;
	 private String videoSize;
	 private boolean isLocalFile = false;
	 
	public boolean isLocalFile() {
		return isLocalFile;
	}
	public void setLocalFile(boolean isLocalFile) {
		this.isLocalFile = isLocalFile;
	}
	public String getVideoSize() {
		return videoSize;
	}
	public void setVideoSize(String videoSize) {
		this.videoSize = videoSize;
	}
	public String getLectuer() {
		return lectuer;
	}
	public void setLectuer(String lectuer) {
		this.lectuer = lectuer;
	}
	public Integer getHour() {
		return hour;
	}
	public void setHour(Integer hour) {
		this.hour = hour;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getTeacherDescription() {
		return teacherDescription;
	}
	public void setTeacherDescription(String teacherDescription) {
		this.teacherDescription = teacherDescription;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCategoryName() {
		return categoryName;
	}
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public int get_id() {
		return _id;
	}
	public void set_id(int _id) {
		this._id = _id;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getVideoUrl() {
		return videoUrl;
	}
	public void setVideoUrl(String videoUrl) {
		this.videoUrl = videoUrl;
	}
	public byte[] getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(byte[] imageUrl) {
		this.imageUrl = imageUrl;
	}

	 
}
