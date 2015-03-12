package com.smartlearning.ui;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.Toast;

import com.smartlearning.R;
import com.smartlearning.biz.BookManager;
import com.smartlearning.constant.Global;
import com.smartlearning.model.Book;
import com.smartlearning.task.DownFileTask;
import com.smartlearning.utils.FileUtil;
import com.smartlearning.utils.Tool;

/**
 * 教辅资料
 */
public class TeachingCategoryActivity extends Activity {

	private Book books = null;
	private Context context = null;
	private String fileName = "";
	private Button main_bottom_download = null;
	private Button bottom_category = null;
	private final String pathName = "myBook";
	private SharedPreferences sharedPreferences;
	ProgressDialog pd = null;
	GridView gridview = null;
	String ip = "";
	
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
		
		
		setContentView(R.layout.tab_teaching_category);
		
		gridview = (GridView) findViewById(R.id.gridview1);
		context = this;

		sharedPreferences = getSharedPreferences("userInfo", MODE_PRIVATE);
		ip = sharedPreferences.getString("serverIp", null);
		
		if (getIntent().getIntExtra("categoryId", 0)!=0){
			int id = getIntent().getIntExtra("categoryId", 0);
			loadData(id);
		} 
		
	}
	
	
	/**
	 * 数据初始化加载
	 */
	private void loadData(int categoryId){
		String serverIp = "http://"+ ip +":"+Global.Common_Port;
		CourseTableList listItem = null;
		
		try{
			listItem = new CourseTableList(serverIp, categoryId);
		} catch(Exception ex){
			Toast.makeText(TeachingCategoryActivity.this, "网络连接出现问题，请检查！", Toast.LENGTH_LONG).show();
		}
		
		Thread thread = new Thread(listItem);
		thread.start();
		
	}

	/**
	 * 适配器
	 * @param lstImageItem
	 * @return
	 */
	private SimpleAdapter ImageAndTextListAdapter(ArrayList<HashMap<String, Object>> lstImageItem) {
		// 生成适配器的ImageItem <====> 动态数组的元素，两者一一对应
			SimpleAdapter saImageItems = new SimpleAdapter(this, // 没什么解释
					lstImageItem,// 数据来源
					R.layout.image_item,// night_item的XML实现
	
					// 动态数组与ImageItem对应的子项
					new String[] { "ItemImage", "ItemText" },
	
					// ImageItem的XML文件里面的一个ImageView,两个TextView ID
					new int[] { R.id.ItemImage, R.id.ItemText });
			
			saImageItems.setViewBinder(new ViewBinder() { 
		            
		            public boolean setViewValue(View view, Object data, String textRepresentation) { 
		                //判断是否为我们要处理的对象  
		                if (view instanceof ImageView)  {
		                	if(data != null){       
                                   if(data instanceof Bitmap )
                                     {
                                             view.setVisibility(View.VISIBLE);
                                             ImageView iv = (ImageView) view;
                                             iv.setImageBitmap((Bitmap) data);
                                     }
                                     else 
                                     {
                                             view.setVisibility(View.VISIBLE);
                                             ((ImageView) view).setImageResource((Integer) data);
                                     }
                                }
                                else
                                {
                                        view.setVisibility(View.GONE);
                                }
                                return true;
                        }
                        else
                        {
                                return false;
                        }
		            }  
		        });
		return saImageItems;
	}
	
	private View.OnClickListener onClickListenerView = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
