package com.feng.tree;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.smartlearning.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Picture;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


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
	
	@SuppressWarnings("unchecked")
	public TreeViewAdapter(Context context, int textViewResourceId,
			List<TreeElementBean> list) {
		super(context, textViewResourceId, list);
		
		this.context = context;
		
		for(TreeElementBean element : list){
			int level = element.getLevel();
			if(level==0){
				rootEleList.add(element);
			}
		}
		mInflater = LayoutInflater.from(context);
		collapse = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.ic_add_circle_outline_white_24dp);
		expand = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.ic_remove_circle_outline_white_24dp);
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
	
	public void onClick(int position,ArrayList<TreeElementBean> subNodeList,TreeViewAdapter treeViewAdapter){
		TreeElementBean element=rootEleList.get(position);
		if (!element.isHasChild()) {
			Toast.makeText(context, element.getId(),Toast.LENGTH_LONG).show();
			return;
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
			rootEleList.get(position).setExpanded(true);
			
			for (TreeElementBean elet : subNodeList) {
				int j=1;
				if (elet.getUpNodeId()==rootEleList.get(position).getId()) {
					elet.setExpanded(false);
					rootEleList.add(position+j, elet);
					j++;
				}			
			}
			treeViewAdapter.notifyDataSetChanged();
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
}
