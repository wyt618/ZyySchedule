package com.example.zyyschedule.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.zyyschedule.database.DataRepositor;
import com.example.zyyschedule.database.Schedule;

import java.util.List;

public class MainActivityViewModel extends AndroidViewModel {
    private final DataRepositor dataRepositor;

    public MainActivityViewModel(@NonNull Application application) {
        super(application);
        this.dataRepositor = new DataRepositor(application);
    }
    public LiveData<List<Schedule>> getALLUnFinishOfRemind(){
        return dataRepositor.getALLUnFinishOfRemind();
    }

    public void updateRemindTag(Integer ...id){
        dataRepositor.updateRemindTag(id);
    }
}
