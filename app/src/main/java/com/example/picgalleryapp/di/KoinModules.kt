package com.example.picgalleryapp.di

import android.content.Context
import androidx.fragment.app.FragmentActivity
import androidx.room.Room
import com.example.picgalleryapp.data.source.PicGalleryDataSource
import com.example.picgalleryapp.data.source.PicGalleryRepository
import com.example.picgalleryapp.data.source.PicGalleryRepositoryImpl
import com.example.picgalleryapp.data.source.local.PicGalleryDatabase
import com.example.picgalleryapp.data.source.local.PicGalleryLocalDataSource
import com.example.picgalleryapp.data.source.remote.PicGalleryRemoteDataSource
import com.example.picgalleryapp.data.source.remote.api.APIConstants.Companion.BASE_URL
import com.example.picgalleryapp.data.source.remote.api.HerokuappAPI
import com.example.picgalleryapp.ui.camera.CameraViewModel
import com.example.picgalleryapp.ui.gallery.GalleryViewModel
import com.example.picgalleryapp.utils.helpers.dialogs.DialogManager
import com.example.picgalleryapp.utils.helpers.dialogs.DialogManagerImpl
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

/**
 * @author Tomislav Curis
 */

val AppModule = module {
    factory { (activity: FragmentActivity) -> DialogManagerImpl(activity) as DialogManager }
}

val DataModule = module {

//    single { androidContext() }

    single {
        Room
            .databaseBuilder(androidContext(), PicGalleryDatabase::class.java, "picGallery_db")
            .build()
    }
    single { get<PicGalleryDatabase>().getPicGalleryDAO() }

    single { Dispatchers.IO }

    single  (named ("local")) { PicGalleryLocalDataSource(get(), get()) as PicGalleryDataSource }
    single ( named ("remote")) { PicGalleryRemoteDataSource(get()) as PicGalleryDataSource }


    viewModel { GalleryViewModel(get(), get()) }
    viewModel { CameraViewModel(get(), get()) }

}

val RemoteModule = module {

    single { PicGalleryRepositoryImpl(get(qualifier = named("local")),
        get(qualifier = named("remote")), androidContext()) as PicGalleryRepository }

}

val NetModule = module {


    single { OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .build() }

    single {
        (Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(get())
        .build()
        .create(HerokuappAPI::class.java)) as HerokuappAPI

    }


}