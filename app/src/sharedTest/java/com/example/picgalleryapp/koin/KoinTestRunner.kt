package com.example.picgalleryapp.koin

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import com.example.picgalleryapp.TestApp


/**
 * @author Tomislav Curis
 *
 * runner for TestApp
 */
class KoinTestRunner: AndroidJUnitRunner() {
    override fun newApplication(cl: ClassLoader?, className: String?, context: Context?): Application {
        return super.newApplication(
            cl, TestApp::class.java.name, context
        )
    }
}