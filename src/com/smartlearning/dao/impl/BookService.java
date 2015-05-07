package com.smartlearning.dao.impl;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.feng.tree.TreeElementBean;
import com.feng.util.LocalBooResDB;
import com.feng.vo.BookCategory;
import com.feng.vo.BookPart;
import com.feng.vo.BookRes;
import com.feng.vo.VideoRes;
import com.smartlearning.common.HttpUtil;
import com.smartlearning.constant.ServerIP;
import com.smartlearning.dao.IBook;
import com.smartlearning.db.DB;
import com.smartlearning.db.DB.TABLES.BOOKCHAPTER;
import com.smartlearning.db.DB.TABLES.BOOKPART;
import com.smartlearning.db.DB.TABLES.EBOOK;
import com.smartlearning.db.DB.TABLES.EVIDEOS;
import com.smartlearning.db.DBHelper;
import com.smartlearning.model.Advise;
import com.smartlearning.model.Book;
import com.smartlearning.model.BookCategoryVo;
import com.smartlearning.model.EBook;
import com.smartlearning.model.EVideo;
import com.smartlearning.model.Note;
import com.smartlearning.model.OnlineForum;
import com.smartlearning.model.PageInfo;
import com.smartlearning.model.SysMessage;
import com.smartlearning.utils.FastJsonTools;
import com.smartlearning.utils.ImageService;
import com.smartlearning.utils.ImageTools;

/**
 * 电子书实现类
 * @author Administrator
 */
public class BookService implements IBook{

	private DBHelper helper = null;

	public BookService(Context context) {
		helper = new DBHelper(context);
	}
	
	
	/**
	 * 获得电子书
	 * @param serverIP服务地址
	 * @return
	 */
	public List<EBook> getAllBookList(String serverIP, int classId) throws Exception{
		int last_id = this.getLastBookId(classId);
		Log.i("learning serverIP", "last_id=="+last_id);
		getBookByPages(serverIP, last_id, classId);
		return getAllEBookDB();
	}
	
