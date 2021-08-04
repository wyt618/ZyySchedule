package com.example.zyyschedule.viewmodel;

import android.app.AlertDialog;
import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;


import com.example.zyyschedule.database.DataRepositor;
import com.example.zyyschedule.database.Label;

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
}