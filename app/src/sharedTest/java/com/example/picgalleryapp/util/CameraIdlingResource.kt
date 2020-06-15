package com.example.picgalleryapp.util

import android.view.View
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingResource
import androidx.test.espresso.ViewFinder
import org.hamcrest.Matcher
import java.lang.reflect.Field
import java.util.*


/**
 * @author Tomislav Curis
 */

class CameraIdlingResource(private val matcher: Matcher<View>) : IdlingResource {
    // List of registered callbacks
    private lateinit var idlingCallback : IdlingResource.ResourceCallback
    // Give it a unique id to work around an Espresso bug where you cannot register/unregister
    // an idling resource with the same name.
    private val id = UUID.randomUUID().toString()
    // Holds whether isIdle was called and the result was false. We track this to avoid calling
    // onTransitionToIdle callbacks if Espresso never thought we were idle in the first place.
    private var wasNotIdle = false

//    lateinit var fragment: Fragment

    override fun getName() = "DataBinding $id"

    override fun isIdleNow(): Boolean {
        var view = getView(matcher)
        val idle = view == null || view.isShown

        if (idle && idlingCallback != null) {
            idlingCallback.onTransitionToIdle()
        }

        return idle
    }

    override fun registerIdleTransitionCallback(callback: IdlingResource.ResourceCallback) {
        idlingCallback = callback
    }

    private fun getView(viewMatcher: Matcher<View>): View? {
        return try {
            val viewInteraction = onView(viewMatcher)
            val finderField: Field = viewInteraction.javaClass.getDeclaredField("viewFinder")
            finderField.setAccessible(true)
            val finder = finderField.get(viewInteraction) as ViewFinder
            finder.view
        } catch (e: Exception) {
            null
        }
    }


}
