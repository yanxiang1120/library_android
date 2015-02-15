package com.yeyanxiang.util;

import android.os.Environment;
import android.os.StatFs;

/**
 * @author 叶雁翔
 * 
 * @Email yanxiang1120@gmail.com
 * 
 * @version 1.0
 * 
 * @update 2014年5月29日
 * 
 * @简介
 */
public class FileUtil {

	public static String[] videoSuffixes = new String[] { "3gp", "mp4", "flv",
			"avi", "mov", "wmv", "rmvb", "rm", "asf" };

	public static String[] audioSuffixes = new String[] { "mp3", "wav", "wma",
			"ape" };

	public static String[] imageSuffixes = new String[] { "png", "gif", "jpeg",
			"jpg", "bmp" };

	public static String[] docSuffixes = new String[] { "txt", "doc", "docx",
			"xls", "xlsx", "pdf", "ppt", "pptx" };

	/**
	 * 检查SD卡剩余大小是否足够
	 * 
	 * @param sizeMb
	 *            给个大小(MB)
	 * @return 如果足够返回true,否则返回false.
	 */
	public static long getSdcardSpace() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			String sdcard = Environment.getExternalStorageDirectory().getPath();
			// File file = new File(sdcard);
			StatFs statFs = new StatFs(sdcard);
			long blockSize = statFs.getBlockSize();
			long blocks = statFs.getAvailableBlocks();
			long availableSpare = (blocks * blockSize) / (1024 * 1024);
			// long availableSpare = (long)
			// (statFs.getBlockSize()*((long)statFs.getAvailableBlocks()-4))/(1024*1024);//以比特计算
			// 换算成MB
			System.out.println("availableSpare = " + availableSpare);
			return availableSpare;
		}
		return -1;
	}

	/**
	 * 根据文件路径判断文件是否是视频
	 * 
	 * @param path
	 *            文件路径
	 * @return
	 */
	public static boolean IsVideo(String path) {
		final String end = path.substring(path.lastIndexOf(".") + 1,
				path.length()).toLowerCase();
		for (String endtype : videoSuffixes) {
			if (end.equals(endtype)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 根据文件路径判断文件是否是音频
	 * 
	 * @param path
	 *            文件路径
	 * @return
	 */
	public static boolean IsAudio(String path) {
		final String end = path.substring(path.lastIndexOf(".") + 1,
				path.length()).toLowerCase();
		for (String endtype : audioSuffixes) {
			if (end.equals(endtype)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 根据文件路径判断文件是否是图片
	 * 
	 * @param path
	 *            文件路径
	 * @return
	 */
	public static boolean IsPicture(String path) {
		final String end = path.substring(path.lastIndexOf(".") + 1,
				path.length()).toLowerCase();
		for (String endtype : imageSuffixes) {
			if (end.equals(endtype)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 根据文件路径判断文件是否是文稿
	 * 
	 * @param path
	 *            文件路径
	 * @return
	 */
	public static boolean IsOffice(String path) {
		final String end = path.substring(path.lastIndexOf(".") + 1,
				path.length()).toLowerCase();
		for (String endtype : docSuffixes) {
			if (end.equals(endtype)) {
				return true;
			}
		}
		return false;
	}

}
