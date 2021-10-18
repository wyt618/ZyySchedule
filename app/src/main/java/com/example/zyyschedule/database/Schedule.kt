package com.example.zyyschedule.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity
data class Schedule(
        @PrimaryKey(autoGenerate = true)
        var id: Int? = null,//日程id
        @ColumnInfo(name = "startTime")
        var startTime: String? = null,//日程开始时间
        @ColumnInfo(name = "endTime")
        var endTime: String? = null,//日程结束时间
        @ColumnInfo(name = "remind")
        var remind: String? = null,//提醒日期
        @ColumnInfo(name = "title")
        var title: String? = null,//日程标题
        @ColumnInfo(name = "detailed")
        var detailed: String? = null,//日程详细
        @ColumnInfo(name = "state")
        var state: String? = null,//日程状态 0未完成 1已完成
        @ColumnInfo(name = "priority")
        var priority: Int? = 0,//优先级索引 0无,1低,2中,3高
        @ColumnInfo(name = "labelId")
        var labelId: String? = "0",//标签id
        @ColumnInfo(name = "tagRemind")
        var tagRemind: Boolean = false, //提醒是否设置
        @Ignore
        var isChecked: Boolean = false,//在列表中判断日程是否选中判断日程是否选中
        @Ignore
        var isEditor: Boolean = false,//判断是否处于编辑状态
        @Ignore
        var isEditorChecked: Boolean = false //判断在编辑状态下是否选中
)