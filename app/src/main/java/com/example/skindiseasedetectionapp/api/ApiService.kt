package com.example.skindiseasedetectionapp.api


import com.example.skindiseasedetectionapp.model.DiseaseResponses
import com.example.skindiseasedetectionapp.model.JwtToken
import com.example.skindiseasedetectionapp.model.Uid
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

    @FormUrlEncoded
    @POST("/predictions")
    fun getPrediction(
        @Header("Content-type") type: String = "application/json",
        @Field("base64") base64: String
    ): Call<DiseaseResponses>

}