	/**
	 * 获得电子书集合
	 * @param page 默认1,  rows 默认10
	 * @return
	 * @throws Exception 
	 */
	private void getBookByPages(String serverIP, int lastBookId, int classId) throws Exception {
		
		String path = String.format(serverIP + ServerIP.SERVLET_BOOK, classId, 1, 100000000);
		Log.i("learning serverIP", "path=="+path);
		byte[] data = HttpUtil.getDataFromUrl(path);
		String jsonString = new String(data);
		if(jsonString == null || jsonString.equals("")){
			throw new Exception();
		}
		Log.i("learning json", jsonString);
		String obj = "";
		JSONObject item;
		try {
			item = new JSONObject(jsonString);
			obj = item.getString("rows");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		List<Book> books = FastJsonTools.getObects(obj, Book.class);
		
		List<EBook> eBooks = new ArrayList<EBook>();
		
		for(Book b : books){
			EBook ebook = new EBook();
			
			ebook.set_id(Integer.parseInt(b.getId().toString()));
			ebook.setTime(b.getCreatedTime());
			ebook.setName(b.getName());
			ebook.setPdfUrl(b.getUrl());
	//		ebook.setImageUrl(b.getPic());
			ebook.setCategoryName(b.getCategoryName());
			ebook.setClass_id(0);
			
			String imageUrl = serverIP + b.getPic();
			Log.i("geBookByPage", "imageUrl="+imageUrl);
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
			ebook.setImageUrl(result);
			
//			Log.i("getBookByPages", "getBookByPages===="+ebook.get_id());
//			Log.i("getBookByPages", "getBookByPages===="+ebook.getName());
			
			//插入
			if (ebook.get_id() > lastBookId) {
				
				this.Insert(ebook);
			}
			
			eBooks.add(ebook);
			
		}
		
		//return eBooks;
	}
	
	/**
	 * 获得电子书集合
	 * @param page 默认1,  rows 默认10
	 * @return
	 * @throws Exception 
	 */
	public List<EBook> geBookByPage(String serverIP, int lastBookId, int classId) throws Exception {
		
		String path = String.format(serverIP + ServerIP.SERVLET_BOOK, classId, 1, 100000000);
		byte[] data = HttpUtil.getDataFromUrl(path);
		String jsonString = new String(data);
		if(jsonString == null || jsonString.equals("")){
			throw new Exception();
		}
		Log.i("learning json", jsonString);
		String obj = "";
		JSONObject item;
		try {
			item = new JSONObject(jsonString);
			obj = item.getString("rows");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		List<Book> books = FastJsonTools.getObects(obj, Book.class);
		
		List<EBook> eBooks = new ArrayList<EBook>();
		
		for(Book b : books){
			EBook ebook = new EBook();
			
			ebook.set_id(Integer.parseInt(b.getId().toString()));
			ebook.setTime(b.getCreatedTime());
			ebook.setName(b.getName());
			ebook.setPdfUrl(b.getUrl());
	//		ebook.setImageUrl(b.getPic());
			ebook.setCategoryName(b.getCategoryName());
			ebook.setClass_id(0);
			
			String imageUrl = serverIP + b.getPic();
			Log.i("geBookByPage", "imageUrl="+imageUrl);
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
			ebook.setImageUrl(result);
			
			//插入
			if (ebook.get_id() > lastBookId) {
				this.Insert(ebook);
			}
			
			eBooks.add(ebook);
			
		}
		
		return eBooks;
	}
	
	
	/**
	 * 获得记事本
	 * @return
	 */
	public List<Note> queryNoteAll(String serverIP,Long userId) {
		
		String path = String.format(serverIP + ServerIP.SERVLET_NOTE, userId);
		byte[] data = HttpUtil.getDataFromUrl(path);
		String jsonString = new String(data);
		Log.i("learning json==", jsonString);
		
		List<Note> notes = FastJsonTools.getObects(jsonString, Note.class);
		
		return notes;
	}
	
	/**
	 * 创建记事本
	 * @param note
	 * @throws Exception 
	 */
	public void createNote(String serverIP,Long userId, String note) throws Exception{
		String path = serverIP + ServerIP.SERVLET_CREATE_NOTE;
		String params = "";
		String success = "";
		try {
			params = "userId="+userId+"&note="+URLEncoder.encode(note, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		byte[] data = HttpUtil.getDataFromUrl(path,params);
		String jsonString = new String(data);
		Log.i("learning json==", jsonString);
		
		try {
			JSONObject item = new JSONObject(jsonString);
			success = item.getString("success");
			
			if("true".equals(success)){
				throw new Exception("添加成功！"); 
			} else {
				throw new Exception("用户和密码错误！"); 
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 获得电子书集合
	 * @param page 默认1,  rows 默认10
	 * @return
	 */
	public List<Book> getHomeworkByall(String serverIP, int page, int rows) {
		
		String path = String.format(serverIP + ServerIP.SERVLET_HOMEWORK, page, rows);
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
		
		List<Book> books = FastJsonTools.getObects(obj, Book.class);
		
		return books;
	}
	
	
	/**
	 * 获得在线交流集合
	 * @param page 默认1,  rows 默认10
	 * @return
	 */
	public List<OnlineForum> getOnlineForumByPage(String serverIP,int page, int rows, Long classId) {
		
		String path = String.format(serverIP + ServerIP.SERVLET_FORUM, page, rows, classId);
		byte[] data = HttpUtil.getDataFromUrl(path);
		String jsonString = new String(data);
		Log.i("Forum json", jsonString);
		String obj = "";
		JSONObject item;
		try {
			item = new JSONObject(jsonString);
			obj = item.getString("rows");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		List<OnlineForum> forums = FastJsonTools.getObects(obj, OnlineForum.class);
		
		return forums;
	}
	
	/**
	 * 添加
	 * @param advise
	 * @throws Exception
	 */
	public boolean  AddAdvise(String serverIp, Advise advise) throws Exception {
		
		String question = URLEncoder.encode(advise.getQuestion(), "UTF-8");
		String url =String.format(serverIp + ServerIP.SERVLET_ADDADVISE, advise.getRootId(), question, advise.getId()) ;
		Log.i("AddAdvise url", url);
		byte[] data = HttpUtil.getDataFromUrl(url);
		String success = "";
		if (data!=null){
			String jsonString = new String(data);
			Log.i("AddAdvise json", jsonString);
			JSONObject item = new JSONObject(jsonString);
			success = item.getString("success");
			
			if("true".equals(success)){
				return true; 
			} else {
				throw new Exception("添加错误！"); 
			}
			
		}
		return false;
	}
	
	/**
	 * 获得在线交流集合
	 * @param page 默认1,  rows 默认10
	 * @return
	 */
	public List<OnlineForum> getChildForumByPage(String serverIP,int id, int page, int rows) {
		
		String path = String.format(serverIP + ServerIP.SERVLET_CHILD, id, page, rows);
		Log.i("Forum path", path);
		byte[] data = HttpUtil.getDataFromUrl(path);
		String jsonString = new String(data);
		Log.i("Forum json", jsonString);
		String obj = "";
		JSONObject item;
		try {
			item = new JSONObject(jsonString);
			obj = item.getString("rows");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		List<OnlineForum> forums = FastJsonTools.getObects(obj, OnlineForum.class);
		
		return forums;
	}
	
	
	/**
	 * 获得电子书集合
	 * @param page 默认1,  rows 默认10
	 * @return
	 */
	public List<Book> geBookByPage(String serverIP, int classId, int page, int rows, Integer categoryId) {
		
		String path = String.format(serverIP + ServerIP.SERVLET_BOOK_CATEGORY, classId, page, rows, categoryId);
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
		
		List<Book> books = FastJsonTools.getObects(obj, Book.class);
		
		return books;
	}
	
	
	/**
	 * 获得资源内容
	 * @param serverIP
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public Book queryBookById(String serverIP, int id) throws Exception {
		
		String path = String.format(serverIP + ServerIP.SERVLET_GETBOOK, id);
		byte[] data = HttpUtil.getDataFromUrl(path);
		String jsonString = new String(data);
		Log.i("learning json", jsonString);
		String success = "";
		Book book = null;
		
		try {
			JSONObject item = new JSONObject(jsonString);
			success = item.getString("success");
			
			if("true".equals(success)){
				String objBook = item.getString("obj");
				book = FastJsonTools.getObect(objBook, Book.class);
				
				Log.i("learning book name====", book.getName());
				
			} else {
				throw new Exception("无数据！"); 
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return book;
	}
	
	
	/**
	 * 插入电子书
	 * @param book
	 */
	public void Insert(EBook ebook) {

		ContentValues values = new ContentValues();
		values.put(EBOOK.FIELDS._ID, ebook.get_id());
		values.put(EBOOK.FIELDS.NAME, ebook.getName());
		values.put(EBOOK.FIELDS.TIME, ebook.getTime());
		values.put(EBOOK.FIELDS.PDF_URL, ebook.getPdfUrl());
		values.put(EBOOK.FIELDS.IMAGE_URL, ebook.getImageUrl());
		values.put(EBOOK.FIELDS.CATEGORY_NAME, ebook.getCategoryName());
		values.put(EBOOK.FIELDS.CLASS_ID, ebook.getClass_id());
		
		this.helper.insert(EBOOK.TABLENAME, values);

	}
	
	private void Insert(List<EBook> ebooks) {
		ContentValues values = null;
		List<ContentValues> contentList = null;
		if(ebooks != null && ebooks.size() > 0){
			contentList = new ArrayList<ContentValues>();
			for(EBook ebook : ebooks ){
				values = new ContentValues();
				values.put(EBOOK.FIELDS._ID, ebook.get_id());
				values.put(EBOOK.FIELDS.NAME, ebook.getName());
				values.put(EBOOK.FIELDS.TIME, ebook.getTime());
				values.put(EBOOK.FIELDS.PDF_URL, ebook.getPdfUrl());
				values.put(EBOOK.FIELDS.IMAGE_URL, ebook.getImageUrl());
				values.put(EBOOK.FIELDS.CATEGORY_NAME, ebook.getCategoryName());
				values.put(EBOOK.FIELDS.CLASS_ID, ebook.getClass_id());
				contentList.add(values);
			}
		}
		if(contentList != null)
			this.helper.insertBacth(EBOOK.TABLENAME, contentList);
	}

	
	/**
	 * 获得最后值
	 * @return
	 */
	public int getLastBookId(int class_id) {
		String sql = "select _id from ebook order by _id desc";
		int last_id = 0;
		try {
			Cursor cursor = helper.SELECT(sql);
			if (cursor.moveToFirst()) {
				last_id = cursor.getInt(cursor.getColumnIndex(EBOOK.FIELDS._ID));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return last_id;
	}
	
	/**
	 * 本地电子书
	 * @return
	 */
	public List<EBook> getAllEBookDB() {
		//String condition = " 1=1 order by _id";
		//String sql = String.format(DB.TABLES.EBOOK.SQL.SELECT_ALL, condition);
		String sql = DB.TABLES.EBOOK.SQL.SELECT_ALL;
		List<EBook> eBooks = new ArrayList<EBook>();
		
		try {
			Cursor cursor = helper.SELECT(sql);
			while (cursor.moveToNext()) {
				EBook ebook = new EBook();
				
				ebook.set_id(cursor.getInt(cursor.getColumnIndex(EBOOK.FIELDS._ID)));
				ebook.setName(cursor.getString(cursor.getColumnIndex(EBOOK.FIELDS.NAME)));
				ebook.setTime(cursor.getString(cursor.getColumnIndex(EBOOK.FIELDS.TIME)));
				ebook.setPdfUrl(cursor.getString(cursor.getColumnIndex(EBOOK.FIELDS.PDF_URL)));
				ebook.setImageUrl(cursor.getBlob(cursor.getColumnIndex(EBOOK.FIELDS.IMAGE_URL)));
				ebook.setCategoryName(cursor.getString(cursor.getColumnIndex(EBOOK.FIELDS.CATEGORY_NAME)));
				ebook.setClass_id(cursor.getInt(cursor.getColumnIndex(EBOOK.FIELDS.CLASS_ID)));
				
				eBooks.add(ebook);
				
			}
			cursor.close();
			return eBooks;
		} catch (Exception e) {
			return null;
		} finally {
			helper.closeDataBase();
		}
	}
	
	
	
	
	@Override
	public List<EBook> getUserEBookDB(long userId) {
				String condition = " class_id = "+userId+" order by _id";
				String sql = String.format(DB.TABLES.EBOOK.SQL.SELECT1, condition);
				List<EBook> eBooks = new ArrayList<EBook>();
				
				try {
					Cursor cursor = helper.SELECT(sql);
					while (cursor.moveToNext()) {
						EBook ebook = new EBook();
						
						ebook.set_id(cursor.getInt(cursor.getColumnIndex(EBOOK.FIELDS._ID)));
						ebook.setName(cursor.getString(cursor.getColumnIndex(EBOOK.FIELDS.NAME)));
						ebook.setTime(cursor.getString(cursor.getColumnIndex(EBOOK.FIELDS.TIME)));
						ebook.setPdfUrl(cursor.getString(cursor.getColumnIndex(EBOOK.FIELDS.PDF_URL)));
						ebook.setImageUrl(cursor.getBlob(cursor.getColumnIndex(EBOOK.FIELDS.IMAGE_URL)));
						ebook.setCategoryName(cursor.getString(cursor.getColumnIndex(EBOOK.FIELDS.CATEGORY_NAME)));
						ebook.setClass_id(cursor.getInt(cursor.getColumnIndex(EBOOK.FIELDS.CLASS_ID)));
						
						eBooks.add(ebook);
						
					}
					cursor.close();
					return eBooks;
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
	public List<EBook> getEBooksBySQL(String sql) {
		
		//String condition = " 1=1 order by time";
		//String sql = String.format(DB.TABLES.EBOOK.SQL.SELECT, condition);
		List<EBook> eBooks = new ArrayList<EBook>();
		
		try {
			Cursor cursor = helper.SELECT(sql);
			while (cursor.moveToNext()) {
				EBook ebook = new EBook();
				
				ebook.set_id(cursor.getInt(cursor.getColumnIndex(EBOOK.FIELDS._ID)));
				ebook.setName(cursor.getString(cursor.getColumnIndex(EBOOK.FIELDS.NAME)));
				ebook.setTime(cursor.getString(cursor.getColumnIndex(EBOOK.FIELDS.TIME)));
				ebook.setPdfUrl(cursor.getString(cursor.getColumnIndex(EBOOK.FIELDS.PDF_URL)));
				ebook.setImageUrl(cursor.getBlob(cursor.getColumnIndex(EBOOK.FIELDS.IMAGE_URL)));
				ebook.setCategoryName(cursor.getString(cursor.getColumnIndex(EBOOK.FIELDS.CATEGORY_NAME)));
				ebook.setClass_id(cursor.getInt(cursor.getColumnIndex(EBOOK.FIELDS.CLASS_ID)));
				
				eBooks.add(ebook);
				
			}
			cursor.close();
			return eBooks;
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
		String sql_count = MessageFormat.format(DB.TABLES.EBOOK.SQL.SELECT_COUNT,condtion);
		
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
	
	public List<EBook> getByPager(int pageIndex, int pageSize,String condtion) {
		
		int pageCount = this.getByPageCount(pageSize,condtion);
		
		if (pageIndex>pageCount-1)//说明是最后一页
		{
			return null;
		}
		
		int startIndex = pageIndex*pageSize;
		String condition = condtion+ " limit "+startIndex+","+pageSize;
		
		String sql = MessageFormat.format(DB.TABLES.EBOOK.SQL.SELECT, condition);
		
		Log.i("EBook", "sql=="+sql);
		
		return this.getEBooksBySQL(sql);
		
	}
	
	/**
	 * 删除
	 * @param id
	 */
	public void removeEbook(int id) throws Exception{
		String condition = "_id ="+id;
		String sql = String.format(DB.TABLES.EBOOK.SQL.DELETE, condition);
		helper.ExecuteSQL(sql);
		
	}
	
	
	@Override
	public void removeVideo(int id) throws Exception {
		String condition = "_id ="+id;
		String sql = String.format(DB.TABLES.EVIDEOS.SQL.DELETE, condition);
		helper.ExecuteSQL(sql);
	}


	public void removeAllEBook(){
		String sql = DB.TABLES.EBOOK.SQL.DELETE_ALL;
		helper.ExecuteSQL(sql);
	}
	

	@Override
	public List<EBook> getPermBooks(String serverIP,int userId,long classId) throws Exception{
		String path = String.format(serverIP + ServerIP.SERVLET_GET_BOOK, classId);
		byte[] data = HttpUtil.getDataFromUrl(path);
		if(data == null)
			throw new Exception("serverError");
		List<EBook> eBooks = new ArrayList<EBook>();
		String jsonString = new String(data);
		JSONObject item = new JSONObject(jsonString);
		int code = item.getInt("code");
		if(code == 200){
			String info = item.getString("info");
			List<Book> books = FastJsonTools.getObects(info, Book.class);
			EBook ebook = null;
			for(Book b : books){
				ebook = new EBook();
				ebook.set_id(Integer.parseInt(b.getId().toString()));
				ebook.setTime(b.getCreatedTime());
				ebook.setName(b.getName());
				ebook.setPdfUrl(b.getUrl());
				ebook.setCategoryName(b.getCategoryName());
				ebook.setClass_id(userId);
				
				String image = b.getPic();
				byte[] result = null;
				if(image == null || "".equals(image) || "/uploadFile/pic/jcjffm.JPG".equals(image)||"/uploadFile/pic/jcjffm.jpg".equals(image)){
					
				}else{
					String imageUrl = serverIP + b.getPic();
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
				ebook.setImageUrl(result);

				eBooks.add(ebook);
			}
			
			try {
				
				if(eBooks != null){
					this.removeAllEBook();//清除本地电子书
					
					this.Insert(eBooks);
				}

			} catch (Exception e) {
				Log.e("opt-local-ebook",e.getLocalizedMessage());
			}
		}
		
		
		return eBooks;
	}


	@Override
	public List<EBook> getPermBooks(String serverIP, int userId, long classId,
			long category,PageInfo pageInfo) throws Exception {
		String path = String.format(serverIP + ServerIP.SERVLET_GET_BOOK, classId,category,pageInfo.getBeginResult(),pageInfo.getPageSize());
		byte[] data = HttpUtil.getDataFromUrl(path);
		if(data == null)
			throw new Exception("serverError");
		List<EBook> eBooks = new ArrayList<EBook>();
		String jsonString = new String(data);
		JSONObject item = new JSONObject(jsonString);
		int code = item.getInt("code");
		if(code == 200){
			String info = item.getString("info");
			int totalSize = item.getInt("totalSize");
			pageInfo.setTotalResult(totalSize);
			List<Book> books = FastJsonTools.getObects(info, Book.class);
			EBook ebook = null;
			for(Book b : books){
				ebook = new EBook();
				ebook.set_id(Integer.parseInt(b.getId().toString()));
				ebook.setTime(b.getCreatedTime());
				ebook.setName(b.getName());
				ebook.setPdfUrl(b.getUrl());
				ebook.setCategoryName(b.getCategoryName());
				ebook.setClass_id(userId);
				
				String image = b.getPic();
				byte[] result = null;
				if(image == null || "".equals(image) || "/uploadFile/pic/jcjffm.JPG".equals(image)||"/uploadFile/pic/jcjffm.jpg".equals(image)){
					
				}else{
					String imageUrl = serverIP + b.getPic();
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
				ebook.setImageUrl(result);

				eBooks.add(ebook);
			}
			
		}
	
		return eBooks;
	}


	@Override
	public List<EBook> getByPager(String condtion, PageInfo pageInfo) {
		String sql_count = MessageFormat.format(DB.TABLES.EBOOK.SQL.SELECT_COUNT,condtion);
		int totalCount = (int) helper.rawQuerySingle(sql_count);  //总条数
		pageInfo.setTotalResult(totalCount);
		String condition = condtion+ " limit "+pageInfo.getBeginResult()+","+pageInfo.getPageSize();
		String sql = MessageFormat.format(DB.TABLES.EBOOK.SQL.SELECT, condition);
		return this.getEBooksBySQL(sql);
	}

static HashMap<String,Integer> categoryMap = new HashMap<String, Integer>();
static {
	categoryMap.put("语文",53);
	categoryMap.put("数学",60);
	categoryMap.put("地理",61);
	categoryMap.put("英语",62);
	categoryMap.put("作业",63);
	categoryMap.put("物理",64);
	categoryMap.put("历史",66);
	categoryMap.put("政治",67);
	categoryMap.put("化学",68);
	categoryMap.put("生物",69);
	categoryMap.put("电子白板",77);
	categoryMap.put("补充",78);
}

public List<BookCategoryVo> getEBooksCategoryBySQL() {
		String sql = DB.TABLES.EBOOK.SQL.GET_CATEGORY;
		List<BookCategoryVo> list = new ArrayList<BookCategoryVo>();
		try {
			Cursor cursor = helper.SELECT(sql);
			BookCategoryVo vo = null;
			while (cursor.moveToNext()) {
				vo = new BookCategoryVo();
				vo.categoryName = cursor.getString(cursor.getColumnIndex(EBOOK.FIELDS.CATEGORY_NAME));
				Integer id = categoryMap.get(vo.categoryName);
				if(id != null){
					vo.categoryId = id;
				}
				list.add(vo);
				
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
	public void insertBook(int userId,BookRes book) {
		
		String allIds = book.getAllIds();
		String allNames = book.getAllNames();
		BookCategory category = new BookCategory();
		BookPart part = new BookPart();
		List<TreeElementBean> chapterList = new ArrayList<>();
		
		LocalBooResDB.getLocalBookInfo(allIds, allNames, category, part, chapterList);
		
		insertPart(userId,category,part);
		
		String resTag = insertChapter(part, chapterList);
		
		insertBookRes(userId,resTag,book);
		
		
	}
	
	
	
	@Override
	public void insertVideo(int userId, VideoRes video) {
		String allIds = video.getAllIds();
		String allNames = video.getAllNames();
		BookCategory category = new BookCategory();
		BookPart part = new BookPart();
		List<TreeElementBean> chapterList = new ArrayList<>();
		
		LocalBooResDB.getLocalBookInfo(allIds, allNames, category, part, chapterList);
		
		insertPart(userId,category,part);
		
		String resTag = insertChapter(part, chapterList);
		
		insertVideoRes(userId,resTag,video);
		
	}


	private void insertPart(int userId,BookCategory category,BookPart part){
		int partId = part.getId();
		String sql = MessageFormat.format(DB.TABLES.BOOKPART.SQL.SELECT_ONLY_ID,DB.TABLES.BOOKPART.FIELDS._ID+" = "+partId+" and "+DB.TABLES.BOOKPART.FIELDS.USER_ID+" = "+userId);
		int result = this.helper.getSelectInt(sql);
		if(result  == partId)
			return;
		ContentValues values = new ContentValues();
		values.put(BOOKPART.FIELDS._ID, part.getId());
		values.put(BOOKPART.FIELDS.NAME,part.getName());
		values.put(BOOKPART.FIELDS.CATEGORY_ID,category.getId());
		values.put(BOOKPART.FIELDS.CATEGORY_NAME,category.getName());
		values.put(BOOKPART.FIELDS.USER_ID,userId);
		this.helper.insert(BOOKPART.TABLENAME, values);
	}
	
	private String insertChapter(BookPart part,List<TreeElementBean> chapterList){
		List<ContentValues> valuesList = new ArrayList<>(chapterList.size());
		ContentValues values = null;
		String chapterTag = "";
		String cteTag = "";
		
		for(TreeElementBean ele : chapterList){
			values = new ContentValues();
			int chapterId = Integer.parseInt(ele.getId());
			
			int canAddRes = ele.getIsAddRes();
			if(canAddRes == 1){
				chapterTag = ele.getId();
			}
			if(canAddRes == 2){
				cteTag = ele.getId();
				continue;
			}
			
	
			String sql = MessageFormat.format(DB.TABLES.BOOKCHAPTER.SQL.SELECT_ONLY_ID,DB.TABLES.BOOKCHAPTER.FIELDS._ID+" = "+chapterId);
			int result = this.helper.getSelectInt(sql);
			if(result  == chapterId)
				continue;
			values.put(BOOKCHAPTER.FIELDS._ID,ele.getId());
			values.put(BOOKCHAPTER.FIELDS.NAME,ele.getNodeName());
			values.put(BOOKCHAPTER.FIELDS.PID,ele.getUpNodeId());
			values.put(BOOKCHAPTER.FIELDS.PART_ID,part.getId());
			values.put(BOOKCHAPTER.FIELDS.PART_NAME,part.getName());
			values.put(BOOKCHAPTER.FIELDS.CAN_ADD_RES,canAddRes);
			valuesList.add(values);
		}
		this.helper.insertBacth(BOOKCHAPTER.TABLENAME, valuesList);
		return chapterTag+"#"+cteTag;
	}

	
	private void insertBookRes(int userId,String tag,BookRes book){
		ContentValues values = new ContentValues();
		values.put(EBOOK.FIELDS._ID,book.getResId());
		values.put(EBOOK.FIELDS.NAME, book.getResName());
		values.put(EBOOK.FIELDS.TIME, book.getResCreateTime());
		values.put(EBOOK.FIELDS.PDF_URL,book.getResUrl());
		
		values.put(EBOOK.FIELDS.CATEGORY_NAME, tag);
		values.put(EBOOK.FIELDS.CLASS_ID, userId);
		this.helper.insert(EBOOK.TABLENAME, values);
	}
	
	private void insertVideoRes(int userId,String tag,VideoRes video){
		ContentValues values = new ContentValues();
		values.put(EVIDEOS.FIELDS._ID,video.getResId());
		values.put(EVIDEOS.FIELDS.NAME, video.getResName());
		values.put(EVIDEOS.FIELDS.TIME, video.getResCreateTime());
		values.put(EVIDEOS.FIELDS.VIDEO_URL,video.getResUrl());
		
		values.put(EVIDEOS.FIELDS.CATEGORY_NAME, tag);
		values.put(EVIDEOS.FIELDS.HOUR, userId);
		this.helper.insert(EVIDEOS.TABLENAME, values);
	}

	@Override
	public List<BookRes> getUserBookDB(int userId) {
		String condition = " class_id = "+userId+" order by _id";
		String sql = String.format(DB.TABLES.EBOOK.SQL.SELECT_NEW, condition);
		List<BookRes> bookList = new ArrayList<BookRes>();
		
		try {
			Cursor cursor = helper.SELECT(sql);
			while (cursor.moveToNext()) {
				BookRes book = new BookRes();
				
				book.setResId(cursor.getInt(cursor.getColumnIndex(EBOOK.FIELDS._ID)));
				book.setResName(cursor.getString(cursor.getColumnIndex(EBOOK.FIELDS.NAME)));
				book.setResCreateTime(cursor.getString(cursor.getColumnIndex(EBOOK.FIELDS.TIME)));
				book.setResUrl(cursor.getString(cursor.getColumnIndex(EBOOK.FIELDS.PDF_URL)));
				book.setAllNames(cursor.getString(cursor.getColumnIndex(EBOOK.FIELDS.CATEGORY_NAME)));
				
				bookList.add(book);
				
			}
			cursor.close();
			return bookList;
		} catch (Exception e) {
			return null;
		} finally {
			helper.closeDataBase();
		}
	}
	

	public List<BookCategory> getBookCategory(int userId){
		String sql = String.format(DB.TABLES.BOOKPART.SQL.SELECT_CATEGORY,DB.TABLES.BOOKPART.FIELDS.USER_ID+" = "+userId);
		List<BookCategory> cList = new ArrayList<BookCategory>();
		Cursor cursor = null;
		try {
    		cursor = helper.SELECT(sql);
			BookCategory category = null;
			while (cursor.moveToNext()) {
				category = new BookCategory();
				
				category.setId(cursor.getInt(cursor.getColumnIndex(BOOKPART.FIELDS.CATEGORY_ID)));;
				category.setName(cursor.getString(cursor.getColumnIndex(BOOKPART.FIELDS.CATEGORY_NAME)));
				
				cList.add(category);
				
			}
			return cList;
		} catch (Exception e) {
			Log.e("getCategory",e.getMessage()+"::"+e.getLocalizedMessage());
			return null;
		} finally {
			if(cursor != null)
				cursor.close();
			helper.closeDataBase();
		}
	}


	@Override
	public List<BookPart> getBookPart(int categoryId) {
		String sql = String.format(DB.TABLES.BOOKPART.SQL.SELECT_PART,DB.TABLES.BOOKPART.FIELDS.CATEGORY_ID+" = "+categoryId);
		List<BookPart> resList = new ArrayList<BookPart>();
		Cursor cursor = null;
		try {
    		cursor = helper.SELECT(sql);
    		BookPart part = null;
			while (cursor.moveToNext()) {
				part = new BookPart();
				part.setId(cursor.getInt(cursor.getColumnIndex(BOOKPART.FIELDS._ID)));;
				part.setName(cursor.getString(cursor.getColumnIndex(BOOKPART.FIELDS.NAME)));
				part.setCategoryId(cursor.getInt(cursor.getColumnIndex(BOOKPART.FIELDS.CATEGORY_ID)));
				resList.add(part);
			}
			
			return resList;
		} catch (Exception e) {
			Log.e("getCategory",e.getMessage()+"::"+e.getLocalizedMessage());
			return null;
		} finally {
			if(cursor != null)
				cursor.close();
			helper.closeDataBase();
		}
	}


	@Override
	public List<TreeElementBean> getBookChapterTree(int partId, int pid) {
		String sql = String.format(DB.TABLES.BOOKCHAPTER.SQL.SELECT1,DB.TABLES.BOOKCHAPTER.FIELDS.PART_ID+" = "+partId+" and "+DB.TABLES.BOOKCHAPTER.FIELDS.PID+" = "+pid);
		List<TreeElementBean> resList = new ArrayList<TreeElementBean>();
		Cursor cursor = null;
		boolean hasParent = true;
		if(pid == 0)
			hasParent = false;
		boolean hasChild = true;
		try {
    		cursor = helper.SELECT(sql);
    		TreeElementBean ele = null;
			while (cursor.moveToNext()) {
				ele = new TreeElementBean();
				ele.setId(cursor.getInt(cursor.getColumnIndex(BOOKCHAPTER.FIELDS._ID))+"");
				ele.setNodeName(cursor.getString(cursor.getColumnIndex(BOOKCHAPTER.FIELDS.NAME)));
				ele.setExpanded(false);
				ele.setLevel(0);
				ele.setHasChild(hasChild);
				ele.setHasParent(hasParent);
				ele.setIsAddRes(cursor.getInt(cursor.getColumnIndex(BOOKCHAPTER.FIELDS.CAN_ADD_RES)));
				ele.setUpNodeId(cursor.getInt(cursor.getColumnIndex(BOOKCHAPTER.FIELDS.PID))+"");
				resList.add(ele);
			}
			
			return resList;
		} catch (Exception e) {
			Log.e("getCategory",e.getMessage()+"::"+e.getLocalizedMessage());
			return null;
		} finally {
			if(cursor != null)
				cursor.close();
			helper.closeDataBase();
		}
	}


	@Override
	public List<BookRes> getBookRes(int userId, String resTag) {
		String sql = String.format(DB.TABLES.EBOOK.SQL.SELECT_BOOK_RES,DB.TABLES.EBOOK.FIELDS.CLASS_ID+" = "+userId+" and "+DB.TABLES.EBOOK.FIELDS.CATEGORY_NAME+" = '"+resTag+"'");
		List<BookRes> resList = new ArrayList<BookRes>();
		Cursor cursor = null;
		try {
    		cursor = helper.SELECT(sql);
    		BookRes res = null;
			while (cursor.moveToNext()) {
				res = new BookRes();
				res.setLocalFile(true);
				res.setResCreateTime(cursor.getString(cursor.getColumnIndex(DB.TABLES.EBOOK.FIELDS.TIME)));
				res.setResId(cursor.getInt(cursor.getColumnIndex(DB.TABLES.EBOOK.FIELDS._ID)));
				res.setResName(cursor.getString(cursor.getColumnIndex(DB.TABLES.EBOOK.FIELDS.NAME)));
				res.setResUrl(cursor.getString(cursor.getColumnIndex(DB.TABLES.EBOOK.FIELDS.PDF_URL)));
				resList.add(res);
			}
			
			return resList;
		} catch (Exception e) {
			Log.e("getCategory",e.getMessage()+"::"+e.getLocalizedMessage());
			return null;
		} finally {
			if(cursor != null)
				cursor.close();
			helper.closeDataBase();
		}
	}


	@Override
	public List<VideoRes> getVideoRes(int userId, String resTag) {
		String sql = String.format(DB.TABLES.EVIDEOS.SQL.SELECT_VIDEO_RES,DB.TABLES.EVIDEOS.FIELDS.CATEGORY_NAME+" = '"+resTag+"'");
		List<VideoRes> resList = new ArrayList<VideoRes>();
		Cursor cursor = null;
		try {
    		cursor = helper.SELECT(sql);
    		VideoRes res = null;
			while (cursor.moveToNext()) {
				res = new VideoRes();
				res.setLocalFile(true);
				res.setResCreateTime(cursor.getString(cursor.getColumnIndex(DB.TABLES.EVIDEOS.FIELDS.TIME)));
				res.setResId(cursor.getInt(cursor.getColumnIndex(DB.TABLES.EVIDEOS.FIELDS._ID)));
				res.setResName(cursor.getString(cursor.getColumnIndex(DB.TABLES.EVIDEOS.FIELDS.NAME)));
				res.setResUrl(cursor.getString(cursor.getColumnIndex(DB.TABLES.EVIDEOS.FIELDS.VIDEO_URL)));
				res.setResLectuer(cursor.getString(cursor.getColumnIndex(DB.TABLES.EVIDEOS.FIELDS.LECTUER)));
				resList.add(res);
			}
			
			return resList;
		} catch (Exception e) {
			Log.e("getCategory",e.getMessage()+"::"+e.getLocalizedMessage());
			return null;
		} finally {
			if(cursor != null)
				cursor.close();
			helper.closeDataBase();
		}
	}


	@Override
	public List<BookRes> searchBookRes(String value) {
		String sql = String.format(DB.TABLES.EBOOK.SQL.SELECT_BOOK_RES,DB.TABLES.EBOOK.FIELDS.NAME+" like '%"+value+"%'");
		List<BookRes> resList = new ArrayList<BookRes>();
		Cursor cursor = null;
		try {
    		cursor = helper.SELECT(sql);
    		BookRes res = null;
			while (cursor.moveToNext()) {
				res = new BookRes();
				res.setLocalFile(true);
				res.setResCreateTime(cursor.getString(cursor.getColumnIndex(DB.TABLES.EBOOK.FIELDS.TIME)));
				res.setResId(cursor.getInt(cursor.getColumnIndex(DB.TABLES.EBOOK.FIELDS._ID)));
				res.setResName(cursor.getString(cursor.getColumnIndex(DB.TABLES.EBOOK.FIELDS.NAME)));
				res.setResUrl(cursor.getString(cursor.getColumnIndex(DB.TABLES.EBOOK.FIELDS.PDF_URL)));
				resList.add(res);
			}
			
			return resList;
		} catch (Exception e) {
			Log.e("getCategory",e.getMessage()+"::"+e.getLocalizedMessage());
			return null;
		} finally {
			if(cursor != null)
				cursor.close();
			helper.closeDataBase();
		}
	}
	
	
	
	
	
	
	
}
