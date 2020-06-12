package com.example.picgalleryapp.gallery

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import com.example.picgalleryapp.data.models.ImageUri
import com.example.picgalleryapp.data.source.FakeRepository
import com.example.picgalleryapp.di.AppModule
import com.example.picgalleryapp.di.DataModule
import com.example.picgalleryapp.getOrAwaitValue
import com.example.picgalleryapp.observeForTesting
import com.example.picgalleryapp.ui.gallery.GalleryViewModel
import com.example.picgalleryapp.util.MainCoroutineRule
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule
import org.koin.test.inject
import com.google.common.truth.Truth.*

/**
 * @author Tomislav Curis
 */
class GalleryViewModelTest : KoinTest {

    // What is testing
    private lateinit var galleryViewModel: GalleryViewModel

    // Use a fake repository to be injected into the viewmodel
    private lateinit var repository: FakeRepository

    val context: Context by inject()
    val dispatchers: CoroutineDispatcher by inject()

    // Rule for koin injection
    @get:Rule
    val koinTestRule = KoinTestRule.create {
        modules(listOf(AppModule, DataModule))
    }

    // Set the main coroutines dispatcher for unit testing.
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setupViewModel() {
        repository = FakeRepository()
        val imageUri = ImageUri("Uri1")
        val imageUri2 = ImageUri("Uri2")
        val imageUri3 = ImageUri("Uri3")
        repository.currentListPics = mutableListOf(imageUri, imageUri2, imageUri3)

        galleryViewModel = GalleryViewModel(repository, context, dispatchers)
    }

    @Test
    fun loadAllReposToView() {
        // Pause dispatcher so we can verify initial values
        mainCoroutineRule.pauseDispatcher()

        // Trigger loading of repos
        galleryViewModel.refresh()

        // Observe the items to keep LiveData emitting
        galleryViewModel.items.observeForTesting {

            // Then progress indicator is shown
            Assert.assertThat(galleryViewModel.dataLoading.getOrAwaitValue()).isTrue()

            // Execute pending coroutines actions
            mainCoroutineRule.resumeDispatcher()

            // Then progress indicator is hidden
            Assert.assertThat(galleryViewModel.dataLoading.getOrAwaitValue()).isFalse()

            // And data correctly loaded
            Assert.assertThat(galleryViewModel.items.getOrAwaitValue()).hasSize(3)
        }
    }

    @Test
    fun fetchingReposGetError() {
        // Pause dispatcher so we can verify initial values
        mainCoroutineRule.pauseDispatcher()

        // Set repo return error
        repository.setReturnError(true)

        // StartFetching
        galleryViewModel.refresh(true)

        // Observe the items to keep LiveData emitting
        galleryViewModel.items.observeForTesting {

            // Loding
            Assert.assertThat(galleryViewModel.dataLoading.getOrAwaitValue()).isTrue()

            // Execute pending coroutines actions
            mainCoroutineRule.resumeDispatcher()

            // loading is done
            Assert.assertThat(galleryViewModel.dataLoading.getOrAwaitValue()).isFalse()

            // If isDataLoadingError response was error
            Assert.assertThat(galleryViewModel.isDataLoadingError.value).isEqualTo(true)
        }
    }

}