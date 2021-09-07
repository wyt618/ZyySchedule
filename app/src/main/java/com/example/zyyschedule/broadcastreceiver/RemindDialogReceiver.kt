package com.example.zyyschedule.broadcastreceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.graphics.Rect
import android.os.Build
import android.view.*
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.example.zyyschedule.R
import com.example.zyyschedule.database.Schedule
import com.example.zyyschedule.databinding.RemindGlobalDialogBinding
import com.google.gson.Gson
import java.util.*

@Suppress("DEPRECATED_IDENTITY_EQUALS", "DEPRECATION")
class RemindDialogReceiver : BroadcastReceiver() {
    private lateinit var binding: RemindGlobalDialogBinding
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onReceive(context: Context?, intent: Intent?) {
        val gson = Gson()
        val strSchedule = intent!!.getStringExtra("remindSchedule")
        val schedule: Schedule = gson.fromJson(strSchedule, Schedule::class.java)
        val systemService: WindowManager = context?.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val params = WindowManager.LayoutParams()
        binding = RemindGlobalDialogBinding.inflate(LayoutInflater.from(context))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            params.type = WindowManager.LayoutParams.TYPE_PHONE
        }
        //这个地方一定要这样设置，不然要么是布局之外不能接受事件，要么是布局里面和返回按键接收不到事件。
        params.flags = WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM
        params.gravity = Gravity.BOTTOM or Gravity.CENTER
        // 设置窗口宽度和高度
        params.width = WindowManager.LayoutParams.MATCH_PARENT
        params.height = WindowManager.LayoutParams.MATCH_PARENT
        params.format = PixelFormat.RGBA_8888 //设置WindowManager背景透明
        binding.root.setOnTouchListener { _, event ->
            val x = event.x.toInt()
            val y = event.y.toInt()
            val rect = Rect()
            binding.remindDialog.getGlobalVisibleRect(rect)
            if (!rect.contains(x, y)) {
                systemService.removeView(binding.root)
            }
            binding.root.performClick()
            true
        } //设置点击移除提醒弹窗

        binding.root.setOnKeyListener { _, keyCode, _ ->
            if (keyCode === KeyEvent.KEYCODE_BACK) {
                systemService.removeView(binding.root)
                true
            }else{
                false
            }
        }
        val flagDrawable = ContextCompat.getDrawable(context, R.drawable.priority_flag)
        if(flagDrawable != null){
            when(schedule.priority){
                0 -> flagDrawable.setTint(ContextCompat.getColor(context, R.color.priority_null))
                1 -> flagDrawable.setTint(ContextCompat.getColor(context, R.color.priority_low))
                2 -> flagDrawable.setTint(ContextCompat.getColor(context, R.color.priority_middle))
                3 -> flagDrawable.setTint(ContextCompat.getColor(context, R.color.priority_high))
            }
        }
        binding.priorityIcon.setImageDrawable(flagDrawable)
        if (schedule.priority != 0) {
            binding.priorityIcon.visibility = View.VISIBLE
        }else{
            binding.priorityIcon.visibility = View.GONE
        }
        binding.date.text = schedule.startTime?.let { checkNowDay(it) }
        binding.labelText.text = intent.getStringExtra("LabelTitle")
        systemService.addView(binding.root, params)
    }

    private fun checkNowDay(scheduleDate: String): String {
        val dateText = StringBuilder()
        val nowDay = Date()
        val calendar = Calendar.getInstance() //日历对象
        calendar.time = nowDay
        if (calendar[Calendar.YEAR] != scheduleDate.substring(0, 4).toInt()) {
            dateText.append(scheduleDate.substring(0, 4)).append("年")
        }
        if (calendar[Calendar.MONTH] + 1 == scheduleDate.substring(5, 7).toInt()
                && calendar[Calendar.DAY_OF_MONTH] == scheduleDate.substring(8, 10).toInt()) {
            dateText.append("今天，")
        } else {
            dateText.append(scheduleDate.substring(5, 7)).append("月").append(scheduleDate.substring(8, 10)).append("日,")
        }
        dateText.append(scheduleDate.substring(scheduleDate.length - 8, scheduleDate.length - 3))
        return dateText.toString()
    }

}


