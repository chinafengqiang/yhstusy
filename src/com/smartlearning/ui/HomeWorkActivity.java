package com.smartlearning.ui;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.Toast;

import com.smartlearning.R;
import com.smartlearning.biz.BookManager;
import com.smartlearning.constant.Global;
import com.smartlearning.model.Book;
import com.smartlearning.task.DownFileTask;
import com.smartlearning.utils.FileUtil;
import com.smartlearning.utils.ImageService;
import com.smartlearning.utils.Tool;

public class HomeWorkActivity extends Activity {
	
	private List<Book> list = null;
	private Context context = null;
	private Button detail_return_btn = null;
//	private Button detail_home_btn = null;
	private Button btnupload_book = null;
	public static final int NONE = 0;
    public static final int PHOTOHRAPH = 1;// 拍照
    public static final int PHOTOZOOM = 2; // 缩放
    public static final int PHOTORESOULT = 3;// 结果
    public static final String IMAGE_UNSPECIFIED = "image/*";
    private Button main_bottom_download = null;
    private int image = 0;
	private final String pathName = "myHomeWork";
	private String fileName = "";
	SharedPreferences sharedPreferences = null;
	String username = "";
	String ip = "";
	String serverIp = "";
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		try
		{
			// 去掉Activity上面的状态栏
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN);
			setContentView(R.layout.activity_homework);
			GridView gridview = (GridView) findViewById(R.id.gridview3);
			context = this;
			
			sharedPreferences = getSharedPreferences("userInfo", MODE_PRIVATE);
			username = sharedPreferences.getString("user", null);
			ip = sharedPreferences.getString("serverIp", null);
			serverIp = "http://"+ ip +":"+Global.Common_Port;
			
			detail_return_btn = (Button) this.findViewById(R.id.detail_return_btn);
		//	detail_home_btn = (Button) this.findViewById(R.id.detail_home_btn);
			main_bottom_download = (Button) this.findViewById(R.id.main_bottom_download);
			main_bottom_download.setOnClickListener(onClickListenerView);
			
			btnupload_book = (Button) this.findViewById(R.id.btnupload_book);
			
		    //调用拍照
			btnupload_book.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					Intent intent = new Intent(HomeWorkActivity.this,UploadActivity.class);					
					startActivityForResult(intent, 1);
				}
			});
		    
			
		    //title上的返回按钮
		    detail_return_btn.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					finish();
				}
			});
		    //title上的回家按钮
//		    detail_home_btn.setOnClickListener(new View.OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {
//					//调用系统的拍照功能
//	                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//	                image = num();
//	                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Environment.getExternalStorageDirectory(), username+"temp.jpg")));
//	                startActivityForResult(intent, PHOTOHRAPH);
//					
//				}
//			});
			BookManager manager = new BookManager(context);
			list = manager.getHomeworkByall(serverIp, 1, 1200);
			if(list == null){
				return;
			}
			
			ArrayList<HashMap<String, Object>> lstImageItem = new ArrayList<HashMap<String, Object>>();
			for(int i = 0; i < list.size(); i ++){
				Book book = list.get(i);
				HashMap<String, Object> map = new HashMap<String, Object>();
				String url = book.getPic();
				String image_url = serverIp + url; 
				Bitmap bitmap = null;
				byte[] result;	
				try {
					result = ImageService.getImage(image_url);
					bitmap = BitmapFactory.decodeByteArray(result, 0,result.length);// 生成图片
				} catch (Exception e) {
					e.printStackTrace();
				}
				map.put("ItemImage", bitmap);
				map.put("ItemText", book.getName());
				lstImageItem.add(map);
			}
	
		// 生成适配器的ImageItem <====> 动态数组的元素，两者一一对应
			SimpleAdapter saImageItems = new SimpleAdapter(this, // 没什么解释
					lstImageItem,// 数据来源
					R.layout.image_item,// night_item的XML实现
	
					// 动态数组与ImageItem对应的子项
					new String[] { "ItemImage", "ItemText" },
	
					// ImageItem的XML文件里面的一个ImageView,两个TextView ID
					new int[] { R.id.ItemImage, R.id.ItemText });
			
			saImageItems.setViewBinder(new ViewBinder() { 
		            
		            public boolean setViewValue(View view, Object data, String textRepresentation) { 
		                //判断是否为我们要处理的对象  
		                if (view instanceof ImageView)  {
		                	if(data != null){       
                                   if(data instanceof Bitmap )
                                     {
                                             view.setVisibility(View.VISIBLE);
                                             ImageView iv = (ImageView) view;
                                             iv.setImageBitmap((Bitmap) data);
                                     }
                                     else 
                                     {
                                             view.setVisibility(View.VISIBLE);
                                             ((ImageView) view).setImageResource((Integer) data);
                                     }
                                }
                                else
                                {
                                        view.setVisibility(View.GONE);
                                }
                                return true;
                        }
                        else
                        {
                                return false;
                        }
		            }  
		        }); 
			
			// 添加并且显示
			gridview.setAdapter(saImageItems);
			// 添加消息处理
			gridview.setOnItemClickListener(new ItemClickListener());
		}
		catch(Exception ex){
			Toast.makeText(this, "网络连接出现问题，请检查！", Toast.LENGTH_LONG).show();
		}
	}

	
	private View.OnClickListener onClickListenerView = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.bottom_book:
				Intent intent1 = new Intent(context,HomeWorkActivity.class);
				startActivity(intent1);
				break;
			case R.id.bottom_note:
