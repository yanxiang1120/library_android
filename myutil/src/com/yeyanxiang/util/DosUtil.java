package com.yeyanxiang.util;

import java.io.DataOutputStream;
import java.io.OutputStream;

/**
 * @author 叶雁翔
 * 
 * @Email yanxiang1120@gmail.com
 * 
 * @version 1.0
 * 
 * @data 2014年1月2日 下午4:23:41
 * 
 * @简介
 */
public class DosUtil {

	/**
	 * 手机获取root权限后可执行adb命令
	 * 
	 * @param cmd
	 *            adb命令
	 */
	public static void execShell(String cmd) {
		try {
			// 权限设置
			Process p = Runtime.getRuntime().exec("su");
			// 获取输出流
			OutputStream outputStream = p.getOutputStream();
			DataOutputStream dataOutputStream = new DataOutputStream(
					outputStream);
			// 将命令写入
			dataOutputStream.writeBytes(cmd);
			// 提交命令
			dataOutputStream.flush();
			// 关闭流操作
			dataOutputStream.close();
			outputStream.close();
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
}
