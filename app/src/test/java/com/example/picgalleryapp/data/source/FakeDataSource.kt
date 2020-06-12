package com.example.picgalleryapp.data.source

import androidx.lifecycle.LiveData
import com.example.picgalleryapp.data.models.ImageUri
import com.example.picgalleryapp.data.models.Result

/**
 * @author Tomislav Curis
 */
class FakeDataSource : PicGalleryDataSource {

    override fun observePictures(page: Int): LiveData<Result<List<ImageUri>>> {
        TODO("Not yet implemented")
    }

    override suspend fun savePicture(uri: String) {
        TODO("Not yet implemented")
    }

    override suspend fun fetchPictures(page: Int, per_page: Int): Result<List<ImageUri>> {
        TODO("Not yet implemented")
    }

    override suspend fun deletePics() {
        TODO("Not yet implemented")
    }
}