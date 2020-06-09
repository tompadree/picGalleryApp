package com.example.picgalleryapp.di

import com.example.picgalleryapp.ui.gallery.GalleryViewModel
import kotlinx.coroutines.Dispatchers
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * @author Tomislav Curis
 */

val DataModule = module {

    single { Dispatchers.IO }


    viewModel { GalleryViewModel() }

}