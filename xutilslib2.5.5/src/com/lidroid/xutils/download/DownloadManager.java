package com.lidroid.xutils.download;

import android.content.Context;
import android.database.Cursor;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.db.converter.ColumnConverter;
import com.lidroid.xutils.db.converter.ColumnConverterFactory;
import com.lidroid.xutils.db.sqlite.ColumnDbType;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.HttpHandler.State;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.util.LogUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 叶雁翔
 * 
 * @Email yanxiang1120@gmail.com
 * 
 * @version 1.0
 * 
 * @update 2014年5月7日
 * 
 * @简介
 */
public class DownloadManager {

	private List<DownloadInfo> downloadInfoList;

	private int maxDownloadThread = 3;

	private Context mContext;
	private DbUtils db;
	private boolean showsuccess = true;

	/* package */DownloadManager(Context appContext, boolean showsuccess) {
		ColumnConverterFactory.registerColumnConverter(HttpHandler.State.class,
				new HttpHandlerStateConverter());
		mContext = appContext;
		this.showsuccess = showsuccess;
		db = DbUtils.create(mContext);
		try {
			if (showsuccess) {
				downloadInfoList = db
						.findAll(Selector.from(DownloadInfo.class));
			} else {
				downloadInfoList = db.findAll(Selector.from(DownloadInfo.class)
						.where("state", "<", HttpHandler.State.SUCCESS));
			}
		} catch (DbException e) {
			LogUtils.e(e.getMessage(), e);
		}
		if (downloadInfoList == null) {
			downloadInfoList = new ArrayList<DownloadInfo>();
		}
	}

	public int getDownloadInfoListCount() {
		return downloadInfoList.size();
	}

	public DownloadInfo getDownloadInfo(int index) {
		return downloadInfoList.get(index);
	}

	public List<DownloadInfo> getDownloadInfoList() {
		return downloadInfoList;
	}

	/**
	 * 
	 * @param url
	 *            下载地址
	 * @param fileName
	 *            下载文件名称
	 * @param target
	 *            保存路径
	 * @param autoResume
	 *            如果目标文件存在，接着未完成的部分继续下载。服务器不支持RANGE时将从新下载。
	 * @param autoRename
	 *            如果从请求返回信息中获取到文件名，下载完成后自动重命名。
	 * @param callback
	 * @throws DbException
	 */
	public void addNewDownload(String url, String fileName, String target,
			boolean autoResume, boolean autoRename,
			final RequestCallBack<File> callback) throws DbException {
		final DownloadInfo downloadInfo = new DownloadInfo();
		downloadInfo.setDownloadUrl(url);
		downloadInfo.setAutoRename(autoRename);
		downloadInfo.setAutoResume(autoResume);
		downloadInfo.setFileName(fileName);
		downloadInfo.setFileSavePath(target);
		HttpUtils http = new HttpUtils();
		http.configRequestThreadPoolSize(maxDownloadThread);
		HttpHandler<File> handler = http.download(url, target, autoResume,
				autoRename, new ManagerCallBack(downloadInfo, callback));
		downloadInfo.setHandler(handler);
		downloadInfo.setState(handler.getState());
		downloadInfoList.add(downloadInfo);
		db.saveBindingId(downloadInfo);
	}

	/**
	 * 
	 * @param url
	 * @param fileName
	 * @param target
	 * @param autoResume
	 * @param autoRename
	 * @param callback
	 * @throws DbException
	 */
	public void addNewDownLoadTask(String url, String fileName, String target,
			boolean autoResume, boolean autoRename,
			final RequestCallBack<File> callback) throws DbException {
		final DownloadInfo downloadInfo = new DownloadInfo();
		downloadInfo.setDownloadUrl(url);
		downloadInfo.setAutoRename(autoRename);
		downloadInfo.setAutoResume(autoResume);
		downloadInfo.setFileName(fileName);
		downloadInfo.setFileSavePath(target);
		downloadInfo.setState(State.valueOf(-1));
		downloadInfoList.add(downloadInfo);
		db.saveBindingId(downloadInfo);
	}

