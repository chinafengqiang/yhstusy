package com.feng.fragment;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.Request.Method;
import com.feng.adapter.BookResAdapter;
import com.feng.tree.TreeElementBean;
import com.feng.vo.BookChapterListVO;
import com.feng.vo.BookRes;
import com.feng.vo.BookResListVO;
import com.feng.volley.FRestClient;
import com.feng.volley.FastJsonRequest;
import com.smartlearning.R;
import com.smartlearning.biz.BookManager;
import com.smartlearning.constant.Global;
import com.smartlearning.task.DownFileTask;
import com.smartlearning.utils.CommonUtil;
import com.smartlearning.utils.FileUtil;
import com.smartlearning.utils.SpUtil;
import com.smartlearning.utils.Tool;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

public class BookResListFragment extends Fragment{
	public static final String PART_ID = "partId";
	public static final String CATEGORY_ID = "categoryId";
	public static final String CATEGORY_NAME = "categoryName";
	public static final String ALL_IDS = "allIds";
	public static final String ALL_NAMES = "allNames";
	private View mBaseView;
	private Context mContext;
	private String serverIp;
	private String serverUrl;
	private SharedPreferences sp;
	
	private ListView listView;
	private LinearLayout book_none;
	
	private BookResAdapter adapter = null;
	private String fileName = "";
	private String allIds = "";
	private String allNames = "";
	private String categoryName;
	private BookRes nowDownloadEBook;
	private List<BookRes> bookList = new ArrayList<BookRes>();
	
	private BookManager bookManager = null;
	
	private int userId = 0;
	private int islocal = 0;
	
	private String[] is = { "删除", "取消" };
	String rootPath = Environment.getExternalStorageDirectory().getPath()
			+ "/myBook";
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContext = getActivity();
		mBaseView = inflater.inflate(R.layout.f_book_res_center, null);
		
		int partId = getArguments().getInt(PART_ID,0);
		int categoryId = getArguments().getInt(CATEGORY_ID,0);
		categoryName = getArguments().getString(CATEGORY_NAME);
		allIds = getArguments().getString(ALL_IDS);
		allNames = getArguments().getString(ALL_NAMES);
				
		findView();
		
		initSp();
		
		setListener();
		
		loadData(partId, categoryId);
		
