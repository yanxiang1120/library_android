package com.yeyanxiang.util;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Application;
import android.content.Context;

/**
 * 
 * Create on 2013-4-28 上午10:44:19 </br> Copyright: Copyright(c) 2013 by 叶雁翔</br>
 * 
 * 简介:在Activity Create时调用add方法将当前Activity加入列表中，exit遍历退出
 * 
 * @Version 1.0
 * @Author <a href="mailto:yanxiang1120@126.com">叶雁翔</a>
 * 
 * 
 */
public class ApplicationManager extends Application {
	private List<Activity> list = new LinkedList<Activity>();
	private static ApplicationManager manager;

	/**
	 * 
	 * method desc：得到ApplicationManager对象
	 * 
	 * @return
	 */
	public static ApplicationManager getManager() {
		if (manager == null) {
			manager = new ApplicationManager();
		}
		return manager;
	}

	/**
	 * 
	 * method desc：将当前Activity 加入列表中
	 * 
	 * @param activity
	 */
	public void add(Activity activity) {
		if (list == null) {
			list = new LinkedList<Activity>();
		}
		list.add(activity);
	}

	/**
	 * method desc：遍历退出Activity并清理闲置进程
	 * 
	 * @param context
	 */
	public void exit(Context context) {
		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		for (RunningAppProcessInfo r : activityManager.getRunningAppProcesses()) {
			String[] pkgList = r.pkgList;
			if (r.importance > ActivityManager.RunningAppProcessInfo.IMPORTANCE_SERVICE) {
				// android.os.Process.killProcess(r.pid);
				for (int j = 0; j < pkgList.length; j++) {
					// 2.2以上是过时的,请用killBackgroundProcesses代替
					activityManager.restartPackage(pkgList[j]);
				}
			}
		}
		for (Activity a : list) {
			a.finish();
		}
		android.os.Process.killProcess(android.os.Process.myPid());
		System.exit(0);
	}

	/**
	 * 
	 * method desc：遍历退出Activity
	 * 
	 * @param activity
	 */
	public void exit() {
		if (list != null) {
			for (Activity a : list) {
				a.finish();
			}
			list.clear();
			list = null;
		}
	}
}
