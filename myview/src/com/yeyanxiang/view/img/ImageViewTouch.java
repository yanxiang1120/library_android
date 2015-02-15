package com.yeyanxiang.view.img;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.Transformation;
import android.widget.ImageView;

/**
 * @author 叶雁翔
 * 
 * @Email yanxiang1120@gmail.com
 * 
 * @version 1.0
 * 
 * @update 2014年3月13日
 * 
 * @简介
 */
public class ImageViewTouch extends ImageView {

	@SuppressWarnings("unused")
	private static final String TAG = "ImageViewTouchBase";

	// This is the base transformation which is used to show the image
	// initially. The current computation for this shows the image in
	// it's entirety, letterboxing as needed. One could choose to
	// show the image as cropped instead.
	//
	// This matrix is recomputed when we go from the thumbnail image to
	// the full size image.
	protected Matrix mBaseMatrix = new Matrix();

	// This is the supplementary transformation which reflects what
	// the user has done in terms of zooming and panning.
	//
	// This matrix remains the same when we go from the thumbnail image
	// to the full size image.
	protected Matrix mSuppMatrix = new Matrix();

	// This is the final matrix which is computed as the concatentation
	// of the base matrix and the supplementary matrix.
	protected final Matrix mDisplayMatrix = new Matrix();

	// Temporary buffer used for getting the values out of a matrix.
	private final float[] mMatrixValues = new float[9];

	int mThisWidth = -1, mThisHeight = -1;
	private Bitmap mBitmap;
	public float mMaxZoom;
	public float mMinZoom;
	public float mBaseZoom;

	// ImageViewTouchBase will pass a Bitmap to the Recycler if it has finished
	// its use of that Bitmap.
	public interface Recycler {
		public void recycle(Bitmap b);
	}

	public void setRecycler(Recycler r) {
		mRecycler = r;
	}

