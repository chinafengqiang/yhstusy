package com.smartlearning.task;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.smartlearning.R;
import com.smartlearning.biz.AdviodManager;
import com.smartlearning.constant.ServerIP;
import com.smartlearning.model.Video;
import com.smartlearning.utils.NetWorkTool;
import com.smartlearning.utils.Tool;

/**
 * 视频任务
 * @author user
 */
public class VideoTask extends AsyncTask<String, Integer, String> {

	private static final int GET_DATA_COMPLETED_FROM_WEB = 1; // 表明从网络端成功获取数据结束
	private ProgressDialog pdialog = null;
	private Handler handler = null;
	private Context context = null;
	private AdviodManager videoManager = null;
	public  List<Bitmap> bitmaps = new ArrayList<Bitmap>();
	public List<Video> videos = new ArrayList<Video>();
	private String serverIP;
	
	private String pdialogTitle = "课程视频";

	/**
	 * @param context 上下文对象
	 * @param handler 用于更新主界面的handler对象
	 * @param flag 用于标志 视频查询的分类 
	 */
	@SuppressWarnings("deprecation")
	public VideoTask(Context context, Handler handler, String serverIP) {

		this.context = context;
		this.handler = handler;
		this.serverIP = serverIP;
		videoManager = new AdviodManager(context);
		pdialog = new ProgressDialog(context);
		pdialog.setIcon(R.drawable.flag);
		pdialog.setTitle(pdialogTitle);

		pdialog.setButton("cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int i) {
				dialog.cancel();
				VideoTask.this.cancel(true);
			}
		});

		pdialog.setCancelable(true);
		pdialog.show();
	}
	
	public void setPdialogTitle(String title){
	    this.pdialogTitle = title;
	}
	
	@Override
	protected void onPreExecute() {
		boolean isNet = NetWorkTool.IsHttpConnect(context);
		if (!isNet) {
			pdialog.cancel();
			VideoTask.this.cancel(true);
			return;
		}
		pdialog.setTitle(pdialogTitle);
		pdialog.setMessage("Loading....");
	}

	@Override
	protected void onPostExecute(String result) {
		pdialog.dismiss();
		Message msg = new Message();
		msg.what = GET_DATA_COMPLETED_FROM_WEB;
		
		this.handler.sendMessage(msg);
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		// 更新进度
		pdialog.setProgress(values[0]);
	}

	@Override
	protected void onCancelled() {
		Tool.ShowMessage(context, "数据未加载完成，操作取消！");
		videos.clear();
		bitmaps.clear();
		
		super.onCancelled();
	}

	@Override
	protected String doInBackground(String... params) {
		try {
			String webPath = params[0];
			String json = NetWorkTool.getStringResourceFromWeb(webPath);
			this.videos = videoManager.transJsonToLists(json);
			Log.i("videos json=====", json);
			try {
				for (int i = 0; i < videos.size(); i++) {
					Video video = videos.get(i);
					String url = video.getPic();
					
					String image_url = serverIP + ServerIP.VIDEO_INFO_URL + url; // 图片的地址
					Log.i("image_url=====", image_url);
					byte[] result;
					result = NetWorkTool.getImageResourceFromWeb(image_url);
					Bitmap bitmap = BitmapFactory.decodeByteArray(result, 0, result.length);// 生成图片
					bitmaps.add(bitmap);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return "ok";
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
