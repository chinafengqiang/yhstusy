package com.smartlearning.model;


/**
 * 试卷数据类
 */
public class UserTestPaper implements java.io.Serializable {

	private static final long serialVersionUID = 3683475447407774489L;

	private int id; 
	
	private int userId;
	
	private int questionId;
	
	private String time;
	
	private String score;
	
	private String isCorrect;
	
	private int testPaperId;
	
	/**
	 * 
	 */
	private String noSelectAnswer;
	
	public String getNoSelectAnswer() {
		return noSelectAnswer;
	}

	public void setNoSelectAnswer(String noSelectAnswer) {
		this.noSelectAnswer = noSelectAnswer;
	}

	public int getTestPaperId() {
		return testPaperId;
	}

	public void setTestPaperId(int testPaperId) {
		this.testPaperId = testPaperId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getQuestionId() {
		return questionId;
	}

	public void setQuestionId(int questionId) {
		this.questionId = questionId;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getScore() {
		return score;
	}

	public void setScore(String score) {
		this.score = score;
	}

	public String getIsCorrect() {
		return isCorrect;
	}

	public void setIsCorrect(String isCorrect) {
		this.isCorrect = isCorrect;
	}
	
	
}
