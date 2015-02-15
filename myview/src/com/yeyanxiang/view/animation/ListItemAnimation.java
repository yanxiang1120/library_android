package com.yeyanxiang.view.animation;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout.LayoutParams;

/**
 * @author 叶雁翔
 * 
 * @Email yanxiang1120@gmail.com
 * 
 * @version 1.0
 * 
 * @data 2014年1月12日 下午3:09:21
 * 
 * @简介
 */
public class ListItemAnimation extends Animation {
	private View AnimationView = null;
	private LayoutParams ViewLayoutParams = null;
	private int viewStart = 0;
	private int viewEnd = 0;
	private OnViewScrollListener onViewScrollListener;

	public void setOnViewScrollListener(
			OnViewScrollListener onViewScrollListener) {
		this.onViewScrollListener = onViewScrollListener;
	}

	public ListItemAnimation(View view) {
		AnimationSettings(view, 300);
	}

	public ListItemAnimation(View view, int duration) {
		AnimationSettings(view, duration);
	}

	private void AnimationSettings(View view, int duration) {
		setDuration(duration);
		AnimationView = view;
		ViewLayoutParams = (LayoutParams) view.getLayoutParams();
		viewStart = ViewLayoutParams.bottomMargin;
		viewEnd = (viewStart == 0 ? (0 - view.getHeight()) : 0);
		view.setVisibility(View.VISIBLE);
	}

	@Override
	protected void applyTransformation(float interpolatedTime, Transformation t) {
		// TODO Auto-generated method stub
		super.applyTransformation(interpolatedTime, t);
		if (interpolatedTime < 1.0f) {
			ViewLayoutParams.bottomMargin = viewStart
					+ (int) ((viewEnd - viewStart) * interpolatedTime);
			AnimationView.requestLayout();
		} else {
			ViewLayoutParams.bottomMargin = viewEnd;
			AnimationView.requestLayout();
			if (viewEnd != 0) {
				AnimationView.setVisibility(View.GONE);
			}
		}

		if (onViewScrollListener != null) {
			onViewScrollListener.scroll(this);
		}
	}
}
