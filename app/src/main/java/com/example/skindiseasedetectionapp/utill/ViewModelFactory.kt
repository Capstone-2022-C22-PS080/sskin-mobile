package com.example.skindiseasedetectionapp.utill

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.skindiseasedetectionapp.setting.SettingDatastore
import com.example.skindiseasedetectionapp.ui.auth.login.LoginViewModel
import com.example.skindiseasedetectionapp.ui.dashboard.DashboardViewModel

class ViewModelFactory(private val setting: SettingDatastore) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(setting) as T
        }else if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
            return DashboardViewModel(setting) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)    }

}