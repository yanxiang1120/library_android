package com.yeyanxiang.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * @author 叶雁翔
 * 
 * @Email yanxiang1120@gmail.com
 * 
 * @version 1.0
 * 
 * @update 2014年3月14日
 * 
 * @简介
 */
public class HttpUtil {

	private static String TAG = "HttpUtil";

	/**
	 * 通过Get方式访问Internet得到返回值
	 * 
	 * @param httpUrl
	 *            不解释
	 * @param timeout
	 *            连接和读取超时时间
	 * @param showexceptioninfo
	 *            是否显示异常信息
	 * @return Internet返回值
	 */
	public static String sendGetRequest(String httpUrl, int timeout,
			boolean showexceptioninfo) {
		TAG = "sendGetRequest";
		String strResult = "";
		try {
			HttpGet httpRequest = new HttpGet(httpUrl);
			HttpClient httpClient = new DefaultHttpClient();
			if (timeout > 0) {
				// 请求超时
				httpClient.getParams().setParameter(
						CoreConnectionPNames.CONNECTION_TIMEOUT, timeout);
				// 读取超时
				httpClient.getParams().setParameter(
						CoreConnectionPNames.SO_TIMEOUT, timeout);
			}

			HttpResponse httpResponse = httpClient.execute(httpRequest);
			int result = httpResponse.getStatusLine().getStatusCode();
			if (result == HttpStatus.SC_OK) {
				strResult = EntityUtils.toString(httpResponse.getEntity());
			} else {
				strResult = result + "";
				System.out.println("error:" + result);
			}
		} catch (SocketTimeoutException e) {
			System.out.println(TAG + "---SocketTimeoutException");
			strResult = "Timeout";
			showexception(e, showexceptioninfo);
		} catch (ConnectTimeoutException e) {
			System.out.println(TAG + "---ConnectTimeoutException");
			strResult = "Timeout";
			showexception(e, showexceptioninfo);
		} catch (UnsupportedEncodingException e) {
			System.out.println(TAG + "---UnsupportedEncodingException");
			showexception(e, showexceptioninfo);
		} catch (ClientProtocolException e) {
			System.out.println(TAG + "---ClientProtocolException");
			showexception(e, showexceptioninfo);
		} catch (IOException e) {
			System.out.println(TAG + "---IOException");
			showexception(e, showexceptioninfo);
		} catch (Exception e) {
			System.out.println(TAG + "---Exception");
			showexception(e, showexceptioninfo);
		}
		return strResult;
	}

	private static void showexception(Exception e, boolean showexceptioninfo) {
		// TODO Auto-generated method stub
		if (showexceptioninfo) {
			e.printStackTrace();
		}
	}

	/**
	 * 通过Get方式访问Internet得到InputStream
	 * 
	 * @param httpUrl
	 *            不解释
	 * @param timeout
	 *            连接和读取超时时间
	 * @param showexceptioninfo
	 *            是否显示异常信息
	 * @return InputStream
	 */
	public static InputStream sendGetRequeststream(String httpUrl, int timeout,
			boolean showexceptioninfo) {
		TAG = "sendGetRequeststream";
		try {
			HttpGet httpRequest = new HttpGet(httpUrl);
			HttpClient httpClient = new DefaultHttpClient();
			if (timeout > 0) {
				// 请求超时
				httpClient.getParams().setParameter(
						CoreConnectionPNames.CONNECTION_TIMEOUT, timeout);
				// 读取超时
				httpClient.getParams().setParameter(
						CoreConnectionPNames.SO_TIMEOUT, timeout);
			}

			HttpResponse httpResponse = httpClient.execute(httpRequest);
			int result = httpResponse.getStatusLine().getStatusCode();
			if (result == HttpStatus.SC_OK) {
				return httpResponse.getEntity().getContent();
			} else {
				System.out.println("error:" + result);
				return null;
			}
		} catch (SocketTimeoutException e) {
			System.out.println(TAG + "---SocketTimeoutException");
			showexception(e, showexceptioninfo);
		} catch (ConnectTimeoutException e) {
			System.out.println(TAG + "---ConnectTimeoutException");
			showexception(e, showexceptioninfo);
		} catch (UnsupportedEncodingException e) {
			System.out.println(TAG + "---UnsupportedEncodingException");
			showexception(e, showexceptioninfo);
		} catch (ClientProtocolException e) {
			System.out.println(TAG + "---ClientProtocolException");
			showexception(e, showexceptioninfo);
		} catch (IOException e) {
			System.out.println(TAG + "---IOException");
			showexception(e, showexceptioninfo);
		} catch (Exception e) {
			System.out.println(TAG + "---Exception");
			showexception(e, showexceptioninfo);
		}
		return null;
	}

