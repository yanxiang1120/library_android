package com.yeyanxiang.view.drawview;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/*
 * 实现方式：
 * 采用了两层bitmap的方式：
 * 底层与表层bitmap：floorBitmap, surfaceBitmap;表层bitmap为透明色，否则会覆盖掉底层bitmap的图形
 * 当前画图东都在表层bitmap：surfaceBitmap。如果改变画笔，则将当前surfaceBitmap的内容绘制到底层bitmap：floorBitmap
 * 如果选择橡皮，则需要在底层bitmap上进行绘制，
 * 查看原图片，也是讲图片绘制到底层bitmap
 */
public class DrawView extends View {

	public static final int NOTIFY = 1001;

	private DrawBS drawBS = null;
	private Point evevtPoint;
	private Bitmap floorBitmap, surfaceBitmap;// 底层与表层bitmap
	private Canvas floorCanvas, surfaceCanvas;// bitmap对应的canvas
	private Handler handler;

	private boolean isEraser = false;
	private boolean touchup = false;

	Bitmap newbm;

	@SuppressLint("ParserError")
	public DrawView(Context context) {
		super(context);
		init();
	}

	private void init() {
		// TODO Auto-generated method stub
		// 初始化drawBS，即drawBS默认为DrawPath类
		evevtPoint = new Point();

		// 底层bitmap与canvas，
		floorBitmap = Bitmap.createBitmap(1920, 1080, Bitmap.Config.ARGB_8888);
		floorCanvas = new Canvas(floorBitmap);

		// 表面bitmap。置于底层bitmap之上，用于赋值绘制当前的所画的图形；需要设置为透明，否则覆盖底部bitmap
		surfaceBitmap = Bitmap
				.createBitmap(1920, 1080, Bitmap.Config.ARGB_8888);
		surfaceCanvas = new Canvas(surfaceBitmap);
		surfaceCanvas.drawColor(Color.TRANSPARENT);

		setDrawTool(currentdraw);
	}

