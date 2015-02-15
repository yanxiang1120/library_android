package com.yeyanxiang.view.listview3D;

import java.util.LinkedList;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.AnimationUtils;
import android.widget.Adapter;
import android.widget.AdapterView;

/**
 * 
 * Create on 2013-5-8 下午3:05:33 </br> Copyright: Copyright(c) 2013 by 叶雁翔</br>
 * 
 * 简介: ListView 3D效果
 * 
 * @Version 1.0
 * @Author <a href="mailto:yanxiang1120@126.com">叶雁翔</a>
 * 
 * 
 */
public class ListView3D extends AdapterView<Adapter> {
	// ===========================================================
	// Constants
	// ===========================================================

	// 新添加的所有子视图在当前最当前最后一个子视图后添加的布局模型
	private static final int LAYOUT_MODE_BELOW = 0;
	// 与LAYOUT_MODE_BELOW相反方向添加的布局模型
	private static final int LAYOUT_MODE_ABOVE = 1;

	// 初始模式，用户还未接触ListView
	private static final int TOUCH_MODE_REST = -1;
	// 触摸Down事件模式
	private static final int TOUCH_MODE_DOWN = 0;
	// 滚动模式
	private static final int TOUCH_MODE_SCROLL = 1;

	private static final int INVALID_INDEX = -1;

	/** 3D 效果 **/
	// Item与List的宽度比
	private static final float ITEM_WIDTH = 0.85f;
	// Item高度因为需要预留给滚动空间，填充后的高度与Item实际高度的比例
	private static final float ITEM_VERTICAL_SPACE = 1.45f;
	private static final int AMBIENT_LIGHT = 55;
	private static final int DIFFUSE_LIGHT = 200;
	private static final float SPECULAR_LIGHT = 70;
	private static final float SHININESS = 200;
	private static final int MAX_INTENSITY = 0xFF;
	private static final float SCALE_DOWN_FACTOR = 0.15f;
	private static final int DEGREES_PER_SCREEN = 270;

	/** Fling 与 阻尼效果 **/
	private static final int PIXELS_PER_SECOND = 1000;
	private static final float POSITION_TOLERANCE = 0.4f;
	private static final float VELOCITY_TOLERANCE = 0.5f;
	private static final float WAVELENGTH = 0.9f;
	private static final float AMPLITUDE = 0.0f;

	// ===========================================================
	// Fields
	// ===========================================================

	// 视图和数据适配
	private Adapter mAdapter;
	// 当前显示最后一个Item在Adapter中位置
	private int mLastItemPosition = -1;
	// 当前显示第一个Item在Adapter中位置
	private int mFirstItemPosition;

	// 当前顶部第一个item
	private int mListTop;
	// 当前第一个显示的item与底部第一个item的顶部偏移量
	private int mListTopOffset;
	// 触摸Down事件时进行记录
	private int mListTopStart;

	// 记录ListView当前处于哪种模式
	private int mTouchMode = TOUCH_MODE_REST;

	// 记录上一次触摸X轴
	private int mTouchStartX;
	// 记录上一次触摸Y轴
	private int mTouchStartY;
	// 仅记录Down事件时Y轴值
	private int mMotionY;

	// 触发滚动的最小移动距离
	private int mTouchSlop;

	// 可反复使用的Rect
	private Rect mRect;

	// 用于检测是手长按动作
	private Runnable mLongPressRunnable;

	// View复用当前仅支持一种类型Item视图复用
	// 想更多了解ListView视图如何复用可以看AbsListView内部类RecycleBin
	private final LinkedList<View> mCachedItemViews = new LinkedList<View>();

	/** 3D 效果 **/
	private int mListRotation;
	private Camera mCamera;
	private Matrix mMatrix;
	private Paint mPaint;
	private boolean mRotationEnabled = true;
	private boolean mLightEnabled = true;

