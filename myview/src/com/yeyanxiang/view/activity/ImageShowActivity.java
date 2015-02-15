package com.yeyanxiang.view.activity;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import com.yeyanxiang.view.R;
import com.yeyanxiang.view.img.ZoomImageView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;

/**
 * 
 * Create on 2013-4-28 上午11:21:38 </br> Copyright: Copyright(c) 2013 by 叶雁翔</br>
 * 
 * 简介: 读取网络图片并显示
 * 
 * @IMAGEURL 网络地址
 * @TIMEOUT 超时时间
 * 
 * @Version 1.0
 * @Author <a href="mailto:yanxiang1120@126.com">叶雁翔</a>
 * 
 * 
 */
public class ImageShowActivity extends Activity {
	public static final String IMAGEURL = "IMAGEURL";
	public static final String IMAGEWIDTH = "IMAGEWIDTH";
	public static final String IMAGEHEIGHT = "IMAGEHEIGHT";
	public static final String TIMEOUT = "TIMEOUT";
	private String url;
	private ZoomImageView imageView;
	private ProgressBar progressBar;
	private Bitmap bitmap;
	// private float imagewidth = 1280;
	// private float imageheight = 1280;
	private int screenwidth = 1280;
	private int screenheight = 1280;
	private int maxwidthorheight = 1280;
	private int timeout = 5000;
	private int defaultdrawableid = R.drawable.defaultpic;
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			progressBar.setVisibility(View.GONE);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.imageshow);
		screenwidth = getWindowManager().getDefaultDisplay().getWidth();
		screenheight = getWindowManager().getDefaultDisplay().getHeight();
		maxwidthorheight = screenwidth > screenheight ? screenwidth
				: screenheight;
		init();
	}

	private void init() {
		// TODO Auto-generated method stub
		imageView = (ZoomImageView) findViewById(R.id.zoomimageview);
		progressBar = (ProgressBar) findViewById(R.id.progressBar1);
		if (getIntent() != null) {
			url = getIntent().getStringExtra(IMAGEURL);
			// imageheight = getIntent().getIntExtra(IMAGEHEIGHT, 1280);
			// imagewidth = getIntent().getIntExtra(IMAGEWIDTH, 1280);
			timeout = getIntent().getIntExtra(TIMEOUT, 10000);
			if (!TextUtils.isEmpty(url)) {
				new Thread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						Looper.prepare();
						setinternetbm(url);
						handler.post(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method
								// stub
								if (bitmap != null) {
									if (bitmap.getWidth() > bitmap.getHeight()) {
										setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
									} else {
										setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
									}
									imageView.setImageBitmap(bitmap);
								} else {
									imageView
											.setImageResource(defaultdrawableid);
								}
								progressBar.setVisibility(View.GONE);
							}
						});
						Looper.loop();
					}
				}).start();
			}
		}
	}

	private void setinternetbm(String url) {
		InputStream inputStream = null;
		HttpURLConnection connection = null;
		try {
			connection = (HttpURLConnection) new URL(url).openConnection();
			connection.setConnectTimeout(timeout);
			inputStream = connection.getInputStream();
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			options.inPreferredConfig = Bitmap.Config.ARGB_4444;
			options.inPurgeable = true;
			options.inInputShareable = true;
			BitmapFactory.decodeStream(inputStream, null, options);
			options.inJustDecodeBounds = false;
			if (options.outWidth > options.outHeight) {
				if (options.outWidth == maxwidthorheight) {
					options.inSampleSize = 1;
				} else {
					options.inSampleSize = (int) (options.outWidth / maxwidthorheight) + 1;
				}
			} else {
				if (options.outHeight == maxwidthorheight) {
					options.inSampleSize = 1;
				} else {
					options.inSampleSize = (int) (options.outHeight / maxwidthorheight) + 1;
				}
			}

			connection.disconnect();
			inputStream.close();
			connection = (HttpURLConnection) new URL(url).openConnection();
			connection.setConnectTimeout(timeout);
			inputStream = connection.getInputStream();
			bitmap = BitmapFactory.decodeStream(inputStream, null, options);

		} catch (OutOfMemoryError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			handler.sendEmptyMessage(0);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			handler.sendEmptyMessage(0);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			handler.sendEmptyMessage(0);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			handler.sendEmptyMessage(0);
		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
					inputStream = null;
				}
				if (connection != null) {
					connection.disconnect();
					connection = null;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (bitmap != null) {
			bitmap.recycle();
			System.gc();
		}
	}

}
