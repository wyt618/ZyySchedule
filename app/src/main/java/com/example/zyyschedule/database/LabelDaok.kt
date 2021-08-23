package com.example.zyyschedule.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface LabelDaok {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertLabel(vararg labels: Label)
    @Query("SELECT * FROM Label WHERE title =:title")
    suspend fun checkLabel(title: String): LiveData<MutableList<Label?>?>?
    @Query("SELECT * FROM Label")
    suspend fun getAllLabel(): LiveData<List<Label?>?>?
    @Delete
    suspend fun deleteLabel(vararg labels: Label)
}