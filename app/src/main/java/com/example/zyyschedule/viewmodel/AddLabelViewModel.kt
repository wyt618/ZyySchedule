package com.example.zyyschedule.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.zyyschedule.database.DataRepository
import com.example.zyyschedule.database.Label

class AddLabelViewModel(application: Application) : AndroidViewModel(application) {
    private val dataRepository: DataRepository = DataRepository(application)
    fun checkLabelTitle(title: String): LiveData<List<Label>> {
        return dataRepository.checkLabel(title)
    }

    fun insertLabel(vararg labels: Label) {
        dataRepository.insertLabel(*labels)
    }

}