	/** Fling 与 阻尼效果 **/
	private VelocityTracker mVelocityTracker;
	private Dynamics mDynamics;
	private Runnable mDynamicsRunnable;
	private int mLastSnapPos = Integer.MIN_VALUE;

	// ===========================================================
	// Constructors
	// ===========================================================

	public ListView3D(Context context) {
		super(context);
		initListView(context);
	}

	public ListView3D(Context context, AttributeSet attrs) {
		super(context, attrs);
		initListView(context);
	}

	public ListView3D(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initListView(context);
	}

	// ===========================================================
	// Setter
	// ===========================================================

	public void setDynamics(final Dynamics dynamics) {
		if (mDynamics != null) {
			dynamics.setState(mDynamics.getPosition(), mDynamics.getVelocity(),
					AnimationUtils.currentAnimationTimeMillis());
		}
		mDynamics = dynamics;
		if (!mRotationEnabled) {
			mDynamics.setMaxPosition(0);
		}
	}

	private void setSnapPoint() {
		if (mRotationEnabled) {
			final int rotation = mListRotation % 90;
			int snapPosition = 0;

			if (rotation < 45) {
				snapPosition = (-(mListRotation - rotation) * getHeight())
						/ DEGREES_PER_SCREEN;
			} else {
				snapPosition = (-(mListRotation + 90 - rotation) * getHeight())
						/ DEGREES_PER_SCREEN;
			}

			if (mLastSnapPos == Integer.MIN_VALUE
					&& mLastItemPosition == mAdapter.getCount() - 1
					&& getChildBottom(getChildAt(getChildCount() - 1)) < getHeight()) {
				mLastSnapPos = snapPosition;
			}

			if (snapPosition > 0) {
				snapPosition = 0;
			} else if (snapPosition < mLastSnapPos) {
				snapPosition = mLastSnapPos;
			}
			mDynamics.setMaxPosition(snapPosition);
			mDynamics.setMinPosition(snapPosition);

		} else {
			if (mLastSnapPos == Integer.MIN_VALUE
					&& mLastItemPosition == mAdapter.getCount() - 1
					&& getChildBottom(getChildAt(getChildCount() - 1)) < getHeight()) {
				mLastSnapPos = mListTop;
				mDynamics.setMinPosition(mLastSnapPos);
			}
		}
	}

	// ===========================================================
	// Getter
	// ===========================================================

	private int getChildMargin(View child) {
		return (int) (child.getMeasuredHeight() * (ITEM_VERTICAL_SPACE - 1) / 2);
	}

	private int getChildTop(View child) {
		return child.getTop() - getChildMargin(child);
	}

	private int getChildBottom(View child) {
		return child.getBottom() + getChildMargin(child);
	}

	private int getChildHeight(View child) {
		return child.getMeasuredHeight() + 2 * getChildMargin(child);
	}

	public void enableRotation(final boolean enable) {
		mRotationEnabled = enable;
		mDynamics.setMaxPosition(Float.MAX_VALUE);
		mDynamics.setMinPosition(-Float.MAX_VALUE);
		mLastSnapPos = Integer.MIN_VALUE;
		if (!mRotationEnabled) {
			mListRotation = 0;
			mDynamics.setMaxPosition(0);
		} else {
			mListRotation = -(DEGREES_PER_SCREEN * mListTop) / getHeight();
			setSnapPoint();
			if (mDynamics != null) {
				mDynamics.setState(mListTop, mDynamics.getVelocity(),
						AnimationUtils.currentAnimationTimeMillis());
				post(mDynamicsRunnable);
			}
		}
		invalidate();
	}

	public boolean isRotationEnabled() {
		return mRotationEnabled;
	}

	public void enableLight(final boolean enable) {
		mLightEnabled = enable;
		if (!mLightEnabled) {
			mPaint.setColorFilter(null);
		} else {
			mPaint.setAlpha(0xFF);
		}
		invalidate();
	}

