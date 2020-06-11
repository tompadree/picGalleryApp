package com.example.picgalleryapp.ui.camera

import android.app.ActionBar
import android.graphics.Bitmap
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.RelativeLayout
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.picgalleryapp.R
import com.example.picgalleryapp.data.models.Result
import com.example.picgalleryapp.databinding.FragmentCameraBinding
import com.example.picgalleryapp.ui.BindingFragment
import com.otaliastudios.cameraview.BitmapCallback
import com.otaliastudios.cameraview.PictureResult
import kotlinx.android.synthetic.main.fragment_camera.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class CameraFragment : BindingFragment<FragmentCameraBinding>() {

    override val layoutId = R.layout.fragment_camera

    private val viewModel: CameraViewModel by viewModel()

    private lateinit var currentImage: PictureResult

    private lateinit var globalRect: Rect

    override fun onViewCreated() {
        super.onViewCreated()

        if (cameraFragmentCamera != null)
            cameraFragmentCamera.open()

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        setupObservers()


        cameraFragmentRotateButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                crop()
            }
        })
    }

    override fun onResume() {
        super.onResume()

    }

    override fun onPause() {
        super.onPause()
        if (cameraFragmentCamera != null)
            cameraFragmentCamera.close()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (cameraFragmentCamera != null)
            cameraFragmentCamera.destroy()
    }

    private fun setupCropFrame() {
//        dragRect.layoutParams = RelativeLayout.LayoutParams(
//            cameraFragmentPreviewIV.width,
//            cameraFragmentPreviewIV.height
//        )
        dragRect.setOnUpCallback(object : DragRectView.OnUpCallback {
            override fun onRectFinished(rect: Rect?) {
                rect?.let { globalRect = it }
                Toast.makeText(
                    context,
                    "Rect is (" + rect?.left + ", " + rect?.top + ", " + rect?.right + ", " + rect?.bottom + ")",
                    Toast.LENGTH_LONG
                ).show();

            }
        })
    }

    private fun setupObservers() {

        viewModel.takePhoto.observe(this) {
            cameraFragmentCamera.takePicture()
        }

        viewModel.photoSaved.observe(this) {
            activity?.onBackPressed()
        }

        viewModel.crop.observe(this) {
            it?.let { currentImage = it }
            setupCropFrame()
        }
    }

    fun crop() {


                try {

                    currentImage.toBitmap(cameraFragmentPreviewIV.width, cameraFragmentPreviewIV.height, object : BitmapCallback {
                        override fun onBitmapReady(bitmap: Bitmap?) {

                            Glide.with(activity!!).load(currentImage.data)
                                .listener(object : RequestListener<Drawable?> {

                                    override fun onLoadFailed(
                                        e: GlideException?,
                                        model: Any?,
                                        target: Target<Drawable?>?,
                                        isFirstResource: Boolean
                                    ): Boolean {
                                        TODO("Not yet implemented")
                                    }

                                    override fun onResourceReady(
                                        resource: Drawable?,
                                        model: Any?,
                                        target: com.bumptech.glide.request.target.Target<Drawable?>?,
                                        dataSource: DataSource?,
                                        isFirstResource: Boolean
                                    ): Boolean {
                                        // Get original bitmap
//                        sourceBitmap = (resource as BitmapDrawable).bitmap


                                        // Create a new bitmap corresponding to the crop area
//                                        val cropAreaXY = IntArray(2)
//                                        val placeHolderXY = IntArray(2)
//                                        val rect = Rect()
                                        cameraFragmentPreviewIV.getViewTreeObserver()
                                            .addOnPreDrawListener(object :
                                                ViewTreeObserver.OnPreDrawListener {
                                                override fun onPreDraw(): Boolean {
                                                    return try {
//                                                        cameraFragmentPreviewIV.getLocationOnScreen(
//                                                            placeHolderXY
//                                                        )
//                                                        cropArea.getLocationOnScreen(cropAreaXY)
//                                                        cropArea.getGlobalVisibleRect(rect)
                                                        var left =  globalRect.left / (context!!.resources
                                                            .displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
                                                        var right =  globalRect.right / (context!!.resources
                                                            .displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)

                                                        globalRect.top -= ((cameraFragmentPreviewIV.height - bitmap!!.height)/2)
                                                        var top =  globalRect.top / (context!!.resources
                                                            .displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
                                                        val test = ((cameraFragmentPreviewIV.height - bitmap!!.height)/2)
                                                        globalRect.bottom -= ((cameraFragmentPreviewIV.height - bitmap!!.height)/2)
                                                        var bottom =  globalRect.bottom / (context!!.resources
                                                            .displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
//                                                        bottom -= ((cameraFragmentPreviewIV.height - bitmap!!.height)/2)

//                                                        val cropWidth = bitmap!!.width - left - right
//                                                        val cropHeight = bitmap.height - top - bottom
                                                        val cropWidth = right - left
                                                        val cropHeight = bottom - top
                                                        var croppedBitmap = Bitmap.createBitmap(bitmap, left.toInt(), top.toInt(), cropWidth.toInt(), cropHeight.toInt())

//                                                        var croppedBitmap = Bitmap.createBitmap(
//                                                            bitmap!!,
//                                                            globalRect.left,
//                                                            globalRect.top,//cropAreaXY[0],
////                                                            cropAreaXY[1] - placeHolderXY[1],
//                                                            112,
//                                                            112
//                                                        )
                                                        // Save the croppedBitmap if you wish
                                                        activity!!.runOnUiThread { cameraFragmentPreviewIV.setImageBitmap(croppedBitmap)
                                                        }
                                                        true
                                                    } finally {
                                                        cameraFragmentPreviewIV.viewTreeObserver.removeOnPreDrawListener(
                                                            this
                                                        )
                                                    }
                                                }
                                            })
                                        return false
                                    }

                                }).into(
                                    cameraFragmentPreviewIV
                                )

                        }
                    })

                } catch (e: Exception) {
                    Result.Error(e)
                    e.printStackTrace()
                }


    }
}
