package com.example.zyyschedule.database

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData


class DataRepository(context: Context) {
    private var labelDao: LabelDao
    private var scheduleDao: ScheduleDao

    init {
        val scheduleDataBase: ScheduleDataBase = ScheduleDataBase.getDataBase(context)
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
        for (i in labels.indices) {
            labels[i].id?.let { it ->
                val scheduleList : List<Schedule> = scheduleDao.queryScheduleLabel("%~${it}~%")
                for (j in scheduleList.indices) {
                        scheduleList[j].labelId = scheduleList[j].labelId?.replace("~${it}~","~")
                        updateSchedule(scheduleList[j])
                    }
            }
        }
    }

    suspend fun insertLabel(vararg labels: Label) {
        labelDao.insertLabel(*labels)
    }

    suspend fun updateSchedule(vararg schedule:Schedule){
        scheduleDao.updateSchedule(*schedule)
    }


    fun getScheduleDayOfTag(): LiveData<List<String>> {
        return scheduleDao.getScheduleDayOfTag()
    }

    fun getUnfinishedScheduleOfDay(day: String): LiveData<List<Schedule>> {
        return scheduleDao.getUnfinishedScheduleOfDay(day)
    }

    fun getFinishedScheduleOfDay(day: String?): LiveData<List<Schedule>> {
        return scheduleDao.getFinishedScheduleOfDay(day)
    }

    fun checkLabel(title: String): LiveData<List<Label>> {
        return labelDao.checkLabel(title)
    }

    fun getAllLabel(): LiveData<List<Label>> {
        return labelDao.getAllLabel()
    }

    fun getLabelTitle(id: String): LiveData<Label> {
        return labelDao.getLabelTitle(id.toInt())

    }

    fun getALLUnFinishOfRemind(): LiveData<List<Schedule>> {
        return scheduleDao.getALLUnFinishOfRemind()
    }

    fun allUFScheduleByTime(): LiveData<List<Schedule>> {
        return scheduleDao.allUFScheduleByTime()
    }

    fun allFScheduleByTime(): LiveData<List<Schedule>> {
        return scheduleDao.allFScheduleByTime()
    }

    fun getFScheduleOfLabel(labelId: String): LiveData<List<Schedule>> {
        return scheduleDao.getFScheduleOfLabel(labelId)
    }

    fun getUFScheduleOfLabel(labelId: String): LiveData<List<Schedule>> {
        return scheduleDao.getUFScheduleOfLabel(labelId)
    }

    fun checkLabelTFI(labelText:String):LiveData<Int>{
        return labelDao.checkLabelTitleForInsert(labelText)
    }
}