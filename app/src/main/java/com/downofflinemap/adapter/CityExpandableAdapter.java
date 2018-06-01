package com.downofflinemap.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.mapapi.map.offline.MKOLSearchRecord;
import com.baidu.mapapi.map.offline.MKOLUpdateElement;
import com.baidu.mapapi.map.offline.MKOfflineMap;
import com.downofflinemap.R;

import java.util.ArrayList;
import java.util.List;


public class CityExpandableAdapter extends BaseExpandableListAdapter {

	private Context context;
	private ArrayList<MKOLSearchRecord> group;
	private ArrayList<List<MKOLSearchRecord>> child;
	private ViewHolder viewHolder;
	private MKOfflineMap mOffline;

	public CityExpandableAdapter(Context context,
                                 ArrayList<MKOLSearchRecord> group,
                                 ArrayList<List<MKOLSearchRecord>> child, MKOfflineMap mOffline) {

		this.context = context;
		this.group = group;
		this.child = child;
		this.mOffline =mOffline;

	}
	@Override
	public int getChildrenCount(int arg0) {
		return child.get(arg0).size();
	}

	@Override
	public Object getChild(int groupPosition, int childId) {
		return child.get(groupPosition).get(childId);
	}

	@Override
	public long getChildId(int groupId, int childId) {
		return childId;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
                             boolean arg2, View view, ViewGroup arg4) {
		if (view == null) {
			viewHolder = new ViewHolder();
			view = LayoutInflater.from(context).inflate(R.layout.citys_item,
					null);
			viewHolder.city_name_tv = (TextView) view
					.findViewById(R.id.city_name_tv);
			viewHolder.isdownload_tv = (TextView) view
					.findViewById(R.id.isdownload_tv);
			viewHolder.city_size_tv = (TextView) view
					.findViewById(R.id.city_size_tv);
			viewHolder.isdownload_iv = (ImageView) view
					.findViewById(R.id.isdownload_iv);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}
		MKOLSearchRecord bean_group = group.get(groupPosition);
		List<MKOLSearchRecord> arrays_child =	child.get(groupPosition);
		MKOLSearchRecord bean = arrays_child.get(childPosition);
		
		int cityId = bean.cityID;
		MKOLUpdateElement update = mOffline.getUpdateInfo(cityId);
		if(update!=null){
			if(update.status==1){
				viewHolder.isdownload_tv.setText("(正在下载)");
				viewHolder.isdownload_tv.setTextColor(context.getResources().getColor(R.color.red));
			}else if(update.status==2){
				viewHolder.isdownload_tv.setText("(等待下载)");
				viewHolder.isdownload_tv.setTextColor(context.getResources().getColor(R.color.red));
			}else if(update.status==3){
				viewHolder.isdownload_tv.setText("(已暂停)");
				viewHolder.isdownload_tv.setTextColor(context.getResources().getColor(R.color.red));
			}else if(update.status==4 ||update.status==10 ){
				viewHolder.isdownload_tv.setText("(已下载)");
				viewHolder.isdownload_tv.setTextColor(context.getResources().getColor(R.color.black));
			}
			viewHolder.isdownload_iv.setBackgroundResource(R.drawable.download_disable_grey);
			
		}else{
			viewHolder.isdownload_tv.setText("");
			viewHolder.isdownload_iv.setBackgroundResource(R.drawable.download_able_hight);
		}
		
		
		
		if (childPosition == 0) {
			viewHolder.city_name_tv.setText("所有城市");
			if(getisDownload(bean_group).size()==arrays_child.size()-1){
				viewHolder.isdownload_tv.setText("(已下载)");
				viewHolder.isdownload_iv.setBackgroundResource(R.drawable.download_disable_grey);
			}
		} else {
			viewHolder.city_name_tv.setText(bean.cityName);
			
		}
		
		
		viewHolder.city_size_tv.setText("");
		viewHolder.city_name_tv.setPadding(40, 0, 0, 0);
		viewHolder.city_name_tv.setTextSize(15);
		viewHolder.city_size_tv.setTextSize(12);
		viewHolder.isdownload_tv.setTextSize(12);
		return view;
	}


	@Override
	public Object getGroup(int groupId) {
		return group.get(groupId);
	}

	@Override
	public int getGroupCount() {
		return group.size();
	}

	@Override
	public long getGroupId(int groupId) {
		return groupId;
	}

	@Override
	public View getGroupView(int arg0, boolean arg1, View view, ViewGroup arg3) {
		if (view == null) {
			viewHolder = new ViewHolder();
			view = LayoutInflater.from(context).inflate(R.layout.citys_item,
					null);
			viewHolder.city_name_tv = (TextView) view
					.findViewById(R.id.city_name_tv);
			viewHolder.isdownload_tv = (TextView) view
					.findViewById(R.id.isdownload_tv);
			viewHolder.city_size_tv = (TextView) view
					.findViewById(R.id.city_size_tv);
			viewHolder.isdownload_iv = (ImageView) view
					.findViewById(R.id.isdownload_iv);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
			
		}
		MKOLSearchRecord bean = group.get(arg0);
		
		int cityId = bean.cityID;
		MKOLUpdateElement update = mOffline.getUpdateInfo(cityId);
		if(update!=null){
			if(update.status==1){
				viewHolder.isdownload_tv.setText("(正在下载)");
				viewHolder.isdownload_tv.setTextColor(context.getResources().getColor(R.color.red));
			}else if(update.status==2){
				viewHolder.isdownload_tv.setText("(等待下载)");
				viewHolder.isdownload_tv.setTextColor(context.getResources().getColor(R.color.red));
			}else if(update.status==3){
				viewHolder.isdownload_tv.setText("(已暂停)");
				viewHolder.isdownload_tv.setTextColor(context.getResources().getColor(R.color.red));
			}else if(update.status==4 ||update.status==10 ){
				viewHolder.isdownload_tv.setText("(已下载)");
				viewHolder.isdownload_tv.setTextColor(context.getResources().getColor(R.color.black));
			}
			viewHolder.isdownload_iv.setBackgroundResource(R.drawable.download_disable_grey);
		}else{
			viewHolder.isdownload_tv.setText("");
			viewHolder.isdownload_iv.setBackgroundResource(R.drawable.download_able_hight);
		}
		
		viewHolder.city_name_tv.setText(bean.cityName);
		if (bean.childCities == null) {
			viewHolder.city_size_tv.setText("");
			viewHolder.city_size_tv.setTextSize(13);
			
		} else {
			
			if(getisDownload(bean).size()==bean.childCities.size()){
				viewHolder.isdownload_tv.setText("(已下载)");
			}
			
			
			viewHolder.city_size_tv.setText("");
			viewHolder.isdownload_iv.setBackgroundResource(R.drawable.icon_down);
		}
		return view;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public boolean isChildSelectable(int arg0, int arg1) {
		return true;
	}

	class ViewHolder {
		private TextView city_name_tv;
		private TextView isdownload_tv;
		private TextView city_size_tv;
		private ImageView isdownload_iv;

	}
public ArrayList<Integer> getisDownload(MKOLSearchRecord bean){
	ArrayList<Integer> arrays = new ArrayList<Integer>();
	for(MKOLSearchRecord record:bean.childCities){
		
		int cityid = record.cityID;
		MKOLUpdateElement element = mOffline.getUpdateInfo(cityid);
		
		if(element!=null){
			if(element.status==4||element.status==10){
				arrays.add(cityid);
			}	
		}
		
	}
	return arrays;
}

	

}
