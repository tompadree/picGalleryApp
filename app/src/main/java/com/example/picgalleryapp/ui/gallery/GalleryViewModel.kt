package com.example.picgalleryapp.ui.gallery

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.SystemClock
import android.util.Log
import androidx.lifecycle.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.ObjectKey
import com.example.picgalleryapp.data.models.ImageUri
import com.example.picgalleryapp.data.models.Result
import com.example.picgalleryapp.data.source.PicGalleryRepository
import com.example.picgalleryapp.utils.SingleLiveEvent
import com.example.picgalleryapp.data.models.Result.Success
import com.example.picgalleryapp.utils.helpers.ImageHelper
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

/**
 * @author Tomislav Curis
 */
class GalleryViewModel(
    private val context: Context,
    private val repository: PicGalleryRepository) : ViewModel() {

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    protected val _error = SingleLiveEvent<Throwable>()
    val error: LiveData<Throwable> get() = _error

    private val _page = MutableLiveData<Int>(0)

    val imageClick = SingleLiveEvent<String>()

    private val _items: LiveData<List<ImageUri>> =
        _page.switchMap { page ->
            viewModelScope.launch {
                repository.fetchPictures(true, 0, 50)
            }
            repository.observePictures(page).map {
                if (it is Success) {
                    _dataLoading.value = false
                    it.data
                } else if (it is Result.Error) {
                    _dataLoading.value = false
                    _error.postValue(it.exception)
                    emptyList()
                } else
                    emptyList()

            }
        }

    val items: LiveData<List<ImageUri>> = _items

    fun refresh() {
        _dataLoading.postValue(true)
        _page.postValue(0)
    }

    fun deleteImages(){
        viewModelScope.launch {
            repository.deletePics()
            Glide.get(context).clearMemory()
            refresh()
        }
    }

    fun handleGalleryPic(imageData: Intent){

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val file = Glide.with(context).downloadOnly().load(imageData.data)
                    .signature(ObjectKey(System.currentTimeMillis()))
                    .submit().get()
                ImageHelper.resizeImage(file, 512)
                repository.savePicture(file.toString())
            } catch (e: Exception) {
                Result.Error(e)
                e.printStackTrace()
            }
        }
    }

    fun imageClick(position: Int, uri: String){
        imageClick.postValue(uri)
    }

}