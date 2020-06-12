package com.example.picgalleryapp.ui


import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.picgalleryapp.R
import com.example.picgalleryapp.utils.helpers.dialogs.DialogManager
import com.example.picgalleryapp.utils.helpers.observe
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import org.koin.core.parameter.parametersOf
import kotlin.coroutines.CoroutineContext
import org.koin.android.ext.android.get


abstract class BaseFragment : Fragment(), CoroutineScope {

    protected val TAG = this::class.java.simpleName

    private var dialogManager: DialogManager? = null

    private val fragmentContextJob = Job()
    override val coroutineContext: CoroutineContext
        get() = fragmentContextJob + Dispatchers.Main



    override fun onStop() {
        super.onStop()
        dialogManager?.dismissAll()
    }

    override fun onDestroy() {
        try {
            fragmentContextJob.cancel()
        } finally {
            super.onDestroy()
        }
    }

    protected open fun observeError(errorLiveData: LiveData<Throwable>) {
        errorLiveData.observe(this) {
            showError(it ?: return@observe)
        }
    }

    protected open fun observeErrorRefreshLayout(errorLiveData: LiveData<Throwable>, swipeRefreshLayout: SwipeRefreshLayout) {
        errorLiveData.observe(this) {
            showError(it ?: return@observe)
            swipeRefreshLayout.isRefreshing = false
        }
    }

    protected open fun showError(throwable: Throwable) {
        if (throwable is IllegalArgumentException){
            showError(getString(R.string.inside_image))
        } else if (throwable is Exception) {
            showError(throwable.localizedMessage)
        } else
            showUnknownError()
    }

    protected open fun showError(error: String?) {
        if (error != null) {
            getDialogManager().openOneButtonDialog(R.string.ok, error, true)
        } else {
            showUnknownError()
        }
    }

    protected open fun showError(errorTitle: String, errorMessage: String) {
        getDialogManager().openOneButtonDialog(R.string.ok, errorTitle, errorMessage, true)
    }

    protected open fun showUnknownError() {
        getDialogManager().openOneButtonDialog(R.string.ok, R.string.error_default, true)
    }

    private fun getDialogManager(): DialogManager {
        if (dialogManager == null) {
            dialogManager = get { parametersOf(requireContext()) }
        }

        return dialogManager!!
    }

    fun removeDialogs() {
        dialogManager?.dismissAll()
    }
}
