package com.yeyanxiang.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @author 叶雁翔
 * 
 * @Email yanxiang1120@126.com
 * 
 * @version 1.0
 * 
 * @date 2013-7-24 上午9:30:27
 * 
 * @简介
 */
public final class SharedPfsUtil {
	private SharedPreferences sp;
	private SharedPreferences.Editor editor;

	public SharedPfsUtil(Context context, String name) {
		sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
	}

	public SharedPreferences getSharedPreferences() {
		return sp;
	}

	public void putValue(String key, String value) {
		editor = sp.edit();
		editor.putString(key, value);
		editor.commit();
	}

	public void putValue(String key, boolean value) {
		editor = sp.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}

	public void putValue(String key, float value) {
		editor = sp.edit();
		editor.putFloat(key, value);
		editor.commit();
	}

	public void putValue(String key, int value) {
		editor = sp.edit();
		editor.putInt(key, value);
		editor.commit();
	}

	public void putValue(String key, long value) {
		editor = sp.edit();
		editor.putLong(key, value);
		editor.commit();
	}

	public String getValue(String key, String defValue) {
		return sp.getString(key, defValue);
	}

	public boolean getValue(String key, boolean defValue) {
		return sp.getBoolean(key, defValue);
	}

	public float getValue(String key, float defValue) {
		return sp.getFloat(key, defValue);
	}

	public int getValue(String key, int defValue) {
		return sp.getInt(key, defValue);
	}

	public long getValue(String key, long defValue) {
		return sp.getLong(key, defValue);
	}

}