//				Intent intent2 = new Intent(context,MyNoteActivity.class);
//				startActivity(intent2);
				break;
			case R.id.main_bottom_download:
				Intent intent3 = new Intent(context,MyHomeWorkListActivity.class);
				startActivity(intent3);
				break;
			}	
		}
	};
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	// 当AdapterView被单击(触摸屏或者键盘)，则返回的Item单击事件
	class ItemClickListener implements OnItemClickListener {
		public void onItemClick(AdapterView<?> arg0,// The AdapterView where the
													// click happened
				View arg1,// The view within the AdapterView that was clicked
				int arg2,// The position of the view in the adapter
				long arg3// The row id of the item that was clicked
		) {
			
			String fileUrl = "";
			try {
				//String urls = list.get(arg2).getUrl();
				fileName = getDownLoadFileName(list.get(arg2).getUrl());
				fileUrl = URLEncoder.encode(fileName,"UTF-8");
		    //		fileUrl = new String(getDownLoadFileName(urls).getBytes(),"UTF-8");
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}
			String url = serverIp + "/uploadFile/file/" + fileUrl;
			
	//		String url = myApp.getServerIP() + list.get(arg2).getUrl();
			
//			Intent intent = new Intent(context, TextReadActivity.class);
//			intent.putExtra("picUrl", url);
//			startActivity(intent);
			
			downloadVideos(url);
		}
	}
	
	private void intentForward(final String fileName) {
		Intent intent = new Intent();
		intent.setAction(android.content.Intent.ACTION_VIEW);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		File file = new File(fileName);
		Uri path = Uri.fromFile(file);
		intent.setDataAndType(path,"application/pdf");
		startActivity(intent);
	};
	
	private Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			//Tool.ShowMessage(context, msg.obj.toString());
			final String fileName = msg.obj.toString();
			String extName = FileUtil.getExtName(fileName);
			if (extName.equalsIgnoreCase(".pdf") ) {
			//	intentForward(fileName);
			}else{
				Tool.ShowMessage(context, "非pdf格式");
			}
			
//			intentForward(fileName);
		}

	};
	
    private int num(){
    	return (int)(Math.random()*100000000);
    }
	//得到当前下载的文件名
		private String getDownLoadFileName(String urls){
			if (urls != null){
				String url = urls;
				int lastdotIndex = url.lastIndexOf("/");
				String fileName = url.substring(lastdotIndex+1, url.length());
				return fileName;
			}else{
				return "";
			}
		}
		
		private void downloadVideos(final String url){
		//	final String fileName = getDownLoadFileName(url);
			Tool.ShowMessage(context, fileName);
			if(FileUtil.isExists("/sdcard/myHomeWork/"+fileName)){
			     AlertDialog.Builder builder = new AlertDialog.Builder(context);
			     builder.setTitle("提示").setMessage("该视频文件已经存在于SDCard中，确定要覆盖下载吗？")
			     .setPositiveButton("确定", new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog, int which) {
						DownFileTask task = new DownFileTask(context,handler,fileName,pathName);
						task.execute(url);
					}
			     }).setNegativeButton("取消", null).create().show();
//			     }).setNegativeButton("取消", new DialogInterface.OnClickListener(){
//
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						String extName = FileUtil.getExtName(fileName);
//						if (extName.equalsIgnoreCase(".pdf") ) {
//							intentForward("/sdcard/"+fileName);
//						} else {
//							Tool.ShowMessage(context, "非pdf格式");
//						}
////						
////				//		intentForward("/sdcard/"+fileName);
//						
//					}
//			    	 
//			     }).create().show();
			}else{
				
				DownFileTask task = new DownFileTask(context,handler,fileName, pathName);
				task.execute(url);
				
			}
			
		}
		
		 //调用startActivityResult，返回之后的回调函数
	    @Override
	    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	        if (resultCode == NONE)
	            return;
	        // 拍照
	        if (requestCode == PHOTOHRAPH) {
	            //设置文件保存路径这里放在跟目录下
	        	
	            File picture = new File(Environment.getExternalStorageDirectory() + "/" +username+"temp.jpg");
	            startPhotoZoom(Uri.fromFile(picture));
	        }
	         
	        if (data == null)
	            return;
	         
	        // 读取相册缩放图片
	        if (requestCode == PHOTOZOOM) {
	            startPhotoZoom(data.getData());
	        }
	        // 处理结果
	        if (requestCode == PHOTORESOULT) {
	            Bundle extras = data.getExtras();
	            if (extras != null) {
	                Bitmap photo = extras.getParcelable("data");
	                ByteArrayOutputStream stream = new ByteArrayOutputStream();
	                photo.compress(Bitmap.CompressFormat.JPEG, 75, stream);// (0 - 100)压缩文件
	            }
	 
	        }
	 
	        super.onActivityResult(requestCode, resultCode, data);
	    }
	 
	    public void startPhotoZoom(Uri uri) {
	        Intent intent = new Intent("com.android.camera.action.CROP");
	        intent.setDataAndType(uri, IMAGE_UNSPECIFIED);
	        intent.putExtra("crop", "true");
	        // aspectX aspectY 是宽高的比例
	        intent.putExtra("aspectX", 1);
	        intent.putExtra("aspectY", 1);
	        // outputX outputY 是裁剪图片宽高
	        intent.putExtra("outputX", 64);
	        intent.putExtra("outputY", 64);
	        intent.putExtra("return-data", true);
	        startActivityForResult(intent, PHOTORESOULT);
	    }
}
