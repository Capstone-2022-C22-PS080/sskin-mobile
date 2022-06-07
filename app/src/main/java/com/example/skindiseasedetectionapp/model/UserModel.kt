package com.example.skindiseasedetectionapp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserModel(
    var userId : String? = null,
    var email: String? = null,
    var name: String? = null,
    var default_profile: Int? = null,
    var photo_profile: String? = null,
) : Parcelable