package com.android.cheyooh.map.adapter;

import java.util.List;

import com.baidu.mapapi.search.route.DrivingRouteLine.DrivingStep;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;


/** 
 * 类说明：   
 * @author  xinhui.cheng
 * @date    2013-4-7
 * @version 1.0
 */
public abstract class SimpleBaseAdapter<T> extends BaseAdapter{

	protected Context mContext;
	protected List<T> mList;
	private LayoutInflater mInflater;
	
	public SimpleBaseAdapter(Context context){
		init(context);
	}
	
	public SimpleBaseAdapter(Context context , List<T> list){
		mList = list;
		init(context);
	}
	
	private void init(Context context){
		mContext = context;
		mInflater = LayoutInflater.from(context);
	}
	
	public abstract View getView(int position, View convertView, ViewGroup parent) ;
	
	
	public List<T> getList() {
		return mList;
	}

	protected LayoutInflater getInflater(){
		return mInflater;
	}
	
	@Override
	public int getCount() {
		return mList == null ? 0 : mList.size();
	}

	@Override
	public Object getItem(int position) {
		return mList == null ? null : mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

}