	public DrawView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		init();
	}

	public DrawView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init();
	}

	public void clear() {
		floorBitmap.recycle();
		surfaceBitmap.recycle();
		init();
		invalidate();
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
	}

	@SuppressLint("WrongCall")
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		canvas.drawColor(Color.WHITE);
		// 将底层bitmap的图形绘制到主画布
		canvas.drawBitmap(floorBitmap, 0, 0, null);

		// 判断选择的图形是否为橡皮
		if (isEraser) {
			// 如果是橡皮，让画笔在底层bitmap上进行操作，
			/*
			 * 传递底层Canvas参数。 调用相应的画图工具类方法,在底层bitmap上使用floorCanvas进行绘图
			 */

			/*
			 * 拖动过程中不停的将bitmap的颜色设置为透明（清空表层bitmap） 否则整个拖动过程的轨迹都会画出来
			 */
			surfaceBitmap.eraseColor(Color.TRANSPARENT);
			drawBS.onDraw(floorCanvas, Color.BLUE);
			canvas.drawBitmap(surfaceBitmap, 0, 0, null);

		} else {
			// 如果不是橡皮，则让画笔在表层bitmap上进行操作，
			/*
			 * 传递表层Canvas参数。 调用相应画图工具类方法,在表层bitmap上使用surfaceCanvas进行绘图
			 */
			drawBS.onDraw(surfaceCanvas, DrawBS.color);
			canvas.drawBitmap(surfaceBitmap, 0, 0, null);

			if (touchup && currentdraw < 3) {
				setDrawTool(currentdraw);
			}
		}
	}

	// 触摸事件。调用相应的画图工具类进行操作
	@SuppressLint("WrongCall")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		evevtPoint.set((int) event.getX(), (int) event.getY());

		switch (event.getAction()) {

		case MotionEvent.ACTION_DOWN:
			touchup = false;
			drawBS.onTouchDown(evevtPoint);
			break;

		case MotionEvent.ACTION_MOVE:
			drawBS.onTouchMove(evevtPoint);
			surfaceBitmap.eraseColor(Color.TRANSPARENT);
			invalidate();
			break;

		case MotionEvent.ACTION_UP:
			drawBS.onTouchUp(evevtPoint);
			touchup = true;

			// 如果重新选择了图形，则需要将表层bitmap上的图像绘制到底层bitmap上进行保存
			if (handler != null) {
				Message message = new Message();
				message.what = NOTIFY;
				message.obj = drawBS.getjson();
				handler.sendMessage(message);
			}

			break;
		default:
			break;
		}
		return true;
	}

	private int currentdraw = 3;

	// 选择图形，实例化相应的类
	public void setDrawTool(int i) {
		currentdraw = i;
		isEraser = false;
		switch (i) {
		case 0:
			drawBS = new DrawLine();
			break;
		case 1:
			drawBS = new DrawRectangle();
			break;
		case 2:
			drawBS = new DrawCircle();
			break;
		case 3:
			drawBS = new DrawScrawl();
			break;
		case 10:
			isEraser = true;
			// 如果需要橡皮。则实例化重新设置画笔的构造方法
			drawBS = new DrawPath();// 橡皮
			break;
		default:
			drawBS = new DrawScrawl();
			break;
		}

		if (floorCanvas != null) {
			// 如果重新选择了图形，则需要将表层bitmap上的图像绘制到底层bitmap上进行保存
			floorCanvas.drawBitmap(surfaceBitmap, 0, 0, null);
		}
	}

	/*
	 * 颜色选择
	 */
	public void setColorTool(int color) {
		setDrawTool(currentdraw);
		DrawBS.color = color;
	}

	public void setSizeTool(int size) {
		setDrawTool(currentdraw);
		drawBS.setSize(size);
	}

	// 将图片存入内存卡
	@SuppressLint("SdCardPath")
	public boolean savePicture(String filepath, String draw_name, int alpha) {
		FileOutputStream fos = null;
		String type = null;

		if (alpha == 0) {// 不透明
			type = ".jpg";
		} else {
			type = ".png";
		}
		try {
			String strPath = new String(filepath);
			File fPath = new File(strPath);
			if (!fPath.exists()) {
				fPath.mkdir();
			}

			File f = new File(strPath + draw_name.trim() + type);
			try {
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}

			fos = new FileOutputStream(f);
			Bitmap b = null;
			destroyDrawingCache();
			setDrawingCacheEnabled(true);

			buildDrawingCache();
			b = getDrawingCache();

			if (b != null) {
				b.compress(Bitmap.CompressFormat.PNG, 100, fos);
				if (!b.isRecycled())
					b.recycle();
				b = null;
				System.gc();
				return true;
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (fos != null) {
				try {
					fos.flush();
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
					Log.e("send picture to dbserver", "关闭上传图片的数据流失败！");
				}
			}
		}

		return false;
	}

	@SuppressLint("WrongCall")
	public void draw(String json) {
		if ("clear".equals(json)) {
			clear();
		} else {

			try {
				JSONObject object = new JSONObject(json);
				String drawtype = object.getString("drawtype");
				int color = object.getInt("paintcolor");
				int size = object.getInt("paintsize");
				if (color != DrawBS.color) {
					setColorTool(color);
				}
				if (size != DrawBS.size) {
					setSizeTool(size);
				}

				if ("circle".equals(drawtype) && currentdraw != 2) {
					setDrawTool(2);
				} else if ("line".equals(drawtype) && currentdraw != 0) {
					setDrawTool(0);
				} else if ("path".equals(drawtype) && currentdraw != 10) {
					setDrawTool(10);
				} else if ("rect".equals(drawtype) && currentdraw != 1) {
					setDrawTool(1);
				} else if ("scrawl".equals(drawtype) && currentdraw != 3) {
					setDrawTool(3);
				}
				setPath(json);
				invalidate();

				if (isEraser) {
					new Handler().postDelayed(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							invalidate();
						}
					}, 10);
				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void setPath(String json) {

		// TODO Auto-generated method stub
		try {
			JSONObject object = new JSONObject(json);
			Point point = new Point();
			point.set(object.getInt("downx"), object.getInt("downy"));
			drawBS.onTouchDown(point);
			// down(object.getInt("downx"), object.getInt("downy"));
			JSONArray jsonArray = object.getJSONArray("paths");
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject pathjson = jsonArray.getJSONObject(i);
				drawBS.move(pathjson.getInt("movex"), pathjson.getInt("movey"));
			}
			drawBS.up(object.getInt("upx"), object.getInt("upy"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
