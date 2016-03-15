package com.marsjiang.mygoogleplay.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedUtils {
	public static SharedPreferences sp;
	public static String FILEName = "MyNewsApp";

	public static SharedPreferences getSharedPreferences(Context context) {
		if (sp == null) {
			sp = context.getSharedPreferences(SharedUtils.FILEName,
					context.MODE_PRIVATE);
		}
		return sp;
	}

	public static void putBoolean(Context context, String key, boolean value) {
		getSharedPreferences(context).edit().putBoolean(key, value).commit();
	}

	public static boolean getBoolean(Context context, String key,boolean defaultValue) {
		return getSharedPreferences(context).getBoolean(key, defaultValue);
	}
	
	public static void putString(Context context, String key, String value) {
		getSharedPreferences(context).edit().putString(key, value).commit();
	}
	
	public static String getString(Context context, String key,String defaultValue) {
		return getSharedPreferences(context).getString(key, defaultValue);
	}
}
