package com.qsr.utilsdemo.utils;

import android.widget.Toast;
import com.qsr.utilsdemo.app.App;
/**************************************
 * FileName : com.qsr.utilsdemo.utils
 * Author : qsr
 * Time : 2017/1/6 15:08
 * Description : Toast统一管理类
 **************************************/
public class ToastUtil {
	public static boolean isShow = true;
	//私有化构造
	private ToastUtil()
	{
		throw new UnsupportedOperationException("cannot be instantiated");
	}
	//短时间显示Toast
	public static void showShort( CharSequence message)
	{
		if (isShow)
			Toast.makeText(App.mContext, message, Toast.LENGTH_SHORT).show();
	}
	//长时间显示Toast
	public static void showLong( CharSequence message)
	{
		if (isShow)
			Toast.makeText(App.mContext, message, Toast.LENGTH_LONG).show();
	}
	//自定义显示Toast时间
	public static void show( CharSequence message, int duration)
	{
		if (isShow)
			Toast.makeText(App.mContext, message, duration).show();
	}
}
