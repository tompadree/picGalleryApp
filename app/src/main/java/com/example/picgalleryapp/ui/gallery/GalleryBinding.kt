package com.example.picgalleryapp.ui.gallery

import android.graphics.Color
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.example.picgalleryapp.data.models.ImageUri
import java.io.File

/**
 * @author Tomislav Curis
 */

@BindingAdapter("app:items")
fun setItems(listView: RecyclerView, items: List<ImageUri>?) {
    if(items.isNullOrEmpty()) return

    (listView.adapter as GalleryAdapter).submitList(items)
}

@BindingAdapter("app:imageSourceGallery")
fun setImagePreview(imageView: SubsamplingScaleImageView, uri: String){

    try {
        imageView.setImage(ImageSource.uri(uri))
    } catch (e: Exception) {
        e.printStackTrace()
        imageView.recycle()
    }
}