package com.smartlearning.model;


import java.util.Date;


public class Video implements java.io.Serializable {
	
	private static final long serialVersionUID = 2758820714104235202L;

	 private Long id;
	 private String name;
	 private String url;
	 private Date createdTime;
	 private String categoryName;
	 private String pic;
	 private String lectuer;
	 private Integer hour;
	 private String description;
	 private String teacherDescription;
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
	public String getPic() {
		return pic;
	}
	public void setPic(String pic) {
		this.pic = pic;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
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
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public Date getCreatedTime() {
		return createdTime;
	}
	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}
	 
}
