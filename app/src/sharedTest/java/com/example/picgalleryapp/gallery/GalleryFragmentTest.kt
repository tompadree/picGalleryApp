package com.example.picgalleryapp.gallery

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import androidx.test.platform.app.InstrumentationRegistry
import com.example.picgalleryapp.R
import com.example.picgalleryapp.TestApp
import com.example.picgalleryapp.data.models.ImageUri
import com.example.picgalleryapp.data.source.FakeRepository
import com.example.picgalleryapp.data.source.PicGalleryRepository
import com.example.picgalleryapp.ui.gallery.GalleryFragment
import com.example.picgalleryapp.ui.gallery.GalleryViewModel
import com.google.common.io.Resources.getResource
import com.googlecode.eyesfree.compat.CompatUtils.getClass
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject
import org.mockito.Mockito
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.*
import androidx.test.espresso.matcher.ViewMatchers.*

/**
 * @author Tomislav Curis
 */

@RunWith(AndroidJUnit4::class)
@MediumTest
@ExperimentalCoroutinesApi
class GalleryFragmentTest : KoinTest{


    // Use a fake repository to be injected
    private lateinit var repository: PicGalleryRepository

    private val imageUri = ImageUri("Uri1")
    private val imageUri2 = ImageUri("Uri2")
    private val imageUri3 = ImageUri("Uri3")
    private val images = listOf(imageUri, imageUri2, imageUri3)

    private val viewModel : GalleryViewModel by inject()

    @Before
    fun initRepo() {

        repository = FakeRepository()

        val application =
            InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as TestApp
        application.injectModule(module {
            single(override = true) { repository }
        })

        // Fill the db
        runBlocking {

            val uri = Uri.parse("android.resource://com.example.picgalleryapp/drawable/test_image").toString()

            repository.savePicture(uri)
            repository.savePicture(uri)
            repository.savePicture(uri)
        }
    }

    @Test
    fun displayImages(){
        // GIVEN - On the home screen
        launchFragment()

        // THEN - Verify repos are displayed on screen
        onView(withResourceName("android.resource://com.example.picgalleryapp/drawable/test_image")).check(matches(isDisplayed()))
//        onView(withText("Repo2")).check(matches(isDisplayed()))

    }

    private fun launchFragment() {
        // GIVEN - On the home screen
        val scenario = launchFragmentInContainer<GalleryFragment>(Bundle(), R.style.AppTheme)

        val navController = Mockito.mock(NavController::class.java)
        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }
    }
}