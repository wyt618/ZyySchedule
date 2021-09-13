package com.example.zyyschedule.calendar

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import com.haibin.calendarview.Calendar
import com.haibin.calendarview.R
import com.haibin.calendarview.YearView
import kotlin.math.min

class CalendarYearView(context: Context) : YearView(context) {
    private var mTextPadding = 0

    /**
     * 闰年字体
     */
    private val mLeapYearTextPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
        mTextPadding = dipToPx(context, 3f)

        mLeapYearTextPaint.textSize = dipToPx(context, 12f).toFloat()
        mLeapYearTextPaint.color = -0x2e2e2f
        mLeapYearTextPaint.isAntiAlias = true
        mLeapYearTextPaint.isFakeBoldText = true
    }

    override fun onDrawMonth(
        canvas: Canvas,
        year: Int,
        month: Int,
        x: Int,
        y: Int,
        width: Int,
        height: Int
    ) {
        val text = context
            .resources
            .getStringArray(R.array.month_string_array)[month - 1]
        canvas.drawText(
            text, (
                    x + mItemWidth / 2 - mTextPadding).toFloat(),
            y + mMonthTextBaseLine,
            mMonthTextPaint
        )
        if (month == 2 && isLeapYear(year)) {
            val w = getTextWidth(mMonthTextPaint, text)
            canvas.drawText(
                "闰年",
                x + mItemWidth / 2 - mTextPadding + w + dipToPx(context, 6f),
                y + mMonthTextBaseLine,
                mLeapYearTextPaint
            )
        }
    }

    override fun onDrawWeek(canvas: Canvas, week: Int, x: Int, y: Int, width: Int, height: Int) {
        val text = context.resources.getStringArray(R.array.year_view_week_string_array)[week]
        canvas.drawText(
            text, (
                    x + width / 2).toFloat(),
            y + mWeekTextBaseLine,
            mWeekTextPaint
        )
    }

    override fun onDrawSelected(
        canvas: Canvas,
        calendar: Calendar,
        x: Int,
        y: Int,
        hasScheme: Boolean
    ): Boolean {
        val cx = x + mItemWidth / 2
        val cy = y + mItemHeight / 2
        val radius = min(mItemWidth, mItemHeight) / 8 * 5
        canvas.drawCircle(cx.toFloat(), cy.toFloat(), radius.toFloat(), mSelectedPaint)
        return true
    }

    override fun onDrawScheme(canvas: Canvas, calendar: Calendar, x: Int, y: Int) {

    }

    override fun onDrawText(
        canvas: Canvas,
        calendar: Calendar,
        x: Int,
        y: Int,
        hasScheme: Boolean,
        isSelected: Boolean
    ) {
        val baselineY = mTextBaseLine + y
        val cx = x + mItemWidth / 2

        when {
            isSelected -> {
                canvas.drawText(
                    calendar.day.toString(),
                    cx.toFloat(),
                    baselineY,
                    if (hasScheme) mSchemeTextPaint else mSelectTextPaint
                )
            }
            hasScheme -> {
                canvas.drawText(
                    calendar.day.toString(),
                    cx.toFloat(),
                    baselineY,
                    if (calendar.isCurrentDay) mCurDayTextPaint else mSchemeTextPaint
                )
            }
            else -> {
                canvas.drawText(
                    calendar.day.toString(), cx.toFloat(), baselineY,
                    if (calendar.isCurrentDay) mCurDayTextPaint else mCurMonthTextPaint
                )
            }
        }
    }

    /**
     * dp转px
     *
     * @param context context
     * @param dpValue dp
     * @return px
     */
    private fun dipToPx(context: Context, dpValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    private fun getTextWidth(paint: Paint, text: String): Float {
        return paint.measureText(text)
    }

    /**
     * 是否是闰年
     *
     * @param year year
     * @return 是否是闰年
     */
    private fun isLeapYear(year: Int): Boolean {
        return year % 4 == 0 && year % 100 != 0 || year % 400 == 0
    }
}