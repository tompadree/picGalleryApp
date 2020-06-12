package com.example.picgalleryapp.ui.gallery

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.example.picgalleryapp.data.models.ImageUri

/**
 * @author Tomislav Curis
 */

@BindingAdapter("app:items")
fun setItems(listView: RecyclerView, items: List<ImageUri>?) {
    if(items == null) return

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