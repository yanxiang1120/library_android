package com.yeyanxiang.view.timedatedialog;

import java.text.DateFormatSymbols;
import java.util.Calendar;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;

/**
 * @author 叶雁翔
 * 
 * @Email yanxiang1120@gmail.com
 * 
 * @version 1.0
 * 
 * @update 2014年3月5日
 * 
 * @简介 获取系统日期和时间
 */
public class DateTimeDialog extends AlertDialog implements OnClickListener,
		OnDateChangedListener, OnTimeChangedListener {
	private static final String YEAR = "year";
	private static final String MONTH = "month";
	private static final String DAY = "day";
	private static final String HOUR = "hour";
	private static final String MINUTE = "minute";
	private static final String IS_24_HOUR = "is24hour";

	private final DatePicker mDatePicker;
	private final OnDateTimeSetListener CallBack;
	private final Calendar mCalendar;
	private final java.text.DateFormat mTitleDateFormat;
	private final String[] mWeekDays;
	private final TimePicker mTimePicker;
	private final java.text.DateFormat mDateFormat;

	private int mInitialYear;
	private int mInitialMonth;
	private int mInitialDay;
	private int mInitialHourOfDay;
	private int mInitialMinute;
	private boolean mIs24HourView;

	private int currentYear;
	private int currentMonth;
	private int currentDay;
	private int currentHourOfDay;
	private int currentMinute;

	/**
	 * The callback used to indicate the user is done filling in the date.
	 */
	public interface OnDateTimeSetListener {

		/**
		 * @param datePicker
		 *            The view associated with this listener.
		 * @param timePicker
		 *            The view associated with this listener.
		 * @param year
		 *            The year that was set.
		 * @param monthOfYear
		 *            The month that was set (0-11) for compatibility with
		 *            {@link java.util.Calendar}.
		 * @param dayOfMonth
		 *            The day of the month that was set.
		 * @param hourOfDay
		 *            The hour that was set.
		 * @param minute
		 *            The minute that was set.
		 */
		void onDateSet(DatePicker datePicker, TimePicker timePicker, int year,
				int monthOfYear, int dayOfMonth, int hourOfDay, int minute);
	}

	public DateTimeDialog(Context context, OnDateTimeSetListener callback,
			boolean is24HourView) {
		// 16973979
		this(context, -1, callback, is24HourView);
	}

	public DateTimeDialog(Context context, int theme,
			OnDateTimeSetListener callback, boolean is24HourView) {
		super(context, theme);

		CallBack = callback;

		mIs24HourView = is24HourView;

		DateFormatSymbols symbols = new DateFormatSymbols();
		mWeekDays = symbols.getShortWeekdays();

		mTitleDateFormat = java.text.DateFormat
				.getDateInstance(java.text.DateFormat.FULL);
		mCalendar = Calendar.getInstance();
		mDateFormat = DateFormat.getTimeFormat(context);
		updateTitle(mInitialYear, mInitialMonth, mInitialDay,
				mInitialHourOfDay, mInitialMinute);

		setButton("确定", this);
		setButton2("取消", (OnClickListener) null);

		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		Resources resources = context.getResources();

		View view = inflater.inflate(
				resources.getIdentifier("datetimedialog", "layout",
						context.getPackageName()), null);
		setView(view);
		mDatePicker = (DatePicker) view.findViewById(resources.getIdentifier(
				"datePicker", "id", context.getPackageName()));
		mTimePicker = (TimePicker) view.findViewById(resources.getIdentifier(
				"timePicker", "id", context.getPackageName()));

		// initialize state
		mTimePicker.setIs24HourView(mIs24HourView);
		mTimePicker.setOnTimeChangedListener(this);

		setDate(Calendar.getInstance());
	}

	@Override
	public void show() {
		super.show();

		/*
		 * Sometimes the full month is displayed causing the title to be very
		 * long, in those cases ensure it doesn't wrap to 2 lines (as that looks
		 * jumpy) and ensure we ellipsize the end.
		 */
		// TextView title = (TextView) findViewById(R.id.alertTitle);
		// title.setSingleLine();
		// title.setEllipsize(TruncateAt.END);
		setDate(Calendar.getInstance());
	}

	public void setDate(Calendar calendar) {
		currentYear = mInitialYear = calendar.get(Calendar.YEAR);
		currentMonth = mInitialMonth = calendar.get(Calendar.MONTH);
		currentDay = mInitialDay = calendar.get(Calendar.DAY_OF_MONTH);
		currentHourOfDay = mInitialHourOfDay = calendar
				.get(Calendar.HOUR_OF_DAY);
		currentMinute = mInitialMinute = calendar.get(Calendar.MINUTE);

		mDatePicker.init(mInitialYear, mInitialMonth, mInitialDay, this);
		mTimePicker.setCurrentHour(mInitialHourOfDay);
		mTimePicker.setCurrentMinute(mInitialMinute);
	}

	private void updateTitle(int year, int month, int day, int hour, int minute) {
		mCalendar.set(Calendar.YEAR, year);
		mCalendar.set(Calendar.MONTH, month);
		mCalendar.set(Calendar.DAY_OF_MONTH, day);
		mCalendar.set(Calendar.HOUR_OF_DAY, hour);
		mCalendar.set(Calendar.MINUTE, minute);
		setTitle(mTitleDateFormat.format(mCalendar.getTime()) + " "
				+ mDateFormat.format(mCalendar.getTime()));
	}

	@Override
	public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
		// TODO Auto-generated method stub
		currentHourOfDay = hourOfDay;
		currentMinute = minute;
		updateTitle(currentYear, currentMonth, currentDay, currentHourOfDay,
				currentMinute);
	}

	@Override
	public void onDateChanged(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {
		// TODO Auto-generated method stub
		currentYear = year;
		currentMonth = monthOfYear;
		currentDay = dayOfMonth;
		updateTitle(currentYear, currentMonth, currentDay, currentHourOfDay,
				currentMinute);
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		// TODO Auto-generated method stub
		if (CallBack != null) {
			mDatePicker.clearFocus();
			mTimePicker.clearFocus();
			CallBack.onDateSet(mDatePicker, mTimePicker, mDatePicker.getYear(),
					mDatePicker.getMonth(), mDatePicker.getDayOfMonth(),
					mTimePicker.getCurrentHour(),
					mTimePicker.getCurrentMinute());
		}
	}

	public void updateDate(int year, int monthOfYear, int dayOfMonth) {
		mInitialYear = year;
		mInitialMonth = monthOfYear;
		mInitialDay = dayOfMonth;
		mDatePicker.updateDate(year, monthOfYear, dayOfMonth);
	}

	@Override
	public Bundle onSaveInstanceState() {
		Bundle state = super.onSaveInstanceState();
		state.putInt(YEAR, mDatePicker.getYear());
		state.putInt(MONTH, mDatePicker.getMonth());
		state.putInt(DAY, mDatePicker.getDayOfMonth());
		state.putInt(HOUR, mTimePicker.getCurrentHour());
		state.putInt(MINUTE, mTimePicker.getCurrentMinute());
		state.putBoolean(IS_24_HOUR, mTimePicker.is24HourView());
		return state;
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		int year = savedInstanceState.getInt(YEAR);
		int month = savedInstanceState.getInt(MONTH);
		int day = savedInstanceState.getInt(DAY);
		mDatePicker.init(year, month, day, this);
		int hour = savedInstanceState.getInt(HOUR);
		int minute = savedInstanceState.getInt(MINUTE);
		mTimePicker.setCurrentHour(hour);
		mTimePicker.setCurrentMinute(minute);
		mTimePicker.setIs24HourView(savedInstanceState.getBoolean(IS_24_HOUR));
		mTimePicker.setOnTimeChangedListener(this);
		updateTitle(year, month, day, hour, minute);
	}

}
