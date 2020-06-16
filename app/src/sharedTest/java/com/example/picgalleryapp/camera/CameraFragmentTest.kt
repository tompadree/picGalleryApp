package com.example.picgalleryapp.camera

import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import androidx.test.platform.app.InstrumentationRegistry
import com.example.picgalleryapp.R
import com.example.picgalleryapp.TestApp
import com.example.picgalleryapp.data.models.ImageUri
import com.example.picgalleryapp.data.models.Result
import com.example.picgalleryapp.data.source.FakeRepository
import com.example.picgalleryapp.data.source.PicGalleryRepository
import com.example.picgalleryapp.ui.camera.CameraFragment
import com.example.picgalleryapp.ui.camera.CameraViewModel
import com.example.picgalleryapp.util.ViewIdlingResource
import com.example.picgalleryapp.util.DataBindingIdlingResource
import com.example.picgalleryapp.utils.EspressoIdlingResource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject
import org.mockito.Mockito
import com.google.common.truth.Truth.*

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

    private val viewModel: CameraViewModel by inject()

    @Before
    fun initRepo() {

        repository = FakeRepository()

        val application =
            InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as TestApp
        application.injectModule(module {
            single(override = true) { repository }
        })
    }

    // An Idling Resource that waits for Data Binding to have no pending bindings
    private val dataBindingIdlingResource = DataBindingIdlingResource()

    @Before
    fun registerIdlingResource() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
//        IdlingRegistry.getInstance().register(dataBindingIdlingResource)
    }

    //Unregister Idling Resource so it can be garbage collected and does not leak any memory.
    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
//        IdlingRegistry.getInstance().unregister(dataBindingIdlingResource)
    }

    @Test
    fun takePhoto() {

        // GIVEN - On the camera screen
        launchFragment()

        // Flag for camera set
        assertThat(viewModel.isCameraVisible.get(), `is`(true))

        // Take photo
        onView(withId(R.id.cameraFragmentTakeShotButton)).perform(click())

        val matcher = withId(R.id.cameraFragmentPreviewLayout)
        val resource = ViewIdlingResource(matcher, isDisplayed())
        try {
            IdlingRegistry.getInstance().register(resource)
            onView(matcher).check(matches(isDisplayed()))

        } finally {
            IdlingRegistry.getInstance().unregister(resource)
        }

        onView(withId(R.id.cameraFragmentRotateButton)).check(matches(isDisplayed()))


    }

    @Test
    fun saveTakenPhoto() {

        // GIVEN - On the camera screen
        launchFragment()

        // Take photo
        onView(withId(R.id.cameraFragmentTakeShotButton)).perform(click())

        val matcher = withId(R.id.cameraFragmentPreviewLayout)
        val resource = ViewIdlingResource(matcher, isDisplayed())
        try {
            IdlingRegistry.getInstance().register(resource)
            onView(matcher).check(matches(isDisplayed()))

        } finally {
            IdlingRegistry.getInstance().unregister(resource)
        }

        // Take photo
        onView(withId(R.id.cameraFragmentConfirmButton)).perform(click())

        val pics = runBlocking {
            repository.savePicture("UriTest")
            (repository.fetchPictures(0, 30) as Result.Success).data
        }

        // Check if images exist after adding
        Assert.assertEquals(pics.size, 2)
        Assert.assertEquals(pics[1].uri, "UriTest")

    }

    @Test
    fun saveTakenPhotoAlongOthers() {

        // Fill db
        runBlocking {
            repository.savePicture("Uri1")
            repository.savePicture("Uri2")
            repository.savePicture("Uri3")
        }

        // GIVEN - On the camera screen
        launchFragment()

        // Take photo
        onView(withId(R.id.cameraFragmentTakeShotButton)).perform(click())

        val matcher = withId(R.id.cameraFragmentPreviewLayout)
        val resource = ViewIdlingResource(matcher, isDisplayed())
        IdlingRegistry.getInstance().register(resource)

        onView(matcher).check(matches(isDisplayed()))

        IdlingRegistry.getInstance().unregister(resource)

        // Confirm photo
        onView(withId(R.id.cameraFragmentConfirmButton)).perform(click())


        val pics = runBlocking {
            (repository.fetchPictures(0, 30) as Result.Success).data
        }

        // Check if images exist after adding
        Assert.assertEquals(pics.size, 4)
        Assert.assertEquals(pics[0].uri, "Uri1")
        assertThat(pics[3].uri).isNotEqualTo("Uri3")

    }

    private fun launchFragment() {
        // GIVEN - On the home screen
        val scenario = launchFragmentInContainer<CameraFragment>(Bundle(), R.style.AppTheme)

        val navController = Mockito.mock(NavController::class.java)
        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }
    }
}