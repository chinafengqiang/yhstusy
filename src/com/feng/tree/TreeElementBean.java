package com.feng.tree;

import java.util.ArrayList;

public class TreeElementBean {
	//���yid
	private String id;
	//���y��
	private String nodeName ;
	//���ۗL�����y
	private boolean hasParent; 
	//���ۗL�q���y
	private boolean hasChild ;
	//�����yid
	private String upNodeId;
	
	private String upNodeName;
	//���ۓW�J
	private boolean expanded;
	//����
	private int level;
	//�q���y����
	private ArrayList<TreeElementBean> childNodes = new ArrayList<TreeElementBean>();  

	private int isAddRes;
	
	private String allIds;
	private String allNames;
	
	private int resCount = 0;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUpNodeId() {
		return upNodeId;
	}

	public void setUpNodeId(String upNodeId) {
		this.upNodeId = upNodeId;
	}

	public String getNodeName() {
		return nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	public boolean isHasParent() {
		return hasParent;
	}

	public void setHasParent(boolean hasParent) {
		this.hasParent = hasParent;
	}

	public boolean isHasChild() {
		return hasChild;
	}

	public void setHasChild(boolean hasChild) {
		this.hasChild = hasChild;
	}

	public boolean isExpanded() {
		return expanded;
	}

	public void setExpanded(boolean expanded) {
		this.expanded = expanded;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}
	
	public ArrayList<TreeElementBean> getChildNodes() {
		return childNodes;
	}

	public void setChildNodes(ArrayList<TreeElementBean> childNodes) {
		this.childNodes = childNodes;
	}
	
	public void addChild(TreeElementBean childNode) {
        this.hasChild = true;
        this.childNodes.add(childNode);
   }


	public int getIsAddRes() {
		return isAddRes;
	}

	public void setIsAddRes(int isAddRes) {
		this.isAddRes = isAddRes;
	}

	
	
	public String getUpNodeName() {
		return upNodeName;
	}

	public void setUpNodeName(String upNodeName) {
		this.upNodeName = upNodeName;
	}
	
	

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

	public void setAlls(String[] alls){
		if(alls != null && alls.length == 2){
			this.setAllIds(alls[0]+","+this.id);
			this.setAllNames(alls[1]+","+this.nodeName);
		}
	}
	
	public TreeElementBean(){
		
	}
	
	public TreeElementBean(String id, String nodeName,
			boolean hasParent, boolean hasChild, String upNodeId, int level,
			boolean expanded) {
		super();
		this.id = id;
		this.nodeName = nodeName;
		this.hasParent = hasParent;
		this.hasChild = hasChild;
		this.upNodeId = upNodeId;
		this.level = level;
		this.expanded = expanded;
	}

	public int getResCount() {
		return resCount;
	}

	public void setResCount(int resCount) {
		this.resCount = resCount;
	}
	
	
}
