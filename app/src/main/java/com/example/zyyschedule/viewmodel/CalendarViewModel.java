package com.example.zyyschedule.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModel;

import java.util.Calendar;

public class CalendarViewModel extends AndroidViewModel {
    private int day ;

    public CalendarViewModel(@NonNull Application application) {
        super(application);
        Calendar calendar=Calendar.getInstance();
        day = calendar.get(Calendar.DAY_OF_MONTH);
    }



    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }
}