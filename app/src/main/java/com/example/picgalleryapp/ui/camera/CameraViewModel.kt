package com.example.picgalleryapp.ui.camera

import android.content.Context
import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.example.picgalleryapp.data.models.Result.Error
import com.example.picgalleryapp.data.source.PicGalleryRepository
import com.example.picgalleryapp.utils.SingleLiveEvent
import com.example.picgalleryapp.utils.helpers.ImageHelper
import com.otaliastudios.cameraview.PictureResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.lang.Exception

/**
 * @author Tomislav Curis
 */
class CameraViewModel(
    private val context : Context,
    private val repository: PicGalleryRepository) : ViewModel(){

    val isCameraVisible = ObservableField(true)
    val photo = ObservableField<File>()

    val takePhoto = SingleLiveEvent<Unit>()
    val photoSaved = SingleLiveEvent<String>()

    fun photoTaken(image : PictureResult) {

        isCameraVisible.set(false)

        //https://youtrack.jetbrains.com/issue/IDEA-227359
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val file = Glide.with(context).downloadOnly().load(image.data).submit().get()
                ImageHelper.resizeImage(file, 512)
                photo.set(file)
                isCameraVisible.set(false)
            } catch (e: Exception) {
                Error(e)
                e.printStackTrace()
            }
        }
    }

    fun takePhoto(){
        takePhoto.call()
    }

    fun saveRetake(save: Boolean) {
        isCameraVisible.set(!save)

        if(save)
            viewModelScope.launch {
                repository.savePicture(photo.get().toString())
                photoSaved.postValue("")
                photo.set(null)
            }
        else
            photo.get()?.delete()
    }
}