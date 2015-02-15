package com.yeyanxiang.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.http.SslError;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.HttpAuthHandler;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.GeolocationPermissions.Callback;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebStorage.QuotaUpdater;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

/**
 * @author 叶雁翔
 * 
 * @Email yanxiang1120@gmail.com
 * 
 * @version 1.0
 * 
 * @data 2014年2月12日 下午5:20:28
 * 
 * @简介
 */
public class WebViewUtil {
	private MyWebChromeClient webChromeClient;

	@SuppressLint("JavascriptInterface")
	public void initWebview(WebView webView, FrameLayout mFullscreenContainer,
			FrameLayout mContentView, Activity activity,
			final WebViewCallBack webViewCallBack) {
		// 得到WebSettings对象，设置支持Javascript的参数
		WebSettings webSettings = webView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
		// 设置可以支持缩放
		webSettings.setSupportZoom(true);
		// 设置默认缩放方式尺寸是far
		webSettings.setDefaultZoom(WebSettings.ZoomDensity.FAR);
		// 设置出现缩放工具
		webSettings.setBuiltInZoomControls(true);
		// webSettings.setPluginsEnabled(true);
		webSettings.setPluginState(PluginState.ON);
		webSettings.setUseWideViewPort(true);
		webSettings.setLoadWithOverviewMode(true);

		webSettings.setAllowFileAccess(true);
		webSettings.setLoadWithOverviewMode(true);
		// webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
		// webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);

		// 取消滚动条
		webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		webView.setHorizontalScrollBarEnabled(false);
		webView.setVerticalScrollBarEnabled(false);

		webView.addJavascriptInterface(new JavaScriptInterface() {

			@Override
			public void getContentWidth(String value) {
				// TODO Auto-generated method stub
				if (webViewCallBack != null) {
					webViewCallBack.getContentWidth(value);
				}
			}
		}, "HTMLOUT");

		webChromeClient = new MyWebChromeClient(mFullscreenContainer,
				mContentView, activity, webViewCallBack);
		webView.setWebChromeClient(webChromeClient);

		webView.setWebViewClient(new WebViewClient() {

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// TODO Auto-generated method stub
				if (webViewCallBack != null) {
					return webViewCallBack.shouldOverrideUrlLoading(view, url);
				}
				return super.shouldOverrideUrlLoading(view, url);
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				// TODO Auto-generated method stub
				super.onPageStarted(view, url, favicon);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				// TODO Auto-generated method stub
				super.onPageFinished(view, url);
				LoadContentWidth(view);
			}

			@Override
			public void onLoadResource(WebView view, String url) {
				// TODO Auto-generated method stub
				super.onLoadResource(view, url);
			}

			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				// TODO Auto-generated method stub
				super.onReceivedError(view, errorCode, description, failingUrl);
				if (webViewCallBack != null) {
					webViewCallBack.onReceivedError(view, errorCode,
							description, failingUrl);
				}
			}

			@Override
			public void onReceivedHttpAuthRequest(WebView view,
					HttpAuthHandler handler, String host, String realm) {
				// TODO Auto-generated method stub
				super.onReceivedHttpAuthRequest(view, handler, host, realm);
			}

			@Override
			public void onTooManyRedirects(WebView view, Message cancelMsg,
					Message continueMsg) {
				// TODO Auto-generated method stub
				super.onTooManyRedirects(view, cancelMsg, continueMsg);
			}

			@Override
			public void onFormResubmission(WebView view, Message dontResend,
					Message resend) {
				// TODO Auto-generated method stub
				super.onFormResubmission(view, dontResend, resend);
			}

			@Override
			public void doUpdateVisitedHistory(WebView view, String url,
					boolean isReload) {
				// TODO Auto-generated method stub
				super.doUpdateVisitedHistory(view, url, isReload);
			}

			@Override
			public void onReceivedSslError(WebView view,
					SslErrorHandler handler, SslError error) {
				// TODO Auto-generated method stub
				super.onReceivedSslError(view, handler, error);
			}

			@Override
			public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
				// TODO Auto-generated method stub
				return super.shouldOverrideKeyEvent(view, event);
			}

			@Override
			public void onUnhandledKeyEvent(WebView view, KeyEvent event) {
				// TODO Auto-generated method stub
				super.onUnhandledKeyEvent(view, event);
			}

