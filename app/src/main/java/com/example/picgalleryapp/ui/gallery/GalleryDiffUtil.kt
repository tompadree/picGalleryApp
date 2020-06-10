package com.example.picgalleryapp.ui.gallery

import androidx.recyclerview.widget.DiffUtil
import com.example.picgalleryapp.data.models.ImageUri

/**
 * @author Tomislav Curis
 */
class GalleryDiffUtil: DiffUtil.ItemCallback<ImageUri>() {
    override fun areContentsTheSame(oldItem: ImageUri, newItem: ImageUri): Boolean {
        return oldItem.uri == newItem.uri
    }

    override fun areItemsTheSame(oldItem: ImageUri, newItem: ImageUri): Boolean {
        return oldItem.hashCode() == newItem.hashCode()
    }
}