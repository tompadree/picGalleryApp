package com.example.picgalleryapp.ui


import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.core.content.ContextCompat
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

/**
 * @author Tomislav Curis
 */
abstract class BaseFragment : Fragment(), CoroutineScope {

    protected val TAG = this::class.java.simpleName

    private var dialogManager: DialogManager? = null

    private val fragmentContextJob = Job()
    override val coroutineContext: CoroutineContext
        get() = fragmentContextJob + Dispatchers.Main

    private val CAMERA_PERMISSION_CODE = 12345

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

    protected open fun showPermissionDialog() {
        getDialogManager().openOneButtonDialog(R.string.ok, getString(R.string.camera_permission),
            getString(R.string.permit_warning),
            true,
            onClickOk = {
                requestPermissions(
                    arrayOf(Manifest.permission.CAMERA),
                    CAMERA_PERMISSION_CODE
                )
            })
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


    // this is called when user closes the permission request dialog
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (permissions[0]  == Manifest.permission.CAMERA &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                onResume()
            }
            else{
                getDialogManager().openTwoButtonsDialog(getString(R.string.camera_permission),
                    getString(R.string.permit_warning_2),
                    getString(R.string.ok),
                    getString(R.string.cancel),
                    onPositiveButtonClick = {
                        val intent = Intent()
                        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        val uri: Uri = Uri.fromParts("package", activity?.packageName, null)
                        intent.data = uri
                        startActivity(intent)
                    },
                    onNegativeButtonClick = {
                        removeDialogs()
                    }
                )
            }
        }
    }

    fun permissionGranted() = ContextCompat.checkSelfPermission(
        activity!!, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
}
