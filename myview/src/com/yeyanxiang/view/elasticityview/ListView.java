package com.yeyanxiang.view.elasticityview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * 
 * Create on 2013-5-8 下午1:04:16 </br> Copyright: Copyright(c) 2013 by 叶雁翔</br>
 * 
 * 简介: 有弹性的ListView
 * 
 * @Version 1.0
 * @Author <a href="mailto:yanxiang1120@126.com">叶雁翔</a>
 * 
 * 
 */
public class ListView extends android.widget.ListView {
	private float mLastDownY = 0f;
	private float mDistance = 0;
	private float mStep = 5;
	private boolean mPositive = false;

	public ListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public ListView(Context context) {
		super(context);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if (mLastDownY == 0f && mDistance == 0) {
				mLastDownY = event.getY();
				return true;
			}
			break;

		case MotionEvent.ACTION_CANCEL:
			break;
		case MotionEvent.ACTION_UP:
			if (mDistance != 0) {
				mStep = 1;
				mPositive = (mDistance >= 0);
				reset();
				return true;
			}
			mLastDownY = 0f;
			mDistance = 0;
			break;

		case MotionEvent.ACTION_MOVE:

			// if (mLastDownY != 0f) {
			if (getFirstVisiblePosition() == 0
					|| getLastVisiblePosition() == getCount() - 1) {

				mDistance = mLastDownY - event.getY();
				if (mDistance < 0) {
					if (getFirstVisiblePosition() == 0
							&& getChildAt(0).getTop() == 0) {
						scrollTo(0, (int) mDistance);
						return true;
					}
				} else {
					if (getLastVisiblePosition() == getCount() - 1) {
						View view = getChildAt(getLastVisiblePosition()
								- getFirstVisiblePosition());
						if (getHeight() - view.getHeight() - view.getTop() == 0) {
							scrollTo(0, (int) mDistance);
							return true;
						}
					}
				}
			}
			// }
			mDistance = 0;
			break;
		}
		return super.onTouchEvent(event);
	}

	private void reset() {
		mDistance += mDistance > 0 ? -mStep : mStep;
		scrollTo(0, (int) mDistance);
		if ((mPositive && mDistance <= 0) || (!mPositive && mDistance >= 0)) {
			scrollTo(0, 0);
			mDistance = 0;
			mLastDownY = 0f;
			return;
		}
		mStep += 1;
		reset();
	}

}
