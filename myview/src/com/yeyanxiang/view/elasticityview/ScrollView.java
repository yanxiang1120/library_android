package com.yeyanxiang.view.elasticityview;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.TranslateAnimation;

/**
 * 
 * Create on 2013-5-7 下午3:52:02 </br> Copyright: Copyright(c) 2013 by 叶雁翔</br>
 * 
 * 简介: 有弹性的scrollView
 * 
 * @Version 1.0
 * @Author <a href="mailto:yanxiang1120@126.com">叶雁翔</a>
 * 
 * 
 */
public class ScrollView extends android.widget.ScrollView {
	private View inner;
	private float y;
	private boolean flag = true;
	private Rect normal = new Rect();
	private int sum = 0;
	private boolean onInterceptTouchEvent = true;

	public ScrollView(Context context) {
		super(context);
	}

	public ScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onFinishInflate() {
		if (getChildCount() > 0) {
			inner = getChildAt(0);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		boolean onTouchflag = true;
		if (inner != null) {
			if (flag) {
				y = ev.getY();
			}
			onTouchflag = super.onTouchEvent(ev);
			commOnTouchEvent(ev);
		} else {
			return super.onTouchEvent(ev);
		}
		return onTouchflag;
	}

	public void commOnTouchEvent(MotionEvent ev) {
		int action = ev.getAction();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			y = ev.getY();
			// System.out.println("down----" + y);
			break;
		case MotionEvent.ACTION_UP:
			flag = true;
			sum = 0;
			// System.out.println("up----" + y);
			if (isNeedAnimation()) {
				animation();
			}
			break;
		case MotionEvent.ACTION_MOVE:
			flag = false;
			int deltaY = (int) (ev.getY() - y);
			sum += deltaY;
			// 滚动
			// scrollBy(0, deltaY);

			y = ev.getY();
			// System.out.println("move---" + y);
			// 当滚动到最上或者最下时就不会再滚动，这时移动布局
			if (isNeedMove(sum)) {
				if (normal.isEmpty()) {
					// 保存正常的布局位置
					normal.set(inner.getLeft(), inner.getTop(),
							inner.getRight(), inner.getBottom());
				}
				// 移动布局
				inner.layout(inner.getLeft(), inner.getTop() + deltaY,
						inner.getRight(), inner.getBottom() + deltaY);
			}
			break;
		default:
			break;
		}
	}

	// 开启动画移动

	public void animation() {
		// 开启移动动画
		TranslateAnimation ta = new TranslateAnimation(0, 0, inner.getTop(),
				normal.top);
		ta.setDuration(100);
		inner.startAnimation(ta);
		// 设置回到正常的布局位置
		inner.layout(normal.left, normal.top, normal.right, normal.bottom);
		normal.setEmpty();
	}

	// 是否需要开启动画
	public boolean isNeedAnimation() {
		return !normal.isEmpty();
	}

	// 是否需要移动布局
	public boolean isNeedMove(int sum) {
		int offset = inner.getMeasuredHeight() - getHeight();
		// int scrollY = getScrollY();
		// System.out.println("offset=" + offset + "----scrolly=" + scrollY);
		// if (scrollY == 0 || scrollY == offset) {
		// return true;
		// }
		if (offset <= 0 || sum >= 0) {
			return true;
		} else {
			if (-sum > offset) {
				return true;
			} else {
				return false;
			}
		}
	}

	public void canscroll(boolean flag) {
		onInterceptTouchEvent = flag;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		if (onInterceptTouchEvent) {
			return super.onInterceptTouchEvent(ev);
		} else {
			return false;
		}
	}
}