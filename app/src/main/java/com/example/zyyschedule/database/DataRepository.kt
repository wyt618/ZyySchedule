package com.example.zyyschedule.database

import android.content.Context
import androidx.lifecycle.LiveData


class DataRepository(context: Context) {
    private var labelDao:LabelDao
    private var scheduleDao:ScheduleDao

    init {
        val scheduleDataBase:ScheduleDataBase = ScheduleDataBase.getDataBase(context)
        labelDao = scheduleDataBase.getLabelDao()!!
        scheduleDao = scheduleDataBase.getScheduleDao()!!
    }

    suspend fun changeStateSchedule(vararg schedules: Schedule) {
        scheduleDao.changeStateSchedule(*schedules)
    }
    suspend fun insertSchedule(vararg schedules: Schedule) {
        scheduleDao.insertSchedule(*schedules)
    }

    suspend fun deleteSchedule(vararg schedules: Schedule) {
        scheduleDao.deleteSchedule(*schedules)
    }

    suspend fun updateRemindTag(vararg id: Int) {
        scheduleDao.updateRemindTag(*id)
    }

    suspend fun deleteLabel(vararg labels: Label) {
        labelDao.deleteLabel(*labels)
        for( i in labels.indices){
            labels[i].id?.let { scheduleDao.deleteScheduleLabel(it) }
        }
    }

    suspend fun insertLabel(vararg labels: Label) {
        labelDao.insertLabel(*labels)
    }


    suspend fun getScheduleDayOfTag(): LiveData<List<String>> {
        return scheduleDao.getScheduleDayOfTag()
    }

    suspend fun getUnfinishedScheduleOfDay(day: String): LiveData<List<Schedule>> {
        return scheduleDao.getUnfinishedScheduleOfDay(day)
    }

    suspend fun getFinishedScheduleOfDay(day: String?): LiveData<List<Schedule>> {
        return scheduleDao.getFinishedScheduleOfDay(day)
    }

    suspend fun checkLabel(title: String): LiveData<List<Label>> {
        return labelDao.checkLabel(title)
    }

    suspend fun getAllLabel(): LiveData<List<Label>>{
        return labelDao.getAllLabel()
    }

    suspend fun getALLUnFinishOfRemind(): LiveData<List<Schedule>> {
        return scheduleDao.getALLUnFinishOfRemind()
    }

    suspend fun allUFScheduleByTime(): LiveData<List<Schedule>> {
        return  scheduleDao.allUFScheduleByTime()
    }

    suspend fun allFScheduleByTime(): LiveData<List<Schedule>> {
        return  scheduleDao.allFScheduleByTime()
    }

    suspend fun getFScheduleOfLabel(labelId: Int): LiveData<List<Schedule>> {
        return  scheduleDao.getFScheduleOfLabel(labelId)
    }

    suspend fun getUFScheduleOfLabel(labelId: Int): LiveData<List<Schedule>> {
        return scheduleDao.getUFScheduleOfLabel(labelId)
    }
}