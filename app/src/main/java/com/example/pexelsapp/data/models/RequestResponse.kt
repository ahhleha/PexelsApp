package com.example.pexelsapp.data.models

import com.squareup.moshi.Json

data class RequestResponse(
    @Json(name = "title") val title: String? = null
)