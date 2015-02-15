package com.yeyanxiang.view.drawview;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

public class DrawPath extends DrawBS {

	private Path path = new Path();
	private float mX, mY;

	// 如果选择橡皮，则需要给画笔重新赋值
	public DrawPath() {

		paint.setAntiAlias(true);
		paint.setDither(true);
		paint.setColor(Color.BLUE);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeJoin(Paint.Join.ROUND);
		paint.setStrokeCap(Paint.Cap.SQUARE);
		paint.setStrokeWidth(size * 2);
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

	}

	public void onDraw(Canvas canvas, int color) {
		// TODO Auto-generated method stub
		paint.setColor(color);
		paint.setStrokeWidth(size);
		canvas.drawPath(path, paint);
	}

	@Override
	public void down(int x, int y) {
		// TODO Auto-generated method stub

		path.moveTo(x, y);
		mX = x;
		mY = y;

	}

	@Override
	public void move(int x, int y) {
		// TODO Auto-generated method stub

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

	@Override
	public void up(int x, int y) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getType() {
		// TODO Auto-generated method stub
		return "path";
	}

}
