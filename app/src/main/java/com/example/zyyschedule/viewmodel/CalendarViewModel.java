package com.example.zyyschedule.viewmodel;

import androidx.lifecycle.ViewModel;

import java.util.Calendar;

public class CalendarViewModel extends ViewModel {
    private int day ;

    public CalendarViewModel() {
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