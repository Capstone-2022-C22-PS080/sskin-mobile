package com.example.skindiseasedetectionapp.api


import com.example.skindiseasedetectionapp.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*
import java.util.*

interface ApiService {

    @Headers("Accept: application/json",
        "Content-Type: application/json")
    @POST("/auth/tokens")
    fun getToken(
        @Body uid: Uid
    ): Call<JwtToken>

    @Headers("Accept: application/json",
        "Content-Type: application/json")
    @POST("/predictions")
    fun getPrediction(
        @Header("Authorization") token: String,
        @Body predictionRequest: PredictionRequest
    ): Call<PredictionResponse>

}