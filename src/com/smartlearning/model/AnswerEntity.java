
package com.smartlearning.model;

public class AnswerEntity {
	
	/**
	 * 是否为正确选项
	 */
	private boolean correct = false;
	
	/**
	 * 是否被选择
	 * 这一项数据不保存到XML数据文件中
	 */
	private boolean selected = false;
	
	/**
	 * 答案内容
	 */
	private String content;

	public boolean isCorrect() {
		return correct;
	}

	public AnswerEntity setCorrect(boolean correct) {
		this.correct = correct;
		return this;
	}

	public boolean isSelected() {
		return selected;
	}

	public AnswerEntity setSelected(boolean selected) {
		this.selected = selected;
		return this;
	}

	public String getContent() {
		return content;
	}

	public AnswerEntity setContent(String content) {
		this.content = content;
		return this;
	}
}
