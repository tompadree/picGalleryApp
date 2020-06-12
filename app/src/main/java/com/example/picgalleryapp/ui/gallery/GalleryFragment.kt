package com.example.picgalleryapp.ui.gallery

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.example.picgalleryapp.R
import com.example.picgalleryapp.databinding.FragmentGalleryBinding
import com.example.picgalleryapp.ui.BindingFragment
import com.example.picgalleryapp.ui.PicGalleryActivity
import kotlinx.android.synthetic.main.fragment_gallery.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.IOException


/**
 * A simple [Fragment] subclass.
 */
class GalleryFragment : BindingFragment<FragmentGalleryBinding>() {

    override val layoutId = R.layout.fragment_gallery

    private val viewModel: GalleryViewModel by viewModel()

    private lateinit var galleryAdapter: GalleryAdapter

    private val SELECT_PICTURE = 1234

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

        viewModel.refresh()
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
                openChooser()
                true
            }
            R.id.menuDeleteImages -> {
                viewModel.deleteImages()
                true
            }
            else -> false
        }

    fun setupObservers() {

        observeError(viewModel.error)

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

    private fun openCamera(){
        val nc = NavHostFragment.findNavController(this)
        nc.navigate(GalleryFragmentDirections.actionGalleryFragmentToCameraFragment())
    }

    private fun openChooser(){
        val chooseFile =  Intent()
        chooseFile.action = Intent.ACTION_GET_CONTENT
        chooseFile.type = "image/*"
        startActivityForResult(Intent.createChooser(chooseFile, "Select picture"), SELECT_PICTURE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == SELECT_PICTURE) {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    data?.let { viewModel.handleGalleryPic(data) }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }


}
