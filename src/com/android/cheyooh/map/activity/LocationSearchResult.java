package com.android.cheyooh.map.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.cheyooh.map.R;
import com.android.cheyooh.map.adapter.LocationSearchResultAdapter;
import com.android.cheyooh.map.lbs.Constant;
import com.android.cheyooh.map.utils.LogUtil;
import com.android.cheyooh.map.utils.MapUtil;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;

/**
 * 类说明：
 *
 * @author Terry.Lu
 * @version 1.0
 * @date 2013-5-9
 */
public class LocationSearchResult extends BaseActivity implements OnClickListener, OnItemClickListener, OnGetPoiSearchResultListener {
    private static final String TAG = LocationSearchResult.class.getSimpleName();
    private LocationSearchResultAdapter mAdatper;
    private ListView mListView;
    private PoiSearch mMapSearch;
    private ProgressDialog mProgressDlg;
    private int mTotalPage;
    private int mCurrPageIndex = 0;
    private String mKeyword;
    LatLng mCurrentLatLng;
    private boolean mPoiListChange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.location_search_result_layout);
        initViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void initViews() {
        findViewById(R.id.location_search_result_layout_btn_next_page).setOnClickListener(this);
        findViewById(R.id.location_search_result_layout_btn_previous_page).setOnClickListener(this);
        findViewById(R.id.location_search_result_layout_back).setOnClickListener(this);
        mListView = (ListView) findViewById(R.id.location_search_result_layout_list_view);
        mMapSearch = PoiSearch.newInstance();

        mAdatper = new LocationSearchResultAdapter(this);
        mListView.setAdapter(mAdatper);
        mListView.setOnItemClickListener(this);
        mMapSearch.setOnGetPoiSearchResultListener(this);
        mCurrentLatLng = MapUtil.getLocation(this);

        Intent it = getIntent();

        if (it != null) {
            mKeyword = it.getStringExtra(Constant.SEARCH_KEY);
            mTotalPage = Constant.mPoiResult.getTotalPageNum();
            mAdatper.setDataList(Constant.mPoiResult.getAllPoi());
        }

        TextView tv = (TextView) findViewById(R.id.location_search_result_layout_title);
        tv.setText(mKeyword);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.location_search_result_layout_btn_next_page:
                if (mCurrPageIndex < mTotalPage - 1) {
                    mCurrPageIndex++;
                    GotoPageSearch(mCurrPageIndex);
                } else {
                    Toast.makeText(
                            getApplicationContext(),
                            R.string.no_next_page,
                            Toast.LENGTH_SHORT)
                            .show();
                }

                break;
            case R.id.location_search_result_layout_btn_previous_page:
                if (mCurrPageIndex > 0) {
                    mCurrPageIndex--;
                    GotoPageSearch(mCurrPageIndex);
                } else {
                    Toast.makeText(
                            getApplicationContext(),
                            R.string.no_prev_page,
                            Toast.LENGTH_SHORT)
                            .show();
                }

                break;
            case R.id.location_search_result_layout_back:
                goBack();
                break;
            default:
                break;
        }
    }

    private void goBack() {
        if (mPoiListChange) {
            Intent it = new Intent(LocationSearchResult.this, LBSActivity.class);
            it.putExtra(Constant.KEY_NEW_INTENT_TYPE, Constant.NEW_INTENT_TYPE_REFRESH_POI_LIST);
            startActivity(it);
        } else {
            finish();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View arg1, int position, long arg3) {
        LogUtil.d(TAG, "onItemClick");
        Intent it = new Intent(this, LBSActivity.class);
        it.putExtra(Constant.KEY_NEW_INTENT_TYPE, Constant.NEW_INTENT_TYPE_GOTO_GEOPOINT);
        PoiInfo poiInfo = (PoiInfo) adapterView.getItemAtPosition(position);

        if (poiInfo != null) {
            Bundle bundle = new Bundle();
            bundle.putString("name", poiInfo.name);
            bundle.putString("address", poiInfo.address);
            bundle.putString("phoneNum", poiInfo.phoneNum);
            double[] point = {poiInfo.location.latitude, poiInfo.location.longitude};
            it.putExtra(Constant.KEY_START_POINT, point);
            it.putExtra(Constant.KEY_SELECTED_INDEX, position);
            it.putExtras(bundle);
        }

        startActivity(it);
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

    private void GotoPageSearch(int currentPage) {
        showProgressDialog(getString(R.string.searching) + mKeyword);
        mMapSearch.searchNearby(new PoiNearbySearchOption().keyword(mKeyword).radius(10000).location(mCurrentLatLng).pageNum(currentPage));
    }

    @Override
    public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

    }

    @Override
    public void onGetPoiResult(PoiResult result) {

        LogUtil.d(TAG, "onGetPoiResult");
        closeProgressDialog();

        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            mTotalPage = result.getTotalPageNum();
            mPoiListChange = true;
            mAdatper.setDataList(result.getAllPoi());
        } else {
            Toast.makeText(getApplicationContext(), R.string.search_failed, Toast.LENGTH_LONG).show();
        }
    }
}
