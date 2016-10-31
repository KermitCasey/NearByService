package com.android.cheyooh.map.utils;

import android.content.Context;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

/**
 * 类说明： 使用百度地图自动定位，只能在主线程中创建，返回的结果是异步的，不需要另起线程。
 * 
 * @author Terry.Lu
 * @date 2013-4-22
 * @version 1.0
 */
public class AutoLocation {
	private Context mContext;
	private AutoLocationListener mListener;
	private LocationClient mLocationClient;

	public interface AutoLocationListener {

		/**
		 * 
		 * @param locType
		 *            61 ： GPS定位结果 62 ： 扫描整合定位依据失败。此时定位结果无效。 63 ：
		 *            网络异常，没有成功向服务器发起请求。此时定位结果无效。 65 ： 定位缓存的结果。 66 ： 离线定位结果。 67
		 *            ： 离线定位失败。 68 ： 网络连接失败时，查找本地离线定位时对应的返回结果 161： 表示网络定位结果
		 *            162~167： 服务端定位失败。
		 * @param cityName
		 *            城市名
		 */
		public void onFinish(int locType, BDLocation location);

		public void onError();
	}

	public AutoLocation(Context context, AutoLocationListener listener) {
		mContext = context.getApplicationContext();
		mListener = listener;
		initBdMap();
	}

	private void initBdMap() {
		mLocationClient = new LocationClient(mContext);
		mLocationClient.registerLocationListener(new MyLocationListenner(mListener));
		mLocationClient.setLocOption(getLocationOption());
		mLocationClient.start();
	}

	private LocationClientOption getLocationOption() {
		LocationClientOption option = new LocationClientOption();
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setIsNeedAddress(true);
		option.setOpenGps(true);
		option.setLocationNotify(true);
		option.SetIgnoreCacheException(true);
		option.setScanSpan(1000);
		// option.setServiceName("com.baidu.location.service_v2.9");
		// option.setPriority(LocationClientOption.GpsFirst); //不设置，默认是gps优先
		// option.disableCache(true);
		return option;
	}

	public void stop() {
		mLocationClient.registerLocationListener(null);
		mLocationClient.stop();
	}

	public boolean isLocating() {
		return mLocationClient.isStarted();
	}

	/**
	 * 
	 * @return 0：正常发起了定位。 1：服务没有启动。 2：没有监听函数。 6：请求间隔过短。 前后两次请求定位时间间隔不能小于1000ms。
	 */
	public int requestLocation() {
		return mLocationClient.requestLocation();
	}

	private static class MyLocationListenner implements BDLocationListener {
		private AutoLocationListener mListener;

		public MyLocationListenner(AutoLocationListener l) {
			mListener = l;
		}

		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null) {
				if (mListener != null) {
					mListener.onError();
				}
				return;
			}

			if (mListener != null) {
				mListener.onFinish(location.getLocType(), location);
			}
		}
	}
}
