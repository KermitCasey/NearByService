package com.android.cheyooh.map.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.cheyooh.map.R;
import com.android.cheyooh.map.lbs.Constant;
import com.android.cheyooh.map.lbs.LocationItemizedOverlay;
import com.android.cheyooh.map.utils.AutoLocation;
import com.android.cheyooh.map.utils.AutoLocation.AutoLocationListener;
import com.android.cheyooh.map.utils.LogUtil;
import com.android.cheyooh.map.utils.MapUtil;
import com.android.cheyooh.map.view.LBSPopUpOptionView;
import com.android.cheyooh.map.view.LBSPopUpOptionView.LBSPopUpOptionClickLitener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMapTouchListener;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.DrivingRouteOverlay;
import com.baidu.mapapi.overlayutil.TransitRouteOverlay;
import com.baidu.mapapi.overlayutil.WalkingRouteOverlay;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteLine;
import com.baidu.mapapi.search.route.TransitRoutePlanOption;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteLine;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;

/**
 * @author:Kermit
 * @createTime:2015年7月24日 下午5:39:08 类说明
 */
public class LBSActivity extends BaseActivity implements OnClickListener, OnCheckedChangeListener, LBSPopUpOptionClickLitener, OnMapClickListener,
		OnMapTouchListener, AutoLocationListener, OnGetRoutePlanResultListener, OnGetPoiSearchResultListener {

	private static final String TAG = "LBSActivity";

	private LinearLayout mLeftBtnLayout;
	private RelativeLayout mTitleLayout;
	private LinearLayout mRightBtnLayout;
	private LinearLayout mBottomBtnLayout;
	private Button mRoutePlanResultBtn;
	private Button mSearchResultBtn;
	private TextView mMapScaleTv;
	private TextView mTitleTv;
	private String[] mScaleDesc;
	private LBSPopUpOptionView mPopUpOptionView;

	private double[] mStartPoint;
	private double[] mEndPoint;
	private String mStartName;
	private String mEndName;
	private int mSearchRoutePlanType;
	private PoiSearch mMapSearch;
	private RoutePlanSearch mRoutePlan;

	MapView mMapView = null;
	private BaiduMap mBaiduMap;
	LocationClient mLocClient;
	private AutoLocation mAutoLocation;
	private LocationItemizedOverlay mLocationItemizedOverlay;

	private ProgressDialog mProgressDlg;

	private int mSearchType = LBSPopUpOptionView.TYPE_GAS_STATION;

	private String mCity;
	private RouteLine routeLine;
	public static int mDefaultZoomLevel = 17; // 取值3-19
	private int mDefaultSearchRadius = 10000;
	private boolean mIsShowBtnLayout = true;
	private boolean mIsNeedRemovePopView = false; // 判断是否需要移除自定义的pop view
	private boolean mIsNeedShowPopUpOptionViewAfterLocation = true; // 在定位之后是否需要弹出搜索菜单

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.lbs_layout);
		// umeng
		MobclickAgent.updateOnlineConfig(this);
		initView();
		initMap();
		initData();
		getMyLocation();
	}

	private void initView() {
		mTitleLayout = (RelativeLayout) findViewById(R.id.lbs_title_layout);
		Button backBtn = (Button) findViewById(R.id.lbs_title_left_button);
		mTitleTv = (TextView) findViewById(R.id.lbs_title_text);
		Button rightBtn = (Button) findViewById(R.id.lbs_title_right_button);

		mLeftBtnLayout = (LinearLayout) findViewById(R.id.lbs_left_btn_layout);
		Button searchRouteBtn = (Button) findViewById(R.id.lbs_layout_search_route_btn);
		Button zoomInBtn = (Button) findViewById(R.id.lbs_layout_zoom_in_btn);
		Button zoomOutBtn = (Button) findViewById(R.id.lbs_layout_zoom_out_btn);

		mRightBtnLayout = (LinearLayout) findViewById(R.id.lbs_right_btn_layout);
		ToggleButton trafficBtn = (ToggleButton) findViewById(R.id.lbs_layout_toggle_traffic);

		mBottomBtnLayout = (LinearLayout) findViewById(R.id.lbs_bottom_btn_layout);
		Button myLocationBtn = (Button) findViewById(R.id.lbs_layout_my_location_btn);
		mSearchResultBtn = (Button) findViewById(R.id.lbs_layout_show_location_search_result_btn);
		mRoutePlanResultBtn = (Button) findViewById(R.id.lbs_layout_show_route_plan_result_btn);

		mMapScaleTv = (TextView) findViewById(R.id.lbs_layout_map_scale);
		mPopUpOptionView = (LBSPopUpOptionView) findViewById(R.id.lbs_pop_up_option_view);

		backBtn.setText(R.string.back);
		backBtn.setVisibility(View.GONE);
		// backBtn.setOnClickListener(this);
		rightBtn.setText(R.string.hide);
		rightBtn.setOnClickListener(this);
		mTitleTv.setText(R.string.choose);
		mTitleTv.setOnClickListener(this);

		searchRouteBtn.setOnClickListener(this);
		zoomInBtn.setOnClickListener(this);
		zoomOutBtn.setOnClickListener(this);
		trafficBtn.setOnCheckedChangeListener(this);
		myLocationBtn.setOnClickListener(this);
		mSearchResultBtn.setOnClickListener(this);
		mSearchResultBtn.setVisibility(View.GONE);
		mRoutePlanResultBtn.setOnClickListener(this);
		mRoutePlanResultBtn.setVisibility(View.GONE);

		mPopUpOptionView.setLBSPopUpOptionClickListener(this);
		UmengUpdateAgent.setUpdateAutoPopup(true);
		UmengUpdateAgent.setUpdateOnlyWifi(true);
		UmengUpdateAgent.update(this);
	}

	private void initMap() {
		mMapView = (MapView) findViewById(R.id.lbs_mapview);
		mMapView.showZoomControls(false);
		mMapView.removeViewAt(1);
		mBaiduMap = mMapView.getMap();
		mBaiduMap.setMaxAndMinZoomLevel(mBaiduMap.getMaxZoomLevel(), 6);
		mBaiduMap.setOnMapTouchListener(this);
		// mBaiduMap.animateMapStatus(u);
		mBaiduMap.setMyLocationEnabled(true);
		mAutoLocation = new AutoLocation(this, this);
		mLocationItemizedOverlay = new LocationItemizedOverlay(this, mMapView);
		mBaiduMap.setOnMarkerClickListener(mLocationItemizedOverlay);

		MapStatusUpdate defaultStatus = MapStatusUpdateFactory.zoomTo(mDefaultZoomLevel);
		mBaiduMap.animateMapStatus(defaultStatus);

		mMapSearch = PoiSearch.newInstance();
		mMapSearch.setOnGetPoiSearchResultListener(this);
		mRoutePlan = RoutePlanSearch.newInstance();
		mRoutePlan.setOnGetRoutePlanResultListener(this);
	}

	private void initData() {
		Resources res = getResources();
		mScaleDesc = res.getStringArray(R.array.map_scales_desc);
		mMapScaleTv.setText(mScaleDesc[mDefaultZoomLevel - 3]);
	}

	private boolean mShouldRemove;
	private View mTipView = null;
	private float mLastMotionX = 0;
	private float mLastMotionY = 0;
	private boolean mMoved;

	private LatLng mLatLng;

	@Override
	public void onTouch(MotionEvent event) {
		final float x = event.getX();
		final float y = event.getY();

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mLastMotionX = x;
			mLastMotionY = y;

			mTipView = mMapView.findViewById(R.id.pop_layout_root);
			if (mTipView != null && mTipView.isFocused()) {
				mShouldRemove = false;
			} else {
				mShouldRemove = true;
			}
			break;
		case MotionEvent.ACTION_MOVE:
			// 防手抖
			if ((int) Math.abs(x - mLastMotionX) >= 15 || (int) Math.abs(y - mLastMotionY) >= 15) {
				mShouldRemove = false;
				mMoved = true;
			}

			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			if (mTipView != null && mShouldRemove) {
				removeView(mTipView, mShouldRemove);
				mTipView = null;
			}
			if (!mIsShowBtnLayout && !mMoved) {
				showPanel(true);
			}

			mMoved = false;
			break;
		default:
			break;
		}
	}

	@Override
	public void onOptionClick(View v, int type) {

		LatLng mCenterLatLng = mMapView.getMap().getMapStatus().target;
		switch (type) {
		case LBSPopUpOptionView.TYPE_AUTO_4S:
			mSearchType = LBSPopUpOptionView.TYPE_AUTO_4S;
			search(mSearchType, mCenterLatLng);
			break;
		case LBSPopUpOptionView.TYPE_AUTO_BEAUTY:
			mSearchType = LBSPopUpOptionView.TYPE_AUTO_BEAUTY;
			search(mSearchType, mCenterLatLng);
			break;
		case LBSPopUpOptionView.TYPE_AUTO_MAINTENACE:
			mSearchType = LBSPopUpOptionView.TYPE_AUTO_MAINTENACE;
			search(mSearchType, mCenterLatLng);
			break;
		case LBSPopUpOptionView.TYPE_AUTO_PARTS:
			mSearchType = LBSPopUpOptionView.TYPE_AUTO_PARTS;
			search(mSearchType, mCenterLatLng);
			break;
		case LBSPopUpOptionView.TYPE_BANK:
			mSearchType = LBSPopUpOptionView.TYPE_BANK;
			search(mSearchType, mCenterLatLng);
			break;
		case LBSPopUpOptionView.TYPE_CAR_WASH:
			mSearchType = LBSPopUpOptionView.TYPE_CAR_WASH;
			search(mSearchType, mCenterLatLng);
			break;
		case LBSPopUpOptionView.TYPE_DEPOT:
			mSearchType = LBSPopUpOptionView.TYPE_DEPOT;
			search(mSearchType, mCenterLatLng);
			break;
		case LBSPopUpOptionView.TYPE_GAS_STATION:
			mSearchType = LBSPopUpOptionView.TYPE_GAS_STATION;
			search(mSearchType, mCenterLatLng);
			break;
		case LBSPopUpOptionView.TYPE_VAO:
			mSearchType = LBSPopUpOptionView.TYPE_VAO;
			search(mSearchType, mCenterLatLng);
			break;
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (isChecked) {
			buttonView.setBackgroundResource(R.drawable.map_traffic_on);
		} else {
			buttonView.setBackgroundResource(R.drawable.map_traffic_off);
		}
		mBaiduMap.setTrafficEnabled(isChecked);
	}

	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
		// case R.id.lbs_title_left_button:
		// finish();
		// break;
		case R.id.lbs_title_right_button: // 隐藏
			showPanel(!mIsShowBtnLayout);
			break;
		case R.id.lbs_title_text:
			mPopUpOptionView.showOptions();
			break;
		case R.id.lbs_layout_search_route_btn:
			if (null != mLatLng) {
				intent = new Intent(this, SearchRoutePlan.class);
				intent.putExtra(Constant.KEY_START_POINT, new double[] { mLatLng.latitude, mLatLng.longitude });
				startActivity(intent);
			}
			break;
		case R.id.lbs_layout_zoom_in_btn:
			zoom(true);
			break;
		case R.id.lbs_layout_zoom_out_btn:
			zoom(false);
			break;
		case R.id.lbs_layout_my_location_btn:
			getMyLocation();
			break;
		case R.id.lbs_layout_show_location_search_result_btn:
			intent = new Intent(this, LocationSearchResult.class);
			intent.putExtra(Constant.SEARCH_KEY, getSearchKey(mSearchType));
			startActivity(intent);
			break;
		case R.id.lbs_layout_show_route_plan_result_btn:
			intent = new Intent(this, RoutePlan.class);
			intent.putExtra(Constant.KEY_SEARCH_ROUTE_PLAN_TYPE, mSearchRoutePlanType);
			intent.putExtra(Constant.KEY_PLAN_NODE_START_NAME, mStartName);
			intent.putExtra(Constant.KEY_PLAN_NODE_END_NAME, mEndName);
			intent.putExtra(Constant.KEY_START_POINT, mStartPoint);
			intent.putExtra(Constant.KEY_END_POINT, mEndPoint);
			Constant.mRouteline = routeLine;
			startActivity(intent);
			break;
		}
	}

	private void zoom(boolean in) {
		MapStatusUpdate u;
		if (in) {
			u = MapStatusUpdateFactory.zoomIn();
		} else {
			u = MapStatusUpdateFactory.zoomOut();
		}
		mBaiduMap.animateMapStatus(u);
	}

	public void removeView(View view, boolean remove) {
		LogUtil.d(TAG, "removeView");
		// mLocationItemizedOverlay.resetIndex();
		mMapView.removeView(view);
	}

	private void showPanel(final boolean isShow) {
		mIsShowBtnLayout = isShow;
		Animation topPanelAnim = null;
		Animation leftPanelAnim = null;
		Animation bottomPanelAnim = null;
		Animation rightPanelAnim = null;

		if (isShow) {
			topPanelAnim = AnimationUtils.loadAnimation(this, R.anim.top_panel_entry);
			leftPanelAnim = AnimationUtils.loadAnimation(this, R.anim.left_panel_entry);
			bottomPanelAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_panel_entry);
			rightPanelAnim = AnimationUtils.loadAnimation(this, R.anim.right_panel_entry);
		} else {
			topPanelAnim = AnimationUtils.loadAnimation(this, R.anim.top_panel_exit);
			leftPanelAnim = AnimationUtils.loadAnimation(this, R.anim.left_panel_exit);
			bottomPanelAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_panel_exit);
			rightPanelAnim = AnimationUtils.loadAnimation(this, R.anim.right_panel_exit);
		}

		topPanelAnim.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				if (isShow) {
					mTitleLayout.setVisibility(View.VISIBLE);
					mLeftBtnLayout.setVisibility(View.VISIBLE);
					mBottomBtnLayout.setVisibility(View.VISIBLE);
					mRightBtnLayout.setVisibility(View.VISIBLE);
				}
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				if (!isShow) {
					mTitleLayout.setVisibility(View.GONE);
					mLeftBtnLayout.setVisibility(View.GONE);
					mBottomBtnLayout.setVisibility(View.GONE);
					mRightBtnLayout.setVisibility(View.GONE);
				}
			}
		});

		mTitleLayout.startAnimation(topPanelAnim);
		mLeftBtnLayout.startAnimation(leftPanelAnim);
		mBottomBtnLayout.startAnimation(bottomPanelAnim);
		mRightBtnLayout.startAnimation(rightPanelAnim);
	}

	@Override
	public void onMapClick(LatLng arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onMapPoiClick(MapPoi arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	private void getMyLocation() {
		showProgressDialog(getResources().getString(R.string.getting_position_wait));
		int ret = mAutoLocation.requestLocation();
		LogUtil.d(TAG, "ret:" + ret);
	}

	private void gotoMyLocation(BDLocation location) {
		if (location != null) {
			mLatLng = new LatLng(location.getLatitude(), location.getLongitude());
			MyLocationData locData = new MyLocationData.Builder().accuracy(location.getRadius())
					// 此处设置开发者获取到的方向信息，顺时针0-360
					.direction(location.getDirection()).latitude(location.getLatitude()).longitude(location.getLongitude()).build();
			mBaiduMap.setMyLocationData(locData);
			gotoLocation(new LatLng(location.getLatitude(), location.getLongitude()));
			MapUtil.saveLocation(this, location);
		}
	}

	private void gotoLocation(LatLng mLatLng) {
		MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(mLatLng);
		mBaiduMap.animateMapStatus(update);
		MapStatus status = mBaiduMap.getMapStatus();
		int level = (int) status.zoom;
		if (level - 3 >= mScaleDesc.length) {
			level = mScaleDesc.length + 2;
		} else if (level < 3) {
			level = 3;
		}
		mMapScaleTv.setText(mScaleDesc[level - 3]);
	}

	private void showRoutePlanResult(Intent intent) {
		showProgressDialog(getResources().getString(R.string.searching_route_plan));
		mRoutePlanResultBtn.setVisibility(View.VISIBLE);
		PlanNode start;
		mStartPoint = intent.getDoubleArrayExtra(Constant.KEY_START_POINT);
		String[] startPoiInfoArray = intent.getStringArrayExtra(Constant.KEY_POI_INFO_START);

		if (startPoiInfoArray != null) {
			mStartName = startPoiInfoArray[0];
		}

		if (mStartPoint != null) {
			start = PlanNode.withLocation(new LatLng(mStartPoint[0], mStartPoint[1]));
		} else {
			start = PlanNode.withLocation(mLatLng);
		}

		mEndPoint = intent.getDoubleArrayExtra(Constant.KEY_END_POINT);
		PlanNode end = PlanNode.withLocation(new LatLng(mEndPoint[0], mEndPoint[1]));
		String[] endPoiInfoArray = intent.getStringArrayExtra(Constant.KEY_POI_INFO_END);

		if (endPoiInfoArray != null) {
			mEndName = endPoiInfoArray[0];
		}

		mCity = MapUtil.getCity(this);
		mSearchRoutePlanType = intent.getIntExtra(Constant.KEY_SEARCH_ROUTE_PLAN_TYPE, Constant.SEARCH_ROUTE_PLAN_TYPE_DRIVING);

		switch (mSearchRoutePlanType) {
		case Constant.SEARCH_ROUTE_PLAN_TYPE_DRIVING:
			mRoutePlan.drivingSearch(new DrivingRoutePlanOption().from(start).to(end));
			break;
		case Constant.SEARCH_ROUTE_PLAN_TYPE_TRANSIT:
			mRoutePlan.transitSearch(new TransitRoutePlanOption().from(start).to(end).city(mCity));
			break;
		case Constant.SEARCH_ROUTE_PLAN_TYPE_WALKING:
			mRoutePlan.walkingSearch(new WalkingRoutePlanOption().from(start).to(end));
			break;
		default:
			break;
		}

	}

	private void showProgressDialog(String message) {
		mProgressDlg = new ProgressDialog(this);
		mProgressDlg.setMessage(message);
		mProgressDlg.setCancelable(true);
		mProgressDlg.setCanceledOnTouchOutside(false);
		mProgressDlg.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				mProgressDlg = null;
			}
		});
		mProgressDlg.show();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
		mRoutePlan.destroy();
		mMapView.onDestroy();
	}

	@Override
	protected void onResume() {
		super.onResume();
		// 在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
		mMapView.onResume();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		// int newIntentType = intent.getIntExtra(Constant.KEY_NEW_INTENT_TYPE,
		// Constant.NEW_INTENT_TYPE_SEARCH_ROUTE_PLAN);
		int newIntentType = intent.getIntExtra(Constant.KEY_NEW_INTENT_TYPE, -1);
		LogUtil.d(TAG, "newIntentType:" + newIntentType);
		if (newIntentType == -1) {
			return;
		}
		if (newIntentType == Constant.NEW_INTENT_TYPE_SEARCH_ROUTE_PLAN) {
			showRoutePlanResult(intent);
		} else if (newIntentType == Constant.NEW_INTENT_TYPE_GOTO_GEOPOINT) {
			double[] point = intent.getDoubleArrayExtra(Constant.KEY_START_POINT);
			int index = intent.getIntExtra(Constant.KEY_SELECTED_INDEX, 0);
			Bundle bundle = intent.getExtras();
			mLocationItemizedOverlay.setPointData(point[0], point[1], bundle.getString("name"), bundle.getString("address"), bundle.getString("phoneNum"));
		} else if (newIntentType == Constant.NEW_INTENT_TYPE_REFRESH_POI_LIST) {
			mIsNeedRemovePopView = true;
			// drawPoi(mMapSearch.getPoiInfoList());
		} else if (newIntentType == Constant.NEW_INTENT_TYPE_REFRESH_ROUTE_PLAN) {
			drawPoi(intent.getIntExtra(Constant.ROUTE_PLAN_TYPE, 0));
		}
	}

	private void drawPoi(int type) {
		if (null == Constant.mRouteline) {
			return;
		}
		mBaiduMap.clear();
		routeLine = Constant.mRouteline;
		if (mIsNeedRemovePopView) {
			View popView = mMapView.findViewById(R.id.pop_layout_root);
			if (popView != null) {
				mMapView.removeView(popView);
			}
			mIsNeedRemovePopView = false;
		}
		switch (type) {
		case Constant.SEARCH_ROUTE_PLAN_TYPE_DRIVING:
			DrivingRouteOverlay drivingOverlay = new DrivingRouteOverlay(mBaiduMap);
			mBaiduMap.setOnMarkerClickListener(drivingOverlay);
			drivingOverlay.setData((DrivingRouteLine) routeLine);
			drivingOverlay.addToMap();
			drivingOverlay.zoomToSpan();
			break;
		case Constant.SEARCH_ROUTE_PLAN_TYPE_TRANSIT:
			TransitRouteOverlay transitRouteOverlay = new TransitRouteOverlay(mBaiduMap);
			mBaiduMap.setOnMarkerClickListener(transitRouteOverlay);
			transitRouteOverlay.setData((TransitRouteLine) routeLine);
			transitRouteOverlay.addToMap();
			transitRouteOverlay.zoomToSpan();
			break;
		case Constant.SEARCH_ROUTE_PLAN_TYPE_WALKING:
			WalkingRouteOverlay walkingRouteOverlay = new WalkingRouteOverlay(mBaiduMap);
			mBaiduMap.setOnMarkerClickListener(walkingRouteOverlay);
			walkingRouteOverlay.setData((WalkingRouteLine) routeLine);
			walkingRouteOverlay.addToMap();
			walkingRouteOverlay.zoomToSpan();
			break;
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		// 在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
		mMapView.onPause();
	}

	@Override
	public void onFinish(int locType, BDLocation location) {
		LogUtil.i(TAG, "locType=" + locType);
		if (mProgressDlg != null && mProgressDlg.isShowing()) {
			mProgressDlg.dismiss();
			mProgressDlg = null;

			if (locType == 61 || locType == 161) {
				gotoMyLocation(location);

				if (mIsNeedShowPopUpOptionViewAfterLocation) {
					mPopUpOptionView.showOptions();
				}
				// mMapSearch.poiSearchNearBy(getSearchKey(mSearchType),
				// mMyGeoPoint, mDefaultSearchRadius);
			} else {
				Toast.makeText(this, R.string.location_failed, Toast.LENGTH_SHORT).show();
			}
		}
		mIsNeedShowPopUpOptionViewAfterLocation = false;
	}

	@Override
	public void onError() {
		if (mProgressDlg != null && mProgressDlg.isShowing()) {
			mProgressDlg.dismiss();
			Toast.makeText(this, R.string.location_failed, Toast.LENGTH_SHORT).show();
		}
		mIsNeedShowPopUpOptionViewAfterLocation = false;
	}

	@Override
	public void onGetDrivingRouteResult(DrivingRouteResult result) {
		showRouteResultLine(result, Constant.SEARCH_ROUTE_PLAN_TYPE_DRIVING);
	}

	@Override
	public void onGetTransitRouteResult(TransitRouteResult result) {
		showRouteResultLine(result, Constant.SEARCH_ROUTE_PLAN_TYPE_TRANSIT);
	}

	@Override
	public void onGetWalkingRouteResult(WalkingRouteResult result) {
		showRouteResultLine(result, Constant.SEARCH_ROUTE_PLAN_TYPE_WALKING);
	}

	public void showRouteResultLine(SearchResult result, int Type) {

		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
		}
		if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
			// 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
			// result.getSuggestAddrInfo();
			return;
		}
		if (result.error == SearchResult.ERRORNO.NO_ERROR) {
			mBaiduMap.clear();

			switch (Type) {
			case Constant.SEARCH_ROUTE_PLAN_TYPE_DRIVING:
				DrivingRouteOverlay drivingOverlay = new DrivingRouteOverlay(mBaiduMap);
				mBaiduMap.setOnMarkerClickListener(drivingOverlay);
				routeLine = ((DrivingRouteResult) result).getRouteLines().get(0);
				drivingOverlay.setData((DrivingRouteLine) routeLine);
				drivingOverlay.addToMap();
				drivingOverlay.zoomToSpan();
				break;
			case Constant.SEARCH_ROUTE_PLAN_TYPE_TRANSIT:
				TransitRouteOverlay transitOverlay = new TransitRouteOverlay(mBaiduMap);
				mBaiduMap.setOnMarkerClickListener(transitOverlay);
				routeLine = ((TransitRouteResult) result).getRouteLines().get(0);
				transitOverlay.setData((TransitRouteLine) routeLine);
				transitOverlay.addToMap();
				transitOverlay.zoomToSpan();
				break;
			case Constant.SEARCH_ROUTE_PLAN_TYPE_WALKING:
				WalkingRouteOverlay walkingOverlay = new WalkingRouteOverlay(mBaiduMap);
				mBaiduMap.setOnMarkerClickListener(walkingOverlay);
				routeLine = ((WalkingRouteResult) result).getRouteLines().get(0);
				walkingOverlay.setData((WalkingRouteLine) routeLine);
				walkingOverlay.addToMap();
				walkingOverlay.zoomToSpan();
				break;
			default:
				break;
			}

		}
	}

	private String getSearchKey(int type) {
		String searchKey = null;
		switch (type) {
		case LBSPopUpOptionView.TYPE_GAS_STATION:
			searchKey = getString(R.string.gas_station);
			break;

		case LBSPopUpOptionView.TYPE_DEPOT:
			searchKey = getString(R.string.depot);
			break;
		case LBSPopUpOptionView.TYPE_CAR_WASH:
			searchKey = getString(R.string.car_wash);
			break;
		case LBSPopUpOptionView.TYPE_AUTO_4S:
			searchKey = getString(R.string.auto_4s_shop);
			break;
		case LBSPopUpOptionView.TYPE_AUTO_MAINTENACE:
			searchKey = getString(R.string.auto_maintenance);
			break;
		case LBSPopUpOptionView.TYPE_VAO:
			searchKey = getString(R.string.transportation_department);
			break;
		case LBSPopUpOptionView.TYPE_AUTO_BEAUTY:
			searchKey = getString(R.string.auto_beauty);
			break;
		case LBSPopUpOptionView.TYPE_AUTO_PARTS:
			searchKey = getString(R.string.auto_parts);
			break;

		case LBSPopUpOptionView.TYPE_BANK:
			searchKey = getString(R.string.bank);
			break;
		default:
			break;
		}

		return searchKey;
	}

	private void search(int type, LatLng point) {
		String searchKey = getSearchKey(type);
		mTitleTv.setText(searchKey);
		showProgressDialog(getString(R.string.searching) + searchKey);
		mIsNeedRemovePopView = true;
		mMapSearch.searchNearby(new PoiNearbySearchOption().keyword(searchKey).radius(mDefaultSearchRadius).location(mLatLng));
	}

	@Override
	public void onGetPoiDetailResult(PoiDetailResult result) {

	}

	@Override
	public void onGetPoiResult(PoiResult result) {
		if (mProgressDlg != null && mProgressDlg.isShowing()) {
			mProgressDlg.dismiss();
			mProgressDlg = null;
		}
		if (result.error == SearchResult.ERRORNO.NO_ERROR) {
			Constant.mPoiResult = result;
			mLocationItemizedOverlay.setData(result.getAllPoi());
			mSearchResultBtn.setVisibility(View.VISIBLE);
		} else {
			Toast.makeText(this, getSearchKey(mSearchType) + getString(R.string.search_failed), Toast.LENGTH_SHORT).show();
		}
	}
}