	/**
	 * 访问Internet得到OutputStream
	 * 
	 * @param httpUrl
	 *            不解释
	 * @param timeout
	 *            连接和读取超时时间
	 * @param showexceptioninfo
	 *            是否显示异常信息
	 * @return OutputStream
	 */
	public static OutputStream getOutputStream(String httpUrl, int timeout,
			boolean showexceptioninfo) {
		TAG = "getOutputStream";
		try {
			HttpURLConnection connection = (HttpURLConnection) new URL(httpUrl)
					.openConnection();
			connection.setConnectTimeout(timeout);
			connection.setReadTimeout(timeout);

			// 设置是否向httpUrlConnection输出，因为这个是post请求，参数要放在
			// http正文内，因此需要设为true, 默认情况下是false;
			connection.setDoOutput(true);

			// 设置是否从httpUrlConnection读入，默认情况下是true;
			connection.setDoInput(true);

			// Post 请求不能使用缓存
			connection.setUseCaches(false);
			connection.setChunkedStreamingMode(0);

			// 设定传送的内容类型是可序列化的java对象
			// (如果不设此项,在传送序列化对象时,当WEB服务默认的不是这种类型时可能抛java.io.EOFException)
			connection.setRequestProperty("Content-type",
					"application/x-java-serialized-object");

			// 设定请求的方法为"POST"，默认是GET
			connection.setRequestMethod("POST");

			// 设置维持长连接
			connection.setRequestProperty("Connection", "Keep-Alive");
			// 设置文件字符集
			connection.setRequestProperty("Charset", "UTF-8");
			// 设置文件长度
			// connection.setRequestProperty("Content-Length",
			// String.valueOf(length));

			// 连接，从上述第2条中url.openConnection()至此的配置必须要在connect之前完成，
			connection.connect();

			return connection.getOutputStream();
		} catch (SocketTimeoutException e) {
			System.out.println(TAG + "---SocketTimeoutException");
			showexception(e, showexceptioninfo);
		} catch (ConnectTimeoutException e) {
			System.out.println(TAG + "---ConnectTimeoutException");
			showexception(e, showexceptioninfo);
		} catch (MalformedURLException e) {
			System.out.println(TAG + "---MalformedURLException");
			showexception(e, showexceptioninfo);
		} catch (IOException e) {
			System.out.println(TAG + "---IOException");
			showexception(e, showexceptioninfo);
		} catch (Exception e) {
			System.out.println(TAG + "---Exception");
			showexception(e, showexceptioninfo);
		}
		return null;
	}

	/**
	 * 通过post方式访问Internet得到返回值
	 * 
	 * @param httpUrl
	 *            不解释
	 * @param params
	 *            参数
	 * @param timeout
	 *            连接和读取超时时间
	 * @param showexceptioninfo
	 *            是否显示异常信息
	 * @return Internet返回值
	 */
	public static String sendPostRequest(String httpUrl,
			List<? extends NameValuePair> parameters, int timeout,
			boolean showexceptioninfo) {
		TAG = "sendPostRequest";
		String strResult = "";
		try {
			HttpPost httpRequest = new HttpPost(httpUrl);
			httpRequest
					.setEntity(new UrlEncodedFormEntity(parameters, "UTF-8"));
			HttpClient httpClient = new DefaultHttpClient();
			if (timeout > 0) {
				// 请求超时
				httpClient.getParams().setParameter(
						CoreConnectionPNames.CONNECTION_TIMEOUT, timeout);
				// 读取超时
				httpClient.getParams().setParameter(
						CoreConnectionPNames.SO_TIMEOUT, timeout);
			}

			HttpResponse httpResponse = httpClient.execute(httpRequest);
			int result = httpResponse.getStatusLine().getStatusCode();
			if (result == HttpStatus.SC_OK) {
				strResult = EntityUtils.toString(httpResponse.getEntity());
			} else {
				strResult = result + "";
				System.out.println("error:" + result);
			}
		} catch (SocketTimeoutException e) {
			System.out.println(TAG + "---SocketTimeoutException");
			strResult = "Timeout";
			showexception(e, showexceptioninfo);
		} catch (ConnectTimeoutException e) {
			System.out.println(TAG + "---ConnectTimeoutException");
			strResult = "Timeout";
			showexception(e, showexceptioninfo);
		} catch (UnsupportedEncodingException e) {
			System.out.println(TAG + "---UnsupportedEncodingException");
			showexception(e, showexceptioninfo);
		} catch (ClientProtocolException e) {
			System.out.println(TAG + "---ClientProtocolException");
			showexception(e, showexceptioninfo);
		} catch (IOException e) {
			System.out.println(TAG + "---IOException");
			showexception(e, showexceptioninfo);
		} catch (Exception e) {
			System.out.println(TAG + "---Exception");
			showexception(e, showexceptioninfo);
		}
		return strResult;
	}

