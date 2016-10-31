package com.android.cheyooh.map.activity;

import java.util.List;

import com.android.cheyooh.map.R;
import com.android.cheyooh.map.adapter.RoutePlanAdapter;
import com.android.cheyooh.map.adapter.RoutePlanAdapter.TYPE;
import com.android.cheyooh.map.lbs.Constant;
import com.android.cheyooh.map.utils.LogUtil;
import com.android.cheyooh.map.utils.MapUtil;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.DrivingRouteOverlay;
import com.baidu.mapapi.overlayutil.TransitRouteOverlay;
import com.baidu.mapapi.overlayutil.WalkingRouteOverlay;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.core.RouteNode;
import com.baidu.mapapi.search.core.RouteStep;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.core.SearchResult.ERRORNO;
import com.baidu.mapapi.search.route.DrivingRouteLine.DrivingStep;
import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteLine.TransitStep;
import com.baidu.mapapi.search.route.TransitRoutePlanOption;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteLine.WalkingStep;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 类说明：
 *
 * @author Kermit
 * @version 1.0
 * @date 2015-7-27
 */
public class RoutePlan extends BaseActivity implements OnItemClickListener, OnClickListener, OnGetRoutePlanResultListener {
    private static final String TAG = RoutePlan.class.getSimpleName();
    private String mPlanNodeStartName;
    private String mPlanNodeEndName;
    private RoutePlanAdapter mAdapter;
    private List<RouteStep> mList;
    private int mSearchRoutePlanType;
    private RoutePlanSearch mMapSearch;
    private LatLng mStartLatLng;
    private LatLng mEndLatLng;
    private PlanNode mStartPlanNode;
    private PlanNode mEndPlanNode;
    private boolean mbListDataChanged;
    private TextView tvRoutename;
    private TextView tvDistance;
    private TextView tvRoutePlanType;

