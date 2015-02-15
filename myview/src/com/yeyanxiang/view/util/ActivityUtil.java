package com.yeyanxiang.view.util;

import com.yeyanxiang.view.R;

import android.app.Activity;

/**
 * @author 叶雁翔
 * 
 * @Email yanxiang1120@gmail.com
 * 
 * @version 1.0
 * 
 * @update 2014年3月13日
 * 
 * @简介
 */
public class ActivityUtil {

	/**
	 * Activity跳转动画
	 * 
	 * @param activity
	 */
	public static void loadanim(Activity activity) {
		activity.overridePendingTransition(R.anim.slide_right_in,
				R.anim.fadeout);
	}

	/**
	 * Activity返回
	 * 
	 * @param activity
	 */
	public static void loadbackanim(Activity activity) {
		activity.overridePendingTransition(R.anim.fadein,
				R.anim.slide_right_out);
	}
}
