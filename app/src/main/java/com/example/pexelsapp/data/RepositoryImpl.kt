package com.example.pexelsapp.data

import com.example.pexelsapp.data.db.PhotosDataBaseSource
import com.example.pexelsapp.data.mappers.PhotoMapper
import com.example.pexelsapp.data.network.PhotoService
import com.example.pexelsapp.di.Cached
import com.example.pexelsapp.di.NonCached
import com.example.pexelsapp.domain.Repository
import com.example.pexelsapp.domain.models.PhotoModel
import com.example.pexelsapp.domain.models.RequestModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RepositoryImpl @Inject constructor(
    @Cached private val cachedService: PhotoService,
    @NonCached private val nonCachedService: PhotoService,
    private val mapper: PhotoMapper,
    private val dataBaseSource: PhotosDataBaseSource,
) : Repository {
    override suspend fun getCuratedPhotos(): List<PhotoModel> {
        return withContext(Dispatchers.IO) {
            val response = cachedService.getCuratedPhotos(TOKEN, PER_PAGE_PHOTOS)
            val result = response.photos?.map {
                mapper.mapToPhotoModel(it)
            }
            result ?: listOf()
        }
    }

    override suspend fun getPhotos(query: String): List<PhotoModel> {
        return withContext(Dispatchers.IO) {
            val response = nonCachedService.getPhotos(TOKEN, query, PER_PAGE_PHOTOS)
            val result = response.photos?.map {
                mapper.mapToPhotoModel(it)
            }
            result ?: listOf()
        }
    }

    override suspend fun getPopularRequests(): List<RequestModel> {
        return withContext(Dispatchers.IO) {
            val response = cachedService.getFeaturedCollections(TOKEN, PER_PAGE_REQUESTS)
            val result = response.collections?.map {
                mapper.mapToRequestModel(it)
            }
            result ?: listOf()
        }
    }

    override suspend fun getPhoto(id: Int): PhotoModel {
        return withContext(Dispatchers.IO) {
            val response = nonCachedService.getPhoto(TOKEN, id)
            mapper.mapToPhotoModel(response)
        }
    }

    override suspend fun getPhotosFromDB(): List<PhotoModel> {
        return withContext(Dispatchers.IO) {
            val photos = dataBaseSource.getAllPhotos()
            photos.map { photoEntity ->
                mapper.mapFromEntityToModel(photoEntity)
            }
        }
    }

    override suspend fun getPhotoFromDB(photoId: Int): PhotoModel? {
        return withContext(Dispatchers.IO) {
            val photo = dataBaseSource.getPhoto(photoId)
            if (photo != null) mapper.mapFromEntityToModel(photo)
            else null
        }
    }

    override suspend fun insertPhotoInDB(photo: PhotoModel) {
        withContext(Dispatchers.IO) {
            dataBaseSource.insertPhoto(mapper.mapFromModelToEntity(photo))
        }
    }

    override suspend fun deletePhotoFromDB(photoId: Int) {
        withContext(Dispatchers.IO) {
            dataBaseSource.deletePhoto(photoId)
        }
    }

    companion object {
        private const val TOKEN = "LXGoL9kSNRQ5DVYWwusvsuMBrnQcc5kVswcBhm5kfxdKcBuVyDSdMxVA"
        private const val PER_PAGE_PHOTOS = 30
        private const val PER_PAGE_REQUESTS = 7
    }
}