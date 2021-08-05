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

    public void ChangeStateSchedule(Schedule ...schedules){
        new ChangeStateScheduleAsyncTask(scheduleDao).execute(schedules);
    }

    public void insertSchedule(Schedule ...schedules){
        new InsertscheduleAsyncTask(scheduleDao).execute(schedules);
    }
    public void deleteSchedule(Schedule ...schedules){
        new DeleteScheduleAsyncTask(scheduleDao).execute(schedules);
    }

    public void deleteLabel(Label ...labels){
        new DeleteLabelAsyncTask(labelDao,scheduleDao).execute(labels);
    }

    public void insertLabel(Label ...labels){
        new InsertLabelAsyncTask(labelDao).execute(labels);
    }

    public LiveData<List<String>> getScheduleDayOfTag(){
        return scheduleDao.getScheduleDayOfTag();
    }

    public LiveData<List<Schedule>>getUnfinishedScheduleOfDay(String day){
        return scheduleDao.getUnfinishedScheduleOfDay(day);
    }
    public LiveData<List<Schedule>>getFinishedScheduleOfDay(String day){
        return scheduleDao.getFinishedScheduleOfDay(day);
    }

    public LiveData<List<Label>> checkLabel(String title){
        return labelDao.checkLabel(title);
    }

    public LiveData<List<Label>> getAllLabel(){
        return labelDao.getAllLabel();
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

    static class ChangeStateScheduleAsyncTask extends AsyncTask<Schedule,Void,Void>{
        private final ScheduleDao scheduleDao;

        public ChangeStateScheduleAsyncTask(ScheduleDao scheduleDao) {
            this.scheduleDao = scheduleDao;
        }

        @Override
        protected Void doInBackground(Schedule... schedules) {
            scheduleDao.ChangeStateSchedule(schedules);
            return null;
        }
    }

    static class DeleteLabelAsyncTask extends AsyncTask<Label,Void,Void>{
        private LabelDao labelDao;
        private ScheduleDao scheduleDao;

        public DeleteLabelAsyncTask(LabelDao labelDao,ScheduleDao scheduleDao) {
            this.labelDao = labelDao;
            this.scheduleDao = scheduleDao;
        }

        @Override
        protected Void doInBackground(Label... labels) {
            labelDao.deleteLabel(labels);
            for(int i=0;i<labels.length;i++){
               scheduleDao.updateScheduleLabel(labels[i].getId());
            }
            return null;
        }
    }
    static class DeleteScheduleAsyncTask extends AsyncTask<Schedule,Void,Void>{
        private ScheduleDao scheduleDao;

        public DeleteScheduleAsyncTask(ScheduleDao scheduleDao) {
            this.scheduleDao = scheduleDao;
        }

        @Override
        protected Void doInBackground(Schedule... schedules) {
            scheduleDao.deleteSchedule(schedules);
            return null;
        }
    }
}
