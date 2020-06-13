package com.example.picgalleryapp.data.source

import androidx.lifecycle.LiveData
import com.example.picgalleryapp.data.models.ImageUri
import com.example.picgalleryapp.data.models.Result
import java.lang.Exception

/**
 * @author Tomislav Curis
 */
class FakeDataSource(
    var images: MutableList<ImageUri>? = mutableListOf()
) : PicGalleryDataSource {

    override fun observePictures(page: Int): LiveData<Result<List<ImageUri>>> {
        TODO("Not yet implemented")
    }

    override suspend fun savePicture(uri: String) {
        images?.add(ImageUri(uri))
    }

    override suspend fun fetchPictures(page: Int, per_page: Int): Result<List<ImageUri>> {
        images?.let { return Result.Success(ArrayList(it)) }
        return Result.Error(Exception("Images not found"))
    }

    override suspend fun deletePics() {
        images?.clear()
    }
}