package com.example.pexelsapp.domain.usecases

import com.example.pexelsapp.domain.Repository
import com.example.pexelsapp.domain.models.PhotoModel
import com.example.pexelsapp.domain.models.RequestModel
import javax.inject.Inject

class GetPhotos @Inject constructor(
    private val repository: Repository,
) {
    open suspend fun getPopularPhotos(): List<PhotoModel> = repository.getCuratedPhotos()

    open suspend fun getPhotos(query: String): List<PhotoModel> = repository.getPhotos(query)

    open suspend fun getPopularRequests(): List<RequestModel> = repository.getPopularRequests()

    open suspend fun getPhoto(id: Int): PhotoModel? = repository.getPhoto(id)

    open suspend fun getPhotosFromDb(): List<PhotoModel> = repository.getPhotosFromDB()

    open suspend fun getPhotoFromDb(photoId: Int): PhotoModel? = repository.getPhotoFromDB(photoId)

    open suspend fun insertPhotoInDB(photo: PhotoModel) = repository.insertPhotoInDB(photo)

    suspend fun deletePhotoFromDB(photoId: Int) = repository.deletePhotoFromDB(photoId)
}