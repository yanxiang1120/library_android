package com.yeyanxiang.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.widget.Button;

public class PortraitButton extends Button {
	private final float offset = 5;

	private float textSize;
	private Paint textPaint;
	private float paddingLeft;
	private float paddingRight;
	private float paddingTop;
	private float paddingBottom;
	private float textheight;
	private float textwidth;

	public PortraitButton(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		paddingLeft = 0;
		paddingRight = 0;
		textPaint = new Paint();
		textPaint.setAntiAlias(true);
		Rect rect = new Rect();
		textPaint.getTextBounds("豆", 0, 1, rect);
		textheight = rect.height() + offset;
		textwidth = rect.width() + offset;
	}

	@Override
	public void setPadding(int left, int top, int right, int bottom) {
		// TODO Auto-generated method stub
		paddingLeft = left;
		paddingRight = right;
		paddingBottom = bottom;
		paddingTop = top;
		super.setPadding(left, top, right, bottom);
	}

	@Override
	public void setTextSize(float size) {
		// TODO Auto-generated method stub
		textSize = size;
		textPaint.setTextSize(size);
		Rect rect = new Rect();
		textPaint.getTextBounds("豆", 0, 1, rect);
		textheight = rect.height() + offset;
		textwidth = rect.width() + offset;
		super.setTextSize(size);
	}

	@Override
	public void setTextColor(ColorStateList colors) {
		// TODO Auto-generated method stub
		super.setTextColor(colors);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		String text = getText().toString();
		char[] textCharArray = text.toCharArray();
		int maxcount = (int) (getHeight() / textheight);

		if (textCharArray.length > maxcount) {
			paddingLeft = paddingRight = (getWidth() - textwidth * 2 - offset) / 2;
			paddingTop = paddingBottom = (getHeight() - textheight * maxcount - offset) / 2;
		} else {
			paddingLeft = paddingRight = (getWidth() - textwidth - offset) / 2;
			paddingTop = paddingBottom = (getHeight() - textheight
					* textCharArray.length - offset) / 2;
		}

		float left = paddingLeft;
		float top = paddingTop + textheight;
		Rect rect = new Rect();
		for (int i = 0; i < textCharArray.length; i++) {
			if (i == maxcount * 2 - 1) {
				textPaint.getTextBounds(".", 0, 1, rect);
				float x = left + textwidth + (textwidth - rect.width()) / 2;
				float y = top + (maxcount - 2) * textheight;
				canvas.drawText(".", x, y + 4 * rect.height(), textPaint);
				canvas.drawText(".", x, y + 6 * rect.height(), textPaint);
				canvas.drawText(".", x, y + 8 * rect.height(), textPaint);
				break;
			}
			textPaint.getTextBounds(textCharArray, i, 1, rect);
			canvas.drawText(textCharArray, i, 1, (i + 1 > maxcount ? left
					+ textwidth : left)
					+ (textwidth - rect.width()) / 2, top + (i % maxcount)
					* textheight, textPaint);
		}
	}
}
