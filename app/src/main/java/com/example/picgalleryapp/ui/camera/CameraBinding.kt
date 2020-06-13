package com.example.picgalleryapp.ui.camera

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.davemorrissey.labs.subscaleview.ImageSource
import com.example.picgalleryapp.utils.helpers.ImageHelper
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.CameraView
import com.otaliastudios.cameraview.PictureResult
import java.io.File

/**
 * @author Tomislav Curis
 */

@BindingAdapter("app:imageSource")
fun setImagePreview(imageView: ImageView, image: Bitmap?){

    try {
        imageView.setImageBitmap(image)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

@BindingAdapter("app:onPhotoTaken")
fun onPhotoTaken(cameraView: CameraView, photoListener: PhotoListener) {

    cameraView.addCameraListener(object : CameraListener() {
        override fun onPictureTaken(result: PictureResult) {
            super.onPictureTaken(result)
            val file = Glide.with(cameraView).downloadOnly().load(result.data).submit().get()
            photoListener.photoTaken(file)
        }
    })
}

interface PhotoListener {
    fun photoTaken(file: File)
}