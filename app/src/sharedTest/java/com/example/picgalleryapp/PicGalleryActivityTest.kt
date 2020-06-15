package com.example.picgalleryapp

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import com.example.picgalleryapp.data.models.Result
import com.example.picgalleryapp.data.source.FakeRepository
import com.example.picgalleryapp.data.source.PicGalleryRepository
import com.example.picgalleryapp.ui.PicGalleryActivity
import com.example.picgalleryapp.ui.camera.CameraViewModel
import com.example.picgalleryapp.ui.gallery.GalleryViewModel
import com.example.picgalleryapp.util.CameraIdlingResource
import com.example.picgalleryapp.util.DataBindingIdlingResource
import com.example.picgalleryapp.util.monitorActivity
import com.example.picgalleryapp.utils.EspressoIdlingResource
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.BaseMatcher
import org.hamcrest.CoreMatchers
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.junit.Assert

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
            InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as TestApp
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

    // Register IdlingResource
    @Before
    fun registerIdlingResource() {
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
        val resource = CameraIdlingResource(matcher)
        IdlingRegistry.getInstance().register(resource)

        onView(matcher).check(matches(isDisplayed()))

        IdlingRegistry.getInstance().unregister(resource)

        // Take photo
        onView(withId(R.id.cameraFragmentConfirmButton)).perform(click())

        val pics = runBlocking {
//            repository.savePicture("UriTest")
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
            repository.savePicture("android.resource://com.example.picgalleryapp/drawable/test_image")
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
                    drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888
                );
                val canvas = Canvas(bitmap);
                drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                drawable.draw(canvas);
                return bitmap;
            }

        }
    }
}