package com.example.picgalleryapp.data.source.remote.api

import com.example.picgalleryapp.data.models.HepoImage
import com.example.picgalleryapp.data.source.remote.api.APIConstants.Companion.GET_IMAGES
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * @author Tomislav Curis
 */
interface HerokuappAPI {

    @GET(GET_IMAGES)
    fun getImages(@Query("user_id") user_id: String) : Response<List<HepoImage>>
}