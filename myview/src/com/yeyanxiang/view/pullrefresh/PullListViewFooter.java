package com.yeyanxiang.view.pullrefresh;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * @author 叶雁翔
 * 
 * @Email yanxiang1120@gmail.com
 * 
 * @version 1.0
 * 
 * @date 2013-11-4 下午1:28:50
 * 
 * @简介
 */
public class PullListViewFooter extends RelativeLayout {
	public final static int STATE_NORMAL = 0;
	public final static int STATE_READY = 1;
	public final static int STATE_LOADING = 2;
	public static final int STATE_FINISH = 3;

	private Context mContext;

	private View mContentView;
	private View mProgressBar;
	private TextView mHintView;

	public PullListViewFooter(Context context) {
		super(context);
		initView(context);
	}

	public PullListViewFooter(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	public void setState(int state) {
		if (state == STATE_READY) {
			mProgressBar.setVisibility(View.INVISIBLE);
			mHintView.setText("松开载入更多");
		} else if (state == STATE_LOADING) {
			mProgressBar.setVisibility(View.VISIBLE);
			mHintView.setText("正在加载...");
		} else if (state == STATE_FINISH) {
			mProgressBar.setVisibility(View.GONE);
			mHintView.setText("数据加载完毕");
		} else {
			mProgressBar.setVisibility(View.INVISIBLE);
			mHintView.setText("加载更多");
		}
	}

	public void setBottomMargin(int height) {
		if (height < 0)
			return;
		RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mContentView
				.getLayoutParams();
		lp.bottomMargin = height;
		mContentView.setLayoutParams(lp);
	}

	public int getBottomMargin() {
		RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mContentView
				.getLayoutParams();
		return lp.bottomMargin;
	}

	/**
	 * normal status
	 */
	public void normal() {
		mHintView.setVisibility(View.VISIBLE);
		mProgressBar.setVisibility(View.GONE);
	}

	/**
	 * loading status
	 */
	public void loading() {
		mHintView.setVisibility(View.GONE);
		mProgressBar.setVisibility(View.VISIBLE);
	}

	/**
	 * hide footer when disable pull load more
	 */
	public void hide() {
		RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mContentView
				.getLayoutParams();
		lp.height = 0;
		mContentView.setLayoutParams(lp);
	}

	/**
	 * show footer
	 */
	public void show() {
		RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mContentView
				.getLayoutParams();
		lp.height = LayoutParams.WRAP_CONTENT;
		mContentView.setLayoutParams(lp);
	}

	private void initView(Context context) {
		mContext = context;

		Resources resources = context.getResources();
		String packagename = context.getPackageName();

		RelativeLayout moreView = (RelativeLayout) LayoutInflater
				.from(mContext).inflate(
						resources.getIdentifier("pulldown_footer", "layout",
								packagename), null);
		addView(moreView);
		moreView.setLayoutParams(new RelativeLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

		mContentView = moreView.findViewById(resources.getIdentifier(
				"xlistview_footer_content", "id", packagename));
		mProgressBar = moreView.findViewById(resources.getIdentifier(
				"pulldown_footer_loading", "id", packagename));
		mHintView = (TextView) moreView.findViewById(resources.getIdentifier(
				"pulldown_footer_text", "id", packagename));
	}

	public void setTextColor(int color) {
		// TODO Auto-generated method stub
		mHintView.setTextColor(color);
	}

}
