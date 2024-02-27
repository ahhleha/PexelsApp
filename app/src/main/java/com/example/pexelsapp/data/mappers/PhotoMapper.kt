package com.example.pexelsapp.data.mappers

import com.example.pexelsapp.data.db.PhotoEntity
import com.example.pexelsapp.data.models.PhotoResponse
import com.example.pexelsapp.data.models.RequestResponse
import com.example.pexelsapp.domain.models.PhotoModel
import com.example.pexelsapp.domain.models.RequestModel
import javax.inject.Inject

class PhotoMapper @Inject constructor() {
    fun mapToPhotoModel(unmapped: PhotoResponse): PhotoModel = with(unmapped) {
        PhotoModel(
            id = id ?: INT_EMPTY,
            url = src?.url ?: STRING_EMPTY,
            photographer = photographer ?: STRING_EMPTY
        )
    }

    fun mapToRequestModel(unmapped: RequestResponse): RequestModel =
        RequestModel(
            title = unmapped.title ?: STRING_EMPTY
        )

    fun mapFromEntityToModel(entity: PhotoEntity): PhotoModel = with(entity) {
        PhotoModel(
            id = photoId,
            url = url,
            photographer = photographer
        )
    }

    fun mapFromModelToEntity(model: PhotoModel): PhotoEntity = with(model) {
        PhotoEntity(
            photoId = id,
            url = url,
            photographer = photographer
        )
    }

    companion object {
        private const val INT_EMPTY = 0
        private const val STRING_EMPTY = ""
    }
}