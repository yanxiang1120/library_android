package com.yeyanxiang.view.img;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.graphics.Shader.TileMode;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * 
 * Create on 2013-4-28 上午11:32:43 </br> Copyright: Copyright(c) 2013 by 叶雁翔</br>
 * 
 * 简介: ImageView 添加倒影效果
 * 
 * @Version 1.0
 * @Author <a href="mailto:yanxiang1120@126.com">叶雁翔</a>
 * 
 * 
 */
public class ReflectImageView extends ImageView {

	private Bitmap originalBitmap;

	public ReflectImageView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ReflectImageView(Context context) {
		this(context, null, 0);
	}

	public ReflectImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// try {
		// DoReflection(((BitmapDrawable) getDrawable()).getBitmap());
		// // DoReflection(getDrawingCache());
		// } catch (Exception e) {
		// // TODO: handle exception
		// }
	}

	@Override
	public void setImageBitmap(Bitmap bm) {
		DoReflection(bm);
	}

	/** 显示倒影效果的setImageBitmap函数 */
	public void setImageBitmap(Bitmap bm, boolean isFlected) {
		if (isFlected) {
			super.setImageBitmap(bm);
		}
	}

	// @Override
	// public void setImageResource(int resId) {
	//
	// originalBitmap = BitmapFactory.decodeResource(getResources(), resId);
	// DoReflection(originalBitmap);
	// }

	private void DoReflection(Bitmap bitmap) {
		if (bitmap == null) {
			System.out.println("bitmap为空");
		} else {
			final int reflectionGap = 0;
			int width = bitmap.getWidth();
			int height = bitmap.getHeight();
			Matrix matrix = new Matrix();
			matrix.preScale(1, -1);
			Bitmap reflectionImage = Bitmap.createBitmap(bitmap, 0,
					height * 2 / 3, width, height / 3, matrix, false);
			Bitmap bitmap4Reflection = Bitmap.createBitmap(width,
					(height + height / 3), Config.ARGB_8888);
			Canvas canvasRef = new Canvas(bitmap4Reflection);
			Paint deafaultPaint = new Paint();
			deafaultPaint.setAntiAlias(true);
			canvasRef.drawBitmap(bitmap, 0, 0, null);
			canvasRef.drawRect(0, height, width, height + reflectionGap,
					deafaultPaint);
			canvasRef.drawBitmap(reflectionImage, 0, height + reflectionGap,
					null);
			Paint paint = new Paint();
			LinearGradient shader = new LinearGradient(0, bitmap.getHeight(),
					0, bitmap4Reflection.getHeight() + reflectionGap,
					0x70ffffff, 0x00ffffff, TileMode.CLAMP);
			paint.setShader(shader);
			paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
			canvasRef.drawRect(0, height, width, bitmap4Reflection.getHeight()
					+ reflectionGap, paint);
			this.setImageBitmap(bitmap4Reflection, true);
		}
	}
}