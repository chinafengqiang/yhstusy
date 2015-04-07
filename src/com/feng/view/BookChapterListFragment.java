package com.feng.view;

import java.util.ArrayList;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.Request.Method;
import com.feng.tree.TreeElementBean;
import com.feng.tree.TreeViewAdapter;
import com.feng.vo.BookChapterListVO;
import com.feng.volley.FRestClient;
import com.feng.volley.FastJsonRequest;
import com.smartlearning.R;
import com.smartlearning.constant.Global;
import com.smartlearning.utils.CommonUtil;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class BookChapterListFragment extends ListFragment{
		
		private Context mContext;
	
	     @Override
	     public View onCreateView(LayoutInflater inflater,
	     ViewGroup container, Bundle savedInstanceState) {      
	    	 this.mContext = getActivity();
	    	 return inflater.inflate(R.layout.f_book_res_left_memu, container, false);
	     }

	     @Override
	     public void onCreate(Bundle savedInstanceState) {
	          super.onCreate(savedInstanceState);
	       
	     }
	   
	     public void onListItemClick(ListView parent, View v,
	     int position, long id)
	     {         
	          
	     } 
	
	     
//	     private void initTree(){
//	 		
//	 		final ProgressDialog pDialog = new ProgressDialog(mContext);
//	 		pDialog.setMessage("Loading...");
//	 		pDialog.show(); 
//	 		
//	 		String tag_json_obj = "json_obj_req";
//	 		final String url = "http://"+ serverIp +":"+Global.Common_Port+"/api/getBookChapter.html?partId="+partId;
//
//	 		FastJsonRequest<BookChapterListVO>   fastRequest = new FastJsonRequest<BookChapterListVO>(Method.GET,url, BookChapterListVO.class,null, new Response.Listener<BookChapterListVO>() {
//
//	 			@Override
//	 			public void onResponse(BookChapterListVO chapterVO) {
//	 				pDialog.dismiss();
//	 				if(chapterVO != null){
//	 					final ArrayList<TreeElementBean> nodeList = (ArrayList<TreeElementBean>) chapterVO.getBookChapterList();
//	 					treeViewAdapter = new TreeViewAdapter(mContext, R.layout.f_book_res_left_menu_item,nodeList,url);
//	 			        listView.setAdapter(treeViewAdapter);
//	 			        listView.setOnItemClickListener(new OnItemClickListener(){
//	 						public void onItemClick(AdapterView<?> adapter, View view, int position,long id) {
//	 							treeViewAdapter.onClick(position, nodeList, treeViewAdapter);
//	 						}
//	 			        });
//	 				}
//	 			}
//	 		},
//	 		new Response.ErrorListener() {
//
//	 			@Override
//	 			public void onErrorResponse(VolleyError error) {
//	 				// TODO Auto-generated method stub
//	 				 VolleyLog.d(TAG, "Error: " + error.getMessage());
//	 				 //获取本地分类
//	 				 CommonUtil.showToast(mContext, "local category",Toast.LENGTH_SHORT);
//	 				 pDialog.dismiss();
//	 			}
//	 		}
//	 	    );
//	 		
//	 		FRestClient.getInstance(mContext).addToRequestQueue(fastRequest,tag_json_obj);
//
//	 		
//	         
//	 	}
}
