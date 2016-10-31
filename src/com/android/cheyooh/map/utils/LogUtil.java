package com.android.cheyooh.map.utils;

import android.util.Log;


/** 
 * 类说明：   
 * @author  xinhui.cheng
 * @date    2012-6-4
 * @version 1.0
 */
public class LogUtil {
	private static final boolean DEBUG = true; 
	
	public static void i(String tag , String msg){
		if (DEBUG) {
			Log.i(tag, msg);
		}
	}
	public static void e(String tag , String msg){
		if (DEBUG) {
			Log.e(tag, msg);
		}
	}
	public static void d(String tag , String msg){
		if (DEBUG) {
			Log.d(tag, msg);
		}
	}
	public static void w(String tag , String msg){
		if (DEBUG) {
			Log.w(tag, msg);
		}
	}
	public static void v(String tag , String msg){
		if (DEBUG) {
			Log.v(tag, msg);
		}
	}
}
