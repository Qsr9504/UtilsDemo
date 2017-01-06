# UtilsDemo
项目常用Utils工具类,主要包括：
> ###[1.Activity栈统一管理类](https://github.com/Qsr9504/UtilsDemo#activity栈统一管理类) 
主要功能有：
* activity跳转
* avticity销毁
* 程序退出销毁所有Activity

> ###[2.全局异常捕捉管理器](https://github.com/Qsr9504/UtilsDemo#全局异常捕捉管理器)
</br>主要功能有：
* 全局异常的捕捉
* 自定义异常处理
* 上传至指定服务器

> ###

##Activity栈统一管理类</br>
主要功能有：
* activity跳转
* avticity销毁
* 程序退出销毁所有Activity
```java
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
```
##全局异常捕捉管理器</br>
