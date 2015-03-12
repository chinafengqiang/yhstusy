package com.smartlearning.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.Toast;

import com.smartlearning.R;
import com.smartlearning.biz.AdviodManager;
import com.smartlearning.common.FileUtil;
import com.smartlearning.constant.Global;
import com.smartlearning.model.Adviod;

public class LessonResourceActivity extends Activity {
	private List<Adviod> list;
	String ip = "";
	String serverIp = "";
	private Context context = null;
	private SharedPreferences sharedPreferences;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		try {
			// 去掉Activity上面的状态栏
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN);
			// setContentView(R.layout.activity_lesson_resource);
			setContentView(R.layout.activity_lesson_resource);
			context = this;
			GridView gridview = (GridView) findViewById(R.id.gridview);

			sharedPreferences = getSharedPreferences("userInfo", MODE_PRIVATE);
			ip = sharedPreferences.getString("serverIp", null);
			serverIp = "http://"+ ip +":"+Global.Common_Port;
			
			AdviodManager manager = new AdviodManager(context);
			list = manager.geAdviodByPage(serverIp, 1, 899);
			if (list == null) {
				return;
			}
			ArrayList<HashMap<String, Object>> lstImageItem = new ArrayList<HashMap<String, Object>>();
			for (int i = 0; i < list.size(); i++) {
				Adviod adviod = list.get(i);
				HashMap<String, Object> map = new HashMap<String, Object>();
				String url = FileUtil.getEncodeUrl(adviod.getPic());
				BitmapDrawable pic = new BitmapDrawable(FileUtil.getBitMap(url));
				map.put("ItemImage", pic);
				map.put("ItemText", adviod.getName());
				lstImageItem.add(map);
			}

			// 生成适配器的ImageItem <====> 动态数组的元素，两者一一对应
			SimpleAdapter saImageItems = new SimpleAdapter(this, // 没什么解释
					lstImageItem,// 数据来源
					R.layout.vedio_item,// night_item的XML实现

					// 动态数组与ImageItem对应的子项
					new String[] { "ItemImage", "ItemText" },

					// ImageItem的XML文件里面的一个ImageView,两个TextView ID
					new int[] { R.id.ItemImage, R.id.ItemText });

			saImageItems.setViewBinder(new ViewBinder() {

				public boolean setViewValue(View view, Object data,
						String textRepresentation) {
					// 判断是否为我们要处理的对象
					if (view instanceof ImageView
							&& data instanceof BitmapDrawable) {
						ImageView iv = (ImageView) view;
						iv.setImageDrawable((BitmapDrawable) data);
						return true;
					} else {
						return false;
					}
				}
			});

			// 添加并且显示
			gridview.setAdapter(saImageItems);
			// 添加消息处理
			gridview.setOnItemClickListener(new ItemClickListener());

			// if (list.size() > 0) {
			// RelativeLayout l1 = (RelativeLayout) findViewById(R.id.vedio1);
			// l1.setOnClickListener(new View.OnClickListener() {
			// public void onClick(View v) {
			// openVedio(0);
			// }
			// });
			// // BitmapDrawable background = new
			// //
			// BitmapDrawable(getBitMap("http://hiphotos.baidu.com/baidu/pic/item/7d8aebfebf3f9e125c6008d8.jpg"));
			// l1.setBackground(getBackground(0));
			// }
			//
			// if (list.size() > 1) {
			// LinearLayout l2 = (LinearLayout) findViewById(R.id.vedio2);
			// l2.setOnClickListener(new View.OnClickListener() {
			// public void onClick(View v) {
			// openVedio(1);
			// }
			// });
			// l2.setBackground(getBackground(1));
			// }
			//
			// if (list.size() > 2) {
			// LinearLayout l3 = (LinearLayout) findViewById(R.id.vedio3);
			// l3.setOnClickListener(new View.OnClickListener() {
			// public void onClick(View v) {
			// openVedio(2);
			// }
			// });
			// l3.setBackground(getBackground(2));
			// }
			//
			// if (list.size() > 3) {
			// LinearLayout l4 = (LinearLayout) findViewById(R.id.vedio4);
			// l4.setOnClickListener(new View.OnClickListener() {
			// public void onClick(View v) {
			// openVedio(3);
			// }
			// });
			// l4.setBackground(getBackground(3));
			// }
		} catch (Exception ex) {
			Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
		}
	}

	// 当AdapterView被单击(触摸屏或者键盘)，则返回的Item单击事件
	class ItemClickListener implements OnItemClickListener {
		public void onItemClick(AdapterView<?> arg0,// The AdapterView where the
													// click happened
				View arg1,// The view within the AdapterView that was clicked
				int arg2,// The position of the view in the adapter
				long arg3// The row id of the item that was clicked
		) {
			// 在本例中arg2=arg3
			// HashMap<String, Object> item = (HashMap<String, Object>) arg0
			// .getItemAtPosition(arg2);
			// // 显示所选Item的ItemText
			// setTitle((String) item.get("ItemText"));
			Intent intent = new Intent(LessonResourceActivity.this,
					VedioPlayActivity.class);
			String url = FileUtil.getEncodeUrl(list.get(arg2).getUrl());
			Log.i("learning url", url);
			String name = list.get(arg2).getName();
			intent.putExtra("url", url);
			intent.putExtra("name", name);
			startActivityForResult(intent, 1);
		}
	}

	private BitmapDrawable getBackground(int index) {
		String url = FileUtil.getEncodeUrl(list.get(index).getPic());
		BitmapDrawable background = new BitmapDrawable(FileUtil.getBitMap(url));
		return background;
	}

	private void openVedio(int index) {
		Intent intent = new Intent(LessonResourceActivity.this,
				VedioPlayActivity.class);
		// intent.putExtra("url",
		// "http://192.168.0.20:8080/uploadFile/file/%E9%AB%98%E7%AD%89%E6%95%B0%E5%AD%A6.flv");
		String url = FileUtil.getEncodeUrl(list.get(index).getUrl());
		intent.putExtra("url", url);
		startActivityForResult(intent, 1);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
