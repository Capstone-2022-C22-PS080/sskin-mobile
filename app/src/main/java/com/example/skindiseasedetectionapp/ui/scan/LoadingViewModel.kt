package com.example.skindiseasedetectionapp.ui.scan

import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.*
import com.example.skindiseasedetectionapp.api.ApiConfig
import com.example.skindiseasedetectionapp.model.DetectionHistories
import com.example.skindiseasedetectionapp.model.InUserModel
import com.example.skindiseasedetectionapp.model.PredictionRequest
import com.example.skindiseasedetectionapp.model.PredictionResponse
import com.example.skindiseasedetectionapp.setting.SettingDatastore
import com.example.skindiseasedetectionapp.ui.home.ScanResultActivity
import com.example.skindiseasedetectionapp.utill.extToBase64
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class LoadingViewModel(private val datastore: SettingDatastore) : ViewModel() {

    private val _predictionResponse: MutableLiveData<PredictionResponse?> = MutableLiveData<PredictionResponse?>()
    val predictionResponse: LiveData<PredictionResponse?> = _predictionResponse

    private val _success: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    val success : LiveData<Boolean> = _success

    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var db : FirebaseFirestore = FirebaseFirestore.getInstance()
    private var storage : FirebaseStorage = FirebaseStorage.getInstance()


    fun prediction(user: InUserModel?,file: File){
        if(user != null && file != null){
            _success.value = false
            Log.d(ScanResultActivity.TAG, "prediction: ${file.extToBase64()}")
            viewModelScope.launch {
                val base64 = file.extToBase64()

                if(base64 != null){
                    val client = ApiConfig.getApiServce().getPrediction("Bearer ${user.jwtToken}",
                        PredictionRequest(base64 = base64)
                    )
                    client.enqueue(object : Callback<PredictionResponse> {
                        override fun onResponse(
                            call: Call<PredictionResponse>,
                            response: Response<PredictionResponse>
                        ) {
                            if(response.body() != null && response.isSuccessful && response.code() == 200){
                                Log.d(ScanResultActivity.TAG, "onResponse: berhasil upload foto ")
                                val body = response.body()
                                _predictionResponse.value = body
                                if (body != null) {
                                    uploadToFireStore(user, body,file.toUri())
                                }
                                Log.d(ScanResultActivity.TAG, "onResponse: berhasil prediksi penyakit ${body?.disease_name} ")
                            }else{

                            }
                        }

                        override fun onFailure(call: Call<PredictionResponse>, t: Throwable) {
                            t.printStackTrace()
                        }

                    })
                }

            }

        }
    }

    private fun uploadToFireStore(inUserModel: InUserModel,predictionResponse: PredictionResponse,uri: Uri){
        val sdf = SimpleDateFormat("ddMMMyyyy HH:mm:ss")
        val today = Date()
        val formattedDate = sdf.format(today)
        val mStorageRef = storage.reference
        mStorageRef.child("detection_user/${inUserModel.userId}/${inUserModel.default_profile}/${predictionResponse.id}-$formattedDate.${uri.lastPathSegment}")
            .putFile(uri)
            .addOnSuccessListener { uploadTask ->
                Log.d(LoadingScanFragment.TAG,"berhasil upload")
                val downloadUrl = uploadTask.storage.downloadUrl
                    .addOnSuccessListener { theUri->
                        Log.d(LoadingScanFragment.TAG,"Berhasil ambil download URL")
                        insertToDetectionDb(theUri.toString(),predictionResponse,inUserModel,today)
                    }.addOnFailureListener { exUri ->
                        exUri.printStackTrace()
                    }

                }
            .addOnFailureListener{ exUpload->
                exUpload.printStackTrace()
            }

    }

    private fun insertToDetectionDb(uri: String,predictionResponse: PredictionResponse, user: InUserModel,today: Date){

        if(predictionResponse != null && user != null){
            val detection = DetectionHistories(
                uri, predictionResponse.id,user.default_profile,user.userId,
                Timestamp(today)
            )
            db.collection("detection_histories").document(detection.userId!!)
                .set(detection)
                .addOnSuccessListener {
                    _success.value = true
                    Log.d(LoadingScanFragment.TAG, "insertToDetectionDb: ")
                    _predictionResponse.value = predictionResponse

                }.addOnFailureListener {  ex->
                    _success.value = false
                    ex.printStackTrace()
                    _predictionResponse.value = null
                }
        }

    }


    fun getDataStore(): LiveData<InUserModel> {
        return datastore.readUserFromDataStore.asLiveData(viewModelScope.coroutineContext)
    }


}