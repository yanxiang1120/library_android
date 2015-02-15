package com.yeyanxiang.view.listview3D;

/**
 * 
 * Create on 2013-5-8 下午3:04:46 </br> Copyright: Copyright(c) 2013 by 叶雁翔</br>
 * 
 * 简介: Fling效果辅助类
 * 
 * @Version 1.0
 * @Author <a href="mailto:yanxiang1120@126.com">叶雁翔</a>
 * 
 * 
 */
public abstract class Dynamics {
	// ===========================================================
	// Constants
	// ===========================================================

	private static final int MAX_TIMESTEP = 50;

	// ===========================================================
	// Fields
	// ===========================================================

	/** 当前位置 **/
	protected float mPosition;
	/** 当前速度 **/
	protected float mVelocity;
	/** 可移动最大位置 **/
	protected float mMaxPosition = Float.MAX_VALUE;
	/** 可移动最小位置 **/
	protected float mMinPosition = -Float.MAX_VALUE;
	/** 记录上一次更新时的时间值 **/
	protected long mLastTime = 0;

	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Setter
	// ===========================================================

	/**
	 * 设置初始状态
	 * 
	 * @param position
	 *            当前位置
	 * @param velocity
	 *            当前速度
	 * @param now
	 *            当期时间
	 */
	public void setState(final float position, final float velocity,
			final long now) {
		mVelocity = velocity;
		mPosition = position;
		mLastTime = now;
	}

	/**
	 * 设置最大可移动到的位置
	 * 
	 * @param maxPosition
	 */
	public void setMaxPosition(final float maxPosition) {
		mMaxPosition = maxPosition;
	}

	/**
	 * 设置最小可移动到的位置
	 * 
	 * @param minPosition
	 */
	public void setMinPosition(final float minPosition) {
		mMinPosition = minPosition;
	}

	// ===========================================================
	// Getter
	// ===========================================================

	/**
	 * 获取当前位置
	 * 
	 * @return
	 */
	public float getPosition() {
		return mPosition;
	}

	/**
	 * 获取当前速度
	 * 
	 * @return
	 */
	public float getVelocity() {
		return mVelocity;
	}

	/**
	 * 是否在恢复状态
	 * 
	 * @param velocityTolerance
	 * @param positionTolerance
	 * @return
	 */
	public boolean isAtRest(final float velocityTolerance,
			final float positionTolerance) {
		final boolean standingStill = Math.abs(mVelocity) < velocityTolerance;
		final boolean withinLimits = mPosition - positionTolerance < mMaxPosition
				&& mPosition + positionTolerance > mMinPosition;
		return standingStill && withinLimits;
	}

	/**
	 * 更新
	 * 
	 * @param now
	 */
	public void update(final long now) {
		int dt = (int) (now - mLastTime);
		if (dt > MAX_TIMESTEP) {
			dt = MAX_TIMESTEP;
		}

		onUpdate(dt);

		mLastTime = now;
	}

	/**
	 * 获取受限制后的可移动距离
	 * 
	 * @return
	 */
	protected float getDistanceToLimit() {
		float distanceToLimit = 0;

		if (mPosition > mMaxPosition) {
			distanceToLimit = mMaxPosition - mPosition;
		} else if (mPosition < mMinPosition) {
			distanceToLimit = mMinPosition - mPosition;
		}

		return distanceToLimit;
	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	/**
	 * @param dt
	 */
	abstract protected void onUpdate(int dt);

	// ===========================================================
	// Private Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

}
