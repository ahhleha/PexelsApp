package com.example.pexelsapp.ui

import android.accounts.NetworkErrorException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pexelsapp.domain.models.PhotoModel
import com.example.pexelsapp.domain.models.RequestModel
import com.example.pexelsapp.domain.usecases.GetPhotos
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.net.UnknownHostException
import javax.inject.Inject

@HiltViewModel
class PhotosViewModel @Inject constructor(
    private val getPhotos: GetPhotos,
) : ViewModel() {

    private val _photoFlow = MutableStateFlow<PhotoModel?>(null)
    val photoFlow: StateFlow<PhotoModel?> = _photoFlow

    private val _photosFlow = MutableStateFlow<List<PhotoModel>>(emptyList())
    val photosFlow: StateFlow<List<PhotoModel>> = _photosFlow

    private val _requestsFlow = MutableStateFlow<List<RequestModel>>(emptyList())
    val requestsFlow: StateFlow<List<RequestModel>> = _requestsFlow

    private val _photosLoadingFlow = MutableStateFlow(false)
    val photosLoadingFlow: StateFlow<Boolean> = _photosLoadingFlow

    private val _photosLoadingProgressFlow = MutableStateFlow(ZERO)
    val photosLoadingProgressFlow: StateFlow<Int> = _photosLoadingProgressFlow

    private val _photoInDBFlow = MutableStateFlow(false)
    val photoInDBFlow: StateFlow<Boolean> = _photoInDBFlow

    private val _networkErrorFlow = MutableStateFlow(false)
    val networkErrorFlow: StateFlow<Boolean> = _networkErrorFlow

    private var currentQuery = POPULAR_PHOTOS

    private val handler = CoroutineExceptionHandler { _, throwable: Throwable ->
        viewModelScope.launch {
            if (throwable is NetworkErrorException || throwable is UnknownHostException) {
                _networkErrorFlow.emit(true)
            }
        }
    }

    fun getPopularPhotos() {
        viewModelScope.launch(handler) {
            _networkErrorFlow.emit(false)
            _photosLoadingFlow.emit(true)

            val loadingProgressJob = launch {
                for (i in START_PROGRESS_VALUE..END_PROGRESS_VALUE) {
                    delay(DELAY_TIME)
                    _photosLoadingProgressFlow.emit(i)
                }
            }

            try {
                _photosFlow.emit(getPhotos.getPopularPhotos())
            } finally {
                loadingProgressJob.cancel()
            }

            _photosLoadingFlow.emit(false)
        }
    }

    fun getPhotos(query: String) {
        viewModelScope.launch(handler) {
            _networkErrorFlow.emit(false)
            _photosLoadingFlow.emit(true)

            val loadingProgressJob = launch {
                for (i in START_PROGRESS_VALUE..END_PROGRESS_VALUE) {
                    delay(DELAY_TIME)
                    _photosLoadingProgressFlow.emit(i)
                }
            }

            try {
                _photosFlow.emit(getPhotos.getPhotos(query))
            } finally {
                loadingProgressJob.cancel()
            }

            _photosLoadingFlow.emit(false)
        }
    }


    fun getPopularRequests() {
        viewModelScope.launch(handler) {
            _networkErrorFlow.emit(false)
            _requestsFlow.emit(getPhotos.getPopularRequests())
        }
    }

    fun getPhoto(id: Int) {
        viewModelScope.launch(handler) {
            _networkErrorFlow.emit(false)
            _photosLoadingFlow.emit(true)

            val loadingProgressJob = launch {
                for (i in START_PROGRESS_VALUE..END_PROGRESS_VALUE) {
                    delay(DELAY_TIME)
                    _photosLoadingProgressFlow.emit(i)
                }
            }

            try {
                _photoFlow.emit(getPhotos.getPhoto(id))
            } finally {
                loadingProgressJob.cancel()
            }

            _photosLoadingFlow.emit(false)
        }
    }

    fun getPhotosFromDB() {
        viewModelScope.launch(handler) {
            _photosLoadingFlow.emit(true)

            val loadingProgressJob = launch {
                for (i in START_PROGRESS_VALUE..END_PROGRESS_VALUE) {
                    delay(DELAY_TIME)
                    _photosLoadingProgressFlow.emit(i)
                }
            }

            try {
                _photosFlow.emit(getPhotos.getPhotosFromDb())
            } finally {
                loadingProgressJob.cancel()
            }

            _photosLoadingFlow.emit(false)
        }
    }

    fun getPhotoFromDB(photoId: Int) {
        viewModelScope.launch(handler) {
            _photosLoadingFlow.emit(true)

            val loadingProgressJob = launch {
                for (i in START_PROGRESS_VALUE..END_PROGRESS_VALUE) {
                    delay(DELAY_TIME)
                    _photosLoadingProgressFlow.emit(i)
                }
            }

            try {
                _photoFlow.emit(getPhotos.getPhotoFromDb(photoId))
            } finally {
                loadingProgressJob.cancel()
            }

            _photosLoadingFlow.emit(false)
        }
    }

    fun checkPhotoInDB(photoId: Int) {
        viewModelScope.launch(handler) {
            val photo = getPhotos.getPhotoFromDb(photoId)
            _photoInDBFlow.emit(photo != null)
        }
    }

    fun insertPhotosInDB(photo: PhotoModel) {
        viewModelScope.launch(handler) {
            getPhotos.insertPhotoInDB(photo)
        }
    }

    fun deletePhotoFromDB(photoId: Int) {
        viewModelScope.launch(handler) {
            getPhotos.deletePhotoFromDB(photoId)
        }
    }

    fun getCurrentQuery() = currentQuery

    fun setCurrentQuery(query: String) {
        currentQuery = query
    }

    companion object {
        private const val POPULAR_PHOTOS = "popular_photos"
        private const val ZERO = 0
        private const val DELAY_TIME = 30L
        private const val START_PROGRESS_VALUE = 1
        private const val END_PROGRESS_VALUE = 100
    }
}