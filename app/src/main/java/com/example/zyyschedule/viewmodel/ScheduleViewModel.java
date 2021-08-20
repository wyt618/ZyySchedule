package com.example.zyyschedule.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.zyyschedule.database.DataRepository;
import com.example.zyyschedule.database.Label;
import com.example.zyyschedule.database.Schedule;

import java.util.List;

public class ScheduleViewModel extends AndroidViewModel {
    private final DataRepository dataRepository;



    public ScheduleViewModel(@NonNull Application application) {
        super(application);
        dataRepository = new DataRepository(application);

    }
    public LiveData<List<Label>> getAllLabel(){
        return dataRepository.getAllLabel();
    }

    public void deleteLabel(Label ...labels){
        dataRepository.deleteLabel(labels);
    }
    public LiveData<List<Schedule>>getUnfinishedScheduleOfDay(String day){
        return dataRepository.getUnfinishedScheduleOfDay(day);
    }
    public LiveData<List<Schedule>>getFinishedScheduleOfDay(String day){
        return dataRepository.getFinishedScheduleOfDay(day);
    }
    public void deleteSchedule(Schedule ...schedules){
        dataRepository.deleteSchedule(schedules);
    }

    public LiveData<List<Schedule>>allUFScheduleByTime(){
       return dataRepository.allUFScheduleByTime();
    }
    public LiveData<List<Schedule>>allFScheduleByTime(){
        return dataRepository.allFScheduleByTime();
    }

    public LiveData<List<Schedule>>getFScheduleOfLabel(int labelid){
        return dataRepository.getFScheduleOfLabel(labelid);
    }
    public LiveData<List<Schedule>>getUFScheduleOfLabel(int labelid){
        return dataRepository.getUFScheduleOfLabel(labelid);
    }

}