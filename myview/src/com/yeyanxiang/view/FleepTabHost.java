package com.yeyanxiang.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TabHost;

/**
 * 
 * Create on 2013-4-28 上午10:55:12 </br> Copyright: Copyright(c) 2013 by 叶雁翔</br>
 * 
 * 简介: 可滑动切换tab页的Tabhost
 * 
 * @Version 1.0
 * @Author <a href="mailto:yanxiang1120@126.com">叶雁翔</a>
 * 
 * 
 */
public class FleepTabHost extends TabHost {
	private Animation slideLeftIn;
	private Animation slideLeftOut;
	private Animation slideRightIn;
	private Animation slideRightOut;

	private int tabCount;// tab页总数

	public FleepTabHost(Context context, AttributeSet attrs) {
		super(context, attrs);
		slideLeftIn = AnimationUtils.loadAnimation(context,
				R.anim.slide_left_in);
		slideLeftOut = AnimationUtils.loadAnimation(context,
				R.anim.slide_left_out);
		slideRightIn = AnimationUtils.loadAnimation(context,
				R.anim.slide_right_in);
		slideRightOut = AnimationUtils.loadAnimation(context,
				R.anim.slide_right_out);
	}

	public int getTabCount() {
		return tabCount;
	}

	@Override
	public void addTab(TabSpec tabSpec) {
		tabCount++;
		super.addTab(tabSpec);
	}

	@Override
	public void setCurrentTab(int index) {
		// index为要切换到的tab页索引，currentTabIndex为现在要当前tab页的索引
		int currentTabIndex = getCurrentTab();

		// 设置当前tab页退出时的动画
		if (null != getCurrentView()) {// 第一次进入MainActivity时，getCurrentView()取得的值为空

			if (tabCount == 2) {
				if (index > currentTabIndex) {
					getCurrentView().startAnimation(slideLeftOut);
				} else if (index < currentTabIndex) {
					getCurrentView().startAnimation(slideRightOut);
				}
			} else {

				if (currentTabIndex == (tabCount - 1) && index == 0) {// 处理边界滑动
					getCurrentView().startAnimation(slideLeftOut);
				} else if (currentTabIndex == 0 && index == (tabCount - 1)) {// 处理边界滑动
					getCurrentView().startAnimation(slideRightOut);
				} else if (index > currentTabIndex) {// 非边界情况下从右往左fleep
					getCurrentView().startAnimation(slideLeftOut);
				} else if (index < currentTabIndex) {// 非边界情况下从左往右fleep
					getCurrentView().startAnimation(slideRightOut);
				}
			}
		}

		super.setCurrentTab(index);

		// 设置即将显示的tab页的动画
		if (null != getCurrentView()) {
			if (tabCount == 2) {
				if (index > currentTabIndex) {// 非边界情况下从右往左fleep
					getCurrentView().startAnimation(slideLeftIn);
				} else if (index < currentTabIndex) {// 非边界情况下从左往右fleep
					getCurrentView().startAnimation(slideRightIn);
				}
			} else {
				if (currentTabIndex == (tabCount - 1) && index == 0) {// 处理边界滑动
					getCurrentView().startAnimation(slideLeftIn);
				} else if (currentTabIndex == 0 && index == (tabCount - 1)) {// 处理边界滑动
					getCurrentView().startAnimation(slideRightIn);
				} else if (index > currentTabIndex) {// 非边界情况下从右往左fleep
					getCurrentView().startAnimation(slideLeftIn);
				} else if (index < currentTabIndex) {// 非边界情况下从左往右fleep
					getCurrentView().startAnimation(slideRightIn);
				}
			}
		}
	}
}
