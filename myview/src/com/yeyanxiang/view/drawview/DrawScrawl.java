package com.yeyanxiang.view.drawview;

import android.graphics.Canvas;
import android.graphics.Path;

/*
 * 涂鸦
 */
public class DrawScrawl extends DrawBS {

	private Path path = new Path();
	private float mX, mY;

	public DrawScrawl() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public void down(int x, int y) {
		path.moveTo(x, y);
		mX = x;
		mY = y;
	}

	@Override
	public void move(int x, int y) {
		float dx = Math.abs(x - mX);
		float dy = Math.abs(y - mY);
		if (dx > 0 || dy > 0) {
			path.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
			mX = x;
			mY = y;
		} else if (dx == 0 || dy == 0) {
			path.quadTo(mX, mY, (x + 1 + mX) / 2, (y + 1 + mY) / 2);
			mX = x + 1;
			mY = y + 1;
		}

	}

	public void onDraw(Canvas canvas, int color) {
		paint.setColor(color);
		paint.setStrokeWidth(size);
		canvas.drawPath(path, paint);
	}

	@Override
	public String getType() {
		// TODO Auto-generated method stub
		return "scrawl";
	}

	@Override
	public void up(int x, int y) {
		// TODO Auto-generated method stub

	}

}