			@Override
			public void onScaleChanged(WebView view, float oldScale,
					float newScale) {
				// TODO Auto-generated method stub
				super.onScaleChanged(view, oldScale, newScale);
			}

		});
	}

	/**
	 * 加载webview内容宽度，在webviewcallback中
	 */
	public void LoadContentWidth(WebView webView) {
		webView.loadUrl("javascript:window.HTMLOUT.getContentWidth(document.getElementsByTagName('html')[0].scrollWidth);");
	}

	private abstract class JavaScriptInterface {

		/**
		 * 获取内容宽度
		 * 
		 * @param value
		 */
		public abstract void getContentWidth(String value);
	}

	public void onHideCustomView() {
		if (webChromeClient != null) {
			webChromeClient.onHideCustomView();
		}
	}

	private class MyWebChromeClient extends WebChromeClient {
		private CustomViewCallback mCustomViewCallback;
		private int mOriginalOrientation = 1;
		private View mCustomView = null;
		private FrameLayout mFullscreenContainer;
		private FrameLayout mContentView;
		private Activity activity;
		private WebViewCallBack webViewCallBack;

		public MyWebChromeClient() {
			super();
			// TODO Auto-generated constructor stub
		}

		public MyWebChromeClient(FrameLayout mFullscreenContainer,
				FrameLayout mContentView, Activity activity,
				WebViewCallBack webViewCallBack) {
			super();
			this.mFullscreenContainer = mFullscreenContainer;
			this.mContentView = mContentView;
			this.activity = activity;
			this.webViewCallBack = webViewCallBack;
		}

		@Override
		public void onShowCustomView(View view, CustomViewCallback callback) {
			// TODO Auto-generated method stub
			onShowCustomView(view, mOriginalOrientation, callback);
			super.onShowCustomView(view, callback);

		}

		@SuppressLint("Override")
		public void onShowCustomView(View view, int requestedOrientation,
				WebChromeClient.CustomViewCallback callback) {
			isshow = true;
			if (mCustomView != null) {
				callback.onCustomViewHidden();
				return;
			}
			if (getPhoneAndroidSDK() >= 14) {
				mFullscreenContainer.addView(view);
				mCustomView = view;
				mCustomViewCallback = callback;
				mOriginalOrientation = activity.getRequestedOrientation();
				mContentView.setVisibility(View.INVISIBLE);
				mFullscreenContainer.setVisibility(View.VISIBLE);
				mFullscreenContainer.bringToFront();
				activity.setRequestedOrientation(mOriginalOrientation);
			}

		}

		@Override
		public void onHideCustomView() {
			isshow = false;
			mContentView.setVisibility(View.VISIBLE);
			if (mCustomView == null) {
				return;
			}
			mCustomView.setVisibility(View.GONE);
			mFullscreenContainer.removeView(mCustomView);
			mCustomView = null;
			mFullscreenContainer.setVisibility(View.GONE);
			try {
				mCustomViewCallback.onCustomViewHidden();
			} catch (Exception e) {
			}
			activity.setRequestedOrientation(mOriginalOrientation);
		}

		@Override
		public void onProgressChanged(WebView view, int newProgress) {
			// TODO Auto-generated method stub
			if (webViewCallBack != null) {
				webViewCallBack.onProgressChanged(view, newProgress);
			}
			super.onProgressChanged(view, newProgress);
		}

		@Override
		public void onReceivedTitle(WebView view, String title) {
			// TODO Auto-generated method stub
			super.onReceivedTitle(view, title);
		}

		@Override
		public void onReceivedIcon(WebView view, Bitmap icon) {
			// TODO Auto-generated method stub
			super.onReceivedIcon(view, icon);
		}

		@Override
		public void onReceivedTouchIconUrl(WebView view, String url,
				boolean precomposed) {
			// TODO Auto-generated method stub
			super.onReceivedTouchIconUrl(view, url, precomposed);
		}

		@Override
		public boolean onCreateWindow(WebView view, boolean dialog,
				boolean userGesture, Message resultMsg) {
			// TODO Auto-generated method stub
			return super.onCreateWindow(view, dialog, userGesture, resultMsg);
		}

		@Override
		public void onRequestFocus(WebView view) {
			// TODO Auto-generated method stub
			super.onRequestFocus(view);
		}

		@Override
		public void onCloseWindow(WebView window) {
			// TODO Auto-generated method stub
			super.onCloseWindow(window);
		}

		/**
		 * 覆盖默认的window.alert展示界面，避免title里显示为“：来自file:////”
		 */
		@Override
		public boolean onJsAlert(WebView view, String url, String message,
				JsResult result) {
			new AlertDialog.Builder(context)
					.setTitle("Alert")
					.setMessage(message)
					.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface arg0,
										int arg1) {
									// TODO
								}
							}).create().show();
			return true;
		}

		@Override
		public boolean onJsConfirm(WebView view, String url, String message,
				JsResult result) {
			// TODO Auto-generated method stub
			return super.onJsConfirm(view, url, message, result);
		}

		@Override
		public boolean onJsPrompt(WebView view, String url, String message,
				String defaultValue, JsPromptResult result) {
			// TODO Auto-generated method stub
			return super.onJsPrompt(view, url, message, defaultValue, result);
		}

		@Override
		public boolean onJsBeforeUnload(WebView view, String url,
				String message, JsResult result) {
			// TODO Auto-generated method stub
			return super.onJsBeforeUnload(view, url, message, result);
		}

		@Override
		public void onGeolocationPermissionsShowPrompt(String origin,
				Callback callback) {
			// TODO Auto-generated method stub
			super.onGeolocationPermissionsShowPrompt(origin, callback);
		}

		@Override
		public void onGeolocationPermissionsHidePrompt() {
			// TODO Auto-generated method stub
			super.onGeolocationPermissionsHidePrompt();
		}

		@Override
		public boolean onJsTimeout() {
			// TODO Auto-generated method stub
			return super.onJsTimeout();
		}

		@Override
		public void onConsoleMessage(String message, int lineNumber,
				String sourceID) {
			// TODO Auto-generated method stub
			super.onConsoleMessage(message, lineNumber, sourceID);
		}

		@Override
		public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
			// TODO Auto-generated method stub
			return super.onConsoleMessage(consoleMessage);
		}

		@Override
		public Bitmap getDefaultVideoPoster() {
			// TODO Auto-generated method stub
			return super.getDefaultVideoPoster();
		}

		@Override
		public View getVideoLoadingProgressView() {
			// TODO Auto-generated method stub
			return super.getVideoLoadingProgressView();
		}

		@Override
		public void getVisitedHistory(ValueCallback<String[]> callback) {
			// TODO Auto-generated method stub
			super.getVisitedHistory(callback);
		}

		@Override
		public void onExceededDatabaseQuota(String url,
				String databaseIdentifier, long currentQuota,
				long estimatedSize, long totalUsedQuota,
				QuotaUpdater quotaUpdater) {
			// TODO Auto-generated method stub
			super.onExceededDatabaseQuota(url, databaseIdentifier,
					currentQuota, estimatedSize, totalUsedQuota, quotaUpdater);
		}

		@Override
		public void onReachedMaxAppCacheSize(long spaceNeeded,
				long totalUsedQuota, QuotaUpdater quotaUpdater) {
			// TODO Auto-generated method stub
			super.onReachedMaxAppCacheSize(spaceNeeded, totalUsedQuota,
					quotaUpdater);
		}

		public int getPhoneAndroidSDK() {
			// TODO Auto-generated method stub
			int version = 0;
			try {
				version = Integer.valueOf(android.os.Build.VERSION.SDK);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
			return version;
		}
	}

	public interface WebViewCallBack {
		/**
		 * Give the host application a chance to take over the control when a
		 * new url is about to be loaded in the current WebView. If
		 * WebViewClient is not provided, by default WebView will ask Activity
		 * Manager to choose the proper handler for the url. If WebViewClient is
		 * provided, return true means the host application handles the url,
		 * while return false means the current WebView handles the url.
		 * 
		 * @param view
		 *            The WebView that is initiating the callback.
		 * @param url
		 *            The url to be loaded.
		 * @return True if the host application wants to leave the current
		 *         WebView and handle the url itself, otherwise return false.
		 */
		boolean shouldOverrideUrlLoading(WebView view, String url);

		/**
		 * 返回内容宽度
		 * 
		 * @param value
		 */
		void getContentWidth(String value);

		/**
		 * Report an error to the host application. These errors are
		 * unrecoverable (i.e. the main resource is unavailable). The errorCode
		 * parameter corresponds to one of the ERROR_* constants.
		 * 
		 * @param view
		 *            The WebView that is initiating the callback.
		 * @param errorCode
		 *            The error code corresponding to an ERROR_* value.
		 * @param description
		 *            A String describing the error.
		 * @param failingUrl
		 *            The url that failed to load.
		 */
		void onReceivedError(WebView view, int errorCode, String description,
				String failingUrl);

		/**
		 * Tell the host application the current progress of loading a page.
		 * 
		 * @param view
		 *            The WebView that initiated the callback.
		 * @param newProgress
		 *            Current page loading progress, represented by an integer
		 *            between 0 and 100.
		 */
		void onProgressChanged(WebView view, int newProgress);
	}

	private Context context;
	private List<String> historyList;
	private ProgressDialog mDialog = null;
	private String url = "";
	private WebView web;

	private int WEBRELOAD = 5;
	private int background;
	private boolean loadShow = true;
	private Message msg = null;
	private View errorview;
	private boolean isshow = false;

	public WebViewUtil(Context con) {
		context = con;
		historyList = new ArrayList<String>();

	}

	public boolean Isshow() {
		return isshow;
	}

	public void SetErrorView(View errorview) {
		this.errorview = errorview;
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Log.i("-------httpStatus", "" + msg.what);
			switch (msg.what) {
			case 200:
				web.setVisibility(View.VISIBLE);
				if (errorview != null) {
					errorview.setVisibility(View.GONE);
				}
				web.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
				web.loadUrl(url);
				historyList.add(url);
				break;
			case 404:
				web.stopLoading();
				web.clearView();
				web.setVisibility(View.GONE);
				if (errorview != null) {
					errorview.setVisibility(View.VISIBLE);
				}
				break;
			case -1:
				web.stopLoading();
				web.clearView();
				web.setVisibility(View.GONE);
				if (errorview != null) {
					errorview.setVisibility(View.VISIBLE);
				}
				break;
			case 5:
				web.setVisibility(View.VISIBLE);
				if (errorview != null) {
					errorview.setVisibility(View.GONE);
				}
				web.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
				web.reload();
				break;
			default:
				web.stopLoading();
				web.clearView();
				web.setVisibility(View.GONE);
				if (errorview != null) {
					errorview.setVisibility(View.VISIBLE);
				}
				break;
			}

		}
	};

	/**
	 * 
	 * @param path
	 *            访问地址
	 * @param javascriptInterface
	 *            与jsp交互 对象
	 * @param webview
	 *            webview对象
	 * @param background
	 *            设置默认webview显示背景 解决显示白屏
	 */
	@SuppressLint("NewApi")
	public void setWebView(String path, Object javascriptInterface,
			WebView webview, int background)

	{
		this.url = path;
		this.web = webview;
		this.background = background;
		web.getSettings().setJavaScriptEnabled(true);

		// 支持缩放
		web.getSettings().setSupportZoom(true);
		web.getSettings().setBuiltInZoomControls(true);

		// 设置图片背景空
		web.setBackgroundColor(0);
		if (background != 0) {
			web.setBackgroundDrawable(context.getResources().getDrawable(
					background));
		}

		web.getSettings().setBlockNetworkImage(false);
		web.getSettings().setRenderPriority(RenderPriority.HIGH);
		// 应用数据
		web.getSettings().setDomStorageEnabled(true);
		web.getSettings().setAllowFileAccess(true);

		web.setVerticalScrollBarEnabled(false);
		web.setVerticalScrollbarOverlay(false);
		web.setHorizontalScrollBarEnabled(false);
		web.setHorizontalScrollbarOverlay(false);
		web.setWebChromeClient(new MyWebChromeClient());
		web.setWebViewClient(new WebViewClient() {

			public boolean shouldOverrideUrlLoading(WebView view, String path) {
				url = path;
				// 将当前访问的地址添加到数组中，并进行保存
				historyList.add(url);
				view.loadUrl(url);
				return true;
			}

			// Beginning page load
			@Override
			public void onPageStarted(WebView view, String path, Bitmap favicon) {
				super.onPageStarted(view, path, favicon);
				url = path;
				// 显示滚动条

			}

			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				super.onReceivedError(view, errorCode, description, failingUrl);

				view.stopLoading();
				view.clearView();
				view.setVisibility(View.GONE);
				if (errorview != null) {
					errorview.setVisibility(View.VISIBLE);
				}
			}

			// Page loaded
			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				// 重新设置优先访问网络
				view.getSettings().setBlockNetworkImage(false);

				loadShow = false;
				// 清理一个小时之前缓存的缓存
				Calendar cal = Calendar.getInstance();
				cal.set(Calendar.HOUR, Calendar.HOUR - 1);// 设置时间为当前时间-1小时
				long time = cal.getTimeInMillis();
				clearCacheFolder(context.getCacheDir(), time);
				// 第一次加载定位

				web.setVisibility(View.VISIBLE);
			}

		});
		if (javascriptInterface != null) {
			webview.addJavascriptInterface(javascriptInterface,
					"javascriptInterface");
		}

		new Thread(new runable()).start();
	}

	/**
	 * 调用者方法前一定要先 调用setWebView() 把web给实例化
	 */
	public void webViewReload() {
		web.reload();
		web.setBackgroundColor(0);
		if (background != 0) {
			web.setBackgroundDrawable(context.getResources().getDrawable(
					background));
		}
		loadShow = true;
		int stat = getRequestContent(url);
		if (stat == 200 || stat == 303 || stat == 301 || stat == 302
				|| stat == 307) {
			web.setVisibility(View.VISIBLE);
			if (errorview != null) {
				errorview.setVisibility(View.GONE);
			}
			web.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
			web.reload();
		} else {
			web.stopLoading();
			web.clearView();
			web.setVisibility(View.GONE);
			if (errorview != null) {
				errorview.setVisibility(View.VISIBLE);
			}
		}
	}

	/**
	 * 获取historyList(当前webview访问的页面历史记录)
	 */
	public List<String> getHistoryList() {
		return historyList;
	}

	/**
	 * 获取historyList的访问过的页面的个数
	 */
	public int getHistoryLength() {
		return historyList.size();
	}

	/**
	 * 捕捉页面报错 400 500 404
	 * 
	 * @param url
	 * @return
	 * @throws CloneNotSupportedException
	 */
	public int getRequestContent(String url) {
		int status = 0;
		// 是否没网络
		if (!isNetWork() && !isWifi()) {
			return status = 404;
		}
		HttpParams params = new BasicHttpParams();
		// 连接时间读取时间，设置为3秒
		HttpConnectionParams.setConnectionTimeout(params, 10000);
		HttpConnectionParams.setSoTimeout(params, 10000);

		DefaultHttpClient httpClient = new DefaultHttpClient(params);
		HttpHead head = new HttpHead(url);
		HttpResponse response;

		try {
			response = httpClient.execute(head);
			// 页面正常连接返回成功为200
			status = response.getStatusLine().getStatusCode();
		} catch (ConnectTimeoutException e) {
			// 连接超时
			status = -1;
		} catch (Exception e) {
			status = 404;
		} finally {
			try {
				head.clone();
			} catch (CloneNotSupportedException e) {

			}
		}
		return status;
	}

	/**
	 * 删除缓存
	 * 
	 * @param dir
	 * @param numDays
	 * @return
	 */
	private int clearCacheFolder(File dir, long numDays) {
		int deletedFiles = 0;
		if (dir != null && dir.isDirectory()) {
			try {
				for (File child : dir.listFiles()) {
					if (child.isDirectory()) {
						deletedFiles += clearCacheFolder(child, numDays);
					}

					if (child.lastModified() < numDays) {
						if (child.delete()) {
							deletedFiles++;
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return deletedFiles;
	}

	/**
	 * 判断是否有网络可以使用
	 * 
	 * @author Administrator
	 * 
	 */
	public boolean isNetWork() {
		boolean status = false;
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			// 网线
			NetworkInfo mNetworkInfo = mConnectivityManager
					.getActiveNetworkInfo();
			if (mNetworkInfo != null) {
				status = mNetworkInfo.isAvailable();
			}
		}
		return status;
	}

	public boolean isWifi() {
		boolean status = false;
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			// 网线
			NetworkInfo mNetworkInfo = mConnectivityManager
					.getActiveNetworkInfo();
			// wifi
			NetworkInfo mWiFiNetworkInfo = mConnectivityManager
					.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			if (mWiFiNetworkInfo != null) {
				status = mWiFiNetworkInfo.isAvailable();
			} else if (mNetworkInfo != null) {
				status = mNetworkInfo.isAvailable();
			}
		}
		return status;
	}

	class runable implements Runnable {
		public void run() {
			msg = handler.obtainMessage();
			msg.what = getRequestContent(url);
			handler.obtainMessage(msg.what, 0).sendToTarget();
		}
	}

	/**
	 * webview OnBack事件
	 * 
	 * @param webView
	 * @param progressBar
	 * @return 如果webview可返回return true,否则return false;
	 */
	public boolean onBack(WebView webView, ProgressBar progressBar) {
		if (webView == null) {
			return false;
		}

		if (isshow) {
			onHideCustomView();
		} else {
			if (webView.canGoBack()) {
				webView.goBack();
				if (progressBar != null) {
					progressBar.setVisibility(View.VISIBLE);
				}
			} else {
				return false;
			}
		}
		return true;
	}
}
