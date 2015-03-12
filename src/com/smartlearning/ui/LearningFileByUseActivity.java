package com.smartlearning.ui;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.smartlearning.R;
import com.smartlearning.adapter.MyEBookAdapter;
import com.smartlearning.biz.BookManager;
import com.smartlearning.constant.Global;
import com.smartlearning.model.EBook;
import com.smartlearning.task.DownFileTask;
import com.smartlearning.utils.CommonUtil;
import com.smartlearning.utils.DateUtil;
import com.smartlearning.utils.FileUtil;
import com.smartlearning.utils.HttpUtil;
import com.smartlearning.utils.ImageTools;
import com.smartlearning.utils.Tool;

/**
 *电子书
 */
public class LearningFileByUseActivity extends Activity {
	private static final String TAG = "EBOOKList";
	ProgressDialog pd = null;
	private SharedPreferences sharedPreferences;
	private Context context;
	String ip = "";
	private ImageView return_btn = null;
	private ListView listView = null;
	private Button bottom_recommend = null;
	private Button main_bottom_download = null;
	private BookManager bookManager = null;
	private LayoutInflater inflater = null;
	private EBookAdapter adapter = null;
	private List<EBook> eBooks = null;
	private String serverIp;
	private Long classId;
	private String userId = "0";
	private String fileName = "";
	private static final int PAGESIZE = 10; // 每次取几条记录
	private int pageIndex = 0; // 用于保存当前是第几页,0代表第一页
	ImageView imgCreate = null;
	private String[] is = {"删除", "取消" };
	String rootPath = Environment.getExternalStorageDirectory().getPath() + "/myBook";
	
	private long loadEbookDate;
	
	private String loadEbookKey;
	
	private View noEbookView;
	
	private MyEBookAdapter myEBookAdapter = null;
	
	private LinearLayout ll_refresh;
	
	private TextView txtTopTitle;
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
	
	/**
	 * 隐藏
	 */
	private void hideProgressDialog(){
		if (pd!=null) pd.dismiss();
	}
	
	@Override
	protected void onDestroy() {
		hideProgressDialog();
		super.onDestroy();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ebook_center);
		
		context = this;
		
		sharedPreferences = getSharedPreferences("userInfo", MODE_PRIVATE);
		ip = sharedPreferences.getString("serverIp", null);
		serverIp = "http://"+ ip +":"+Global.Common_Port;
		classId = sharedPreferences.getLong("classId", 0);
		userId = sharedPreferences.getString("user","0");
		loadEbookKey = userId+"_load_ebook_date";
		loadEbookDate = sharedPreferences.getLong(loadEbookKey,0);
		initView();
		loadData();
		bindClickListener();
	}
	
	public void initView() {
		return_btn = (ImageView) findViewById(R.id.return_btn);
//		title = (TextView) findViewById(R.id.title);
		listView = (ListView) findViewById(R.id.listView);
		/*bottom_recommend = (Button) findViewById(R.id.bottom_recommend);
		main_bottom_download = (Button) findViewById(R.id.main_bottom_download);
		main_bottom_download.setOnClickListener(onClickListenerView);*/
		imgCreate = (ImageView) findViewById(R.id.imgCreate);
		
//		return_btn = (Button) findViewById(R.id.return_btn);
		return_btn.setOnClickListener(onClickListenerView);
	//	refreshableView = (RefreshableView) findViewById(R.id.refreshable_view);
		
		//bottom_recommend.setOnClickListener(onClickListenerView);
		context = this;
		inflater = LayoutInflater.from(context);
		
		noEbookView = inflater.inflate(R.layout.activity_no_ebook, null);
		
		ll_refresh = (LinearLayout)findViewById(R.id.ll_refresh);
		
		txtTopTitle = (TextView)findViewById(R.id.txtTopTitle);
		
		// list需要刷新时调用		
//		refreshableView.setOnRefreshListener(new PullToRefreshListener() {
//			@Override
//			public void onRefresh() {
//				new GetDataTask(0).execute(serverIp);
//			}
//		}, 0);
		
		
		imgCreate.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				refreshData();
			}
		});
		
		ll_refresh.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				refreshData();
			}
		});
		
		// list需要刷新时调用
