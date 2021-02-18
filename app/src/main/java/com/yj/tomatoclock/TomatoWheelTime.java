package com.yj.tomatoclock;

import android.view.View;

import com.bigkoo.pickerview.configure.PickerOptions;
import com.bigkoo.pickerview.listener.ISelectTimeCallback;
import com.bigkoo.pickerview.view.WheelTime;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TomatoWheelTime {
    private WheelTime wheelTime;
    private PickerOptions mPickerOptions;
    private View timePickerView;
    private int type;
    private static final DateFormat YYYYMMddHHmm = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.CHINA);
    private static final DateFormat YYYYMMdd = new SimpleDateFormat("yyyy/MM/dd", Locale.CHINA);
    private static final DateFormat HHmm = new SimpleDateFormat("HH:mm", Locale.CHINA);


    // 时间选择器展示的类型
    public interface Type {
        int YEAR_MONTH_DAY_HOUR_MIN = 0; // 年月日时分
        int YEAR_MONTH_DAY = 1; // 年月日
        int HOUR_MIN = 2; // 时分
    }

    public TomatoWheelTime(View timePickerView, int type) {
        this.type = type;
        this.timePickerView = timePickerView;
        mPickerOptions = new PickerOptions(PickerOptions.TYPE_PICKER_TIME);
        switch (type) {
            case Type.YEAR_MONTH_DAY_HOUR_MIN:
                mPickerOptions.type = new boolean[]{true, true, true, true, true, false};
                break;
            case Type.YEAR_MONTH_DAY:
                mPickerOptions.type = new boolean[]{true, true, true, false, false, false};
                break;
            case Type.HOUR_MIN:
                mPickerOptions.type = new boolean[]{false, false, false, true, true, false};
                break;
        }
        mPickerOptions.isCenterLabel = true;
    }

    public void initWheelTime() {
        wheelTime = new WheelTime(timePickerView, mPickerOptions.type, mPickerOptions.textGravity, mPickerOptions.textSizeContent);
        if (mPickerOptions.timeSelectChangeListener != null) {
            wheelTime.setSelectChangeCallback(new ISelectTimeCallback() {
                @Override
                public void onTimeSelectChanged() {
                    try {
                        Date date = WheelTime.dateFormat.parse(wheelTime.getTime());
                        mPickerOptions.timeSelectChangeListener.onTimeSelectChanged(date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        wheelTime.setLunarMode(mPickerOptions.isLunarCalendar);

        if (mPickerOptions.startYear != 0 && mPickerOptions.endYear != 0
                && mPickerOptions.startYear <= mPickerOptions.endYear) {
            setRange();
        }

        //若手动设置了时间范围限制
        if (mPickerOptions.startDate != null && mPickerOptions.endDate != null) {
            if (mPickerOptions.startDate.getTimeInMillis() > mPickerOptions.endDate.getTimeInMillis()) {
                throw new IllegalArgumentException("startDate can't be later than endDate");
            } else {
                setRangDate();
            }
        } else if (mPickerOptions.startDate != null) {
            if (mPickerOptions.startDate.get(Calendar.YEAR) < 1900) {
                throw new IllegalArgumentException("The startDate can not as early as 1900");
            } else {
                setRangDate();
            }
        } else if (mPickerOptions.endDate != null) {
            if (mPickerOptions.endDate.get(Calendar.YEAR) > 2100) {
                throw new IllegalArgumentException("The endDate should not be later than 2100");
            } else {
                setRangDate();
            }
        } else {//没有设置时间范围限制，则会使用默认范围。
            setRangDate();
        }

        setTime("00:00");
        wheelTime.setLabels(mPickerOptions.label_year, mPickerOptions.label_month, mPickerOptions.label_day
                , mPickerOptions.label_hours, mPickerOptions.label_minutes, mPickerOptions.label_seconds);
        wheelTime.setTextXOffset(mPickerOptions.x_offset_year, mPickerOptions.x_offset_month, mPickerOptions.x_offset_day,
                mPickerOptions.x_offset_hours, mPickerOptions.x_offset_minutes, mPickerOptions.x_offset_seconds);
//        setOutSideCancelable(mPickerOptions.cancelable);
        wheelTime.setCyclic(mPickerOptions.cyclic);
        wheelTime.setDividerColor(mPickerOptions.dividerColor);
        wheelTime.setDividerType(mPickerOptions.dividerType);
        wheelTime.setLineSpacingMultiplier(mPickerOptions.lineSpacingMultiplier);
        wheelTime.setTextColorOut(mPickerOptions.textColorOut);
        wheelTime.setTextColorCenter(mPickerOptions.textColorCenter);
        wheelTime.isCenterLabel(mPickerOptions.isCenterLabel);
    }

    /**
     * 设置可以选择的时间范围, 要在setTime之前调用才有效果
     */
    private void setRange() {
        wheelTime.setStartYear(mPickerOptions.startYear);
        wheelTime.setEndYear(mPickerOptions.endYear);

    }

    /**
     * 设置可以选择的时间范围, 要在setTime之前调用才有效果
     */
    private void setRangDate() {
        wheelTime.setRangDate(mPickerOptions.startDate, mPickerOptions.endDate);
        initDefaultSelectedDate();
    }

    private void initDefaultSelectedDate() {
        //如果手动设置了时间范围
        if (mPickerOptions.startDate != null && mPickerOptions.endDate != null) {
            //若默认时间未设置，或者设置的默认时间越界了，则设置默认选中时间为开始时间。
            if (mPickerOptions.date == null || mPickerOptions.date.getTimeInMillis() < mPickerOptions.startDate.getTimeInMillis()
                    || mPickerOptions.date.getTimeInMillis() > mPickerOptions.endDate.getTimeInMillis()) {
                mPickerOptions.date = mPickerOptions.startDate;
            }
        } else if (mPickerOptions.startDate != null) {
            //没有设置默认选中时间,那就拿开始时间当默认时间
            mPickerOptions.date = mPickerOptions.startDate;
        } else if (mPickerOptions.endDate != null) {
            mPickerOptions.date = mPickerOptions.endDate;
        }
    }

    /**
     * 设置选中时间,默认选中当前时间
     */
    public void setTime(String time) {
//        int year, month, day, hours, minute, seconds;
//        Calendar calendar = Calendar.getInstance();
//
//        if (mPickerOptions.date == null) {
//            calendar.setTimeInMillis(System.currentTimeMillis());
//            year = calendar.get(Calendar.YEAR);
//            month = calendar.get(Calendar.MONTH);
//            day = calendar.get(Calendar.DAY_OF_MONTH);
//            hours = calendar.get(Calendar.HOUR_OF_DAY);
//            minute = calendar.get(Calendar.MINUTE);
//            seconds = calendar.get(Calendar.SECOND);
//        } else {
//            year = mPickerOptions.date.get(Calendar.YEAR);
//            month = mPickerOptions.date.get(Calendar.MONTH);
//            day = mPickerOptions.date.get(Calendar.DAY_OF_MONTH);
//            hours = mPickerOptions.date.get(Calendar.HOUR_OF_DAY);
//            minute = mPickerOptions.date.get(Calendar.MINUTE);
//            seconds = mPickerOptions.date.get(Calendar.SECOND);
//        }
        String[] times = time.split(":");
        wheelTime.setPicker(0, 0, 0, Integer.parseInt(times[0]), Integer.parseInt(times[1]), 0);
    }

    public String getTimeData() {
        String time = "";
        try {
            Date date = WheelTime.dateFormat.parse(wheelTime.getTime());
            switch (type) {
                case Type.YEAR_MONTH_DAY_HOUR_MIN:
                    time = YYYYMMddHHmm.format(date);
                    break;
                case Type.YEAR_MONTH_DAY:
                    time = YYYYMMdd.format(date);
                    break;
                case Type.HOUR_MIN:
                    time = HHmm.format(date);
                    break;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return time;
    }
}
