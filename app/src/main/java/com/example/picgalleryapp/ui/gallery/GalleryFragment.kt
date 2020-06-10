package com.example.picgalleryapp.ui.gallery


import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator

import com.example.picgalleryapp.R
import com.example.picgalleryapp.databinding.FragmentGalleryBinding
import com.example.picgalleryapp.ui.BindingFragment
import com.example.picgalleryapp.ui.PicGalleryActivity
import kotlinx.android.synthetic.main.fragment_gallery.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.example.picgalleryapp.utils.helpers.observe

/**
 * A simple [Fragment] subclass.
 */
class GalleryFragment : BindingFragment<FragmentGalleryBinding>() {

    override val layoutId = R.layout.fragment_gallery

    private val viewModel: GalleryViewModel by viewModel()

    private lateinit var galleryAdapter: GalleryAdapter

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

        viewModel.refresh(true)
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

        observeError(viewModel.error)

        viewModel.empty.observe(this) {}

    }

    fun setupRV(){
        galleryAdapter = GalleryAdapter(viewModel)

        with(galleryFragRv) {
            layoutManager = GridLayoutManager(context, 2)
//            layoutManager = LinearLayoutManager(context)
            adapter = galleryAdapter

            // For testing
            (this.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false


            // Set the number of offscreen views to retain before adding them
            // to the potentially shared recycled view pool
            setItemViewCacheSize(100)
        }

    }

    private fun setupNavigation(){


    }

    private fun openCamera(){
        val nc = NavHostFragment.findNavController(this)
        nc.navigate(GalleryFragmentDirections.actionGalleryFragmentToCameraFragment())
    }




}
