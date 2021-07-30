package com.example.zyyschedule.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.zyyschedule.PriorityBean;
import com.example.zyyschedule.RemindBean;
import com.example.zyyschedule.database.DataRepositor;
import com.example.zyyschedule.database.Label;
import com.example.zyyschedule.database.Schedule;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CalendarViewModel extends AndroidViewModel {
    private int day;
    public MutableLiveData<String> AddScheduleDateAgo;
    public MutableLiveData<String> AddScheduleTime;
    private String dateAgo;
    public MutableLiveData<Integer> priorityid;
    public MutableLiveData<String> priority;
    public MutableLiveData<String> label;
    public MutableLiveData<String> remindtext;
    public MutableLiveData<String> getPriority() {
        return priority;
    }
    private DataRepositor dataRepositor;
    private PriorityBean priorityBean;
    private RemindBean remindBean;

    public void setPriority(MutableLiveData<String> priority) {
        this.priority = priority;
    }

    public CalendarViewModel(@NonNull Application application) {
        super(application);
        dataRepositor = new DataRepositor(application);
        Calendar calendar = Calendar.getInstance();
        day = calendar.get(Calendar.DAY_OF_MONTH);
        AddScheduleDateAgo = new MutableLiveData<>();
        AddScheduleTime = new MutableLiveData<>();
        priority = new MutableLiveData<>();
        priorityid = new MutableLiveData<>();
        label = new MutableLiveData<>();
        remindtext = new MutableLiveData<>();
        AddScheduleTime.setValue("00:00");
        priority.setValue("无优先级");
        label.setValue("无标签");
        priorityid.setValue(0);
        remindtext.setValue("无提醒");
    }
    public void insertSchedule(Schedule ...schedules){
        dataRepositor.insertSchedule(schedules);
    }

    public LiveData<List<Label>>getAllLabel(){
        return dataRepositor.getAllLabel();
    }

    public MutableLiveData<String> getAddScheduleDateAgo() {
        return AddScheduleDateAgo;
    }

    public void setAddScheduleDateAgo(MutableLiveData<String> addScheduleDateAgo) {
        AddScheduleDateAgo = addScheduleDateAgo;
    }

    public MutableLiveData<String> getAddScheduleTime() {
        return AddScheduleTime;
    }

    public void setAddScheduleTime(MutableLiveData<String> addScheduleTime) {
        AddScheduleTime = addScheduleTime;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public void addScheduleDateAgo(int selyear, int selmonth, int selday, int toyear, int tomonth, int today) {

        if (selyear > toyear) {
            if (selyear - 1 == toyear) {
                dateAgo = "明年" + selmonth + "月" + selday + "号";
            } else {
                dateAgo = String.valueOf(selyear - toyear) + "年后的" + selmonth + "月" + selday + "号";
            }
        } else if (selyear < toyear) {
            if (selyear + 1 == toyear) {
                dateAgo = "去年" + selmonth + "月" + selday + "号";
            } else {
                dateAgo = String.valueOf(toyear - selyear) + "年前的" + selmonth + "月" + selday + "号";
            }
        } else {
            if (selmonth > tomonth) {
                if (selmonth - 1 == tomonth) {
                    dateAgo = "下个月" + String.valueOf(selday) + "号";
                } else {
                    dateAgo = String.valueOf(selmonth - tomonth) + "个月后的" + String.valueOf(selday) + "号";
                }
            } else if (selmonth < tomonth) {
                if (selmonth + 1 == toyear) {
                    dateAgo = "上个月" + selday + "号";
                } else {
                    dateAgo = String.valueOf(tomonth - selmonth) + "个月前的" + String.valueOf(selday) + "号";
                }
            } else {
                if (selday > today) {
                    dateAgo = String.valueOf(selday - today) + "天后";
                } else if (selday < today) {
                    dateAgo = String.valueOf(today - selday) + "天前";
                } else {
                    dateAgo = "今天";
                }

            }

        }
        AddScheduleDateAgo.setValue(dateAgo);

    }


    public ArrayList PriorityListData() {
        ArrayList ary = new ArrayList<>();
        priorityBean = new PriorityBean();
        priorityBean.setPrioritytitle("无优先级");
        priorityBean.setPrioritytype(0);
        ary.add(priorityBean);
        priorityBean = new PriorityBean();
        priorityBean.setPrioritytitle("低优先级");
        priorityBean.setPrioritytype(1);
        ary.add(priorityBean);
        priorityBean = new PriorityBean();
        priorityBean.setPrioritytitle("中优先级");
        priorityBean.setPrioritytype(2);
        ary.add(priorityBean);
        priorityBean = new PriorityBean();
        priorityBean.setPrioritytitle("高优先级");
        priorityBean.setPrioritytype(3);
        ary.add(priorityBean);
        return ary;
    }

    public ArrayList RemindListData() {
        ArrayList ary = new ArrayList<>();
        remindBean = new RemindBean();
        remindBean.setRemindtitle("准时");
        remindBean.setRemindtype(1);
        remindBean.setRemindisChecked(false);
        ary.add(remindBean);
        remindBean = new RemindBean();
        remindBean.setRemindtitle("提前1分钟");
        remindBean.setRemindtype(2);
        remindBean.setRemindisChecked(false);
        ary.add(remindBean);
        remindBean = new RemindBean();
        remindBean.setRemindtitle("提前5分钟");
        remindBean.setRemindtype(3);
        remindBean.setRemindisChecked(false);
        ary.add(remindBean);
        remindBean = new RemindBean();
        remindBean.setRemindtitle("提前10分钟");
        remindBean.setRemindtype(4);
        remindBean.setRemindisChecked(false);
        ary.add(remindBean);
        remindBean = new RemindBean();
        remindBean.setRemindtitle("提前15分钟");
        remindBean.setRemindtype(5);
        remindBean.setRemindisChecked(false);
        ary.add(remindBean);
        remindBean = new RemindBean();
        remindBean.setRemindtitle("提前20分钟");
        remindBean.setRemindtype(6);
        remindBean.setRemindisChecked(false);
        ary.add(remindBean);
        remindBean = new RemindBean();
        remindBean.setRemindtitle("提前25分钟");
        remindBean.setRemindtype(7);
        remindBean.setRemindisChecked(false);
        ary.add(remindBean);
        remindBean = new RemindBean();
        remindBean.setRemindtitle("提前30分钟");
        remindBean.setRemindtype(8);
        remindBean.setRemindisChecked(false);
        ary.add(remindBean);
        remindBean = new RemindBean();
        remindBean.setRemindtitle("提前45分钟");
        remindBean.setRemindtype(9);
        remindBean.setRemindisChecked(false);
        ary.add(remindBean);
        remindBean = new RemindBean();
        remindBean.setRemindtitle("提前1个小时");
        remindBean.setRemindtype(10);
        remindBean.setRemindisChecked(false);
        ary.add(remindBean);
        remindBean = new RemindBean();
        remindBean.setRemindtitle("提前2个小时");
        remindBean.setRemindtype(11);
        remindBean.setRemindisChecked(false);
        ary.add(remindBean);
        remindBean = new RemindBean();
        remindBean.setRemindtitle("提前3个小时");
        remindBean.setRemindtype(12);
        remindBean.setRemindisChecked(false);
        ary.add(remindBean);
        remindBean = new RemindBean();
        remindBean.setRemindtitle("提前12个小时");
        remindBean.setRemindtype(13);
        remindBean.setRemindisChecked(false);
        ary.add(remindBean);
        remindBean = new RemindBean();
        remindBean.setRemindtitle("提前1天");
        remindBean.setRemindtype(14);
        remindBean.setRemindisChecked(false);
        ary.add(remindBean);
        remindBean = new RemindBean();
        remindBean.setRemindtitle("提前2天");
        remindBean.setRemindtype(15);
        remindBean.setRemindisChecked(false);
        ary.add(remindBean);
        remindBean = new RemindBean();
        remindBean.setRemindtitle("提前1周");
        remindBean.setRemindtype(15);
        remindBean.setRemindisChecked(false);
        ary.add(remindBean);
        return ary;
    }

}
