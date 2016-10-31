package com.android.cheyooh.map.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.cheyooh.map.R;
import com.baidu.mapapi.search.core.PoiInfo;

/**
 * 类说明：
 * 
 * @author Terry Lu
 * @date 2012-6-8
 * @version 1.0
 */
public class PoiListAdapter extends BaseAdapter {
	private List<PoiInfo> mPoiInfoList;
	private Context mContext;
	private LayoutInflater mInflater;
	
	public PoiListAdapter(Context context, List<PoiInfo> poiInfoList) {
		mContext = context;
		mPoiInfoList = poiInfoList;
		mInflater = LayoutInflater.from(mContext);
	}

	@Override
	public int getCount() {
		if (mPoiInfoList != null) {
			return mPoiInfoList.size();
		} else {
			return 0;
		}
	}

	@Override
	public Object getItem(int position) {
		if (mPoiInfoList != null) {
			return mPoiInfoList.get(position);
		} else {
			return null;
		}
	}

	@Override
	public long getItemId(int position) {
		if (mPoiInfoList != null) {
			return position;
		} else {
			return 0;
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.search_route_pan_item_layout, null);
			holder.name = (TextView) convertView.findViewById(R.id.search_route_plan_layout_item_name);
			holder.address = (TextView)convertView.findViewById(R.id.search_route_plan_layout_item_addr);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder)convertView.getTag();
		}
		
		PoiInfo poiInfo = mPoiInfoList.get(position);
		holder.name.setText(poiInfo.name);
		holder.address.setText(poiInfo.address);
		return convertView;
	}
	
	private static class ViewHolder {
		TextView name;
		TextView address;
	}
	
	public void setData(List<PoiInfo> list) {
		mPoiInfoList = list;
	}

}
