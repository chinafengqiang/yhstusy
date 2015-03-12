package com.smartlearning.dao.impl;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.util.Log;

import com.smartlearning.common.HttpUtil;
import com.smartlearning.constant.ServerIP;
import com.smartlearning.dao.IUser;
import com.smartlearning.db.DB;
import com.smartlearning.db.DB.TABLES.SYSMESSAGE;
import com.smartlearning.db.DB.TABLES.USERINFO;
import com.smartlearning.db.DBHelper;
import com.smartlearning.model.SysMessage;
import com.smartlearning.model.User;
import com.smartlearning.model.UserInfo;
import com.smartlearning.utils.FastJsonTools;


/**
 * 用户登录接口
 * @author Administrator
 */
public class UserService implements IUser{
	
	private DBHelper helper = null;

	public UserService(Context context) {
		helper = new DBHelper(context);
	}

	/**
	 * 用户登录方法
	 * @param name
	 * @param password
	 * @return 返回用户对象
	 * @throws Exception 
	 */
	public UserInfo login(String serverIP, String name, String password) throws Exception {
		
		String path = String.format(serverIP + ServerIP.SERVLET_LOGIN, name, password);
		byte[] data = HttpUtil.getDataFromUrl(path);
		String jsonString = new String(data);
		Log.i("learning json", jsonString);
		String success = "";
		UserInfo userInfo = null;
		
		try {
			JSONObject item = new JSONObject(jsonString);
			success = item.getString("success");
			String msg = item.getString("msg");
			/*if("密码错误！".equals(msg)){
				UserInfo user = new UserInfo();
				user.setName(msg);
				return user;
						
			}*/

			if("true".equals(success)){
				String juser = item.getString("obj");
				userInfo = FastJsonTools.getObect(juser, UserInfo.class);
			} else {
				UserInfo user = new UserInfo();
				user.setId(-1L);
				user.setName(msg);
				return user;
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return userInfo;
	}
	
	/**
	 * 获得通知信息
	 * @param serverIP服务地址
	 * @return
	 */
	public List<SysMessage> getAllSysMessageList(String serverIP, int page, int rows, Long classId) {
		int last_id = this.getLastSysMessageId(classId);
		getNewSysMessageList(serverIP, last_id, page, rows, classId);
		//remove();
		
		return getAllSysMessageFromDB();
	}
	
	
	
	
	/**
	 * 获得课程表集合
	 * @param classId
	 * @return
	 */
	public List<SysMessage> getNewSysMessageList(String serverIP, int last_message_id, int page, int rows, Long classId) {
		
		Log.i("last_message_id json==", "last_message_id json="+last_message_id);
		
		String path = String.format(serverIP+ServerIP.SERVLET_MESSAGE, page, rows, classId);
	//	String path = serverIP  + ServerIP.SERVLET_MESSAGE;
		Log.i("SysMessage path==", path);
		
		byte[] data = HttpUtil.getDataFromUrl(path);
		
		String jsonString = new String(data);
		Log.i("SysMessage json==", jsonString);
		
//		List<SysMessage> list = FastJsonTools.getObects(jsonString, SysMessage.class);
//		return list;
		JSONArray jsonArray = null;
		try {
			JSONObject item1 = new JSONObject(jsonString);
			String obj = item1.getString("rows");
			jsonArray = new JSONArray(obj);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		List<SysMessage> sysMessages = new ArrayList<SysMessage>();
		
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject item = null;
			try {
				item = jsonArray.getJSONObject(i);

				SysMessage sysMessage = new SysMessage();

				sysMessage.set_id(item.getInt("id"));
				sysMessage.setName(item.getString("name"));
				sysMessage.setContent(item.getString("content"));
				sysMessage.setTime(item.getString("createdTime"));
				sysMessage.setCreatorName(item.getString("creator"));
				sysMessage.setIsRead(0);
				sysMessage.setClass_id(item.getInt("class_id"));
				
				if (sysMessage.get_id() > last_message_id) {
					this.Insert(sysMessage);
				}
				sysMessages.add(sysMessage);

			} catch (JSONException e) {
				e.printStackTrace();
			}

		}
		
		return getAllSysMessageFromDB();
	}
	
	
	/**
	 * 获得课程表集合
	 * @param classId
	 * @return
	 */
	public List<SysMessage> getRefreshSysMessageList(String serverIP, int page, int rows, Long classId) {
		
		String path = String.format(serverIP+ServerIP.SERVLET_MESSAGE, page, rows, classId);
//		String path = serverIP + ServerIP.SERVLET_MESSAGE;
		Log.i("SysMessage path==", path);
		
		byte[] data = HttpUtil.getDataFromUrl(path);
		
		String jsonString = new String(data);
		Log.i("SysMessage json==", jsonString);
		
		JSONArray jsonArray = null;
		try {
			JSONObject item1 = new JSONObject(jsonString);
			String obj = item1.getString("rows");
			jsonArray = new JSONArray(obj);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		List<SysMessage> sysMessages = new ArrayList<SysMessage>();
		
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject item = null;
			try {
				item = jsonArray.getJSONObject(i);

				SysMessage sysMessage = new SysMessage();

				sysMessage.set_id(item.getInt("id"));
				sysMessage.setName(item.getString("name"));
				sysMessage.setContent(item.getString("content"));
				sysMessage.setTime(item.getString("createdTime"));
				
				if("1".equals(item.getString("organGrade")) ){
					sysMessage.setIsRead(item.getInt("organGrade"));
				} else {
					sysMessage.setIsRead(0);
				}

				sysMessages.add(sysMessage);

			} catch (JSONException e) {
				e.printStackTrace();
			}

		}
		
		Log.i("sysMessages.size()","sysMessages.size()==="+sysMessages.size() );
		
		if(sysMessages != null){
			this.remove();
		}
		
		for(SysMessage s : sysMessages){
			this.Insert(s);
		}
		
		return getAllSysMessageFromDB();
	}
	
	
	
	/**
	 * 插入通知对象
	 * @param sysMessage
	 */
	private void Insert(SysMessage sysMessage) {

		Log.i("sysMessage.getName()","Insert.getName()==="+sysMessage.getName() );
		
		ContentValues values = new ContentValues();
		values.put(SYSMESSAGE.FIELDS._ID, sysMessage.get_id());
		values.put(SYSMESSAGE.FIELDS.NAME, sysMessage.getName());
		values.put(SYSMESSAGE.FIELDS._CONTENT, sysMessage.getContent());
		values.put(SYSMESSAGE.FIELDS.TIME, sysMessage.getTime());
		values.put(SYSMESSAGE.FIELDS.ISREAD, sysMessage.getIsRead());
		values.put(SYSMESSAGE.FIELDS.CREATOR, sysMessage.getCreatorName());
		
		Log.i("sysMessage.getName()","sysMessage.getClass_id()==="+sysMessage.getClass_id() );
		values.put(SYSMESSAGE.FIELDS.CLASS_ID, sysMessage.getClass_id());
		
		this.helper.insert(SYSMESSAGE.TABLENAME, values);

	}
	
	
	/**
	 * 插入通知对象
	 * @param sysMessage
	 */
	private void remove() {

		this.helper.ExecuteSQL(SYSMESSAGE.SQL.DELETE);

	}
	
	
	/**
	 * 获得最后值
	 * @return
	 */
	public int getLastSysMessageId(Long class_id) {
		String sql = "select _id from sysmessage order by _id desc";
		int last_id = 0;
		try {
			Cursor cursor = helper.SELECT(sql);
			if (cursor.moveToFirst()) {
				last_id = cursor.getInt(cursor
						.getColumnIndex(SYSMESSAGE.FIELDS._ID));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		Log.i("sq", "sysmessagedddcccc last_id=" + last_id);
		return last_id;
	}
	
	
	/**
	 * 本地获得通知消息
	 * @return
	 */
	public List<SysMessage> getAllSysMessageFromDB() {
		String condition = " 1=1 order by id limit 20";

		String sql = String.format(DB.TABLES.SYSMESSAGE.SQL.SELECT, condition);
		List<SysMessage> sysMessages = new ArrayList<SysMessage>();
		
		try {
			Cursor cursor = helper.SELECT(sql);
			while (cursor.moveToNext()) {
				SysMessage sysMessage = new SysMessage();
				
				sysMessage.set_id(cursor.getInt(cursor.getColumnIndex(SYSMESSAGE.FIELDS._ID)));
				sysMessage.setName(cursor.getString(cursor.getColumnIndex(SYSMESSAGE.FIELDS.NAME)));
				sysMessage.setContent(cursor.getString(cursor.getColumnIndex(SYSMESSAGE.FIELDS._CONTENT)));
				sysMessage.setIsRead(cursor.getInt(cursor.getColumnIndex(SYSMESSAGE.FIELDS.ISREAD)));
				sysMessage.setTime(cursor.getString(cursor.getColumnIndex(SYSMESSAGE.FIELDS.TIME)));
				sysMessage.setCreatorName(cursor.getString(cursor.getColumnIndex(SYSMESSAGE.FIELDS.CREATOR)));
				
				sysMessages.add(sysMessage);
				
			}
			cursor.close();
			return sysMessages;
		} catch (Exception e) {
			return null;
		} finally {
			helper.closeDataBase();
		}
	}
	
	
	/**
	 * 更新
	 * @param sysMessage
	 */
	public void updataSysMessage(String serverIP, SysMessage sysMessage) throws Exception{
		String sql = String.format(DB.TABLES.SYSMESSAGE.SQL.UPDATE,sysMessage.getIsRead(), sysMessage.get_id());
		helper.ExecuteSQL(sql);
        
		updateData(serverIP, new Long(sysMessage.get_id()));
		
	}

	private boolean updateData(String serverIP, Long messageId) throws Exception{
		String path = String.format(serverIP+ ServerIP.SERVLET_UPDATE_MESSAGE, messageId);
		byte[] data = HttpUtil.getDataFromUrl(path);
		String jsonString = new String(data);
		String success = "";
		
		try {
			JSONObject item = new JSONObject(jsonString);
			success = item.getString("success");
			
			if("true".equals(success)){
				return true; 
			} else {
				throw new Exception("添加错误！"); 
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 用户插入
	 * @param user
	 */
	public void insert(User user) {

		ContentValues values = new ContentValues();
		values.put(USERINFO.FIELDS.USER_ID, user.getUser_id());
		values.put(USERINFO.FIELDS.USER_NAME, user.getUser_name());
		values.put(USERINFO.FIELDS.USER_PASSWORD, user.getUser_password());
		values.put(USERINFO.FIELDS.CLASS_ID, user.getClassId());
		
		this.helper.insert(USERINFO.TABLENAME, values);
	} 
	
	/**
	 * 本用户
	 * @return
	 */
	public List<User> getAllUserFromLocal() {
		//String condition = " 1=1 order by id limit 20";

		// String sql = String.format(DB.TABLES.USERINFO.SQL.SELECT_ALL, condition);
		 String sql =  DB.TABLES.USERINFO.SQL.SELECT_ALL;
		 List<User> users  = new ArrayList<User>();
	        try {
	        	Cursor cursor = helper.SELECT(sql);
	        		
					while(cursor.moveToNext()){
						User user = new User();
						user.setUser_id(cursor.getString(cursor.getColumnIndex(USERINFO.FIELDS.USER_ID)));
						user.setUser_name(cursor.getString(cursor.getColumnIndex(USERINFO.FIELDS.USER_NAME)));
						user.setUser_password(cursor.getString(cursor.getColumnIndex(USERINFO.FIELDS.USER_PASSWORD)));
						user.setClassId(cursor.getLong(cursor.getColumnIndex(USERINFO.FIELDS.CLASS_ID)));
						users.add(user);
					}
				
				cursor.close();
				return users;
			} catch (SQLException ex) {
				return null;
			}finally
			{
				helper.closeDataBase();
			}
	}
	
	/**
	 * 获得记录数
	 * @param condtion
	 * @return
	 */
	public int getUserCount(String condtion){
		String sql_count = MessageFormat.format(DB.TABLES.USERINFO.SQL.SELECT_COUNT,condtion);
		return (int) helper.rawQuerySingle(sql_count);  //总条数
	}

	@Override
	public void deleteAllUser() {
		helper.ExecuteSQL(DB.TABLES.USERINFO.SQL.DELETE);
	}
	
	
	
	
}
