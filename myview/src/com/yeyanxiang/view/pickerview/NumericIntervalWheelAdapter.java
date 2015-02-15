package com.yeyanxiang.view.pickerview;

import java.text.DecimalFormat;

import android.text.TextUtils;

/**
 * @author 叶雁翔
 * 
 * @Email yanxiang1120@gmail.com
 * 
 * @version 1.0
 * 
 * @data 2014年1月9日 下午4:31:32
 * 
 * @简介
 */
public class NumericIntervalWheelAdapter implements WheelAdapter {

	/** The default min value */
	public static final int DEFAULT_MAX_VALUE = 9;

	/** The default max value */
	private static final int DEFAULT_MIN_VALUE = 0;

	// Values
	private int minValue;
	private int maxValue;
	private static int interval = 1;

	private DecimalFormat decimalFormat;

	/**
	 * Default constructor
	 */
	public NumericIntervalWheelAdapter() {
		this(DEFAULT_MIN_VALUE, DEFAULT_MAX_VALUE, interval);
	}

	/**
	 * Constructor
	 * 
	 * @param minValue
	 *            the wheel min value
	 * @param maxValue
	 *            the wheel max value
	 */
	public NumericIntervalWheelAdapter(int minValue, int maxValue, int interval) {
		this(minValue, maxValue, null, interval);
	}

	/**
	 * Constructor
	 * 
	 * @param minValue
	 *            the wheel min value
	 * @param maxValue
	 *            the wheel max value
	 * @param format
	 *            the format string
	 */
	public NumericIntervalWheelAdapter(int minValue, int maxValue,
			String format, int interval) {
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.interval = interval;
		if (!TextUtils.isEmpty(format)) {
			decimalFormat = new DecimalFormat(format);
		}
	}

	@Override
	public String getItem(int index) {
		if (index >= 0 && index < getItemsCount()) {
			int value = minValue + index;
			if (interval > 1) {
				if (minValue % 5 == 0) {
					value = minValue + index * interval;
				} else {
					value = minValue + (index + 1) * interval - minValue
							% interval;
				}
			}
			return decimalFormat != null ? decimalFormat.format(value)
					: Integer.toString(value);
		}
		return null;
	}

	@Override
	public int getItemsCount() {
		if (interval > 1) {
			if (maxValue < interval) {
				return 0;
			}
			if (minValue % interval == 0) {
				return (maxValue - minValue) / 5 + 1;
			} else {
				if (maxValue % interval == 0) {
					return (maxValue - minValue) / 5 + 1;
				} else {
					return (maxValue - minValue) / 5;
				}
			}
		}
		return maxValue - minValue + 1;
	}

	@Override
	public int getMaximumLength() {
		int max = Math.max(Math.abs(maxValue), Math.abs(minValue));
		int maxLen = Integer.toString(max).length();
		if (minValue < 0) {
			maxLen++;
		}
		return maxLen;
	}
}