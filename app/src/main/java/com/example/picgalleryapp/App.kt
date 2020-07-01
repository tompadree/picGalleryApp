package com.example.picgalleryapp

import android.app.Application
import com.example.picgalleryapp.di.AppModule
import com.example.picgalleryapp.di.DataModule
import com.example.picgalleryapp.di.NetModule
import com.example.picgalleryapp.di.RemoteModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

/**
 * @author Tomislav Curis
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin()
    }

    private fun initKoin() {
        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(listOf(AppModule, NetModule, RemoteModule, DataModule))
        }
    }
}