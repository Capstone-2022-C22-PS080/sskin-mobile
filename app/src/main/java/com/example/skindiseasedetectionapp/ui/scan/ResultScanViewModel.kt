package com.example.skindiseasedetectionapp.ui.scan

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.skindiseasedetectionapp.model.InUserModel
import com.example.skindiseasedetectionapp.setting.SettingDatastore

class ResultScanViewModel(private val datastore: SettingDatastore) : ViewModel() {

    fun getDatastore() : LiveData<InUserModel>{
        return datastore.readUserFromDataStore.asLiveData()
    }
}