package com.example.pexelsapp.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PhotosDao {
    @Query("SELECT * FROM photos_table")
    fun getAllPhotos(): List<PhotoEntity>

    @Query("SELECT * FROM photos_table WHERE photo_id = :photoId")
    fun getPhoto(photoId: Int): PhotoEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPhoto(photos: PhotoEntity)

    @Query("DELETE FROM photos_table WHERE photo_id = :photoId")
    fun deletePhoto(photoId: Int)
}