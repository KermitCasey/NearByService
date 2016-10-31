package com.android.cheyooh.map.lbs;

import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.poi.PoiResult;

/** 
 * 类说明：   
 * @author  Terry.Lu
 * @date    2013-5-8
 * @version 1.0
 */
public class Constant {
	public static final String KEY_POI_INFO_START = "poi_info_start";
	public static final String KEY_POI_INFO_END = "poi_info_end";
	public static final String KEY_END_POINT = "point_end";
	public static final String KEY_START_POINT = "point_start";
	public static final String KEY_PLAN_NODE_START_NAME = "plan_node_start_name";
	public static final String KEY_PLAN_NODE_END_NAME = "plan_node_end_name";
	public static final String KEY_SEARCH_ROUTE_PLAN_TYPE = "search_route_plan_type";
	public static final int SEARCH_ROUTE_PLAN_TYPE_TRANSIT = 1;
	public static final int SEARCH_ROUTE_PLAN_TYPE_DRIVING = 2;
	public static final int SEARCH_ROUTE_PLAN_TYPE_WALKING = 3;
	public static final int NEW_INTENT_TYPE_SEARCH_ROUTE_PLAN = 0x20001;
	public static final int NEW_INTENT_TYPE_GOTO_GEOPOINT = 0x20002;
	public static final int NEW_INTENT_TYPE_REFRESH_POI_LIST = 0x20003;
	public static final String KEY_NEW_INTENT_TYPE = "new_intent_type";
	public static final String KEY_SELECTED_INDEX = "select_index";
	public static final String KEY_SELECTED_TIP = "select_tip";

	public static final String KEY_SHOW_TIP = "show_tip";
	public static final String SEARCH_KEY = "search_key";
	public static final String ROUTE_PLAN_TYPE = "plan_type";
	public static final int NEW_INTENT_TYPE_REFRESH_ROUTE_PLAN = 0x20004;
	
	public static RouteLine mRouteline;
	public static PoiResult mPoiResult;

}
