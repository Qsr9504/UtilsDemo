package com.qsr.utilsdemo.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;

/**************************************
 * FileName : com.qsr.utilsdemo.utils
 * Author : qsr
 * Time : 2017/1/6 17:33
 * Description :SharedPreferences的工具类
 **************************************/
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
