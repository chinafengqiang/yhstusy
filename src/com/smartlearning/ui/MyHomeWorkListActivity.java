package com.smartlearning.ui;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.smartlearning.R;
import com.smartlearning.utils.Tool;

/**
 * 我的作业题
 * @author Administrator
 */
public class MyHomeWorkListActivity extends Activity {
	/*
	 * 对象声明 items：存放显示的名称 paths：存放文件路径 rootPath：起始目录
	 */

	private List<String> items = null;
	private List<String> paths = null;
	private String rootPath = "/";
	private ListView list = null;
	private TextView mPath;

	private Button common_back;

	private String[] is = { "打开", "删除", "取消" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_homework_download);
		/* 初始化mPath，用以显示目前路径 */
		rootPath = Environment.getExternalStorageDirectory().getPath() + "/myHomeWork";
		
		common_back = (Button) this.findViewById(R.id.common_back);
		mPath = (TextView) findViewById(R.id.mPath);

		list = (ListView) this.findViewById(R.id.list);
		list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					final int position, long id) {
				AlertDialog.Builder builder = new AlertDialog.Builder(MyHomeWorkListActivity.this);
				builder.setTitle("请选择操作");
				builder.setItems(is, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case 0:
							File file = new File(paths.get(position));
							if (file.isDirectory()) {
								/* 如果是文件夹就运行getFileDir() */
								getFileDir(paths.get(position));
							} else {
								/* 如果是文件调用fileHandle() */
								String t = getMIMEType(file);
								if (t.equals("video")) {
									intentForward("/sdcard/myHomeWork/"+getMIMETypeName(file));
								}
							}
							break;
						case 1:
							File f = new File(paths.get(position));
							deleteFile(f);
							getFileDir(rootPath);
							Tool.ShowMessage(MyHomeWorkListActivity.this, is[which]);
							break;
						case 2:
							Tool.ShowMessage(MyHomeWorkListActivity.this, is[which]);
							break;
						default:
							break;
						}
					}
				}).create().show();
				return false;
			}
		});
		getFileDir(rootPath);

		common_back.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
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
	
	/* 取得文件架构的method */
	private void getFileDir(String filePath) {
		/* 设定目前所存路径 */
	//	mPath.setText("当前路径：" + filePath);
		items = new ArrayList<String>();
		paths = new ArrayList<String>();
		File f = new File(filePath);
		if(!f.exists()){
			f.mkdirs();
		}

		File[] files = f.listFiles();

//		if (!filePath.equals(rootPath)) {
//			/* 第一笔设定为[并到根目录] */
//			items.add("b1");
//			paths.add(rootPath);
//			/* 第二笔设定为[并勺层] */
//			items.add("b2");
//			paths.add(f.getParent());
//		}
		/* 将所有文件放入ArrayList中 */
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			String type = getMIMEType(file);
			if (!type.equals("pdf") || file.isDirectory()) {
				items.add(file.getName());
				paths.add(file.getPath());
			}
		}

		/* 使用自定义的MyAdapter来将数据传入ListActivity */
		list.setAdapter(new MyAdapter(this, items, paths));

		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				File file = new File(paths.get(position));
				if (file.isDirectory()) {
					/* 如果是文件夹就运行getFileDir() */
					getFileDir(paths.get(position));
				} else {
					/* 如果是文件调用fileHandle() */
					String t = getMIMEType(file);
					if (t.equals("video")) {
						intentForward("/sdcard/myHomeWork/"+getMIMETypeName(file));
					}
				}
			}
		});
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
	
	/* 判断文件MimeType的method */
	private String getMIMEType(File f) {
		String type = "";
		String fName = f.getName();
		/* 取得扩展名 */
		String end = fName
				.substring(fName.lastIndexOf(".") + 1, fName.length())
				.toLowerCase();

		/* 按扩展名的类型决定MimeType */
		if (end.equals("pdf")) {
			type = "video";
		} else {
			type = "*";
		}
		return type;
	}
	
	/* 判断文件MimeType的method */
	private String getMIMETypeName(File f) {
		String type = "";
		String fName = f.getName();
		return fName;
	}

	/**
	 * 自定义
	 */
	class MyAdapter extends BaseAdapter {
		/*
		 * 变量声明 mIcon1：并到根目录的图文件 mIcon2：并到第几层的图片 mIcon3：文件夹的图文件 mIcon4：文件的图片
		 */
		private LayoutInflater mInflater;
		private Bitmap mIcon1;
		private Bitmap mIcon2;
		private Bitmap mIcon3;
		private Bitmap mIcon4;
		private List<String> items;
		private List<String> paths;

		/* MyAdapter的构造符，传入三个参数 */
		public MyAdapter(Context context, List<String> it, List<String> pa) {
			/* 参数初始化 */
			mInflater = LayoutInflater.from(context);
			items = it;
			paths = pa;
			mIcon1 = BitmapFactory.decodeResource(context.getResources(),
					R.drawable.file_back_disable);
			mIcon2 = BitmapFactory.decodeResource(context.getResources(),
					R.drawable.file_back_normal);
			mIcon3 = BitmapFactory.decodeResource(context.getResources(),
					R.drawable.folder);
			mIcon4 = BitmapFactory.decodeResource(context.getResources(),
					R.drawable.zouye);
		}

		/* 继承BaseAdapter，需重写method */
		@Override
		public int getCount() {
			return items.size();
		}

		@Override
		public Object getItem(int position) {
			return items.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;

			if (convertView == null) {
				/* 使用告定义的file_row作为Layout */
				convertView = mInflater.inflate(R.layout.file_row, null);
				/* 初始化holder的text与icon */
				holder = new ViewHolder();
				holder.text = (TextView) convertView.findViewById(R.id.text);
				holder.textsize = (TextView) convertView
						.findViewById(R.id.textsize);
				holder.icon = (ImageView) convertView.findViewById(R.id.icon);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			File f = new File(paths.get(position));
			/* 设定[并到根目录]的文字与icon */
			if (items.get(position).equals("b1")) {
				holder.text.setText("Back to /");
				holder.icon.setImageBitmap(mIcon1);
				holder.textsize.setVisibility(View.GONE);
			}
			/* 设定[并到第几层]的文字与icon */
			else if (items.get(position).equals("b2")) {
				holder.text.setText("Back to ..");
				holder.icon.setImageBitmap(mIcon2);
				holder.textsize.setVisibility(View.GONE);
			}
			/* 设定[文件或文件夹]的文字与icon */
			else {
				String name = f.getName();
				String subName = name.substring(0, name.lastIndexOf("."));
				holder.text.setText(subName);
				if (f.isDirectory()) {
					holder.icon.setImageBitmap(mIcon3);
				} else {
					holder.icon.setImageBitmap(mIcon4);
					holder.textsize.setVisibility(View.VISIBLE);
					holder.textsize.setText(size2string(f.length()));
				}
			}
			return convertView;
		}

		/* class ViewHolder */
		private class ViewHolder {
			TextView text;
			TextView textsize;
			ImageView icon;
		}
	}

	private String size2string(long size) {
		DecimalFormat df = new DecimalFormat("0.00");
		String mysize = "";
		if (size > 1024 * 1024) {
			mysize = df.format(size / 1024f / 1024f) + "M";
		} else if (size > 1024) {
			mysize = df.format(size / 1024f) + "K";
		} else {
			mysize = size + "B";
		}
		return mysize;
	}
}
