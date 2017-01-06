package com.qsr.utilsdemo.app;

import android.app.Application;
import android.content.Context;
import android.os.Handler;

import com.qsr.utilsdemo.utils.ActivityManager;

/**************************************
 * FileName : com.qsr.utilsdemo
 * Author : qsr
 * Time : 2017/1/6 13:37
 * Description : 全局
 **************************************/
public class App extends Application {
	public static Context mContext = null;
	public static Handler handler = null;
	public static Thread mainThread = null;
	public static int mainThreadId = 0;//主线程id
	public static ActivityManager activityManager = null;

	@Override
	public void onCreate() {
		super.onCreate();
		mContext = getApplicationContext();
		handler = new Handler();
		mainThread = Thread.currentThread();
		mainThreadId = android.os.Process.myPid();
		initUtils();//各种工具类的初始化
	}

	private void initUtils() {
		//activity管理栈初始化
		activityManager = ActivityManager.getInstance();
		//
	}
}