		return mBaseView;
	}
	
	public static Fragment newInstance(int partId,int categoryId,String categoryName,String allIds,String allNames){
		BookResListFragment fragment = new BookResListFragment();
		Bundle bundle = new Bundle();
		bundle.putInt(PART_ID,partId);
		bundle.putInt(CATEGORY_ID,categoryId);
		bundle.putString(CATEGORY_NAME,categoryName);
		bundle.putString(ALL_IDS,allIds);
		bundle.putString(ALL_NAMES,allNames);
		fragment.setArguments(bundle);
		return fragment;
	}
	
	private void findView(){
		listView = (ListView)mBaseView.findViewById(R.id.listView);
		book_none = (LinearLayout)mBaseView.findViewById(R.id.book_none);
	}
	
	private void setListener(){
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				if (id != -1) {
					String fileUrl = "";
					try {

						nowDownloadEBook = bookList.get(position);
						fileName = getDownLoadFileName(nowDownloadEBook.getResUrl());
						fileUrl = URLEncoder.encode(fileName, "UTF-8");
					} catch (UnsupportedEncodingException e1) {
					}
					//Log.i("fileUrl", "fileUrlfile:==================== "+ fileUrl);
					String url = serverUrl + "/uploadFile/file/" + fileUrl;
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
							BookRes ebook = bookList.get(position);
							fileName = getDownLoadFileName(ebook.getResUrl());
							if (ebook.isLocalFile() || FileUtil.isExists("/sdcard/myBook/" + fileName)) {
								File f = new File(rootPath + "/" + fileName);
								deleteFile(f);
								
								int id = ebook.getResId();
								bookManager = new BookManager(mContext);
								try {
									bookManager.removeEbook(id);
								} catch (Exception e) {
									
								}
								
								bookList.remove(position);
								
								if(adapter != null){
									adapter.notifyDataSetChanged();
								}
								
								CommonUtil.showToast(mContext,is[which],Toast.LENGTH_LONG);
							}else{
								CommonUtil.showToast(mContext, "非本地文件不能进行删除操作",Toast.LENGTH_LONG);
							}
							break;
						case 1:
							CommonUtil.showToast(mContext,is[which],Toast.LENGTH_LONG);
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
	
	private void initSp(){
		sp = SpUtil.getSharePerference(mContext);
		serverIp = sp.getString("serverIp","");
		serverUrl = "http://" + serverIp + ":" + Global.Common_Port;
		String strUserId = sp.getString("user","0");
		userId = Integer.parseInt(strUserId);
		islocal = sp.getInt("book_is_local",0);
	}
	
	private void loadData(final int partId,final int categoryId){
		if(islocal == 1){
			class GetBookRes extends AsyncTask<Boolean,Integer,Boolean>{

				@Override
				protected Boolean doInBackground(Boolean... params) {
					BookManager bookManager = new BookManager(mContext);
					bookList = bookManager.getBookRes(userId,partId+"#"+categoryId);
				    return true;
				}

				@Override
				protected void onPreExecute() {
					super.onPreExecute();
				}

				@Override
				protected void onPostExecute(Boolean result) {
					super.onPostExecute(result);
					 if(bookList == null || bookList.size() == 0){
					    	CommonUtil.showToast(mContext, getString(R.string.local_book_res_isnull), Toast.LENGTH_LONG);
					    	 book_none.setVisibility(View.VISIBLE);
					 }else{
						  book_none.setVisibility(View.GONE);
						  adapter = new BookResAdapter(bookList, mContext,categoryName);
						  listView.setAdapter(adapter);
					 }
				}
				
			}
			
			new GetBookRes().execute(true);
		}else{
			final ProgressDialog pDialog = new ProgressDialog(mContext);
			pDialog.setMessage("Loading...");
			pDialog.show(); 
			
			String tag_json_obj = "json_obj_req";
			String url = "http://" + serverIp + ":" + Global.Common_Port+"/api/getBookRes.html?partId="+partId+"&categoryId="+categoryId;
			
			FastJsonRequest<BookResListVO>   fastRequest = new FastJsonRequest<BookResListVO>(Method.GET,url, BookResListVO.class,null, new Response.Listener<BookResListVO>() {

				@Override
				public void onResponse(BookResListVO resVO) {
					pDialog.dismiss();
					if (resVO == null || resVO.getBookResList().size() <= 0) {
						CommonUtil.showToast(mContext, "对不起，最新电子还没公布", Toast.LENGTH_LONG);
						return;
					} 
					
					book_none.setVisibility(View.GONE);
					
					bookList.addAll(resVO.getBookResList());
					adapter = new BookResAdapter(bookList, mContext,categoryName);
					listView.setAdapter(adapter);
				}
			},
			new Response.ErrorListener() {

				@Override
				public void onErrorResponse(VolleyError error) {
					// TODO Auto-generated method stub
					 VolleyLog.d("BookResListFragment", "Error: " + error.getMessage());
					 pDialog.dismiss();
				}
			}
		    );
			
			FRestClient.getInstance(mContext).addToRequestQueue(fastRequest,tag_json_obj);
		}
	}
	
	private void downloadFiles(final String url) {
		String extName = FileUtil.getExtName(fileName);
		if (!extName.equalsIgnoreCase(".pdf") ){
			//Tool.ShowMessage(mContext, "非法资料，非pdf格式");
			CommonUtil.showToast(mContext,"非法资料，非pdf格式",Toast.LENGTH_SHORT);
			return;
		}
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
	
	private void intentForward(final String fileName) {
		Intent intent = new Intent();
		intent.setAction(android.content.Intent.ACTION_VIEW);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		File file = new File(fileName);
		Uri path = Uri.fromFile(file);
		intent.setDataAndType(path, "application/pdf");
		startActivity(intent);
	};
	
	private Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			final String fileName = msg.obj.toString();
			String extName = FileUtil.getExtName(fileName);
			if (extName.equalsIgnoreCase(".pdf") ) {
				if(bookManager == null)
					bookManager = new BookManager(mContext);
				nowDownloadEBook.setLocalFile(true);
				if(adapter != null)
					adapter.notifyDataSetChanged();
				
				nowDownloadEBook.setAllIds(allIds);
				nowDownloadEBook.setAllNames(allNames);
				bookManager.insertBook(userId,nowDownloadEBook);
				
			}else{
				Tool.ShowMessage(mContext, "非法资料，非pdf格式");
			}
		}

	};
	
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
}
