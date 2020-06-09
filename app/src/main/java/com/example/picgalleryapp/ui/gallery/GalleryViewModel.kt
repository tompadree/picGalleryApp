package com.example.picgalleryapp.ui.gallery

import androidx.lifecycle.*
import com.example.picgalleryapp.utils.SingleLiveEvent

/**
 * @author Tomislav Curis
 */
class GalleryViewModel : ViewModel() {

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    protected val _error = SingleLiveEvent<Throwable>()
    val error: LiveData<Throwable> get() = _error


    fun refresh(refresh: Boolean){

    }
}