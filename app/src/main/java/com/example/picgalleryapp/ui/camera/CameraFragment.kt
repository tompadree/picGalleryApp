package com.example.picgalleryapp.ui.camera

import android.graphics.Rect
import com.example.picgalleryapp.R
import com.example.picgalleryapp.databinding.FragmentCameraBinding
import com.example.picgalleryapp.ui.BindingFragment
import kotlinx.android.synthetic.main.fragment_camera.*
import kotlinx.android.synthetic.main.fragment_gallery.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.example.picgalleryapp.utils.helpers.observe


class CameraFragment : BindingFragment<FragmentCameraBinding>() {

    override val layoutId = R.layout.fragment_camera

    private val viewModel: CameraViewModel by viewModel()

    override fun onViewCreated() {
        super.onViewCreated()

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        setupObservers()
        setupCropFrame()
    }

    override fun onResume() {
        super.onResume()
        if (cameraFragmentCamera != null)
            cameraFragmentCamera.open()
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
        dragRect.setOnUpCallback(object : DragRectView.OnUpCallback {
            override fun onRectFinished(rect: Rect?, coors: IntArray) {
                viewModel.cropFrame = coors
            }
        })
    }

    private fun setupObservers() {

        observeError(viewModel.error)

        viewModel.takePhoto.observe(this) {
            cameraFragmentCamera.takePicture()
        }

        viewModel.photoSaved.observe(this) {
            activity?.onBackPressed()
        }

        viewModel.photoCropped.observe(this) {
            it?.let {  dragRect.drawEdges(it[0], it[1]) }
        }

        viewModel.loading.observe(this) {
            it?.let {
                galleryFragSwipeLayout.isEnabled = it
                galleryFragSwipeLayout.isRefreshing = it
            }
        }
    }
}
