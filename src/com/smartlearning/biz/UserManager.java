package com.smartlearning.biz;

import java.util.List;

import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.smartlearning.dao.IUser;
import com.smartlearning.dao.impl.UserService;
import com.smartlearning.model.SysKey;
import com.smartlearning.model.SysMessage;
import com.smartlearning.model.User;
import com.smartlearning.model.UserInfo;
import com.smartlearning.utils.FastJsonTools;


/**
 * 用户登录集合管理
 * @author Administrator
 *
 */
public class UserManager {
	
	IUser userService ;
	
	/**
	 *初始化对象 
	 */
	public UserManager(Context context) {
		userService = new UserService(context);
	}
	
	/**
	 * 用户登录方法
	 * @param name
	 * @param password
	 * @return 返回用户对象
	 * @throws Exception 
	 */
	public UserInfo login(String serverIP, String name, String password) throws Exception {
		return userService.login(serverIP, name, password);
	}
	
	/**
	 *  传递url返回结果集
	 * @param jsonString
	 * @return
	 */
	public SysKey transJsonToObj(String jsonString) {
		String success = "";
		SysKey sysKey = null;
		try {
			JSONObject item = new JSONObject(jsonString);
			success = item.getString("success");
			
			if("true".equals(success)){
				String jIP = item.getString("obj");
				sysKey = FastJsonTools.getObect(jIP, SysKey.class);
				
				Log.i("learning SERVER IP ====", sysKey.getKeyValue());
			} else if("false".equals(success)) {
				return null;
			}else{
				throw new Exception("网络异常！"); 
			}
			
		} catch (Exception ex) {
			return null;
		}
		
		return sysKey;
	}
	
	/**
	 * 获得通知信息
	 * @param serverIP服务地址
	 * @return
	 */
	public List<SysMessage> getAllSysMessageList(String serverIP, int page, int rows, Long classId) {
		
		return userService.getAllSysMessageList(serverIP,  page, rows, classId);
	}
	
	/**
	 * 获得课程表集合
	 * @param classId
	 * @return
	 */
	public List<SysMessage> getNewSysMessageList(String serverIP, int last_message_id, int page, int rows, Long classId) {
		return userService.getNewSysMessageList(serverIP, last_message_id,  page, rows, classId);
	}
	
	/**
	 * 获得最后值
	 * @return
	 */
	public int getLastSysMessageId(Long class_id) {
		return userService.getLastSysMessageId(class_id);
	}
	
	
	/**
	 * 获得课程表集合
	 * @param classId
	 * @return
	 */
	public List<SysMessage> getRefreshSysMessageList(String serverIP, int page, int rows, Long classId) {
		return userService.getRefreshSysMessageList(serverIP, page, rows, classId);
	}
	
	/**
	 * 更新
	 * @param sysMessage
	 */
	public void updataSysMessage(String serverIP, SysMessage sysMessage) throws Exception{
		userService.updataSysMessage(serverIP, sysMessage);
	}
	
	/**
	 * 本用户
	 * @return
	 */
	public List<User> getAllUserFromLocal(){
		return userService.getAllUserFromLocal();
	}
	
	/**
	 * 用户插入
	 * @param user
	 */
	public void insert(User user){
		userService.insert(user);
	}
	
	public List<SysMessage> getAllSysMessageFromDB() {
		return userService.getAllSysMessageFromDB();
	}
	
	/**
	 * 获得记录数
	 * @param condtion
	 * @return
	 */
	public int getUserCount(String condtion){
		return userService.getUserCount(condtion);
	}
	
	public void deleteAllUser(){
		userService.deleteAllUser();
	}
	
	public void modifyPass(int userId,String pass){
		userService.modifyPass(userId, pass);
	}
}
