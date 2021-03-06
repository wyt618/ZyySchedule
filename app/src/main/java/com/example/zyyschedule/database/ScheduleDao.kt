package com.example.zyyschedule.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.*

@Dao
interface ScheduleDao {
    @Insert
    fun insertSchedule(vararg schedules: Schedule)

    @Query("SELECT * FROM Schedule WHERE startTime like :day and state = 0 ")
    fun getUnfinishedScheduleOfDay(day: String?): LiveData<List<Schedule>>

    @Update
    fun changeStateSchedule(vararg schedules: Schedule)

    @Query("SELECT * FROM Schedule WHERE startTime like :day and state = 1")
    fun getFinishedScheduleOfDay(day: String?): LiveData<List<Schedule>>

    @Query("SELECT DISTINCT substr(startTime,0,length(startTime)-7) FROM Schedule WHERE state = 0")
    fun getScheduleDayOfTag(): LiveData<List<String>>

    @Query("SELECT * FROM Schedule WHERE labelId like :slId ")
    fun queryScheduleLabel(slId: String): List<Schedule>


    @Delete
    fun deleteSchedule(vararg schedules: Schedule)

    @Query("SELECT * FROM Schedule WHERE state = 0 and tagRemind = 0")
    fun getALLUnFinishOfRemind(): LiveData<List<Schedule>>

    @Query("UPDATE Schedule SET tagRemind = 1 WHERE id =:id")
    fun updateRemindTag(vararg id: Int)

    @Query("SELECT * FROM Schedule WHERE state = 0 ORDER BY datetime(startTime)")
    fun allUFScheduleByTime(): LiveData<List<Schedule>>

    @Query("SELECT * FROM Schedule WHERE state = 1 ORDER BY datetime(startTime)")
    fun allFScheduleByTime(): LiveData<List<Schedule>>

    @Query("SELECT * FROM Schedule WHERE labelId like :labelId and state = 0 ORDER BY datetime(startTime)")
    fun getUFScheduleOfLabel(labelId: String): LiveData<List<Schedule>>

    @Query("SELECT * FROM Schedule WHERE labelId like  :labelId and state = 1 ORDER BY datetime(startTime)")
    fun getFScheduleOfLabel(labelId: String): LiveData<List<Schedule>>

    @Update
    fun updateSchedule(vararg schedules: Schedule)
}