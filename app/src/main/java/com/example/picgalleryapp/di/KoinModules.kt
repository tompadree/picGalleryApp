package com.example.picgalleryapp.di

import kotlinx.coroutines.Dispatchers
import org.koin.dsl.module

/**
 * @author Tomislav Curis
 */

val DataModule = module {

    single { Dispatchers.IO }

}