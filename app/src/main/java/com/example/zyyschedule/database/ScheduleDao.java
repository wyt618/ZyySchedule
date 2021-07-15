package com.example.zyyschedule.database;

import androidx.room.Dao;
import androidx.room.Insert;

@Dao
public interface ScheduleDao {
    @Insert
    void insertSchedule(Schedule ...schedules);
}
