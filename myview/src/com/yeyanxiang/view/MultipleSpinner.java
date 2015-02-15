package com.yeyanxiang.view;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

/**
 * 
 * Create on 2013-4-28 上午11:29:02 </br> Copyright: Copyright(c) 2013 by 叶雁翔</br>
 * 
 * 简介: 多选Spinner
 * 
 * @Version 1.0
 * @Author <a href="mailto:yanxiang1120@126.com">叶雁翔</a>
 * 
 * 
 */
public class MultipleSpinner extends Spinner implements
		OnMultiChoiceClickListener {
	private CharSequence mPrompt;
	private AlertDialog mPopup;
	private String[] mitems;
	private boolean[] checkitems;
	private String[] spinneritems;
	private String itemString;

	public MultipleSpinner(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public MultipleSpinner(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public MultipleSpinner(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Sets the prompt to display when the dialog is shown.
	 * 
	 * @param prompt
	 *            the prompt to set
	 */
	public void setPrompt(CharSequence prompt) {
		mPrompt = prompt;
	}

	@Override
	public boolean performClick() {

		Context context = getContext();
		Builder mPopupBuilder = new AlertDialog.Builder(context);
		if (mPrompt != null) {
			mPopupBuilder.setTitle(mPrompt);
		}
		itemString = "";
		spinneritems[0] = mitems[0];
		resetcheckitems(false);
		mPopupBuilder.setMultiChoiceItems(mitems, checkitems, this)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						if ("".equals(itemString)) {
							itemString = spinneritems[0];
						} else {
							spinneritems[0] = itemString;
							setAdapter(new ArrayAdapter<String>(getContext(),
									android.R.layout.simple_spinner_item,
									spinneritems));
						}
					}
				}).setNegativeButton("取消", null);
		mPopup = mPopupBuilder.show();
		return true;
	}

	private void resetcheckitems(boolean flag) {
		// TODO Auto-generated method stub
		for (int i = 0; i < checkitems.length; i++) {
			checkitems[i] = flag;
		}
	}

	@Override
	public void onClick(DialogInterface dialog, int which, boolean isChecked) {
		// TODO Auto-generated method stub
		if (isChecked) {
			if (itemString.length() > 0) {
				itemString += "," + mitems[which];
			} else {
				itemString += mitems[which];
			}
		} else {
			if (itemString.contains("," + mitems[which])) {
				itemString = itemString.replace("," + mitems[which], "");
			} else if (itemString.contains(mitems[which] + ",")) {
				itemString = itemString.replace(mitems[which] + ",", "");
			}
		}
	}

	public void setItems(String[] items) {
		mitems = items;
		checkitems = new boolean[mitems.length];
		resetcheckitems(false);

		spinneritems = new String[] { items[0] };
		setAdapter(new ArrayAdapter<String>(getContext(),
				android.R.layout.simple_spinner_item, spinneritems));
	}
}
