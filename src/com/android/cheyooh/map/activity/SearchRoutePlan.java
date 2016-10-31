package com.android.cheyooh.map.activity;

import java.util.List;

import com.android.cheyooh.map.R;
import com.android.cheyooh.map.adapter.PoiListAdapter;
import com.android.cheyooh.map.lbs.Constant;
import com.android.cheyooh.map.utils.MapUtil;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 类说明：
 * 
 * @author Terry Lu
 * @date 2012-6-7
 * @version 1.0
 * 
 * @modify xinhui.cheng
 * @modify-date 2012-5-9
 * @modify-version 2.0
 */
public class SearchRoutePlan extends BaseActivity implements OnClickListener, OnGetPoiSearchResultListener {

	private static final String TAG = SearchRoutePlan.class.getSimpleName();
	private EditText mEditTextFrom;
	private EditText mEditTextTo;
	private Button mBtnBus;
	private Button mBtnCar;
	private Button mBtnWalk;
	private int mPageIndex = 0;
	private int mPageNums = 0;
	private String[] mStartPoiInfoArrary;
	private String[] mEndPoiInfoArray;
	private double[] mStartPoint;
	private double[] mEndPoint;

	private PoiSearch mMapSearch;
	private PoiCitySearchOption searchOption = new PoiCitySearchOption();

