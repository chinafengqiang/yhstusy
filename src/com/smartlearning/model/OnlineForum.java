package com.smartlearning.model;


public class OnlineForum {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column online_forum.id
     *
     * @mbggenerated
     */
    private Long id;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column online_forum.name
     *
     * @mbggenerated
     */
    private String name;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column online_forum.question
     *
     * @mbggenerated
     */
    private String question;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column online_forum.root_id
     *
     * @mbggenerated
     */
    private Long rootId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column online_forum.child_num
     *
     * @mbggenerated
     */
    private Integer childNum;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column online_forum.created_time
     *
     * @mbggenerated
     */
    private String createdTime;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column online_forum.creator
     *
     * @mbggenerated
     */
    private String creator;
    
    private Long classId;

    public Long getClassId() {
		return classId;
	}

	public void setClassId(Long classId) {
		this.classId = classId;
	}

	/**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column online_forum.id
     *
     * @return the value of online_forum.id
     *
     * @mbggenerated
     */
    public Long getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column online_forum.id
     *
     * @param id the value for online_forum.id
     *
     * @mbggenerated
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column online_forum.name
     *
     * @return the value of online_forum.name
     *
     * @mbggenerated
     */
    public String getName() {
        return name;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column online_forum.name
     *
     * @param name the value for online_forum.name
     *
     * @mbggenerated
     */
    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column online_forum.question
     *
     * @return the value of online_forum.question
     *
     * @mbggenerated
     */
    public String getQuestion() {
        return question;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column online_forum.question
     *
     * @param question the value for online_forum.question
     *
     * @mbggenerated
     */
    public void setQuestion(String question) {
        this.question = question == null ? null : question.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column online_forum.root_id
     *
     * @return the value of online_forum.root_id
     *
     * @mbggenerated
     */
    public Long getRootId() {
        return rootId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column online_forum.root_id
     *
     * @param rootId the value for online_forum.root_id
     *
     * @mbggenerated
     */
    public void setRootId(Long rootId) {
        this.rootId = rootId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column online_forum.child_num
     *
     * @return the value of online_forum.child_num
     *
     * @mbggenerated
     */
    public Integer getChildNum() {
        return childNum;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column online_forum.child_num
     *
     * @param childNum the value for online_forum.child_num
     *
     * @mbggenerated
     */
    public void setChildNum(Integer childNum) {
        this.childNum = childNum;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column online_forum.created_time
     *
     * @return the value of online_forum.created_time
     *
     * @mbggenerated
     */

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column online_forum.creator
     *
     * @return the value of online_forum.creator
     *
     * @mbggenerated
     */
    public String getCreator() {
        return creator;
    }

    public String getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(String createdTime) {
		this.createdTime = createdTime;
	}

	/**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column online_forum.creator
     *
     * @param creator the value for online_forum.creator
     *
     * @mbggenerated
     */
    public void setCreator(String creator) {
        this.creator = creator == null ? null : creator.trim();
    }
}