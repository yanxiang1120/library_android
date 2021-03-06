package com.yeyanxiang.util.dlna;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import org.cybergarage.upnp.Device;
import org.cybergarage.util.CommonLog;
import org.cybergarage.util.LogFactory;

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
public class UpnpUtil {

	private static final CommonLog log = LogFactory.createLog();

	public static boolean isValidDevice(Device device) {
		// if (UpnpUtil.isMediaRenderDevice(device)){
		// return true;
		// }

		if (UpnpUtil.isMediaServerDevice(device)
				&& !UpnpUtil.isLocalIpAddress(device)) {
			return true;
		}

		return false;

	}

	public static boolean isMediaServerDevice(Device device) {
		if ("urn:schemas-upnp-org:device:MediaServer:1".equalsIgnoreCase(device
				.getDeviceType())) {
			return true;
		}
		return false;
	}

	public static boolean isMediaRenderDevice(Device device) {
		if ("urn:schemas-upnp-org:device:MediaRenderer:1"
				.equalsIgnoreCase(device.getDeviceType())) {
			return true;
		}
		return false;
	}

	public final static String DLNA_OBJECTCLASS_MUSICID = "object.item.audioItem";
	public final static String DLNA_OBJECTCLASS_VIDEOID = "object.item.videoItem";
	public final static String DLNA_OBJECTCLASS_PHOTOID = "object.item.imageItem";

	public static boolean ismedia(Item item) {
		String objectClass = item.getObjectClass();
		if (objectClass != null && objectClass.contains("object.item")) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isAudioItem(Item item) {
		String objectClass = item.getObjectClass();
		if (objectClass != null
				&& objectClass.contains(DLNA_OBJECTCLASS_MUSICID)) {
			return true;
		}
		return false;
	}

	public static boolean isVideoItem(Item item) {
		String objectClass = item.getObjectClass();
		if (objectClass != null
				&& objectClass.contains(DLNA_OBJECTCLASS_VIDEOID)) {
			return true;
		}
		return false;
	}

	public static boolean isPictureItem(Item item) {
		String objectClass = item.getObjectClass();
		if (objectClass != null
				&& objectClass.contains(DLNA_OBJECTCLASS_PHOTOID)) {
			return true;
		}
		return false;
	}

	public static boolean isLocalIpAddress(Device device) {
		try {
			String addrip = device.getLocation();
			addrip = addrip.substring("http://".length(), addrip.length());
			addrip = addrip.substring(0, addrip.indexOf(":"));
			boolean ret = isLocalIpAddress(addrip);
			ret = false;
			return ret;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	public static boolean isLocalIpAddress(String checkip) {

		boolean ret = false;
		if (checkip != null) {
			try {
				for (Enumeration<NetworkInterface> en = NetworkInterface
						.getNetworkInterfaces(); en.hasMoreElements();) {
					NetworkInterface intf = en.nextElement();
					for (Enumeration<InetAddress> enumIpAddr = intf
							.getInetAddresses(); enumIpAddr.hasMoreElements();) {
						InetAddress inetAddress = enumIpAddr.nextElement();
						if (!inetAddress.isLoopbackAddress()) {
							String ip = inetAddress.getHostAddress().toString();

							if (ip == null) {
								continue;
							}
							if (checkip.equals(ip)) {
								return true;
							}
						}
					}
				}
			} catch (SocketException ex) {
				ex.printStackTrace();
			}
		}

		return ret;
	}
}
