package com.example.picgalleryapp.ui.camera

import android.content.Context
import android.graphics.Bitmap
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
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
    private val context: Context,
    private val repository: PicGalleryRepository) : ViewModel(){

    val isCameraVisible = ObservableField(true)
    val photo = ObservableField<Bitmap>()
    val photoFile = ObservableField<File>()

    val takePhoto = SingleLiveEvent<Unit>()
    val photoSaved = SingleLiveEvent<String>()
    val photoCropped = SingleLiveEvent<IntArray>()

    var cropFrame = IntArray(4)

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    protected val _error = SingleLiveEvent<Throwable>()
    val error: LiveData<Throwable> get() = _error

    fun photoTaken(image: PictureResult) {
        isCameraVisible.set(false)

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val file = Glide.with(context).downloadOnly().load(image.data).submit().get()
                val bitmap = ImageHelper.resizeImage(file, 512)
                photoFile.set(file)
                photo.set(ImageHelper.setOrientation(bitmap, 90))
                photoCropped.postValue(intArrayOf(bitmap.height, bitmap.width))
                isCameraVisible.set(false)
            } catch (e: Exception) {
                _error.postValue(e)
                e.printStackTrace()
            }
        }
    }

    fun takePhoto(){
        takePhoto.call()
    }

    fun saveRetake(save: Boolean) {
        isCameraVisible.set(!save)

        if (save)
            viewModelScope.launch {
                photoFile.get()?.outputStream().use {
                    photo.get()?.compress(Bitmap.CompressFormat.JPEG, 90, it)
                }
                repository.savePicture(photoFile.get().toString())
                photoFile.set(null)
                photo.set(null)
                photoSaved.postValue("")
            }
        else
            photoFile.get()?.delete()
    }

    fun rotateImage(){
        photo.get()?.let {
            val bitmap = ImageHelper.setOrientation(it, 90)
            photo.set(bitmap)
            photoCropped.postValue(intArrayOf(bitmap!!.width, bitmap.height))
        }
    }

    fun crop(){
        viewModelScope.launch {
            try {
                val cropWidth = cropFrame[1] - cropFrame[0]
                val cropHeight = cropFrame[3] - cropFrame[2]
                val croppedBitmap = Bitmap.createBitmap(
                    photo.get()!!,
                    cropFrame[0],
                    cropFrame[2],
                    cropWidth,
                    cropHeight
                )

                photo.set(croppedBitmap)
                photoCropped.postValue(intArrayOf(cropWidth, cropHeight))

            } catch (e: Exception) {
                _error.postValue(e)
                e.printStackTrace()
            }
        }
    }
}