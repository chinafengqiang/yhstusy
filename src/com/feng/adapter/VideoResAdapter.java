package com.feng.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.feng.vo.VideoRes;
import com.smartlearning.R;
import com.smartlearning.utils.FileUtil;

public class VideoResAdapter extends BaseAdapter{
	private Context context = null;
	private List<VideoRes> list = null;
	private String categoryName;
	
	public VideoResAdapter(Context context) {
		this.context = context;
	}
	
	public VideoResAdapter(List<VideoRes> list, Context context,String categoryName){
		this.list = list;
		this.context = context;
		this.categoryName = categoryName;
	}
	
	public VideoResAdapter(List<VideoRes> list, Context context){
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
		final VideoRes video = list.get(position);
		String fileName = getDownLoadFileName(video.getResUrl());
		if (FileUtil.isExists("/sdcard/myVideo/"+ fileName)) {
			video.setLocalFile(true);
		}
		if(convertView == null){
			convertView = View.inflate(context, R.layout.f_video_res_center_item,null);
			holder = new ViewHolder();
			holder.videoName = (TextView) convertView.findViewById(R.id.videoName);
			holder.videoCrateTime = (TextView) convertView.findViewById(R.id.videoCrateTime);
			holder.videoStatus = (TextView) convertView.findViewById(R.id.videoStatus);
			holder.videoCategory = (TextView)convertView.findViewById(R.id.videoCategory);
			holder.videoLect = (TextView)convertView.findViewById(R.id.videoLect);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		holder.videoName.setText(video.getResName());
		holder.videoCrateTime.setText(context.getString(R.string.book_crete_time)+video.getResCreateTime());
		String status = context.getString(R.string.book_not_download);
		if(video.isLocalFile()){ 
			status = context.getString(R.string.book_download);
		}
		holder.videoStatus.setText(status);
		holder.videoCategory.setText(context.getString(R.string.book_category)+this.categoryName);
		holder.videoLect.setText(context.getText(R.string.video_lect)+video.getResLectuer());
		return convertView;
	}
	
	public final class ViewHolder {
		public TextView videoName;
		public TextView videoCrateTime;
		public TextView videoStatus;
		public TextView videoCategory;
		public TextView videoLect;
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
