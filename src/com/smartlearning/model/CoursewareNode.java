package com.smartlearning.model;

public class CoursewareNode {

	/**
	 * 上级节点编号
	 */
	private Long parentId;
	
	/**
	 * 当前节点编号
	 */
	private Long id;
	
	/**
	 * 节点名称
	 */
	private String name;
	
	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
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
}
