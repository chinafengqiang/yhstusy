package com.feng.vo;

import java.util.HashMap;
import java.util.List;

public class MessageListVO {
	private int totals = 0;
	private List<OnlineMessage> messageList;
	public int getTotals() {
		return totals;
	}
	public void setTotals(int totals) {
		this.totals = totals;
	}
	public List<OnlineMessage> getMessageList() {
		return messageList;
	}
	public void setMessageList(List<OnlineMessage> messageList) {
		this.messageList = messageList;
	}
	
	
}
