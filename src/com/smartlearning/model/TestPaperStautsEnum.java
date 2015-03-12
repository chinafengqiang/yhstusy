package com.smartlearning.model;

import java.util.TreeMap;

/**
 * 
 */
public enum TestPaperStautsEnum {
	
	/**
	 * 待做
	 */
	waitDO,
	/**
	 * 正在做
	 */
	finishDoing,
	/**
	 * 已做
	 */
	finish,
	/**
	 * 已上传
	 */
	uploaded;
	
		
	/**
	 * 获取枚举类型名称
	 */
	public String toName() {

		if (this.equals(waitDO)) { return "待做"; }
		if (this.equals(finishDoing)) { return "正在做"; }
		if (this.equals(finish)) { return "已做"; }
		if (this.equals(uploaded)) { return "已上传"; }

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
	public static TestPaperStautsEnum valueOf(Integer value) {
		
		if (value == null) {
			return null;
		}

		return TestPaperStautsEnum.values()[value];
	}
	
	/**
	 * 获取枚举类型的所有<值,名称>对
	 * @return
	 */
	public static TreeMap<Integer, String> toMap(){
		
		TreeMap<Integer, String> map = new TreeMap<Integer, String>();
		for (int i=0; i< TestPaperStautsEnum.values().length; i++ ) {
			map.put(TestPaperStautsEnum.values()[i].ordinal(), TestPaperStautsEnum.values()[i].toName());
		}
		return map;
	}		
}
