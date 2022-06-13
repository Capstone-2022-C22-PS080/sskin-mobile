package com.example.skindiseasedetectionapp.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.skindiseasedetectionapp.model.DetectionHistories
import com.example.skindiseasedetectionapp.model.InUserModel
import com.example.skindiseasedetectionapp.model.ResultDetection
import com.example.skindiseasedetectionapp.setting.SettingDatastore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class ScanHistoriesVieModel(private val datastore: SettingDatastore) : ViewModel(){

    private val _detectionHistories: MutableLiveData<List<DetectionHistories?>> = MutableLiveData<List<DetectionHistories?>>()
    val detectionHistories: MutableLiveData<List<DetectionHistories?>> = _detectionHistories

    private val _loading: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    val loading : LiveData<Boolean> = _loading

    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var db : FirebaseFirestore = FirebaseFirestore.getInstance()
    private var storage : FirebaseStorage = FirebaseStorage.getInstance()

    fun getData(docId: String){
        _loading.value = true
        db.collection("detection_histories").document(docId)
            .get()
            .addOnFailureListener {
                _detectionHistories.value = listOf()
                _loading.value = false
            }
            .addOnSuccessListener {
                if(it != null){
                    //val map = HashMap<String,List<DetectionHistories>>(it.data)
                    val list = it.toObject(ResultDetection::class.java)
                    if(list != null ){
                        if(list.result_detection != null){
                            _detectionHistories.value = list.result_detection!!
                        }else{
                            _detectionHistories.value = listOf()
                        }
                    }
                    Log.d("TAG", "getData: ${list}")
                   // _detectionHistories.value = list
                }
                _loading.value = false

            }
    }

    fun getDatastore() : LiveData<InUserModel>{
        return datastore.readUserFromDataStore.asLiveData()
    }




}