	private Recycler mRecycler;

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		mThisWidth = right - left;
		mThisHeight = bottom - top;
		mMaxZoom = maxZoom();
		mMinZoom = minZoom();
		Runnable r = mOnLayoutRunnable;
		if (r != null) {
			mOnLayoutRunnable = null;
			r.run();
		}
		if (mBitmap != null) {
			getProperBaseMatrix(mBitmap, mBaseMatrix);
			setImageMatrix(getImageViewMatrix());
		}
	}

	public Bitmap getBitmap() {
		return mBitmap;
	}

	protected Handler mHandler = new Handler();

	@Override
	public void setImageBitmap(Bitmap bitmap) {
		super.setImageBitmap(bitmap);
		mBitmap = bitmap;
		setImageRotateBitmapResetBase(true);
	}

	@Override
	public void setImageResource(int resId) {
		// TODO Auto-generated method stub
		super.setImageResource(resId);
		mBitmap = ((BitmapDrawable) getDrawable()).getBitmap();
		setImageRotateBitmapResetBase(true);
	}

	public void clear() {
		setImageRotateBitmapResetBase(true);
	}

	private Runnable mOnLayoutRunnable = null;

	public void setImageRotateBitmapResetBase(final boolean resetSupp) {
		if (mBitmap != null) {
			getProperBaseMatrix(mBitmap, mBaseMatrix);
		} else {
			mBaseMatrix.reset();
		}

		if (resetSupp) {
			mSuppMatrix.reset();
		}
		setImageMatrix(getImageViewMatrix());
		mBaseZoom = getScale(mBaseMatrix);
		center(true, true);
	}

	public void center(boolean horizontal, boolean vertical) {
		centerCharge(horizontal, vertical, false);
	}

	private void centerCharge(boolean horizontal, boolean vertical,
			boolean hasAni) {
		if (mBitmap == null) {
			return;
		}

		Matrix m = getImageViewMatrix();

		if (mBitmap != null) {
			RectF rect = new RectF(0, 0, mBitmap.getWidth(),
					mBitmap.getHeight());

			m.mapRect(rect);

			float height = rect.height();
			float width = rect.width();

			float deltaX = 0, deltaY = 0;

			if (vertical) {
				int viewHeight = getHeight();
				if (height < viewHeight) {
					deltaY = (viewHeight - height) / 2 - rect.top;
				} else if (rect.top > 0) {
					deltaY = -rect.top;
				} else if (rect.bottom < viewHeight) {
					deltaY = getHeight() - rect.bottom;
				}
			}

			if (horizontal) {
				int viewWidth = getWidth();
				if (width < viewWidth) {
					deltaX = (viewWidth - width) / 2 - rect.left;
				} else if (rect.left > 0) {
					deltaX = -rect.left;
				} else if (rect.right < viewWidth) {
					deltaX = viewWidth - rect.right;
				}
			}
			postTranslate(deltaX, deltaY);
		}

		if (!hasAni) {
			setImageMatrix(getImageViewMatrix());
		}
	}

	protected void centerWithAni(boolean horizontal, boolean vertical) {
		centerCharge(horizontal, vertical, true);
	}

	public ImageViewTouch(Context context) {
		super(context);
		init();
	}

	public ImageViewTouch(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		setScaleType(ImageView.ScaleType.MATRIX);
	}

	protected float getValue(Matrix matrix, int whichValue) {
		matrix.getValues(mMatrixValues);
		return mMatrixValues[whichValue];
	}

	// Get the scale factor out of the matrix.
	protected float getScale(Matrix matrix) {
		return getValue(matrix, Matrix.MSCALE_X);
	}

	public float getScale() {
		return getScale(mSuppMatrix);
	}

	// Setup the base matrix so that the image is centered and scaled properly.
	private void getProperBaseMatrix(Bitmap bitmap, Matrix matrix) {
		float viewWidth = getWidth();
		float viewHeight = getHeight();

		float w = bitmap.getWidth();
		float h = bitmap.getHeight();
		matrix.reset();

		// We limit up-scaling to 3x otherwise the result may look bad if it's
		// a small icon.
		float widthScale = Math.min(viewWidth / w, 3.0f);
		float heightScale = Math.min(viewHeight / h, 3.0f);

		// float widthScale = viewWidth / w;
		// float heightScale = viewHeight / h;
		float scale = Math.min(widthScale, heightScale);
		// Log.d(TAG, "scale: " + scale);

		// if (scale < 1F) {
		matrix.postScale(scale, scale);

		matrix.postTranslate((viewWidth - w * scale) / 2F, (viewHeight - h
				* scale) / 2F);
		// } else {
		// matrix.postTranslate((viewWidth - w) / 2F, (viewHeight - h) / 2F);
		// }
	}

	// Combine the base matrix and the supp matrix to make the final matrix.
	public Matrix getImageViewMatrix() {
		// The final matrix is computed as the concatentation of the base matrix
		// and the supplementary matrix.
		mDisplayMatrix.set(mBaseMatrix);
		mDisplayMatrix.postConcat(mSuppMatrix);
		return mDisplayMatrix;
	}

	static final float SCALE_RATE = 1.25F;

	// Sets the maximum zoom, which is a scale relative to the base matrix. It
	// is calculated to show the image at 400% zoom regardless of screen or
	// image orientation. If in the future we decode the full 3 megapixel image,
	// rather than the current 1024x768, this should be changed down to 200%.
	protected float maxZoom() {
		if (mBitmap == null) {
			return 1F;
		}

		float fw = (float) mBitmap.getWidth() / (float) mThisWidth;
		float fh = (float) mBitmap.getHeight() / (float) mThisHeight;
		float max = Math.max(fw, fh) * 4;
		return max;
	}

	protected float minZoom() {
		float baseScale = getScale(mBaseMatrix);
		if (baseScale < 1) {
			return 1F;
		} else {
			return 1F / baseScale;
		}
	}

	protected void zoomTo(float scale, float centerX, float centerY) {
		if (scale > mMaxZoom) {
			scale = mMaxZoom;
		}

		float oldScale = getScale();
		float deltaScale = scale / oldScale;

		mSuppMatrix.postScale(deltaScale, deltaScale, centerX, centerY);
		setImageMatrix(getImageViewMatrix());
		center(true, true);
	}

	protected void zoomTo(final float scale, final float centerX,
			final float centerY, final float durationMs) {
		final float incrementPerMs = (scale - getScale()) / durationMs;
		final float oldScale = getScale();
		final long startTime = System.currentTimeMillis();

		mHandler.post(new Runnable() {
			public void run() {
				long now = System.currentTimeMillis();
				float currentMs = Math.min(durationMs, now - startTime);
				float target = oldScale + (incrementPerMs * currentMs);
				zoomTo(target, centerX, centerY);

				if (currentMs < durationMs) {
					mHandler.post(this);
				}
			}
		});
	}

	public void zoomTo(float scale) {
		float cx = getWidth() / 2F;
		float cy = getHeight() / 2F;

		zoomTo(scale, cx, cy);
	}

	public void zoomToPoint(float scale, float pointX, float pointY) {
		float cx = getWidth() / 2F;
		float cy = getHeight() / 2F;

		panBy(cx - pointX, cy - pointY);
		zoomTo(scale, cx, cy);
	}

	public void zoomToNoCenter(float scale, float centerX, float centerY) {
		float oldScale = getScale();
		float deltaScale = scale / oldScale;

		mSuppMatrix.postScale(deltaScale, deltaScale, centerX, centerY);
		setImageMatrix(getImageViewMatrix());
	}

	public class MatrixTransformAnimation extends Animation {
		Matrix mFrom;
		Matrix mTo;

		public MatrixTransformAnimation(Matrix from, Matrix to) {
			mFrom = from;
			mTo = to;
		}

		@Override
		protected void applyTransformation(float interpolatedTime,
				Transformation t) {
			Matrix matrix = t.getMatrix();
			// matrix.set(mMatrix);
			float from = getValue(mFrom, Matrix.MSCALE_X);
			float to = getValue(mTo, Matrix.MSCALE_X);
			matrix.setScale(from / to, from / to);
		}
	}

	public void zoomToNoCenterWithAni(float scale, float toScale,
			float centerX, float centerY) {
		ScaleAnimation animation = new ScaleAnimation(scale, toScale, scale,
				toScale, centerX, centerY);
		animation.setDuration(300);
		startAnimation(animation);
	}

	public void zoomIn() {
		zoomIn(SCALE_RATE);
	}

	public void zoomOut() {
		zoomOut(SCALE_RATE);
	}

	protected void zoomIn(float rate) {
		if (getScale() >= mMaxZoom) {
			return; // Don't let the user zoom into the molecular level.
		}
		if (mBitmap == null) {
			return;
		}

		float cx = getWidth() / 2F;
		float cy = getHeight() / 2F;

		mSuppMatrix.postScale(rate, rate, cx, cy);
		setImageMatrix(getImageViewMatrix());
	}

	protected void zoomOut(float rate) {
		if (mBitmap == null) {
			return;
		}

		float cx = getWidth() / 2F;
		float cy = getHeight() / 2F;

		// Zoom out to at most 1x.
		Matrix tmp = new Matrix(mSuppMatrix);
		tmp.postScale(1F / rate, 1F / rate, cx, cy);

		if (getScale(tmp) < mMinZoom) {
			mSuppMatrix.setScale(mMinZoom, mMinZoom, cx, cy);
		}
		// if (getScale(tmp) < 1F) {
		// mSuppMatrix.setScale(1F, 1F, cx, cy);
		// }
		else {
			mSuppMatrix.postScale(1F / rate, 1F / rate, cx, cy);
		}
		setImageMatrix(getImageViewMatrix());
		center(true, true);
	}

	protected void postTranslate(float dx, float dy) {
		mSuppMatrix.postTranslate(dx, dy);
	}

	public void panBy(float dx, float dy) {
		postTranslate(dx, dy);
		setImageMatrix(getImageViewMatrix());
	}
}
