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
import com.feng.tree.TreeElementBean;
import com.feng.tree.TreeViewAdapter;
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
	private BookManager bookManager = null;
	private List<TreeElementBean> chapterList = null;
	
	private ProgressDialog pd;
	
	@InjectView(R.id.title_back) LinearLayout title;
	@InjectView(R.id.title_back_index) LinearLayout titleBackIndex;
	@InjectView(R.id.title_text) TextView titleText;
	@InjectView(R.id.id_menu) SlidingMenu mMenu;
	@InjectView(R.id.title_book_chapter) LinearLayout bookChapter;
	@InjectView(R.id.treeList) ListView listView;
	
	
	//nodelist
	//private ArrayList<TreeElementBean> nodeList = new ArrayList<TreeElementBean>();
	//樹形數據適配器
	private TreeViewAdapter treeViewAdapter = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.f_book_res);
		
		mContext = this;
		
		ButterKnife.inject(this);
		
		initSp();
		
		initTitle();
		
		initTree();
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
		
		title.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		
		titleBackIndex.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext,FBookCategoryActivity.class);
				startActivity(intent);
				//finish();
			}
		});
	}

	
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
						treeViewAdapter = new TreeViewAdapter(mContext, R.layout.f_book_res_left_menu_item,nodeList,url,alls,partId);
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
				treeViewAdapter = new TreeViewAdapter(mContext, R.layout.f_book_res_left_menu_item,chapterList,"",alls,partId);
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
	
}
