package com.yeyanxiang.view.drawview;

import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;

/*
 * 
 * 画直线
 */
public class DrawLine extends DrawBS {

	private Point lPoint1, lPoint2;// 直线的起点与终点
	private Rect lPoint1Rect, lPoint2Rect;// 以直线起点和终点为中心的矩形

	public DrawLine() {
		new Point();
		lPoint1 = new Point();
		lPoint2 = new Point();
		lPoint1Rect = new Rect();
		lPoint2Rect = new Rect();
	}

	/*
	 * 判断当前所点击的点是否在直线上
	 * 
	 * 根据用户所点击的点到线段两个端点的距离之和 与线段的距离进行比较 来判断
	 */
	public boolean panduan(Point point) {

		double lDis = Math.sqrt((lPoint1.x - lPoint2.x)
				* (lPoint1.x - lPoint2.x) + (lPoint1.y - lPoint2.y)
				* (lPoint1.y - lPoint2.y));

		double lDis1 = Math.sqrt((point.x - lPoint1.x) * (point.x - lPoint1.x)
				+ (point.y - lPoint1.y) * (point.y - lPoint1.y));
		double lDis2 = Math.sqrt((point.x - lPoint2.x) * (point.x - lPoint2.x)
				+ (point.y - lPoint2.y) * (point.y - lPoint2.y));

		if (lDis1 + lDis2 >= lDis + 0.00f && lDis1 + lDis2 <= lDis + 5.00f) {
			return true;
		} else {
			return false;
		}
	}

	public void onDraw(Canvas canvas, int color) {
		paint.setColor(color);
		paint.setStrokeWidth(size);
		// 画直线
		canvas.drawLine(lPoint1.x, lPoint1.y, lPoint2.x, lPoint2.y, paint);
	}

	@Override
	public void down(int x, int y) {
		// TODO Auto-generated method stub

		downPoint.set(x, y);

		// 如果点击起点
		if (lPoint1Rect.contains(x, y)) {
			Log.i("onTouchDown", "downState = 1");
			downState = 1;
			// 如果点击终点
		} else if (lPoint2Rect.contains(x, y)) {
			Log.i("onTouchDown", "downState = 2");
			downState = 2;
			// 在直线外
		} else {
			Log.i("onTouchDown", "downState = 0");
			downState = 0;
		}

	}

	@Override
	public void move(int x, int y) {
		// TODO Auto-generated method stub

		switch (downState) {
		// 如果拖动直线起点，则直线的终点不变
		case 1:
			lPoint1.set(x, y);
			moveState = 1;
			break;
		// 如果拖动直线终点，则直线的起点不变
		case 2:
			lPoint2.set(x, y);
			moveState = 2;
			break;
		// 如果不在直线上，则重新画直线
		default:
			lPoint1.set(downPoint.x, downPoint.y);
			lPoint2.set(x, y);
			break;
		}

	}

	@Override
	public String getType() {
		// TODO Auto-generated method stub
		return "line";
	}

	@Override
	public void up(int x, int y) {
		// TODO Auto-generated method stub

		// 重新设定直线起点和终点为中心的矩形
		lPoint1Rect.set(lPoint1.x - 25, lPoint1.y - 25, lPoint1.x + 25,
				lPoint1.y + 25);
		lPoint2Rect.set(lPoint2.x - 25, lPoint2.y - 25, lPoint2.x + 25,
				lPoint2.y + 25);

	}

}
