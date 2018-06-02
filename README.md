# UtilsDemo
## 项目常用Utils工具类,主要包括：

> ###[1. Activity栈统一管理类](https://github.com/Qsr9504/UtilsDemo#1activity栈统一管理类) 

> ###[2. 全局异常捕捉管理器](https://github.com/Qsr9504/UtilsDemo#2全局异常捕捉管理器)

> ###[3. App信息工具类](https://github.com/Qsr9504/UtilsDemo#3app信息工具类)

> ###[4. Md5加密类、软键盘工具类](https://github.com/Qsr9504/UtilsDemo#4md5加密类软键盘工具类)

> ###[5. Log统一管理类、toast统一管理类](https://github.com/Qsr9504/UtilsDemo#5log统一管理类toast统一管理类)

> ###[6. 网络状态工具类](https://github.com/Qsr9504/UtilsDemo#6网络状态工具类)

> ###[7. 屏幕信息工具类](https://github.com/Qsr9504/UtilsDemo#7屏幕信息工具类)

> ###[8. SharedPreferences工具类](https://github.com/Qsr9504/UtilsDemo#8sharedpreferences工具类)

> ###[9. 界面相关工具类](https://github.com/Qsr9504/UtilsDemo#9界面相关工具类)

## 1.Activity栈统一管理类</br>
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
## 2.全局异常捕捉管理器</br>
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
## 3.App信息工具类</br>
* 获取应用名称
* 获取版本名称 versionName
* 获取版本号 versionCode
```java
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
```
## 4.Md5加密类、软键盘工具类</br>
* Md5加密
* 软键盘的展开与关闭
```java
public class Md5Util {
    private static String addSalt = "Qiao9504_*/";
    public static String encoder(String str){
        str += str + addSalt;
        try {
            //1.指定加密算法类型
            MessageDigest digest = MessageDigest.getInstance("MD5");
            //2.将需要加密的字符串中转化成byte类型的数组，然后随机哈希过程
            byte[] bs = digest.digest(str.getBytes());
            //3.循环遍历bs，然后让其生成32位字符串，这是固定写法
            //4.拼接字符串过程
            StringBuffer stringBuffer = new StringBuffer();
            for (byte b:bs){
                //int类型的i需要转化成16机制字符
                int i = b & 0xff;
                String hexString = Integer.toHexString(i);
                //如果长度不够两位，补成0
                if(hexString.length() < 2){
                    hexString = "0" + hexString;
                }
                stringBuffer.append(hexString);
                return stringBuffer.toString();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }
}
```
```java
ublic class KeyBoardUtil {
	private KeyBoardUtil(){}
	/**
	 * 打开软键盘
	 * @param mEditText
	 * @param mContext
	 */
	public static void openKeybord(EditText mEditText, Context mContext)
	{
		InputMethodManager imm = (InputMethodManager) mContext
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(mEditText, InputMethodManager.RESULT_SHOWN);
		imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,
		                    InputMethodManager.HIDE_IMPLICIT_ONLY);
	}

	/**
	 * 关闭软键盘
	 * @param mEditText
	 * @param mContext
	 */
	public static void closeKeybord(EditText mEditText, Context mContext)
	{
		InputMethodManager imm = (InputMethodManager) mContext
				.getSystemService(Context.INPUT_METHOD_SERVICE);

		imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
	}
}
```
## 5.Log统一管理类、toast统一管理类</br>
* Log信息的打印与关闭
* toast的弹出与关闭
```java
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
```
```java
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
```
## 6.网络状态工具类</br>
* 判断是否有网络
* 判断是否是wifi连接
* 打开网络设置界面
```java
public class NetUtil {
	private NetUtil()
	{
        /* cannot be instantiated */
		throw new UnsupportedOperationException("cannot be instantiated");
	}

	/**
	 * 判断是否有网络连接
	 * @param context
	 *
	 * @return boolean true为已经连接
	 */
	public static boolean isConnected(Context context)
	{

		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		if (null != connectivity)
		{

			NetworkInfo info = connectivity.getActiveNetworkInfo();
			if (null != info && info.isConnected())
			{
				if (info.getState() == NetworkInfo.State.CONNECTED)
				{
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 判断是否是wifi连接
	 * @param context
	 *
	 * @return true 为wifi连接
	 */
	public static boolean isWifi(Context context)
	{
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		if (cm == null)
			return false;
		return cm.getActiveNetworkInfo().getType() == ConnectivityManager.TYPE_WIFI;

	}

	/**
	 * 打开网络设置界面
	 * @param activity 当前界面的activity
	 */
	public static void openSetting(Activity activity)
	{
		Intent intent = new Intent("/");
		ComponentName cm = new ComponentName("com.android.settings",
		                                     "com.android.settings.WirelessSettings");
		intent.setComponent(cm);
		intent.setAction("android.intent.action.VIEW");
		activity.startActivityForResult(intent, 0);
	}
}
```
## 7.屏幕信息工具类</br>
* 获取屏幕宽
* 获取屏幕高度
* 获取状态栏高度
* 获取屏幕截图（包括状态栏）
* 获取屏幕截图(不包括状态栏)
```java
public class ScreenUtil {
	private ScreenUtil()
	{
        /* 不允许实例化 */
		throw new UnsupportedOperationException("cannot be instantiated");
	}

	/**
	 * @param context
	 *
	 * @return 屏幕宽度 int
	 */
	public static int getScreenWidth(Context context)
	{
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);
		return outMetrics.widthPixels;
	}

	/**
	 * @param context
	 *
	 * @return 屏幕高度 int型
	 */
	public static int getScreenHeight(Context context)
	{
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);
		return outMetrics.heightPixels;
	}

	/**
	 * @param context
	 *
	 * @return 获取状态栏高度
	 */
	public static int getStatusHeight(Context context)
	{
		int statusHeight = -1;
		try
		{
			Class<?> clazz = Class.forName("com.android.internal.R$dimen");
			Object object = clazz.newInstance();
			int height = Integer.parseInt(clazz.getField("status_bar_height")
					                              .get(object).toString());
			statusHeight = context.getResources().getDimensionPixelSize(height);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return statusHeight;
	}

	/**
	 * 获取屏幕截图包括状态栏
	 * @param activity
	 *
	 * @return 带状态栏的截图
	 */
	public static Bitmap snapShotWithStatusBar(Activity activity)
	{
		View view = activity.getWindow().getDecorView();
		view.setDrawingCacheEnabled(true);
		view.buildDrawingCache();
		Bitmap bmp = view.getDrawingCache();
		int width = getScreenWidth(activity);
		int height = getScreenHeight(activity);
		Bitmap bp = null;
		bp = Bitmap.createBitmap(bmp, 0, 0, width, height);
		view.destroyDrawingCache();
		return bp;
	}

	/**
	 * 获取屏幕截图，不包括状态栏
	 * @param activity
	 *
	 * @return 不带状态栏的截图
	 */
	public static Bitmap snapShotWithoutStatusBar(Activity activity)
	{
		View view = activity.getWindow().getDecorView();
		view.setDrawingCacheEnabled(true);
		view.buildDrawingCache();
		Bitmap bmp = view.getDrawingCache();
		Rect frame = new Rect();
		activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
		int statusBarHeight = frame.top;

		int width = getScreenWidth(activity);
		int height = getScreenHeight(activity);
		Bitmap bp = null;
		bp = Bitmap.createBitmap(bmp, 0, statusBarHeight, width, height
				- statusBarHeight);
		view.destroyDrawingCache();
		return bp;
	}
}

```

