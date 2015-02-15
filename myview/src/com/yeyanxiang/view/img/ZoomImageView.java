package com.yeyanxiang.view.img;

import java.util.Observable;
import java.util.Observer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

/**
 * @author 叶雁翔
 * 
 * @Email yanxiang1120@gmail.com
 * 
 * @version 1.0
 * 
 * @data 2014年2月20日
 * 
 * @简介
 */
public class ZoomImageView extends ImageView implements Observer,
		View.OnTouchListener {

	private final Paint mPaint = new Paint(Paint.FILTER_BITMAP_FLAG);
	private final Rect mRectSrc = new Rect();
	private final Rect mRectDst = new Rect();
	private float mAspectQuotient;

	private Bitmap mBitmap;
	private ZoomState mState;

	public ZoomImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		init();
	}

	public ZoomImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init();
	}

	public ZoomImageView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init();
	}

	private void init() {
		// TODO Auto-generated method stub
		mState = new ZoomState();
		mState.addObserver(this);
		mState.setZoom(1.0f);
		setOnTouchListener(this);
	}

	private float getinitzoom(int viewWidth, int viewHeight, int bitmapWidth,
			int bitmapHeight) {
		float zoomx = viewWidth * 1.0f / bitmapWidth;
		float zoomy = viewHeight * 1.0f / bitmapHeight;
		return zoomx < zoomy ? zoomx : zoomy;
	}

	protected void onDraw(Canvas canvas) {
		if (mBitmap != null && mState != null) {
			final int viewWidth = getWidth();
			final int viewHeight = getHeight();
			final int bitmapWidth = mBitmap.getWidth();
			final int bitmapHeight = mBitmap.getHeight();

			final float panX = mState.getPanX();
			final float panY = mState.getPanY();
			final float zoomX = mState.getZoomX(mAspectQuotient) * viewWidth
					/ bitmapWidth;
			final float zoomY = mState.getZoomY(mAspectQuotient) * viewHeight
					/ bitmapHeight;

			// Setup source and destination rectangles

			mRectSrc.left = (int) (panX * bitmapWidth - viewWidth / (zoomX * 2));
			mRectSrc.top = (int) (panY * bitmapHeight - viewHeight
					/ (zoomY * 2));
			mRectSrc.right = (int) (mRectSrc.left + viewWidth / zoomX);
			mRectSrc.bottom = (int) (mRectSrc.top + viewHeight / zoomY);
			mRectDst.left = getLeft();
			mRectDst.top = getTop();
			mRectDst.right = getRight();
			mRectDst.bottom = getBottom();
			// Adjust source rectangle so that it fits within the source
			// image.
			if (mRectSrc.left < 0) {
				mRectDst.left += -mRectSrc.left * zoomX;
				mRectSrc.left = 0;
			}
			if (mRectSrc.right > bitmapWidth) {
				mRectDst.right -= (mRectSrc.right - bitmapWidth) * zoomX;
				mRectSrc.right = bitmapWidth;
			}
			if (mRectSrc.top < 0) {
				mRectDst.top += -mRectSrc.top * zoomY;
				mRectSrc.top = 0;
			}
			if (mRectSrc.bottom > bitmapHeight) {
				mRectDst.bottom -= (mRectSrc.bottom - bitmapHeight) * zoomY;
				mRectSrc.bottom = bitmapHeight;
			}

			canvas.drawBitmap(mBitmap, mRectSrc, mRectDst, mPaint);
		}
	}

	public void update(Observable observable, Object data) {
		invalidate();
	}

	private void calculateAspectQuotient() {
		if (mBitmap != null) {
			mAspectQuotient = (((float) mBitmap.getWidth()) / mBitmap
					.getHeight()) / (((float) getWidth()) / getHeight());
		}
	}

	@Override
	public void setImageResource(int resId) {
		// TODO Auto-generated method stub
		super.setImageResource(resId);
		mBitmap = ((BitmapDrawable) getDrawable()).getBitmap();
		calculateAspectQuotient();
		invalidate();
	}

	@Override
	public void setImageBitmap(Bitmap bm) {
		// TODO Auto-generated method stub
		super.setImageBitmap(bm);
		mBitmap = bm;
		calculateAspectQuotient();
		invalidate();
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		calculateAspectQuotient();
	}

	public float getZoom() {
		return mState.getZoom();
	}

	public void setZoom(float zoom) {
		mState.setZoom(zoom);
		invalidate();
	}

	public void setPanX(float panX) {
		mState.setPanX(panX);
		invalidate();
	}

	public void setPanY(float panY) {
		mState.setPanY(panY);
		invalidate();
	}

	public enum ControlType {
		PAN, ZOOM
	}

	private ControlType mControlType = ControlType.PAN;

	private float mX;
	private float mY;
	private float mGap;

	public void setControlType(ControlType controlType) {
		mControlType = controlType;
	}

	private float getGap(float x0, float x1, float y0, float y1) {
		return (float) Math.pow(
				Math.pow((x0 - x1), 2) + Math.pow((y0 - y1), 2), 0.5);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		final int action = event.getAction();
		int pointCount = event.getPointerCount();
		if (pointCount == 1) {
			final float x = event.getX();
			final float y = event.getY();
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				mX = x;
				mY = y;
				break;
			case MotionEvent.ACTION_MOVE:
				final float dx = (x - mX) / v.getWidth();
				final float dy = (y - mY) / v.getHeight();
				if (mState.getZoom() > 1) {
					if (mRectSrc.top > 0
							|| mRectSrc.bottom < mBitmap.getHeight()
									* mState.getZoom()) {
						mState.setPanY(mState.getPanY() - dy);
					}
					if (dx < 0 && mRectSrc.right < mBitmap.getWidth() || dx > 0
							&& mRectSrc.left > 0) {
						mState.setPanX(mState.getPanX() - dx);
					}
					mState.notifyObservers();
					mX = x;
					mY = y;
				}
				break;
			}
		} else if (pointCount == 2) {
			final float x0 = event.getX(event.getPointerId(0));
			final float y0 = event.getY(event.getPointerId(0));

			final float x1 = event.getX(event.getPointerId(1));
			final float y1 = event.getY(event.getPointerId(1));

			final float gap = getGap(x0, x1, y0, y1);
			switch (action) {
			case MotionEvent.ACTION_POINTER_2_DOWN:
			case MotionEvent.ACTION_POINTER_1_DOWN:
				mGap = gap;
				break;
			case MotionEvent.ACTION_POINTER_1_UP:
				mX = x1;
				mY = y1;
				break;
			case MotionEvent.ACTION_POINTER_2_UP:
				mX = x0;
				mY = y0;
				break;
			case MotionEvent.ACTION_MOVE: {
				final float dgap = (gap - mGap) / mGap;
				// Log.d("Gap", String.valueOf(dgap));
				// Log.d("Gap", String.valueOf((float) Math.pow(20, dgap)));
				float zoom = mState.getZoom() * (float) Math.pow(5, dgap);
				if (zoom > 1) {
					mState.setZoom(zoom);
					invalidate();
				}
				mGap = gap;
				break;
			}
			}
		}
		return true;
	}

	private class ZoomState extends Observable {

		private float mZoom;
		private float mPanX;
		private float mPanY;

		public ZoomState() {
			super();
			// TODO Auto-generated constructor stub
			mPanX = 0.5f;
			mPanY = 0.5f;
		}

		public float getPanX() {
			if (mPanX < 0.25f) {
				return 0.25f;
			} else if (mPanX > 0.75) {
				return 0.75f;
			}
			return mPanX;
		}

		public float getPanY() {
			if (mPanY > 0.75f) {
				return 0.75f;
			} else if (mPanY < 0.25f) {
				return 0.25f;
			}
			return mPanY;
		}

		public float getZoom() {
			return mZoom;
		}

		public void setPanX(float panX) {
			if (panX != mPanX) {
				mPanX = panX;
				setChanged();
			}
		}

		public void setPanY(float panY) {
			if (panY != mPanY) {
				mPanY = panY;
				setChanged();
			}
		}

		public void setZoom(float zoom) {
			if (zoom != mZoom) {
				mZoom = zoom;
				setChanged();
			}
		}

		public float getZoomX(float aspectQuotient) {
			return Math.min(mZoom, mZoom * aspectQuotient);
		}

		public float getZoomY(float aspectQuotient) {
			return Math.min(mZoom, mZoom / aspectQuotient);
		}
	}
}
