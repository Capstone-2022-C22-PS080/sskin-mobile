package com.example.skindiseasedetectionapp.ui.scan

import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.*
import com.example.skindiseasedetectionapp.api.ApiConfig
import com.example.skindiseasedetectionapp.model.*
import com.example.skindiseasedetectionapp.setting.SettingDatastore
import com.example.skindiseasedetectionapp.ui.home.ScanResultActivity
import com.example.skindiseasedetectionapp.utill.extToBase64
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import okhttp3.internal.cookieToString
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
        mStorageRef.child("detection_user/${inUserModel.userId}/defProf${inUserModel.default_profile}-${predictionResponse.id}-$formattedDate")
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

        val collection = db.collection("detection_histories")

        if(predictionResponse != null && user != null){
            val detection = DetectionHistories(
                uri, predictionResponse.id,user.default_profile,user.userId,
                Timestamp(today),
    1,predictionResponse.disease_name,predictionResponse.disease_description,predictionResponse.first_aid_description
            )
            collection.document(detection.userId.toString())
                .get()
                .addOnSuccessListener { doc->
                    val counter = doc.get("result_detection")
                    if(doc != null && doc.data != null){
                        if(!doc.exists()){
                           collection.document(detection.userId!!)
                               .set(ResultDetection(
                                   arrayOf(detection).asList()
                               ))
                               .addOnSuccessListener {
                                    _success.value = true
                                   _predictionResponse.value = predictionResponse
                               }
                               .addOnFailureListener {
                                   it.printStackTrace()
                                   _success.value = true
                                   _predictionResponse.value = null
                               }
                        }else{
                            collection.document(detection.userId!!)
                                .update("result_detection",FieldValue.arrayUnion(detection))
                                .addOnSuccessListener {
                                     _success.value = true
                                    _predictionResponse.value = predictionResponse
                                }
                                .addOnFailureListener {
                                    it.printStackTrace()
                                    _success.value = true
                                    _predictionResponse.value = null
                                }
                        }

                    }

                }
                .addOnFailureListener {
                    _success.value = true
                }

        }

    }


    fun getDataStore(): LiveData<InUserModel> {
        return datastore.readUserFromDataStore.asLiveData(viewModelScope.coroutineContext)
    }


}