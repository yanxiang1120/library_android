package com.yeyanxiang.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Scroller;

/**
 * @author 叶雁翔
 * 
 * @Email yanxiang1120@gmail.com
 * 
 * @version 1.0
 * 
 * @update 2014年4月17日
 * 
 * @简介
 */
public class SlideFunctionListView extends ListView {
	private int slidePosition, preSlidePosition;
	private int downY;
	private int downX;
	private View itemView, preItemView;
	private Scroller scroller;
	private static final int SNAP_VELOCITY = 600;
	private VelocityTracker velocityTracker;
	public static boolean isSlide = false;
	public static boolean isObstruct = true;
	private int mTouchSlop;
	private int functionid;
	private boolean show = false;

	private static ScaleAnimation scaleHideAnimation;
	private static ScaleAnimation scaleShowAnimation;

	public SlideFunctionListView(Context context) {
		this(context, null);
	}

	public SlideFunctionListView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public SlideFunctionListView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		scroller = new Scroller(context);
		mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
		scaleShowAnimation = new ScaleAnimation(0.0f, 1.0f, 1.0f, 1.0f,
				Animation.RELATIVE_TO_SELF, 1, Animation.RELATIVE_TO_SELF, 0);
		scaleShowAnimation.setDuration(250);
		scaleShowAnimation.setAnimationListener(new AnimationListener() {

			public void onAnimationStart(Animation animation) {
				isObstruct = false;
			}

			public void onAnimationRepeat(Animation animation) {

			}

			public void onAnimationEnd(Animation animation) {
				isSlide = false;
			}
		});

		scaleHideAnimation = new ScaleAnimation(1.0f, 0.0f, 1.0f, 1.0f,
				Animation.RELATIVE_TO_SELF, 1, Animation.RELATIVE_TO_SELF, 0);
		scaleHideAnimation.setDuration(250);
	}

	public void setFunctionid(int functionid) {
		this.functionid = functionid;
	}

	public boolean dispatchTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN: {

			addVelocityTracker(event);

			downX = (int) event.getX();
			downY = (int) event.getY();

			slidePosition = pointToPosition(downX, downY);

			if (slidePosition == AdapterView.INVALID_POSITION) {
				return super.dispatchTouchEvent(event);
			}

			itemView = getChildAt(slidePosition - getFirstVisiblePosition())
					.findViewById(functionid);
			preSlidePosition = slidePosition;
			break;
		}
		case MotionEvent.ACTION_MOVE: {
			if (Math.abs(getScrollVelocity()) > SNAP_VELOCITY
					|| (Math.abs(downX - event.getX()) > mTouchSlop && Math
							.abs(event.getY() - downY) < mTouchSlop)) {
				isSlide = true;
				if (downX > event.getX()) {
					show = true;
				} else {
					show = false;
				}
			} else {
				isSlide = false;
			}
			break;
		}
		case MotionEvent.ACTION_UP:
			recycleVelocityTracker();
			isObstruct = true;
			break;
		}

		return super.dispatchTouchEvent(event);
	}

	public boolean onInterceptTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_MOVE:
			if (itemView.getVisibility() == View.GONE) {
				isSlide = false;
			} else {
				isSlide = true;
			}
			break;

		default:
			break;
		}
		return super.onInterceptTouchEvent(ev);
	}

	public boolean onTouchEvent(MotionEvent ev) {
		if (isSlide && slidePosition != AdapterView.INVALID_POSITION) {
			addVelocityTracker(ev);
			final int action = ev.getAction();
			switch (action) {
			case MotionEvent.ACTION_MOVE:
				if (isObstruct) {

					if (preItemView == null) {
						preItemView = itemView;
					} else {
						if (preItemView == itemView) {
							if (show) {
								show(preItemView);
							} else {
								hide(preItemView);
							}
						} else {
							hide(preItemView);
							preItemView = itemView;
							if (show) {
								show(itemView);
							}
						}
					}
				}
				break;
			case MotionEvent.ACTION_UP:
				isObstruct = true;
				recycleVelocityTracker();
				isSlide = false;
				break;
			}
			return true;
		}

		return super.onTouchEvent(ev);
	}

	private void show(View view) {
		if (view.getVisibility() == View.GONE) {
			view.setVisibility(View.VISIBLE);
			view.startAnimation(scaleShowAnimation);
		}

	}

	private void hide(final View view) {

		if (view.getVisibility() == View.VISIBLE) {
			scaleHideAnimation.setAnimationListener(new AnimationListener() {

				public void onAnimationStart(Animation animation) {
					isObstruct = false;
				}

				public void onAnimationRepeat(Animation animation) {

				}

				public void onAnimationEnd(Animation animation) {
					view.setVisibility(View.GONE);

				}
			});
			view.startAnimation(scaleHideAnimation);
		}
	}

	public void computeScroll() {
		if (scroller.computeScrollOffset()) {
			postInvalidate();
			if (scroller.isFinished()) {
				itemView.scrollTo(0, 0);
			}
		}
	}

	private void addVelocityTracker(MotionEvent event) {
		if (velocityTracker == null) {
			velocityTracker = VelocityTracker.obtain();
		}
		velocityTracker.addMovement(event);
	}

	private void recycleVelocityTracker() {
		if (velocityTracker != null) {
			velocityTracker.recycle();
			velocityTracker = null;
		}
	}

	private int getScrollVelocity() {
		velocityTracker.computeCurrentVelocity(1000);
		int velocity = (int) velocityTracker.getXVelocity();
		return velocity;
	}

}
