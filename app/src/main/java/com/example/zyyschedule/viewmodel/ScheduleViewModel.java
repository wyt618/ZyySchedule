package com.example.zyyschedule.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.zyyschedule.database.DataRepositor;
import com.example.zyyschedule.database.Label;
import com.example.zyyschedule.database.Schedule;

import java.util.List;

public class ScheduleViewModel extends AndroidViewModel {
    private final DataRepositor dataRepositor;



    public ScheduleViewModel(@NonNull Application application) {
        super(application);
        dataRepositor = new DataRepositor(application);

    }
    public LiveData<List<Label>> getAllLabel(){
        return dataRepositor.getAllLabel();
    }

    public void deleteLabel(Label ...labels){
        dataRepositor.deleteLabel(labels);
    }
    public LiveData<List<Schedule>>getUnfinishedScheduleOfDay(String day){
        return dataRepositor.getUnfinishedScheduleOfDay(day);
    }
    public LiveData<List<Schedule>>getFinishedScheduleOfDay(String day){
        return dataRepositor.getFinishedScheduleOfDay(day);
    }
    public void deleteSchedule(Schedule ...schedules){
        dataRepositor.deleteSchedule(schedules);
    }

    public LiveData<List<Schedule>>allUFScheduleByTime(){
       return dataRepositor.allUFScheduleByTime();
    }
    public LiveData<List<Schedule>>allFScheduleByTime(){
        return dataRepositor.allFScheduleByTime();
    }
}