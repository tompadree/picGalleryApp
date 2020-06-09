package com.example.picgalleryapp.data.source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.picgalleryapp.data.models.ImageUri

/**
 * @author Tomislav Curis
 */
@Dao
interface PicGalleryDAO {

    /**
     * Delete all repos.
     */
    @Query("DELETE FROM uris")
    suspend fun deletePictures()

    /**
     * Save image uri.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun savePic(picture: ImageUri)

}