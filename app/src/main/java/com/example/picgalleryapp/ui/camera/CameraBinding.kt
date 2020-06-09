package com.example.picgalleryapp.ui.camera

import androidx.databinding.BindingAdapter
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.CameraView
import com.otaliastudios.cameraview.PictureResult
import java.io.File

/**
 * @author Tomislav Curis
 */

@BindingAdapter("app:imageSource")
fun setImagePreview(imageView: SubsamplingScaleImageView, file: File?){

    try {
        imageView.orientation = SubsamplingScaleImageView.ORIENTATION_90
        imageView.setImage(ImageSource.uri(file.toString()))
    } catch (e: Exception) {
        e.printStackTrace()
        imageView.recycle()
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