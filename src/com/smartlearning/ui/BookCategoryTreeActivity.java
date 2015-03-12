package com.smartlearning.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Window;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import com.smartlearning.R;
import com.smartlearning.biz.AdviodManager;
import com.smartlearning.biz.BookManager;
import com.smartlearning.constant.Global;
import com.smartlearning.model.Book;
import com.smartlearning.model.CoursewareNode;
import com.smartlearningclient.tree.TreeElement;
import com.smartlearningclient.tree.TreeView;
import com.smartlearningclient.tree.TreeView.LastLevelItemClickListener;
import com.smartlearningclient.tree.TreeViewAdapter;

/**
 * 课程分类
 */
public class BookCategoryTreeActivity extends Activity {

	private List<CoursewareNode> coursePlans = null;
	private Context context = null;
	private String fileName = "";
	private Button main_bottom_download = null;
	private final String pathName = "myCourseTable";
	private SharedPreferences sharedPreferences;
	ProgressDialog pd = null;
	GridView gridview = null;
	String ip = "";
	private TreeView lvCategorys; // 列表
	private List<TreeElement> lvCategoryData = null;
	Long classId;
	
	/**
	 * 提标框
	 */
	private void  showProgressDialog(){
		if (pd==null){
			pd = new ProgressDialog(this);
			pd.setTitle("系统提示");
			pd.setMessage("数据获取中,请稍候...");
		}
		pd.show();
	}
	
	@Override
	protected void onDestroy() {
		hideProgressDialog();
		super.onDestroy();
	}

	/**
	 * 隐藏
	 */
	private void hideProgressDialog(){
		if (pd!=null) pd.dismiss();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		
		setContentView(R.layout.tab_course_category);
		
	//	gridview = (GridView) findViewById(R.id.gridview1);
		context = this;
		lvCategorys = (TreeView) findViewById(R.id.frame_listview_address);
		
		sharedPreferences = getSharedPreferences("userInfo", MODE_PRIVATE);
		ip = sharedPreferences.getString("serverIp", null);
		classId = sharedPreferences.getLong("classId", 0);
		
		loadData();
		
	}
	
	
	/**
	 * 数据初始化加载
	 */
	private void loadData(){
		String serverIp = "http://"+ ip +":"+Global.Common_Port;
		CourseCategoryList listItem = null;
		
		try{
			listItem = new CourseCategoryList(serverIp);
		} catch(Exception ex){
			Toast.makeText(BookCategoryTreeActivity.this, "网络连接出现问题，请检查！", Toast.LENGTH_LONG).show();
		}
		
		Thread thread = new Thread(listItem);
		thread.start();
		
	}

	
//	private View.OnClickListener onClickListenerView = new View.OnClickListener() {
//		
//		@Override
//		public void onClick(View v) {
//			switch (v.getId()) {
//			case R.id.main_bottom_download1:
//				Intent intent3 = new Intent(context, MyCourseTableActivity.class);
//				startActivity(intent3);
//				break;
//			}	
//		}
//	};

	// 当AdapterView被单击(触摸屏或者键盘)，则返回的Item单击事件
//	class ItemClickListener implements OnItemClickListener {
//		public void onItemClick(AdapterView<?> arg0,// The AdapterView where the
//													// click happened
//				View arg1,// The view within the AdapterView that was clicked
//				int arg2,// The position of the view in the adapter
//				long arg3// The row id of the item that was clicked
//		) {
//			Intent intent = new Intent(context, TeachingCategoryActivity.class);
//			Integer ids = Integer.parseInt(coursePlans.get(arg2).getId().toString());
//			intent.putExtra("categoryId", ids);
//			startActivity(intent);
//		}
//	}
	
	
	LastLevelItemClickListener itemClickCallBack = new LastLevelItemClickListener() {
		// 创建节点点击事件监听
		@Override
		public void onLastLevelItemClick(int position,
				TreeViewAdapter adapter) {
//			TreeElement element = (TreeElement) adapter.getItem(position);
//			Toast.makeText(BookCategoryTreeActivity.this, "element"+element.getTitle(),Toast.LENGTH_SHORT).show();
			
			Intent intent = new Intent(context, TeachingCategoryActivity.class);
			TreeElement element = (TreeElement) adapter.getItem(position);
			Integer ids = Integer.parseInt(element.getId().toString());
			intent.putExtra("categoryId", ids);
			startActivity(intent);
		}
	};
	
	private Handler handleCourses= new Handler(){
		public void handleMessage(android.os.Message msg) {
		
			lvCategoryData = (ArrayList<TreeElement>)msg.obj;
			if (lvCategoryData == null) {
				Toast.makeText(BookCategoryTreeActivity.this, "对不起，无数据", Toast.LENGTH_SHORT).show();
			}else{
				pd.setMessage("数据已获取,界面绑定中...");
				
				// 初始化数据
				lvCategorys.initData(context, lvCategoryData);
				// 设置节点点击事件监听
				lvCategorys.setLastLevelItemClickCallBack(itemClickCallBack);
				
				pd.setMessage("数据获取中,请稍候...");
				hideProgressDialog();
			}	
		};
	};
	
 /**
  * 取出分类数据
 */
 class CourseCategoryList implements Runnable{
	    
	   List<TreeElement> lvCategoryData = new ArrayList<TreeElement>();
	   private String serverIp = "";
		
	    public CourseCategoryList(String serverIp){
	    	this.serverIp = serverIp;
			showProgressDialog();
		}
		
	    @Override
		public void run() {
			AdviodManager manager = new AdviodManager(context);
			BookManager bookManager = new BookManager(context);
				try {
					List<CoursewareNode> categoryList = manager.getCategoryByAll(this.serverIp);
					
					for(CoursewareNode rootItem : categoryList){
						
						TreeElement root = new TreeElement();
						root.setId(rootItem.getId().toString());
						root.setLevel(1);
						root.setTitle(rootItem.getName());
						root.setFold(false);
						root.setHasChild(true);
						root.setHasParent(false);
						root.setParentId(null);
						lvCategoryData.add(root);
						
						List<Book> bookList = bookManager.geBookByPage(this.serverIp, Integer.parseInt(classId.toString()), 1, 12000, new Long(root.getId()).intValue());
						
						for(Book childItem : bookList){
							
							TreeElement childData = new TreeElement();
							childData.setId(childItem.getId().toString());
							childData.setLevel(2);
							childData.setTitle(childItem.getName());
							childData.setFold(false);
							childData.setHasChild(false);
							childData.setHasParent(true);
							childData.setParentId(rootItem.getId().toString());
							lvCategoryData.add(childData);
							
						}
						
					}
					
					
				} catch (Exception e) {
				}
				Message message = Message.obtain();
				message.obj = lvCategoryData;
				handleCourses.sendMessage(message);
		
		}
	}
		
}
