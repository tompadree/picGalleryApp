package com.example.picgalleryapp.ui.camera

import android.content.Context
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.davemorrissey.labs.subscaleview.ImageSource
import com.example.picgalleryapp.data.models.Result
import com.example.picgalleryapp.data.models.Result.Success
import com.example.picgalleryapp.data.models.Result.Error
import com.example.picgalleryapp.utils.SingleLiveEvent
import com.example.picgalleryapp.utils.helpers.ImageHelper
import com.otaliastudios.cameraview.PictureResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.lang.Exception

/**
 * @author Tomislav Curis
 */
class CameraViewModel(private val context : Context) : ViewModel(){

    val isCameraVisible = ObservableField(true)
    val photo = ObservableField<File>()

    val takePhoto = SingleLiveEvent<Unit>()

    fun photoTaken(image : PictureResult) {

        isCameraVisible.set(false)

        //https://youtrack.jetbrains.com/issue/IDEA-227359
        viewModelScope.launch {

            try {
                val file = getFile(image) as File
                photo.set(file)
                isCameraVisible.set(false)
            } catch (e: Exception) {
                Error(e)
                e.printStackTrace()
            }
        }

    }

    suspend fun getFile(image : PictureResult) =
        withContext(Dispatchers.IO) {
           return@withContext try {
                val file = Glide.with(context).downloadOnly().load(image.data).submit().get()
                ImageHelper.resizeImage(file, 512)
                file
            } catch (e: Exception) {
                Result.Error(e)
            }
        }


    fun takePhoto(){
        takePhoto.call()
    }

    fun saveRetake(save: Boolean) {
        isCameraVisible.set(!save)
        photo.get()?.delete()


    }


    protected fun <T> handleResponseWithError(response: Result<T>): T? {
        return when (response) {
            is Result.Success -> {
//                isDataLoadingError.value = false
                response.data
            }
            is Result.Error -> {
//                isDataLoadingError.value = true
//                _error.postValue(response.exception)
                null
            }
            is Result.Loading -> null
        }
    }
}