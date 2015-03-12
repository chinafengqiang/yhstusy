package com.smartlearning.model;

import java.io.Serializable;


/**
 * 试卷题目数据类
 */
public class TestPaperQuestion  implements Serializable { 
	private static final long serialVersionUID = 8847928377217762504L;

	private int id; 
	
	private int _id; 
	
	/**
	 * 名称
	 */
	private String name;
	
	/**
	 * 试卷编号
	 */
	private int testPaperId;
	
	/**
	 * 题目编号
	 */
	private int questionId;
	
	/**
	 * 题目类型 
	 */
	private int questionType;
	
	/**
	 * 选项
	 */
	private String options;
	
	/**
	 * 答案
	 */
	private String answer;
	
	/**
	 * 知识点
	 */
	private String ken;
	
	/**
	 * 难度
	 */
	private int difficulty;

	/**
	 * 分数
	 */
	private double score;
	
	/**
	 * 备注
	 */
	private byte[] note;
	
	private int favStauts;

	public int getFavStauts() {
		return favStauts;
	}

	public void setFavStauts(int favStauts) {
		this.favStauts = favStauts;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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

	public int getTestPaperId() {
		return testPaperId;
	}

	public void setTestPaperId(int testPaperId) {
		this.testPaperId = testPaperId;
	}

	public int getQuestionId() {
		return questionId;
	}

	public void setQuestionId(int questionId) {
		this.questionId = questionId;
	}

	public int getQuestionType() {
		return questionType;
	}

	public void setQuestionType(int questionType) {
		this.questionType = questionType;
	}

	public String getOptions() {
		return options;
	}

	public void setOptions(String options) {
		this.options = options;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public String getKen() {
		return ken;
	}

	public void setKen(String ken) {
		this.ken = ken;
	}

	public int getDifficulty() {
		return difficulty;
	}

	public void setDifficulty(int difficulty) {
		this.difficulty = difficulty;
	}

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}

	public byte[] getNote() {
		return note;
	}

	public void setNote(byte[] note) {
		this.note = note;
	}
	
}
