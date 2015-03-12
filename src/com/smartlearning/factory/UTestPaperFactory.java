package com.smartlearning.factory;

import android.content.Context;

import com.smartlearning.biz.UserTestPaperManager;

/**
 *简单工厂
 */
public class UTestPaperFactory {

	private UTestPaperFactory(){
	}
	
	public static UserTestPaperManager createUTestPaper(Context context){
		return new UserTestPaperManager(context);
	}
	
}
