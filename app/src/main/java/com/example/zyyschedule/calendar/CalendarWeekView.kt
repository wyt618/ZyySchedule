package com.example.zyyschedule.calendar

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.text.TextUtils
import com.haibin.calendarview.Calendar
import com.haibin.calendarview.WeekView
import kotlin.math.min

class CalendarWeekView(context: Context) : WeekView(context) {
    private var mRadius = 0
    private val dateTextPaint = Paint() //自定义日历日期文本画笔
    private val solarTextPaint = Paint() //自定义24节气画笔
    private val backgroundPaint = Paint() //自定义背景圆点画笔
    private val nowDayPaint = Paint() //自定义今天背景色画笔

    /**
     * 自定义标记小圆点半径
     */
    private var mPointRadius = 0f //点的位置
    private var mPadding = 0 //填充
    private var mCircleRadius = 0f //圆的半径

    /**
     * 自定义日历显示的圆形背景
     */
    private val mSchemeBasicPaint = Paint()
    private var mSchemeBaseLine = 0f

    init {
        dateTextPaint.textSize = dipToPx(context, 8f).toFloat()
        dateTextPaint.color = -0x1
        dateTextPaint.isAntiAlias = true
        dateTextPaint.isFakeBoldText = true
        //设置日历显示的日期字体画笔颜色

        //设置日历显示的日期字体画笔颜色
        solarTextPaint.color = -0xff0100
        solarTextPaint.isAntiAlias = true
        solarTextPaint.textAlign = Paint.Align.CENTER
        //设置24节气字体画笔

        //设置24节气字体画笔
        mSchemeBasicPaint.isAntiAlias = true
        mSchemeBasicPaint.style = Paint.Style.FILL
        mSchemeBasicPaint.textAlign = Paint.Align.CENTER
        mSchemeBasicPaint.isFakeBoldText = true
        mSchemeBasicPaint.color = 0xe1e1e1
        //设置日历点击后字体颜色画笔

        //设置日历点击后字体颜色画笔
        nowDayPaint.isAntiAlias = true
        nowDayPaint.style = Paint.Style.FILL
        nowDayPaint.color = -0x1
        //设置本日背景颜色

        //设置本日背景颜色
        backgroundPaint.isAntiAlias = true
        backgroundPaint.style = Paint.Style.FILL
        backgroundPaint.textAlign = Paint.Align.CENTER
        backgroundPaint.color = Color.RED

        //设置本日字体颜色
        mCircleRadius = dipToPx(context, 6f).toFloat()

        mPadding = dipToPx(context, 3f)

        mPointRadius = dipToPx(context, 2f).toFloat()

        val metrics = mSchemeBasicPaint.fontMetrics
        mSchemeBaseLine = mCircleRadius - metrics.descent + (metrics.bottom - metrics.top) / 2 + dipToPx(context, 1f)
    }

    override fun onDrawSelected(canvas: Canvas, calendar: Calendar, x: Int, hasScheme: Boolean): Boolean {
        val cx = x + mItemWidth / 2
        val cy = mItemHeight / 2
        canvas.drawCircle(cx.toFloat(), cy.toFloat(), mRadius.toFloat(), mSelectedPaint)
        return true
    }

    override fun onDrawScheme(canvas: Canvas, calendar: Calendar, x: Int) {
        val isSelected = isSelected(calendar)
        if (isSelected) {
            backgroundPaint.color = Color.WHITE
        } else {
            backgroundPaint.color = Color.RED
        }

        canvas.drawCircle((x + mItemWidth / 2).toFloat(), (mItemHeight - 3 * mPadding).toFloat(), mPointRadius, backgroundPaint)
    }

    override fun onDrawText(canvas: Canvas, calendar: Calendar, x: Int, hasScheme: Boolean, isSelected: Boolean) {
        val cx = x + mItemWidth / 2
        val cy = mItemHeight / 2
        val top = -mItemHeight / 6

        if (calendar.isCurrentDay && !isSelected) {
            canvas.drawCircle(cx.toFloat(), cy.toFloat(), mRadius.toFloat(), nowDayPaint)
        }

        if (hasScheme) {
            canvas.drawCircle(x + mItemWidth - mPadding - mCircleRadius / 2, mPadding + mCircleRadius, mCircleRadius, mSchemeBasicPaint)
            dateTextPaint.color = calendar.schemeColor
            dateTextPaint.textSize = 15f
            canvas.drawText(calendar.scheme, x + mItemWidth - mPadding * 19 - mCircleRadius, mPadding + mSchemeBaseLine, dateTextPaint)
        }

        if (calendar.isWeekend && calendar.isCurrentMonth) {
            mCurMonthTextPaint.color = -0xb76201
            mCurMonthLunarTextPaint.color = -0xb76201
            mSchemeTextPaint.color = -0xb76201
            mSchemeLunarTextPaint.color = -0xb76201
            mOtherMonthLunarTextPaint.color = -0xb76201
            mOtherMonthTextPaint.color = -0xb76201 //周末周视图日期颜色显示
        } else {
            mCurMonthTextPaint.color = -0xcccccd
            mCurMonthLunarTextPaint.color = -0xebebec
            mSchemeTextPaint.color = -0xcccccd
            mSchemeLunarTextPaint.color = -0x303031
            mOtherMonthTextPaint.color = -0x1e1e1f
            mOtherMonthLunarTextPaint.color = -0x1e1e1f //工作日周视图自定义显示
        }

        when {
            isSelected -> {
                canvas.drawText(calendar.day.toString(), cx.toFloat(), mTextBaseLine + top,
                        mSelectTextPaint)
                canvas.drawText(calendar.lunar, cx.toFloat(), mTextBaseLine + mItemHeight / 10, mSelectedLunarTextPaint)
            }
            hasScheme -> {
                canvas.drawText(calendar.day.toString(), cx.toFloat(), mTextBaseLine + top,
                        if (calendar.isCurrentMonth) mSchemeTextPaint else mOtherMonthTextPaint)
                canvas.drawText(calendar.lunar, cx.toFloat(), mTextBaseLine + mItemHeight / 10,
                        if (!TextUtils.isEmpty(calendar.solarTerm)) solarTextPaint else mSchemeLunarTextPaint)
            }
            else -> {
                canvas.drawText(calendar.day.toString(), cx.toFloat(), mTextBaseLine + top,
                        if (calendar.isCurrentDay) mCurDayTextPaint else if (calendar.isCurrentMonth) mCurMonthTextPaint else mOtherMonthTextPaint)
                canvas.drawText(calendar.lunar, cx.toFloat(), mTextBaseLine + mItemHeight / 10,
                        if (calendar.isCurrentDay) mCurDayLunarTextPaint else if (!TextUtils.isEmpty(calendar.solarTerm)) solarTextPaint else if (calendar.isCurrentMonth) mCurMonthLunarTextPaint else mOtherMonthLunarTextPaint)
            }
        }
    }

    //dp转px
    private fun dipToPx(context: Context, dpValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    override fun onPreviewHook() {
        solarTextPaint.textSize = mCurMonthLunarTextPaint.textSize
        mRadius = min(mItemWidth, mItemHeight) / 11 * 5
    }
}