    TYPE style = TYPE.DRIVING;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.route_plan_layout);
        initViews();
        initTitle();
        initSearch();
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void initSearch() {
        mMapSearch = RoutePlanSearch.newInstance();
        mMapSearch.setOnGetRoutePlanResultListener(this);
    }

    private void initData() {
        Intent it = getIntent();

        mPlanNodeStartName = it.getStringExtra(Constant.KEY_PLAN_NODE_START_NAME);
        mPlanNodeEndName = it.getStringExtra(Constant.KEY_PLAN_NODE_END_NAME);
        mSearchRoutePlanType = it.getIntExtra(Constant.KEY_SEARCH_ROUTE_PLAN_TYPE, Constant.SEARCH_ROUTE_PLAN_TYPE_DRIVING);
        double[] startPoint = it.getDoubleArrayExtra(Constant.KEY_START_POINT);
        double[] endPoint = it.getDoubleArrayExtra(Constant.KEY_END_POINT);
        mStartLatLng = new LatLng(startPoint[0], startPoint[1]);
        mEndLatLng = new LatLng(endPoint[0], endPoint[1]);
        mStartPlanNode = PlanNode.withLocation(mStartLatLng);
        mEndPlanNode = PlanNode.withLocation(mEndLatLng);
        /** 显示方案形式 **/
        String plan = "";
        switch (mSearchRoutePlanType) {
            case Constant.SEARCH_ROUTE_PLAN_TYPE_DRIVING:
                plan = getString(R.string.driving_plan);
                style = TYPE.DRIVING;
                break;

            case Constant.SEARCH_ROUTE_PLAN_TYPE_TRANSIT:
                plan = getString(R.string.transit_plan);
                style = TYPE.TRANSIT;
                break;
            case Constant.SEARCH_ROUTE_PLAN_TYPE_WALKING:
                plan = getString(R.string.walking_plan);
                style = TYPE.WALK;
                break;
            default:
                break;
        }

        /** 显示底部选项 **/
        View bottomPanel = findViewById(R.id.route_plan_layout_bottom_panel);
        if (mSearchRoutePlanType == Constant.SEARCH_ROUTE_PLAN_TYPE_DRIVING) {
            bottomPanel.setVisibility(View.VISIBLE);
            findViewById(R.id.route_plan_layout_btn_less_express).setOnClickListener(this);
            findViewById(R.id.route_plan_layout_btn_shortest).setOnClickListener(this);
            findViewById(R.id.route_plan_layout_btn_timeless).setOnClickListener(this);
        } else {
            bottomPanel.setVisibility(View.GONE);
        }

        /** 设置数据 **/
        tvRoutePlanType.setText(plan);
        tvRoutename.setText(mPlanNodeStartName + "->" + mPlanNodeEndName);
        if (null == Constant.mRouteline) {
            tvDistance.setText(getString(R.string.distance) + MapUtil.getDisanceString(mStartLatLng, mEndLatLng));
            showProgressDialog(getResources().getString(R.string.searching));
            switch (style) {
                case DRIVING:
                    mMapSearch.drivingSearch(new DrivingRoutePlanOption().from(mStartPlanNode).to(mEndPlanNode));
                    break;
                case TRANSIT:
                    mMapSearch.transitSearch(new TransitRoutePlanOption().from(mStartPlanNode).to(mEndPlanNode).city(MapUtil.getCity(this)));
                    break;
                case WALK:
                    mMapSearch.walkingSearch(new WalkingRoutePlanOption().from(mStartPlanNode).from(mEndPlanNode));
                    break;
            }
        } else {
            tvDistance.setText(getString(R.string.distance) + MapUtil.getDisanceString(Constant.mRouteline.getDistance()));
            mAdapter.setRouteLine(Constant.mRouteline, style);
        }
    }

    private void initTitle() {
        Button btnBack = (Button) findViewById(R.id.title_left_button);
        btnBack.setText(R.string.back);
        btnBack.setOnClickListener(this);
        findViewById(R.id.title_right_button).setVisibility(View.GONE);
        TextView tvTitle = (TextView) findViewById(R.id.title_text);
        tvTitle.setText(R.string.route_plan);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_left_button:
                goBack();
                break;
            case R.id.route_plan_layout_btn_less_express:
                showProgressDialog(getResources().getString(R.string.searching));
                mMapSearch.drivingSearch(
                        new DrivingRoutePlanOption().from(mStartPlanNode).to(mEndPlanNode).policy(DrivingRoutePlanOption.DrivingPolicy.ECAR_DIS_FIRST));
                break;
            case R.id.route_plan_layout_btn_shortest:
                showProgressDialog(getResources().getString(R.string.searching));
                mMapSearch.drivingSearch(
                        new DrivingRoutePlanOption().from(mStartPlanNode).to(mEndPlanNode).policy(DrivingRoutePlanOption.DrivingPolicy.ECAR_TIME_FIRST));
                break;
            case R.id.route_plan_layout_btn_timeless:
                showProgressDialog(getResources().getString(R.string.searching));
                mMapSearch.drivingSearch(
                        new DrivingRoutePlanOption().from(mStartPlanNode).to(mEndPlanNode).policy(DrivingRoutePlanOption.DrivingPolicy.ECAR_FEE_FIRST));
                break;
            default:
                break;
        }
    }

    private void goBack() {
        if (mbListDataChanged) {
            Intent it = new Intent(RoutePlan.this, LBSActivity.class);
            switch (style) {
                case DRIVING:
                    it.putExtra(Constant.ROUTE_PLAN_TYPE, Constant.SEARCH_ROUTE_PLAN_TYPE_DRIVING);
                    break;
                case TRANSIT:
                    it.putExtra(Constant.ROUTE_PLAN_TYPE, Constant.SEARCH_ROUTE_PLAN_TYPE_TRANSIT);
                    break;
                case WALK:
                    it.putExtra(Constant.ROUTE_PLAN_TYPE, Constant.SEARCH_ROUTE_PLAN_TYPE_WALKING);
                    break;
            }
            it.putExtra(Constant.KEY_NEW_INTENT_TYPE, Constant.NEW_INTENT_TYPE_REFRESH_ROUTE_PLAN);
            startActivity(it);
        } else {
            finish();
        }
    }

    private void initViews() {
        tvRoutePlanType = (TextView) findViewById(R.id.route_plan_layout_drive_plan);
        tvRoutename = (TextView) findViewById(R.id.route_plan_layout_my_location_name);
        tvDistance = (TextView) findViewById(R.id.route_plan_layout_distance);
        ListView listView = (ListView) findViewById(R.id.route_plan_layout_list_view);
        mAdapter = new RoutePlanAdapter(this, mList);
        listView.setOnItemClickListener(this);
        listView.setAdapter(mAdapter);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterview, View view, int position, long l) {

        Intent it = new Intent(RoutePlan.this, LBSActivity.class);
        it.putExtra(Constant.KEY_NEW_INTENT_TYPE, Constant.NEW_INTENT_TYPE_REFRESH_ROUTE_PLAN);
        it.putExtra(Constant.KEY_SHOW_TIP, false);
        double[] point = null;
        String showTip = "";
        switch (style) {
            case DRIVING:
                it.putExtra(Constant.ROUTE_PLAN_TYPE, Constant.SEARCH_ROUTE_PLAN_TYPE_DRIVING);
                DrivingStep drivingStep = (DrivingStep) Constant.mRouteline.getAllStep().get(position);
                if (null != drivingStep) {
                    point = new double[]{drivingStep.getEntrance().getLocation().latitude, drivingStep.getEntrance().getLocation().longitude};
                    showTip = drivingStep.getInstructions();
                }
                break;
            case TRANSIT:
                it.putExtra(Constant.ROUTE_PLAN_TYPE, Constant.SEARCH_ROUTE_PLAN_TYPE_TRANSIT);
                TransitStep transitStep = (TransitStep) Constant.mRouteline.getAllStep().get(position);
                if (null != transitStep) {
                    point = new double[]{transitStep.getEntrance().getLocation().latitude, transitStep.getEntrance().getLocation().longitude};
                    showTip = transitStep.getInstructions();
                }
                break;
            case WALK:
                it.putExtra(Constant.ROUTE_PLAN_TYPE, Constant.SEARCH_ROUTE_PLAN_TYPE_WALKING);
                WalkingStep walkingStep = (WalkingStep) Constant.mRouteline.getAllStep().get(position);
                if (null != walkingStep) {
                    point = new double[]{walkingStep.getEntrance().getLocation().latitude, walkingStep.getEntrance().getLocation().longitude};
                    showTip = walkingStep.getInstructions();
                }
                break;
        }
        it.putExtra(Constant.KEY_START_POINT, point);
        it.putExtra(Constant.KEY_SELECTED_INDEX, position);
        it.putExtra(Constant.KEY_SELECTED_TIP, showTip);
        startActivity(it);
    }

    private ProgressDialog mProgressDlg = null;

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

    private void closeProgressDialog() {
        if (mProgressDlg != null && mProgressDlg.isShowing()) {
            mProgressDlg.dismiss();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onGetDrivingRouteResult(DrivingRouteResult result) {
        showRouteResultLine(result, TYPE.DRIVING);
    }

    @Override
    public void onGetTransitRouteResult(TransitRouteResult result) {
        // showRouteResultLine(result, TYPE.TRANSIT);
    }

    @Override
    public void onGetWalkingRouteResult(WalkingRouteResult result) {
        // showRouteResultLine(result, TYPE.WALK);
    }

    public void showRouteResultLine(SearchResult result, TYPE mType) {

        closeProgressDialog();
        LogUtil.d(TAG, "onGetDrivingRouteResult error:" + result.error.name());
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
            return;
        }
        if (result.error == SearchResult.ERRORNO.NETWORK_ERROR) {
            Toast.makeText(getApplicationContext(), R.string.network_error, Toast.LENGTH_LONG).show();
        }
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
            // result.getSuggestAddrInfo();
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            mbListDataChanged = true;
            switch (mType) {
                case DRIVING:
                    DrivingRouteResult drivingRouteResult = (DrivingRouteResult) result;
                    Constant.mRouteline = drivingRouteResult.getRouteLines().get(0);
                    tvDistance.setText(getString(R.string.distance) + MapUtil.getDisanceString(drivingRouteResult.getRouteLines().get(0).getDistance()));
                    mAdapter.setRouteLine(Constant.mRouteline, TYPE.DRIVING);
                    break;
                case TRANSIT:
                    TransitRouteResult transitRouteResult = (TransitRouteResult) result;
                    Constant.mRouteline = transitRouteResult.getRouteLines().get(0);
                    tvDistance.setText(getString(R.string.distance) + MapUtil.getDisanceString(transitRouteResult.getRouteLines().get(0).getDistance()));
                    mAdapter.setRouteLine(Constant.mRouteline, TYPE.DRIVING);
                    break;
                case WALK:
                    WalkingRouteResult walkingRouteResult = (WalkingRouteResult) result;
                    Constant.mRouteline = walkingRouteResult.getRouteLines().get(0);
                    tvDistance.setText(getString(R.string.distance) + MapUtil.getDisanceString(walkingRouteResult.getRouteLines().get(0).getDistance()));
                    mAdapter.setRouteLine(Constant.mRouteline, TYPE.DRIVING);
                    break;
            }
            mAdapter.notifyDataSetChanged();
        }else {
			Toast.makeText(this, MapUtil.getPoiSearchName(result.error), Toast.LENGTH_SHORT).show();
		}
    }
}
