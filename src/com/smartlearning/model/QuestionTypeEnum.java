package com.smartlearning.model;

import java.util.TreeMap;

/**
 * 题目类型枚举
 */
public enum QuestionTypeEnum {
	
	/**
	 * 单选题 
	 */
	SingleSelect,
	/**
	 * 多选题
	 */
	MultiSelect,
	/**
	 * 判断题
	 */
	Judge,
	
	/**
	 * 非选择题
	 */
	NoSelect;
		
	/**
	 * 获取枚举类型名称
	 */
	public String toName() {

		if (this.equals(SingleSelect)) { return "单选题"; }
		if (this.equals(MultiSelect)) { return "多选题"; }
		if (this.equals(Judge)) { return "判断题"; }
		if (this.equals(NoSelect)) { return "非选题"; }

		return "";
	}
	
	/**
	 * 获取枚举类型数值
	 */
	public Integer toValue() {
	
		return this.ordinal();
	}
	
	/**
	 * 按数值获取对应的枚举类型
	 * @param value 数值
	 * @return 枚举类型
	 */
	public static QuestionTypeEnum valueOf(Integer value) {
		
		if (value == null) {
			return null;
		}

		return QuestionTypeEnum.values()[value];
	}
	
	/**
	 * 获取枚举类型的所有<值,名称>对
	 * @return
	 */
	public static TreeMap<Integer, String> toMap(){
		
		TreeMap<Integer, String> map = new TreeMap<Integer, String>();
		for (int i=0; i< QuestionTypeEnum.values().length; i++ ) {
			map.put(QuestionTypeEnum.values()[i].ordinal(), QuestionTypeEnum.values()[i].toName());
		}
		return map;
	}		
}
