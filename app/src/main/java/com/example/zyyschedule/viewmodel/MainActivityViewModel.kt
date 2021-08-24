package com.example.zyyschedule.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.zyyschedule.database.DataRepository
import com.example.zyyschedule.database.Schedule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {
    private val dataRepository: DataRepository = DataRepository(application)

    fun getALLUnFinishOfRemind(): LiveData<List<Schedule>>? {
        var aLLUnFinishOfRemind: LiveData<List<Schedule>>? = null
        viewModelScope.launch {
            try {
                aLLUnFinishOfRemind = dataRepository.getALLUnFinishOfRemind()
            } catch (e: Exception) {
                Log.i("main", "获取所有日程提醒失败：$e")
            }
        }
        return aLLUnFinishOfRemind
    }

    fun updateRemindTag(vararg id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try{
                dataRepository.updateRemindTag(*id)
            }catch (e: Exception){
                Log.i("main", "更改提醒状态失败：$e")
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

}