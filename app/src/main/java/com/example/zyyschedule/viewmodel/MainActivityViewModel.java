package com.example.zyyschedule.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.zyyschedule.database.DataRepository;
import com.example.zyyschedule.database.Schedule;

import java.util.List;

public class MainActivityViewModel extends AndroidViewModel {
    private final DataRepository dataRepository;

    public MainActivityViewModel(@NonNull Application application) {
        super(application);
        this.dataRepository = new DataRepository(application);
    }
    public LiveData<List<Schedule>> getALLUnFinishOfRemind(){
        return dataRepository.getALLUnFinishOfRemind();
    }

    public void updateRemindTag(Integer ...id){
        dataRepository.updateRemindTag(id);
    }
}
