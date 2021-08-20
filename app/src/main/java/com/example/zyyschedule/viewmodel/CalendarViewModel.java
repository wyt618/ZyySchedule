package com.example.zyyschedule.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.zyyschedule.PriorityBean;
import com.example.zyyschedule.RemindBean;
import com.example.zyyschedule.database.DataRepository;
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
    private final DataRepository dataRepository;
    private PriorityBean priorityBean;
    private RemindBean remindBean;

    public void setPriority(MutableLiveData<String> priority) {
        this.priority = priority;
    }

    public CalendarViewModel(@NonNull Application application) {
        super(application);
        dataRepository = new DataRepository(application);
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
        dataRepository.insertSchedule(schedules);
    }

    public void deleteSchedule(Schedule ...schedules){
        dataRepository.deleteSchedule(schedules);
    }

    public LiveData<List<Label>>getAllLabel(){
        return dataRepository.getAllLabel();
    }

    public LiveData<List<Schedule>>getUnfinishedScheduleOfDay(String day){
       return dataRepository.getUnfinishedScheduleOfDay(day);
    }
    public LiveData<List<Schedule>>getFinishedScheduleOfDay(String day){
        return dataRepository.getFinishedScheduleOfDay(day);
    }
    public LiveData<List<String>> getScheduleDayOfTag(){
        return dataRepository.getScheduleDayOfTag();
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
    public void ChangeStateSchedule(Schedule ...schedules){
        dataRepository.ChangeStateSchedule(schedules);
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
                dateAgo = (selyear - toyear) + "年后的" + selmonth + "月" + selday + "号";
            }
        } else if (selyear < toyear) {
            if (selyear + 1 == toyear) {
                dateAgo = "去年" + selmonth + "月" + selday + "号";
            } else {
                dateAgo = (toyear - selyear) + "年前的" + selmonth + "月" + selday + "号";
            }
        } else {
            if (selmonth > tomonth) {
                if (selmonth - 1 == tomonth) {
                    dateAgo = "下个月" + selday + "号";
                } else {
                    dateAgo = (selmonth - tomonth) + "个月后的" + selday + "号";
                }
            } else if (selmonth < tomonth) {
                if (selmonth + 1 == toyear) {
                    dateAgo = "上个月" + selday + "号";
                } else {
                    dateAgo = (tomonth - selmonth) + "个月前的" + selday + "号";
                }
            } else {
                if (selday > today) {
                    dateAgo = (selday - today) + "天后";
                } else if (selday < today) {
                    dateAgo = (today - selday) + "天前";
                } else {
                    dateAgo = "今天";
                }

            }

        }
        AddScheduleDateAgo.setValue(dateAgo);

    }


    public ArrayList<PriorityBean> PriorityListData() {
        ArrayList<PriorityBean> ary = new ArrayList<>();
        priorityBean = new PriorityBean();
        priorityBean.setPriorityTitle("无优先级");
        priorityBean.setPriorityType(0);
        ary.add(priorityBean);
        priorityBean = new PriorityBean();
        priorityBean.setPriorityTitle("低优先级");
        priorityBean.setPriorityType(1);
        ary.add(priorityBean);
        priorityBean = new PriorityBean();
        priorityBean.setPriorityTitle("中优先级");
        priorityBean.setPriorityType(2);
        ary.add(priorityBean);
        priorityBean = new PriorityBean();
        priorityBean.setPriorityTitle("高优先级");
        priorityBean.setPriorityType(3);
        ary.add(priorityBean);
        return ary;
    }

    public ArrayList<RemindBean> RemindListData() {
        ArrayList<RemindBean> ary = new ArrayList<>();
        remindBean = new RemindBean();
        remindBean.setRemindTitle("准时");
        remindBean.setRemindType(1);
        remindBean.setRemindIsChecked(false);
        ary.add(remindBean);
        remindBean = new RemindBean();
        remindBean.setRemindTitle("提前1分钟");
        remindBean.setRemindType(2);
        remindBean.setRemindIsChecked(false);
        ary.add(remindBean);
        remindBean = new RemindBean();
        remindBean.setRemindTitle("提前5分钟");
        remindBean.setRemindType(3);
        remindBean.setRemindIsChecked(false);
        ary.add(remindBean);
        remindBean = new RemindBean();
        remindBean.setRemindTitle("提前10分钟");
        remindBean.setRemindType(4);
        remindBean.setRemindIsChecked(false);
        ary.add(remindBean);
        remindBean = new RemindBean();
        remindBean.setRemindTitle("提前15分钟");
        remindBean.setRemindType(5);
        remindBean.setRemindIsChecked(false);
        ary.add(remindBean);
        remindBean = new RemindBean();
        remindBean.setRemindTitle("提前20分钟");
        remindBean.setRemindType(6);
        remindBean.setRemindIsChecked(false);
        ary.add(remindBean);
        remindBean = new RemindBean();
        remindBean.setRemindTitle("提前25分钟");
        remindBean.setRemindType(7);
        remindBean.setRemindIsChecked(false);
        ary.add(remindBean);
        remindBean = new RemindBean();
        remindBean.setRemindTitle("提前30分钟");
        remindBean.setRemindType(8);
        remindBean.setRemindIsChecked(false);
        ary.add(remindBean);
        remindBean = new RemindBean();
        remindBean.setRemindTitle("提前45分钟");
        remindBean.setRemindType(9);
        remindBean.setRemindIsChecked(false);
        ary.add(remindBean);
        remindBean = new RemindBean();
        remindBean.setRemindTitle("提前1个小时");
        remindBean.setRemindType(10);
        remindBean.setRemindIsChecked(false);
        ary.add(remindBean);
        remindBean = new RemindBean();
        remindBean.setRemindTitle("提前2个小时");
        remindBean.setRemindType(11);
        remindBean.setRemindIsChecked(false);
        ary.add(remindBean);
        remindBean = new RemindBean();
        remindBean.setRemindTitle("提前3个小时");
        remindBean.setRemindType(12);
        remindBean.setRemindIsChecked(false);
        ary.add(remindBean);
        remindBean = new RemindBean();
        remindBean.setRemindTitle("提前12个小时");
        remindBean.setRemindType(13);
        remindBean.setRemindIsChecked(false);
        ary.add(remindBean);
        remindBean = new RemindBean();
        remindBean.setRemindTitle("提前1天");
        remindBean.setRemindType(14);
        remindBean.setRemindIsChecked(false);
        ary.add(remindBean);
        remindBean = new RemindBean();
        remindBean.setRemindTitle("提前2天");
        remindBean.setRemindType(15);
        remindBean.setRemindIsChecked(false);
        ary.add(remindBean);
        remindBean = new RemindBean();
        remindBean.setRemindTitle("提前1周");
        remindBean.setRemindType(15);
        remindBean.setRemindIsChecked(false);
        ary.add(remindBean);
        return ary;
    }


}
