package com.smartlearning.dao;

import java.util.HashMap;
import java.util.List;

import com.feng.tree.TreeElementBean;
import com.feng.vo.BookCategory;
import com.feng.vo.BookChapterListVO;
import com.feng.vo.BookLocalVO;
import com.feng.vo.BookPart;
import com.feng.vo.BookRes;
import com.feng.vo.VideoRes;
import com.smartlearning.model.Advise;
import com.smartlearning.model.Book;
import com.smartlearning.model.BookCategoryVo;
import com.smartlearning.model.EBook;
import com.smartlearning.model.Note;
import com.smartlearning.model.OnlineForum;
import com.smartlearning.model.PageInfo;

/**
 * 电子书接口
 * @author Administrator
 */
public interface IBook {

	/**
	 * 获得电子书集合
	 * @param page 默认1,  rows 默认10
	 * @return
	 * @throws Exception 
	 */
	public List<EBook> geBookByPage(String serverIP, int lastBookId, int classId) throws Exception;
	
	/**
	 * 获得记事本
	 * @return
	 */
	public List<Note> queryNoteAll(String serverIP,Long userId);
	
	/**
	 * 创建记事本
	 * @param note
	 * @throws Exception 
	 */
	public void createNote(String serverIP,Long userId, String note) throws Exception;
	
	/**
	 * 获得电子书集合
	 * @param page 默认1,  rows 默认10
	 * @return
	 */
	public List<Book> getHomeworkByall(String serverIP,int page, int rows);
	
	/**
	 * 获得在线交流集合
	 * @param page 默认1,  rows 默认10
	 * @return
	 */
	public List<OnlineForum> getOnlineForumByPage(String serverIP,int page, int rows, Long classId);
	
	/**
	 * 添加
	 * @param advise
	 * @throws Exception
	 */
	public boolean  AddAdvise(String serverIp, Advise advise) throws Exception;
	
	/**
	 * 获得在线交流集合
	 * @param page 默认1,  rows 默认10
	 * @return
	 */
	public List<OnlineForum> getChildForumByPage(String serverIP,int id, int page, int rows);
	
	/**
	 * 获得电子书集合
	 * @param page 默认1,  rows 默认10
	 * @return
	 */
	public List<Book> geBookByPage(String serverIP, int classId, int page, int rows, Integer categoryId);
	
	/**
	 * 获得资源内容
	 * @param serverIP
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public Book queryBookById(String serverIP, int id) throws Exception;
	
	/**
	 * 本地电子书
	 * @return
	 */
	public List<EBook> getAllEBookDB();
	
	
	public List<EBook> getUserEBookDB(long userId);
	
	/**
	 * 获得电子书
	 * @param serverIP服务地址
	 * @return
	 */
	public List<EBook> getAllBookList(String serverIP, int classId) throws Exception;
	
	/**
	 * 得到用于分页的数据源的总条数，一般要配合getByPager()方法共同使用 
	 * @param pageSize
	 * @param condtion 1=1 代表无条件
	 * @return
	 */
	public int getByPageCount(int pageSize,String condtion);
	
	/**
	 * 分页存取数据
	 * params:
	 * condtion: 如果无条件 condition 传入 1=1
	 * pageIndex:代表是第几页(首页从0开始)
	 * pageSize:代表每页多少条记录
	 * return:返回的就是分页之后的数据
	 */
	
	public List<EBook> getByPager(int pageIndex, int pageSize,String condtion);
	
	/**
	 * 删除
	 * @param id
	 */
	public void removeEbook(int id) throws Exception;
	
	public void removeVideo(int id) throws Exception;

	public void Insert(EBook ebook);
	
	public List<EBook> getPermBooks(String serverIP,int userId,long classId) throws Exception;
	
	public List<EBook> getPermBooks(String serverIP,int userId,long classId,long category,PageInfo pageInfo) throws Exception;
	
	public List<EBook> getByPager(String condtion,PageInfo pageInfo);
	
	public List<BookCategoryVo> getEBooksCategoryBySQL(); 
	
	public void insertBook(int userId,BookRes book);
	
	public List<BookRes> getUserBookDB(int userId);
	
	public List<BookCategory> getBookCategory(int userId);
	
	public List<BookPart> getBookPart(int categoryId);
	
	public List<TreeElementBean> getBookChapterTree(int partId,int pid);
	
	public List<BookRes> getBookRes(int userId,String resTag);
	
	public List<VideoRes> getVideoRes(int userId,String resTag);
	
	public void insertVideo(int userId,VideoRes video);
	
	public List<BookRes> searchBookRes(String value);
	
	public List<VideoRes> searchVideoRes(String value);
	
	public HashMap<String,Integer> getResLocalCount(String whereSql);
}
