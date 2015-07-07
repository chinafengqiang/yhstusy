package com.feng.adapter;

import java.util.List;

import com.feng.vo.LessonMessageVO;
import com.smartlearning.R;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class LessonMessageAdapter extends BaseAdapter{
	private Context mContext;
	private List<LessonMessageVO> msgList;
	
	
	
	public LessonMessageAdapter(Context mContext, List<LessonMessageVO> msgList) {
		super();
		this.mContext = mContext;
		this.msgList = msgList;
	}

	@Override
	public int getCount() {
		return msgList.size();
	}

	@Override
	public Object getItem(int position) {
		return msgList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		LessonMessageVO msg = msgList.get(position);
		if(convertView == null){
			convertView = View.inflate(mContext, R.layout.f_lesson_message_item,null);
			viewHolder = new ViewHolder();
			viewHolder.title = (TextView)convertView.findViewById(R.id.title);
			viewHolder.content = (TextView)convertView.findViewById(R.id.content);
			viewHolder.time = (TextView)convertView.findViewById(R.id.time);
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder)convertView.getTag();
		}
		viewHolder.title.setText(msg.getTitle());
		viewHolder.content.setText(msg.getContent());
		viewHolder.time.setText(msg.getStartTime()+"--"+msg.getEndTime());
		return convertView;
	}
	
	class ViewHolder {
		public TextView title;
		public TextView content;
		public TextView time;
		
	}
	
}
