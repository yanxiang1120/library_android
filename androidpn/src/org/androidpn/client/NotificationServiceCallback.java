package org.androidpn.client;

import android.content.SharedPreferences;

public interface NotificationServiceCallback {

	SharedPreferences getSharedPreferences();

	void registerNotificationReceiver();

	void unregisterNotificationReceiver();

	void registerConnectivityReceiver();

	void unregisterConnectivityReceiver();

	String getShowNotifyAction();
}
