package com.feng.adapter;

import java.util.List;

import com.feng.vo.BookRes;
import com.smartlearning.R;
import com.smartlearning.utils.FileUtil;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class BookResAdapter extends BaseAdapter{
	private Context context = null;
	private List<BookRes> list = null;
	private String categoryName;
	
	public BookResAdapter(Context context) {
		this.context = context;
	}
	
	public BookResAdapter(List<BookRes> list, Context context,String categoryName){
		this.list = list;
		this.context = context;
		this.categoryName = categoryName;
	}
	
	public BookResAdapter(List<BookRes> list, Context context){
		this.list = list;
		this.context = context;
	}
	
	@Override
	public int getCount() {
		if (list != null) {
			return list.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		final BookRes book = list.get(position);
		String fileName = getDownLoadFileName(book.getResUrl());
		if (FileUtil.isExists("/sdcard/myBook/"+ fileName)) {
			book.setLocalFile(true);
		}
		if(convertView == null){
			convertView = View.inflate(context, R.layout.f_book_res_center_item,null);
			holder = new ViewHolder();
			holder.bookName = (TextView) convertView.findViewById(R.id.bookName);
			holder.bookCrateTime = (TextView) convertView.findViewById(R.id.bookCrateTime);
			holder.bookStatus = (TextView) convertView.findViewById(R.id.bookStatus);
			holder.bookCategory = (TextView)convertView.findViewById(R.id.bookCategory);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		holder.bookName.setText(book.getResName());
		holder.bookCrateTime.setText(context.getString(R.string.book_crete_time)+book.getResCreateTime());
		String status = context.getString(R.string.book_not_download);
		if(book.isLocalFile()){ 
			status = context.getString(R.string.book_download);
		}
		holder.bookStatus.setText(status);
		holder.bookCategory.setText(context.getString(R.string.book_category)+this.categoryName);
		return convertView;
	}
	
	public final class ViewHolder {
		public TextView bookName;
		public TextView bookCrateTime;
		public TextView bookStatus;
		public TextView bookCategory;
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
}
