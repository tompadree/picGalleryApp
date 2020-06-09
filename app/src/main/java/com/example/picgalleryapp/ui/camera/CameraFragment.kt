package com.example.picgalleryapp.ui.camera

import org.koin.androidx.viewmodel.ext.android.viewModel
import com.example.picgalleryapp.R
import com.example.picgalleryapp.databinding.FragmentCameraBinding
import com.example.picgalleryapp.ui.BindingFragment
import kotlinx.android.synthetic.main.fragment_camera.*

class CameraFragment : BindingFragment<FragmentCameraBinding>() {

    override val layoutId = R.layout.fragment_camera

    private val viewModel: CameraViewModel by viewModel()

    override fun onViewCreated() {
        super.onViewCreated()

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        setupObservers()

    }

    override fun onResume() {
        super.onResume()
        if(cameraFragmentCamera != null)
            cameraFragmentCamera.open()
    }

    override fun onPause() {
        super.onPause()
        if(cameraFragmentCamera != null)
            cameraFragmentCamera.close()
    }

    override fun onDestroy() {
        super.onDestroy()
        if(cameraFragmentCamera != null)
            cameraFragmentCamera.destroy()
    }

    private fun setupObservers(){

        viewModel.takePhoto.observe(this){
            cameraFragmentCamera.takePicture()
        }
    }


}
