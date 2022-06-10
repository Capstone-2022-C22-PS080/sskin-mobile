package com.example.skindiseasedetectionapp.ui.front

import android.util.Log
import androidx.lifecycle.*
import com.example.skindiseasedetectionapp.model.InUserModel
import com.example.skindiseasedetectionapp.setting.SettingDatastore
import com.example.skindiseasedetectionapp.ui.auth.login.LoginActivity
import com.example.skindiseasedetectionapp.utill.Event
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class GetStartedViewModel(private val datastore: SettingDatastore): ViewModel() {

    private val _inUserModel: MutableLiveData<InUserModel?> = MutableLiveData<InUserModel?>()
    val inUserModel: LiveData<InUserModel?> = _inUserModel

    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var db : FirebaseFirestore = FirebaseFirestore.getInstance()



    fun getDatastore(): LiveData<InUserModel>{
        return datastore.readUserFromDataStore.asLiveData()
    }


}