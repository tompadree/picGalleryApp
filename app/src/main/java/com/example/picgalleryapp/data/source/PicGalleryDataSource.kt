package com.example.picgalleryapp.data.source

import androidx.lifecycle.LiveData
import com.example.picgalleryapp.data.models.ImageUri
import com.example.picgalleryapp.data.models.Result

/**
 * @author Tomislav Curis
 */
interface PicGalleryDataSource {

    fun observePictures(page: Int) : LiveData<Result<List<ImageUri>>>

    suspend fun savePicture(uri: String)

    suspend fun fetchPictures(page: Int, per_page: Int) : Result<List<ImageUri>>

    suspend fun deletePics()

}