//			switch (v.getId()) {
//			case R.id.main_bottom_download1:
//				Intent intent3 = new Intent(context, MyCourseTableActivity.class);
//				startActivity(intent3);
//				break;
//			case R.id.bottom_category:
//				Intent intent1 = new Intent(context, BookCategoryActivity.class);
//				startActivity(intent1);
//				break;
//			}
		}
	};

	// 当AdapterView被单击(触摸屏或者键盘)，则返回的Item单击事件
	class ItemClickListener implements OnItemClickListener {
		public void onItemClick(AdapterView<?> arg0,// The AdapterView where the
													// click happened
				View arg1,// The view within the AdapterView that was clicked
				int arg2,// The position of the view in the adapter
				long arg3// The row id of the item that was clicked
		) {
			
			String fileUrl = "";
			String serverIp = "http://"+ ip +":8080";
			try {
				fileName = getDownLoadFileName(books.getUrl());
				fileUrl = URLEncoder.encode(fileName,"UTF-8");
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}
			
			String url = serverIp + "/uploadFile/file/" + fileUrl;
			
			downloadVideos(url);
		}
	}
	
	private void intentForward(final String fileName) {
		Intent intent = new Intent();
		intent.setAction(android.content.Intent.ACTION_VIEW);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		File file = new File(fileName);
		Uri path = Uri.fromFile(file);
		intent.setDataAndType(path,"application/pdf");
		startActivity(intent);
	};
	
	private Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			//Tool.ShowMessage(context, msg.obj.toString());
			final String fileName = msg.obj.toString();
			String extName = FileUtil.getExtName(fileName);
			if (extName.equalsIgnoreCase(".pdf") ) {
				intentForward(fileName);
			}else{
				Tool.ShowMessage(context, "非pdf格式");
			}
			
			// intentForward(fileName);
		}

	};
	
	
	private Handler handleCourses= new Handler(){
		public void handleMessage(android.os.Message msg) {
		
			books = (Book)msg.obj;
			if (books == null) {
				Toast.makeText(TeachingCategoryActivity.this, "对不起，教辅资料还没公布", Toast.LENGTH_SHORT).show();
			}else{
				pd.setMessage("数据已获取,界面绑定中...");
				
				ArrayList<HashMap<String, Object>> lstImageItem = new ArrayList<HashMap<String, Object>>();
//				for(int i = 0; i < books.size(); i ++){
//					Book book = books.get(i);
					HashMap<String, Object> map = new HashMap<String, Object>();
				//	String url = book.getUrl();
					
					Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.kechejidu);
					
					map.put("ItemImage", bitmap);
					map.put("ItemText", books.getName());
					lstImageItem.add(map);
//				}
				
				final SimpleAdapter saImageItems = ImageAndTextListAdapter(lstImageItem); 
				// 添加并且显示
				gridview.setAdapter(saImageItems);
				// 添加消息处理
				gridview.setOnItemClickListener(new ItemClickListener());
				
				pd.setMessage("数据获取中,请稍候...");
				hideProgressDialog();
			}	
		};
	};
	
 /**
  * 取出课程表数据
 */
 class CourseTableList implements Runnable{
	    
	   Book book = null;
	   private String serverIp = "";
	   private int categoryId; 
		
	    public CourseTableList(String serverIp, int categoryId){
	    	this.serverIp = serverIp;
	    	this.categoryId = categoryId;
			showProgressDialog();
		}
		
	    @Override
		public void run() {
	    	BookManager manager = new BookManager(context);
				try {
				//	bookList = manager.geBookByPage(this.serverIp,1, 12000, this.categoryId);
					book = manager.queryBookById(this.serverIp, this.categoryId);
					
				} catch (Exception e) {
				}
				Message message = Message.obtain();
				message.obj = book;
				handleCourses.sendMessage(message);
		
		}
	}
	
	
	//得到当前下载的文件名
		private String getDownLoadFileName(String urls){
			if (urls != null){
				String url = urls;
				int lastdotIndex = url.lastIndexOf("/");
				String fileName = url.substring(lastdotIndex+1, url.length());
				return fileName;
			}else{
				return "";
			}
		}
		
	private void downloadVideos(final String url){
			
			Tool.ShowMessage(context, fileName);
			if(FileUtil.isExists("/sdcard/myBook/"+fileName)){
			     AlertDialog.Builder builder = new AlertDialog.Builder(context);
			     builder.setTitle("提示").setMessage("该视频文件已经存在于SDCard中，确定要覆盖下载吗？")
			     .setPositiveButton("确定", new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog, int which) {
						DownFileTask task = new DownFileTask(context,handler,fileName,"myBook");
						task.execute(url);
					}
	//		     }).setNegativeButton("取消", null).create().show();
			     }).setNegativeButton("取消", new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog, int which) {
						String extName = FileUtil.getExtName(fileName);
						if (extName.equalsIgnoreCase(".pdf") ) {
							intentForward("/sdcard/myBook/"+fileName);
						} else {
							Tool.ShowMessage(context, "非pdf格式");
						}
						
					}
			    	 
			     }).create().show();
			}else{
				
				DownFileTask task = new DownFileTask(context,handler,fileName,"myBook");
				task.execute(url);
				
			}
			
		}
}
