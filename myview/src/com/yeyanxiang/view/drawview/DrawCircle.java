package com.yeyanxiang.view.drawview;

import android.graphics.Canvas;
import android.graphics.Point;

/*
 * 画圆
 * 
 */
public class DrawCircle extends DrawBS {

	private Point rDotsPoint;// 圆心
	private int radius = 0;// 半径
	private Double dtance = 0.0;// 当前点到圆心的距离

	public DrawCircle() {
		// TODO Auto-generated constructor stub
		rDotsPoint = new Point();
	}

	public void onDraw(Canvas canvas, int color) {
		// TODO Auto-generated method stub
		paint.setColor(color);
		paint.setStrokeWidth(size);
		canvas.drawCircle(rDotsPoint.x, rDotsPoint.y, radius, paint);// 画圆
	}

	@Override
	public void down(int x, int y) {
		// TODO Auto-generated method stub

		downPoint.set(x, y);

		if (radius != 0) {
			// 计算当前所点击的点到圆心的距离
			dtance = Math.sqrt((downPoint.x - rDotsPoint.x)
					* (downPoint.x - rDotsPoint.x)
					+ (downPoint.y - rDotsPoint.y)
					* (downPoint.y - rDotsPoint.y));
			// 如果距离半径减20和加20个范围内，则认为用户点击在圆上
			if (dtance >= radius - 20 && dtance <= radius + 20) {
				downState = 1;
				// 如果距离小于半径，则认为用户点击在圆内
			} else if (dtance < radius) {
				downState = -1;
				// 当前点击的点在园外
			} else if (dtance > radius) {
				downState = 0;
			}
		} else {
			downState = 0;
		}

	}

	@Override
	public void move(int x, int y) {
		// TODO Auto-generated method stub
		rDotsPoint.set(downPoint.x, downPoint.y);
		radius = (int) Math.sqrt((x - rDotsPoint.x) * (x - rDotsPoint.x)
				+ (y - rDotsPoint.y) * (y - rDotsPoint.y));
	}

	@Override
	public String getType() {
		// TODO Auto-generated method stub
		return "circle";
	}

	@Override
	public void up(int x, int y) {
		// TODO Auto-generated method stub

	}

}
