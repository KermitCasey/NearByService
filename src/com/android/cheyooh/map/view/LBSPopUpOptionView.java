package com.android.cheyooh.map.view;

import com.android.cheyooh.map.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;

/**
 * 类说明：
 * 
 * @author xinhui.cheng
 * @date 2013-5-8
 * @version 1.0
 */
public class LBSPopUpOptionView extends LinearLayout implements OnClickListener {

	public static final int TYPE_GAS_STATION = 0;
	public static final int TYPE_DEPOT = 1;
	public static final int TYPE_CAR_WASH = 2;
	public static final int TYPE_BANK = 3;
	public static final int TYPE_AUTO_4S = 4;
	public static final int TYPE_AUTO_MAINTENACE = 5;
	public static final int TYPE_VAO = 6;
	public static final int TYPE_AUTO_BEAUTY = 7;
	public static final int TYPE_AUTO_PARTS = 8;

	private LinearLayout mOptionLayout;
	private LBSPopUpOptionClickLitener mListener;

	public interface LBSPopUpOptionClickLitener {
		public void onOptionClick(View v, int type);
	}

	public LBSPopUpOptionView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public LBSPopUpOptionView(Context context) {
		super(context);
		init();
	}

	private void init() {
		setVisibility(View.GONE);
		initViews();
	}

	private void initViews() {
		LinearLayout mainLayout = (LinearLayout) LayoutInflater.from(getContext()).inflate(
				R.layout.lbs_pop_up_option_layout, null);
		mOptionLayout = (LinearLayout) mainLayout
				.findViewById(R.id.lbs_pop_up_option_btn_layout);
		Button gasStationBtn = (Button) mainLayout
				.findViewById(R.id.lbs_btn_search_gas_station);
		Button depotBtn = (Button) mainLayout
				.findViewById(R.id.lbs_btn_search_depot);
		Button carwashBtn = (Button) mainLayout
				.findViewById(R.id.lbs_btn_search_car_wash);
		Button bankBtn = (Button) mainLayout
				.findViewById(R.id.lbs_btn_search_bank);
		Button auto4sBtn = (Button) mainLayout
				.findViewById(R.id.lbs_btn_search_auto_4s_shop);
		Button autoMaintenaceBtn = (Button) mainLayout
				.findViewById(R.id.lbs_btn_search_auto_maintenace);
		Button vaoBtn = (Button) mainLayout
				.findViewById(R.id.lbs_btn_search_transportation_department);
		Button autoBeautyBtn = (Button) mainLayout
				.findViewById(R.id.lbs_btn_search_auto_beauty);
		Button autoPartsBtn = (Button) mainLayout
				.findViewById(R.id.lbs_btn_search_auto_parts);

		mainLayout.setOnClickListener(this);
		gasStationBtn.setOnClickListener(this);
		depotBtn.setOnClickListener(this);
		carwashBtn.setOnClickListener(this);
		bankBtn.setOnClickListener(this);
		auto4sBtn.setOnClickListener(this);
		autoMaintenaceBtn.setOnClickListener(this);
		vaoBtn.setOnClickListener(this);
		autoBeautyBtn.setOnClickListener(this);
		autoPartsBtn.setOnClickListener(this);

		addView(mainLayout , new LayoutParams(LayoutParams.FILL_PARENT
				, LayoutParams.FILL_PARENT));
	}

	public void setLBSPopUpOptionClickListener(LBSPopUpOptionClickLitener l) {
		mListener = l;
	}

	public void showOptions() {
		showOptions(true);
	}

	private void showOptions(final boolean isShow) {
		Animation anim = null;

		if (isShow) {
			setVisibility(View.VISIBLE);
			mOptionLayout.setVisibility(View.VISIBLE);
			anim = AnimationUtils.loadAnimation(getContext(),
					R.anim.map_view_pop_layout_scale_in);
		} else {
			anim = AnimationUtils.loadAnimation(getContext(),
					R.anim.map_view_pop_layout_scale_out);
		}

		anim.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				if (!isShow) {
					mOptionLayout.setVisibility(View.GONE);
					setVisibility(View.GONE);
				}
			}
		});

		mOptionLayout.startAnimation(anim);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.lbs_btn_search_auto_4s_shop:
			if (mListener != null) {
				mListener.onOptionClick(v, TYPE_AUTO_4S);
			}
			break;
		case R.id.lbs_btn_search_auto_beauty:
			if (mListener != null) {
				mListener.onOptionClick(v, TYPE_AUTO_BEAUTY);
			}
			break;
		case R.id.lbs_btn_search_auto_maintenace:
			if (mListener != null) {
				mListener.onOptionClick(v, TYPE_AUTO_MAINTENACE);
			}
			break;
		case R.id.lbs_btn_search_auto_parts:
			if (mListener != null) {
				mListener.onOptionClick(v, TYPE_AUTO_PARTS);
			}
			break;
		case R.id.lbs_btn_search_bank:
			if (mListener != null) {
				mListener.onOptionClick(v, TYPE_BANK);
			}
			break;
		case R.id.lbs_btn_search_car_wash:
			if (mListener != null) {
				mListener.onOptionClick(v, TYPE_CAR_WASH);
			}
			break;
		case R.id.lbs_btn_search_depot:
			if (mListener != null) {
				mListener.onOptionClick(v, TYPE_DEPOT);
			}
			break;
		case R.id.lbs_btn_search_transportation_department:
			if (mListener != null) {
				mListener.onOptionClick(v, TYPE_VAO);
			}
			break;
		case R.id.lbs_btn_search_gas_station:
			if (mListener != null) {
				mListener.onOptionClick(v, TYPE_GAS_STATION);
			}
			break;
		case R.id.lbs_pop_up_option_layout:
			break;

		}
		showOptions(false);

	}

}
