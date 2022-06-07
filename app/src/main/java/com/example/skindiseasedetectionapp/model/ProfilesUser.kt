package com.example.skindiseasedetectionapp.model

import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class ProfilesUser(
    var fotoUrl: String? = null,
    var id : Int? = null,
    var name: String?= null,
    var created_at : Timestamp?= null
): Parcelable