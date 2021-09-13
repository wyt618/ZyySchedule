package com.example.zyyschedule.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Label(
        @PrimaryKey(autoGenerate = true)
        var id: Int? = null,
        @ColumnInfo(name = "title")
        var title: String? = null,
        @ColumnInfo(name = "color")
        var color: Int? = null
)