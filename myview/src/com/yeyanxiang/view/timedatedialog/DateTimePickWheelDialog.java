package com.yeyanxiang.view.timedatedialog;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import com.yeyanxiang.view.pickerview.NumericWheelAdapter;
import com.yeyanxiang.view.pickerview.OnWheelChangedListener;
import com.yeyanxiang.view.pickerview.WheelView;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;

/**
 * @author 叶雁翔
 * 
 * @Email yanxiang1120@gmail.com
 * 
 * @version 1.0
 * 
 * @update 2014年3月5日
 * 
 * @简介 获取系统日期和时间 iphone风格
 */
public class DateTimePickWheelDialog extends AlertDialog {

	private static int START_YEAR = 2000, END_YEAR = 2100;
	private WheelView wv_year;
	private WheelView wv_month;
	private WheelView wv_day;
	private WheelView wv_hours;
	private WheelView wv_mins;
	String[] months_big = { "1", "3", "5", "7", "8", "10", "12" };
	String[] months_little = { "4", "6", "9", "11" };

	final List<String> list_big = Arrays.asList(months_big);
	final List<String> list_little = Arrays.asList(months_little);
	private final Context mContext;
	private Calendar calendar;

	public DateTimePickWheelDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.mContext = context;
		String packagename = context.getPackageName();
		Resources resources = context.getResources();
		View view = LayoutInflater.from(context).inflate(
				resources.getIdentifier("pickerdatetimedialog", "layout",
						packagename), null);
		setView(view);
		findView(view, resources, packagename);
		adjustView();
		calendar = Calendar.getInstance();
		setDate(calendar);
	}

	private void adjustView() {
		// TODO Auto-generated method stub
		// 根据屏幕密度来指定选择器字体的大小
		int textSize = 0;

		textSize = pixelsToDip(mContext.getResources(), 16);

		wv_day.setTextSize(textSize);
		wv_hours.setTextSize(textSize);
		wv_mins.setTextSize(textSize);
		wv_month.setTextSize(textSize);
		wv_year.setTextSize(textSize);
	}

	public static int pixelsToDip(Resources res, int pixels) {
		final float scale = res.getDisplayMetrics().density;
		return (int) (pixels * scale + 3.0f);
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub
		super.show();
	}

	private void findView(View view, Resources resources, String packagename) {
		// TODO Auto-generated method stub
		// 年
		wv_year = (WheelView) view.findViewById(resources.getIdentifier(
				"pickeryear", "id", packagename));
		wv_year.setLeft_Offset(10);
		wv_year.setLable_Offset(0);
		wv_year.setLabel("年");// 添加文字
		wv_year.setAdapter(new NumericWheelAdapter(START_YEAR, END_YEAR));// 设置"年"的显示数据

		// 月
		wv_month = (WheelView) view.findViewById(resources.getIdentifier(
				"pickermonth", "id", packagename));
		wv_month.setLeft_Offset(10);
		wv_month.setLable_Offset(0);
		wv_month.setLabel("月");
		wv_month.setAdapter(new NumericWheelAdapter(1, 12));

		// 日
		wv_day = (WheelView) view.findViewById(resources.getIdentifier(
				"pickerday", "id", packagename));
		// 判断大小月及是否闰年,用来确定"日"的数据
		wv_day.setLeft_Offset(10);
		wv_day.setLable_Offset(0);
		wv_day.setLabel("日");
		wv_year.addChangingListener(wheelListener_year);
		wv_month.addChangingListener(wheelListener_month);

		// 时
		wv_hours = (WheelView) view.findViewById(resources.getIdentifier(
				"pickerhour", "id", packagename));
		wv_hours.setLeft_Offset(10);
		wv_hours.setLable_Offset(0);
		wv_hours.setLabel("时");
		wv_hours.setAdapter(new NumericWheelAdapter(0, 23));

		// 分
		wv_mins = (WheelView) view.findViewById(resources.getIdentifier(
				"pickermins", "id", packagename));
		wv_mins.setLeft_Offset(10);
		wv_mins.setLable_Offset(0);
		wv_mins.setLabel("分");
		wv_mins.setAdapter(new NumericWheelAdapter(0, 59));

	}

	// 添加"年"监听
	private final OnWheelChangedListener wheelListener_year = new OnWheelChangedListener() {
		@Override
		public void onChanged(WheelView wheel, int oldValue, int newValue) {
			int year_num = newValue + START_YEAR;
			// 判断大小月及是否闰年,用来确定"日"的数据
			if (list_big
					.contains(String.valueOf(wv_month.getCurrentItem() + 1))) {
				wv_day.setAdapter(new NumericWheelAdapter(1, 31));
			} else if (list_little.contains(String.valueOf(wv_month
					.getCurrentItem() + 1))) {
				wv_day.setAdapter(new NumericWheelAdapter(1, 30));
			} else {
				if ((year_num % 4 == 0 && year_num % 100 != 0)
						|| year_num % 400 == 0)
					wv_day.setAdapter(new NumericWheelAdapter(1, 29));
				else
					wv_day.setAdapter(new NumericWheelAdapter(1, 28));
			}
		}
	};
	// 添加"月"监听
	private final OnWheelChangedListener wheelListener_month = new OnWheelChangedListener() {
		@Override
		public void onChanged(WheelView wheel, int oldValue, int newValue) {
			int month_num = newValue + 1;
			// 判断大小月及是否闰年,用来确定"日"的数据
			if (list_big.contains(String.valueOf(month_num))) {
				wv_day.setAdapter(new NumericWheelAdapter(1, 31));
			} else if (list_little.contains(String.valueOf(month_num))) {
				wv_day.setAdapter(new NumericWheelAdapter(1, 30));
			} else {
				if (((wv_year.getCurrentItem() + START_YEAR) % 4 == 0 && (wv_year
						.getCurrentItem() + START_YEAR) % 100 != 0)
						|| (wv_year.getCurrentItem() + START_YEAR) % 400 == 0)
					wv_day.setAdapter(new NumericWheelAdapter(1, 29));
				else
					wv_day.setAdapter(new NumericWheelAdapter(1, 28));
			}
		}
	};

	public void setPositiveButton(CharSequence mPositiveButtonText,
			DialogInterface.OnClickListener onClickListener) {
		setButton(mPositiveButtonText, onClickListener);
	}

	public void setNegativeButton(CharSequence mNegativeButtonText,
			DialogInterface.OnClickListener onClickListener) {
		setButton2(mNegativeButtonText, onClickListener);
	}

	public void setCalendar(Calendar calendar) {
		// TODO Auto-generated method stub
		this.calendar = calendar;
		setDate(calendar);
	}

	public DateTimePickWheelDialog setDate(Calendar calendar) {
		if (calendar == null)
			return this;
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int minute = calendar.get(Calendar.MINUTE);

		wv_year.setCurrentItem(year - START_YEAR);// 初始化时显示的数据
		wv_month.setCurrentItem(month);
		if (list_big.contains(String.valueOf(month + 1))) {
			wv_day.setAdapter(new NumericWheelAdapter(1, 31));
		} else if (list_little.contains(String.valueOf(month + 1))) {
			wv_day.setAdapter(new NumericWheelAdapter(1, 30));
		} else {
			// 闰年
			if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0)
				wv_day.setAdapter(new NumericWheelAdapter(1, 29));
			else
				wv_day.setAdapter(new NumericWheelAdapter(1, 28));
		}
		wv_day.setCurrentItem(day - 1);
		wv_hours.setCurrentItem(hour);
		wv_mins.setCurrentItem(minute);
		return this;
	}

	public Calendar getCalendar() {
		// TODO Auto-generated method stub
		if (calendar == null) {
			calendar = Calendar.getInstance();
		}
		calendar.set(wv_year.getCurrentItem() + START_YEAR,
				wv_month.getCurrentItem(), wv_day.getCurrentItem() + 1,
				wv_hours.getCurrentItem(), wv_mins.getCurrentItem());
		return calendar;
	}

	public static class Builder {
		private final DatePickParams P;

		public Builder(Context context) {
			P = new DatePickParams(context);
		}

		public Builder setTitle(CharSequence title) {
			P.mTitle = title;
			return this;
		}

		public Builder setIcon(int iconId) {
			P.mIconId = iconId;
			return this;
		}

		public Builder setPositiveButton(CharSequence text,
				final DialogInterface.OnClickListener listener) {
			P.mPositiveButtonText = text;
			P.mPositiveButtonListener = listener;
			return this;
		}

		public Builder setNegativeButton(CharSequence text,
				final DialogInterface.OnClickListener listener) {
			// TODO Auto-generated method stub
			P.mNegativeButtonText = text;
			P.mNegativeButtonListener = listener;
			return this;
		}

		public DateTimePickWheelDialog create() {
			final DateTimePickWheelDialog dialog = new DateTimePickWheelDialog(
					P.mContext);
			P.apply(dialog);
			return dialog;
		}
	}

	public static class DatePickParams {
		public int mIconId;
		public DialogInterface.OnClickListener mPositiveButtonListener;
		public CharSequence mPositiveButtonText;
		public CharSequence mTitle;
		public final Context mContext;
		public Calendar calendar;
		private CharSequence mNegativeButtonText;
		private DialogInterface.OnClickListener mNegativeButtonListener;

		public DatePickParams(Context context) {
			mContext = context;
			calendar = Calendar.getInstance();
		};

		public DatePickParams(Context context, Calendar calendar) {
			mContext = context;
			this.calendar = calendar;
		}

		public void apply(DateTimePickWheelDialog dialog) {
			if (mTitle != null) {
				dialog.setTitle(mTitle);
			}

			if (mPositiveButtonText != null) {
				dialog.setPositiveButton(mPositiveButtonText,
						mPositiveButtonListener);
			}
			if (mNegativeButtonText != null) {
				dialog.setNegativeButton(mNegativeButtonText,
						mNegativeButtonListener);
			}
			if (calendar != null)
				dialog.setCalendar(calendar);
		}
	}

	public String getFormatTime() {
		String parten = "00";
		DecimalFormat decimal = new DecimalFormat(parten);
		// 设置日期的显示
		Calendar calendar = getCalendar();
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DATE);
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int minute = calendar.get(Calendar.MINUTE);
		return year + "-" + decimal.format(month + 1) + "-"
				+ decimal.format(day) + " " + decimal.format(hour) + ":"
				+ decimal.format(minute);

	}
}
