package com.example.zyyschedule.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.zyyschedule.database.DataRepository
import com.example.zyyschedule.database.Label
import com.example.zyyschedule.database.Schedule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ScheduleViewModel(application: Application) : AndroidViewModel(application) {
    private val dataRepository:DataRepository = DataRepository(application)
    fun getAllLabel(): LiveData<List<Label>>? {
        var allLabel:LiveData<List<Label>>? = null
        viewModelScope.launch {
            try {
                allLabel = dataRepository.getAllLabel()
            }catch (e: Exception){
                Log.i("schedule", "获取所有标签失败：$e")
            }
        }
        return allLabel
    }

    fun deleteLabel(vararg labels: Label) {
         viewModelScope.launch(Dispatchers.IO) {
             try {
                 dataRepository.deleteLabel(*labels)
             }catch (e: Exception){
                 Log.i("schedule", " 删除标签失败：$e")
             }
         }
    }

    fun getUnfinishedScheduleOfDay(day: String): LiveData<List<Schedule>>? {
        var unfinishedScheduleOfDay: LiveData<List<Schedule>>? = null
        viewModelScope.launch {
            try {
                unfinishedScheduleOfDay = dataRepository.getUnfinishedScheduleOfDay(day)
            } catch (e: Exception) {
                Log.i("schedule", "获取某天未完日程失败：$e")
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
                Log.i("schedule", "获取某天已完日程失败：$e")
            }
        }
        return finishedScheduleOfDay
    }

    fun allUFScheduleByTime(): LiveData<List<Schedule>>? {
        var allUFScheduleByTime:LiveData<List<Schedule>>? = null
        viewModelScope.launch {
            try {
                allUFScheduleByTime = dataRepository.allUFScheduleByTime()
            }catch (e: Exception){
                Log.i("schedule", "获取所有未完日程失败：$e")
            }
        }
        return allUFScheduleByTime
    }

    fun allFScheduleByTime(): LiveData<List<Schedule>>? {
        var allFScheduleByTime:LiveData<List<Schedule>>? = null
        viewModelScope.launch {
            try {
                allFScheduleByTime = dataRepository.allFScheduleByTime()
            }catch (e: Exception){
                Log.i("schedule", "获取所有已完日程失败：$e")
            }
        }
        return allFScheduleByTime
    }

    fun getFScheduleOfLabel(labelId: Int): LiveData<List<Schedule>>? {
        var fScheduleOfLabel:LiveData<List<Schedule>>? = null
        viewModelScope.launch {
            try {
                fScheduleOfLabel = dataRepository.getFScheduleOfLabel(labelId)
            }catch (e: Exception){
                Log.i("schedule", " 按标签获取所有已完日程失败：$e")
            }
        }
        return fScheduleOfLabel
    }

    fun getUFScheduleOfLabel(labelId: Int): LiveData<List<Schedule>>? {
        var ufScheduleOfLabel:LiveData<List<Schedule>>? = null
        viewModelScope.launch {
            try {
                ufScheduleOfLabel = dataRepository.getUFScheduleOfLabel(labelId)
            }catch (e: Exception){
                Log.i("schedule", " 按标签获取所有未完日程失败：$e")
            }
        }
        return ufScheduleOfLabel
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

}