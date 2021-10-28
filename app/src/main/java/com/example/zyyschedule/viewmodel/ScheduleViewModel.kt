package com.example.zyyschedule.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.zyyschedule.database.DataRepository
import com.example.zyyschedule.database.Label
import com.example.zyyschedule.database.Schedule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ScheduleViewModel(application: Application) : AndroidViewModel(application) {
    private val dataRepository: DataRepository = DataRepository(application)
    var checkAllTag: MutableLiveData<Int> = MutableLiveData(-1)

    fun getAllLabel(): LiveData<List<Label>> {
        return dataRepository.getAllLabel()
    }

    fun deleteLabel(vararg labels: Label) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                dataRepository.deleteLabel(*labels)
            } catch (e: Exception) {
                Log.i("schedule", " 删除标签失败：$e")
            }
        }
    }

    fun getUnfinishedScheduleOfDay(day: String): LiveData<List<Schedule>> {
        return dataRepository.getUnfinishedScheduleOfDay(day)
    }

    fun getFinishedScheduleOfDay(day: String): LiveData<List<Schedule>> {
        return dataRepository.getFinishedScheduleOfDay(day)
    }

    fun allUFScheduleByTime(): LiveData<List<Schedule>> {
        return dataRepository.allUFScheduleByTime()
    }

    fun allFScheduleByTime(): LiveData<List<Schedule>> {
        return dataRepository.allFScheduleByTime()
    }

    fun getFScheduleOfLabel(labelId: String): LiveData<List<Schedule>> {
        return dataRepository.getFScheduleOfLabel(labelId)
    }

    fun getUFScheduleOfLabel(labelId: String): LiveData<List<Schedule>> {
        return dataRepository.getUFScheduleOfLabel(labelId)
    }

    fun deleteSchedule(vararg schedules: Schedule) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                dataRepository.deleteSchedule(*schedules)
            } catch (e: Exception) {
                Log.i("schedule", "删除日程失败：$e")
            }
        }
    }

    fun getLabelTitle(id: String): LiveData<Label> {
        return dataRepository.getLabelTitle(id)
    }
}