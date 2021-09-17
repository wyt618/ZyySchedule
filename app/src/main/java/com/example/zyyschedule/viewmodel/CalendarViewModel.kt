package com.example.zyyschedule.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.zyyschedule.PriorityBean
import com.example.zyyschedule.R
import com.example.zyyschedule.RemindBean
import com.example.zyyschedule.database.DataRepository
import com.example.zyyschedule.database.Label
import com.example.zyyschedule.database.Schedule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class CalendarViewModel(application: Application) : AndroidViewModel(application) {
    var day = 0
    var addScheduleDateAgo: MutableLiveData<String>
    var addScheduleTime: MutableLiveData<String>
    private lateinit var dateAgo: String
    var priorityid: MutableLiveData<Int>
    var priority: MutableLiveData<String>
    var label: MutableLiveData<String>
    var remindText: MutableLiveData<String>
    private var dataRepository: DataRepository = DataRepository(application)
    private lateinit var priorityBean: PriorityBean
    private lateinit var remindBean: RemindBean
    private lateinit var remindTime: String


    init {
        val calendar = Calendar.getInstance()
        day = calendar[Calendar.DAY_OF_MONTH]
        addScheduleDateAgo = MutableLiveData()
        addScheduleTime = MutableLiveData()
        priority = MutableLiveData()
        priorityid = MutableLiveData()
        label = MutableLiveData()
        remindText = MutableLiveData()
        addScheduleTime.value = "00:00"
        priority.value = application.getString(R.string.priority_null_text)
        label.value = "无标签"
        priorityid.value = 0
        remindText.value = "无提醒"
    }


    fun addScheduleDateAgo(
        selYear: Int,
        selMonth: Int,
        selDay: Int,
        toYear: Int,
        toMonth: Int,
        today: Int
    ) {
        dateAgo = if (selYear > toYear) {
            if (selYear - 1 == toYear) {
                "明年" + selMonth + "月" + selDay + "号"
            } else {
                (selYear - toYear).toString() + "年后的" + selMonth + "月" + selDay + "号"
            }
        } else if (selYear < toYear) {
            if (selYear + 1 == toYear) {
                "去年" + selMonth + "月" + selDay + "号"
            } else {
                (toYear - selYear).toString() + "年前的" + selMonth + "月" + selDay + "号"
            }
        } else {
            if (selMonth > toMonth) {
                if (selMonth - 1 == toMonth) {
                    "下个月" + selDay + "号"
                } else {
                    (selMonth - toMonth).toString() + "个月后的" + selDay + "号"
                }
            } else if (selMonth < toMonth) {
                if (selMonth + 1 == toYear) {
                    "上个月" + selDay + "号"
                } else {
                    (toMonth - selMonth).toString() + "个月前的" + selDay + "号"
                }
            } else {
                when {
                    selDay > today -> {
                        (selDay - today).toString() + "天后"
                    }
                    selDay < today -> {
                        (today - selDay).toString() + "天前"
                    }
                    else -> {
                        "今天"
                    }
                }
            }
        }
        addScheduleDateAgo.value = dateAgo
    }

    fun priorityListData(context: Context): ArrayList<PriorityBean> {
        val ary = ArrayList<PriorityBean>()
        priorityBean = PriorityBean()
        priorityBean.priorityTitle = context.getString(R.string.priority_null_text)
        priorityBean.priorityType = 0
        ary.add(priorityBean)
        priorityBean = PriorityBean()
        priorityBean.priorityTitle = context.getString(R.string.priority_low_text)
        priorityBean.priorityType = 1
        ary.add(priorityBean)
        priorityBean = PriorityBean()
        priorityBean.priorityTitle = context.getString(R.string.priority_medium_text)
        priorityBean.priorityType = 2
        ary.add(priorityBean)
        priorityBean = PriorityBean()
        priorityBean.priorityTitle = context.getString(R.string.priority_high_text)
        priorityBean.priorityType = 3
        ary.add(priorityBean)
        return ary
    }

    fun remindListData(context: Context): ArrayList<RemindBean> {
        val ary = ArrayList<RemindBean>()
        remindBean = RemindBean()
        remindBean.remindTitle = context.getString(R.string.remind_on_time_text)
        remindBean.remindType = 1
        remindBean.remindIsChecked = false
        ary.add(remindBean)
        remindBean = RemindBean()
        remindBean.remindTitle = context.getString(R.string.remind_1_minute_ahead_text)
        remindBean.remindType = 2
        remindBean.remindIsChecked = false
        ary.add(remindBean)
        remindBean = RemindBean()
        remindBean.remindTitle = context.getString(R.string.remind_5_minute_early_text)
        remindBean.remindType = 3
        remindBean.remindIsChecked = false
        ary.add(remindBean)
        remindBean = RemindBean()
        remindBean.remindTitle = context.getString(R.string.remind_10_minute_early_text)
        remindBean.remindType = 4
        remindBean.remindIsChecked = false
        ary.add(remindBean)
        remindBean = RemindBean()
        remindBean.remindTitle = context.getString(R.string.remind_15_minute_early_text)
        remindBean.remindType = 5
        remindBean.remindIsChecked = false
        ary.add(remindBean)
        remindBean = RemindBean()
        remindBean.remindTitle = context.getString(R.string.remind_20_minute_early_text)
        remindBean.remindType = 6
        remindBean.remindIsChecked = false
        ary.add(remindBean)
        remindBean = RemindBean()
        remindBean.remindTitle = context.getString(R.string.remind_25_minute_early_text)
        remindBean.remindType = 7
        remindBean.remindIsChecked = false
        ary.add(remindBean)
        remindBean = RemindBean()
        remindBean.remindTitle = context.getString(R.string.remind_30_minute_early_text)
        remindBean.remindType = 8
        remindBean.remindIsChecked = false
        ary.add(remindBean)
        remindBean = RemindBean()
        remindBean.remindTitle = context.getString(R.string.remind_45_minute_early_text)
        remindBean.remindType = 9
        remindBean.remindIsChecked = false
        ary.add(remindBean)
        remindBean = RemindBean()
        remindBean.remindTitle = context.getString(R.string.remind_one_hour_earlier_text)
        remindBean.remindType = 10
        remindBean.remindIsChecked = false
        ary.add(remindBean)
        remindBean = RemindBean()
        remindBean.remindTitle = context.getString(R.string.remind_two_hours_advance_text)
        remindBean.remindType = 11
        remindBean.remindIsChecked = false
        ary.add(remindBean)
        remindBean = RemindBean()
        remindBean.remindTitle = context.getString(R.string.remind_three_hours_advance_text)
        remindBean.remindType = 12
        remindBean.remindIsChecked = false
        ary.add(remindBean)
        remindBean = RemindBean()
        remindBean.remindTitle = context.getString(R.string.remind_12_hours_advance_text)
        remindBean.remindType = 13
        remindBean.remindIsChecked = false
        ary.add(remindBean)
        remindBean = RemindBean()
        remindBean.remindTitle = context.getString(R.string.remind_1_day_advance_text)
        remindBean.remindType = 14
        remindBean.remindIsChecked = false
        ary.add(remindBean)
        remindBean = RemindBean()
        remindBean.remindTitle = context.getString(R.string.remind_2_day_advance_text)
        remindBean.remindType = 15
        remindBean.remindIsChecked = false
        ary.add(remindBean)
        remindBean = RemindBean()
        remindBean.remindTitle = context.getString(R.string.remind_1_weeks_advance_text)
        remindBean.remindType = 15
        remindBean.remindIsChecked = false
        ary.add(remindBean)
        return ary
    }


    fun insertSchedule(vararg schedules: Schedule) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                dataRepository.insertSchedule(*schedules)
            } catch (e: Exception) {
                Log.i("calendar", "插入日程失败：$e")
            }
        }
    }

    fun deleteSchedule(vararg schedules: Schedule) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                dataRepository.deleteSchedule(*schedules)
            } catch (e: Exception) {
                Log.i("calendar", "删除日程失败：$e")
            }
        }
    }

    fun getAllLabel(): LiveData<List<Label>> {
        return dataRepository.getAllLabel()
    }

    fun getUnfinishedScheduleOfDay(day: String): LiveData<List<Schedule>> {
        return dataRepository.getUnfinishedScheduleOfDay(day)
    }

    fun getFinishedScheduleOfDay(day: String): LiveData<List<Schedule>> {
        return dataRepository.getFinishedScheduleOfDay(day)
    }

    fun getScheduleDayOfTag(): LiveData<List<String>> {
        return dataRepository.getScheduleDayOfTag()
    }

    fun changeStateSchedule(vararg schedules: Schedule) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                dataRepository.changeStateSchedule(*schedules)
            } catch (e: Exception) {
                Log.i("calendar", "修改日程状态失败：$e")
            }
        }
    }

    fun getDateForRemindToTime(selectYear: Int, selectMonth: Int, selectDay: Int, time: String) {
        remindTime = "$selectYear-$selectMonth-$selectDay $time:00"
    }

    //处理提醒与时间的方法
    fun remindToTime(remindType: Int): String {
        var date = Date()
        @SuppressLint("SimpleDateFormat") val std = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        try {
            date = std.parse(remindTime)
        } catch (ignored: Exception) {
        }
        when (remindType) {
            1 -> {
                remindTime = std.format(date)
            }
            2 -> {
                date.time = date.time - 60 * 1000
                remindTime = std.format(date)
            }
            3 -> {
                date.time = date.time - 5 * 60 * 1000
                remindTime = std.format(date)
            }
            4 -> {
                date.time = date.time - 10 * 60 * 1000
                remindTime = std.format(date)
            }
            5 -> {
                date.time = date.time - 15 * 60 * 1000
                remindTime = std.format(date)
            }
            6 -> {
                date.time = date.time - 20 * 60 * 1000
                remindTime = std.format(date)
            }
            7 -> {
                date.time = date.time - 25 * 60 * 1000
                remindTime = std.format(date)
            }
            8 -> {
                date.time = date.time - 30 * 60 * 1000
                remindTime = std.format(date)
            }
            9 -> {
                date.time = date.time - 45 * 60 * 1000
                remindTime = std.format(date)
            }
            10 -> {
                date.time = date.time - 60 * 60 * 1000
                remindTime = std.format(date)
            }
            11 -> {
                date.time = date.time - 2 * 60 * 60 * 1000
                remindTime = std.format(date)
            }
            12 -> {
                date.time = date.time - 3 * 60 * 60 * 1000
                remindTime = std.format(date)
            }
            13 -> {
                date.time = date.time - 12 * 60 * 60 * 1000
                remindTime = std.format(date)
            }
            14 -> {
                date.time = date.time - 24 * 60 * 60 * 1000
                remindTime = std.format(date)
            }
            15 -> {
                date.time = date.time - 2 * 24 * 60 * 60 * 1000
                remindTime = std.format(date)
            }
            16 -> {
                date.time = date.time - 7 * 24 * 60 * 60 * 1000
                remindTime = std.format(date)
            }
        }
        return remindTime
    }

    fun getLabelTitle(id: Int): LiveData<String> {
        return dataRepository.getLabelTitle(id)
    }
}