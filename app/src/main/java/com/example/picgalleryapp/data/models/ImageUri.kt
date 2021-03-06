package com.example.picgalleryapp.data.models

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

/**
 * @author Tomislav Curis
 */

@Entity(tableName = "uris")
data class ImageUri (
    @NonNull
    @PrimaryKey
    var uri : String
) : Serializable