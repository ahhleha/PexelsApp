package com.example.pexelsapp.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "photos_table")
data class PhotoEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo("photo_id") val photoId: Int,
    @ColumnInfo("photographer") val photographer: String,
    @ColumnInfo("url") val url: String,
)