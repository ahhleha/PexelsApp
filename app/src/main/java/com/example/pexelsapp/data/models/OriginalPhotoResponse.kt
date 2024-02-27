package com.example.pexelsapp.data.models

import com.squareup.moshi.Json

data class OriginalPhotoResponse(
    @Json(name = "original") val url: String? = null,
)