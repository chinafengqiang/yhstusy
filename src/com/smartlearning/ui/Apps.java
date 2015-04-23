package com.smartlearning.ui;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;

import com.smartlearning.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Apps extends Activity {

	// 用来记录应用程序的信息
	List<AppsItemInfo> list;

	private GridView gridview;
	private LinearLayout title;
	private TextView titleText;
	
	private PackageManager pManager;
	
	final static String[] APPS = {"com.rain.android.wuli",
		"com.rainsponsor.android.huaxue",
		"com.mduckstudio.dailywords",
		"org.breezesoft.techolite",
		"ning.tableofelements",
		"com.ibox.calculators",
		"cn.dict.android.pro",
		"com.sun.cidian.yinghandacidian"
		};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.f_local_app);

		// 取得gridview
		gridview = (GridView) findViewById(R.id.gridview);
		title = (LinearLayout)findViewById(R.id.title_back);
		titleText = (TextView)findViewById(R.id.title_text);
		
		// 获取图片、应用名、包名
		pManager = Apps.this.getPackageManager();
		List<PackageInfo> appList = getAllApps(Apps.this);

		list = new ArrayList<AppsItemInfo>();

		for (int i = 0; i < appList.size(); i++) {
			PackageInfo pinfo = appList.get(i);
			AppsItemInfo shareItem = new AppsItemInfo();
			// 设置图片
			shareItem.setIcon(pManager
					.getApplicationIcon(pinfo.applicationInfo));
			// 设置应用程序名字
			shareItem.setLabel(pManager.getApplicationLabel(
					pinfo.applicationInfo).toString());
			// 设置应用程序的包名
			shareItem.setPackageName(pinfo.applicationInfo.packageName);

			list.add(shareItem);

		}

		// 设置gridview的Adapter
		gridview.setAdapter(new baseAdapter());

		// 点击应用图标时，做出响应
		gridview.setOnItemClickListener(new ClickListener());
		
		initTitle();

	}

	
	
	private void initTitle(){
		
		titleText.setText(R.string.more_apps_title);
	
		title.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
	}
	
	public static boolean isPerm(String pkName){
		for(String pk : APPS){
			if(pk.equals(pkName))
				return true;
		}
		return false;
	}
	
	public static List<PackageInfo> getAllApps(Context context) {

		List<PackageInfo> apps = new ArrayList<PackageInfo>();
		PackageManager pManager = context.getPackageManager();
		// 获取手机内所有应用
		List<PackageInfo> packlist = pManager.getInstalledPackages(0);
		for (int i = 0; i < packlist.size(); i++) {
			PackageInfo pak = (PackageInfo) packlist.get(i);

			String pkName = pak.packageName;
			if(!isPerm(pkName))
				continue;
			
			// 判断是否为非系统预装的应用程序
			// 这里还可以添加系统自带的，这里就先不添加了，如果有需要可以自己添加
			// if()里的值如果<=0则为自己装的程序，否则为系统工程自带
			if ((pak.applicationInfo.flags & pak.applicationInfo.FLAG_SYSTEM) <= 0) {
				// 添加自己已经安装的应用程序
				apps.add(pak);
			}

		}
		return apps;
	}

	private class baseAdapter extends BaseAdapter {
		LayoutInflater inflater = LayoutInflater.from(Apps.this);

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder;
			if (convertView == null) {
				// 使用View的对象itemView与R.layout.item关联
				convertView = inflater.inflate(R.layout.f_local_app_item, null);
				holder = new ViewHolder();
				holder.icon = (ImageView) convertView
						.findViewById(R.id.apps_image);
				holder.label = (TextView) convertView
						.findViewById(R.id.apps_textview);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.icon.setImageDrawable(list.get(position).getIcon());
			holder.label.setText(list.get(position).getLabel().toString());

			return convertView;

		}

	}
	
	private class ViewHolder{
		private ImageView icon;
		private TextView label;
	}

	// 当用户点击应用程序图标时，将对这个类做出响应
	private class ClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {

//			// 将应用所选的应用程序信息共享到Application中
//			MyApp appState = ((MyApp) getApplicationContext());
//			// 获取当前所在选项卡
//			String tab_id = appState.getTab_id();
//			// 设置所选应用程序信息
//			appState.set_AppInfo(tab_id, list.get(arg2).getLabel(), list.get(
//					arg2).getIcon(), list.get(arg2).getPackageName());
			Intent intent = new Intent();
			intent = Apps.this.getPackageManager().getLaunchIntentForPackage(list.get(arg2).getPackageName());
			startActivity(intent);
			// 销毁当前Activity
//			finish();
		}

	}

	// 自定义一个 AppsItemInfo 类，用来存储应用程序的相关信息
	private class AppsItemInfo {

		private Drawable icon; // 存放图片
		private String label; // 存放应用程序名
		private String packageName; // 存放应用程序包名

		public Drawable getIcon() {
			return icon;
		}

		public void setIcon(Drawable icon) {
			this.icon = icon;
		}

		public String getLabel() {
			return label;
		}

		public void setLabel(String label) {
			this.label = label;
		}

		public String getPackageName() {
			return packageName;
		}

		public void setPackageName(String packageName) {
			this.packageName = packageName;
		}

	}

}
