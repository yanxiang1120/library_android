package com.yeyanxiang.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.view.View;

/**
 * @author 叶雁翔
 * 
 * @Email yanxiang1120@gmail.com
 * 
 * @version 1.0
 * 
 * @data 2013年12月5日 下午1:45:17
 * 
 * @简介
 */
public class CircleProgressbar extends View {

	// 画实心圆的画笔
	private Paint CirclePaint;
	// 画圆环的画笔
	private Paint RingPaint;
	// 画字体的画笔
	private TextPaint textPaint;
	// 圆形颜色
	private int CircleColor = Color.parseColor("#323a45");
	// 圆环颜色
	private int RingColor = Color.parseColor("#14b9d6");
	// 半径
	private float Radius = 100;
	// 圆环半径
	private float RingRadius = 85;
	// 圆环宽度
	private float StrokeWidth = 10;
	// 圆心x坐标
	private int XCenter;
	// 圆心y坐标
	private int YCenter;
	// 字的长度
	private int textwidth = 0;
	// 总进度
	private int TotalProgress = 100;
	// 当前进度
	private int Progress = 0;

	private float Offset = 2;

	private float TextSize = 20;

	private StaticLayout textprogressLayout;

	private int textColor = Color.WHITE;

	public CircleProgressbar(Context context, AttributeSet attrs) {
		super(context, attrs);
		// 获取自定义的属性
		initVariable();
	}

	private void initVariable() {
		CirclePaint = new Paint();
		CirclePaint.setAntiAlias(true);
		CirclePaint.setColor(CircleColor);
		CirclePaint.setStyle(Paint.Style.FILL);

		RingPaint = new Paint();
		RingPaint.setAntiAlias(true);
		RingPaint.setColor(RingColor);
		RingPaint.setStyle(Paint.Style.STROKE);
		RingPaint.setStrokeWidth(StrokeWidth);

		textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG
				| Paint.FAKE_BOLD_TEXT_FLAG);
		textPaint.setColor(textColor);
		textPaint.setTextSize(TextSize);
		textwidth = (int) FloatMath.ceil(Layout.getDesiredWidth("100%",
				textPaint));
	}

	@Override
	protected void onDraw(Canvas canvas) {

		XCenter = getWidth() / 2;
		YCenter = getHeight() / 2;

		canvas.drawCircle(XCenter, YCenter, Radius, CirclePaint);

		if (Progress >= 0) {
			RectF oval = new RectF();
			oval.left = (XCenter - RingRadius);
			oval.top = (YCenter - RingRadius);
			oval.right = RingRadius * 2 + (XCenter - RingRadius);
			oval.bottom = RingRadius * 2 + (YCenter - RingRadius);
			canvas.drawArc(oval, -90, ((float) Progress / TotalProgress) * 360,
					false, RingPaint); //
			textPaint.drawableState = getDrawableState();
			textprogressLayout = new StaticLayout(Progress + "%", textPaint,
					textwidth, Layout.Alignment.ALIGN_CENTER, 1, 0, false);
			canvas.save();
			canvas.translate(XCenter - textwidth / 2, YCenter
					- textprogressLayout.getHeight() / 2);
			textprogressLayout.draw(canvas);
			canvas.restore();
		}
	}

	/**
	 * 设置字体颜色
	 * 
	 * @param mTextSize
	 */
	public int getTextColor() {
		return textColor;
	}

	public void setTextColor(int textColor) {
		this.textColor = textColor;
	}

	/**
	 * 设置字体大小
	 * 
	 * @param mTextSize
	 */
	public void setTextSize(float TextSize) {
		this.TextSize = TextSize;
		textPaint.setTextSize(TextSize);
		textwidth = (int) FloatMath.ceil(Layout.getDesiredWidth("100%",
				textPaint));
	}

	public int getCircleColor() {
		return CircleColor;
	}

	/**
	 * 设置圆形颜色
	 * 
	 * @param CircleColor
	 */
	public void setCircleColor(int CircleColor) {
		this.CircleColor = CircleColor;
		CirclePaint.setColor(CircleColor);
	}

	public int getRingColor() {
		return RingColor;
	}

	/**
	 * 设置圆环颜色
	 * 
	 * @param RingColor
	 */
	public void setRingColor(int RingColor) {
		this.RingColor = RingColor;
		RingPaint.setColor(RingColor);
	}

	public float getRadius() {
		return Radius;
	}

	/**
	 * 设置半径
	 * 
	 * @param Radius
	 */
	public void setRadius(float Radius) {
		this.Radius = Radius;
		RingRadius = Radius - StrokeWidth - Offset;
		setTextSize(Radius / 2);
	}

	public float getStrokeWidth() {
		return StrokeWidth;
	}

	/**
	 * 设置圆环宽度
	 * 
	 * @param StrokeWidth
	 */
	public void setStrokeWidth(float StrokeWidth) {
		Offset = StrokeWidth / 5;
		this.StrokeWidth = StrokeWidth;
		RingPaint.setStrokeWidth(StrokeWidth);
		RingRadius = Radius - StrokeWidth - Offset;
	}

	public float getOffset() {
		return Offset;
	}

	public void setOffset(float offset) {
		Offset = offset;
	}

	public int getTotalProgress() {
		return TotalProgress;
	}

	/**
	 * 设置总进度
	 * 
	 * @param TotalProgress
	 */
	public void setTotalProgress(int TotalProgress) {
		this.TotalProgress = TotalProgress;
	}

	public int getProgress() {
		return Progress;
	}

	public void setProgress(int Progress) {
		this.Progress = Progress;
		postInvalidate();
	}
}
