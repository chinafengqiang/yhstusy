package com.feng.adapter;

import com.feng.vo.BookCategory;
import com.feng.vo.BookCategoryEnum;
import com.smartlearning.R;
import com.smartlearning.ui.FBookPartActivity;
import com.smartlearning.utils.CommonUtil;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class BookCategoryAdapter extends BaseAdapter {  
	 private Context context;  
	 private List<BookCategory> bookCategoryList;
	 
	 
	 public BookCategoryAdapter(Context context,List<BookCategory> bookCategoryList) {  
		 this.context=context;  
		 this.bookCategoryList = bookCategoryList;
	 }  

	 private Integer[] images = {  
	   //九宫格图片的设置  
	   R.drawable.bc_53,  
	   R.drawable.bc_53,  
	   R.drawable.bc_53, 
	   R.drawable.bc_53, 
	   };  
	   
	 private String[] texts = {  
	   //九宫格图片下方文字的设置  
	   
	   };  
	   
	 //get the number  
	 @Override  
	 public int getCount() {  
		 int count = 0;
		 if(bookCategoryList != null)
			 count = bookCategoryList.size();
		 return count;  
	 }  
	 
	 @Override  
	 public Object getItem(int position) {  
	  return position;  
	 }  
	 
	 //get the current selector's id number  
	 @Override  
	 public long getItemId(int position) {  
	  return position;  
	 }  
	 
	 //create view method  
	 @Override  
	 public View getView(final int position, View view, ViewGroup viewgroup) {  
	  ImgTextWrapper wrapper;  
	  if(view==null) {  
	   wrapper = new ImgTextWrapper();  
	   LayoutInflater inflater = LayoutInflater.from(context);  
	   view = inflater.inflate(R.layout.f_book_category_item, null);  
	   view.setTag(wrapper);  
	   view.setPadding(15, 15, 15, 15);  //每格的间距  
	  } else {  
	   wrapper = (ImgTextWrapper)view.getTag();  
	  }  
	    
	  wrapper.imageView = (ImageView)view.findViewById(R.id.MainActivityImage);  
	 
	  final BookCategory category = bookCategoryList.get(position);
	  final int id = category.getId();
	  final int image = BookCategoryEnum.getValue(id+"");
	  final String name = category.getName();
	  
	  wrapper.imageView.setBackgroundResource(image);  
	  wrapper.imageView.setOnClickListener(new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(context,FBookPartActivity.class);
			intent.putExtra("categoryId",id);
			intent.putExtra("categoryName",name);
			context.startActivity(intent);
		}
	  });
	  return view;  
	 }  
	}  
	 
	class ImgTextWrapper {  
	 ImageView imageView;  
	 TextView textView;  
	   
	}  