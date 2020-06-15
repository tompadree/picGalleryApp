package com.example.picgalleryapp.camera

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.pressMenuKey
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import androidx.test.platform.app.InstrumentationRegistry
import com.example.picgalleryapp.TestApp
import com.example.picgalleryapp.data.models.ImageUri
import com.example.picgalleryapp.data.source.FakeRepository
import com.example.picgalleryapp.data.source.PicGalleryRepository
import com.example.picgalleryapp.ui.camera.CameraViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject
import org.mockito.Mockito

/**
 * @author Tomislav Curis
 */
@RunWith(AndroidJUnit4::class)
@MediumTest
@ExperimentalCoroutinesApi
class CameraFragmentTest : KoinTest {

    // Use a fake repository to be injected
    private lateinit var repository: PicGalleryRepository

    private val imageUri = ImageUri("Uri1")
    private val imageUri2 = ImageUri("Uri2")
    private val imageUri3 = ImageUri("Uri3")
    private val images = listOf(imageUri, imageUri2, imageUri3)

    private val viewModel : CameraViewModel by inject()

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
            repository.savePicture("Uri1")
            repository.savePicture("Uri2")
            repository.savePicture("Uri3")
        }
    }



    private fun launchFragment() {
        // GIVEN - On the home screen
//        val bundle = GitResultsDetailsFragmentArgs(RepoObject(1,"Repo1")).toBundle()
//        val scenario = launchFragmentInContainer<GitResultsDetailsFragment>(bundle, R.style.AppTheme)
//
//        val navController = Mockito.mock(NavController::class.java)
//        scenario.onFragment {
//            Navigation.setViewNavController(it.view!!, navController)
//        }
    }



}