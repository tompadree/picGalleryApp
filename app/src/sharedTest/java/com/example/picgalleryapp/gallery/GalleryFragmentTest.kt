package com.example.picgalleryapp.gallery

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.*
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.CoreMatchers.not
import org.hamcrest.Matchers.allOf
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
//            repository.savePicture(uri)
//            repository.savePicture(uri)
        }
    }

    @Test
    fun checkImage(){
        // GIVEN - On the home screen
        launchFragment()

        // THEN - Verify repos are displayed on screen
        onView(withId(R.id.galleryItemIv)).check(matches(customMatcherForDrawable(R.drawable.test_image)))

    }


    @Test
    fun deleteImage(){
        // GIVEN - On the home screen
        launchFragment()
//        Espresso.openContextualActionModeOverflowMenu()

//        onView(withId(R.id.galleryFragToolbar)).perform(Toolbar(hasDescendant(withText(withText(""))), click()))

//        val btnMenu: UiObject =
//            mDevice.findObject(UiSelector().description(mActivity.getString(R.string.menu_button_identifier)))
//        btnMenu.click()

//        openActionBarOverflowOrOptionsMenu(ApplicationProvider.getApplicationContext())
//        onView(withContentDescription(R.string.open_menu))
//            .perform(click())
//        onView(withText(R.string.delete_images)).perform(click())

//        onView(allOf(instanceOf(View::class.java), withParent(withId(R.id.galleryFragToolbar)))).perform(click())

//        onView(withId(R.id.galleryFragToolbar))
//            .check(matches(withChild(withChild(withText("Open menu")))))

//        val textView4 = onView(
//            Matchers.allOf(
//                withId(R.id.menuMore), withContentDescription("Open menu"),
//                childAtPosition(
//                    childAtPosition(
//                        withId(R.id.galleryFragToolbar),
//                        1
//                    ),
//                    0
//                ),
//                isDisplayed()
//            )
//        )
        openActionBarOverflowOrOptionsMenu(ApplicationProvider.getApplicationContext())
//        textView4.check(matches(withText("Open menu")))
//
//        onView(withId(R.id.galleryFragToolbar)).check(matches(hasDescendant(withText("Open menu"))));

//        openActionBarOverflowOrOptionsMenu(ApplicationProvider.getApplicationContext())
//        onView(withText(R.string.delete_images)).perform(click())
////        // Check if image exists
//        onView(withId(R.id.galleryItemIv)).check(matches(not(customMatcherForDrawable(R.drawable.test_image))))

    }

    private fun childAtPosition(
        parentMatcher: Matcher<View>, position: Int
    ): Matcher<View> {

        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent)
                        && view == parent.getChildAt(position)
            }
        }
    }


    // https://medium.com/@dbottillo/android-ui-test-espresso-matcher-for-imageview-1a28c832626f
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

    private fun launchFragment() {
        // GIVEN - On the home screen
        val scenario = launchFragmentInContainer<GalleryFragment>(Bundle(), R.style.AppTheme)

        val navController = Mockito.mock(NavController::class.java)
        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }
    }

}