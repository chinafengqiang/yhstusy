package com.smartlearning.factory;

import android.content.Context;

import com.smartlearning.biz.TestPaperManager;

/**
 *简单工厂
 */
public class TestPaperFactory {

	private TestPaperFactory(){
	}
	
	public static TestPaperManager createTestPaper(Context context){
		return new TestPaperManager(context);
	}
	
}
