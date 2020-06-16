package com.example.picgalleryapp

import android.Manifest
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.SystemClock
import android.widget.ImageView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingPolicies
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import com.example.picgalleryapp.data.models.Result
import com.example.picgalleryapp.data.source.FakeRepository
import com.example.picgalleryapp.data.source.PicGalleryRepository
import com.example.picgalleryapp.ui.PicGalleryActivity
import com.example.picgalleryapp.ui.camera.CameraViewModel
import com.example.picgalleryapp.ui.gallery.GalleryImageViewHolder
import com.example.picgalleryapp.ui.gallery.GalleryViewModel
import com.example.picgalleryapp.util.ViewIdlingResource
import com.example.picgalleryapp.util.DataBindingIdlingResource
import com.example.picgalleryapp.util.monitorActivity
import com.example.picgalleryapp.utils.EspressoIdlingResource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.BaseMatcher
import org.hamcrest.CoreMatchers
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject
import java.util.concurrent.TimeUnit


/**
 * @author Tomislav Curis
 *
 * Large End-to-End test.
 *
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
@ExperimentalCoroutinesApi
class PicGalleryActivityTest : KoinTest {

    // Use a fake repository to be injected
    private lateinit var repository: PicGalleryRepository

    // An Idling Resource that waits for Data Binding to have no pending bindings
    private val dataBindingIdlingResource = DataBindingIdlingResource()


    private val cameraViewModel: CameraViewModel by inject()
    private val galleryViewModel: GalleryViewModel by inject()

    @Before
    fun initRepo() {

        repository = FakeRepository()

        val application =
            getInstrumentation().targetContext.applicationContext as TestApp
        application.injectModule(module {
            single(override = true) { repository }
        })

        // Fill the db
        runBlocking {

            val uri = Uri.parse("android.resource://com.example.picgalleryapp/drawable/test_image")
                .toString()

            repository.savePicture(uri)

        }
    }

    // Camera permission
    @Before
    fun cameraPermissionInit(){

        val permissions: ArrayList<String> = ArrayList()
        permissions.add(Manifest.permission.CAMERA)

        for (i in 0 until permissions.size) {
            val command = java.lang.String.format(
                "pm grant %s %s",
                getInstrumentation().targetContext.packageName,
                permissions[i]
            )
            getInstrumentation().uiAutomation.executeShellCommand(command)
            // wait a bit until the command is finished
            SystemClock.sleep(1000)
        }
    }

    // Register general IdlingResource
    @Before
    fun registerIdlingResource() {
        IdlingPolicies.setIdlingResourceTimeout(1, TimeUnit.MINUTES)
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().register(dataBindingIdlingResource)
    }


    //Unregister Idling Resource so it can be garbage collected and does not leak any memory.
    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().unregister(dataBindingIdlingResource)
    }

    @Test
    fun checkImageOnStart() {
        // Start up Activity screen and start monitor
        val activityScenario = ActivityScenario.launch(PicGalleryActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        // THEN - Verify image is displayed on screen
        onView(withId(R.id.galleryItemIv)).check(matches(customMatcherForDrawable(R.drawable.test_image)))

    }


    @Test
    fun takePhotoAndDelete(){
        // Start up Activity screen and start monitor
        val activityScenario = ActivityScenario.launch(PicGalleryActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        // THEN - Verify image is displayed on screen
        onView(withId(R.id.galleryItemIv)).check(matches(customMatcherForDrawable(R.drawable.test_image)))

        onView(withText(R.string.take_photo)).perform(click())

        // Flag for camera set
        assertThat(cameraViewModel.isCameraVisible.get(), CoreMatchers.`is`(true))

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
            (repository.fetchPictures(0, 30) as Result.Success).data
        }

        // Check if images exist after adding
        Assert.assertEquals(pics.size, 2)
        Assert.assertEquals(pics[0].uri, "android.resource://com.example.picgalleryapp/drawable/test_image")

        onView(withText(R.string.delete_images)).perform(click())

        // Check if image exist
        val picsAfterDelete = runBlocking {
            (repository.fetchPictures(0, 30) as Result.Success).data
        }
        Assert.assertEquals(picsAfterDelete.size, 0)
    }

    @Test
    fun addFromRollAndDelete(){
        // Start up Activity screen and start monitor
        val activityScenario = ActivityScenario.launch(PicGalleryActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        // THEN - Verify image is displayed on screen
        onView(withId(R.id.galleryItemIv)).check(matches(customMatcherForDrawable(R.drawable.test_image)))

        val pics = runBlocking {
            repository.savePicture("android.resource://com.example.picgalleryapp/drawable/test_image_2")
            (repository.fetchPictures(0, 30) as Result.Success).data
        }

        // Check if images exist after adding
        Assert.assertEquals(pics.size, 2)
        Assert.assertEquals(pics[0].uri, "android.resource://com.example.picgalleryapp/drawable/test_image")

        onView(withText(R.string.delete_images)).perform(click())

        // Check if image exist
        val picsAfterDelete = runBlocking {
            (repository.fetchPictures(0, 30) as Result.Success).data
        }
        Assert.assertEquals(picsAfterDelete.size, 0)
    }

    @Test
    fun takePhotoClickImageForEditRotateSaveAndDelete(){
        // Start up Activity screen and start monitor
        val activityScenario = ActivityScenario.launch(PicGalleryActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        // THEN - Verify image is displayed on screen
        onView(withId(R.id.galleryItemIv)).check(matches(customMatcherForDrawable(R.drawable.test_image)))

        onView(withText(R.string.take_photo)).perform(click())

        // Flag for camera set
        assertThat(cameraViewModel.isCameraVisible.get(), CoreMatchers.`is`(true))

        // Take photo
        onView(withId(R.id.cameraFragmentTakeShotButton)).perform(click())

        // Idle
        val matcher = withId(R.id.cameraFragmentPreviewLayout)
        val resource = ViewIdlingResource(matcher, isDisplayed())
        try {
            IdlingRegistry.getInstance().register(resource)
            onView(matcher).check(matches(isDisplayed()))

        } finally {
            IdlingRegistry.getInstance().unregister(resource)
        }

        // Save photo
        onView(withId(R.id.cameraFragmentConfirmButton)).perform(click())

        var pics = runBlocking {
            (repository.fetchPictures(0, 30) as Result.Success).data
        }

        // Check if image exist after adding
        Assert.assertEquals(pics.size, 2)

        // Idle
        val matcher3 = withId(R.id.cameraFragmentPreviewLayout)
        val resource3 = ViewIdlingResource(matcher3, isDisplayed())
        try {
            IdlingRegistry.getInstance().register(resource3)
            onView(matcher3).check(matches(isDisplayed()))

        } finally {
            IdlingRegistry.getInstance().unregister(resource3)
        }

        // Click photo
//        onView(withId(R.id.galleryFragRv)).check(matches(RecyclerViewActions.actionOnItemAtPosition<GalleryImageViewHolder>(0)))
        onView(withId(R.id.galleryFragRv)).perform(RecyclerViewActions.actionOnItemAtPosition<GalleryImageViewHolder>(1, click()))

        // Check again idle for R.id.cameraFragmentPreviewLayout
        val matcher2 = withId(R.id.cameraFragmentPreviewLayout)
        val resource2 = ViewIdlingResource(matcher2, isDisplayed())
        try {
            IdlingRegistry.getInstance().register(resource2)
            onView(matcher2).check(matches(isDisplayed()))

        } finally {
            IdlingRegistry.getInstance().unregister(resource2)
        }

        // Rotate image
        onView(withId(R.id.cameraFragmentRotateButton)).perform(click())

        // Save photo
        onView(withId(R.id.cameraFragmentConfirmButton)).perform(click())

        pics = runBlocking {
            (repository.fetchPictures(0, 30) as Result.Success).data
        }

        // Check if images exist after adding
        Assert.assertEquals(pics.size, 2)

        // Delete
        onView(withText(R.string.delete_images)).perform(click())

        // Check if image exist
        val picsAfterDelete = runBlocking {
            (repository.fetchPictures(0, 30) as Result.Success).data
        }
        Assert.assertEquals(picsAfterDelete.size, 0)
    }


    private fun <T> customMatcherForDrawable(imageId: Int): Matcher<T> {

        return object : BaseMatcher<T>() {
            override fun matches(item: Any?): Boolean {
                if (item !is ImageView)
                    return false

                val bitmap1 = getBitmap(item.drawable)
                val bitmap2 = getBitmap(item.resources.getDrawable(imageId, null))

                return bitmap1.sameAs(bitmap2)
            }

            override fun describeTo(description: Description?) {}


            fun getBitmap(drawable: Drawable): Bitmap {
                val bitmap = Bitmap.createBitmap(
                    drawable.intrinsicWidth,
                    drawable.intrinsicHeight, Bitmap.Config.ARGB_8888
                )
                val canvas = Canvas(bitmap)
                drawable.setBounds(0, 0, canvas.width, canvas.height)
                drawable.draw(canvas)
                return bitmap
            }

        }
    }
}