package com.example.zyyschedule.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.zyyschedule.database.DataRepository
import com.example.zyyschedule.database.Label
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

class AddLabelViewModel(application: Application) : AndroidViewModel(application) {
    private val dataRepository: DataRepository = DataRepository(application)
    fun checkLabelTitle(title: String): LiveData<List<Label>>? {
        var checkLabel: LiveData<List<Label>>? = null
        viewModelScope.launch {
            try {
            checkLabel  =    dataRepository.checkLabel(title)
            }catch (e:Exception){
                Log.i("addLabel","检查标签失败：$e")
            }
        }
        return checkLabel
    }

    fun insertLabel(vararg labels: Label) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                dataRepository.insertLabel(*labels)
            }catch (e:Exception){
                Log.i("addLabel","插入标签失败：$e")
            }
        }

    }



}