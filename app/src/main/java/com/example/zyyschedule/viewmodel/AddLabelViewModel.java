package com.example.zyyschedule.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.zyyschedule.database.DataRepositor;
import com.example.zyyschedule.database.Label;

import java.util.List;

public class AddLabelViewModel extends AndroidViewModel {
    private DataRepositor dataRepositor;
    public AddLabelViewModel(@NonNull Application application) {
        super(application);
        dataRepositor = new DataRepositor(application);
    }
    public LiveData<List<Label>> checkLabelTitle(String title){
        return dataRepositor.checkLabel(title);
    }

    public void insertLabel(Label ...labels){
        dataRepositor.insertLabel(labels);
    }


}
