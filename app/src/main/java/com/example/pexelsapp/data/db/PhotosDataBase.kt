package com.example.pexelsapp.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [PhotoEntity::class],
    version = 1
)
abstract class PhotosDataBase : RoomDatabase() {
    abstract fun photosDao(): PhotosDao
}