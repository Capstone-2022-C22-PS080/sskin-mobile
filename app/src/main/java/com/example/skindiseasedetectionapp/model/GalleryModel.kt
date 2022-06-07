package com.example.skindiseasedetectionapp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GalleryModel(
    var name: String = "",
    var date: String = "",
    var path: String = ""
) : Parcelable