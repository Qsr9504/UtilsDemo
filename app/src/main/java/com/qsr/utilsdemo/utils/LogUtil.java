package com.qsr.utilsdemo.utils;

import android.content.Context;
import android.util.Log;
/**************************************
 * FileName : com.qsr.utilsdemo.utils
 * Author : qsr
 * Time : 2017/1/6 14:48
 * Description : Log信息的统一管理，便于测试与统一log消除
 **************************************/
public class LogUtil {
	private static boolean isOpen = true;
	private LogUtil(){}//私有化构造
	public static void openLog(boolean b){
		isOpen = b;
	}
	public static void MyLog_i(String str){
		if(isOpen){
			Log.i("Log", str);
		}
	}
	public static void MyLog_e(String str){
		if(isOpen){
			Log.e("Log", str);
		}
	}

	public static void MyLog_e(Context context,String str){
		if(isOpen){
			Log.e(context.getClass().getSimpleName(),str);
		}
	}
	public static void MyLog_i(Context context,String str){
		if(isOpen){
			Log.i(context.getClass().getSimpleName(),str);
		}
	}
}
