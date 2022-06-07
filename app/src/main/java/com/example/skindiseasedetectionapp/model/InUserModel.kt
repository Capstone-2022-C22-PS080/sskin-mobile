package com.example.skindiseasedetectionapp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class InUserModel(
    var userId : String? = null,
    var email: String? = null,
    var name: String? = null,
    var default_profile: Int? = null,
    var photo_profile: String? = null,
    var jwtToken: String? = null,
    var profiles : List<ProfilesUser>? = null
) : Parcelable