	private ProgressDialog mProgressDialog;
	private boolean mIsFlipOver = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.search_route_plan_layout);
		initData();
		initViews();
	}

	@Override
	public void onResume() {
		super.onResume();
		// MobclickAgent.onResume(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		// MobclickAgent.onPause(this);
	}

	private void initData() {
		Intent it = getIntent();
		mStartPoiInfoArrary = it.getStringArrayExtra(Constant.KEY_POI_INFO_START);
		mEndPoiInfoArray = it.getStringArrayExtra(Constant.KEY_POI_INFO_END);
		mStartPoint = it.getDoubleArrayExtra(Constant.KEY_START_POINT);
		mEndPoint = it.getDoubleArrayExtra(Constant.KEY_END_POINT);
	}

	private void initViews() {
		mMapSearch = PoiSearch.newInstance();
		mMapSearch.setOnGetPoiSearchResultListener(this);
		Button btn = (Button) findViewById(R.id.title_left_button);
		btn.setText(R.string.back);
		btn.setOnClickListener(this);
		findViewById(R.id.title_right_button).setVisibility(View.GONE);
		TextView tvTitle = (TextView) findViewById(R.id.title_text);
		tvTitle.setText(R.string.search_location);
		mEditTextFrom = (EditText) findViewById(R.id.search_route_plan_layout_from);
		if (mStartPoiInfoArrary != null) {
			String name = mStartPoiInfoArrary[0];
			mEditTextFrom.setText(name);
		}

		mEditTextTo = (EditText) findViewById(R.id.search_route_plan_layout_to);
		if (mEndPoiInfoArray != null) {
			String name = mEndPoiInfoArray[0];
			mEditTextTo.setText(name);
		}

		mBtnBus = (Button) findViewById(R.id.search_route_plan_layout_bus);
		mBtnBus.setOnClickListener(this);
		mBtnCar = (Button) findViewById(R.id.search_route_plan_layout_car);
		mBtnCar.setOnClickListener(this);
		mBtnWalk = (Button) findViewById(R.id.search_route_plan_layout_walk);
		mBtnWalk.setOnClickListener(this);
		setButtonSeleted(R.id.search_route_plan_layout_car);
		findViewById(R.id.search_route_plan_layout_begin_btn).setOnClickListener(this);

		mEditTextFrom.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				mStartPoint = null;
			}
		});

		mEditTextTo.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence charsequence, int i, int j, int k) {
			}

			@Override
			public void beforeTextChanged(CharSequence charsequence, int i, int j, int k) {
			}

			@Override
			public void afterTextChanged(Editable editable) {
				mEndPoint = null;
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_left_button:
			finish();
			break;
		case R.id.search_route_plan_layout_bus:
			mSearchRoutePlanType = Constant.SEARCH_ROUTE_PLAN_TYPE_TRANSIT;
			setButtonSeleted(R.id.search_route_plan_layout_bus);
			break;
		case R.id.search_route_plan_layout_car:
			mSearchRoutePlanType = Constant.SEARCH_ROUTE_PLAN_TYPE_DRIVING;
			setButtonSeleted(R.id.search_route_plan_layout_car);
			break;
		case R.id.search_route_plan_layout_walk:
			mSearchRoutePlanType = Constant.SEARCH_ROUTE_PLAN_TYPE_WALKING;
			setButtonSeleted(R.id.search_route_plan_layout_walk);
			break;
		case R.id.search_route_plan_layout_begin_btn:
			mPageIndex = 0;
			doSearch();
			break;
		case R.id.poi_list_dialog_layout_prevous:
			if (mPageIndex > 0) {
				mProgressDialog = ProgressDialog.show(this, null, getString(R.string.searching_poi_please_wait), true, true);
				mPageIndex--;
				mIsFlipOver = true;
				searchOption.pageNum(mPageIndex);
				mMapSearch.searchInCity(searchOption);
			} else {
				Toast.makeText(getApplicationContext(), R.string.no_prev_page, Toast.LENGTH_SHORT).show();
			}

			break;
		case R.id.poi_list_dialog_layout_next:
			if (mPageIndex < mPageNums - 1) {
				mProgressDialog = ProgressDialog.show(this, null, getString(R.string.searching_poi_please_wait), true, true);
				mPageIndex++;
				mIsFlipOver = true;
				searchOption.pageNum(mPageIndex);
				mMapSearch.searchInCity(searchOption);
			} else {
				Toast.makeText(getApplicationContext(), R.string.no_next_page, Toast.LENGTH_SHORT).show();
			}
		default:
			break;
		}
	}

	private void setButtonSeleted(int resId) {
		switch (resId) {
		case R.id.search_route_plan_layout_bus:
			mBtnBus.setBackgroundResource(R.drawable.btn_transit_press);
			mBtnCar.setBackgroundResource(R.drawable.btn_driving_normal);
			mBtnWalk.setBackgroundResource(R.drawable.btn_walking_normal);
			break;
		case R.id.search_route_plan_layout_car:
			mBtnBus.setBackgroundResource(R.drawable.btn_transit_normal);
			mBtnCar.setBackgroundResource(R.drawable.btn_driving_press);
			mBtnWalk.setBackgroundResource(R.drawable.btn_walking_normal);
			break;
		case R.id.search_route_plan_layout_walk:
			mBtnBus.setBackgroundResource(R.drawable.btn_transit_normal);
			mBtnCar.setBackgroundResource(R.drawable.btn_driving_normal);
			mBtnWalk.setBackgroundResource(R.drawable.btn_walking_press);
			break;
		default:
			break;
		}
	}

	private void doSearch() {
		String fromName = mEditTextFrom.getText().toString();
		String toName = mEditTextTo.getText().toString();

		if (fromName.length() == 0 || toName.length() == 0) {
			Toast.makeText(SearchRoutePlan.this, R.string.start_dest_null, Toast.LENGTH_SHORT).show();
			return;
		}

		mCity = MapUtil.getCity(this);

		searchOption.city(mCity);
		searchOption.pageNum(mPageIndex);

		if (mStartPoint == null) {
			mProgressDialog = ProgressDialog.show(this, null, getString(R.string.searching_poi_please_wait), true, true);
			initStartPoiInfoDialog();
			searchOption.keyword(fromName);
			mMapSearch.searchInCity(searchOption);
		} else if (mStartPoint != null && mEndPoint == null) {
			mProgressDialog = ProgressDialog.show(this, null, getString(R.string.searching_poi_please_wait), true, true);
			initEndPoiInfoDialog();
			searchOption.keyword(toName);
			mMapSearch.searchInCity(searchOption);
		} else {
			startActivity();
		}
	}

	private String mCity;
	private AlertDialog.Builder mBuilder;
	private PoiListAdapter mAdapter;
	private List<PoiInfo> mPoiInfoList;
	private boolean mIsDialogShow;
	private int mSearchRoutePlanType = Constant.SEARCH_ROUTE_PLAN_TYPE_DRIVING;
	private View mPoiListViewParent;

	private void initPoiListView() {
		mPoiListViewParent = LayoutInflater.from(SearchRoutePlan.this).inflate(R.layout.poi_list_dialog_layout, null);
		mPoiListViewParent.findViewById(R.id.poi_list_dialog_layout_prevous).setOnClickListener(this);
		mPoiListViewParent.findViewById(R.id.poi_list_dialog_layout_next).setOnClickListener(this);
	}

	private void initStartPoiInfoDialog() {
		initPoiListView();
		mBuilder = new AlertDialog.Builder(SearchRoutePlan.this);
		mBuilder.setTitle("选择一个起点");
		mAdapter = new PoiListAdapter(getApplicationContext(), mPoiInfoList);
		mBuilder.setAdapter(mAdapter, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				PoiInfo poiInfo = mPoiInfoList.get(which);
				if (mStartPoint == null) {
					mStartPoint = new double[2];
				}
				mStartPoint[0] = poiInfo.location.latitude;
				mStartPoint[1] = poiInfo.location.longitude;
				if (mStartPoiInfoArrary == null) {
					mStartPoiInfoArrary = new String[3];
				}
				mStartPoiInfoArrary[0] = poiInfo.name;
				mStartPoiInfoArrary[1] = poiInfo.address;
				mStartPoiInfoArrary[2] = poiInfo.phoneNum;
				initEndPoiInfoDialog();
				PoiCitySearchOption searchOption = new PoiCitySearchOption();
				searchOption.city(mCity);
				searchOption.keyword(mEditTextTo.getText().toString());
				mIsDialogShow = false;
			}
		});

		mBuilder.setView(mPoiListViewParent);
		mIsDialogShow = false;
	}

	private void initEndPoiInfoDialog() {
		initPoiListView();
		mBuilder = new AlertDialog.Builder(SearchRoutePlan.this);
		mBuilder.setTitle("选择一个终点");
		mAdapter = new PoiListAdapter(getApplicationContext(), mPoiInfoList);
		mBuilder.setAdapter(mAdapter, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				PoiInfo poiInfo = mPoiInfoList.get(which);
				if (mEndPoint == null) {
					mEndPoint = new double[2];
				}
				mEndPoint[0] = poiInfo.location.latitude;
				mEndPoint[1] = poiInfo.location.longitude;
				if (mEndPoiInfoArray == null) {
					mEndPoiInfoArray = new String[3];
				}

				mEndPoiInfoArray[0] = poiInfo.name;
				mEndPoiInfoArray[1] = poiInfo.address;
				mEndPoiInfoArray[2] = poiInfo.phoneNum;
				startActivity();
			}
		});

		mBuilder.setView(mPoiListViewParent);
		mIsDialogShow = false;
	}

	private void startActivity() {
		Intent it = new Intent(this, LBSActivity.class);
		it.putExtra(Constant.KEY_NEW_INTENT_TYPE, Constant.NEW_INTENT_TYPE_SEARCH_ROUTE_PLAN);
		it.putExtra(Constant.KEY_START_POINT, mStartPoint);
		it.putExtra(Constant.KEY_END_POINT, mEndPoint);
		it.putExtra(Constant.KEY_SEARCH_ROUTE_PLAN_TYPE, mSearchRoutePlanType);

		if (mStartPoiInfoArrary == null) {
			mStartPoiInfoArrary = new String[3];
			mStartPoiInfoArrary[0] = mEditTextFrom.getText().toString();
		}

		it.putExtra(Constant.KEY_POI_INFO_START, mStartPoiInfoArrary);
		it.putExtra(Constant.KEY_POI_INFO_END, mEndPoiInfoArray);
		startActivity(it);
	}

	@Override
	public void onGetPoiDetailResult(PoiDetailResult result) {

	}

	@Override
	public void onGetPoiResult(PoiResult result) {
		for (SearchResult.ERRORNO c : SearchResult.ERRORNO.values())
			Log.e("searchout", c.name() + "+" + c.toString());

		if ((mProgressDialog != null && mProgressDialog.isShowing()) || mIsFlipOver) {
			if (mProgressDialog != null) {
				mProgressDialog.dismiss();
				mProgressDialog = null;
			}
			if (result.error == SearchResult.ERRORNO.NO_ERROR) {
				mPageNums = result.getTotalPageNum();
				if (mPageNums <= 1) {
					mPoiListViewParent.setVisibility(View.GONE);
				}
				mPoiInfoList = result.getAllPoi();
				mAdapter.setData(mPoiInfoList);
				mAdapter.notifyDataSetChanged();
				if (!mIsDialogShow) {
					mBuilder.show();
					mIsDialogShow = true;
				}
			} else {
				Toast.makeText(SearchRoutePlan.this, MapUtil.getPoiSearchName(result.error), Toast.LENGTH_SHORT).show();
			}
			mIsFlipOver = false;
		}
	}
}