//		refreshableView.setOnRefreshListener(new OnRefreshListener() {
//					@Override
//					public void onRefresh() {
//						// 在这执行后台工作
//						new GetDataTask(0).execute(serverIp);
//				}
//		});
	}
	

	
	/**
	 * 数据初始化加载
	 */
	private void loadData(){
		/*EbookList listItem = new EbookList();
		Thread thread = new Thread(listItem);
		thread.start();*/
		boolean loadDataFormServer = false;
		long nowDate = DateUtil.date2Long(new Date());
		if(loadEbookDate == 0){
			loadDataFormServer = true;
		}else{
			if(nowDate > loadEbookDate){
				loadDataFormServer = true;
			}
		}
		new GetEbookTask().execute(loadDataFormServer);
		
	}
	
	/**
	 *绑定事件 
	 */
	private void bindClickListener() {
		
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				
				if (id != -1){
					String fileUrl = "";
					try {
						//fileName = getDownLoadFileName(eBooks.get(position-1).getPdfUrl());
						fileName = getDownLoadFileName(eBooks.get(position).getPdfUrl());
						fileUrl = URLEncoder.encode(fileName,"UTF-8");
					} catch (UnsupportedEncodingException e1) {
					}
					Log.i("fileUrl", "fileUrlfile:==================== " + fileUrl);
					String url = serverIp + "/uploadFile/file/" + fileUrl;
					downloadFiles(url);
				}
			}
		});
		
		listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					final int position, long id) {
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setTitle("请选择操作");
				builder.setItems(is, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case 0:
							//fileName = getDownLoadFileName(eBooks.get(position-1).getPdfUrl());
							fileName = getDownLoadFileName(eBooks.get(position).getPdfUrl());
							File f = new File(rootPath + "/" + fileName);
							deleteFile(f);
							//int id = eBooks.get(position-1).get_id();
							int id = eBooks.get(position).get_id();
							try {
								bookManager.removeEbook(id);
							} catch (Exception e) {
							}
							loadData();
							Tool.ShowMessage(context, is[which]);
							break;
						case 1:
							Tool.ShowMessage(context, is[which]);
							break;
						default:
							break;
						}
					}
				}).create().show();
				return true;
			}
		});
	}
	
	/**
	 * 删除文件
	 * @param file
	 */
	private void deleteFile(File file){
		//File file = new File(filePath);
	     if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
	     {
	      if (file.exists())
	      {
	       if (file.isFile())
	       {
	        file.delete();
	       }
	       // 如果它是一个目录
	       else if (file.isDirectory())
	       {
	        // 声明目录下所有的文件 files[];
	        File files[] = file.listFiles();
	        for (int i = 0; i < files.length; i++)
	        { // 遍历目录下所有的文件
	         deleteFile(files[i]); // 把每个文件 用这个方法进行迭代
	        }
	       }
	       file.delete();
	      }
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
	
	private Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			final String fileName = msg.obj.toString();
			String extName = FileUtil.getExtName(fileName);
			if (extName.equalsIgnoreCase(".pdf") ) {
			}else{
				Tool.ShowMessage(context, "非pdf格式");
			}
		}

	};
	
	private void intentForward(final String fileName) {
		Intent intent = new Intent();
		intent.setAction(android.content.Intent.ACTION_VIEW);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		File file = new File(fileName);
		Uri path = Uri.fromFile(file);
		intent.setDataAndType(path,"application/pdf");
		startActivity(intent);
	};
	
	private void downloadFiles(final String url){
		
		if(FileUtil.isExists("/sdcard/myBook/"+fileName)){
			String path = "/sdcard/myBook/"+fileName;
			File file = new File(path);
			if (file.isDirectory()) {
			} else {
				intentForward("/sdcard/myBook/"+fileName);
			}
		}else{
			DownFileTask task = new DownFileTask(context,handler,fileName,"myBook");
			task.execute(url);
		}
		
	}
	
	private View.OnClickListener onClickListenerView = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			/*case R.id.bottom_recommend:
				Intent intent1 = new Intent(context, BookCategoryTreeActivity.class);
				startActivity(intent1);
				break;*/
			case R.id.return_btn:
				Intent intent = new Intent(context, MainActivity.class);
				startActivity(intent);
				finish();
				break;	
			
			}	
		}
	};
	
	/**
	 * 数据初始化加载
	 */
	private void refreshData(){
		/*String serverIp = "http://"+ ip +":"+Global.Common_Port;
		EbookRemoreList listItem = null;
		
		try{
			listItem = new EbookRemoreList(serverIp);
		} catch(Exception ex){
			Toast.makeText(context, "网络连接出现问题，请检查！", Toast.LENGTH_LONG).show();
		}
		
		Thread thread = new Thread(listItem);
		thread.start();*/
		
		boolean loadDataFormServer = true;
		new GetEbookTask().execute(loadDataFormServer);
	}
	
	private Handler handlebookRemoreList = new Handler(){
		public void handleMessage(android.os.Message msg) {
			
			List<EBook> eBookt = (ArrayList<EBook>)msg.obj;
			if (eBookt == null) {
				Toast.makeText(context, "对不起，最新电子还没公布", Toast.LENGTH_SHORT).show();
				hideProgressDialog();
			}else{
				loadData();
//				pd.setMessage("数据已获取,界面绑定中...");
//				
//				adapter = new EBookAdapter(eBooks, context);
//				listView.setAdapter(adapter);
//				pd.setMessage("数据获取中,请稍候...");
				hideProgressDialog();
				
			}	
		};
	};
	
	class EbookRemoreList implements Runnable{
		List<EBook> bookRemoreList = null;
		String 	serverIP;
	    public EbookRemoreList(String serverIP){
	    	this.serverIP = serverIP;
			showProgressDialog();
		}
	    
		@Override
		public void run() {
			bookManager = new BookManager(context);
			try{
				bookRemoreList = bookManager.getAllBookList(serverIP, Integer.parseInt(classId.toString()));
			//	bookRemoreList = bookManager.getAllBookList(serverIP, 2);
				
				Log.i("bookRemoreList", "bookRemoreList.size=="+bookRemoreList.size());
				
			} catch(Exception e){
			}
			Message message = Message.obtain();
			message.obj = bookRemoreList;
			handlebookRemoreList.sendMessage(message);
		}
	}
	
	private Handler handleBookList = new Handler(){
		public void handleMessage(android.os.Message msg) {
			
			eBooks = (ArrayList<EBook>)msg.obj;
			if (eBooks == null) {
				Toast.makeText(context, "对不起，最新电子还没公布", Toast.LENGTH_SHORT).show();
				hideProgressDialog();
			}else{
				pd.setMessage("数据已获取,界面绑定中...");
				
				adapter = new EBookAdapter(eBooks, context);
				listView.setAdapter(adapter);
				pd.setMessage("数据获取中,请稍候...");
				hideProgressDialog();
				
			}	
		};
	};
	
	class EbookList implements Runnable{
		List<EBook> bookList = null;
			
	    public EbookList(){
			showProgressDialog();
		}
	    
		@Override
		public void run() {
			bookManager = new BookManager(context);
			try{
				bookList =	bookManager.getByPager(pageIndex, PAGESIZE, "1=1 order by time");
			} catch(Exception e){
			}
			Message message = Message.obtain();
			message.obj = bookList;
			handleBookList.sendMessage(message);
		}
	}
	
	/*用于获取用于显示的分页信息*/
	private String getPagerInfo(){
		String pagerInfo = "第{0}页 ,共{1}页";
		int totalPageCount = bookManager.getByPageCount(PAGESIZE, "1=1 order by time desc");
		return MessageFormat.format(pagerInfo, pageIndex+1, totalPageCount);
	}
	
	private Handler handlerTpCount = new Handler(){
		public void handleMessage(android.os.Message msg) {
			int gettotalcount = (Integer)msg.obj;
			String pagerInfo = "第{0}页 ,共{1}页";
			MessageFormat.format(pagerInfo, pageIndex+1, gettotalcount);
		};
	};
	
	
	public void getTestPaperCount() {

		Thread thread = new Thread() {
			@Override
			public void run() {
				int totalcount = bookManager.getByPageCount(PAGESIZE, "1=1 order by time desc");
				Message message = Message.obtain();
				message.obj = totalcount;
				handlerTpCount.sendMessage(message);
			}
		};
		
		thread.start();
		thread = null;

	}
	
