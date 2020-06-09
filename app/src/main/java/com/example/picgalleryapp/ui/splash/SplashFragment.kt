package com.example.picgalleryapp.ui.splash


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.NavHostFragment
import com.example.picgalleryapp.R
import com.example.picgalleryapp.utils.AppConstants
import com.example.picgalleryapp.utils.helpers.delay

class SplashFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        launchCurrencies()
    }

    private fun launchCurrencies() {
        delay(AppConstants.SPLASH_DISPLAY_LENGTH) {
            navigateToGallery()
        }
    }

    private fun navigateToGallery() {
        val nc = NavHostFragment.findNavController(this)
        nc.navigate(SplashFragmentDirections.actionSplashFragmentToGalleryFragment())
    }


}
