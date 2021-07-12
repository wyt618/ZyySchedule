package com.example.zyyschedule.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Schedule {
    @PrimaryKey(autoGenerate = true)
    private int id;   //日程id
    @ColumnInfo(name = "starttime")
    private String starttime;  //日程开始时间
    @ColumnInfo(name = "endtime")
    private String endtime;  //日程开始时间
    @ColumnInfo(name = "remind")
    private int remind;  //提醒模式
    @ColumnInfo(name = "title")
    private String title;  //日程标题
    @ColumnInfo(name = "detailed")
    private String detailed;  //日程详细
    @ColumnInfo(name = "state")
    private String state;  //日程状态
    @ColumnInfo(name = "priority")
    private int priority;  //优先级索引
    @ColumnInfo(name = "labelid")
    private int labelid;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRemind() {
        return remind;
    }

    public void setRemind(int remind) {
        this.remind = remind;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetailed() {
        return detailed;
    }

    public void setDetailed(String detailed) {
        this.detailed = detailed;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getLabelid() {
        return labelid;
    }

    public void setLabelid(int labelid) {
        this.labelid = labelid;
    }

    public String getStarttime() {
        return starttime;
    }

    public void setStarttime(String starttime) {
        this.starttime = starttime;
    }

    public String getEndtime() {
        return endtime;
    }

    public void setEndtime(String endtime) {
        this.endtime = endtime;
    }
}