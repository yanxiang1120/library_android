package com.yeyanxiang.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;

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
public class StringUtil {
	/**
	 * 处理包含中文的url
	 * 
	 * @param url
	 *            包含中文的url
	 * @return 返回处理后的url
	 */
	public static String parseChineseUrl(String url) {
		try {
			String urlString = URLEncoder.encode(url, "UTF-8");
			urlString = urlString.replace("%3A", ":");
			urlString = urlString.replace("%2F", "/");
			return urlString;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return url;
	}

	/**
	 * 用颜色标记字体
	 * 
	 * @param value
	 * @param mark
	 *            需要标记的字体
	 * @param color
	 * @return SpannableStringBuilder
	 */
	public static SpannableStringBuilder parseStringbyColor(String value,
			String mark, int color) {
		SpannableStringBuilder style = new SpannableStringBuilder(value);
		if (value.contains(mark)) {
			style.setSpan(new ForegroundColorSpan(color), value.indexOf(mark),
					value.indexOf(mark) + mark.length(),
					Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		return style;
	}

	/**
	 * 标记字体大小
	 * 
	 * @param value
	 * @param mark
	 *            需要标记的字体
	 * @param size
	 *            参数表示为默认字体大小的多少倍
	 * @return SpannableStringBuilder
	 */
	public static SpannableStringBuilder parseStringbySize(String value,
			String mark, float size) {
		SpannableStringBuilder style = new SpannableStringBuilder(value);
		if (value.contains(mark)) {
			style.setSpan(new RelativeSizeSpan(size), value.indexOf(mark),
					value.indexOf(mark) + mark.length(),
					Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		return style;
	}

	/**
	 * 标记字体属性
	 * 
	 * @param value
	 * @param mark
	 *            需要标记的字体
	 * @param typeface
	 *            字体属性(例如：Typeface.NORMAL)
	 * 
	 * @return SpannableStringBuilder
	 */
	public static SpannableStringBuilder parseStringbySize(String value,
			String mark, int typeface) {
		SpannableStringBuilder style = new SpannableStringBuilder(value);
		if (value.contains(mark)) {
			style.setSpan(new StyleSpan(typeface), value.indexOf(mark),
					value.indexOf(mark) + mark.length(),
					Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		return style;
	}

	/**
	 * 用颜色标记字体
	 * 
	 * @param style
	 * @param mark
	 *            需要标记的字体
	 * @param color
	 * @return SpannableStringBuilder
	 */
	public static SpannableStringBuilder parseStringbyColor(
			SpannableStringBuilder style, String mark, int color) {
		if (style == null) {
			return null;
		}
		String value = style.toString();
		if (value.contains(mark)) {
			style.setSpan(new ForegroundColorSpan(color), value.indexOf(mark),
					value.indexOf(mark) + mark.length(),
					Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		return style;
	}

	/**
	 * 标记字体大小
	 * 
	 * @param style
	 * @param mark
	 *            需要标记的字体
	 * @param size
	 *            参数表示为默认字体大小的多少倍
	 * @return SpannableStringBuilder
	 */
	public static SpannableStringBuilder parseStringbySize(
			SpannableStringBuilder style, String mark, float size) {
		if (style == null) {
			return null;
		}
		String value = style.toString();
		if (value.contains(mark)) {
			style.setSpan(new RelativeSizeSpan(size), value.indexOf(mark),
					value.indexOf(mark) + mark.length(),
					Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		return style;
	}

	/**
	 * 标记字体属性
	 * 
	 * @param style
	 * @param mark
	 *            需要标记的字体
	 * @param typeface
	 *            字体属性(例如：Typeface.NORMAL)
	 * 
	 * @return SpannableStringBuilder
	 */
	public static SpannableStringBuilder parseStringbySize(
			SpannableStringBuilder style, String mark, int typeface) {
		if (style == null) {
			return null;
		}
		String value = style.toString();
		if (value.contains(mark)) {
			style.setSpan(new StyleSpan(typeface), value.indexOf(mark),
					value.indexOf(mark) + mark.length(),
					Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		return style;
	}
	
	/**
	 * 检查ip格式是否正确
	 * 
	 * @param ip
	 * @return
	 */
	public static boolean checkip(String ip) {
		if (ip.contains(".")) {
			if (".".equals(ip.substring(ip.lastIndexOf(".")).trim())) {
				return false;
			}

			ip = ip.replace(".", ",");
			String[] values = ip.split(",");
			if (values.length == 4) {
				for (String value : values) {
					if (!checkvalue(value.trim())) {
						return false;
					}
				}
				return true;
			}
		}
		return false;
	}

	private static boolean checkvalue(String ipvalue) {
		try {
			int value = Integer.parseInt(ipvalue);
			if (value < 0 || value > 255) {
				return false;
			}
		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}
		return true;
	}

	/**
	 * 返回http头部信息,例如"http://127.0.0.0:8080"
	 * 
	 * @param ip
	 * @param port
	 * @return
	 */
	public static String getHttpHead(String ip, String port) {
		return "http://" + ip.trim() + ":" + port.trim();
	}
}