	public void resumeDownload(int index, final RequestCallBack<File> callback)
			throws DbException {
		final DownloadInfo downloadInfo = downloadInfoList.get(index);
		resumeDownload(downloadInfo, callback);
	}

	public void resumeDownload(DownloadInfo downloadInfo,
			final RequestCallBack<File> callback) throws DbException {
		HttpUtils http = new HttpUtils();
		http.configRequestThreadPoolSize(maxDownloadThread);
		HttpHandler<File> handler = http.download(
				downloadInfo.getDownloadUrl(), downloadInfo.getFileSavePath(),
				downloadInfo.isAutoResume(), downloadInfo.isAutoRename(),
				new ManagerCallBack(downloadInfo, callback));
		downloadInfo.setHandler(handler);
		downloadInfo.setState(handler.getState());
		db.saveOrUpdate(downloadInfo);
	}

	public void removeDownload(int index) throws DbException {
		DownloadInfo downloadInfo = downloadInfoList.get(index);
		removeDownload(downloadInfo);
	}

	public void removeDownload(DownloadInfo downloadInfo) throws DbException {
		HttpHandler<File> handler = downloadInfo.getHandler();
		if (handler != null && !handler.isStopped()) {
			handler.stop();
		}
		downloadInfoList.remove(downloadInfo);
		db.delete(downloadInfo);
	}

	public void stopDownload(int index) throws DbException {
		DownloadInfo downloadInfo = downloadInfoList.get(index);
		stopDownload(downloadInfo);
	}

	public void stopDownload(DownloadInfo downloadInfo) throws DbException {
		HttpHandler<File> handler = downloadInfo.getHandler();
		if (handler != null && !handler.isStopped()) {
			handler.stop();
		} else {
			downloadInfo.setState(HttpHandler.State.STOPPED);
		}
		db.saveOrUpdate(downloadInfo);
	}

	public void stopAllDownload() throws DbException {
		for (DownloadInfo downloadInfo : downloadInfoList) {
			HttpHandler<File> handler = downloadInfo.getHandler();
			if (handler != null && !handler.isStopped()) {
				handler.stop();
				downloadInfo.setState(HttpHandler.State.STOPPED);
			}
		}
		db.saveOrUpdateAll(downloadInfoList);
	}

	public void backupDownloadInfoList() throws DbException {
		for (DownloadInfo downloadInfo : downloadInfoList) {
			HttpHandler<File> handler = downloadInfo.getHandler();
			if (handler != null) {
				downloadInfo.setState(handler.getState());
			}
		}
		db.saveOrUpdateAll(downloadInfoList);
	}

	public int getMaxDownloadThread() {
		return maxDownloadThread;
	}

	public void setMaxDownloadThread(int maxDownloadThread) {
		this.maxDownloadThread = maxDownloadThread;
	}

	public class ManagerCallBack extends RequestCallBack<File> {
		private DownloadInfo downloadInfo;
		private RequestCallBack<File> baseCallBack;

		public RequestCallBack<File> getBaseCallBack() {
			return baseCallBack;
		}

		public void setBaseCallBack(RequestCallBack<File> baseCallBack) {
			this.baseCallBack = baseCallBack;
		}

		private ManagerCallBack(DownloadInfo downloadInfo,
				RequestCallBack<File> baseCallBack) {
			this.baseCallBack = baseCallBack;
			this.downloadInfo = downloadInfo;
		}

		@Override
		public Object getUserTag() {
			if (baseCallBack == null)
				return null;
			return baseCallBack.getUserTag();
		}

		@Override
		public void setUserTag(Object userTag) {
			if (baseCallBack == null)
				return;
			baseCallBack.setUserTag(userTag);
		}

		@Override
		public void onStart() {
			HttpHandler<File> handler = downloadInfo.getHandler();
			if (handler != null) {
				downloadInfo.setState(handler.getState());
			}
			try {
				db.saveOrUpdate(downloadInfo);
			} catch (DbException e) {
				LogUtils.e(e.getMessage(), e);
			}
			if (baseCallBack != null) {
				baseCallBack.onStart();
			}
		}

