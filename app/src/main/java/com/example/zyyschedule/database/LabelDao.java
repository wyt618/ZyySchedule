package com.example.zyyschedule.database;

import androidx.room.Dao;
import androidx.room.Insert;

@Dao
public interface LabelDao {
    @Insert
    void insertLabel(Label ...labels);
}
