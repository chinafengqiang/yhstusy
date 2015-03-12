package com.smartlearning.biz;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Calendar;
import java.util.List;

import javax.jcr.LoginException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.apache.jackrabbit.rmi.repository.URLRemoteRepository;

import android.content.Context;

import com.smartlearning.dao.IUserTestPaperService;
import com.smartlearning.dao.impl.UserTestPaperService;
import com.smartlearning.model.UserTestPaper;
import com.smartlearning.utils.StreamTool;


/**
 * 用户试卷记录集合管理
 * @author Administrator
 *
 */
public class UserTestPaperManager {
	
	IUserTestPaperService userTestPaperService ;
	
	private static Repository repository = null;
	
	/**
	 *初始化对象 
	 */
	public UserTestPaperManager(Context context) {
		userTestPaperService = new UserTestPaperService(context);
	}
	
	/**
	 * 保存试卷信息
	 * @param testPaper
	 */
	public void InsertUserTestPaper(UserTestPaper userTestPaper){
		userTestPaperService.InsertUserTestPaper(userTestPaper);
	}
	
	/**
	 * 获得分数
	 * @return
	 */
	public double getSumScore(int testPaperId) {
		return userTestPaperService.getSumScore(testPaperId);
	}
	
	/**
	 * 获得是否存在用户记录数据
	 * @return
	 */
	public boolean profileExist(int testPaperId){
		return userTestPaperService.profileExist(testPaperId);
	}
	
	/**
	 * 取得考卷对错矩阵
	 * @param id
	 * @return
	 */
	public  boolean[] getRwMatrix(int testPaperId) {
		return userTestPaperService.getRwMatrix(testPaperId);
	}
	
	public List<UserTestPaper> getUserTestPaperById(int testPaperId) {
		return userTestPaperService.getUserTestPaperById(testPaperId);
	}
	/**
	 * 创建用户试卷记录
	 * @param userTestPaper
	 * @throws Exception 
	 */
	public boolean saveUserTestPaper(String serverIP, UserTestPaper userTestPaper) throws Exception{
		return userTestPaperService.saveUserTestPaper(serverIP, userTestPaper);
	}
	
	
	/**
	 * 初始化
	 */
	private Repository initRepository(){
		if(null == repository){
			//String url = ReadConfig.getString("file.rmi");
			String url = "http://192.168.1.101:7000/rmi";
			try {
				repository = new URLRemoteRepository(url);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
		return repository;
	}
	
	/**
	 * 获得节点
	 * @param root
	 * @param name
	 * @return
	 */
	public Node getNode(Node root,String name){
		Node node=null;
		try {
			if(root.hasNode(name)){
				node= root.getNode(name);
			}else{
				node= root.addNode(name);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return node;
	}
	
	/**
	 * 获得seesion
	 * @return
	 */
	public Session getSession(){
		Session session=null;
		try {
			session=initRepository().login(new SimpleCredentials("admin", "admin".toCharArray()));
		} catch (LoginException e) {
			e.printStackTrace();
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
		return session;
	}
	
	
	
	public boolean deleteFile(String fileId, String userId) {
		boolean flag=false;
		Session session=getSession();
		try {
			Node root = session.getRootNode();
			Node n=getNode(root, userId);
			NodeIterator iterator=n.getNodes(fileId);
			while(iterator.hasNext()){
				Node fnode=iterator.nextNode();
				fnode.remove();
			}
			session.save();
			session.logout();
			flag=true;
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
		return flag;
	}

	public InputStream getFileById(String fileId, String userId) {
		InputStream is=null;
		Session session=getSession();
		try {
			Node root=session.getRootNode();
			Node n=getNode(root, userId);
			NodeIterator iterator=n.getNodes(fileId);
			while(iterator.hasNext()){
				Node filenode=iterator.nextNode();
				NodeIterator ni1=filenode.getNodes();
				while(ni1.hasNext()){
					Node ni=ni1.nextNode();
					if(ni.getName().equals("jcr:content")){
						is=ni.getProperty("jcr:data").getStream();
					}
				}
			}
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
		session.logout();
		return is;
	}

	public String save(InputStream is, String userId) {
		String fileId = StreamTool.createUUID();
		Session session=getSession();
		try {
			Node root=session.getRootNode();
			Node n=getNode(root, userId);
			String mimeType = "application/octet-stream";
			Node filenode=n.addNode(fileId,"nt:file");
			Node resourceNode=filenode.addNode("jcr:content","nt:resource");
			resourceNode.setProperty("jcr:mimeType", mimeType);
			resourceNode.setProperty("jcr:encoding", "");
			resourceNode.setProperty("jcr:data", is);
			Calendar lastmodifyDate=Calendar.getInstance();
			resourceNode.setProperty("jcr:lastModified", lastmodifyDate);
			session.save();
			is.close();
			session.logout();
		} catch (RepositoryException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fileId;
	}
	
}
