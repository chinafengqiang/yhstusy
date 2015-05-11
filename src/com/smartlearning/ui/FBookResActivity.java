package com.smartlearning.ui;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.Request.Method;
import com.feng.adapter.BookPartAdapter;
import com.feng.fragment.BookResListFragment;
import com.feng.fragment.VideoResListFragment;
import com.feng.fragment.listener.IResFragmentListener;
import com.feng.tree.TreeElementBean;
import com.feng.tree.TreeViewAdapter;
import com.feng.util.StringUtils;
import com.feng.view.SearchBarView;
import com.feng.view.SlidingMenu;
import com.feng.vo.BookChapterListVO;
import com.feng.vo.BookPartListVO;
import com.feng.volley.FRestClient;
import com.feng.volley.FastJsonRequest;
import com.smartlearning.R;
import com.smartlearning.biz.BookManager;
import com.smartlearning.constant.Global;
import com.smartlearning.utils.CommonUtil;
import com.smartlearning.utils.SpUtil;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class FBookResActivity extends FragmentActivity{
	private static final String TAG = FBookResActivity.class.getSimpleName();
	private Context mContext;
	private SharedPreferences sp;
	private String serverIp;
	private long classId;
	private int partId;
	private String partName;
	
	private String[] alls;
	
	private int islocal = 0;
	
	private int moduleId = 0;//判断是电子书还是视频教程 0:电子书、1：视频教程
	
	private BookManager bookManager = null;
	private List<TreeElementBean> chapterList = null;
	
	private ProgressDialog pd;
	
	@InjectView(R.id.title_refresh) LinearLayout titleRefresh;
	@InjectView(R.id.title_back_index) LinearLayout titleBackIndex;
	@InjectView(R.id.title_text) TextView titleText;
	@InjectView(R.id.id_menu) SlidingMenu mMenu;
	@InjectView(R.id.title_book_chapter) LinearLayout bookChapter;
	@InjectView(R.id.treeList) ListView listView;
	@InjectView(R.id.search_bar) SearchBarView  searchBarView;
	
	
	//nodelist
	//private ArrayList<TreeElementBean> nodeList = new ArrayList<TreeElementBean>();
	//樹形數據適配器
	private TreeViewAdapter treeViewAdapter = null;
	
	private IResFragmentListener mIResFragmentListener;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.f_book_res);
		
		mContext = this;
		
		ButterKnife.inject(this);
		
		initSp();
		
		initTitle();
		
		initTree();
		
		initSearchBarView();
	}
	
	

	public void toggleMenu(View view)
	{
		mMenu.toggle();
	}
	
	private void initSp(){
		sp = SpUtil.getSharePerference(mContext);
		serverIp = sp.getString("serverIp","");
		classId = sp.getLong("classId",0);
		
		islocal = sp.getInt("book_is_local",0);
		
		moduleId = sp.getInt("module_id",0);
		
		partId = getIntent().getIntExtra("partId",0);
		partName = getIntent().getStringExtra("partName");
		alls = getIntent().getStringArrayExtra("book_alls");
	}
	
	private void initTitle(){
		if(islocal == 1){
			titleText.setText(getString(R.string.local_book_index)+partName);
		}else{
			titleText.setText(partName);
		}
		
		titleRefresh.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mIResFragmentListener != null){
					mIResFragmentListener.loadData();
				}else{
					CommonUtil.showToast(mContext,"已完成刷新",Toast.LENGTH_LONG);
				}
			}
		});
		
		titleBackIndex.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//Intent intent = new Intent(mContext,FBookPartActivity.class);
				//startActivity(intent);
				//finish();
				onBackPressed();
			}
		});
	}

	
	private void initSearchBarView(){
			searchBarView.setBtnSearchOnClickListener(searchListener);
			//searchBarView.setBtnRefreshOnClickListener(refreshListener);
	}
	
	private OnClickListener searchListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			String value = searchBarView.getSearchValue();
			if(StringUtils.isBlank(value)){
				CommonUtil.showToast(mContext, "请输入要搜索的内容",Toast.LENGTH_LONG);
				return;
			}
			
			if(mIResFragmentListener != null){
				mIResFragmentListener.searchRes(value);
			}else{
				FragmentManager fm = getSupportFragmentManager();
				FragmentTransaction ft = fm.beginTransaction();
				if(moduleId == 1){
					VideoResListFragment vf = new VideoResListFragment();
					vf.setSearchValue(true, value);
					ft.replace(R.id.book_res_content,vf,"VideoResListFragment");
					ft.commit();
				}else{
					BookResListFragment bf = new BookResListFragment();
					bf.setSearchValue(true, value);
					ft.replace(R.id.book_res_content,bf,"BookResListFragment");
					ft.commit();
				}

			}
		}
		
	};
	
