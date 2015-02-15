package com.yeyanxiang.util;

/**
 * @author 叶雁翔
 * 
 * @Email yanxiang1120@gmail.com
 * 
 * @version 1.0
 * 
 * @data 2014年1月3日 上午11:08:30
 * 
 * @简介
 */
public class Utils {

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
