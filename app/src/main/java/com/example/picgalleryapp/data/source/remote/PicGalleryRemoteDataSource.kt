package com.example.picgalleryapp.data.source.remote

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.picgalleryapp.data.models.HepoImage
import com.example.picgalleryapp.data.models.ImageUri
import com.example.picgalleryapp.data.models.Result
import com.example.picgalleryapp.data.source.PicGalleryDataSource
import com.example.picgalleryapp.data.source.remote.api.HerokuappAPI
import java.io.IOException

/**
 * @author Tomislav Curis
 */
class PicGalleryRemoteDataSource(private val herokuappAPI: HerokuappAPI) : PicGalleryDataSource {

    private val observaleImages = MutableLiveData<Result<List<HepoImage>>>()

    override fun observePictures(page: Int): LiveData<Result<List<ImageUri>>> {
        TODO("Not yet implemented")
    }

    override suspend fun savePicture(uri: String) {
        TODO("Not yet implemented")
    }

    override suspend fun fetchPictures(page: Int, per_page: Int): Result<List<ImageUri>> {
        TODO("Not yet implemented")
    }

    override suspend fun fetchAllPictures(user_id: String): Result<List<HepoImage>> {
        val response = herokuappAPI.getImages(user_id)

        if(response.isSuccessful) {
            val body = response.body()
            if(body != null) {
                val result = Result.Success(body)
                observaleImages.value = result
                return result
            }
        }
        return Result.Error(IOException("There is an error: " + response.message()))
    }

    override suspend fun deletePics() {
        TODO("Not yet implemented")
    }
}