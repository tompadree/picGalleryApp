package com.example.picgalleryapp.ui.gallery

import androidx.lifecycle.*
import com.example.picgalleryapp.data.models.ImageUri
import com.example.picgalleryapp.data.source.PicGalleryRepository
import com.example.picgalleryapp.utils.SingleLiveEvent
import com.example.picgalleryapp.data.models.Result.Success
import kotlinx.coroutines.launch

/**
 * @author Tomislav Curis
 */
class GalleryViewModel(private val repository: PicGalleryRepository) : ViewModel() {

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    protected val _error = SingleLiveEvent<Throwable>()
    val error: LiveData<Throwable> get() = _error

    private val _page = MutableLiveData<Int>(0)

    private val _items: LiveData<List<ImageUri>> =
        _page.switchMap { page ->
            repository.observePictures(page).map {
                if (it is Success) {
                    it.data
                } else {
                    emptyList()
                }
            }
        }

    val items: LiveData<List<ImageUri>> = _items

    // This LiveData depends on another so we can use a transformation.
    val empty: LiveData<Boolean> = Transformations.map(_items) {
        it.isEmpty()
    }

    fun refresh(refresh: Boolean) {
        _page.value = 0
    }


}