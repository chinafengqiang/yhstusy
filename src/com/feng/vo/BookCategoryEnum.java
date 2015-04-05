package com.feng.vo;

import com.smartlearning.R;

public enum BookCategoryEnum {
	YW("53", R.drawable.bc_53), 
	SX("60", R.drawable.bc_60), 
	DL("61",R.drawable.bc_61), 
	YY("62", R.drawable.bc_62), 
	WL("64",R.drawable.bc_64), 
	LS("66", R.drawable.bc_66), 
	ZZ("67",R.drawable.bc_67), 
	HX("68", R.drawable.bc_68), 
	SW("69",R.drawable.bc_69);

	// 成员变量
	private String name;
	private int index;

	// 构造方法
	private BookCategoryEnum(String name, int index) {
		this.name = name;
		this.index = index;
	}
	
	// 普通方法
	public static String getName(int index) {
		for (BookCategoryEnum c : BookCategoryEnum.values()) {
			if (c.getIndex() == index) {
				return c.name;
			}
		}
		return "";
	}
	
	public static int getValue(String name) {
		for (BookCategoryEnum c : BookCategoryEnum.values()) {
			if (c.getName().equals(name)) {
				return c.index;
			}
		}
		return 0;
	}
	// get set 方法
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
}
