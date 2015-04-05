package com.smartlearning.ui;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

import com.feng.tree.TreeElementBean;
import com.feng.tree.TreeViewAdapter;
import com.feng.view.SlidingMenu;
import com.smartlearning.R;
import com.smartlearning.utils.SpUtil;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class FBookResActivity extends Activity{
	private static final String TAG = FBookResActivity.class.getSimpleName();
	private Context mContext;
	private SharedPreferences sp;
	private String serverIp;
	private long classId;
	private int partId;
	private String partName;
	
	@InjectView(R.id.title_back) LinearLayout title;
	@InjectView(R.id.title_text) TextView titleText;
	@InjectView(R.id.id_menu) SlidingMenu mMenu;
	@InjectView(R.id.title_book_chapter) LinearLayout bookChapter;
	@InjectView(R.id.treeList) ListView listView;
	
	
	//nodelist
	private ArrayList<TreeElementBean> nodeList = new ArrayList<TreeElementBean>();
	//樹形數據適配器
	private TreeViewAdapter treeViewAdapter = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.f_book_res);
		
		mContext = this;
		
		ButterKnife.inject(this);
		
		initSp();
		
		initTitle();
		
		initTree();
	}
	
	

	public void toggleMenu(View view)
	{
		mMenu.toggle();
	}
	
	private void initSp(){
		sp = SpUtil.getSharePerference(mContext);
		serverIp = sp.getString("serverIp","");
		classId = sp.getLong("classId",0);
		
		partId = getIntent().getIntExtra("partId",0);
		partName = getIntent().getStringExtra("partName");
	}
	
	private void initTitle(){
		titleText.setText(partName);
		title.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
	}

	
	private void initTree(){
		
		initTreeData(nodeList);
		
        treeViewAdapter = new TreeViewAdapter(this, R.layout.f_book_res_left_menu_item,nodeList);
        listView.setAdapter(treeViewAdapter);
        listView.setOnItemClickListener(new OnItemClickListener(){

			public void onItemClick(AdapterView<?> adapter, View view, int position,long id) {
				treeViewAdapter.onClick(position, nodeList, treeViewAdapter);
			}
        });
	}
	
	
	 private void initTreeData(ArrayList<TreeElementBean> nodeList){
	    	TreeElementBean pdfOutlineElement1=new TreeElementBean("01", "关键类", false	, false, "00", 0,false);
			TreeElementBean pdfOutlineElement2=new TreeElementBean("02", "应用程序组件", false	, true, "00", 0,false);
			TreeElementBean pdfOutlineElement3=new TreeElementBean("03", "Activity和任务", false	, true, "00", 0,false);
			TreeElementBean pdfOutlineElement4=new TreeElementBean("04", "激活组件：intent", true	, false, "02", 1,false);
			TreeElementBean pdfOutlineElement5=new TreeElementBean("05", "关闭组件", true	, false, "02", 1,false);
			TreeElementBean pdfOutlineElement6=new TreeElementBean("06", "manifest文件", true	, false, "02", 1,false);
			TreeElementBean pdfOutlineElement7=new TreeElementBean("07", "Intent过滤器", true	, false, "02", 1,false);
			TreeElementBean pdfOutlineElement8=new TreeElementBean("08", "Affinity（吸引力）和新任务", true	, false, "03", 1,false);
			TreeElementBean pdfOutlineElement9=new TreeElementBean("09", "加载模式", true	, true, "03", 1,false);
			TreeElementBean pdfOutlineElement10=new TreeElementBean("10", "孩子1", true	, true, "09", 2,false);
			TreeElementBean pdfOutlineElement11=new TreeElementBean("11", "孩子2", true	, true, "09", 2,false);
			TreeElementBean pdfOutlineElement12=new TreeElementBean("12", "孩子2的孩子1", true	, false, "11", 3,false);
			TreeElementBean pdfOutlineElement13=new TreeElementBean("13", "孩子2的孩子2", true	, false, "11", 3,false);
			TreeElementBean pdfOutlineElement14=new TreeElementBean("14", "孩子1的孩子1", true	, false, "10", 3,false);
			TreeElementBean pdfOutlineElement15=new TreeElementBean("15", "孩子1的孩子2", true	, false, "10", 3,false);
			TreeElementBean pdfOutlineElement16=new TreeElementBean("16", "孩子1的孩子3", true	, false, "10", 3,false);
			TreeElementBean pdfOutlineElement17=new TreeElementBean("17", "孩子1的孩子4", true	, false, "10", 3,false);
			TreeElementBean pdfOutlineElement18=new TreeElementBean("18", "孩子1的孩子5", true	, false, "10", 3,false);
			TreeElementBean pdfOutlineElement19=new TreeElementBean("19", "孩子1的孩子6", true	, false, "10", 3,false);
			TreeElementBean pdfOutlineElement20=new TreeElementBean("20", "孩子3", true	, true, "09", 2,false);
			TreeElementBean pdfOutlineElement21=new TreeElementBean("21", "孩子3的孩子1", true	, false, "20", 3,false);
			TreeElementBean pdfOutlineElement22=new TreeElementBean("22", "孩子3的孩子2", true	, true, "20", 3,false);
			//String id, String outlineTitle,boolean mhasParent, boolean mhasChild, String parent, int level,boolean expanded
			TreeElementBean pdfOutlineElement23=new TreeElementBean("23", "孩子3的孩子2的孩子1", true	, false, "22", 4,false);
			TreeElementBean pdfOutlineElement24=new TreeElementBean("24", "孩子3的孩子2的孩子2", true	, false, "22", 4,false);
			TreeElementBean pdfOutlineElement25=new TreeElementBean("25", "孩子3的孩子2的孩子3", true	, false, "22", 4,false);
			TreeElementBean pdfOutlineElement26=new TreeElementBean("26", "孩子3的孩子2的孩子4", true	, false, "22", 4,false);
			
			nodeList.add(pdfOutlineElement1);
			nodeList.add(pdfOutlineElement2);
			nodeList.add(pdfOutlineElement4);
			nodeList.add(pdfOutlineElement5);
			nodeList.add(pdfOutlineElement6);
			nodeList.add(pdfOutlineElement7);
			nodeList.add(pdfOutlineElement3);
			nodeList.add(pdfOutlineElement8);
			nodeList.add(pdfOutlineElement9);
			nodeList.add(pdfOutlineElement10);
			nodeList.add(pdfOutlineElement11);
			nodeList.add(pdfOutlineElement12);
			nodeList.add(pdfOutlineElement13);
			nodeList.add(pdfOutlineElement14);
			nodeList.add(pdfOutlineElement15);
			nodeList.add(pdfOutlineElement16);
			nodeList.add(pdfOutlineElement17);
			nodeList.add(pdfOutlineElement18);
			nodeList.add(pdfOutlineElement19);
			nodeList.add(pdfOutlineElement20);
			nodeList.add(pdfOutlineElement21);
			nodeList.add(pdfOutlineElement22);
			nodeList.add(pdfOutlineElement23);
			nodeList.add(pdfOutlineElement24);
			nodeList.add(pdfOutlineElement25);
			nodeList.add(pdfOutlineElement26);
	    }
}
