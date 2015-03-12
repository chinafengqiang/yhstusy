
package com.smartlearning.db;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * 用于保存考卷简报的类
 * <p>
 * 	主要利用SharedPreferences进行数据保存。SharedPreferences是以Key-Value（键值对）的方式
 * 	把数据保存到/data/data/本应用的包名/shared_prefs里。本应用所保存的数据量很小，所以不
 * 	需要用到数据库进行保存。
 * </p>
 * @version 1.1
 * 		<ul>
 * 			<li>1.0 创建</li>
 * 			<li>1.1 把全部方法修改为静态方法</li>
 *      </ul>
 */
public class DBAccess {
	
	private final static String profileDatabase = "androidexam_profile_database";
	/**
	 * 如果这些顺序被修改，请修改{@link updateProfile}内的顺序。
	 */
	private final static int scoreIndex  = 0;
	private final static int checkIndex  = 1;
	private final static int matrixIndex  = 2;
	private static Map<String, String> map = new HashMap<String, String>();
	
	/**
	 * 查询ID对应的简报信息是否存在
	 * @param id 
	 * 		Long型数据，记录在考卷中，唯一标识考卷的数值串
	 * @return
	 * 		如果考卷简报信息存在，则返回true，否则返回false。
	 */
	public static boolean profileExist(Context context,String id){
		SharedPreferences sp = context.getSharedPreferences(profileDatabase, Activity.MODE_PRIVATE);
		String data = sp.getString(id, null);
		Log.i("DBAccess", "DBAccess profileExist data="+data);
		if(data != null){
			return true;
		} else{
			return false;
		}
		
	//	return sp.getString(id, null) == null ? false : true;
	}
	
	/**
	 * 根据指定的考卷ID，读取考卷的成绩
	 * @param id 考卷ID
	 * @return 考卷成绩
	 */
	public static int getScore(Context context,String id){
		int score = 0;
		SharedPreferences sp = context.getSharedPreferences(profileDatabase, Activity.MODE_PRIVATE);
		String data = sp.getString(id, null);
		if(data != null){
			score = Integer.valueOf(data.split(",")[scoreIndex]);
		}
		return score;
	}
	
	/**
	 * 读取考卷用时
	 * @param id
	 * @return
	 */
	public static String getAllObj(Context context,String id){
		SharedPreferences sp = context.getSharedPreferences(profileDatabase, Activity.MODE_PRIVATE);
		String data = sp.getString(id, null);
		
		return data;
	}
	
	/**
	 * 取得考卷对错矩阵
	 * @param id
	 * @return
	 */
	public static boolean[] getRwMatrix(Context context,String id){
		SharedPreferences sp = context.getSharedPreferences(profileDatabase, Activity.MODE_PRIVATE);
		String data = sp.getString(id, null);
		if(data != null){
			char[] cMatrix = data.split(",")[matrixIndex].toCharArray();
			boolean[] rwMatrix = new boolean[cMatrix.length];
			for(int i=0;i<cMatrix.length;i++){
				rwMatrix[i] = cMatrix[i] == 'T' ? true : false;
			}
			return rwMatrix;
		}
		return null;
	}
	
	/**
	 * 取得考卷对错矩阵
	 * @param id
	 * @return
	 */
	public static int getCountMatrix(Context context,String id){
		SharedPreferences sp = context.getSharedPreferences(profileDatabase, Activity.MODE_PRIVATE);
		String data = sp.getString(id, null);
		int count = 0;
		if(data != null){
			char[] cMatrix = data.split(",")[matrixIndex].toCharArray();
			for(int i=0;i<cMatrix.length;i++){
				if(cMatrix[i] == 'T'){
					count ++;
				}
			}
			return count;
		}
		return 0;
	}
	
	/**
	 * 取得选中
	 * @param id
	 * @return
	 */
	public static String[] getCheckMatrix(Context context,String id){
		SharedPreferences sp = context.getSharedPreferences(profileDatabase, Activity.MODE_PRIVATE);
		String data = sp.getString(id, null);
		if(data != null){
			String cMatrix = data.split(",")[checkIndex];
			String[] str2 = cMatrix.split(";");
			return str2;
		}
		return null;
	}
	
	/**
	 * 取得选中
	 * @param id
	 * @return
	 */
	public static String[] getListMatrix(Context context,String id){
//		SharedPreferences sp = context.getSharedPreferences(profileDatabase, Activity.MODE_PRIVATE);
//		String data = sp.getString(id, null);
		String data = map.get(id);
		Log.i("DBAccess", "DBAccess getListMatrix data="+data);
		if(data != null){
			String cMatrix = data.split(",")[checkIndex];
			String[] str2 = cMatrix.split(";");
			return str2;
		}
		return null;
	}
	
	
	/**
	 * 取得选中
	 * @param id
	 * @return
	 */
	public static char[] getCheckMatrixs(Context context,String id, int location){
		SharedPreferences sp = context.getSharedPreferences(profileDatabase, Activity.MODE_PRIVATE);
		String data = sp.getString(id, null);
		if(data != null){
			String cMatrix = data.split(",")[checkIndex];
			String str2 = cMatrix.split(";")[location];
			char[] c = str2.toCharArray();
			return c;
		}
		return null;
	}
	
