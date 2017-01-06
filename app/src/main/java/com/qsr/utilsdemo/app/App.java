package com.qsr.utilsdemo.app;

import android.app.Application;
import android.content.Context;
import android.os.Handler;

import com.qsr.utilsdemo.utils.LogUtil;

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
		//全局异常捕捉器初始化
		AppCrashHandle.getInstance().init(mContext);
		//自定义log信息的开启
		LogUtil.openLog(true);
	}
	public static void appExit(){
		//删除所有的activity
		//清除sp内容
		//删除sqLite数据库内容
	}
}
