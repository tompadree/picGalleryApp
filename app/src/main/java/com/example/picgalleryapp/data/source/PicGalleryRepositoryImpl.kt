package com.example.picgalleryapp.data.source

import androidx.lifecycle.LiveData
import com.example.picgalleryapp.data.models.ImageUri
import com.example.picgalleryapp.utils.wrapEspressoIdlingResource
import com.example.picgalleryapp.data.models.Result
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

/**
 * @author Tomislav Curis
 */
class PicGalleryRepositoryImpl(
    private val localDataSource: PicGalleryDataSource
) : PicGalleryRepository {

    override fun observePictures(page: Int): LiveData<Result<List<ImageUri>>> =
        wrapEspressoIdlingResource {
            return localDataSource.observePictures(page)
        }


    override suspend fun savePicture(uri: String) {
        coroutineScope {
            try {
                launch { localDataSource.savePicture(uri) }
            } catch (e: Exception) {
                throw e
            }
        }
    }


    override suspend fun fetchPictures(page: Int, per_page: Int): Result<List<ImageUri>> =
        wrapEspressoIdlingResource {
             return localDataSource.fetchPictures(page, per_page)
        }

    override suspend fun deletePics() {
        wrapEspressoIdlingResource { localDataSource.deletePics() }
    }
}