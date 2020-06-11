package com.example.picgalleryapp.ui.camera

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.ViewTreeObserver
import androidx.annotation.Nullable
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.picgalleryapp.data.models.Result.Error
import com.example.picgalleryapp.data.source.PicGalleryRepository
import com.example.picgalleryapp.utils.SingleLiveEvent
import com.example.picgalleryapp.utils.helpers.ImageHelper
import com.otaliastudios.cameraview.PictureResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File


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

    val crop = SingleLiveEvent<PictureResult>()

    fun photoTaken(image: PictureResult) {
        isCameraVisible.set(false)
        crop.postValue(image)


        viewModelScope.launch(Dispatchers.IO) {

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

    fun rotateImage(){


    }
}