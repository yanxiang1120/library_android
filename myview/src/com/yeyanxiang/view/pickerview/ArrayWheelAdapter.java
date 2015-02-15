package com.yeyanxiang.view.pickerview;

/**
 * @author 叶雁翔
 * 
 * @Email yanxiang1120@gmail.com
 * 
 * @version 1.0
 * 
 * @data 2013年12月27日 下午1:47:52
 * 
 * @简介
 */
public class ArrayWheelAdapter implements WheelAdapter {

	/** The default items length */
	public static final int DEFAULT_LENGTH = -1;

	// items
	private String items[];
	// length
	private int length;

	/**
	 * Constructor
	 * 
	 * @param items
	 *            the items
	 * @param length
	 *            the max items length
	 */
	public ArrayWheelAdapter(String items[], int length) {
		this.items = items;
		this.length = length;
	}

	/**
	 * Contructor
	 * 
	 * @param items
	 *            the items
	 */
	public ArrayWheelAdapter(String items[]) {
		this(items, DEFAULT_LENGTH);
	}

	@Override
	public String getItem(int index) {
		if (index >= 0 && index < items.length) {
			return items[index];
		}
		return null;
	}

	@Override
	public int getItemsCount() {
		return items.length;
	}

	@Override
	public int getMaximumLength() {

		if (length == DEFAULT_LENGTH) {
			for (String string : items) {
				length = Math.max(length, string.length() * 2);
			}
		}
		return length;
	}
}
