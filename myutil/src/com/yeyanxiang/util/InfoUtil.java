package com.yeyanxiang.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import org.apache.http.conn.util.InetAddressUtils;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

/**
 * @author 叶雁翔
 * 
 * @Email yanxiang1120@126.com
 * 
 * @version 1.0
 * 
 * @date 2013-8-15 上午11:22:15
 * 
 * @简介
 */
public class InfoUtil {

	/**
	 * 
	 * @param context
	 * @return DeviceID
	 */
	public static String getDevicedId(Context context) {
		return ((TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
	}

	/**
	 * 通过WIFI获取IP地址
	 * 
	 * @param context
	 * @return
	 */
	public static String getIpAddressFromWifi(Context context) {
		int ipaddress = ((WifiManager) context
				.getSystemService(Context.WIFI_SERVICE)).getConnectionInfo()
				.getIpAddress();
		return (ipaddress & 0xFF) + "." + ((ipaddress >> 8) & 0xFF) + "."
				+ ((ipaddress >> 16) & 0xFF) + "." + ((ipaddress >> 24) & 0xFF);
	}

	/**
	 * 通过WIFI获取MAC地址
	 * 
	 * @param context
	 * @return
	 */
	public static String getMacAddressFromWifi(Context context) {
		return ((WifiManager) context.getSystemService(Context.WIFI_SERVICE))
				.getConnectionInfo().getMacAddress();
	}

	/**
	 * 获取本地IP地址，可获取3g状态下的IP地址，也可获取网线的IP地址
	 * 
	 * @return
	 */
	public static String getIpAddressFromLocal() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {

				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()
							&& InetAddressUtils.isIPv4Address(inetAddress
									.getHostAddress())) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	/**
	 * 判断网络是否连接
	 * 
	 * @param context
	 * @return
	 */
	public static boolean IsNetWorkOK(Context context) {
		final NetworkInfo netInfo = ((ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE))
				.getActiveNetworkInfo();
		if (netInfo != null) {
			return netInfo.isConnected();
		} else {
			return false;
		}
	}

	/**
	 * 
	 * @param context
	 * @return 当前网络状态，异常返回error
	 */
	public static String getNetState(Context context) {
		final NetworkInfo netInfo = ((ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE))
				.getActiveNetworkInfo();
		if (netInfo != null) {
			if (netInfo.isConnected()) {
				return netInfo.getTypeName();
			}
		}
		return "error";
	}

	/**
	 * 通过IP地址获取Mac(只能在2.3以后的真机中使用)
	 * 
	 * @param context
	 * @param ipaddress
	 * @return
	 */
	public static String getMacAddressFromIp(Context context, String ipaddress) {
		String mac_s = "";
		try {
			byte[] mac;
			NetworkInterface ne = NetworkInterface.getByInetAddress(InetAddress
					.getByName(ipaddress));
			mac = ne.getHardwareAddress();
			mac_s = byte2hex(mac);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return mac_s;
	}

	private static String byte2hex(byte[] b) {
		StringBuffer hs = new StringBuffer(b.length);
		String stmp = "";
		int len = b.length;
		for (int n = 0; n < len; n++) {
			stmp = Integer.toHexString(b[n] & 0xFF);
			if (stmp.length() == 1)
				hs = hs.append("0").append(stmp);
			else {
				hs = hs.append(stmp);
			}
		}
		return String.valueOf(hs);
	}

	/**
	 * 通过Linux命令获取MAC
	 * 
	 * @param wifistate
	 *            true 返回wifi状态下的mac,false返回net状态下的mac
	 * @return
	 */
	public static String getMacAddressFromBusybox(boolean wifistate) {
		try {
			Process proc = Runtime.getRuntime().exec("busybox ifconfig");
			InputStreamReader is = new InputStreamReader(proc.getInputStream());
			BufferedReader br = new BufferedReader(is);
			String line = "";
			while ((line = br.readLine()) != null) {
				if (line.contains("p2p")) {
					if (!wifistate) {
						if (line.contains("HWaddr")) {
							return line.substring(line.indexOf("HWaddr") + 6)
									.trim();
						}
					}
				} else if (line.contains("wlan")) {
					if (line.contains("HWaddr")) {
						return line.substring(line.indexOf("HWaddr") + 6)
								.trim();
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "error";
	}

	/**
	 * 执行CMD命令并返回结果
	 * 
	 * @param cmd
	 * @return
	 */
	public static String CallCmd(String cmd) {
		String result = "";
		String line = "";
		try {
			Process proc = Runtime.getRuntime().exec(cmd);
			InputStreamReader is = new InputStreamReader(proc.getInputStream());
			BufferedReader br = new BufferedReader(is);
			while ((line = br.readLine()) != null) {
				result += line;
				Log.i("CallCmd", line);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 获取网络时间
	 * 
	 * @param context
	 * @param urlstring
	 *            需要获取时间的网址，null取默认值 http://open.baidu.com/special/time/
	 * @return
	 */
	public static Long getDateFromInternet(Context context, String urlstring) {
		long date = System.currentTimeMillis();

		if (IsNetWorkOK(context)) {
			try {
				URL url;
				if (TextUtils.isEmpty(urlstring)) {
					url = new URL("http://open.baidu.com/special/time/");
				} else {
					url = new URL(urlstring);
				}
				URLConnection uc = url.openConnection();
				uc.connect();
				date = uc.getDate();
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			Log.i("getDateFromInternet", "error");
		}
		return date;
	}

	/**
	 * 转换时间格式
	 * 
	 * @param date
	 *            时间
	 * @param format
	 *            格式（yyyy-MM-dd HH:mm:ss）
	 * @return
	 */
	public static String getDate(Date date, String format) {
		if (TextUtils.isEmpty(format)) {
			format = "yyyy-MM-dd HH:mm:ss";
		}
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		return dateFormat.format(date);
	}

}
