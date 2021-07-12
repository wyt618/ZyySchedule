package com.example.zyyschedule.calendar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextUtils;

import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.MonthView;

public class CalendarMonthView extends MonthView {

    private int mRadius;
    private final Paint dateTextPaint = new Paint() ; //自定义日历日期文本画笔
    private final Paint SolarTextPaint = new Paint(); //自定义24节气画笔
    private final Paint BackgroundPaint = new Paint();//自定义背景圆点画笔
    private final Paint nowDayPaint = new Paint();//自定义今天背景色画笔

    /**
     * 自定义标记小圆点半径
     */
    private final float mPointRadius; //点的位置

    private final int mPadding; //填充

    private final float mCircleRadius; //圆的半径


    /**
     * 自定义日历显示的圆形背景
     */
    private final Paint mSchemeBasicPaint = new Paint();

    private final float mSchemeBaseLine;
    public CalendarMonthView(Context context) {
        super(context);
        dateTextPaint.setTextSize(dipToPx(context, 8));
        dateTextPaint.setColor(0xffffffff);
        dateTextPaint.setAntiAlias(true);
        dateTextPaint.setFakeBoldText(true);
        //设置日历显示的日期字体画笔颜色

        SolarTextPaint.setColor(0xff00ff00);
        SolarTextPaint.setAntiAlias(true);
        SolarTextPaint.setTextAlign(Paint.Align.CENTER);
        //设置24节气字体画笔

        mSchemeBasicPaint.setAntiAlias(true);
        mSchemeBasicPaint.setStyle(Paint.Style.FILL);
        mSchemeBasicPaint.setTextAlign(Paint.Align.CENTER);
        mSchemeBasicPaint.setFakeBoldText(true);
        mSchemeBasicPaint.setColor(0xe1e1e1);
        //设置日历点击后字体颜色画笔

        nowDayPaint.setAntiAlias(true);
        nowDayPaint.setStyle(Paint.Style.FILL);
        nowDayPaint.setColor(0xffffffff);
        //设置本日背景颜色

        BackgroundPaint.setAntiAlias(true);
        BackgroundPaint.setStyle(Paint.Style.FILL);
        BackgroundPaint.setTextAlign(Paint.Align.CENTER);
        BackgroundPaint.setColor(Color.RED);
        //设置本日字体颜色

        mCircleRadius = dipToPx(getContext(), 6);

        mPadding = dipToPx(getContext(), 3);

        mPointRadius = dipToPx(context, 2);

        Paint.FontMetrics metrics = mSchemeBasicPaint.getFontMetrics();
        mSchemeBaseLine = mCircleRadius - metrics.descent + (metrics.bottom - metrics.top) / 2 + dipToPx(getContext(), 1);
    }

    @Override
    protected boolean onDrawSelected(Canvas canvas, Calendar calendar, int x, int y, boolean hasScheme) {
        int cx = x + mItemWidth / 2;
        int cy = y + mItemHeight / 2;
        canvas.drawCircle(cx, cy, mRadius, mSelectedPaint);
        return true;
    }

    @Override
    protected void onDrawScheme(Canvas canvas, Calendar calendar, int x, int y) {
        boolean isSelected = isSelected(calendar);
        if (isSelected) {
            BackgroundPaint.setColor(Color.WHITE);
        } else {
            BackgroundPaint.setColor(Color.RED);
        }

        canvas.drawCircle(x + mItemWidth / 2, y + mItemHeight - 3 * mPadding, mPointRadius, BackgroundPaint);

    }

    @Override
    protected void onDrawText(Canvas canvas, Calendar calendar, int x, int y, boolean hasScheme, boolean isSelected) {
        int cx = x + mItemWidth / 2;
        int cy = y + mItemHeight / 2;
        int top = y - mItemHeight / 6;

        if (calendar.isCurrentDay() && !isSelected) {
            canvas.drawCircle(cx, cy, mRadius, nowDayPaint);
        }

        if (hasScheme) {
            canvas.drawCircle(x + mItemWidth - mPadding - mCircleRadius / 2, y + mPadding + mCircleRadius, mCircleRadius, mSchemeBasicPaint);
            dateTextPaint.setColor(calendar.getSchemeColor());
            dateTextPaint.setTextSize(15);
            canvas.drawText(calendar.getScheme(), x + mItemWidth - mPadding*19 - mCircleRadius, y + mPadding + mSchemeBaseLine,  dateTextPaint);
        }

        //当然可以换成其它对应的画笔就不麻烦，
        if (calendar.isWeekend() && calendar.isCurrentMonth()) {
            mCurMonthTextPaint.setColor(0xFF489dff);
            mCurMonthLunarTextPaint.setColor(0xFF489dff);
            mSchemeTextPaint.setColor(0xFF489dff);
            mSchemeLunarTextPaint.setColor(0xFF489dff);
            mOtherMonthLunarTextPaint.setColor(0xFF489dff);
            mOtherMonthTextPaint.setColor(0xFF489dff);
        } else {
            mCurMonthTextPaint.setColor(0xff333333);
            mCurMonthLunarTextPaint.setColor(0xffffffff);
            mSchemeTextPaint.setColor(0xff333333);
            mSchemeLunarTextPaint.setColor(0xffCFCFCF);

            mOtherMonthTextPaint.setColor(0xFFe1e1e1);
            mOtherMonthLunarTextPaint.setColor(0xFFe1e1e1);
        }

        if (isSelected) {
            canvas.drawText(String.valueOf(calendar.getDay()), cx, mTextBaseLine + top,
                    mSelectTextPaint);
            canvas.drawText(calendar.getLunar(), cx, mTextBaseLine + y + mItemHeight / 10, mSelectedLunarTextPaint);
        } else if (hasScheme) {

            canvas.drawText(String.valueOf(calendar.getDay()), cx, mTextBaseLine + top,
                    calendar.isCurrentMonth() ? mSchemeTextPaint : mOtherMonthTextPaint);

            canvas.drawText(calendar.getLunar(), cx, mTextBaseLine + y + mItemHeight / 10,
                    !TextUtils.isEmpty(calendar.getSolarTerm()) ? SolarTextPaint : mSchemeLunarTextPaint);
        } else {
            canvas.drawText(String.valueOf(calendar.getDay()), cx, mTextBaseLine + top,
                    calendar.isCurrentDay() ? mCurDayTextPaint :
                            calendar.isCurrentMonth() ? mCurMonthTextPaint : mOtherMonthTextPaint);

            canvas.drawText(calendar.getLunar(), cx, mTextBaseLine + y + mItemHeight / 10,
                    calendar.isCurrentDay() ? mCurDayLunarTextPaint :
                            calendar.isCurrentMonth() ? !TextUtils.isEmpty(calendar.getSolarTerm()) ? SolarTextPaint  :
                                    mCurMonthLunarTextPaint : mOtherMonthLunarTextPaint);
        }

    }




    //dp转px
    private static int dipToPx (Context context ,float dpValue){
        final float scale = context.getResources().getDisplayMetrics().density;
        return(int) (dpValue * scale +0.5f);
    }

    @Override
    protected void onPreviewHook() {
        SolarTextPaint.setTextSize(mCurMonthLunarTextPaint.getTextSize());
        mRadius = Math.min(mItemWidth, mItemHeight) / 11 * 5;
    }




}