	/**
	 * 通过post方式访问Internet得到InputStream
	 * 
	 * @param httpUrl
	 *            不解释
	 * @param params
	 *            参数
	 * @param timeout
	 *            连接和读取超时时间
	 * @param showexceptioninfo
	 *            是否显示异常信息
	 * @return InputStream
	 */
	public static InputStream sendPostRequeststream(String httpUrl,
			List<? extends NameValuePair> parameters, int timeout,
			boolean showexceptioninfo) {
		TAG = "sendPostRequeststream";
		try {
			HttpPost httpRequest = new HttpPost(httpUrl);
			httpRequest
					.setEntity(new UrlEncodedFormEntity(parameters, "UTF-8"));
			HttpClient httpClient = new DefaultHttpClient();
			if (timeout > 0) {
				// 请求超时
				httpClient.getParams().setParameter(
						CoreConnectionPNames.CONNECTION_TIMEOUT, timeout);
				// 读取超时
				httpClient.getParams().setParameter(
						CoreConnectionPNames.SO_TIMEOUT, timeout);
			}

			HttpResponse httpResponse = httpClient.execute(httpRequest);
			int result = httpResponse.getStatusLine().getStatusCode();
			if (result == HttpStatus.SC_OK) {
				return httpResponse.getEntity().getContent();
			} else {
				System.out.println("error:" + result);
				return null;
			}
		} catch (SocketTimeoutException e) {
			System.out.println(TAG + "---SocketTimeoutException");
			showexception(e, showexceptioninfo);
		} catch (ConnectTimeoutException e) {
			System.out.println(TAG + "---ConnectTimeoutException");
			showexception(e, showexceptioninfo);
		} catch (UnsupportedEncodingException e) {
			System.out.println(TAG + "---UnsupportedEncodingException");
			showexception(e, showexceptioninfo);
		} catch (ClientProtocolException e) {
			System.out.println(TAG + "---ClientProtocolException");
			showexception(e, showexceptioninfo);
		} catch (IOException e) {
			System.out.println(TAG + "---IOException");
			showexception(e, showexceptioninfo);
		} catch (Exception e) {
			System.out.println(TAG + "---Exception");
			showexception(e, showexceptioninfo);
		}
		return null;
	}

	/**
	 * 判断网络是否连接
	 * 
	 * @param context
	 * @return
	 */
	public static boolean IsNetworkConnect(Context context) {
		NetworkInfo netInfo = ((ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE))
				.getActiveNetworkInfo();
		if (netInfo != null) {
			// return netInfo.isAvailable();
			return netInfo.isConnected();
		} else {
			return false;
		}
	}

	/**
	 * 创建设置网络Dialog
	 * @param context
	 * @return
	 */
	public static AlertDialog CreateNetSettingDialog(final Context context) {
		return new AlertDialog.Builder(context).setTitle("网络设置提示")
				.setIcon(android.R.drawable.stat_notify_error)
				.setMessage("网络连接不可用,是否进行设置?")
				.setPositiveButton("设置", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = null;
						// 判断手机系统的版本 ：API大于10 就是3.0或以上版本
						if (android.os.Build.VERSION.SDK_INT > 10) {
							intent = new Intent(
									android.provider.Settings.ACTION_WIRELESS_SETTINGS);
						} else {
							intent = new Intent();
							ComponentName component = new ComponentName(
									"com.android.settings",
									"com.android.settings.WirelessSettings");
							intent.setComponent(component);
							intent.setAction("android.intent.action.VIEW");
						}
						context.startActivity(intent);
					}
				}).setNegativeButton("取消", null).create();
	}

	/**
	 * 判断当前网络是否通过Wifi连接
	 * @param context
	 * @return
	 */
	public static boolean IsWifiConnect(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkINfo = cm.getActiveNetworkInfo();
		if (networkINfo != null
				&& networkINfo.getType() == ConnectivityManager.TYPE_WIFI) {
			return true;
		}
		return false;
	}
}
