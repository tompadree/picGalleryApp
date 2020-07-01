package com.example.picgalleryapp.data.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * @author Tomislav Curis
 */



data class HepoImage (

    @SerializedName("url")
    var url: String,

    @SerializedName("created_at")
    var created_at: String
) : Serializable