package com.example.picgalleryapp.data.source

import androidx.lifecycle.LiveData
import com.example.picgalleryapp.data.models.ImageUri
import com.example.picgalleryapp.data.models.Result

/**
 * @author Tomislav Curis
 */
interface PicGalleryRepository {

    fun observePictures(page: Int) : LiveData<Result<List<ImageUri>>>

    suspend fun savePicture(uri: String)

    suspend fun fetchPictures(update: Boolean = false, page: Int, per_page: Int) : Result<List<ImageUri>>

    suspend fun deletePics()
}