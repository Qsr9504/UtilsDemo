package com.qsr.utilsdemo.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**************************************
 * FileName : com.qsr.utilsdemo.utils
 * Author : qsr
 * Time : 2017/1/6 18:24
 * Description : 当前应用信息工具类
 **************************************/
public class AppUtil {
	private AppUtil()
	{
        /* cannot be instantiated */
		throw new UnsupportedOperationException("cannot be instantiated");
	}

	/**
	 * @param context
	 *
	 * @return 获取应用程序名称 String
	 */
	public static String getAppName(Context context)
	{
		try
		{
			PackageManager packageManager = context.getPackageManager();
			PackageInfo packageInfo = packageManager.getPackageInfo(
					context.getPackageName(), 0);
			int labelRes = packageInfo.applicationInfo.labelRes;
			return context.getResources().getString(labelRes);
		} catch (PackageManager.NameNotFoundException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @param context
	 *
	 * @return 获取应用程序 版本名称 String
	 */
	public static String getVersionName(Context context)
	{
		try
		{
			PackageManager packageManager = context.getPackageManager();
			PackageInfo packageInfo = packageManager.getPackageInfo(
					context.getPackageName(), 0);
			return packageInfo.versionName;

		} catch (PackageManager.NameNotFoundException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @param context
	 *
	 * @return 获取当前应用 程序版本号 int
	 */
	public static int getVersionCode(Context context)
	{
		try
		{
			PackageManager packageManager = context.getPackageManager();
			PackageInfo packageInfo = packageManager.getPackageInfo(
					context.getPackageName(), 0);
			return packageInfo.versionCode;

		} catch (PackageManager.NameNotFoundException e)
		{
			e.printStackTrace();
		}
		return 0;
	}
}
