package com.example.zyyschedule.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import java.util.Calendar;

public class CalendarViewModel extends AndroidViewModel {
    private int day;
    public MutableLiveData<String> AddScheduleDateAgo;
    public MutableLiveData<String> AddScheduleTime;
    private String dateAgo;

    public CalendarViewModel(@NonNull Application application) {
        super(application);
        Calendar calendar = Calendar.getInstance();
        day = calendar.get(Calendar.DAY_OF_MONTH);
        AddScheduleDateAgo = new MutableLiveData<>();
        AddScheduleTime  = new MutableLiveData<>();
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
            if(selyear-1 == toyear ){
                dateAgo = "明年"+selmonth+"月"+selday+"号";
            }else{
            dateAgo = String.valueOf(selyear - toyear) + "年后的"+selmonth+"月"+selday+"号";
            }
        } else if (selyear < toyear) {
            if(selyear+1 == toyear){
                dateAgo = "去年"+selmonth+"月"+selday+"号";
            }else {
                dateAgo = String.valueOf(toyear - selyear) + "年前的"+selmonth+"月"+selday+"号";
            }
        } else {
            if(selmonth>tomonth){
                if(selmonth-1 == tomonth ){
                    dateAgo = "下个月" + String.valueOf(selday) +"号";
                }else{
                    dateAgo = String.valueOf(selmonth - tomonth) + "个月后的" + String.valueOf(selday) +"号";
                }
            }else if(selmonth<tomonth){
                if(selmonth+1 == toyear){
                    dateAgo = "上个月"+selday+"号";
                }else {
                    dateAgo = String.valueOf( tomonth - selmonth ) + "个月前的" + String.valueOf(selday) +"号";
                }
            }else{
                if(selday > today){
                    dateAgo = String.valueOf(selday - today)+"天后";
                }else if(selday < today){
                    dateAgo = String.valueOf(today - selday)+"天前";
                }else{
                    dateAgo = "今天";
                }

            }

        }
        AddScheduleDateAgo.setValue(dateAgo);

    }

}
