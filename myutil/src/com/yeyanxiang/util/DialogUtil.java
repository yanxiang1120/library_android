package com.yeyanxiang.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;

/**
 * @author 叶雁翔
 * 
 * @Email yanxiang1120@gmail.com
 * 
 * @version 1.0
 * 
 * @update 2014年3月16日
 * 
 * @简介
 */
public class DialogUtil {

	/**
	 * 
	 * @param context
	 * @param title
	 * @param message
	 * @param alpha
	 * @return
	 */
	public static ProgressDialog CreateProgressDialog(Context context,
			String title, String message, float alpha) {
		ProgressDialog progressDialog = new ProgressDialog(context);
		progressDialog.setCancelable(true);
		progressDialog.setTitle(TextUtils.isEmpty(title) ? "正在努力加载中" : title);
		progressDialog.setMessage(TextUtils.isEmpty(message) ? "客官请稍后..."
				: message);
		progressDialog.show();
		Window window = progressDialog.getWindow();
		WindowManager.LayoutParams wLayoutParams = window.getAttributes();
		wLayoutParams.gravity = Gravity.CENTER;
		wLayoutParams.alpha = alpha > 0 ? alpha : 0.7f;
		wLayoutParams.width = LayoutParams.WRAP_CONTENT;
		wLayoutParams.height = LayoutParams.WRAP_CONTENT;
		window.setAttributes(wLayoutParams);
		return progressDialog;
	}
}
