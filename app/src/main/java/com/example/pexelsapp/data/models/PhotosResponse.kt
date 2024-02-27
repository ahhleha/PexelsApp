package com.example.pexelsapp.data.models

import com.squareup.moshi.Json

data class PhotosResponse(
    @Json(name = "photos") val photos: List<PhotoResponse>? = null,
)
