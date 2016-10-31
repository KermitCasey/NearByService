package com.android.cheyooh.map.lbs;

import java.util.List;

import com.android.cheyooh.map.R;
import com.android.cheyooh.map.activity.LocationDetailInfo;
import com.android.cheyooh.map.utils.MapUtil;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.InfoWindow.OnInfoWindowClickListener;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

/**
 * 类说明：
 *
 * @author Terry.Lu
 * @version 1.0
 * @date 2013-5-8
 */
public class LocationItemizedOverlay implements OnMarkerClickListener, OnInfoWindowClickListener {
    private List<PoiInfo> mSearchPoiInfos;
    private Activity mActivity;
    private float mDensity;
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private View mPopView;
    private Button mDetail;
    private InfoWindow mInfoWindow;
    private Marker mCurrentMarker;

    public LocationItemizedOverlay(Activity activity, MapView mapView) {

        mMapView = mapView;
        mBaiduMap = mMapView.getMap();
        mActivity = activity;
        DisplayMetrics dm = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        mDensity = dm.density;
        mPopView = LayoutInflater.from(mActivity).inflate(R.layout.pop_layout, null);
        mDetail = (Button) mPopView.findViewById(R.id.pop_layout_detail);
    }

    private void startActivity() {
        if (null == mSearchPoiInfos || mSearchPoiInfos.size() == 0) {
            return;
        }
        Intent it = new Intent(mActivity, LocationDetailInfo.class);
        String[] poiInfo = new String[3];
        Bundle bundle = mCurrentMarker.getExtraInfo();

        poiInfo[0] = bundle.getString("name");
        poiInfo[1] = bundle.getString("address");
        poiInfo[2] = bundle.getString("phoneNum");
        it.putExtra(Constant.KEY_POI_INFO_END, poiInfo);
        double[] point = new double[2];
        point[0] = mCurrentMarker.getPosition().latitude;
        point[1] = mCurrentMarker.getPosition().longitude;
        it.putExtra(Constant.KEY_END_POINT, point);
        point = new double[2];
        LatLng geopoint = MapUtil.getLocation(mActivity);
        point[0] = geopoint.latitude;
        point[1] = geopoint.longitude;
        it.putExtra(Constant.KEY_START_POINT, point);
        mActivity.startActivity(it);
    }


    public void setData(List<PoiInfo> poiList) {
        this.mSearchPoiInfos = poiList;
        if (null == poiList || poiList.size() == 0) {
            return;
        }
        MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(poiList.get(0).location);
        mBaiduMap.animateMapStatus(update);
        mBaiduMap.clear();

        for (int i = 0; i < mSearchPoiInfos.size(); i++) {
            PoiInfo info = mSearchPoiInfos.get(i);
            Bundle bundle = new Bundle();
            bundle.putString("name", info.name);
            bundle.putString("address", info.address);
            bundle.putString("phoneNum", info.phoneNum);
            OverlayOptions options = new MarkerOptions().position(info.location)
                    .icon(BitmapDescriptorFactory.fromBitmap(MapUtil.getMarkerIcon(mActivity, i + 1))).title(info.name).extraInfo(bundle);
            mBaiduMap.addOverlay(options);
        }
    }

    public void setPointData(double lat, double lng, String name, String address, String phoneNum) {

        if (lat <= 0 || lng <= 0) {
            return;
        }
        if (TextUtils.isEmpty(name)) {
            name = "";
        }
        if (TextUtils.isEmpty(address)) {
            address = "";
        }
        if (TextUtils.isEmpty(phoneNum)) {
            phoneNum = "";
        }
        MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(new LatLng(lat, lng));
        mBaiduMap.animateMapStatus(update);
        mBaiduMap.clear();

        Bundle bundle = new Bundle();
        bundle.putString("name", name);
        bundle.putString("address", address);
        bundle.putString("phoneNum", phoneNum);
        OverlayOptions options = new MarkerOptions().position(new LatLng(lat, lng))
                .icon(BitmapDescriptorFactory.fromBitmap(MapUtil.getMarkerIcon(mActivity, 1))).title(name).extraInfo(bundle);
        Marker marker = (Marker) mBaiduMap.addOverlay(options);
        mCurrentMarker = marker;
        showTip(marker);
    }

    public List<PoiInfo> getlist() {
        return mSearchPoiInfos;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (TextUtils.isEmpty(marker.getTitle())) {
            return false;
        }
        mCurrentMarker = marker;
        showTip(marker);
        return false;
    }

    public void showTip(Marker marker) {
        mMapView.removeView(mPopView);
        MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(marker.getPosition());
        mBaiduMap.animateMapStatus(update);

        mDetail.setText(marker.getTitle());
        mInfoWindow = new InfoWindow(BitmapDescriptorFactory.fromView(mPopView), marker.getPosition(), -40, this);
        mBaiduMap.showInfoWindow(mInfoWindow);
    }

    @Override
    public void onInfoWindowClick() {
        startActivity();
    }

}
