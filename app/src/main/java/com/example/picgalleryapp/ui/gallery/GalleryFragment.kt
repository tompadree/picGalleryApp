package com.example.picgalleryapp.ui.gallery


import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment

import com.example.picgalleryapp.R
import com.example.picgalleryapp.databinding.FragmentGalleryBinding
import com.example.picgalleryapp.ui.BindingFragment
import com.example.picgalleryapp.ui.PicGalleryActivity
import kotlinx.android.synthetic.main.fragment_gallery.*
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * A simple [Fragment] subclass.
 */
class GalleryFragment : BindingFragment<FragmentGalleryBinding>() {

    override val layoutId = R.layout.fragment_gallery

    private val viewModel: GalleryViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this.viewLifecycleOwner


        setupObservers()
        setupRV()

    }

    override fun onResume() {
        super.onResume()
        if(activity is PicGalleryActivity){
            (activity as PicGalleryActivity).setSupportActionBar(galleryFragToolbar)
            setHasOptionsMenu(true)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.gallery_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when(item.itemId){
            R.id.menuTakePhoto -> {
                openCamera()
                true
            }
            R.id.menuPickImage -> {
                true
            }
            else -> false
        }

    fun setupObservers() {


    }

    fun setupRV(){


    }

    private fun setupNavigation(){


    }

    private fun openCamera(){
        val nc = NavHostFragment.findNavController(this)
        nc.navigate(GalleryFragmentDirections.actionGalleryFragmentToCameraFragment())
    }




}
