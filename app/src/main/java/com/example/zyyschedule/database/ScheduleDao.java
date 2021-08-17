package com.example.zyyschedule.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ScheduleDao {
    @Insert
    void insertSchedule(Schedule ...schedules);
    @Query("SELECT * FROM Schedule WHERE starttime like :day and state = 0 ")
    LiveData<List<Schedule>>getUnfinishedScheduleOfDay(String day);
    @Update
    void ChangeStateSchedule(Schedule ...schedules);
    @Query("SELECT * FROM Schedule WHERE starttime like :day and state = 1")
    LiveData<List<Schedule>>getFinishedScheduleOfDay(String day);
    @Query("SELECT DISTINCT substr(starttime,0,length(starttime)-7) FROM Schedule WHERE state = 0")
    LiveData<List<String>>getScheduleDayOfTag();
    @Query("DELETE FROM Schedule  WHERE labelid =:i ")
    void updateScheduleLabel(int i);
    @Delete
    void deleteSchedule(Schedule... schedules);
    @Query("SELECT * FROM Schedule WHERE state = 0 and tagRemind = 0")
    LiveData<List<Schedule>>getALLUnFinishOfRemind();
    @Query("UPDATE Schedule SET tagRemind = 1 WHERE id =:id")
    void updateRemindTag(int id);
    @Query("SELECT * FROM Schedule WHERE state = 0 ORDER BY datetime(starttime)")
    LiveData<List<Schedule>>allUFScheduleByTime();
    @Query("SELECT * FROM Schedule WHERE state = 1 ORDER BY datetime(starttime)")
    LiveData<List<Schedule>>allFScheduleByTime();
    @Query("SELECT * FROM Schedule WHERE labelid = :labelid and state = 0 ORDER BY datetime(starttime)")
    LiveData<List<Schedule>>getUFScheduleOfLabel(int labelid);
    @Query("SELECT * FROM Schedule WHERE labelid = :labelid and state = 1 ORDER BY datetime(starttime)")
    LiveData<List<Schedule>>getFScheduleOfLabel(int labelid);
}
