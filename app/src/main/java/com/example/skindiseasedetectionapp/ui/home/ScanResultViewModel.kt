package com.example.skindiseasedetectionapp.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skindiseasedetectionapp.api.ApiConfig
import com.example.skindiseasedetectionapp.model.PredictionRequest
import com.example.skindiseasedetectionapp.model.PredictionResponse
import com.example.skindiseasedetectionapp.ui.home.ScanResultActivity.Companion.TAG
import com.example.skindiseasedetectionapp.utill.extToBase64
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.util.*

class ScanResultViewModel : ViewModel() {

    fun prediction(file: File){
        Log.d(TAG, "prediction: ${file.extToBase64()}")
        viewModelScope.launch {
            val base64 = file.extToBase64()
            if(base64 != null){
                val client = ApiConfig.getApiServce().getPrediction("Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1aWQiOiJhQnBmRGdDOElJTVFRbFdadGhBU3I1N1dORWcxIiwiaWF0IjoxNjU0NjY1MDQ0MDg0fQ.brbr1Cn77uVCFvKJLLdPsQsW3RIPyTUbeaS_7s8MK1c",
                    PredictionRequest(base64 = base64)
                )
                client.enqueue(object : Callback<PredictionResponse>{
                    override fun onResponse(
                        call: Call<PredictionResponse>,
                        response: Response<PredictionResponse>
                    ) {
                        if(response.body() != null && response.isSuccessful && response.code() == 200){
                            Log.d(TAG, "onResponse: berhasil upload foto ")
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