package com.feng.tree;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.Request.Method;
import com.baidu.cyberplayer.utils.B;
import com.feng.fragment.BookResListFragment;
import com.feng.fragment.VideoResListFragment;
import com.feng.util.BadgeView;
import com.feng.view.SlidingMenu;
import com.feng.vo.BookChapterListVO;
import com.feng.volley.FRestClient;
import com.feng.volley.FastJsonRequest;
import com.smartlearning.R;
import com.smartlearning.biz.BookManager;
import com.smartlearning.constant.Global;
import com.smartlearning.fragment.BookFragment;
import com.smartlearning.utils.CommonUtil;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Picture;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;


@SuppressWarnings("rawtypes")
public class TreeViewAdapter extends ArrayAdapter {
	
	private static final String tag = "ljz";
	//init node id
	private static int nodeId = 1;
	//the root element list
	private ArrayList<TreeElementBean> rootEleList = new ArrayList<TreeElementBean>();
	//layout object
	private LayoutInflater mInflater;
	//Bitmap object(collapse)
	private Bitmap collapse;
	//Bitmap object(expand)
	private Bitmap expand;
	private Context context;
	
	private String reqUrl;
	private String[] alls;
	
	private int partId;
	private boolean isLocal = false;
	private List<TreeElementBean> chapterList = null;
	
	private int moduleId = 0;
	
