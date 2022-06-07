package com.example.skindiseasedetectionapp.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ResponsesModel(
    @field:SerializedName("statusCode")
    var statusCode: Int? = null,
    @field:SerializedName("error")
    var error: String? = null,
    @field:SerializedName("message")
    var message: String? = null,

    ) : Parcelable

@Parcelize
data class JwtToken(
    @field:SerializedName("jwtToken")
    var jwtToken: String? = null
) : Parcelable


@Parcelize
data class Uid(
    @field:SerializedName("uid")
    var uid: String? = null
) : Parcelable


@Parcelize
data class Disease_Response(
    @field:SerializedName("id")
    var id: Int? = null,
    @field:SerializedName("disease_name")
    var disease_name: String? = null,
    @field:SerializedName("disease_description")
    var disease_description: String? = null,
    @field:SerializedName("first_aid_description")
    var first_aid_description: String? = null,
) : Parcelable


@Parcelize
data class DiseaseResponses(
    @field:SerializedName("id")
    var id: Int? = null,
    @field:SerializedName("disease_name")
    var disease_name: String? = null,
    @field:SerializedName("disease_description")
    var disease_description: String? = null,
    @field:SerializedName("first_aid_description")
    var first_aid_description: String? = null,
    @field:SerializedName("statusCode")
    var statusCode: Int? = null,
    @field:SerializedName("error")
    var error: String? = null,
    @field:SerializedName("message")
    var message: String? = null,

    ) : Parcelable