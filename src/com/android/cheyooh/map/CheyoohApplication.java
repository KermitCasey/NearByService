package com.android.cheyooh.map;

import android.app.Application;
import com.baidu.mapapi.SDKInitializer;

/**
 * @author:Kermit
 * @createTime:2015年7月21日 下午4:39:07 类说明
 */
public class CheyoohApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		// 在使用 SDK 各组间之前初始化 context 信息，传入 ApplicationContext
		SDKInitializer.initialize(this);
	}

}
