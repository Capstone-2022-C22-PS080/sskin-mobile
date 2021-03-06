package com.example.skindiseasedetectionapp.model

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize

@Parcelize
data class DetectionHistories(
    var photoLocation: String? = null,
    var disease_id : Int? = null,
    var profile_id : Int? = null,
    var userId: String?= null,
    var takenPhoto : Timestamp?= null,
    var id_detection : Int?= null,
    var disease_name: String? = null,
    var disease_description: String? = null,
    var first_aid_description: String? = null
): Parcelable
