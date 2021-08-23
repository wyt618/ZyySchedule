package com.example.zyyschedule.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Label::class, Schedule::class], version = 1, exportSchema = false)
abstract class ScheduleDataBase:RoomDatabase() {
    abstract fun getScheduleDao(): ScheduleDao?
    abstract fun getLabelDao(): LabelDao?
    companion object{
        @Volatile
        private var INSTANCE:ScheduleDataBase? = null
        private const val dbname:String = "ZyySchedule.db"
        fun getDataBase(context: Context):ScheduleDataBase{
            return INSTANCE?: synchronized(this){
                val instance = Room.databaseBuilder(context.applicationContext, ScheduleDataBase::class.java,dbname)
                        .build()
                INSTANCE = instance
                instance
            }
        }
    }

}