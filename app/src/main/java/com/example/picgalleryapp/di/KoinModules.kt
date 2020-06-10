package com.example.picgalleryapp.di

import androidx.room.Room
import com.example.picgalleryapp.data.source.PicGalleryDataSource
import com.example.picgalleryapp.data.source.PicGalleryRepository
import com.example.picgalleryapp.data.source.PicGalleryRepositoryImpl
import com.example.picgalleryapp.data.source.local.PicGalleryDatabase
import com.example.picgalleryapp.data.source.local.PicGalleryLocalDataSource
import com.example.picgalleryapp.ui.camera.CameraViewModel
import com.example.picgalleryapp.ui.gallery.GalleryViewModel
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * @author Tomislav Curis
 */

val DataModule = module {

    single {
        Room
            .databaseBuilder(androidContext(), PicGalleryDatabase::class.java, "picGallery_db")
            .build()
    }
    single { get<PicGalleryDatabase>().getPicGalleryDAO() }

    single { Dispatchers.IO }

    single { PicGalleryLocalDataSource(get(), get()) as PicGalleryDataSource }

    single { PicGalleryRepositoryImpl(get()) as PicGalleryRepository }


    viewModel { GalleryViewModel(get()) }
    viewModel { CameraViewModel(get(), get()) }

}