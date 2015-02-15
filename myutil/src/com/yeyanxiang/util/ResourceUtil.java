package com.yeyanxiang.util;

import android.content.Context;
import android.content.res.Resources;

/**
 * @author 叶雁翔
 * 
 * @Email yanxiang1120@gmail.com
 * 
 * @version 1.0
 * 
 * @update 2014年3月7日
 * 
 * @简介
 */
public class ResourceUtil {
	public static int getidfromname(Context context, String name, String defType) {
		Resources resources = context.getResources();
		String packagename = context.getPackageName();
		return resources.getIdentifier(name, defType, packagename);
	}
}
