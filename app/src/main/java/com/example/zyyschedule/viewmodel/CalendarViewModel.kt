package com.example.zyyschedule.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.zyyschedule.PriorityBean
import com.example.zyyschedule.RemindBean
import com.example.zyyschedule.database.DataRepository
import com.example.zyyschedule.database.Label
import com.example.zyyschedule.database.Schedule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

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

    init {
        val calendar = Calendar.getInstance()
        day = calendar[Calendar.DAY_OF_MONTH]
        addScheduleDateAgo = MutableLiveData()
        addScheduleTime = MutableLiveData()
        priority = MutableLiveData()
        priorityid = MutableLiveData()
        label = MutableLiveData()
        remindText = MutableLiveData<String>()
        addScheduleTime.value = "00:00"
        priority.value = "无优先级"
        label.value = "无标签"
        priorityid.value = 0
        remindText.value = "无提醒"
    }

    fun addScheduleDateAgo(selYear: Int, selMonth: Int, selDay: Int, toYear: Int, toMonth: Int, today: Int) {
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

    fun priorityListData(): ArrayList<PriorityBean> {
        val ary = ArrayList<PriorityBean>()
        priorityBean = PriorityBean()
        priorityBean.priorityTitle = "无优先级"
        priorityBean.priorityType = 0
        ary.add(priorityBean)
        priorityBean = PriorityBean()
        priorityBean.priorityTitle = "低优先级"
        priorityBean.priorityType = 1
        ary.add(priorityBean)
        priorityBean = PriorityBean()
        priorityBean.priorityTitle = "中优先级"
        priorityBean.priorityType = 2
        ary.add(priorityBean)
        priorityBean = PriorityBean()
        priorityBean.priorityTitle = "高优先级"
        priorityBean.priorityType = 3
        ary.add(priorityBean)
        return ary
    }

    fun remindListData(): ArrayList<RemindBean> {
        val ary = ArrayList<RemindBean>()
        remindBean = RemindBean()
        remindBean.remindTitle = "准时"
        remindBean.remindType = 1
        remindBean.remindIsChecked = false
        ary.add(remindBean)
        remindBean = RemindBean()
        remindBean.remindTitle = "提前1分钟"
        remindBean.remindType = 2
        remindBean.remindIsChecked = false
        ary.add(remindBean)
        remindBean = RemindBean()
        remindBean.remindTitle = "提前5分钟"
        remindBean.remindType = 3
        remindBean.remindIsChecked = false
        ary.add(remindBean)
        remindBean = RemindBean()
        remindBean.remindTitle = "提前10分钟"
        remindBean.remindType = 4
        remindBean.remindIsChecked = false
        ary.add(remindBean)
        remindBean = RemindBean()
        remindBean.remindTitle = "提前15分钟"
        remindBean.remindType = 5
        remindBean.remindIsChecked = false
        ary.add(remindBean)
        remindBean = RemindBean()
        remindBean.remindTitle = "提前20分钟"
        remindBean.remindType = 6
        remindBean.remindIsChecked = false
        ary.add(remindBean)
        remindBean = RemindBean()
        remindBean.remindTitle = "提前25分钟"
        remindBean.remindType = 7
        remindBean.remindIsChecked = false
        ary.add(remindBean)
        remindBean = RemindBean()
        remindBean.remindTitle = "提前30分钟"
        remindBean.remindType = 8
        remindBean.remindIsChecked = false
        ary.add(remindBean)
        remindBean = RemindBean()
        remindBean.remindTitle = "提前45分钟"
        remindBean.remindType = 9
        remindBean.remindIsChecked = false
        ary.add(remindBean)
        remindBean = RemindBean()
        remindBean.remindTitle = "提前1个小时"
        remindBean.remindType = 10
        remindBean.remindIsChecked = false
        ary.add(remindBean)
        remindBean = RemindBean()
        remindBean.remindTitle = "提前2个小时"
        remindBean.remindType = 11
        remindBean.remindIsChecked = false
        ary.add(remindBean)
        remindBean = RemindBean()
        remindBean.remindTitle = "提前3个小时"
        remindBean.remindType = 12
        remindBean.remindIsChecked = false
        ary.add(remindBean)
        remindBean = RemindBean()
        remindBean.remindTitle = "提前12个小时"
        remindBean.remindType = 13
        remindBean.remindIsChecked = false
        ary.add(remindBean)
        remindBean = RemindBean()
        remindBean.remindTitle = "提前1天"
        remindBean.remindType = 14
        remindBean.remindIsChecked = false
        ary.add(remindBean)
        remindBean = RemindBean()
        remindBean.remindTitle = "提前2天"
        remindBean.remindType = 15
        remindBean.remindIsChecked = false
        ary.add(remindBean)
        remindBean = RemindBean()
        remindBean.remindTitle = "提前1周"
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

    fun getAllLabel(): LiveData<List<Label>>? {
        var allLabel: LiveData<List<Label>>? = null
        viewModelScope.launch {
            try {
                allLabel = dataRepository.getAllLabel()
            } catch (e: Exception) {
                Log.i("calendar", "获取全部标签失败：$e")
            }
        }
        return allLabel
    }

    fun getUnfinishedScheduleOfDay(day: String): LiveData<List<Schedule>>? {
        var unfinishedScheduleOfDay: LiveData<List<Schedule>>? = null
        viewModelScope.launch {
            try {
                unfinishedScheduleOfDay = dataRepository.getUnfinishedScheduleOfDay(day)
            } catch (e: Exception) {
                Log.i("calendar", "获取某天未完日程失败：$e")
            }
        }
        return unfinishedScheduleOfDay
    }

    fun getFinishedScheduleOfDay(day: String): LiveData<List<Schedule>>? {
        var finishedScheduleOfDay: LiveData<List<Schedule>>? = null
        viewModelScope.launch {
            try {
                finishedScheduleOfDay = dataRepository.getFinishedScheduleOfDay(day)
            } catch (e: Exception) {
                Log.i("calendar", "获取某天已完日程失败：$e")
            }
        }
        return finishedScheduleOfDay
    }

    fun getScheduleDayOfTag(): LiveData<List<String>>? {
        var scheduleDayOfTag: LiveData<List<String>>? = null
        viewModelScope.launch {
            try {
                scheduleDayOfTag = dataRepository.getScheduleDayOfTag()
            } catch (e: Exception) {
                Log.i("calendar", "获取日历已有日程天数标记失败：$e")
            }
        }
        return scheduleDayOfTag
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
}