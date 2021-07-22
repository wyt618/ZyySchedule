package com.example.zyyschedule.database;

import android.content.Context;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

public class DataRepositor {
    private LabelDao labelDao;
    private ScheduleDao scheduleDao;

    public DataRepositor(Context context) {
        SchenduleDataBase schenduleDataBase = SchenduleDataBase.getDataBase(context);
        labelDao = schenduleDataBase.getLabelDao();
        scheduleDao = schenduleDataBase.getScheduleDao();
    }

    public void insertSchedule(Schedule ...schedules){
        new InsertscheduleAsyncTask(scheduleDao).execute(schedules);
    }

    public void insertLabel(Label ...labels){
        new InsertLabelAsyncTask(labelDao).execute(labels);
    }
    public LiveData<List<Label>> CheckLabel(String title){
        return labelDao.CheckLabel(title);
    }


    static class InsertscheduleAsyncTask extends AsyncTask<Schedule, Void, Void> {
        private ScheduleDao scheduleDao;

        public InsertscheduleAsyncTask(ScheduleDao scheduleDao) {
            this.scheduleDao = scheduleDao;
        }

        @Override
        protected Void doInBackground(Schedule... schedules) {
            scheduleDao.insertSchedule(schedules);
            return null;
        }
    }

    static class InsertLabelAsyncTask extends AsyncTask<Label,Void,Void>{
        private LabelDao labelDao;

        public InsertLabelAsyncTask(LabelDao labelDao) {
            this.labelDao = labelDao;
        }

        @Override
        protected Void doInBackground(Label... labels) {
            labelDao.insertLabel(labels);
            return null;
        }
    }
}
