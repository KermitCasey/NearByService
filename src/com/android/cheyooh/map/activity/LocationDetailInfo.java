package com.android.cheyooh.map.activity;

import com.android.cheyooh.map.R;
import com.android.cheyooh.map.lbs.Constant;
import com.android.cheyooh.map.utils.MapUtil;
import com.android.cheyooh.map.view.OptionDialog;
import com.baidu.mapapi.model.LatLng;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
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
 * @modify-date 2013-5-9
 * @modify-version 2.0
 */
public class LocationDetailInfo extends BaseActivity implements OnClickListener {
	
	private String[] mEndPoiInfo;
	private double[] mEndPoint;
	private double mDistance;
	private String mPhoneNum;
	private double[] mStartPoint;
	private Button mBtnPhoneNumber;
	private String[] mPhoneNumbers;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.location_detail_info_layout);
		initData();
		initViews();
	}
	
	@Override
	public void onResume() {
	    super.onResume();
	}
	
	@Override
	public void onPause() {
	    super.onPause();
	}

	private void initData() {
		Intent it = getIntent();
		mEndPoiInfo = it.getStringArrayExtra(Constant.KEY_POI_INFO_END);
		mEndPoint = it.getDoubleArrayExtra(Constant.KEY_END_POINT);
		mStartPoint = it.getDoubleArrayExtra(Constant.KEY_START_POINT);
		LatLng start = MapUtil.getLocation(getApplicationContext());
		mDistance = MapUtil.getDistance(start, new LatLng(mEndPoint[0], mEndPoint[1]));
	}
	
	private void initViews() {
		String name = mEndPoiInfo[0];
		String addr = mEndPoiInfo[1];
		mPhoneNum = mEndPoiInfo[2];
		
		Button btnBack = (Button) findViewById(R.id.title_left_button);
		btnBack.setOnClickListener(this);
		btnBack.setText(R.string.back);
		findViewById(R.id.title_right_button).setVisibility(View.GONE);
		TextView tvTitle = (TextView) findViewById(R.id.title_text);
		tvTitle.setText(R.string.detail);
		
		TextView tvName = (TextView) findViewById(R.id.location_detail_info_layout_detail_name);

		if (name != null) {
			tvName.setText(name);
		}

		TextView tvAddr = (TextView) findViewById(R.id.location_detail_info_layout_detail_address);

		if (addr != null) {
			tvAddr.setText(addr);
		}

		TextView tvDistance = (TextView) findViewById(R.id.location_detail_info_layout_distance);
		tvDistance.setText(getResources().getString(R.string.distance) 
				+ MapUtil.getDisanceString(mDistance));

		mBtnPhoneNumber = (Button) findViewById(R.id.location_detail_info_layout_phonum);
		
		if (mPhoneNum != null && !mPhoneNum.equals("")) {
			String phones = mPhoneNum.replace("(", "").replace(")", "");
			mBtnPhoneNumber.setVisibility(View.VISIBLE);
			mBtnPhoneNumber.setText(phones);
			mBtnPhoneNumber.setOnClickListener(this);
			mPhoneNumbers = phones.split(",");
		}

		findViewById(R.id.location_detail_info_layout_from).setOnClickListener(
				this);
		findViewById(R.id.location_detail_info_layout_to).setOnClickListener(
				this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_left_button:
			finish();
			break;
		case R.id.location_detail_info_layout_phonum:
			if (mPhoneNumbers != null && mPhoneNumbers.length > 1) {
				popupPhoneNumbersOption();
			} else {
				call();
			}
			break;
		case R.id.location_detail_info_layout_from: 
			{
				Intent it = new Intent(LocationDetailInfo.this,
						SearchRoutePlan.class);
				it.putExtra(Constant.KEY_POI_INFO_START, mEndPoiInfo);
				it.putExtra(Constant.KEY_START_POINT, mEndPoint);
				startActivity(it);
			}

			break;
		case R.id.location_detail_info_layout_to: 
			{
				Intent it = new Intent(LocationDetailInfo.this,
						SearchRoutePlan.class);
				it.putExtra(Constant.KEY_START_POINT, mStartPoint);
				it.putExtra(Constant.KEY_POI_INFO_END, mEndPoiInfo);
				it.putExtra(Constant.KEY_END_POINT, mEndPoint);
				startActivity(it);
			}
			break;
		default:
			break;
		}
	}

	private void call() {
		if (mPhoneNum != null) {
			try {
				Intent intent = new Intent(Intent.ACTION_CALL,
						Uri.parse("tel:" + mPhoneNum));
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
			} catch (Exception e) {
				Toast.makeText(LocationDetailInfo.this, R.string.failed_to_call, Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	private void popupPhoneNumbersOption() {
		if (mPhoneNumbers != null) {
			final OptionDialog dlg = new OptionDialog(this,"", mPhoneNumbers);
			dlg.setOptionsItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					dlg.dismiss();
					mPhoneNum = mPhoneNumbers[position];
					call();
				}
			});
			dlg.show();
		}
	}
	
}
