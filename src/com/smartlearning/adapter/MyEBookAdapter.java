package com.smartlearning.adapter;

import java.util.List;

import com.smartlearning.R;
import com.smartlearning.model.EBook;
import com.smartlearning.utils.DateUtil;
import com.smartlearning.utils.ImageTools;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class MyEBookAdapter extends BaseAdapter{

	private Context context = null;
	private List<EBook> list = null;

	public MyEBookAdapter(Context context) {
		this.context = context;
	}
	
	public MyEBookAdapter(List<EBook> list, Context context){
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
		final EBook book = list.get(position);
		if(convertView == null){
			convertView = View.inflate(context, R.layout.videocenter_list_item,null);
			holder = new ViewHolder();
			holder.videoImage = (ImageView) convertView.findViewById(R.id.image);
			holder.name = (TextView) convertView.findViewById(R.id.videoName);
			holder.category = (TextView) convertView.findViewById(R.id.videoCategory);
			holder.attention = (TextView) convertView.findViewById(R.id.lectuer);
			//holder.category_title = (TextView) convertView.findViewById(R.id.category_title);
			holder.is_local_file = (TextView)convertView.findViewById(R.id.is_local_file);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		
		
		byte[] result = book.getImageUrl();
		if(result != null&&result.length>0){
			Bitmap bitmap = ImageTools.getBitmapFromByte(result);
			if(bitmap!= null){
				holder.videoImage.setImageBitmap(bitmap);
			} 
		}
		holder.name.setText(book.getName());
		holder.category.setText("分类 ：" + book.getCategoryName());
		holder.attention.setText("时间：" +DateUtil.dateToChineseString(DateUtil.stringsToDate(book.getTime()),true));
		/*String ctgname1=book.getCategoryName();
		String ctgname2=position-1>=0?list.get(position-1).getCategoryName():"";
		if (!ctgname1.equals(ctgname2)) {
			holder.category_title.setVisibility(View.VISIBLE);
			holder.category_title.setText("  "+ctgname1);
		}else{
			holder.category_title.setVisibility(View.GONE);
		}*/
		boolean isLocal = book.isLocalFile();
		if(isLocal){
			holder.is_local_file.setVisibility(View.VISIBLE);
		}
		return convertView;
	}

	public final class ViewHolder {
		public ImageView videoImage;
		public TextView name;
		public TextView category;
		public TextView attention;
		public TextView category_title;
		public TextView is_local_file;
	}
}
