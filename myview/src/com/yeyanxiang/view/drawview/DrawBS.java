package com.yeyanxiang.view.drawview;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Paint.Style;

/*
 * 主view类。
 * 该类类似于一个接口，子类都继承实现了主类的这些方法
 */
public abstract class DrawBS {

	public int downState;
	public int moveState;
	public Point downPoint = new Point();
	public Point movePoint = new Point();
	public Point eventPoint = new Point();
	public Paint paint;// 声明画笔
	public static int color = Color.BLACK;
	public static int size = 5;

	private PathUtil poingdownutil;
	private ArrayList<PathUtil> pathUtils;

	public DrawBS() {
		// 设置画笔
		paint = new Paint();
		paint.setStyle(Style.STROKE);// 设置非填充
		paint.setStrokeWidth(size);// 笔宽5像素
		paint.setColor(color);// 默认设置为红笔
		paint.setAntiAlias(true);// 锯齿不显示

		poingdownutil = new PathUtil();
		pathUtils = new ArrayList<PathUtil>();
	}

	public void setSize(int size) {
		this.size = size;
		paint.setStrokeWidth(size);
	}

	public void onTouchDown(Point point) {
		pathUtils.clear();
		poingdownutil.setDownX(point.x);
		poingdownutil.setDownY(point.y);

		down(point.x, point.y);
	}

	public void onTouchMove(Point point) {
		pathUtils.add(new PathUtil(point.x, point.y));
		move(point.x, point.y);
	}

	public void onTouchUp(Point point) {
		poingdownutil.setUpx(point.x);
		poingdownutil.setUpy(point.y);
		up(point.x, point.y);
	}

	public abstract void down(int x, int y);

	public abstract void move(int x, int y);

	public abstract void up(int x, int y);

	public abstract void onDraw(Canvas canvas, int color);

	public abstract String getType();

	/*
	 * 设置颜色
	 */
	public void setColor(int color) {
		DrawBS.color = color;
	}

	public String getjson() {
		try {
			JSONObject pathobject = new JSONObject();
			pathobject.put("drawtype", getType());
			pathobject.put("downx", poingdownutil.getDownX());
			pathobject.put("downy", poingdownutil.getDownY());
			pathobject.put("upx", poingdownutil.getUpx());
			pathobject.put("upy", poingdownutil.getUpy());
			pathobject.put("paintcolor", color);
			pathobject.put("paintsize", size);
			JSONArray jsonArray = new JSONArray();
			for (PathUtil pathUtil : pathUtils) {
				JSONObject path = new JSONObject();
				path.put("movex", pathUtil.getMovex());
				path.put("movey", pathUtil.getMovey());
				jsonArray.put(path);
			}
			pathobject.put("paths", jsonArray);
			return pathobject.toString();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