	public boolean isLightEnabled() {
		return mLightEnabled;
	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	public Adapter getAdapter() {
		return mAdapter;
	}

	@Override
	public void setAdapter(Adapter adapter) {
		mAdapter = adapter;
		removeAllViewsInLayout();
		requestLayout();
	}

	@Override
	public View getSelectedView() {
		throw new UnsupportedOperationException("Not supported");
	}

	@Override
	public void setSelection(int position) {
		throw new UnsupportedOperationException("Not supported");
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);

		// 异常处理
		if (mAdapter == null) {
			return;
		}

		// 当前ListView没有任何子视图(Item)，所以依次在从上向下填充子视图
		if (getChildCount() == 0) {
			mLastItemPosition = -1;
			// add and measure
			fillListDown(mListTop, 0);
		} else {
			final int offset = mListTop + mListTopOffset
					- getChildTop(getChildAt(0));
			// 移除可视区域的都干掉
			removeNonVisibleViews(offset);
			fillList(offset);
		}

		// layout，添加测量完后，获取视图摆放位置
		positioinItems();

		// draw， 上面子视图都添加完了，重绘布局把子视图绘制出来吧
		invalidate();
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			startTouch(ev);
			return false;

		case MotionEvent.ACTION_HOVER_MOVE:
			return startScrollIfNeeded((int) ev.getY());

		default:
			endTouch(0);
			return false;
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (getChildCount() == 0) {
			return false;
		}

		final int y = (int) event.getY();

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			startTouch(event);
			break;

		case MotionEvent.ACTION_MOVE:
			if (mTouchMode == TOUCH_MODE_DOWN) {
				startScrollIfNeeded(y);
			} else if (mTouchMode == TOUCH_MODE_SCROLL) {
				mVelocityTracker.addMovement(event);
				scrollList(y - mTouchStartY);
			}
			break;

		case MotionEvent.ACTION_UP:
			float velocity = 0;
			// 如果当前触摸没有触发滚动，状态依然是DOWN
			// 说明是点击某一个Item
			if (mTouchMode == TOUCH_MODE_DOWN) {
				clickChildAt((int) event.getX(), y);
			} else if (mTouchMode == TOUCH_MODE_SCROLL) {
				mVelocityTracker.addMovement(event);
				mVelocityTracker.computeCurrentVelocity(PIXELS_PER_SECOND);
				velocity = mVelocityTracker.getYVelocity();
			}

			endTouch(velocity);
			break;

		default:
			endTouch(0);
			break;
		}

