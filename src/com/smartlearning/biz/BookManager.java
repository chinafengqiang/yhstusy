package com.smartlearning.biz;

import java.util.List;

import android.content.Context;
import android.util.Log;

import com.feng.tree.TreeElementBean;
import com.feng.vo.BookCategory;
import com.feng.vo.BookPart;
import com.feng.vo.BookRes;
import com.feng.vo.VideoRes;
import com.smartlearning.dao.IBook;
import com.smartlearning.dao.impl.BookService;
import com.smartlearning.model.Advise;
import com.smartlearning.model.Book;
import com.smartlearning.model.BookCategoryVo;
import com.smartlearning.model.EBook;
import com.smartlearning.model.Note;
import com.smartlearning.model.OnlineForum;
import com.smartlearning.model.PageInfo;


/**
 * 电子书集合管理
 * @author Administrator
 *
 */
public class BookManager {
	public static final String TAG = "BookManager";
	IBook bookService ;
	
	/**
	 *初始化对象 
	 */
	public BookManager(Context context) {
		bookService = new BookService(context);
	}
	
	/**
	 * 获得电子书集合
	 * @param page 默认1,  rows 默认10
	 * @return
	 * @throws Exception 
	 */
	public List<EBook> geBookByPage(String serverIP, int lastBookId, int classId) throws Exception {
		return bookService.geBookByPage(serverIP, lastBookId, classId);
	}
	
	/**
	 * 获得记事本
	 * @return
	 */
	public List<Note> queryNoteAll(String serverIP,Long userId) {
		return bookService.queryNoteAll(serverIP,userId);
	}
	
	/**
	 * 创建记事本
	 * @param note
	 * @throws Exception 
	 */
	public void createNote(String serverIP,Long userId, String note) throws Exception{
		bookService.createNote(serverIP,userId, note);
	}
	
	/**
	 * 获得电子书集合
	 * @param page 默认1,  rows 默认10
	 * @return
	 */
	public List<Book> getHomeworkByall(String serverIP,int page, int rows) {
		return bookService.getHomeworkByall(serverIP,page, rows);
	}
	
	/**
	 * 获得在线交流集合
	 * @param page 默认1,  rows 默认10
	 * @return
	 */
	public List<OnlineForum> getOnlineForumByPage(String serverIP,int page, int rows, Long classId) {
		return bookService.getOnlineForumByPage(serverIP, page, rows, classId);
	}
	
	/**
	 * 添加
	 * @param advise
	 * @throws Exception
	 */
	public boolean  AddAdvise(String serverIp, Advise advise) throws Exception {
		return bookService.AddAdvise(serverIp, advise);
	}
	
	/**
	 * 获得在线交流集合
	 * @param page 默认1,  rows 默认10
	 * @return
	 */
	public List<OnlineForum> getChildForumByPage(String serverIP,int id, int page, int rows) {
		return bookService.getChildForumByPage(serverIP, id, page, rows);
	}
	
	/**
	 * 获得电子书集合
	 * @param page 默认1,  rows 默认10
	 * @return
	 */
	public List<Book> geBookByPage(String serverIP, int classId, int page, int rows, Integer categoryId) {
		return bookService.geBookByPage(serverIP, classId, page, rows, categoryId);
	}
	
	/**
	 * 获得资源内容
	 * @param serverIP
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public Book queryBookById(String serverIP, int id) throws Exception {
		return bookService.queryBookById(serverIP, id);
	}
	
	/**
	 * 本地电子书
	 * @return
	 */
	public List<EBook> getAllEBookDB() {
		return bookService.getAllEBookDB();
	}
	
	/**
	 * 获得电子书
	 * @param serverIP服务地址
	 * @return
	 */
	public List<EBook> getAllBookList(String serverIP, int classId) throws Exception{
		return bookService.getAllBookList(serverIP, classId);
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
		return bookService.getByPager(pageIndex, pageSize, condtion);
	}
	
	
	
	/**
	 * 得到用于分页的数据源的总条数，一般要配合getByPager()方法共同使用 
	 * @param pageSize
	 * @param condtion 1=1 代表无条件
	 * @return
	 */
	public int getByPageCount(int pageSize,String condtion){
		return bookService.getByPageCount(pageSize, condtion);
	}
	
	/**
	 * 删除
	 * @param id
	 */
	public void removeEbook(int id) throws Exception{
		bookService.removeEbook(id);
	}
	
	
	public void removeVideo(int id) throws Exception{
		bookService.removeVideo(id);
	}
	
	public void insertEbook(EBook ebook){
		bookService.Insert(ebook);
	}
	
	
	public  List<EBook> getPermBooks(String serverIP,int userId,long classId){
		
		List<EBook> resList = null;
		try {
			resList = bookService.getPermBooks(serverIP, userId, classId);
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG, e.getLocalizedMessage());
			
			resList = getUserEBookDB(userId);
		}
		return resList;

	}
	
	public  List<EBook> getPermBooks(String serverIP,int userId,long classId,long category,PageInfo pageInfo){
		
		List<EBook> resList = null;
		try {
			resList = bookService.getPermBooks(serverIP, userId, classId,category,pageInfo);
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG, e.getLocalizedMessage());
		}
		return resList;

	}
	
	public List<EBook> getUserEBookDB(long userId) {
		return bookService.getUserEBookDB(userId);
	}
	
	/**
	 * 分页存取数据
	 * params:
	 * condtion: 如果无条件 condition 传入 1=1
	 * pageIndex:代表是第几页(首页从0开始)
	 * pageSize:代表每页多少条记录
	 * return:返回的就是分页之后的数据
	 */
	
	public List<EBook> getByPager(String condtion,PageInfo pageInfo) {
		return bookService.getByPager(condtion, pageInfo);
	}
	
	public List<BookCategoryVo> getEBooksCategory(){
		return bookService.getEBooksCategoryBySQL();
	}
	
	
	public void insertBook(int userId,BookRes book){
		bookService.insertBook(userId, book);
	}
	
	public void insertVideo(int userId,VideoRes video){
		bookService.insertVideo(userId, video);
	}
	
	public List<BookRes> getBookResList(int userId){
		return bookService.getUserBookDB(userId);
	}
	
	public List<BookCategory> getBookCategory(int userId){
		return bookService.getBookCategory(userId);
	}
	
	public List<BookPart> getBookPart(int categoryId){
		return bookService.getBookPart(categoryId);
	}
	
	public List<TreeElementBean> getBookChapterTree(int partId,int pid){
		return bookService.getBookChapterTree(partId, pid);
	}
	
	public List<BookRes> getBookRes(int userId,String resTag){
		return bookService.getBookRes(userId, resTag);
	}
	
	public List<VideoRes> getVideoRes(int userId,String resTag){
		return bookService.getVideoRes(userId, resTag);
	}
	
	public List<BookRes> searchBookRes(String value){
		return bookService.searchBookRes(value);
	}
}
