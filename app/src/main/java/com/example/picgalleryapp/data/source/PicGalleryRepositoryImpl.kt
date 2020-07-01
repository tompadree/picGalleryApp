package com.example.picgalleryapp.data.source

import android.content.Context
import androidx.lifecycle.LiveData
import com.bumptech.glide.Glide
import com.bumptech.glide.signature.ObjectKey
import com.example.picgalleryapp.data.models.ImageUri
import com.example.picgalleryapp.utils.wrapEspressoIdlingResource
import com.example.picgalleryapp.data.models.Result
import com.example.picgalleryapp.data.source.remote.PicGalleryRemoteDataSource
import com.example.picgalleryapp.utils.helpers.ImageHelper
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

/**
 * @author Tomislav Curis
 */
class PicGalleryRepositoryImpl(
    private val localDataSource: PicGalleryDataSource,
    private val remoteDataSource: PicGalleryDataSource,
    private val context: Context
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


    override suspend fun fetchPictures(update: Boolean, page: Int, per_page: Int): Result<List<ImageUri>> =
        wrapEspressoIdlingResource {
            if(update)
                try{
                    updateDBFromRemote()
                } catch (e : Exception) {
                    throw e
                }

             return localDataSource.fetchPictures(page, per_page)
        }

    override suspend fun deletePics() {
        wrapEspressoIdlingResource { localDataSource.deletePics() }
    }

    private suspend fun updateDBFromRemote() {
        wrapEspressoIdlingResource {
            val hepoImages = remoteDataSource.fetchAllPictures("100")

            if(hepoImages is Result.Success){
                val file = Glide.with(context).downloadOnly().load(hepoImages.data[0].url)
                    .signature(ObjectKey(System.currentTimeMillis()))
                    .submit().get()
                ImageHelper.resizeImage(file, 512)
                localDataSource.savePicture(file.toString())
            } else if (hepoImages is Result.Error){
                throw hepoImages.exception
            }

        }
    }
}