package com.example.zyyschedule.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class Schedule {
    @PrimaryKey(autoGenerate = true)
    private int id;   //日程id
    @ColumnInfo(name = "starttime")
    private String starttime;  //日程开始时间
    @ColumnInfo(name = "endtime")
    private String endtime;  //日程结束时间
    @ColumnInfo(name = "remind")
    private String remind;  //提醒日期
    @ColumnInfo(name = "title")
    private String title;  //日程标题
    @ColumnInfo(name = "detailed")
    private String detailed;  //日程详细
    @ColumnInfo(name = "state")
    private String state;  //日程状态 0未完成 1已完成
    @ColumnInfo(name = "priority")
    private int priority;  //优先级索引 0无,1低,2中,3高
    @ColumnInfo(name = "labelid")
    private int labelid;  //标签id
    @ColumnInfo(name = "tagRemind")
    private boolean tagRemind = false; //提醒是否设置
    @Ignore
    private Boolean isChecked;//在列表中判断日程是否选中判断日程是否选中
    @Ignore
    private Boolean isEditor = false; //判断是否处于编辑状态
    @Ignore
    private Boolean isEditorChecked = false; //判断在编辑状态下是否选中
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRemind() {
        return remind;
    }

    public void setRemind(String remind) {
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

    public Boolean getChecked() {
        return isChecked;
    }

    public void setChecked(Boolean checked) {
        isChecked = checked;
    }

    public Boolean getEditor() {
        return isEditor;
    }

    public void setEditor(Boolean editor) {
        isEditor = editor;
    }

    public Boolean getEditorChecked() {
        return isEditorChecked;
    }

    public void setEditorChecked(Boolean editorChecked) {
        isEditorChecked = editorChecked;
    }

    public boolean isTagRemind() {
        return tagRemind;
    }

    public void setTagRemind(boolean tagRemind) {
        this.tagRemind = tagRemind;
    }

    @Override
    public String toString() {
        return "Schedule{" +
                "id='" + id + '\''+
                "starttime='" + starttime + '\''+
                "endtime='" + endtime + '\''+
                "remind='" + remind + '\''+
                "title='" + title + '\''+
                "detailed='" + detailed + '\''+
                "state='" + state + '\''+
                "priority='" + priority + '\''+
                "labelid='" + labelid + '\''+
                "isChecked='" + isChecked + '\''+
                "isEditor='" + isEditor + '\''+
                "isEditorChecked='" + isEditorChecked + '\''+
                "tagRemind='" + tagRemind + '\''+
                '}';
    }
}
