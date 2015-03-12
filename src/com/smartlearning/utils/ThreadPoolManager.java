package com.smartlearning.utils;

import android.os.Looper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPoolManager {
	private ExecutorService service;
	
	private ThreadPoolManager(){
        //cpu的处理个数
		int num = Runtime.getRuntime().availableProcessors();
	    service = Executors.newFixedThreadPool(2*num);
        //service = Executors.newCachedThreadPool();
	}
	
	private static final ThreadPoolManager manager= new ThreadPoolManager();
	
	public static ThreadPoolManager getInstance(){
		return manager;
	}
	
	public void addTask(Runnable runnable){
		service.execute(runnable);
	}
}
