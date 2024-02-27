package com.example.pexelsapp.data.models

import com.squareup.moshi.Json

data class PhotoResponse(
    @Json(name = "id") val id: Int? = null,
    @Json(name = "src") val src: OriginalPhotoResponse? = null,
    @Json(name = "photographer") val photographer: String? = null,
)