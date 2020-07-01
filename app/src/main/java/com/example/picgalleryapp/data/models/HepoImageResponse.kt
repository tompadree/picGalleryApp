package com.example.picgalleryapp.data.models

import com.google.gson.annotations.SerializedName

/**
 * @author Tomislav Curis
 */


data class HepoImageResponse(

    @SerializedName("images")
    var images: List<HepoImage>
)