	@SuppressWarnings("unchecked")
	public TreeViewAdapter(Context context, int textViewResourceId,
			List<TreeElementBean> list,String reqUrl,String[] alls,int partId,int moduleId) {
		super(context, textViewResourceId, list);
		
		this.context = context;
		
		for(TreeElementBean element : list){
			int level = element.getLevel();
			if(level==0){
				element.setAlls(alls);
				rootEleList.add(element);
			}
		}
		mInflater = LayoutInflater.from(context);
		collapse = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.ic_add_circle_outline_white_24dp);
		expand = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.ic_remove_circle_outline_white_24dp);
		
		this.reqUrl = reqUrl;
		this.partId = partId;
		
		if(reqUrl.equals("")){
			isLocal = true;
		}
		
		this.moduleId = moduleId;
	}

	public int getCount() {
		return rootEleList.size();
	}

	public Object getItem(int position) {
		return rootEleList.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;	
			convertView = mInflater.inflate(R.layout.f_book_res_left_menu_item, null);
			holder = new ViewHolder();
			holder.text = (TextView) convertView.findViewById(R.id.text);
			holder.icon = (ImageView) convertView.findViewById(R.id.icon);
			convertView.setTag(holder);
			if(isLocal){
				int isAddRes = rootEleList.get(position).getIsAddRes();
				int count = rootEleList.get(position).getResCount();
				if(isAddRes == 2 && count > 0){
					BadgeView badge = new BadgeView(context, holder.text);  
			        badge.setText(count+"");  
			        badge.setBadgePosition(BadgeView.POSITION_CENTER);  
			        badge.show();
				}
			}

	  
			
		int level = rootEleList.get(position).getLevel();
			holder.icon.setPadding(25 * (level + 1), holder.icon
				.getPaddingTop(), 0, holder.icon.getPaddingBottom());
		
		holder.text.setText(rootEleList.get(position).getNodeName());
		if (rootEleList.get(position).isHasChild()
				&& (rootEleList.get(position).isExpanded() == false)) {
			holder.icon.setImageBitmap(collapse);
		} else if (rootEleList.get(position).isHasChild()
				&& (rootEleList.get(position).isExpanded() == true)) {
			holder.icon.setImageBitmap(expand);
		} else if (!rootEleList.get(position).isHasChild()){
			holder.icon.setImageBitmap(collapse);
			holder.icon.setVisibility(View.INVISIBLE);
		}
		return convertView;
	}

	class ViewHolder {
		TextView text;
		ImageView icon;

	}
	
	public void onClick(int position,ArrayList<TreeElementBean> subNodeList,TreeViewAdapter treeViewAdapter
			,FragmentTransaction ft,int bookResContent,SlidingMenu mMenu,TextView titleText){
		TreeElementBean element=rootEleList.get(position);
		if (!element.isHasChild()) {
			int partId = Integer.parseInt(element.getUpNodeId());
			int categoryId = Integer.parseInt(element.getId());
			String pidName = element.getUpNodeName();
			String name = element.getNodeName();
			String allIds = element.getAllIds();
			String allNames = element.getAllNames();
			titleText.setText(pidName);
			
			if(moduleId == 1){
				Fragment fg = VideoResListFragment.newInstance(partId,categoryId,name,allIds,allNames);
				ft.replace(bookResContent,fg,"VideoResListFragment");
				ft.commit();
			}else{
				Fragment fg = BookResListFragment.newInstance(partId,categoryId,name,allIds,allNames);
				ft.replace(bookResContent,fg,"BookResListFragment");
				ft.commit();
			}

			
			//mMenu.closeMenu();
			//mMenu.toggle();
			mMenu.hide();
			
			//return;
		}
		
		if (rootEleList.get(position).isExpanded()) {
			rootEleList.get(position).setExpanded(false);
			
			ArrayList<TreeElementBean> temp=new ArrayList<TreeElementBean>();
			
			for (int i = position+1; i < rootEleList.size(); i++) {
				if (element.getLevel()>=rootEleList.get(i).getLevel()) {
					break;
				}
				temp.add(rootEleList.get(i));
			}
			
			rootEleList.removeAll(temp);
			
			treeViewAdapter.notifyDataSetChanged();
			
		} else {
			/*rootEleList.get(position).setExpanded(true);
			for (TreeElementBean elet : subNodeList) {
				int j=1;
				if (elet.getUpNodeId()==rootEleList.get(position).getId()) {
					elet.setExpanded(false);
					rootEleList.add(position+j, elet);
					j++;
				}			
			}*/
			
			loadTree(element,treeViewAdapter,position);
		}
	}
	
	private static void createTree(TreeElementBean parNode, File parentFile,ArrayList<TreeElementBean> nodeList) {
		if (parentFile.isDirectory()) {
			parNode.setHasChild(false);
			if (parentFile.list().length > 0) {
				parNode.setHasChild(true);
			}
			for (File subFile : parentFile.listFiles()) {
				nodeId++;
				//id,nodeName,hasParent,hasChild,upNodeId,level,expanded
				TreeElementBean subNode = new TreeElementBean(format(nodeId),
						subFile.getName(), true, subFile.isDirectory(), parNode.getId(),
						parNode.getLevel() + 1, false);
				nodeList.add(subNode);
				createTree(subNode, subFile,nodeList);
			}
		}
	}
	
    public static void getTreeNodes(ArrayList<TreeElementBean> nodeList,String rootPath) {
    	nodeId = 1;
		if (nodeList.size() == 0) {
			File rootDir = new File(rootPath);
			if (rootDir.isDirectory()) {
				//id,nodeName,hasParent,hasChild,upNodeId,level,expanded
				TreeElementBean rootNode = new TreeElementBean(format(1),rootDir.getName(),false,true,format(0),0,false);
				nodeList.add(rootNode);
				createTree(rootNode, rootDir,nodeList);
			}
		}
		
	}
    
    protected static String format (int str){
    	return String.format("%1$,02d", str);
    }
    
    private void loadTree(final TreeElementBean element,final TreeViewAdapter treeViewAdapter,final int position){
 
		final int plevel = element.getLevel();
		final int pid = Integer.parseInt(element.getId());
		final String name = element.getNodeName();
		final String allIds = element.getAllIds();
		final String allNames = element.getAllNames();
		final int isAddRes = element.getIsAddRes();
		final BookManager bookManager = new BookManager(context);
		if(isLocal){
			  class GetLocalBookChapter extends AsyncTask<Boolean,Integer,Boolean>{
			    	@Override
					protected Boolean doInBackground(Boolean... params) {
			    		if(isAddRes == 1){
			    			if(moduleId == 1){
			    				chapterList = InitTreeCategory.getVideoCategoryTree(pid, plevel,bookManager);
			    			}else{
			    				//chapterList = InitTreeCategory.getCategoryTree(pid, plevel);
			    				chapterList = InitTreeCategory.getCategoryTree(pid, plevel,bookManager);
			    			}
			    		}else{
							chapterList = bookManager.getBookChapterTree(partId, pid);
			    		}

						return true;
					}

					@Override
					protected void onPreExecute() {
						super.onPreExecute();
					}

					@Override
					protected void onPostExecute(Boolean result) {
						super.onPostExecute(result);
						if(chapterList != null && chapterList.size() > 0){
							int n = 1;
							for (TreeElementBean elet : chapterList) {
								elet.setUpNodeName(name);
								elet.setLevel(plevel+1);
								elet.setAllIds(allIds+","+elet.getId());
								elet.setAllNames(allNames+","+elet.getNodeName());
								rootEleList.add(position+n, elet);
								n++;
							}
						}
						element.setExpanded(true);
						treeViewAdapter.notifyDataSetChanged();
					}
					
				}
			  
			  new GetLocalBookChapter().execute(true);
		}else{
			
		   	final ProgressDialog pDialog = new ProgressDialog(context);
			pDialog.setMessage("Loading...");
			pDialog.show(); 
			
			String tag_json_obj = "json_obj_req";
			String url = reqUrl+"&pid="+pid+"&plevel="+plevel;
			
			if(isAddRes == 1){
				String[] urlArr = url.split("getBookChapter.html");
				url = urlArr[0]+"getBookResCategory.html?partId="+pid+"&plevel="+plevel+"&type="+moduleId;
			}

			FastJsonRequest<BookChapterListVO>   fastRequest = new FastJsonRequest<BookChapterListVO>(Method.GET,url, BookChapterListVO.class,null, new Response.Listener<BookChapterListVO>() {

				@Override
				public void onResponse(BookChapterListVO chapterVO) {
					pDialog.dismiss();
					if(chapterVO != null){
						List<TreeElementBean> nodeList = chapterVO.getBookChapterList();
						int n = 1;
						for (TreeElementBean elet : nodeList) {
							//elet.setExpanded(elet.isExpanded());
							elet.setUpNodeName(name);
							elet.setAllIds(allIds+","+elet.getId());
							elet.setAllNames(allNames+","+elet.getNodeName());
							rootEleList.add(position+n, elet);
							n++;
						}
					}
					element.setExpanded(true);
					treeViewAdapter.notifyDataSetChanged();
				}
			},
			new Response.ErrorListener() {

				@Override
				public void onErrorResponse(VolleyError error) {
					// TODO Auto-generated method stub
					 VolleyLog.d("TreeViewAdapter", "Error: " + error.getMessage());
					 //获取本地分类
					 CommonUtil.showToast(context,context.getString(R.string.get_online_category_fail),Toast.LENGTH_SHORT);
					 pDialog.dismiss();
				}
			}
		    );
			
			FRestClient.getInstance(context).addToRequestQueue(fastRequest,tag_json_obj);
	    }
		}
		
   
}


