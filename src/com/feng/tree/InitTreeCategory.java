package com.feng.tree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.smartlearning.biz.BookManager;

public class InitTreeCategory {
	public static final int[] IDS = {63,77,78,79};
	public static final String[] NAMES = {"课后练习","课堂板书","补充资料","其他"};
	
	public static final int[] VIDEO_IDS = {100,101};
	public static final String[] VIDEO_NAMES = {"课前导读","优秀录像"}; 
	
	public static List<TreeElementBean> getCategoryTree(int partId,int plevel,BookManager bookManager){
		List<TreeElementBean> resList = new ArrayList<>(IDS.length);
		TreeElementBean ele;
		String whereSql = " from ebook e where  ";
		for(int i = 0;i<IDS.length;i++){
			whereSql += "category_name = '"+partId+"#"+IDS[i]+"'";
			if(i != IDS.length-1){
				whereSql += " or ";
			}
		}
		
		HashMap<String,Integer> res = bookManager.getResLocalCount(whereSql);
		
		for(int i = 0;i<IDS.length;i++){
			ele = new TreeElementBean();
			ele.setId(IDS[i]+"");
			ele.setNodeName(NAMES[i]);
			ele.setExpanded(true);
			ele.setHasChild(false);
			ele.setHasParent(true);
			ele.setIsAddRes(2);
			ele.setLevel(plevel+1);
			ele.setUpNodeId(partId+"");
			
			Integer count = res.get(partId+"#"+IDS[i]);
			if(count != null){
				ele.setResCount(count);
			}
			resList.add(ele);
		}
		return resList;
	}
	
	
	
	public static List<TreeElementBean> getCategoryTree(int partId,int plevel){
		List<TreeElementBean> resList = new ArrayList<>(IDS.length);
		TreeElementBean ele;
		for(int i = 0;i<IDS.length;i++){
			ele = new TreeElementBean();
			ele.setId(IDS[i]+"");
			ele.setNodeName(NAMES[i]);
			ele.setExpanded(true);
			ele.setHasChild(false);
			ele.setHasParent(true);
			ele.setIsAddRes(2);
			ele.setLevel(plevel+1);
			ele.setUpNodeId(partId+"");
			resList.add(ele);
		}
		return resList;
	}
	
	public static List<TreeElementBean> getVideoCategoryTree(int partId,int plevel,BookManager bookManager){
		List<TreeElementBean> resList = new ArrayList<>(VIDEO_IDS.length);
		TreeElementBean ele;
		String whereSql = " from evideos e where  ";
		for(int i = 0;i<VIDEO_IDS.length;i++){
			whereSql += "category_name = '"+partId+"#"+VIDEO_IDS[i]+"'";
			if(i != VIDEO_IDS.length-1){
				whereSql += " or ";
			}
		}
		
		HashMap<String,Integer> res = bookManager.getResLocalCount(whereSql);
		for(int i = 0;i<VIDEO_IDS.length;i++){
			ele = new TreeElementBean();
			ele.setId(VIDEO_IDS[i]+"");
			ele.setNodeName(VIDEO_NAMES[i]);
			ele.setExpanded(true);
			ele.setHasChild(false);
			ele.setHasParent(true);
			ele.setIsAddRes(2);
			ele.setLevel(plevel+1);
			ele.setUpNodeId(partId+"");
			
			Integer count = res.get(partId+"#"+IDS[i]);
			if(count != null){
				ele.setResCount(count);
			}
			
			resList.add(ele);
		}
		return resList;
	}
}