		@Override
		public void onStopped() {
			HttpHandler<File> handler = downloadInfo.getHandler();
			if (handler != null) {
				downloadInfo.setState(handler.getState());
			}
			try {
				db.saveOrUpdate(downloadInfo);
			} catch (DbException e) {
				LogUtils.e(e.getMessage(), e);
			}
			if (baseCallBack != null) {
				baseCallBack.onStopped();
			}
		}

		@Override
		public void onLoading(long total, long current, boolean isUploading) {
			HttpHandler<File> handler = downloadInfo.getHandler();
			if (handler != null) {
				downloadInfo.setState(handler.getState());
			}
			downloadInfo.setFileLength(total);
			downloadInfo.setProgress(current);
			try {
				db.saveOrUpdate(downloadInfo);
			} catch (DbException e) {
				LogUtils.e(e.getMessage(), e);
			}
			if (baseCallBack != null) {
				baseCallBack.onLoading(total, current, isUploading);
			}
		}

		@Override
		public void onSuccess(ResponseInfo<File> responseInfo) {
			HttpHandler<File> handler = downloadInfo.getHandler();
			if (handler != null) {
				downloadInfo.setState(handler.getState());
			}
			try {
				db.saveOrUpdate(downloadInfo);
			} catch (DbException e) {
				LogUtils.e(e.getMessage(), e);
			}
			if (baseCallBack != null) {
				baseCallBack.onSuccess(responseInfo);
			}
		}

		@Override
		public void onFailure(HttpException error, String msg) {
			HttpHandler<File> handler = downloadInfo.getHandler();
			if (handler != null) {
				downloadInfo.setState(handler.getState());
			}
			try {
				db.saveOrUpdate(downloadInfo);
			} catch (DbException e) {
				LogUtils.e(e.getMessage(), e);
			}
			if (baseCallBack != null) {
				baseCallBack.onFailure(error, msg);
			}
		}

		@Override
		public void onCompletely() {
			// TODO Auto-generated method stub
			HttpHandler<File> handler = downloadInfo.getHandler();
			if (handler != null) {
				downloadInfo.setState(handler.getState());
			}
			try {
				db.saveOrUpdate(downloadInfo);
			} catch (DbException e) {
				LogUtils.e(e.getMessage(), e);
			}
			if (baseCallBack != null) {
				baseCallBack.onCompletely();
			}
		}
	}

	private class HttpHandlerStateConverter implements
			ColumnConverter<HttpHandler.State> {

		@Override
		public HttpHandler.State getFieldValue(Cursor cursor, int index) {
			return HttpHandler.State.valueOf(cursor.getInt(index));
		}

		@Override
		public HttpHandler.State getFieldValue(String fieldStringValue) {
			if (fieldStringValue == null)
				return null;
			return HttpHandler.State.valueOf(fieldStringValue);
		}

		@Override
		public Object fieldValue2ColumnValue(HttpHandler.State fieldValue) {
			return fieldValue.value();
		}

		@Override
		public ColumnDbType getColumnDbType() {
			return ColumnDbType.INTEGER;
		}
	}

	public static abstract class DownloadRequestCallBack extends
			RequestCallBack<File> {

		public DownloadRequestCallBack() {
			super();
			// TODO Auto-generated constructor stub
		}

		public DownloadRequestCallBack(int rate, Object userTag) {
			super(rate, userTag);
			// TODO Auto-generated constructor stub
		}

		public DownloadRequestCallBack(int rate) {
			super(rate);
			// TODO Auto-generated constructor stub
		}

		public DownloadRequestCallBack(Object userTag) {
			super(userTag);
			// TODO Auto-generated constructor stub
		}

		public abstract void refreshListItem();

		@Override
		public void onStart() {
			refreshListItem();
		}

		@Override
		public void onLoading(long total, long current, boolean isUploading) {
			refreshListItem();
		}

		@Override
		public void onSuccess(ResponseInfo<File> responseInfo) {
			refreshListItem();
		}

		@Override
		public void onFailure(HttpException error, String msg) {
			refreshListItem();
		}

		@Override
		public void onStopped() {
			refreshListItem();
		}

		@Override
		public void onCompletely() {
			// TODO Auto-generated method stub
			refreshListItem();
		}

	}
}