	/**
	 * 创建空的Profile
	 * @param context
	 * @param id
	 * @param subjectCount
	 */
	public static void createProfile(Context context,String id, int subjectCount){
		Log.i("DBAccess", "DBAccess createProfile subjectCount="+subjectCount);
		final boolean[] matrix = new boolean[subjectCount];
		final String [] checkVallue = new String[subjectCount];
		for(int i = 0; i < matrix.length; i++) matrix[i] = false;
		for(int i = 0; i < checkVallue.length; i++) checkVallue[i] = "9999";
		updateProfile(context,id,0,checkVallue,matrix);
	}
	
	/**
	 * 更新Profile
	 * @param id
	 * @param score
	 * @param time
	 * @param matrix
	 */
	private static void updateProfile(Context context,String id, int score,String [] checkVallue, boolean[] matrix){
		StringBuffer buffer = new StringBuffer("");
		buffer.append(String.valueOf(score));
		buffer.append(",");
		for(String  item : checkVallue){
			buffer.append(item + ";");
		}
		buffer.append(",");
		for(boolean item : matrix){
			buffer.append(item ? 'T' : 'F');
		}
		Log.i("DBAccess", "DBAccess updateProfile 初始化="+buffer.toString());

		SharedPreferences sp = context.getSharedPreferences(profileDatabase, Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString(id, buffer.toString());
		editor.commit();
	}
	
	/**
	 * 更新Profile
	 * @param id
	 * @param score
	 * @param time
	 * @param matrix
	 */
	private static void updateProfiles(Context context,String id, int score,String [] checkVallue, char[] cValue, boolean[] matrix){
		
		
		StringBuffer buffer = new StringBuffer("");
		buffer.append(String.valueOf(score));
		buffer.append(",");
		for(String  item : checkVallue){
			Log.i("DBAccess", "DBAccess updateProfiles 初始checkVallue="+item);
			buffer.append(item + ";");
		}
	 
		buffer.append(";");
		buffer.append(",");
		for(boolean item : matrix){
			buffer.append(item ? 'T' : 'F');
		}
		Log.i("DBAccess", "DBAccess updateProfile 初始化多选择="+buffer.toString());
		SharedPreferences sp = context.getSharedPreferences(profileDatabase, Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString(id, buffer.toString());
		editor.commit();
	}
	
	/**
	 * 更新指定位置的对错矩阵
	 * @param id
	 * @param score
	 * @param time
	 * @param location
	 * @param rOrw
	 */
	public static void updateProfile(Context context,String id,int score, String cValue, int location, boolean rOrw){
		Log.i("DBAccess", "DBAccess location="+location);
		final boolean[] matrix = getRwMatrix(context,id);
		final String[] checkMatrix = getCheckMatrix(context,id);
		for(int i = 0; i < checkMatrix.length; i++){
			Log.i("DBAccess", "DBAccess updateProfile="+checkMatrix[i]);
		}
		matrix[location - 1] = rOrw;
		checkMatrix[location -1] = cValue;
		updateProfile(context,id,score,checkMatrix ,matrix);
		
	}
	
	/**
	 * 更新指定位置的对错矩阵
	 * @param id
	 * @param score
	 * @param time
	 * @param location
	 * @param rOrw
	 */
	public static void updateProfile(Context context,String id,int score, char cValue, int location, boolean rOrw, int postion){
		Log.i("DBAccess", "DBAccess location="+location);
		final boolean[] matrix = getRwMatrix(context,id);
		final String[] checkMatrix = getCheckMatrix(context,id);
		final char[] cMatrix = getCheckMatrixs(context,id,location);
		
		matrix[location - 1] = rOrw;	
		char[] str2 = checkMatrix[location -1].toCharArray();
		str2[postion] = cValue;
		String str3 = "";
		for( char s : str2){
			str3 += String.valueOf(s);
		}
		checkMatrix[location -1] = str3;
		cMatrix[postion] = cValue;
		
		updateProfiles(context,id,score,checkMatrix, cMatrix ,matrix);
	}
	
	/**
	 * 删除ID所在记录
	 * @param id
	 */
	public static void deleteProfile(Context context,String id){
		SharedPreferences sp = context.getSharedPreferences(profileDatabase, Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.remove(id);
		editor.commit();
	}
}
