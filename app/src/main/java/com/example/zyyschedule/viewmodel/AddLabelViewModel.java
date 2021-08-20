package com.example.zyyschedule.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.zyyschedule.database.DataRepository;
import com.example.zyyschedule.database.Label;

import java.util.List;

public class AddLabelViewModel extends AndroidViewModel {
    private final DataRepository dataRepository;
    public AddLabelViewModel(@NonNull Application application) {
        super(application);
        dataRepository = new DataRepository(application);
    }
    public LiveData<List<Label>> checkLabelTitle(String title){
        return dataRepository.checkLabel(title);
    }

    public void insertLabel(Label ...labels){
        dataRepository.insertLabel(labels);
    }


}
