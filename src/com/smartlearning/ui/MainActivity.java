package com.smartlearning.ui;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.smartlearning.R;
import com.smartlearning.utils.FileUtil;

public class MainActivity extends Activity {

	private Context context = null;
	private LinearLayout ll_denglu;
	private SharedPreferences sharedPreferences;
	private String userId = "";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_main);
		context = this;
		
		sharedPreferences = getSharedPreferences("userInfo", MODE_PRIVATE);
		userId = sharedPreferences.getString("user","0");
		if(userId.equals("0")){
			Intent intent = new Intent(this,LoginActivity.class);
			startActivity(intent);
			finish();
		}
		String rootPath = Environment.getExternalStorageDirectory().getPath()
				+ "/myHomeWork";
		FileUtil.getFileDir(rootPath);

		String rootPath1 = Environment.getExternalStorageDirectory().getPath()
				+ "/myBook";
		FileUtil.getFileDir(rootPath1);

		String rootPath2 = Environment.getExternalStorageDirectory().getPath()
				+ "/myCoursePlan";
		FileUtil.getFileDir(rootPath2);

		String rootPath3 = Environment.getExternalStorageDirectory().getPath()
				+ "/myVideo";
		FileUtil.getFileDir(rootPath3);
		

		Button btnSchedule = (Button) findViewById(R.id.btnSchedule);

		ImageView imageView = (ImageView) findViewById(R.id.imageView2);
		
		ImageView btnImageView = (ImageView) findViewById(R.id.imageView3);

		Button btnStudyRecord = (Button) findViewById(R.id.btnStudyRecord);

		Button btnCommunicate = (Button) findViewById(R.id.btnCommunicate);

		Button btnPersonFile = (Button) findViewById(R.id.btnPersonFile);
		
		ll_denglu = (LinearLayout) findViewById(R.id.ll_denglu);

		btnPersonFile.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				try {
					Intent intent = new Intent(MainActivity.this,
							StudentFileActivity.class);
					startActivityForResult(intent, 1);

				} catch (Exception ex) {
					System.out.println(ex.getMessage());
				}
			}
		});

		ll_denglu.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this,
						LoginActivity.class);
				startActivityForResult(intent, 1);
			}
		});
		
		imageView.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				try {
					Intent intent = new Intent(MainActivity.this,
							LoginActivity.class);
					startActivityForResult(intent, 1);

				} catch (Exception ex) {
					System.out.println(ex.getMessage());
				}
			}
		});
		
		btnImageView.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				try {
					Intent intent = new Intent(MainActivity.this,
							InitilizeActivity.class);
					startActivityForResult(intent, 1);

				} catch (Exception ex) {
					System.out.println(ex.getMessage());
				}
			}
		});

		btnSchedule.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				try {
					Intent intent = new Intent(MainActivity.this,
							ScheduleActivity.class);
					startActivityForResult(intent, 1);

				} catch (Exception ex) {
					System.out.println(ex.getMessage());
				}
			}
		});

		Button btnLearningFile = (Button) findViewById(R.id.btnLearningFile);
		btnLearningFile.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				try {
					/*Intent intent = new Intent(MainActivity.this,
							LearningFileByUseActivity.class);
					startActivityForResult(intent, 1);*/
					Intent intent = new Intent(MainActivity.this,
							BookCategoryListActivity.class);
					startActivity(intent);
				} catch (Exception ex) {
					System.out.println(ex.getMessage());
				}
			}
		});

		Button btnLessonResource = (Button) findViewById(R.id.btnLessonResource);
		btnLessonResource.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				try {
					Intent intent = new Intent(MainActivity.this,
							VideoCenterActivity.class);
					startActivityForResult(intent, 1);

				} catch (Exception ex) {
					System.out.println(ex.getMessage());
				}
			}
		});

		btnStudyRecord.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				try {
					Intent intent = new Intent(MainActivity.this,
							TestPaperActivity.class);
					startActivityForResult(intent, 1);

				} catch (Exception ex) {
					System.out.println(ex.getMessage());
				}
			}
		});

		// 更多应用
		btnCommunicate.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				try {
					Intent intent = new Intent(MainActivity.this,
							OnlineForumActivity.class);
					startActivityForResult(intent, 1);

				} catch (Exception ex) {
					System.out.println(ex.getMessage());
				}
			}
		});

		// Button btnMyCourse = (Button)findViewById(R.id.btnMyCourse);
		// btnMyCourse.setOnClickListener(new Button.OnClickListener() {
		// public void onClick(View v) {
		// try {
		// Intent intent = new Intent(MainActivity.this,
		// PageTurn.class);
		// startActivityForResult(intent, 1);
		//
		// } catch (Exception ex) {
		// System.out.println(ex.getMessage());
		// }
		// }
		// });
	}

	public static void dialog_Exit(Context context) {
		AlertDialog.Builder builder = new Builder(context);
		builder.setMessage("确定要退出吗?");
		builder.setTitle("提示");
		builder.setPositiveButton("确认",
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						android.os.Process.killProcess(android.os.Process
								.myPid());
					}
				});
		builder.setNegativeButton("取消",
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		builder.create().show();
	}


	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 获得mac地址
	 */
	private String getMacAddress() {
		
		WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifi.getConnectionInfo();
		
		return info.getMacAddress();
	}
	
	/**
	 * 获取版本号
	 * @return 当前应用的版本号
	 */
	public String getVersion() {
	    try {
	        PackageManager manager = this.getPackageManager();
	        PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
	        String version = info.versionName;
	        Log.i("version", "version==="+version);
	        return version;
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
		return "";
	}

	
	private void doStartApplicationWithPackageName(String packagename) {  
		  
	    // 通过包名获取此APP详细信息，包括Activities、services、versioncode、name等等  
	    PackageInfo packageinfo = null;  
	    try {  
	        packageinfo = getPackageManager().getPackageInfo(packagename, 0);  
	    } catch (NameNotFoundException e) {  
	        e.printStackTrace();  
	    }  
	    if (packageinfo == null) {  
	        return;  
	    }  
	  
	    // 创建一个类别为CATEGORY_LAUNCHER的该包名的Intent  
	    Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);  
	    resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);  
	    resolveIntent.setPackage(packageinfo.packageName);  
	  
	    // 通过getPackageManager()的queryIntentActivities方法遍历  
	    List<ResolveInfo> resolveinfoList = getPackageManager()  
	            .queryIntentActivities(resolveIntent, 0);  
	  
	    ResolveInfo resolveinfo = resolveinfoList.iterator().next();  
	    if (resolveinfo != null) {  
	        // packagename = 参数packname  
	        String packageName = resolveinfo.activityInfo.packageName;  
	        // 这个就是我们要找的该APP的LAUNCHER的Activity[组织形式：packagename.mainActivityname]  
	        String className = resolveinfo.activityInfo.name;  
	        // LAUNCHER Intent  
	        Intent intent = new Intent(Intent.ACTION_MAIN);  
	        intent.addCategory(Intent.CATEGORY_LAUNCHER);  
	  
	        // 设置ComponentName参数1:packagename参数2:MainActivity路径  
	        ComponentName cn = new ComponentName(packageName, className);  
	  
	        intent.setComponent(cn);  
	        startActivity(intent);  
	    }  
	}  
}
