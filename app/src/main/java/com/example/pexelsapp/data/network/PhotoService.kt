package com.example.pexelsapp.data.network

import com.example.pexelsapp.data.models.PhotoResponse
import com.example.pexelsapp.data.models.PhotosResponse
import com.example.pexelsapp.data.models.RequestCollectionResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface PhotoService {
    @GET("photos/{id}")
    suspend fun getPhoto(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
    ): PhotoResponse

    @GET("search")
    suspend fun getPhotos(
        @Header("Authorization") token: String,
        @Query("query") category: String,
        @Query("per_page") perPage: Int,
    ): PhotosResponse

    @GET("curated")
    suspend fun getCuratedPhotos(
        @Header("Authorization") token: String,
        @Query("per_page") perPage: Int,
    ): PhotosResponse

    @GET("collections/featured")
    suspend fun getFeaturedCollections(
        @Header("Authorization") token: String,
        @Query("per_page") perPage: Int,
    ): RequestCollectionResponse
}