package com.example.zyyschedule.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface LabelDao {
    @Insert
    void insertLabel(Label ...labels);

    @Query("SELECT * FROM Label WHERE title =:title")
    LiveData<List<Label>> CheckLabel(String title);
}
