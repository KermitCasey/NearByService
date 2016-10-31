package com.android.cheyooh.map.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.cheyooh.map.R;
import com.android.cheyooh.map.activity.LocationDetailInfo;
import com.android.cheyooh.map.lbs.Constant;
import com.android.cheyooh.map.utils.MapUtil;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.poi.PoiSearch;

import java.util.List;

/**
 * 类说明：
 *
 * @author Kermit
 * @version 1.0
 * @date 2015-7-29
 */
public class LocationSearchResultAdapter extends SimpleBaseAdapter<PoiInfo> {
    private String[] mId = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N"};
    private LatLng mLatlngPoint;

    public LocationSearchResultAdapter(Context context, List<PoiInfo> list) {
        super(context, list);
        mLatlngPoint = MapUtil.getLocation(context);
    }

    public LocationSearchResultAdapter(Context context) {
        super(context);
        mLatlngPoint = MapUtil.getLocation(context);
    }

    public void setDataList(List<PoiInfo> dataList){
        mList = dataList;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.location_search_result_item_layout, null);
            holder = new ViewHolder();
            holder.marker = (TextView) convertView.findViewById(R.id.location_search_result_item_layout_marker);
            holder.name = (TextView) convertView.findViewById(R.id.location_search_result_item_layout_name);
            holder.address = (TextView) convertView.findViewById(R.id.location_search_result_item_layout_address);
            holder.distance = (TextView) convertView.findViewById(R.id.location_search_result_item_layout_distance);
            holder.detail = (ImageView) convertView.findViewById(R.id.location_search_result_item_layout_detail);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final PoiInfo mkpoiInfo = mList.get(position);

        if (mkpoiInfo != null) {
            holder.marker.setText(mId[position]);
            holder.name.setText(mkpoiInfo.name);
            holder.address.setText(mkpoiInfo.address);
            holder.distance.setText(MapUtil.getDisanceString(MapUtil.getDistance(mLatlngPoint, mkpoiInfo.location)));
            holder.detail.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent it = new Intent(mContext, LocationDetailInfo.class);
                    String[] poiInfo = new String[3];
                    poiInfo[0] = mkpoiInfo.name;
                    poiInfo[1] = mkpoiInfo.address;
                    poiInfo[2] = mkpoiInfo.phoneNum;
                    it.putExtra(Constant.KEY_POI_INFO_END, poiInfo);
                    double[] point = new double[2];
                    point[0] = mkpoiInfo.location.latitude;
                    point[1] = mkpoiInfo.location.longitude;
                    it.putExtra(Constant.KEY_END_POINT, point);
                    it.putExtra(Constant.KEY_START_POINT, new double[]{mLatlngPoint.latitude, mLatlngPoint.longitude});
                    mContext.startActivity(it);
                }
            });
        }

        return convertView;
    }

    private static class ViewHolder {
        TextView marker;
        TextView name;
        TextView address;
        TextView distance;
        ImageView detail;
    }
}
