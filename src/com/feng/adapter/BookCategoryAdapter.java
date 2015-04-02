package com.feng.adapter;

import com.smartlearning.R;
import com.smartlearning.utils.CommonUtil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class BookCategoryAdapter extends BaseAdapter {  
	 private Context context;  
	   
	 public BookCategoryAdapter(Context context) {  
	  this.context=context;  
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
	  return images.length;  
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
	  wrapper.imageView.setBackgroundResource(images[position]);  
	  //wrapper.textView = (TextView)view.findViewById(R.id.MainActivityText);  
	 // wrapper.textView.setText(texts[position]);  
	  wrapper.imageView.setOnClickListener(new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			CommonUtil.showToast(context, position+"", Toast.LENGTH_LONG);
		}
	  });
	  return view;  
	 }  
	}  
	 
	class ImgTextWrapper {  
	 ImageView imageView;  
	 TextView textView;  
	   
	}  