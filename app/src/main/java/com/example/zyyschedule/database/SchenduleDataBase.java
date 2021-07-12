package com.example.zyyschedule.database;

import android.content.Context;


import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;


@Database(entities = {Label.class, Schedule.class}, version = 1, exportSchema = false)
public abstract class SchenduleDataBase extends RoomDatabase {
    private static SchenduleDataBase INSTANCE;
    public static final String DB_NAME = "ZyySchedule.db";

    static synchronized SchenduleDataBase getDataBase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), SchenduleDataBase.class, DB_NAME)
                    .build();
        }
        return INSTANCE;
    }

    public abstract ScheduleDao getScheduleDao();

    public abstract LabelDao getLabelDao();
}
