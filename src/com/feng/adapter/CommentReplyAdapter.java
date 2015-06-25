package com.feng.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.feng.util.StringUtils;
import com.feng.vo.OnlineMessageReply;
import com.feng.volley.FRestClient;
import com.smartlearning.R;
import com.smartlearning.ui.ShowImageActivity;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CommentReplyAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private List<OnlineMessageReply> list; // 二级评论数据
	private ViewHolder viewHolder;
	private Context context;
	private String serverPath;
	ImageLoader imageLoader;
	public CommentReplyAdapter(Context context,
			List<OnlineMessageReply> list) {
		inflater = LayoutInflater.from(context);
		this.list = list;
		this.context = context;
		imageLoader = FRestClient.getInstance(context).getImageLoader();
	}
	
	public void setServerPath(String serverPath){
		this.serverPath = serverPath;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	public void clearList() {
		this.list.clear();
	}

	public void updateList(List<OnlineMessageReply> list_comment) {
		this.list.addAll(list_comment);
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_comment_reply, null);
			viewHolder = new ViewHolder();
			viewHolder.tv_comment_reply_text = (TextView) convertView
					.findViewById(R.id.tv_comment_reply_text);
			viewHolder.tv_comment_reply_writer = (TextView) convertView
					.findViewById(R.id.tv_comment_reply_writer);
			viewHolder.replyImageView = (ImageView)convertView.findViewById(R.id.replyImageView);
			viewHolder.replyTime = (TextView) convertView.findViewById(R.id.replyTime);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
			
		}
		
		final OnlineMessageReply reply = list.get(position);
		viewHolder.tv_comment_reply_text.setText(reply.getRpmsg());
		viewHolder.tv_comment_reply_writer.setText(reply.getRpuname()+"：");
		viewHolder.replyTime.setText(reply.getRptime());
		if(StringUtils.isNotBlank(reply.getRpimagePath())){
			viewHolder.replyImageView.setVisibility(View.VISIBLE);
			String requestUrl = serverPath+reply.getRpimagePath();
			ImageListener imageListener = imageLoader.getImageListener(viewHolder.replyImageView,R.drawable.chat_bg_default, R.drawable.chat_bg_default);
			imageLoader.get(requestUrl, imageListener,150,150);
		}else{
			viewHolder.replyImageView.setVisibility(View.GONE);
		}
		
		viewHolder.replyImageView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context,ShowImageActivity.class);
				intent.putExtra("netImagePath",serverPath+reply.getRpimagePath());
				intent.putExtra("localImagePath",reply.getRpimagePath());
				context.startActivity(intent);
			}
		});

		return convertView;
	}

	public class ViewHolder {
		private TextView tv_comment_reply_writer; // 评论者昵称
		private TextView tv_comment_reply_text; // 评论 内容
		private ImageView replyImageView;
		private TextView replyTime;
	}
}
