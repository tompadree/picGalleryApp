package com.example.picgalleryapp.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.picgalleryapp.data.models.ImageUri

/**
 * @author Tomislav Curis
 */
@Database(entities = [ImageUri::class], version = 1, exportSchema = false)
abstract class PicGalleryDatabase : RoomDatabase() {
    abstract fun getPicGalleryDAO() : PicGalleryDAO
}