package com.example.zyyschedule.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface LabelDao {
    @Insert
    fun insertLabel(vararg labels: Label)

    @Query("SELECT * FROM Label WHERE title =:title")
    fun checkLabel(title: String): LiveData<List<Label>>

    @Query("SELECT * FROM Label")
    fun getAllLabel(): LiveData<List<Label>>

    @Delete
    fun deleteLabel(vararg labels: Label)

    @Query("SELECT title FROM Label WHERE id =:id")
    fun getLabelTitle(id: Int): LiveData<String>
}