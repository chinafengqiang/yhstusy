package com.feng.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.feng.util.StringUtils;
import com.feng.view.MyListView;
import com.feng.vo.OnlineMessage;
import com.feng.vo.OnlineMessageReply;
import com.feng.volley.FRestClient;
import com.smartlearning.R;
import com.smartlearning.ui.ShowImageActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CommentAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private ViewHolder viewHolder;
	private List<OnlineMessage> list_comment; // 一级评论数据
	private List<List<HashMap<String, Object>>> list_comment_child; // 二级评论数据
	private Context context;
	private OnClickListener myOnitemcListener;
	private CommentReplyAdapter myAdapter;
	private OnClickListener mShowClickListener;
	private String serverPath = "";
	ImageLoader imageLoader;

	public CommentAdapter(Context context,
			List<OnlineMessage> list_comment,
			List<List<HashMap<String, Object>>> list_comment_child,
			OnClickListener myOnitemcListener, CommentReplyAdapter myAdapter) {
		this.context = context;
		this.inflater = LayoutInflater.from(context);
		this.myAdapter = myAdapter;
		this.list_comment = new ArrayList<OnlineMessage>();
		this.list_comment_child = new ArrayList<List<HashMap<String, Object>>>();
		this.myOnitemcListener = myOnitemcListener;
		this.list_comment.addAll(list_comment);
		this.list_comment_child.addAll(list_comment_child);

	}
	
	public CommentAdapter(Context context,
			List<OnlineMessage> list_comment,
			List<List<HashMap<String, Object>>> list_comment_child,
			OnClickListener myOnitemcListener, CommentReplyAdapter myAdapter,OnClickListener mShowClickListener,
			String serverPath) {
		this.context = context;
		this.inflater = LayoutInflater.from(context);
		this.myAdapter = myAdapter;
		this.list_comment = new ArrayList<OnlineMessage>();
		this.list_comment_child = new ArrayList<List<HashMap<String, Object>>>();
		this.myOnitemcListener = myOnitemcListener;
		this.list_comment.addAll(list_comment);
		this.list_comment_child.addAll(list_comment_child);
		this.mShowClickListener = mShowClickListener;
		this.serverPath = serverPath;
		imageLoader =  FRestClient.getInstance(context).getImageLoader();
	}

	public void clearList() {
		this.list_comment.clear();
		this.list_comment_child.clear();
	}

	public void updateList(List<OnlineMessage> list_comment,
			List<List<HashMap<String, Object>>> list_comment_child) {
		this.list_comment.addAll(list_comment);
		this.list_comment_child.addAll(list_comment_child);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list_comment.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list_comment.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_comment, null);
			viewHolder = new ViewHolder();
			viewHolder.iv_user_photo = (ImageView) convertView
					.findViewById(R.id.iv_user_photo);
			viewHolder.tv_user_name = (TextView) convertView
					.findViewById(R.id.tv_user_name);
			viewHolder.tv_user_comment = (TextView) convertView
					.findViewById(R.id.tv_user_comment);

			viewHolder.tv_user_reply = (TextView) convertView
					.findViewById(R.id.tv_user_reply);
			viewHolder.lv_user_comment_replys = (MyListView) convertView
					.findViewById(R.id.lv_user_comment_replys);
			viewHolder.tv_show_reply = (TextView)convertView.findViewById(R.id.tv_show_replys);
			viewHolder.tv_user_comment_date = (TextView)convertView.findViewById(R.id.tv_user_comment_date);
			viewHolder.imageView = (ImageView)convertView.findViewById(R.id.imageView);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		final OnlineMessage msg = list_comment.get(position);
		viewHolder.tv_user_name.setText(msg.getSender());
		viewHolder.tv_user_comment.setText(msg.getMsg());
		viewHolder.tv_user_comment_date.setText(msg.getMsgtime());
		viewHolder.lv_user_comment_replys.setSelector(new ColorDrawable(
				Color.TRANSPARENT));
		List<OnlineMessageReply> replyList = msg.getReplyList();
		if(replyList != null){
				CommentReplyAdapter replyAdapter = new CommentReplyAdapter(context,replyList);
				replyAdapter.setServerPath(serverPath);
				viewHolder.lv_user_comment_replys.setAdapter(replyAdapter);
		}
		
		if(StringUtils.isNotBlank(msg.getImagePath())){
			viewHolder.imageView.setVisibility(View.VISIBLE);
			String requestUrl = serverPath+msg.getImagePath();
			ImageListener imageListener = imageLoader.getImageListener(viewHolder.imageView,R.drawable.chat_bg_default, R.drawable.chat_bg_default);
			imageLoader.get(requestUrl, imageListener,150,150);
		}else{
			viewHolder.imageView.setVisibility(View.GONE);
		}
		
		viewHolder.tv_user_reply.setTag(position);
		viewHolder.tv_show_reply.setTag(position);
		viewHolder.tv_user_reply.setOnClickListener(myOnitemcListener);
		if(msg.isClickReply()){
			viewHolder.tv_show_reply.setText(R.string.close_show_reply);
		}else{
			viewHolder.tv_show_reply.setText(R.string.show_reply);
		}
		viewHolder.tv_show_reply.setOnClickListener(mShowClickListener);
		
		viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context,ShowImageActivity.class);
				intent.putExtra("netImagePath",serverPath+msg.getImagePath());
				intent.putExtra("localImagePath",msg.getLocalImagePath());
				context.startActivity(intent);
			}
		});
		
		return convertView;
	}

	public class ViewHolder {
		private ImageView iv_user_photo; // 品论者 头像
		private TextView tv_user_name; // 品论者 昵称
		private TextView tv_user_comment; // 品论者 一级品论内容
		private TextView tv_user_comment_date; //
		private TextView tv_user_reply; // 品论者 二级品论内容
		private MyListView lv_user_comment_replys; // 品论者 二级品论内容列表
		private TextView tv_show_reply;
		private ImageView imageView;

	}
	
	

}
