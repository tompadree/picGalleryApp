package com.example.picgalleryapp.ui.gallery

import android.net.Uri
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
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
fun setImagePreview(imageView: ImageView, uri: String){

    try {
        imageView.setImageURI(Uri.parse(uri))
    } catch (e: Exception) {
        e.printStackTrace()
    }
}