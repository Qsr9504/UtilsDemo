package com.qsr.utilsdemo.utils;

import android.content.Context;
import android.os.Handler;
import android.view.View;

import com.qsr.utilsdemo.app.App;

/**************************************
 * FileName : com.qsr.utilsdemo.utils
 * Author : qsr
 * Time : 2017/1/6 18:40
 * Description :界面相关工具类
 **************************************/
public class UIUtil {
	//获取全局上下文对象
	public static Context getContext() {
		return App.mContext;
	}

	//获取全局handle对象
	public static Handler getHandle() {
		return App.handler;
	}
	//获取颜色
	public static int getColorId(int color) {
		return getContext().getResources().getColor(color);
	}

	//获取string.xml中字符串
	public static String getStringId(int stringId) {
		return getContext().getResources().getString(stringId);
	}

	//xml转化为View对象-fragment中布局转化
	public static View getXmlView(int layoutId) {
		return View.inflate(getContext(), layoutId, null);
	}

	//dp转换px
	public static int dpToPx(int dp){
		//先求出转换的比例
		float density = getContext().getResources().getDisplayMetrics().density;
		//再进行转换 （进一法，提高精确度）
		return (int) (dp * density + 0.5);
	}

	//px转换dp
	public static int pxToDp(int px){
		//获取转换比例
		float density = getContext().getResources().getDisplayMetrics().density;
		//进行转换
		return (int) (px/density + 0.5);
	}

	//从Value-stringArray中获取并返回数组\
	public static String[] getStringArr(int arrId){
		return getContext().getResources().getStringArray(arrId);
	}
	public static Handler getHandler() {
		return App.handler;
	}
	/**
	 * 保证runnable对象的run方法是运行在主线程当中
	 *
	 * @param runnable
	 */
	public static void runOnUIThread(Runnable runnable) {
		if (isInMainThread()) {
			runnable.run();
		} else {
			getHandler().post(runnable);
		}
	}

	private static boolean isInMainThread() {
		//当前线程的id
		int tid = android.os.Process.myTid();
		if (tid == App.mainThreadId) {
			return true;
		}
		return false;
	}
}
