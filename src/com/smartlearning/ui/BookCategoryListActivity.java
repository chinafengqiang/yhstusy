package com.smartlearning.ui;

import java.util.ArrayList;
import java.util.TreeMap;

import com.smartlearning.R;
import com.smartlearning.biz.BookManager;
import com.smartlearning.constant.Global;
import com.smartlearning.model.BookCategoryVo;
import com.smartlearning.model.Request;
import com.smartlearning.parser.BookCategoryParser;
import com.smartlearning.utils.CommonUtil;

import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;


public class BookCategoryListActivity extends BaseActivity{
	private Context mContext;
    private GridView bookShelf;
	private Long classId;
	private String serverIp;
	private Button btn_leftTop,btn_rightTop;
	private SharedPreferences sharedPreferences;
	private List<BookCategoryVo> dataList = new ArrayList<BookCategoryVo>();

	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void findViewById() {
		 bookShelf = (GridView) findViewById(R.id.bookShelf);
		 btn_leftTop = (Button)findViewById(R.id.btn_leftTop);
		 btn_rightTop = (Button)findViewById(R.id.btn_rightTop);
	}

	@Override
	protected void loadViewLayout() {
		mContext = this;
		setContentView(R.layout.book_category_list);
	}

	@Override
	protected void processLogic() {
		sharedPreferences = getSharedPreferences("userInfo", MODE_PRIVATE);
		classId = sharedPreferences.getLong("classId", 0);
		String ip = sharedPreferences.getString("serverIp", null);
		serverIp = "http://"+ ip +":"+Global.Common_Port;
		
		Request request = new Request();
		request.context = mContext;
		request.serverPath = serverIp+"/coursewareController/getPermBooksCategory.html";
		TreeMap<String, String> params = new TreeMap<String, String>();
		params.put("classId", classId+"");
		request.requestDataMap = params;
		request.jsonParser = new BookCategoryParser();
		
		getDataFromServer(request, new DataCallback<List<BookCategoryVo>>() {

			@Override
			public void processData(List<BookCategoryVo> paramObject,
					boolean paramBoolean) {
				if(paramObject != null){
					dataList = paramObject;
					ShlefAdapter adapter=new ShlefAdapter();
			        bookShelf.setAdapter(adapter);
				}else{
					BookManager manager = new BookManager(mContext);
					dataList = manager.getEBooksCategory();
					if(dataList != null){
						ShlefAdapter adapter=new ShlefAdapter();
				        bookShelf.setAdapter(adapter);
					}
				}
			}
			
		});
	}

	@Override
	protected void setListener() {
		 bookShelf.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int position,
						long arg3) {
					// TODO Auto-generated method stub
					if(position>=dataList.size()){
						CommonUtil.showToast(mContext, "非法操作数据",Toast.LENGTH_LONG);
					}else{
					   if(dataList != null){
						   BookCategoryVo vo = dataList.get(position);
						   Intent intent = new Intent(mContext,PagerListActivity.class);
						   intent.putExtra("category",vo.categoryId);
						   intent.putExtra("categoryName",vo.categoryName);
						   startActivity(intent);
					   }
					}
				}
			});
		 
		 btn_rightTop.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				comeBack();
			}
		});
		 
		 btn_leftTop.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				comeBack();
			}
		});
	}
	
	private void comeBack(){
		this.finish();
	}
	 class ShlefAdapter extends BaseAdapter{

			@Override
			public int getCount() {
				return dataList.size();
			}

			@Override
			public Object getItem(int position) {
				return dataList.get(position);
			}

			@Override
			public long getItemId(int position) {
				return position;
			}

			@Override
			public View getView(int position, View contentView, ViewGroup arg2) {
				// TODO Auto-generated method stub
				BookCategoryVo vo = dataList.get(position);
				contentView=LayoutInflater.from(getApplicationContext()).inflate(R.layout.item1, null);
				
				TextView view=(TextView) contentView.findViewById(R.id.imageView1);
				
				view.setText(vo.categoryName);
				view.setBackgroundResource(R.drawable.cover_txt);
				
				return contentView;
			}
	    	
	    }
	    
}
