package com.smartlearning.model;

import java.util.Date;

/**
 * 记事本
 * @author Administrator
 */
public class Note {

    private Long id;

    private Long userId;

    private Long coursewareId;

    private Date createdTime;

    private String note;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getCoursewareId() {
		return coursewareId;
	}

	public void setCoursewareId(Long coursewareId) {
		this.coursewareId = coursewareId;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}
    
}
