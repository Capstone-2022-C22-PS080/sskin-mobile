package com.example.skindiseasedetectionapp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DetectionHistories(
    var photoLocation: String? = null,
    var disease_id : Int? = null,
    var profile_id : Int? = null,
    var userId: String?= null
): Parcelable