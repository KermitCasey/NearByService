package com.android.cheyooh.map.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.cheyooh.map.R;
import com.android.cheyooh.map.adapter.RoutePlanAdapter.TYPE;
import com.android.cheyooh.map.utils.MapUtil;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.core.RouteNode;
import com.baidu.mapapi.search.core.RouteStep;
import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.mapapi.search.route.DrivingRouteLine.DrivingStep;
import com.baidu.mapapi.search.route.TransitRouteLine;
import com.baidu.mapapi.search.route.TransitRouteLine.TransitStep;
import com.baidu.mapapi.search.route.WalkingRouteLine;
import com.baidu.mapapi.search.route.WalkingRouteLine.WalkingStep;

/**
 * 类说明：
 * 
 * @author Terry.Lu
 * @date 2013-5-9
 * @version 1.0
 */
public class RoutePlanAdapter extends SimpleBaseAdapter<RouteStep> {

	public enum TYPE {
		WALK, DRIVING, TRANSIT
	}

	private RouteLine mRouteLine;

	private TYPE type = TYPE.DRIVING;

	public RoutePlanAdapter(Context context, TYPE type) {
		super(context);
		this.type = type;
	}

	public RoutePlanAdapter(Context context, List<RouteStep> list, TYPE type) {
		super(context, list);
		this.type = type;
	}

	public RoutePlanAdapter(Context context, List<RouteStep> list) {
		super(context, list);
	}

	public void setRouteLine(RouteLine routeline, TYPE type) {
		this.type = type;
		switch (type) {
		case DRIVING:
			this.mRouteLine = (DrivingRouteLine) routeline;
			break;
		case TRANSIT:
			this.mRouteLine = (TransitRouteLine) routeline;
			break;
		case WALK:
			this.mRouteLine = (WalkingRouteLine) routeline;
			break;
		}
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mRouteLine == null ? 0 : this.mRouteLine.getAllStep().size();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;

		if (convertView == null) {
			holder = new ViewHolder();
			convertView = View.inflate(mContext, R.layout.route_plan_item_layout, null);
			holder.icon = (ImageView) convertView.findViewById(R.id.route_plan_item_layout_icon);
			holder.step = (TextView) convertView.findViewById(R.id.route_plan_item_layout_step);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (type == TYPE.DRIVING) {
			DrivingStep step = ((DrivingRouteLine) mRouteLine).getAllStep().get(position);
			holder.step.setText(step.getInstructions() + " - " + MapUtil.getDisanceStr(step.getDistance()));
		} else if (type == TYPE.TRANSIT) {
			TransitStep step = ((TransitRouteLine) mRouteLine).getAllStep().get(position);
			holder.step.setText(step.getInstructions() + " - " + MapUtil.getDisanceStr(step.getDistance()));
		} else if (type == TYPE.WALK) {
			WalkingStep step = ((WalkingRouteLine) mRouteLine).getAllStep().get(position);
			holder.step.setText(step.getInstructions() + " - " + MapUtil.getDisanceStr(step.getDistance()));
		}
		return convertView;
	}

	private static class ViewHolder {
		ImageView icon;
		TextView step;
	}

}
