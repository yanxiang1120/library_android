/*
 * Copyright (C) 2010 Moduad Co., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.androidpn.client;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

/**
 * 
 * Create on 2013-4-28 上午10:31:04 </br> Copyright: Copyright(c) 2013 by 叶雁翔</br>
 * 
 * 简介: Service that continues to run in background and respond to the push
 * notification events from the server. This should be registered as service in
 * 
 * @Version 1.0
 * @Author <a href="mailto:yanxiang1120@126.com">叶雁翔</a>
 * 
 * 
 */
public class NotificationService extends Service implements
		NotificationServiceCallback {

	private static final String LOGTAG = LogUtil
			.makeLogTag(NotificationService.class);

	public static final String SERVICE_NAME = "org.androidpn.client.NotificationService";

	public TelephonyManager telephonyManager;

	public PhoneStateListener phoneStateListener;

	private ExecutorService executorService;

	public TaskSubmitter taskSubmitter;

	private TaskTracker taskTracker;

	public XmppManager xmppManager;

	public SharedPreferences sharedPrefs;

	public String deviceId;

	public NotificationService() {
		phoneStateListener = new PhoneStateChangeListener(this);
		executorService = Executors.newSingleThreadExecutor();
		taskSubmitter = new TaskSubmitter(this);
		taskTracker = new TaskTracker(this);
	}

	public void setDeviceId() {
		// Get deviceId
		deviceId = telephonyManager.getDeviceId();
		// Log.d(LOGTAG, "deviceId=" + deviceId);

		Editor editor = sharedPrefs.edit();
		editor.putString(Constants.DEVICE_ID, deviceId);

		// If running on an emulator
		if (deviceId == null || deviceId.trim().length() == 0
				|| deviceId.matches("0+")) {
			if (sharedPrefs.contains("EMULATOR_DEVICE_ID")) {
				deviceId = sharedPrefs.getString(Constants.EMULATOR_DEVICE_ID,
						"");
			} else {
				deviceId = (new StringBuilder("EMU")).append(
						(new Random(System.currentTimeMillis())).nextLong())
						.toString();
				editor.putString(Constants.EMULATOR_DEVICE_ID, deviceId);
				editor.commit();
			}
		}
	};

	public ExecutorService getExecutorService() {
		return executorService;
	}

	public TaskSubmitter getTaskSubmitter() {
		return taskSubmitter;
	}

	public TaskTracker getTaskTracker() {
		return taskTracker;
	}

	public XmppManager getXmppManager() {
		return xmppManager;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void connect() {
		// Log.d(LOGTAG, "connect()...");
		taskSubmitter.submit(new Runnable() {
			public void run() {
				getXmppManager().connect();
			}
		});
	}

	public void disconnect() {
		// Log.d(LOGTAG, "disconnect()...");
		taskSubmitter.submit(new Runnable() {
			public void run() {
				NotificationService.this.getXmppManager().disconnect();
			}
		});
	}

	public void start() {
		// Log.d(LOGTAG, "start()...");
		registerNotificationReceiver();
		registerConnectivityReceiver();
		xmppManager.connect();
	}

	public void stop() {
		// Log.d(LOGTAG, "stop()...");
		unregisterNotificationReceiver();
		unregisterConnectivityReceiver();
		xmppManager.disconnect();
		executorService.shutdown();
	}

	/**
	 * Class for summiting a new runnable task.
	 */
	public class TaskSubmitter {

		final NotificationService notificationService;

		public TaskSubmitter(NotificationService notificationService) {
			this.notificationService = notificationService;
		}

		@SuppressWarnings("unchecked")
		public Future submit(Runnable task) {
			Future result = null;
			if (!notificationService.getExecutorService().isTerminated()
					&& !notificationService.getExecutorService().isShutdown()
					&& task != null) {
				result = notificationService.getExecutorService().submit(task);
			}
			return result;
		}

	}

	/**
	 * Class for monitoring the running task count.
	 */
	public class TaskTracker {

		final NotificationService notificationService;

		public int count;

		public TaskTracker(NotificationService notificationService) {
			this.notificationService = notificationService;
			this.count = 0;
		}

		public void increase() {
			synchronized (notificationService.getTaskTracker()) {
				notificationService.getTaskTracker().count++;
				// Log.d(LOGTAG, "Incremented task count to " + count);
			}
		}

		public void decrease() {
			synchronized (notificationService.getTaskTracker()) {
				notificationService.getTaskTracker().count--;
				// Log.d(LOGTAG, "Decremented task count to " + count);
			}
		}

	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void registerNotificationReceiver() {
		// TODO Auto-generated method stub
	}

	@Override
	public void unregisterNotificationReceiver() {
		// TODO Auto-generated method stub

	}

	@Override
	public void registerConnectivityReceiver() {
		// TODO Auto-generated method stub
		telephonyManager.listen(phoneStateListener,
				PhoneStateListener.LISTEN_DATA_CONNECTION_STATE);
	}

	@Override
	public void unregisterConnectivityReceiver() {
		// TODO Auto-generated method stub
		telephonyManager.listen(phoneStateListener,
				PhoneStateListener.LISTEN_NONE);
	}

	@Override
	public String getShowNotifyAction() {
		// TODO Auto-generated method stub
		return Constants.ACTION_SHOW_NOTIFICATION;
	}

	@Override
	public SharedPreferences getSharedPreferences() {
		// TODO Auto-generated method stub
		return sharedPrefs;
	}

}