## 8.SharedPreferences工具类</br>
* 存、取
* 清除所有数据
* 查询某个key是否存在
* 返回所有的键值对
* 反射查找apply方法，否则使用commit方法
```java
public class SPUtil {
	private static Context mContext = null;
	private static SharedPreferences sp = null;
	private static SharedPreferences.Editor editor = null;
	//保存在手机里面的文件名
	public static final String FILE_NAME = "share_data";

	private SPUtil(){}//不允许外界创建对象

	public static void init(Context context,String SP_NAME){
		mContext = context;
		if(sp==null && editor == null){
			sp = context.getSharedPreferences(SP_NAME,
			                                  Context.MODE_PRIVATE);
			editor = sp.edit();
		}
	}
	//保存数据的方法，我们需要拿到保存数据的具体类型，然后根据类型调用不同的保存方法
	public static void put(String key, Object object) {
		if (object instanceof String) {
			editor.putString(key, (String) object);
		} else if (object instanceof Integer) {
			editor.putInt(key, (Integer) object);
		} else	if (object instanceof Boolean) {
			editor.putBoolean(key, (Boolean) object);
		} else	if (object instanceof Float) {
			editor.putFloat(key, (Float) object);
		} else	if (object instanceof Long) {
			editor.putLong(key, (Long) object);
		} else {
			editor.putString(key, object.toString());
		}
		SharedPreferencesCompat.apply(editor);
	}

	/**
	 * 得到保存数据的方法，我们根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
	 */
	public static Object get(String key, Object defaultObject) {
		if (defaultObject instanceof String) {
			return sp.getString(key, (String) defaultObject);
		} else if (defaultObject instanceof Integer) {
			return sp.getInt(key, (Integer) defaultObject);
		} else if (defaultObject instanceof Boolean) {
			return sp.getBoolean(key, (Boolean) defaultObject);
		} else if (defaultObject instanceof Float) {
			return sp.getFloat(key, (Float) defaultObject);
		} else if (defaultObject instanceof Long) {
			return sp.getLong(key, (Long) defaultObject);
		}//此处添加需要的返回值
		return null;
	}

	/**
	 * 移除某个key值已经对应的值
	 */
	public static void remove(String key) {
		editor.remove(key);
		SharedPreferencesCompat.apply(editor);
	}

	//清除所有数据
	public static void clear(Context context) {
		editor.clear();
		SharedPreferencesCompat.apply(editor);
	}

	//查询某个key是否已经存在
	public static boolean contains(String key) {
		return sp.contains(key);
	}

	//返回所有的键值对
	public static Map<String, ?> getAll() {
		SharedPreferences sp = mContext.getSharedPreferences(FILE_NAME,
		                                                    Context.MODE_PRIVATE);
		return sp.getAll();
	}

	//创建一个解决SharedPreferencesCompat.apply方法的一个兼容类
	private static class SharedPreferencesCompat {
		private static final Method sApplyMethod = findApplyMethod();
		//反射查找apply的方法
		@SuppressWarnings({"unchecked", "rawtypes"})
		private static Method findApplyMethod() {
			try {
				Class clz = SharedPreferences.Editor.class;
				return clz.getMethod("apply");
			} catch (NoSuchMethodException e) {
			}
			return null;
		}

		//如果找到则使用apply执行，否则使用commit
		public static void apply(SharedPreferences.Editor editor) {
			try {
				if (sApplyMethod != null) {
					sApplyMethod.invoke(editor);
					return;
				}
			} catch (IllegalArgumentException e) {
			} catch (IllegalAccessException e) {
			} catch (InvocationTargetException e) {
			}
			editor.commit();
		}
	}
}
```
## 9.界面相关工具类</br>
* 获取xml中获取color，string ,stringArry
* 将layout.xml文件 转成 view
* 进制转换(dp -> px , px -> dp)
* 保证当前线程在主线程中运行
* 判断当前线程是不是在中线程中运行
```java
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
```
