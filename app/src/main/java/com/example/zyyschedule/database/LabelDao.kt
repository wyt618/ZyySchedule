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

    @Query("SELECT * FROM Label WHERE id =:id")
    fun getLabelTitle(id: Int): LiveData<Label>

    @Query("SELECT COUNT(*) FROM Label WHERE title = :labelText")
    fun checkLabelTitleForInsert(labelText:String):LiveData<Int>

    @Query("SELECT * FROM Label WHERE title like :labelText ")
    fun fuzzyLabelTitle(labelText: String):LiveData<List<Label>>
}