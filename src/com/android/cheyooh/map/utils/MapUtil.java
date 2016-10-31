package com.android.cheyooh.map.utils;

import java.text.NumberFormat;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

import com.android.cheyooh.map.R;
import com.baidu.location.BDLocation;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult.ERRORNO;
import com.baidu.mapapi.utils.DistanceUtil;

/**
 * 类说明：
 * 
 * @author Terry.Lu
 * @date 2013-5-8
 * @version 1.0
 */
public class MapUtil {

	// 百度poi搜索错误信息
	private String AMBIGUOUS_KEYWORD;// 检索词有岐义
	private String AMBIGUOUS_ROURE_ADDR;// 检索地址有岐义
	private String KEY_ERROR;// key有误
	private String NETWORK_ERROR;// 网络错误
	private String NETWORK_TIME_OUT;// 网络超时
	private String NO_ERROR;// 检索结果正常返回
	private String NOT_SUPPORT_BUS;// 该城市不支持公交搜索
	private String NOT_SUPPORT_BUS_2CITY;// 不支持跨城市公交
	private String PERMISSION_UNFINISHED;// 授权未完成
	private String RESULT_NOT_FOUND;// 没有找到检索结果
	private String ST_EN_TOO_NEAR;// 起终点太近

	private MapUtil() {
	}

	public static void saveLocation(Context context, BDLocation location) {
		SharedPreferences sp = context.getSharedPreferences("geopoint", Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putString("latitude", String.valueOf(location.getLatitude()));
		editor.putString("longtitude", String.valueOf(location.getLongitude()));
		editor.commit();
	}

	public static LatLng getLocation(Context context) {
		SharedPreferences sps = context.getSharedPreferences("geopoint", Context.MODE_PRIVATE);
		double latitude = Double.valueOf(sps.getString("latitude", "0"));
		double longtitude = Double.valueOf(sps.getString("longtitude", "0"));

		if (latitude == 0 && longtitude == 0) {
			return null;
		} else {
			return new LatLng(latitude, longtitude);
		}
	}

	public static void setCity(Context context, String city) {
		SharedPreferences sp = context.getSharedPreferences("geopoint", Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putString("city", city);
		editor.commit();
	}

	public static String getCity(Context context) {
		SharedPreferences sps = context.getSharedPreferences("geopoint", Context.MODE_PRIVATE);
		String city = sps.getString("city", context.getString(R.string.default_city));

		return city;
	}

	public static double getDistance(LatLng pt1, LatLng pt2) {
		return DistanceUtil.getDistance(pt1, pt2);
	}

	public static String getDisanceString(double distance) {
		NumberFormat formater = NumberFormat.getInstance();
		formater.setMaximumFractionDigits(2);

		if (distance > 1000) {
			return formater.format(distance / 1000) + "KM";
		} else {
			return formater.format(distance) + "M";
		}
	}

	public static String getDisanceStr(double distance) {
		NumberFormat formater = NumberFormat.getInstance();
		formater.setMaximumFractionDigits(2);

		if (distance > 1000) {
			return formater.format(distance / 1000) + "公里";
		} else {
			return formater.format(distance) + "米";
		}
	}

	public static String getDisanceString(LatLng pt1, LatLng pt2) {
		double distance = getDistance(pt1, pt2);
		NumberFormat formater = NumberFormat.getInstance();
		formater.setMaximumFractionDigits(2);

		if (distance > 1000) {
			return formater.format(distance / 1000) + "KM";
		} else {
			return formater.format(distance) + "M";
		}
	}

	public static Bitmap getMarkerIcon(Context context, int position) {
		Bitmap icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_mark_bg);
		Bitmap newPicture = Bitmap.createBitmap(icon.getWidth(), icon.getHeight(), icon.getConfig());
		Canvas canvas = new Canvas(newPicture);
		canvas.drawBitmap(icon, 0, 0, null);
		Paint textPaint = new Paint();
		textPaint.setColor(Color.WHITE);
		textPaint.setTypeface(Typeface.create("宋体", Typeface.BOLD));
		textPaint.setTextSize(18);
		// int changeX = 5;
		// if (position >= 100) {
		// changeX = 15;
		// } else if (position >= 10) {
		// changeX = 10;
		// }
		char text = (char) (position + 64);

		canvas.drawText(String.valueOf(text), icon.getWidth() / 2 - 5, icon.getHeight() / 2, textPaint);
		canvas.save(Canvas.ALL_SAVE_FLAG);
		icon.recycle();
		return newPicture;
	}

	public static String getPoiSearchName(ERRORNO no) {
		String errorStr = "";
		switch (no) {
		case AMBIGUOUS_KEYWORD:
			errorStr = "检索词有岐义";
			break;
		case AMBIGUOUS_ROURE_ADDR:
			errorStr = "检索地址有岐义";
			break;
		case KEY_ERROR:
			errorStr = "key有误";
			break;
		case NETWORK_ERROR:
			errorStr = "网络错误";
			break;
		case NETWORK_TIME_OUT:
			errorStr = "网络超时";
			break;
		case NO_ERROR:
			errorStr = "检索结果正常返回";
			break;
		case NOT_SUPPORT_BUS:
			errorStr = "该城市不支持公交搜索";
			break;
		case NOT_SUPPORT_BUS_2CITY:
			errorStr = "不支持跨城市公交";
			break;
		case PERMISSION_UNFINISHED:
			errorStr = "授权未完成";
			break;
		case RESULT_NOT_FOUND:
			errorStr = "没有找到检索结果";
			break;
		case ST_EN_TOO_NEAR:
			errorStr = "起终点太近";
			break;
		}
		return errorStr;
	}
}
