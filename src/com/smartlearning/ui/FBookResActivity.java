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
import com.smartlearning.constant.Global;
import com.smartlearning.utils.CommonUtil;
import com.smartlearning.utils.SpUtil;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class FBookResActivity extends Activity{
	private static final String TAG = FBookResActivity.class.getSimpleName();
	private Context mContext;
	private SharedPreferences sp;
	private String serverIp;
	private long classId;
	private int partId;
	private String partName;
	
	@InjectView(R.id.title_back) LinearLayout title;
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
		
		partId = getIntent().getIntExtra("partId",0);
		partName = getIntent().getStringExtra("partName");
	}
	
	private void initTitle(){
		titleText.setText(partName);
		title.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
	}

	
	private void initTree(){
		
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
					treeViewAdapter = new TreeViewAdapter(mContext, R.layout.f_book_res_left_menu_item,nodeList,url);
			        listView.setAdapter(treeViewAdapter);
			        listView.setOnItemClickListener(new OnItemClickListener(){
						public void onItemClick(AdapterView<?> adapter, View view, int position,long id) {
							treeViewAdapter.onClick(position, nodeList, treeViewAdapter);
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
				 CommonUtil.showToast(mContext, "local category",Toast.LENGTH_SHORT);
				 pDialog.dismiss();
			}
		}
	    );
		
		FRestClient.getInstance(mContext).addToRequestQueue(fastRequest,tag_json_obj);

		
        
	}
	
	
	
}
