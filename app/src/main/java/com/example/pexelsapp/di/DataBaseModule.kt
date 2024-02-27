package com.example.pexelsapp.di

import android.content.Context
import androidx.room.Room
import com.example.pexelsapp.data.db.PhotosDao
import com.example.pexelsapp.data.db.PhotosDataBase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class DataBaseModule {
    @Provides
    fun provideFoodDataBase(@ApplicationContext context: Context): PhotosDataBase {
        return Room.databaseBuilder(context, PhotosDataBase::class.java, PHOTOS_DB_NAME)
            .build()
    }

    @Provides
    fun providePhotosDao(db: PhotosDataBase): PhotosDao = db.photosDao()

    companion object {
        const val PHOTOS_DB_NAME = "photos"
    }
}