//	private OnClickListener refreshListener = new OnClickListener() {
//		@Override
//		public void onClick(View v) {
//			// TODO Auto-generated method stub
//			if(mIResFragmentListener != null){
//				mIResFragmentListener.loadData();
//			}else{
//				CommonUtil.showToast(mContext,"已完成刷新",Toast.LENGTH_LONG);
//			}
//		}
//	};
	
	/**
	 * 提标框
	 */
	private void showProgressDialog() {
		if (pd == null) {
			pd = new ProgressDialog(mContext);
			pd.setMessage("Loading...");
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
	
	private void initTree(){
		if(islocal == 1){
			new GetLocalBookChapter().execute(true);
		}else{
			final ProgressDialog pDialog = new ProgressDialog(mContext);
			pDialog.setMessage("Loading...");
			pDialog.show(); 
			
			String tag_json_obj = "json_obj_req";
			final String url = "http://"+ serverIp +":"+Global.Common_Port+"/api/getBookChapter.html?partId="+partId;

			FastJsonRequest<BookChapterListVO>   fastRequest = new FastJsonRequest<BookChapterListVO>(Method.GET,url, BookChapterListVO.class,null, new Response.Listener<BookChapterListVO>() {

				@Override
				public void onResponse(BookChapterListVO chapterVO) {
					pDialog.dismiss();
					if(chapterVO != null){
						final ArrayList<TreeElementBean> nodeList = (ArrayList<TreeElementBean>) chapterVO.getBookChapterList();
						treeViewAdapter = new TreeViewAdapter(mContext, R.layout.f_book_res_left_menu_item,nodeList,url,alls,partId,moduleId);
				        listView.setAdapter(treeViewAdapter);
				        listView.setOnItemClickListener(new OnItemClickListener(){
							public void onItemClick(AdapterView<?> adapter, View view, int position,long id) {
								FragmentManager fm = getSupportFragmentManager();
								FragmentTransaction ft = fm.beginTransaction();
								treeViewAdapter.onClick(position, nodeList, treeViewAdapter,ft,R.id.book_res_content
										,mMenu,titleText); 
							}
				        });
					}
				}
			},
			new Response.ErrorListener() {

				@Override
				public void onErrorResponse(VolleyError error) {
					// TODO Auto-generated method stub
					 VolleyLog.d(TAG, "Error: " + error.getMessage());
					 //获取本地分类
					 CommonUtil.showToast(mContext,getString(R.string.get_online_category_fail),Toast.LENGTH_SHORT);
					 pDialog.dismiss();
				}
			}
		    );
			
			FRestClient.getInstance(mContext).addToRequestQueue(fastRequest,tag_json_obj);
		}
		
	
        
	}
	
	private class GetLocalBookChapter extends AsyncTask<Boolean,Integer,Boolean>{

		@Override
		protected Boolean doInBackground(Boolean... params) {
			bookManager = new BookManager(mContext);
			chapterList = bookManager.getBookChapterTree(partId, 0);
			return true;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showProgressDialog();
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			hideProgressDialog();
			if(chapterList == null || chapterList.size() == 0){
				CommonUtil.showToast(mContext, getString(R.string.local_book_chapter_isnull), Toast.LENGTH_LONG);
			}else{
				final ArrayList<TreeElementBean> nodeList = (ArrayList<TreeElementBean>)chapterList;
				treeViewAdapter = new TreeViewAdapter(mContext, R.layout.f_book_res_left_menu_item,chapterList,"",alls,partId,moduleId);
		        listView.setAdapter(treeViewAdapter);
		        listView.setOnItemClickListener(new OnItemClickListener(){
					public void onItemClick(AdapterView<?> adapter, View view, int position,long id) {
						FragmentManager fm = getSupportFragmentManager();
						FragmentTransaction ft = fm.beginTransaction();
						treeViewAdapter.onClick(position, nodeList, treeViewAdapter,ft,R.id.book_res_content
								,mMenu,titleText);
					}
		        });
			}
		}
		
	}

	@Override
	public void onAttachFragment(Fragment fragment) {
		try {
			mIResFragmentListener = (IResFragmentListener)fragment;
		} catch (Exception e) {
			// TODO: handle exception
		}
		super.onAttachFragment(fragment);
	}




	
	
	
}
