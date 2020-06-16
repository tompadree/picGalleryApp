package com.example.picgalleryapp.utils.helpers

import android.os.Handler
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

/**
 * @author Tomislav Curis
 */

fun <T> LiveData<T>.observe(owner: LifecycleOwner, f: (T?) -> Unit) {
    observe(owner, Observer {
        f(it)
    })
}

fun delay(t: Long = 300, f: () -> Unit) {
    Handler().postDelayed({
        f()
    }, t)
}


