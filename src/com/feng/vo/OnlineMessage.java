package com.feng.vo;

import java.util.List;

public class OnlineMessage {
	private int id;
	private String msg;
	private String msgtime;
	private String sender;
	private String imagePath;
	private String localImagePath;
	
	private boolean isClickReply = false;//打开还是折叠回复列表的标志位
	
	private List<OnlineMessageReply> replyList;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}

	

	public String getMsgtime() {
		return msgtime;
	}
	public void setMsgtime(String msgtime) {
		this.msgtime = msgtime;
	}
	public String getSender() {
		return sender;
	}
	public void setSender(String sender) {
		this.sender = sender;
	}
	public String getImagePath() {
		return imagePath;
	}
	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}
	public String getLocalImagePath() {
		return localImagePath;
	}
	public void setLocalImagePath(String localImagePath) {
		this.localImagePath = localImagePath;
	}
	public List<OnlineMessageReply> getReplyList() {
		return replyList;
	}
	public void setReplyList(List<OnlineMessageReply> replyList) {
		this.replyList = replyList;
	}
	public boolean isClickReply() {
		return isClickReply;
	}
	public void setClickReply(boolean isClickReply) {
		this.isClickReply = isClickReply;
	}
	
	
}
