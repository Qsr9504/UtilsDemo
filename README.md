# UtilsDemo
项目常用Utils工具类,主要包括：
> ###[1.Activity栈统一管理类](https://github.com/Qsr9504/UtilsDemo#activity栈统一管理类) 
主要功能有：
* activity跳转
* avticity销毁
* 程序退出销毁所有Activity

> ###[2.全局异常捕捉管理器](https://github.com/Qsr9504/UtilsDemo#全局异常捕捉管理器)
主要功能有：
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
```java
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
```
