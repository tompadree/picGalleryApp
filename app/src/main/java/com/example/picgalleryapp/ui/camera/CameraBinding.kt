package com.example.picgalleryapp.ui.camera

import android.net.Uri
import android.widget.ImageView
import androidx.databinding.BindingAdapter
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
fun setImagePreview(imageView: ImageView, file: File?){

    try {

        imageView.setImageURI(Uri.fromFile(file))
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

@BindingAdapter("app:onPhotoTaken")
fun onPhotoTaken(cameraView: CameraView, photoListener: PhotoListener) {

    cameraView.addCameraListener(object : CameraListener() {
        override fun onPictureTaken(result: PictureResult) {
            super.onPictureTaken(result)
            photoListener.photoTaken(result)
        }
    })
}

interface PhotoListener {
    fun photoTaken(result: PictureResult)
}