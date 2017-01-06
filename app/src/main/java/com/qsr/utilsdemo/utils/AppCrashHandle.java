package com.qsr.utilsdemo.utils;

import android.content.Context;
import android.os.Build;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.qsr.utilsdemo.app.App;

/**************************************
 * FileName : com.qsr.utilsdemo.utils
 * Author : qsr
 * Time : 2017/1/6 14:10
 * Description :全局异常捕获器
 **************************************/
public class AppCrashHandle implements Thread.UncaughtExceptionHandler{
	private static AppCrashHandle crashHandle = null;
	private Context mContext;
	private Thread.UncaughtExceptionHandler exceptionHandler;
	//拦截系统未能捕捉的崩溃信息
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		if(isHandle(ex))
		{
			//自己处理的异常
			handleException(thread, ex);
		}else {
			//自己不想处理的异常交回给系统处理
			exceptionHandler.uncaughtException(thread,ex);
		}
	}
	//私有构造函数
	private AppCrashHandle(){}
	//单例模式
	public static synchronized AppCrashHandle getInstance(){
		if(crashHandle == null){
			synchronized (AppCrashHandle.class) {
				crashHandle = new AppCrashHandle();
			}
		}
		return crashHandle;
	}
	//初始化
	public void init(Context context){
		//将cashHandler作为系统的默认异常捕获处理器
		this.mContext = context;
		exceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(this);
	}
	//自定义处理错误信息
	private void handleException(Thread thread, Throwable ex) {
		new Thread() {
			@Override
			public void run() {
				//Android系统当中，默认情况下，线程是没有开启looper消息处理的，但是主线程除外
				Looper.prepare();
				Toast.makeText(mContext, "抱歉，系统出现未知异常，即将退出....", Toast.LENGTH_SHORT).show();
				Looper.loop();
			}
		}.start();
		collectionException(ex);
		try {
			thread.sleep(2000);
			ActivityManager.getInstance().removeAll();
			android.os.Process.killProcess(android.os.Process.myPid());
			//关闭程序，释放所有内存
			System.exit(0);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	//判断是否需要自己处理（检查错误信息是不是空）
	public boolean isHandle(Throwable ex) {
		if (ex == null) {
			return false;
		} else {
			return true;
		}
	}
	//自定义收集崩溃信息
	private void collectionException(Throwable ex) {
		final String deviceInfo = Build.DEVICE + Build.VERSION.SDK_INT + Build.MODEL + Build.PRODUCT;
		final String errorInfo = ex.getMessage().toString();
		new Thread() {
			@Override
			public void run() {
				//可以收集bug信息上传至服务器，以便后期完善
				Log.e("程序已经崩溃", "设备信息:" + deviceInfo + "\n" + "崩溃信息:" + errorInfo);
			}
		}.start();
	}
}
