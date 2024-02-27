package com.example.pexelsapp.domain

import com.example.pexelsapp.domain.models.PhotoModel
import com.example.pexelsapp.domain.models.RequestModel

interface Repository {
    suspend fun getCuratedPhotos(): List<PhotoModel>

    suspend fun getPhotos(query: String): List<PhotoModel>

    suspend fun getPopularRequests(): List<RequestModel>

    suspend fun getPhoto(id: Int): PhotoModel

    suspend fun getPhotosFromDB(): List<PhotoModel>

    suspend fun getPhotoFromDB(photoId: Int): PhotoModel?

    suspend fun insertPhotoInDB(photo: PhotoModel)

    suspend fun deletePhotoFromDB(photoId: Int)
}