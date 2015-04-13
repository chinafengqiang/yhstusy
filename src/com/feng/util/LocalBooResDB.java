package com.feng.util;

import java.util.HashMap;
import java.util.List;

import com.feng.tree.TreeElementBean;
import com.feng.vo.BookCategory;
import com.feng.vo.BookPart;

public class LocalBooResDB {

	public static void getLocalBookInfo(final String allIds,final String allNames
			,BookCategory category,BookPart part,List<TreeElementBean> resList){
						String[] ids = allIds.split(",");
						String[] names = allNames.split(",");
						String categoryId = ids[0];
						String categoryName = names[0];
						String partId = ids[1];
						String partName = names[1];
						int catId = Integer.parseInt(categoryId);
						category.setId(catId);
						category.setName(categoryName);
						int pId = Integer.parseInt(partId);
						part.setId(pId);
						part.setName(partName);
						part.setCategoryId(catId);
						
						TreeElementBean ele = null;
						
						int level = 0;
						
						for(int i = 2;i<ids.length;i++){
							ele = new TreeElementBean();
							boolean hasParent = true;
							boolean hasChild = true;
							int isAddRes = 0;
							String pid = ids[i-1];
							String pidName = names[i-1];
							if(i == 2){
								hasParent = false;
								pid = "0";
								pidName="";
							}
							if(i == ids.length - 1){
								hasChild = false;
								isAddRes = 2;
							}
							if(i == ids.length - 2){
								isAddRes = 1;
							}
							ele.setId(ids[i]);
							ele.setNodeName(names[i]);
							ele.setHasChild(hasChild);
							ele.setHasParent(hasParent);
							ele.setUpNodeId(pid);
							ele.setUpNodeName(pidName);
							ele.setExpanded(false);
							ele.setLevel(level);
							ele.setIsAddRes(isAddRes);
							resList.add(ele);
							level++;
						}
	}
	
	
}
