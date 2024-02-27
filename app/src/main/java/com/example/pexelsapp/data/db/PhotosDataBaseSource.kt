package com.example.pexelsapp.data.db

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PhotosDataBaseSource @Inject constructor(
    private val photosDao: PhotosDao,
) {
    suspend fun getAllPhotos() = withContext(Dispatchers.IO) {
        photosDao.getAllPhotos()
    }

    suspend fun getPhoto(photoId: Int) = withContext(Dispatchers.IO) {
        photosDao.getPhoto(photoId)
    }

    suspend fun insertPhoto(photo: PhotoEntity) = withContext(Dispatchers.IO) {
        photosDao.insertPhoto(photo)
    }

    suspend fun deletePhoto(photoId: Int) = withContext(Dispatchers.IO) {
        photosDao.deletePhoto(photoId)
    }
}