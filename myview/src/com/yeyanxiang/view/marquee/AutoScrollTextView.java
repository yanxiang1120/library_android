package com.yeyanxiang.view.marquee;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.TextView;

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
public class AutoScrollTextView extends TextView implements OnClickListener {
	public final static String TAG = AutoScrollTextView.class.getSimpleName();

	private String[] words;
	private float textHeight = 0f;// 文本长度
	private float viewHeight = 0f;
	private float x = 0f;// 文字的横坐标
	private float step = 0f;// 文字的纵坐标
	private float temp_view_plus_text_height = 0.0f;// 用于计算的临时变量
	private float temp_view_plus_multi_text_height = 0.0f;// 用于计算的临时变量
	public boolean isStarting = false;// 是否开始滚动
	private Paint paint = null;// 绘图样式
	private int compoundPaddingLeft;
	private int compoundPaddingTop;
	private int compoundPaddingRight;
	private int compoundPaddingBottom;

	public AutoScrollTextView(Context context) {
		super(context);
		initView();
	}

	public AutoScrollTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}

	public AutoScrollTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView();
	}

	/**
	 * 初始化控件
	 */
	private void initView() {
		setOnClickListener(this);
	}

	/**
	 * 文本初始化，每次更改文本内容或者文本效果等之后都需要重新初始化一下
	 */
	public void init(WindowManager windowManager) {
		compoundPaddingLeft = getCompoundPaddingLeft();
		compoundPaddingTop = getCompoundPaddingTop();
		compoundPaddingRight = getCompoundPaddingRight();
		compoundPaddingBottom = getCompoundPaddingBottom();

		paint = getPaint();
		// text = getText().toString();
		// text =words[0];
		// textLength = paint.measureText(text);
		textHeight = getTextSize();
		// viewWidth = getWidth();
		viewHeight = getHeight();
		if (viewHeight == 0) {
			if (windowManager != null) {
				Display display = windowManager.getDefaultDisplay();
				viewHeight = display.getHeight();
			}
		}
		temp_view_plus_text_height = viewHeight + textHeight;
		temp_view_plus_multi_text_height = viewHeight + textHeight
				* words.length;
		step = viewHeight - textHeight * words.length;
	}

	@Override
	public Parcelable onSaveInstanceState() {
		Parcelable superState = super.onSaveInstanceState();
		SavedState ss = new SavedState(superState);

		ss.step = step;
		ss.isStarting = isStarting;

		return ss;

	}

	@Override
	public void onRestoreInstanceState(Parcelable state) {
		if (!(state instanceof SavedState)) {
			super.onRestoreInstanceState(state);
			return;
		}
		SavedState ss = (SavedState) state;
		super.onRestoreInstanceState(ss.getSuperState());

		step = ss.step;
		isStarting = ss.isStarting;

	}

	public static class SavedState extends BaseSavedState {
		public boolean isStarting = false;
		public float step = 0.0f;

		SavedState(Parcelable superState) {
			super(superState);
		}

		@Override
		public void writeToParcel(Parcel out, int flags) {
			super.writeToParcel(out, flags);
			out.writeBooleanArray(new boolean[] { isStarting });
			out.writeFloat(step);
		}

		public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {

			public SavedState[] newArray(int size) {
				return new SavedState[size];
			}

			public SavedState createFromParcel(Parcel in) {
				return new SavedState(in);
			}
		};

		private SavedState(Parcel in) {
			super(in);
			boolean[] b = null;
			in.readBooleanArray(b);
			if (b != null && b.length > 0)
				isStarting = b[0];
			step = in.readFloat();
		}
	}

	/**
	 * 开始滚动
	 */
	public void startScroll() {
		isStarting = true;
		invalidate();
	}

	/**
	 * 停止滚动
	 */
	public void stopScroll() {
		isStarting = false;
		invalidate();
	}

	@Override
	public void onDraw(Canvas canvas) {
		canvas.clipRect(compoundPaddingLeft, compoundPaddingTop,
				canvas.getWidth() - compoundPaddingRight, canvas.getHeight()
						- compoundPaddingBottom);

		for (int i = 0; i < words.length; i++) {
			float textLength = paint.measureText(words[i]);

			float y = temp_view_plus_text_height - step + textHeight * i;

			if (y > 0 && y < (canvas.getHeight() + textHeight)) {
				canvas.drawText(words[i], (getWidth() - textLength) / 2, y,
						paint);
			}
		}
		if (!isStarting) {
			return;
		}
		step += 2;
		if (step > temp_view_plus_multi_text_height)
			step = viewHeight - textHeight * words.length;

		invalidate();
	}

	public void onClick(View v) {
		if (isStarting)
			stopScroll();
		else
			startScroll();

	}

	public void setWords(String[] words) {
		this.words = words;
	}
}