		return true;
	}

	@Override
	protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
		final Bitmap bitmap = child.getDrawingCache();
		if (bitmap == null) {
			return super.drawChild(canvas, child, drawingTime);
		}

		// 当前Item左侧顶部坐标值
		int left = child.getLeft();
		int top = child.getTop();

		// 当前Item中部偏移
		int centerX = child.getWidth() / 2;
		int centerY = child.getHeight() / 2;

		// 计算离中间位置的距离
		float centerScreen = getHeight() / 2;

		// 计算缩放
		// ?
		float distFromCenter = (top + centerY - centerScreen) / centerScreen;
		float scale = (float) (1 - SCALE_DOWN_FACTOR
				* (1 - Math.cos(distFromCenter)));

		// 计算旋转
		float childRotation = mListRotation - 20 * distFromCenter;
		childRotation %= 90;
		if (childRotation < 0) {
			childRotation += 90;
		}

		// 绘制当前Item
		if (childRotation < 45) {
			// 菱角朝上时 - 下侧3D
			drawFace(canvas, bitmap, top, left, centerX, centerY, scale,
					childRotation - 90);
			// 正中心显示的Item
			drawFace(canvas, bitmap, top, left, centerX, centerY, scale,
					childRotation);
		} else {
			// 正中心显示的Item
			drawFace(canvas, bitmap, top, left, centerX, centerY, scale,
					childRotation);
			// 菱角朝上时 - 上侧3D
			drawFace(canvas, bitmap, top, left, centerX, centerY, scale,
					childRotation - 90);
		}

		return false;
	}

	// ===========================================================
	// Private Methods
	// ===========================================================

	/**
	 * 绘制3D界面块
	 * 
	 * @param canvas
	 *            drawChild回调提供的Canvas对象
	 * @param view
	 * @param top
	 * @param left
	 * @param centerX
	 * @param centerY
	 * @param scale
	 * @param rotation
	 */
	private void drawFace(final Canvas canvas, final Bitmap view,
			final int top, final int left, final int centerX,
			final int centerY, final float scale, final float rotation) {

		// 如果之前没有创建新对象
		if (mCamera == null) {
			mCamera = new Camera();
		}

		// 保存，以免以下操作对之后系统使用的Canvas造成影响
		mCamera.save();

		// 平移和旋转Camera
		mCamera.translate(0, 0, centerY);
		mCamera.rotateX(rotation);
		mCamera.translate(0, 0, -centerY);

		// 如果之前没有Matrix创建新对象
		if (mMatrix == null) {
			mMatrix = new Matrix();
		}

		mCamera.getMatrix(mMatrix);
		mCamera.restore();

		// 平移和缩放Matrix
		mMatrix.preTranslate(-centerX, -centerY);
		mMatrix.postScale(scale, scale);
		mMatrix.postTranslate(left + centerX, top + centerY);

		// 创建和初始化
		if (mPaint == null) {
			mPaint = new Paint();
			mPaint.setAntiAlias(true);
			mPaint.setFilterBitmap(true);
		}

		//
		if (mLightEnabled) {
			mPaint.setColorFilter(calculateLight(rotation));
		} else {
			//
			mPaint.setAlpha(0xFF - (int) (2 * Math.abs(rotation)));
		}

		// 绘制Bitmap
		canvas.drawBitmap(view, mMatrix, mPaint);

	}

	private LightingColorFilter calculateLight(final float rotation) {
		final double cosRotation = Math.cos(Math.PI * rotation / 180);
		int intensity = AMBIENT_LIGHT + (int) (DIFFUSE_LIGHT * cosRotation);
		int highlightIntensity = (int) (SPECULAR_LIGHT * Math.pow(cosRotation,
				SHININESS));

		if (intensity > MAX_INTENSITY) {
			intensity = MAX_INTENSITY;
		}
		if (highlightIntensity > MAX_INTENSITY) {
			highlightIntensity = MAX_INTENSITY;
		}

		final int light = Color.rgb(intensity, intensity, intensity);
		final int highlight = Color.rgb(highlightIntensity, highlightIntensity,
				highlightIntensity);

		return new LightingColorFilter(light, highlight);
	}

	/**
	 * ListView初始化
	 */
	private void initListView(Context context) {

		final ViewConfiguration configuration = ViewConfiguration.get(context);
		mTouchSlop = configuration.getScaledTouchSlop();
	}

	/**
	 * 向当前ListView添加子视图并负责Measure子视图操作
	 * 
	 * @param child
	 *            需要添加的ListView子视图(Item)
	 * @param layoutMode
	 *            在顶部添加上面添加还是在底部下面添加子视图 ， LAYOUT_MODE_ABOVE 或 LAYOUT_MODE_BELOW
	 */
	private void addAndMeasureChild(View child, int layoutMode) {
		LayoutParams params = child.getLayoutParams();
		if (params == null) {
			params = new LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
		}

		// addViewInLayout index？
		final int index = layoutMode == LAYOUT_MODE_ABOVE ? 0 : -1;
		child.setDrawingCacheEnabled(true);
		addViewInLayout(child, index, params, true);

		final int itemWidth = (int) (getWidth() * ITEM_WIDTH);
		child.measure(MeasureSpec.EXACTLY | itemWidth, MeasureSpec.UNSPECIFIED);
	}

	/**
	 * 对所有子视图进行layout操作，取得所有子视图正确的位置
	 */
	private void positioinItems() {
		int top = mListTop + mListTopOffset;
		final float amplitude = getWidth() * AMPLITUDE;
		final float frequency = 1 / (getHeight() * WAVELENGTH);

		for (int i = 0; i < getChildCount(); i++) {
			final View child = getChildAt(i);

			final int offset = (int) (amplitude * Math.sin(2 * Math.PI
					* frequency * top));
			// 当前视图未虽然添加到ViewGroup但是还未重新进行measure, layout, draw操作
			// 直接通过child.getWidth();获取不到宽度
			final int width = child.getMeasuredWidth();
			final int height = child.getMeasuredHeight();
			final int left = offset + (getWidth() - width) / 2;
			final int margin = getChildMargin(child);
			final int childTop = top + margin;

			child.layout(left, childTop, left + width, childTop + height);
			// 上下都需要Margin
			top += height + 2 * margin;
		}
	}

	/**
	 * 初始化用于之后触摸事件判断处理的参数
	 * 
	 * @param event
	 */
	private void startTouch(MotionEvent event) {
		removeCallbacks(mDynamicsRunnable);

		mTouchStartX = (int) event.getX();
		mMotionY = mTouchStartY = (int) event.getY();

		mListTopStart = getChildTop(getChildAt(0)) - mListTopOffset;

		startLongPressCheck();

		mVelocityTracker = VelocityTracker.obtain();
		mVelocityTracker.addMovement(event);

		mTouchMode = TOUCH_MODE_DOWN;
	}

	/**
	 * 是否满足滚动条件
	 * 
	 * @param y
	 *            当前触摸点Y轴的值
	 * @return true 可以滚动
	 */
	private boolean startScrollIfNeeded(int y) {
		// 不同，此处模拟AbsListView实现

		final int deltaY = y - mMotionY;
		final int distance = Math.abs(deltaY);

		// 只有移动一定距离之后才认为目的是想让ListView滚动
		if (distance > mTouchSlop) {

			// 记录当前处于滚动状态
			mTouchMode = TOUCH_MODE_SCROLL;
			return true;
		}

		return false;
	}

	/**
	 * 通过触摸点X,Y轴坐标获取是点击哪一个Item
	 * 
	 * @param x
	 *            触摸点X轴值
	 * @param y
	 *            触摸点Y轴值
	 * @return
	 */
	private int getContainingChildIndex(int x, int y) {
		if (mRect == null) {
			mRect = new Rect();
		}

		// 遍历当前ListView所有Item
		for (int i = 0; i < getChildCount(); i++) {
			getChildAt(i).getHitRect(mRect);
			// x,y是否在当前视图区域内
			if (mRect.contains(x, y)) {
				return i;
			}
		}

		return INVALID_POSITION;
	}

	/**
	 * 控制ListView进行滚动
	 * 
	 * @param scrolledDistance
	 *            当前手指坐在位置与刚触摸到屏幕之间的距离, 也就是当前手指在屏幕上Y轴总移动位置
	 */
	private void scrollList(final int scrolledDistance) { // scrollIfNeeded
		// 改变当前记录的ListView顶部位置
		mListTop = mListTopStart + scrolledDistance;

		if (mRotationEnabled) {
			mListRotation = -(DEGREES_PER_SCREEN * mListTop) / getHeight();
		}

		setSnapPoint();

		// 关键，要想使相面的计算生效必须重新请求布局
		// 会触发当前onLayout方法，指定Item位置与绘制先关还是在onLayout中
		requestLayout();
	}

	/**
	 * ListView向上或者向下移动后需要向顶部或者底部添加视图
	 * 
	 * @param offset
	 */
	private void fillList(final int offset) {
		// 最后一个item的下边界值就是当前ListView的下边界值
		final int bottomEdge = getChildBottom(getChildAt(getChildCount() - 1));
		fillListDown(bottomEdge, offset);

		// 第一个Item的上边界值就是ListVie的上边界值
		final int topEdge = getChildTop(getChildAt(0));
		fillListUp(topEdge, offset);
	}

	/**
	 * 与fillListDown相反方向添加
	 * 
	 * @param topEdge
	 *            当前第一个子视图顶部边界值
	 * @param offset
	 *            显示区域偏移量
	 */
	private void fillListUp(int topEdge, int offset) {
		while (topEdge + offset > 0 && mFirstItemPosition > 0) {
			// 现在添加的视图时当前子视图前面，所以位置-1
			mFirstItemPosition--;

			View newTopChild = mAdapter.getView(mFirstItemPosition,
					getCachedView(), this);
			addAndMeasureChild(newTopChild, LAYOUT_MODE_ABOVE);
			int childHeight = getChildHeight(newTopChild);
			topEdge -= childHeight;

			// 在顶部添加视图后，更新顶部偏移
			mListTopOffset -= childHeight;
		}
	}

	/**
	 * 向当前最后一个子视图下面添加，填充到当前ListView底部无再可填充区域为止
	 * 
	 * @param bottomEdge
	 *            当前最后一个子视图底部边界值
	 * @param offset
	 *            显示区域偏移量
	 */
	private void fillListDown(int bottomEdge, int offset) {
		while (bottomEdge + offset < getHeight()
				&& mLastItemPosition < mAdapter.getCount() - 1) {
			// 现在添加的视图时当前子视图后面，所以位置+1
			mLastItemPosition++;

			// 数据和视图通过Adapter适配，此处从Adapter获取视图。
			// 第二个参数传入复用的View对象，先出入null，之后再添加View对象复用机制
			View newBottomChild = mAdapter.getView(mLastItemPosition,
					getCachedView(), this);
			// **具体添加视图处理
			addAndMeasureChild(newBottomChild, LAYOUT_MODE_BELOW);
			// 添加一个子视图(Item)，随之底部边界也发生改变
			bottomEdge += getChildHeight(newBottomChild);
		}
	}

	/**
	 * 触摸屏幕结束，进行清理操作
	 */
	private void endTouch(final float velocity) {
		if (mVelocityTracker != null) {
			mVelocityTracker.recycle();
			mVelocityTracker = null;
		}

		// 都结束了，无论是否执行了，干掉长按子线程
		removeCallbacks(mLongPressRunnable);

		if (mDynamicsRunnable == null) {
			mDynamicsRunnable = new Runnable() {
				public void run() {
					if (mDynamics == null) {
						return;
					}
					mListTopStart = getChildTop(getChildAt(0)) - mListTopOffset;
					mDynamics.update(AnimationUtils
							.currentAnimationTimeMillis());

					scrollList((int) mDynamics.getPosition() - mListTopStart);

					if (!mDynamics.isAtRest(VELOCITY_TOLERANCE,
							POSITION_TOLERANCE)) {
						postDelayed(this, 16);
					}

				}
			};
		}

		if (mDynamics != null) {
			// update the dynamics with the correct position and start the
			// runnable
			mDynamics.setState(mListTop, velocity,
					AnimationUtils.currentAnimationTimeMillis());
			post(mDynamicsRunnable);
		}

		mTouchMode = TOUCH_MODE_REST;
	}

	/**
	 * 调用ItemClickListener提供当前点击位置
	 * 
	 * @param x
	 *            触摸点X轴值
	 * @param y
	 *            触摸点Y轴值
	 */
	private void clickChildAt(int x, int y) {
		// 触摸点在当前显示所有Item中哪一个
		final int itemIndex = getContainingChildIndex(x, y);

		if (itemIndex != INVALID_INDEX) {
			final View itemView = getChildAt(itemIndex);
			// 当前Item在ListView所有Item中的位置
			final int position = mFirstItemPosition + itemIndex;
			final long id = mAdapter.getItemId(position);

			// 调用父类方法，会触发ListView ItemClickListener
			performItemClick(itemView, position, id);
		}
	}

	/**
	 * 开启异步线程，条件允许时调用LongClickListener
	 */
	private void startLongPressCheck() {
		// 创建子线程
		if (mLongPressRunnable == null) {
			mLongPressRunnable = new Runnable() {

				@Override
				public void run() {
					if (mTouchMode == TOUCH_MODE_DOWN) {
						final int index = getContainingChildIndex(mTouchStartX,
								mTouchStartY);
						if (index != INVALID_INDEX) {
							longClickChild(index);
						}
					}
				}
			};
		}

		// ViewConfiguration.getLongPressTimeout() 获取系统配置的长按的时间间隔
		// 如果点击已经超过长按要求时间，才开始执行此线程
		postDelayed(mLongPressRunnable, ViewConfiguration.getLongPressTimeout());
	}

	/**
	 * 调用ItemLongClickListener提供点击位置等信息
	 * 
	 * @param index
	 *            Item索引值
	 */
	private void longClickChild(final int index) {
		final View itemView = getChildAt(index);
		final int position = mFirstItemPosition + index;
		final long id = mAdapter.getItemId(position);
		// 从父类获取绑定的OnItemLongClickListener
		OnItemLongClickListener listener = getOnItemLongClickListener();

		if (listener != null) {
			listener.onItemLongClick(this, itemView, position, id);
		}
	}

	/**
	 * 删除当前已经移除可视范围的Item View
	 * 
	 * @param offset
	 *            可视区域偏移量
	 */
	private void removeNonVisibleViews(final int offset) {
		int childCount = getChildCount();

		/** ListView向上滚动，删除顶部移除可视区域的所有视图 **/

		// 不在ListView底部，子视图大于1
		if (mLastItemPosition != mAdapter.getCount() - 1 && childCount > 1) {
			View firstChild = getChildAt(0);
			// 通过第二条件判断当前最上面的视图是否被移除可是区域
			while (firstChild != null
					&& getChildBottom(firstChild) + offset < 0) {
				// 既然顶部第一个视图已经移除可视区域从当前ViewGroup中删除掉
				removeViewInLayout(firstChild);
				// 用于下次判断，是否当前顶部还有需要移除的视图
				childCount--;
				// View对象回收，目的是为了复用
				mCachedItemViews.addLast(firstChild);
				// 既然最上面的视图被干掉了，当前ListView第一个显示视图也需要+1
				mFirstItemPosition++;
				// 同上更新
				mListTopOffset += getChildHeight(firstChild);

				// 为下一次while遍历获取参数
				if (childCount > 1) {
					// 当前已经删除第一个，再接着去除删除后剩余的第一个
					firstChild = getChildAt(0);
				} else {
					// 没啦
					firstChild = null;
				}
			}
		}

		/** ListView向下滚动，删除底部移除可视区域的所有视图 **/
		// 与上面操作一样，只是方向相反一个顶部操作一个底部操作
		if (mFirstItemPosition != 0 && childCount > 1) {
			View lastChild = getChildAt(childCount - 1);
			while (lastChild != null
					&& getChildTop(lastChild) + offset > getHeight()) {
				removeViewInLayout(lastChild);
				childCount--;
				mCachedItemViews.addLast(lastChild);
				mLastItemPosition--;

				if (childCount > 1) {
					lastChild = getChildAt(childCount - 1);
				} else {
					lastChild = null;
				}
			}
		}

	}

	/**
	 * 获取一个可以复用的Item View
	 * 
	 * @return view 可以复用的视图或者null
	 */
	private View getCachedView() {

		if (mCachedItemViews.size() != 0) {
			return mCachedItemViews.removeFirst();
		}

		return null;
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

}
