package com.example.skindiseasedetectionapp.ui.home

import android.util.Log
import androidx.lifecycle.*
import com.example.skindiseasedetectionapp.api.ApiConfig
import com.example.skindiseasedetectionapp.model.InUserModel
import com.example.skindiseasedetectionapp.model.PredictionRequest
import com.example.skindiseasedetectionapp.model.PredictionResponse
import com.example.skindiseasedetectionapp.setting.SettingDatastore
import com.example.skindiseasedetectionapp.ui.home.ScanResultActivity.Companion.TAG
import com.example.skindiseasedetectionapp.utill.extToBase64
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.util.*

class ScanResultViewModel(private val datastore: SettingDatastore) : ViewModel() {


    private val _predictionResponse: MutableLiveData<PredictionResponse?> = MutableLiveData<PredictionResponse?>()
    val predictionResponse: LiveData<PredictionResponse?> = _predictionResponse

    fun prediction(token: String,file: File){
        if(token != null && file != null){
            Log.d(TAG, "prediction: ${file.extToBase64()}")
            viewModelScope.launch {
                val base64 = file.extToBase64()
                if(base64 != null){
                    val client = ApiConfig.getApiServce().getPrediction("Bearer $token",
                        PredictionRequest(base64 = base64)
                    )
                    client.enqueue(object : Callback<PredictionResponse>{
                        override fun onResponse(
                            call: Call<PredictionResponse>,
                            response: Response<PredictionResponse>
                        ) {
                            if(response.body() != null && response.isSuccessful && response.code() == 200){

                                Log.d(TAG, "onResponse: berhasil upload foto ")
                                val body = response.body()
//                                Log.d(TAG, "onResponse: berhasil prediksi penyakit ${body?.disease_name} ${body?.id} ")
                                _predictionResponse.value = body!!
                            }else{
                                _predictionResponse.value = null
                            }
                        }

                        override fun onFailure(call: Call<PredictionResponse>, t: Throwable) {
                            _predictionResponse.value = null
                            t.printStackTrace()
                        }

                    })
                }

            }

        }
    }


    fun getDataStore(): LiveData<InUserModel> {
        return datastore.readUserFromDataStore.asLiveData(viewModelScope.coroutineContext)
    }



}