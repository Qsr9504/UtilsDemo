package com.qsr.utilsdemo.utils;

import android.app.Activity;

import java.util.Stack;

/**************************************
 * FileName : com.qsr.utilsdemo.utils
 * Author : qsr
 * Time : 2017/1/6 13:39
 * Description : Activity的管理类
 * 主要负责，Activity的统一栈管理，Activity跳转，退出。应用程序的退出清除所有Activity
 **************************************/
public class ActivityManager {
	private static Stack<Activity> activityStack = null;
	private static ActivityManager instance = null;
	//实现单例模式
	private ActivityManager(){}
	//双重锁
	public static synchronized ActivityManager getInstance(){
		if(instance == null){
			synchronized(ActivityManager.class) {
				instance = new ActivityManager();
			}
		}
		return instance;
	}
	//添加一个activity
	public void addActivity(Activity activity){
		if(activityStack == null)
			activityStack = new Stack<Activity>();
		activityStack.add(activity);
	}
	/**
	 * 获取当前Activity（堆栈中最后一个压入的）
	 */
	public Activity currentActivity() {
		Activity activity = activityStack.lastElement();
		return activity;
	}
	/**
	 * 结束指定类名的Activity
	 */
	public void removeActivityByName(Class<?> clazz) {
		for (Activity act : activityStack) {
			if (act.getClass().equals(clazz)) {
				removeActivity(act);
			}
		}
	}
	//删除一个指定的activity
	public void removeActivity(Activity activity){
		for (int i = activityStack.size() - 1; i >= 0 ;i-- ) {
			Activity activityInStack = activityStack.get(i);
			if(activityInStack.getClass().equals(activity.getClass())){
				activityInStack.finish();
				activityStack.remove(activity);
				break;
			}
		}
	}
	//删除当前的activity
	public void removeCurrent(){
		Activity lastElement = activityStack.lastElement();
		lastElement.finish();
		activityStack.remove(lastElement);
	}
	//删除所有的activity，当程序退出时，清空
	public void removeAll(){
		for (int i = activityStack.size() - 1 ; i >= 0; i--){
			Activity a = activityStack.get(i);
			a.finish();
			activityStack.remove(a);
		}
	}
	//查看当前栈中有多少activity
	public int getSize(){
		return activityStack.size();
	}

}
