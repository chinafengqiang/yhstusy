package com.smartlearning.dao.impl;

import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.smartlearning.common.HttpUtil;
import com.smartlearning.constant.ServerIP;
import com.smartlearning.dao.IAdviod;
import com.smartlearning.db.DB;
import com.smartlearning.db.DB.TABLES.EBOOK;
import com.smartlearning.db.DB.TABLES.EVIDEOS;
import com.smartlearning.db.DBHelper;
import com.smartlearning.model.Adviod;
import com.smartlearning.model.CoursePlan;
import com.smartlearning.model.CoursewareNode;
import com.smartlearning.model.EBook;
import com.smartlearning.model.EVideo;
import com.smartlearning.model.Video;
import com.smartlearning.utils.DateUtil;
import com.smartlearning.utils.FastJsonTools;
import com.smartlearning.utils.ImageService;
import com.smartlearning.utils.ImageTools;

/**
 * 视频实现类
 * @author Administrator
 */
public class AdviodService implements IAdviod{

	private DBHelper helper = null;

	public AdviodService(Context context) {
		helper = new DBHelper(context);
	}
	
	/**
	 * 获得视频集合
	 * @param page 默认1,  rows 默认10
	 * @return
	 */
	public List<Adviod> geAdviodByPage(String serverIP,int page, int rows) {
		
		String path = String.format(serverIP + ServerIP.SERVLET_ADVIOD, page, rows);
		byte[] data = HttpUtil.getDataFromUrl(path);
		String jsonString = new String(data);
		Log.i("learning json", jsonString);
		String obj = "";
		JSONObject item;
		try {
			item = new JSONObject(jsonString);
			obj = item.getString("rows");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		List<Adviod> adviods = FastJsonTools.getObects(obj, Adviod.class);
		
		return adviods;
	}
	
	
	/**
	 * 获得电子书
	 * @param serverIP服务地址
	 * @return
	 */
	public List<EVideo> getAllEVideoList(String serverIP, int classId) throws Exception{
		int last_id = this.getLastVideoId(classId);
		Log.i("learning serverIP", "last_id=="+last_id);
		getVideosByAll(serverIP, classId, last_id);
		return getAllEVideoDB();
	}
	
	/**
	 * 获得视频集合
	 * @param 
	 * @return
	 */
	private List<Video> getVideosByAll(String serverIP, int classId, int lastId) {
		
		String path = String.format(serverIP + ServerIP.SERVLET_VIDEO, classId);
		byte[] data = HttpUtil.getDataFromUrl(path);
		String jsonString = new String(data);
		Log.i("learning getVideosByAll==", jsonString);
		
		List<Video> adviods = FastJsonTools.getObects(jsonString, Video.class);
		
		List<EVideo> list = new ArrayList<EVideo>();
		
		for(Video video : adviods){
			
			EVideo eVideo = new EVideo();
			eVideo.set_id(Integer.parseInt(video.getId().toString()));
			eVideo.setName(video.getName());
			eVideo.setTime(DateUtil.dateToString(video.getCreatedTime(), false));
			eVideo.setCategoryName(video.getCategoryName());
			eVideo.setVideoUrl(video.getUrl());
			
			String imageUrl = serverIP + video.getPic();
			Log.i("getVideosByAll", "imageUrl="+imageUrl);
			byte[] result = null;
			try {
				InputStream is  = ImageService.getInputStream(imageUrl);
				Bitmap  bitmap = BitmapFactory.decodeStream(is);
				if(bitmap != null){
					result = ImageTools.getByteFromBitmap(bitmap);
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			eVideo.setImageUrl(result);
			
			eVideo.setLectuer(video.getLectuer());
			eVideo.setHour(video.getHour());
			eVideo.setDescription(video.getDescription());
			eVideo.setTeacherDescription(video.getTeacherDescription());
			
			//插入
			if (eVideo.get_id() > lastId) {
				this.Insert(eVideo);
			}
			
			list.add(eVideo);
			
		}
		
		
		return adviods;
	}
	
	/**
	 * 获得视频编号
	 * @param 
	 * @return
	 */
	public Video getVideosById(String serverIP,int id) {
		
		String path = String.format(serverIP + ServerIP.SERVLET_VIDEOID,id);
		Log.i("learning path==", path);
		byte[] data = HttpUtil.getDataFromUrl(path);
		String jsonString = new String(data);
		Log.i("learning json", jsonString);
		
		Video adviods = FastJsonTools.getObect(jsonString, Video.class);
		
		return adviods;
	}
	
	/**
	 * 获得课程教学计划
	 * @param 
	 * @return
	 */
	public List<CoursePlan> getPlanByAll(String serverIP) {
		
		String path = serverIP + ServerIP.SERVLET_PLAN;
		byte[] data = HttpUtil.getDataFromUrl(path);
		String jsonString = new String(data);
		Log.i("learning json==", jsonString);
		
		List<CoursePlan> adviods = FastJsonTools.getObects(jsonString, CoursePlan.class);
		
		return adviods;
	}
	
	
	
	@Override
	public List<CoursePlan> getPlanByAll(String serverIP, long gradeId) {
		String path = serverIP + ServerIP.SERVLET_GET_PLAN+"gradeId="+gradeId;
		byte[] data = HttpUtil.getDataFromUrl(path);
		if(data != null){
			String jsonString = new String(data);
			Log.i("learning json==", jsonString);
			
			List<CoursePlan> adviods = FastJsonTools.getObects(jsonString, CoursePlan.class);
			
			return adviods;
		}
		return null;
	}

	/**
	 * 获得课程分类
	 * @param 
	 * @return
	 * @throws Exception 
	 */
	public List<CoursewareNode> getCategoryByAll(String serverIP) {
		
		String path = serverIP + ServerIP.SERVLET_CATEGORY;
		byte[] data = HttpUtil.getDataFromUrl(path);
		String jsonString = new String(data);
		Log.i("learning json==", jsonString);
		
		List<CoursewareNode> category = FastJsonTools.getObects(jsonString, CoursewareNode.class);
		
		return category;
	}
	
	/**
	 * 插入视频
	 * @param book
	 */
	private void Insert(EVideo eVideo) {

		ContentValues values = new ContentValues();
		values.put(EVIDEOS.FIELDS._ID, eVideo.get_id());
		values.put(EVIDEOS.FIELDS.NAME, eVideo.getName());
		values.put(EVIDEOS.FIELDS.TIME, eVideo.getTime());
		values.put(EVIDEOS.FIELDS.VIDEO_URL, eVideo.getVideoUrl());
		values.put(EVIDEOS.FIELDS.IMAGE_URL, eVideo.getImageUrl());
		values.put(EVIDEOS.FIELDS.CATEGORY_NAME, eVideo.getCategoryName());
		values.put(EVIDEOS.FIELDS.LECTUER, eVideo.getLectuer());
		values.put(EVIDEOS.FIELDS.HOUR, eVideo.getHour());
		values.put(EVIDEOS.FIELDS.DESCRIPTION, eVideo.getDescription());
		values.put(EVIDEOS.FIELDS.TEACHERDESCRIPTION, eVideo.getTeacherDescription());
		values.put(EVIDEOS.FIELDS.VOIDE_SIZE, 0);
		
		this.helper.insert(EVIDEOS.TABLENAME, values);

	}
	
	/**
	 * 获得最后值
	 * @return
	 */
	public int getLastVideoId(int class_id) {
		String sql = "select _id from evideos order by _id desc";
		int last_id = 0;
		try {
			Cursor cursor = helper.SELECT(sql);
			if (cursor.moveToFirst()) {
				last_id = cursor.getInt(cursor.getColumnIndex(EVIDEOS.FIELDS._ID));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return last_id;
	}
	
	/**
	 * 本地视频
	 * @return
	 */
	public List<EVideo> getAllEVideoDB() {
		String condition = " 1=1 order by _id";

		String sql = String.format(DB.TABLES.EVIDEOS.SQL.SELECT1, condition);
		List<EVideo> list = new ArrayList<EVideo>();
		
		try {
			Cursor cursor = helper.SELECT(sql);
			while (cursor.moveToNext()) {
				EVideo eVideo = new EVideo();
				
				eVideo.set_id(cursor.getInt(cursor.getColumnIndex(EVIDEOS.FIELDS._ID)));
				eVideo.setName(cursor.getString(cursor.getColumnIndex(EVIDEOS.FIELDS.NAME)));
				eVideo.setTime(cursor.getString(cursor.getColumnIndex(EVIDEOS.FIELDS.TIME)));
				eVideo.setVideoUrl(cursor.getString(cursor.getColumnIndex(EVIDEOS.FIELDS.VIDEO_URL)));
				eVideo.setImageUrl(cursor.getBlob(cursor.getColumnIndex(EVIDEOS.FIELDS.IMAGE_URL)));
				eVideo.setCategoryName(cursor.getString(cursor.getColumnIndex(EVIDEOS.FIELDS.CATEGORY_NAME)));
				eVideo.setLectuer(cursor.getString(cursor.getColumnIndex(EVIDEOS.FIELDS.LECTUER)));
				eVideo.setHour(cursor.getInt(cursor.getColumnIndex(EVIDEOS.FIELDS.HOUR)));
				eVideo.setDescription(cursor.getString(cursor.getColumnIndex(EVIDEOS.FIELDS.DESCRIPTION)));
				eVideo.setTeacherDescription(cursor.getString(cursor.getColumnIndex(EVIDEOS.FIELDS.TEACHERDESCRIPTION)));
				list.add(eVideo);
				
			}
			cursor.close();
			return list;
		} catch (Exception e) {
			return null;
		} finally {
			helper.closeDataBase();
		}
	}
	
	/**
	 * 本地电子书
	 * @return
	 */
	public List<EVideo> getEVideosBySQL(String sql) {
		
		//String condition = " 1=1 order by time";
		//String sql = String.format(DB.TABLES.EBOOK.SQL.SELECT, condition);
		List<EVideo> list = new ArrayList<EVideo>();
		
		try {
			Cursor cursor = helper.SELECT(sql);
			while (cursor.moveToNext()) {
				EVideo eVideo = new EVideo();
				
				eVideo.set_id(cursor.getInt(cursor.getColumnIndex(EVIDEOS.FIELDS._ID)));
				eVideo.setName(cursor.getString(cursor.getColumnIndex(EVIDEOS.FIELDS.NAME)));
				eVideo.setTime(cursor.getString(cursor.getColumnIndex(EVIDEOS.FIELDS.TIME)));
				eVideo.setVideoUrl(cursor.getString(cursor.getColumnIndex(EVIDEOS.FIELDS.VIDEO_URL)));
				eVideo.setImageUrl(cursor.getBlob(cursor.getColumnIndex(EVIDEOS.FIELDS.IMAGE_URL)));
				eVideo.setCategoryName(cursor.getString(cursor.getColumnIndex(EVIDEOS.FIELDS.CATEGORY_NAME)));
				eVideo.setLectuer(cursor.getString(cursor.getColumnIndex(EVIDEOS.FIELDS.LECTUER)));
				eVideo.setHour(cursor.getInt(cursor.getColumnIndex(EVIDEOS.FIELDS.HOUR)));
				eVideo.setDescription(cursor.getString(cursor.getColumnIndex(EVIDEOS.FIELDS.DESCRIPTION)));
				eVideo.setTeacherDescription(cursor.getString(cursor.getColumnIndex(EVIDEOS.FIELDS.TEACHERDESCRIPTION)));
		//		eVideo.setVideoSize(cursor.getString(cursor.getColumnIndex(EVIDEOS.FIELDS.VOIDE_SIZE)));
				
				list.add(eVideo);
				
			}
			cursor.close();
			return list;
		} catch (Exception e) {
			return null;
		} finally {
			helper.closeDataBase();
		}
	}
	
	/**
	 * 得到用于分页的数据源的总条数，一般要配合getByPager()方法共同使用 
	 * @param pageSize
	 * @param condtion 1=1 代表无条件
	 * @return
	 */
	public int getByPageCount(int pageSize,String condtion){
		String sql_count = MessageFormat.format(DB.TABLES.EVIDEOS.SQL.SELECT_COUNT,condtion);
		
		int totalCount = (int) helper.rawQuerySingle(sql_count);  //总条数
		int pageCount=1; //总页数
		if (totalCount % pageSize==0){
			pageCount = totalCount/pageSize;
		}else{
			pageCount = (totalCount/pageSize)+1;
		}
		return pageCount;

	}
	
	/**
	 * 分页存取数据
	 * params:
	 * condtion: 如果无条件 condition 传入 1=1
	 * pageIndex:代表是第几页(首页从0开始)
	 * pageSize:代表每页多少条记录
	 * return:返回的就是分页之后的数据
	 */
	
	public List<EVideo> getByPager(int pageIndex, int pageSize,String condtion) {
		
		int pageCount = this.getByPageCount(pageSize,condtion);
		
		if (pageIndex>pageCount-1)//说明是最后一页
		{
			return null;
		}
		
		int startIndex = pageIndex*pageSize;
		String condition = condtion+ " limit "+startIndex+","+pageSize;
		
		String sql = MessageFormat.format(DB.TABLES.EVIDEOS.SQL.SELECT, condition);
		
		Log.i("EVIDEOS", "sql=="+sql);
		
		return this.getEVideosBySQL(sql);
		
	}
	
	/**
	 * 删除
	 * @param id
	 */
	public void removeEVideo(int id) throws Exception{
		String condition = "_id ="+id;
		String sql = String.format(DB.TABLES.EVIDEOS.SQL.DELETE, condition);
		helper.ExecuteSQL(sql);
		
	}
	
	/**
	 * 更新
	 * @param id
	 */
//	public void modifyEVideo(int id, int videoSize) throws Exception{
//		String condition = "_id ="+id;
//		String sql = String.format(DB.TABLES.EVIDEOS.SQL.UPDATE, condition);
//		helper.ExecuteSQL(sql);
//		
//	}
//	
	/**
	 * 更新
	 * @param modifyEVideo
	 */
	public void modifyEVideo(int id, String videoSize) throws Exception{
		String sql = String.format(DB.TABLES.EVIDEOS.SQL.UPDATE, "'"+videoSize+"'", id);
		Log.i("modifyEVideo json", sql);
		helper.ExecuteSQL(sql);
	}
	
	/**
	 * 本地视频
	 * @return
	 */
	public List<EVideo> getEVideoById(int id) {
		String condition = " _id="+id+" order by _id";

		String sql = String.format(DB.TABLES.EVIDEOS.SQL.SELECT1, condition);
		List<EVideo> list = new ArrayList<EVideo>();
		
		try {
			Cursor cursor = helper.SELECT(sql);
			while (cursor.moveToNext()) {
				EVideo eVideo = new EVideo();
				
				eVideo.set_id(cursor.getInt(cursor.getColumnIndex(EVIDEOS.FIELDS._ID)));
				eVideo.setName(cursor.getString(cursor.getColumnIndex(EVIDEOS.FIELDS.NAME)));
				eVideo.setTime(cursor.getString(cursor.getColumnIndex(EVIDEOS.FIELDS.TIME)));
				eVideo.setVideoUrl(cursor.getString(cursor.getColumnIndex(EVIDEOS.FIELDS.VIDEO_URL)));
				eVideo.setImageUrl(cursor.getBlob(cursor.getColumnIndex(EVIDEOS.FIELDS.IMAGE_URL)));
				eVideo.setCategoryName(cursor.getString(cursor.getColumnIndex(EVIDEOS.FIELDS.CATEGORY_NAME)));
				eVideo.setLectuer(cursor.getString(cursor.getColumnIndex(EVIDEOS.FIELDS.LECTUER)));
				eVideo.setHour(cursor.getInt(cursor.getColumnIndex(EVIDEOS.FIELDS.HOUR)));
				eVideo.setDescription(cursor.getString(cursor.getColumnIndex(EVIDEOS.FIELDS.DESCRIPTION)));
				eVideo.setTeacherDescription(cursor.getString(cursor.getColumnIndex(EVIDEOS.FIELDS.TEACHERDESCRIPTION)));
				eVideo.setVideoSize(cursor.getString(cursor.getColumnIndex(EVIDEOS.FIELDS.VOIDE_SIZE)));
				
				list.add(eVideo);
				
			}
			cursor.close();
			return list;
		} catch (Exception e) {
			return null;
		} finally {
			helper.closeDataBase();
		}
	}

	@Override
	public List<EVideo> getUserEVideoDB(long userId) {
		String condition = " hour = "+userId+" order by _id";
		String sql = String.format(DB.TABLES.EVIDEOS.SQL.SELECT1, condition);
		return getEVideosBySQL(sql);
	}

	@Override
	public List<EVideo> getPermVideos(String serverIP, int userId, long classId)
			throws Exception {
		String path = String.format(serverIP + ServerIP.SERVLET_GET_VIDEO, classId);
		byte[] data = HttpUtil.getDataFromUrl(path);
		if(data == null)
			throw new Exception("serverError");
		List<EVideo> evideos = new ArrayList<EVideo>();
		String jsonString = new String(data);
		JSONObject item = new JSONObject(jsonString);
		int code = item.getInt("code");
		if(code == 200){
			String info = item.getString("info");
			List<Video> adviods = FastJsonTools.getObects(info, Video.class);
			EVideo eVideo = null;
			for(Video video : adviods){
				eVideo = new EVideo();
				eVideo.setId(video.getId());
				eVideo.set_id(Integer.parseInt(video.getId().toString()));
				eVideo.setName(video.getName());
				eVideo.setTime(DateUtil.dateToString(video.getCreatedTime(), false));
				eVideo.setCategoryName(video.getCategoryName());
				eVideo.setVideoUrl(video.getUrl());
				
				String image =  video.getPic();
				byte[] result = null;
				if(image != null && !image.equals("")){
					String imageUrl = serverIP + video.getPic();
					try {
						InputStream is  = ImageService.getInputStream(imageUrl);
						Bitmap  bitmap = BitmapFactory.decodeStream(is);
						if(bitmap != null){
							result = ImageTools.getByteFromBitmap(bitmap);
						}
						
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
		
				
				eVideo.setImageUrl(result);
				
				eVideo.setLectuer(video.getLectuer());
				eVideo.setHour(userId);
				eVideo.setDescription(video.getDescription());
				eVideo.setTeacherDescription(video.getTeacherDescription());
				
				evideos.add(eVideo);
				
			}
			
			try {
				
				if(evideos != null){
					this.removeAllEVideo();//清除本地
					
					this.Insert(evideos);
				}

			} catch (Exception e) {
				Log.e("opt-local-evideo",e.getLocalizedMessage());
			}
		}
		
		
		return evideos;
	}
	
	
	public void removeAllEVideo(){
		String sql = DB.TABLES.EVIDEOS.SQL.DELETE_ALL;
		helper.ExecuteSQL(sql);
	}
	
	private void Insert(List<EVideo> eVideos) {
		ContentValues values = null;
		List<ContentValues> contentList = null;
		if(eVideos != null && eVideos.size() > 0){
			contentList = new ArrayList<ContentValues>();
			for(EVideo eVideo : eVideos ){
				values = new ContentValues();
				values.put(EVIDEOS.FIELDS._ID, eVideo.get_id());
				values.put(EVIDEOS.FIELDS.NAME, eVideo.getName());
				values.put(EVIDEOS.FIELDS.TIME, eVideo.getTime());
				values.put(EVIDEOS.FIELDS.VIDEO_URL, eVideo.getVideoUrl());
				values.put(EVIDEOS.FIELDS.IMAGE_URL, eVideo.getImageUrl());
				values.put(EVIDEOS.FIELDS.CATEGORY_NAME, eVideo.getCategoryName());
				values.put(EVIDEOS.FIELDS.LECTUER, eVideo.getLectuer());
				values.put(EVIDEOS.FIELDS.HOUR, eVideo.getHour());
				values.put(EVIDEOS.FIELDS.DESCRIPTION, eVideo.getDescription());
				values.put(EVIDEOS.FIELDS.TEACHERDESCRIPTION, eVideo.getTeacherDescription());
				values.put(EVIDEOS.FIELDS.VOIDE_SIZE, 0);
				contentList.add(values);
			}
		}
		if(contentList != null)
			this.helper.insertBacth(EVIDEOS.TABLENAME, contentList);
	}
	
	
}
