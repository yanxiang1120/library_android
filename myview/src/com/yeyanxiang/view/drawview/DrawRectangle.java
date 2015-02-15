package com.yeyanxiang.view.drawview;

import java.util.ArrayList;

import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;

/*
 * 矩形
 * 
 * 思路：
 * 1、画矩形
 * 2、将矩形左上角的顶点始终设置为point1点
 * 矩形右上角的顶点始终设置为point2点
 * 3、移动、拉伸后，执行步骤2
 */
public class DrawRectangle extends DrawBS {

	private Point point1, point2, point3, point4;
	private Rect rect;
	private Rect point1Rect, point2Rect, point3Rect, point4Rect;

	private PathUtil downPathUtil;
	private ArrayList<PathUtil> pathUtils;

	public DrawRectangle() {
		point1 = new Point();
		point2 = new Point();
		point3 = new Point();
		point4 = new Point();
		new Point();
		rect = new Rect();

		downPathUtil = new PathUtil();
		pathUtils = new ArrayList<PathUtil>();
	}

	@Override
	public void down(int x, int y) {
		downPathUtil.setDownX(x);
		downPathUtil.setDownY(y);
		downPoint.set(x, y);

		/*
		 * 判断以矩形顶点point2为中心的小矩形point2Rect是否为空，
		 * 
		 * 为什么要判断point2Rect而不是point1Rect？
		 * 如果用户在当前页面只点击一下，也会产生point1Rect而不会产生point2Rect。
		 * 只有point1Rect而没有point2Rect判断是没有意义的 而如果point2Rect !=
		 * null，则说明当前页面已经有画好的矩形了，可见进行判断用户多点击的点和矩形的关系
		 */
		if (point2Rect != null) {
			// 判断用户所点击的点是否在矩形顶点point1为中心的矩形point1Rect内，
			// 如果在这个矩形内，则我们认为用户点击了该点
			if (point1Rect.contains(downPoint.x, downPoint.y)) {
				downState = 1;
				// Log.i("onTouchDown", "downState = 1");
			} else if (point2Rect.contains(downPoint.x, downPoint.y)) {
				downState = 2;
				// Log.i("onTouchDown", "downState = 2");
			} else if (point3Rect.contains(downPoint.x, downPoint.y)) {
				downState = 3;
				// Log.i("onTouchDown", "downState = 3");
			} else if (point4Rect.contains(downPoint.x, downPoint.y)) {
				downState = 4;
				// Log.i("onTouchDown", "downState = 4");
			} else if (rect.contains(downPoint.x, downPoint.y)) {
				downState = 5;
				// Log.i("onTouchDown", "downState = 5");
			} else {
				downState = 0;
				// Log.i("onTouchDown", "downState = 0");
			}
		}

	}

	@Override
	public void move(int x, int y) {

		movePoint.set(x, y);

		// 根据用户所点击的坐标点画相应的矩形
		switch (downState) {
		case 1:
			// 点击点point1，则point2不变；根据point1、point2重新设置点point3,point4
			point1.set(x, y);
			point3.set(point1.x, point2.y);
			point4.set(point2.x, point1.y);
			moveState = 1;
			break;
		case 2:
			// 点击点point2，则point1不变；根据point1、point2重新设置点point3,point4
			point2.set(x, y);
			point3.set(point1.x, point2.y);
			point4.set(point2.x, point1.y);
			moveState = 2;
			break;
		case 3:
			// 点击点point3，则重新设置矩形点point3、1、2
			point3.set(x, y);
			point1.set(point3.x, point4.y);
			point2.set(point4.x, point3.y);
			moveState = 3;
			break;
		case 4:
			// 点击点point4，则重新设置矩形点point4、1、2
			point4.set(x, y);
			point1.set(point3.x, point4.y);
			point2.set(point4.x, point3.y);
			moveState = 4;
			break;
		default:
			getStartPoint();
			moveState = 0;
			break;
		}
		// 每次拖动完之后需要重新设定4个顶点小矩形。
		setRect();

	}

	// 设置矩形的开始点与结束点pont1/point2
	public void getStartPoint() {

		if (downPoint.x < movePoint.x && downPoint.y < movePoint.y) {
			point1.set(downPoint.x, downPoint.y);
			point2.set(movePoint.x, movePoint.y);
			point3.set(point1.x, point2.y);
			point4.set(point2.x, point1.y);
		} else if (downPoint.x < movePoint.x && downPoint.y > movePoint.y) {
			point3.set(downPoint.x, downPoint.y);
			point4.set(movePoint.x, movePoint.y);
			point1.set(point3.x, point4.y);
			point2.set(point4.x, point3.y);
		} else if (downPoint.x > movePoint.x && downPoint.y > movePoint.y) {
			point2.set(downPoint.x, downPoint.y);
			point1.set(movePoint.x, movePoint.y);
			point3.set(point1.x, point2.y);
			point4.set(point2.x, point1.y);
		} else if (downPoint.x > movePoint.x && downPoint.y < movePoint.y) {
			point4.set(downPoint.x, downPoint.y);
			point3.set(movePoint.x, movePoint.y);
			point1.set(point3.x, point4.y);
			point2.set(point4.x, point3.y);
		}

		setRect();

	}

	private int offect = 30;

	public void setRect() {
		// 设置以矩形的4个顶点为中心的小矩形
		point1Rect = new Rect(point1.x - offect, point1.y - offect, point1.x
				+ offect, point1.y + offect);
		point2Rect = new Rect(point2.x - offect, point2.y - offect, point2.x
				+ offect, point2.y + offect);
		point3Rect = new Rect(point3.x - offect, point3.y - offect, point3.x
				+ offect, point3.y + offect);
		point4Rect = new Rect(point4.x - offect, point4.y - offect, point4.x
				+ offect, point4.y + offect);

		rect.set(point1.x, point1.y, point2.x, point2.y);

	}

	public void onDraw(Canvas canvas, int color) {
		paint.setColor(color);
		paint.setStrokeWidth(size);
		canvas.drawRect(rect, paint);// 画矩形
	}

	@Override
	public String getjson() {
		// TODO Auto-generated method stub
		return super.getjson();
	}

	@Override
	public String getType() {
		// TODO Auto-generated method stub
		return "rect";
	}

	@Override
	public void up(int x, int y) {
		// TODO Auto-generated method stub

	}

}
