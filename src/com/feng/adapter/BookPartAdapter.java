package com.feng.adapter;

import java.util.List;






import com.feng.vo.BookCategory;
import com.feng.vo.BookCategoryEnum;
import com.feng.vo.BookPart;
import com.smartlearning.R;
import com.smartlearning.ui.FBookPartActivity;
import com.smartlearning.ui.FBookResActivity;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class BookPartAdapter extends BaseAdapter{
	 private Context context;  
	 private List<BookPart> bookPartList;
	 
	 private int categoryId;
	 private String categoryName;
	 
	 public BookPartAdapter(Context context,List<BookPart> bookPartList) {  
		 this.context=context;  
		 this.bookPartList = bookPartList;
	 }  
	 
	 public BookPartAdapter(Context context,List<BookPart> bookPartList,int categoryId,String categoryName) {  
		 this.context=context;  
		 this.bookPartList = bookPartList;
		 this.categoryId = categoryId;
		 this.categoryName = categoryName;
	 } 
	@Override
	public int getCount() {
		 int count = 0;
		 if(bookPartList != null)
			 count = bookPartList.size();
		 return count;  
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	 public View getView(final int position, View view, ViewGroup viewgroup) {  
		  BookPartWrapper wrapper;  
		  if(view==null) {  
		   wrapper = new BookPartWrapper();  
		   LayoutInflater inflater = LayoutInflater.from(context);  
		   view = inflater.inflate(R.layout.f_book_part_item, null);  
		   view.setTag(wrapper);  
		   view.setPadding(15, 15, 15, 15);  //每格的间距  
		  } else {  
		   wrapper = (BookPartWrapper)view.getTag();  
		  }  
		    
		  wrapper.imageView = (ImageView)view.findViewById(R.id.book_part_img);  
		  wrapper.textView = (TextView)view.findViewById(R.id.book_part_desc_text);
		  final BookPart part = bookPartList.get(position);
		  final int id = part.getId();
		  final String name = part.getName();
		  
		  //存放文件所有的分类名称和ID
		  String allNames = categoryName+","+name;
		  String allIds = categoryId+","+id;
		  final String[] alls = new String[]{allIds,allNames};
		  
		  wrapper.textView.setText(name);
		  wrapper.imageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context,FBookResActivity.class);
				intent.putExtra("partId",id);
				intent.putExtra("partName",name);
				intent.putExtra("book_alls", alls);
				context.startActivity(intent);
			}
		  });
		  return view;  
		 }  

}

class BookPartWrapper {  
	 ImageView imageView;  
	 TextView textView;  
	   
}  
