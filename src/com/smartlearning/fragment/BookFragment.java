package com.smartlearning.fragment;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.smartlearning.R;
import com.smartlearning.adapter.MyEBookAdapter;
import com.smartlearning.biz.BookManager;
import com.smartlearning.constant.Global;
import com.smartlearning.model.EBook;
import com.smartlearning.model.PageInfo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.smartlearning.task.DownFileTask;
import com.smartlearning.ui.MainActivity;
import com.smartlearning.ui.PagerListActivity;
import com.smartlearning.ui.PagerListActivity.BookCallback;
import com.smartlearning.utils.CommonUtil;
import com.smartlearning.utils.DateUtil;
import com.smartlearning.utils.FileUtil;
import com.smartlearning.utils.HttpUtil;
import com.smartlearning.utils.Tool;

public class BookFragment extends Fragment implements
		PagerListActivity.BookCallback {
	private View mBaseView;
	private Context mContext;
	private SharedPreferences sharedPreferences;
	private String ip = "";
	private ImageView return_btn = null;
	private ListView listView = null;
	private Button bottom_recommend = null;
	private Button main_bottom_download = null;
	private BookManager bookManager = null;
	private LayoutInflater inflater = null;
	private List<EBook> eBooks = null;
	private String serverIp;
	private Long classId;
	private String userId = "0";
	private String fileName = "";
	ImageView imgCreate = null;
	private String[] is = { "删除", "取消" };
	String rootPath = Environment.getExternalStorageDirectory().getPath()
			+ "/myBook";

	private long loadEbookDate;

	private String loadEbookKey;

	private View noEbookView;

	private MyEBookAdapter myEBookAdapter = null;

	private LinearLayout ll_refresh;

	private TextView txtTopTitle;
	private long category = 0;
	private String categoryName;
	private ProgressDialog pd = null;
	private int totalPage = 1;//总页数
	private int currentPage = 1;//当前页数
	private PagerCallback pagerCallBack;
	private int totalSize = 0;
	private LinearLayout book_none;
	private EBook nowDownloadEBook;
	private boolean loadFromServer = false;
	/**
	 * 提标框
	 */
	private void showProgressDialog() {
		if (pd == null) {
			pd = new ProgressDialog(mContext);
			pd.setTitle("系统提示");
			pd.setMessage("数据获取中,请稍候...");
		}
		pd.show();
	}

	/**
	 * 隐藏
	 */
	private void hideProgressDialog() {
		if (pd != null)
			pd.dismiss();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContext = getActivity();
		mBaseView = inflater.inflate(R.layout.ebook_center, null);
		findViewById();
		setListener();
		processLogic();
		return mBaseView;
	}

	private void findViewById() {
		return_btn = (ImageView) mBaseView.findViewById(R.id.return_btn);

		listView = (ListView) mBaseView.findViewById(R.id.listView);

		imgCreate = (ImageView) mBaseView.findViewById(R.id.imgCreate);

		ll_refresh = (LinearLayout) mBaseView.findViewById(R.id.ll_refresh);

		txtTopTitle = (TextView) mBaseView.findViewById(R.id.txtTopTitle);

		book_none = (LinearLayout)mBaseView.findViewById(R.id.book_none);
	}

	private void setListener() {
		imgCreate.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				loadFromServer = true;
				loadData();
			}
		});

		ll_refresh.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				loadFromServer = true;
				loadData();
			}
		});

		return_btn.setOnClickListener(onClickListenerView);

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				if (id != -1) {
					String fileUrl = "";
					try {
						// fileName =
						// getDownLoadFileName(eBooks.get(position-1).getPdfUrl());
						nowDownloadEBook = eBooks.get(position);
						fileName = getDownLoadFileName(nowDownloadEBook.getPdfUrl());
						fileUrl = URLEncoder.encode(fileName, "UTF-8");
					} catch (UnsupportedEncodingException e1) {
					}
					//Log.i("fileUrl", "fileUrlfile:==================== "+ fileUrl);
					String url = serverIp + "/uploadFile/file/" + fileUrl;
					downloadFiles(url);
				}
			}
		});

		listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					final int position, long id) {
				AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
				builder.setTitle("请选择操作");
				builder.setItems(is, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case 0:
							// fileName =
							// getDownLoadFileName(eBooks.get(position-1).getPdfUrl());
							EBook ebook = eBooks.get(position);
							fileName = getDownLoadFileName(ebook.getPdfUrl());
							if (FileUtil.isExists("/sdcard/myBook/" + fileName)) {
								File f = new File(rootPath + "/" + fileName);
								deleteFile(f);
								// int id = eBooks.get(position-1).get_id();
								int id = eBooks.get(position).get_id();
								try {
									bookManager.removeEbook(id);
								} catch (Exception e) {
								}
								loadFromServer = false;
								loadData();
								Tool.ShowMessage(mContext, is[which]);
							}else{
								CommonUtil.showToast(mContext, "非本地文件不能进行删除操作",Toast.LENGTH_LONG);
							}
							break;
						case 1:
							Tool.ShowMessage(mContext, is[which]);
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

	private void processLogic() {
		sharedPreferences = getActivity().getSharedPreferences("userInfo",
				mContext.MODE_PRIVATE);
		ip = sharedPreferences.getString("serverIp", null);
		serverIp = "http://" + ip + ":" + Global.Common_Port;
		classId = sharedPreferences.getLong("classId", 0);
		userId = sharedPreferences.getString("user", "0");
		loadEbookKey = userId + "_load_ebook_date";
		loadEbookDate = sharedPreferences.getLong(loadEbookKey, 0);
		loadData();
	}

	@Override
	public void setCategory(long category,String categoryName) {
		this.category = category;
		this.categoryName = categoryName;
	}
	
	

	@Override
	public void setLoadDataType(boolean loadFromServer) {
		this.loadFromServer = loadFromServer; 
	}

	@Override
	public void setCurrentPage(int pageIndex) {
		this.currentPage = pageIndex;
	}

	/**
	 * 删除文件
	 * 
	 * @param file
	 */
	private void deleteFile(File file) {
		// File file = new File(filePath);
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			if (file.exists()) {
				if (file.isFile()) {
					file.delete();
				}
				// 如果它是一个目录
				else if (file.isDirectory()) {
					// 声明目录下所有的文件 files[];
					File files[] = file.listFiles();
					for (int i = 0; i < files.length; i++) { // 遍历目录下所有的文件
						deleteFile(files[i]); // 把每个文件 用这个方法进行迭代
					}
				}
				file.delete();
			}
		}
	}

	// 得到当前下载的文件名
	private String getDownLoadFileName(String urls) {
		if (urls != null) {
			String url = urls;
			int lastdotIndex = url.lastIndexOf("/");
			String fileName = url.substring(lastdotIndex + 1, url.length());
			return fileName;
		} else {
			return "";
		}
	}


	private void intentForward(final String fileName) {
		Intent intent = new Intent();
		intent.setAction(android.content.Intent.ACTION_VIEW);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		File file = new File(fileName);
		Uri path = Uri.fromFile(file);
		intent.setDataAndType(path, "application/pdf");
		startActivity(intent);
	};

	private void downloadFiles(final String url) {

		if (FileUtil.isExists("/sdcard/myBook/" + fileName)) {
			String path = "/sdcard/myBook/" + fileName;
			File file = new File(path);
			if (file.isDirectory()) {
			} else {
				intentForward("/sdcard/myBook/" + fileName);
			}
		} else {
			DownFileTask task = new DownFileTask(mContext, handler, fileName,
					"myBook");
			task.execute(url);
		}

	}

	/**
	 * 数据初始化加载
	 */
	private void loadData() {
		pagerCallBack.setLoadDataType(this.loadFromServer);
		new GetEbookTask().execute(loadFromServer);
	}

	private class GetEbookTask extends AsyncTask<Boolean, Integer, Integer> {

		@Override
		protected Integer doInBackground(Boolean... params) {
			bookManager = new BookManager(mContext);
			PageInfo pageInfo = new PageInfo(currentPage);
			boolean loadDataFromServer = params[0];
			if (HttpUtil.netWorkIsOK(mContext)&&loadDataFromServer) {// 读取远程数据
				eBooks = bookManager.getPermBooks(serverIp,
						Integer.parseInt(userId), classId,category,pageInfo);
			} else {// 读取本地数据
				Log.i("BookFragment", "网络不可用或已经缓存数据，获取本地电子书...");
				String condtion = " class_id="+userId+" and category_name = '"+categoryName+"' order by id desc";
				eBooks = bookManager.getByPager(condtion, pageInfo);
			}
			totalPage = pageInfo.getTotalPage();
			totalSize = pageInfo.getTotalResult();
			pagerCallBack.setTotalPage(totalPage);
			return 1;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showProgressDialog();
			if (eBooks != null)
				eBooks.clear();
		}

		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			if (eBooks == null) {
				CommonUtil
						.showToast(mContext, "对不起，最新电子还没公布", Toast.LENGTH_LONG);
			} 
			else {
				pd.setMessage("数据已获取,界面绑定中...");
				// adapter = new EBookAdapter(eBooks, context);
				groupByEbooks(eBooks);
				myEBookAdapter = new MyEBookAdapter(eBooks, mContext);
				if(totalSize == 0){
					book_none.setVisibility(View.VISIBLE);
				}else{
					book_none.setVisibility(View.GONE);
				}
				txtTopTitle.setText(categoryName+" -- 电子书资料(共：" + totalSize + "本)");
				listView.setAdapter(myEBookAdapter);
				pd.setMessage("数据获取中,请稍候...");
			}
			hideProgressDialog();
		}

	}

	private void groupByEbooks(List<EBook> ebooks) {
		Map<String, List<EBook>> tempMap = new HashMap<String, List<EBook>>();
		if (ebooks != null && ebooks.size() > 0) {
			for (EBook book : ebooks) {
				//String ctgname = book.getCategoryName();
				String fileName = getDownLoadFileName(book.getPdfUrl());
				if (FileUtil.isExists("/sdcard/myBook/" + fileName)) {
					book.setLocalFile(true);
				}
				/*List<EBook> tempList = tempMap.get(ctgname);
				if (tempList != null && tempList.size() > 0) {
					tempList.add(book);
				} else {
					tempList = new ArrayList<EBook>();
					tempList.add(book);
					tempMap.put(ctgname, tempList);
				}*/
			}
		}
		
		/*ebooks.clear();
		Iterator<Entry<String, List<EBook>>> iter = tempMap.entrySet()
				.iterator();
		while (iter.hasNext()) {
			Entry<String, List<EBook>> entry = iter.next();
			ebooks.addAll(entry.getValue());
		}*/
		
		// return resList;
	}

	private View.OnClickListener onClickListenerView = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			/*
			 * case R.id.bottom_recommend: Intent intent1 = new Intent(context,
			 * BookCategoryTreeActivity.class); startActivity(intent1); break;
			 */
			case R.id.return_btn:
				/*Intent intent = new Intent(mContext, MainActivity.class);
				startActivity(intent);
				getActivity().finish();*/
				getActivity().finish();
				break;

			}
		}
	};

	private Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			final String fileName = msg.obj.toString();
			String extName = FileUtil.getExtName(fileName);
			if (extName.equalsIgnoreCase(".pdf") ) {
				if(bookManager == null)
					bookManager = new BookManager(mContext);
				nowDownloadEBook.setLocalFile(true);
				bookManager.insertEbook(nowDownloadEBook);
				if(myEBookAdapter != null)
					myEBookAdapter.notifyDataSetChanged();
			}else{
				Tool.ShowMessage(mContext, "非pdf格式");
			}
		}

	};
	
	public interface PagerCallback{
		public void setTotalPage(int totalPage);
		
		public void setLoadDataType(boolean loadFromServer);
	}

	@Override
	public void onAttach(Activity activity) {
		try {
			pagerCallBack = (PagerCallback) activity;
			//pagerCallBack.setTotalPage(totalPage);
		} catch (Exception e) {
			throw new ClassCastException(this.toString()
					+ " must implement PagerCallback");
		}
		super.onAttach(activity);
	}
	
	
	
}