class EBookAdapter extends BaseAdapter {
		private Context context = null;
		private List<EBook> list = null;

		public EBookAdapter(Context context) {
			this.context = context;
			inflater = LayoutInflater.from(context);
		}
		
		public EBookAdapter(List<EBook> list, Context context){
			this.list = list;
			this.context = context;
			inflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return list.size() + 2;
		}

		@Override
		public Object getItem(int position) {
			return list.get(position-1);
		}

		@Override
		public long getItemId(int position) {
			//return position;
			if (position == 0)// 选中第一项
			{
				return -1;// 代表点击的是第一项
			} else if (position > 0 && (position < this.getCount() - 1)) {
				return list.get(position - 1).get_id();// 如果用户选中了中间项
			} else {
				return -2;// 表示用户选中最后一项
			}
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			// 说明是第一项
			if (position == 0) {
				convertView = inflater.inflate(R.layout.test_paper_question_item, null);
				return convertView;
			}
			
			// 说明是最后一项
			if (position == this.getCount() - 1 ) {
				convertView = inflater.inflate(R.layout.ebookmoreitemsview, null);
				TextView txtPagerInfo = (TextView) convertView.findViewById(R.id.txtPagerInfo);
				txtPagerInfo.setText(getPagerInfo());
				Button btnFirst = (Button) convertView.findViewById(R.id.btnFirst);
				btnFirst.setOnClickListener(new View.OnClickListener() {
					
					public void onClick(View v) {
						pageIndex=0;
						loadData();
					}
				 });
							
							
				Button btnPrev = (Button) convertView.findViewById(R.id.btnPrev);
				btnPrev.setOnClickListener(new View.OnClickListener() {
					
					public void onClick(View v) {
						if (pageIndex>0){
							 pageIndex--;
							 loadData();
						     Toast.makeText(context, "第"+(pageIndex+1)+"页", Toast.LENGTH_SHORT).show();
						}else{
							 Toast.makeText(context, "已经是第一页了", Toast.LENGTH_SHORT).show();
						}
						
					}
				 });
							
				Button btnNext = (Button) convertView.findViewById(R.id.btnNext);
				btnNext.setOnClickListener(new View.OnClickListener() {
					
					public void onClick(View v) {
						int totalPageCount = bookManager.getByPageCount(PAGESIZE, "1=1 order by time desc");
						if (pageIndex < totalPageCount-1){
							pageIndex++;
						    loadData();
						}else{
						   Toast.makeText(context, "已经最后一页了", Toast.LENGTH_SHORT).show();
						}
					}
				});
							
				Button btnLast = (Button) convertView.findViewById(R.id.btnLast);
				btnLast.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						int totalPageCount = bookManager.getByPageCount(PAGESIZE, "1=1 order by time desc");
						pageIndex = totalPageCount-1;
						loadData();
					}
				});
							
				return convertView;
			}
			
	//		ViewHolder holder;
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.videocenter_list_item,null);
		//		holder = new ViewHolder();
		//		holder.videoImage = (ImageView) convertView.findViewById(R.id.image);
		//		holder.name = (TextView) convertView.findViewById(R.id.videoName);
		//		name = (TextView) convertView.findViewById(R.id.videoName);
		//		holder.category = (TextView) convertView.findViewById(R.id.videoCategory);
		//		holder.attention = (TextView) convertView.findViewById(R.id.lectuer);
				
		//		convertView.setTag(holder);
			} 
			
			ImageView videoImage = (ImageView) convertView.findViewById(R.id.image);
			TextView name = (TextView) convertView.findViewById(R.id.videoName);
			TextView category = (TextView) convertView.findViewById(R.id.videoCategory);
			TextView attention = (TextView) convertView.findViewById(R.id.lectuer);
			
			int flashback = position-1;
			byte[] result = list.get(flashback).getImageUrl();
			if(result != null){
				Bitmap bitmap = ImageTools.getBitmapFromByte(result);
				if(bitmap!= null){
					videoImage.setImageBitmap(bitmap);
				} 
			}
			name.setText(list.get(flashback).getName());
			category.setText("分类 ：" + list.get(flashback).getCategoryName());
			attention.setText("时间：" +DateUtil.dateToChineseString(DateUtil.stringsToDate(list.get(flashback).getTime()),true));
			
			return convertView;
		}

	}
	
	
	public final class ViewHolder {
		public ImageView videoImage;
		public TextView name;
		public TextView category;
		public TextView attention;
		public TextView category_title;
	}
	
	private class GetEbookTask extends AsyncTask<Boolean,Integer,Integer>{
		
		@Override
		protected Integer doInBackground(Boolean... params) {
			bookManager = new BookManager(context);
			//List<EBook> eBooks = null;
			boolean loadDataFromServer = params[0];
			if(HttpUtil.netWorkIsOK(context) && loadDataFromServer){//读取远程数据
				eBooks = bookManager.getPermBooks(serverIp, Integer.parseInt(userId), classId);
				if(loadDataFromServer){//设置已经从服务器获取
					SharedPreferences.Editor editor = sharedPreferences.edit();
					editor.putLong(loadEbookKey, DateUtil.date2Long(new Date()));
					editor.commit();
				}
			}else{//读取本地数据
				Log.i(TAG,"网络不可用或已经缓存数据，获取本地电子书...");
				eBooks = bookManager.getUserEBookDB(Long.parseLong(userId));
			}
			
			return 1;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showProgressDialog();
			if(eBooks != null)
				eBooks.clear();
		}

		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			if (eBooks == null) {
				CommonUtil.showToast(context, "对不起，最新电子还没公布", Toast.LENGTH_LONG);
			} /*else if(result.size() == 0){
				//listView.addFooterView(noEbookView);
				CommonUtil.showToast(context, "不存在您有权限的电子书", Toast.LENGTH_LONG);
			}*/
			else{
				pd.setMessage("数据已获取,界面绑定中...");
				//adapter = new EBookAdapter(eBooks, context);
				groupByEbooks(eBooks);
				myEBookAdapter = new MyEBookAdapter(eBooks, context);
				int count = 0;
				if(eBooks!=null){
					count = eBooks.size();
				}
				txtTopTitle.setText("电子书资料(共："+count+"本)");
				listView.setAdapter(myEBookAdapter);
				pd.setMessage("数据获取中,请稍候...");
			}	
			hideProgressDialog();
		}
		
	}
	
	
	private void groupByEbooks(List<EBook> ebooks){
		Map<String,List<EBook>> tempMap = new HashMap<String,List<EBook>>();
		if(ebooks != null && ebooks.size() > 0){
			for( EBook book : ebooks){
				String ctgname = book.getCategoryName();
				String fileName = getDownLoadFileName(book.getPdfUrl());
				if(FileUtil.isExists("/sdcard/myBook/"+fileName)){
					book.setLocalFile(true);
				}				
				List<EBook> tempList = tempMap.get(ctgname);
				if(tempList != null && tempList.size() > 0){
					tempList.add(book);
				}else{
					tempList = new ArrayList<EBook>();
					tempList.add(book);
					tempMap.put(ctgname, tempList);
				}
			}
		}
		ebooks.clear();
		Iterator<Entry<String,List<EBook>>> iter = tempMap.entrySet().iterator();
		while(iter.hasNext()){
			Entry<String,List<EBook>> entry = iter.next();
			ebooks.addAll(entry.getValue());
		}
		//return resList;
	}
	
	private  class GetDataTask extends AsyncTask<String, Void, List<EBook>>{

		private int classId;
		
		public GetDataTask(int classId) {
			this.classId = classId;
		}
		
		@Override
		protected List<EBook> doInBackground(String... params) {
			
			bookManager = new BookManager(context);
			List<EBook> eBooks = null;
			try {
				eBooks = bookManager.getAllBookList(params[0], classId);
			} catch (NumberFormatException e) {
			} catch (Exception e) {
			}
			return eBooks;
		}

		@Override
		protected void onPostExecute(List<EBook> result) {
			if(result != null){
//				adapter = new EBookAdapter(result, context);
//				listView.setAdapter(adapter);
//				adapter.notifyDataSetChanged();
//				refreshableView.finishRefreshing();
				
				loadData();
			}
			
//			refreshableView.finishRefreshing();
		}
		
	}
	
}
