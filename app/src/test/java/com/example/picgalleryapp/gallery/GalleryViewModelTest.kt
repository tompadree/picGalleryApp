package com.example.picgalleryapp.gallery

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.picgalleryapp.data.models.ImageUri
import com.example.picgalleryapp.data.source.FakeRepository
import com.example.picgalleryapp.di.AppModule
import com.example.picgalleryapp.di.DataModule
import com.example.picgalleryapp.getOrAwaitValue
import com.example.picgalleryapp.observeForTesting
import com.example.picgalleryapp.ui.gallery.GalleryViewModel
import com.example.picgalleryapp.util.MainCoroutineRule
import kotlinx.coroutines.CoroutineDispatcher
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule
import org.koin.test.inject
import com.google.common.truth.Truth.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

/**
 * @author Tomislav Curis
 */

@RunWith(MockitoJUnitRunner::class)
@ExperimentalCoroutinesApi
class GalleryViewModelTest : KoinTest {

    // What is testing
    private lateinit var galleryViewModel: GalleryViewModel

    // Use a fake repository to be injected into the viewmodel
    private lateinit var repository: FakeRepository

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

    private val imageUri = ImageUri("Uri1")
    private val imageUri2 = ImageUri("Uri2")
    private val imageUri3 = ImageUri("Uri3")

    @Mock
    private lateinit var mockContext: Context

    @Before
    fun setupViewModel() {
        repository = FakeRepository()

        repository.currentListPics = mutableListOf(imageUri, imageUri2, imageUri3)

        galleryViewModel = GalleryViewModel(mockContext, repository, dispatchers)
    }

    @Test
    fun loadAllImagesToView() {
        // Pause dispatcher so we can verify initial values
        mainCoroutineRule.pauseDispatcher()

        // Trigger loading of images
        galleryViewModel.refresh()

        // Then progress indicator is shown
        assertThat(galleryViewModel.dataLoading.getOrAwaitValue()).isTrue()

        // Observe the items to keep LiveData emitting
        galleryViewModel.items.observeForTesting {

            // Execute pending coroutines actions
            mainCoroutineRule.resumeDispatcher()

            // Then progress indicator is hidden
            assertThat(galleryViewModel.dataLoading.getOrAwaitValue()).isFalse()

            // And data correctly loaded
            assertThat(galleryViewModel.items.getOrAwaitValue()).hasSize(3)
        }
    }

    @Test
    fun deleteImages() {

        // assert that list is filled
        assertThat(repository.currentListPics.contains(imageUri)).isTrue()

        // When the deletion of a images is requested
        galleryViewModel.deleteImages()

        // Observe the items to keep LiveData emitting
        galleryViewModel.items.observeForTesting {

            // Execute pending coroutines actions
            mainCoroutineRule.resumeDispatcher()

            // loading is done
            assertThat(galleryViewModel.dataLoading.getOrAwaitValue()).isFalse()

            // Assert that images is empty
            assertThat(repository.currentListPics.contains(imageUri)).isFalse()
        }
    }

    @Test
    fun fetchingImagesGetError() {
        // Pause dispatcher so we can verify initial values
        mainCoroutineRule.pauseDispatcher()

        // Set images return error
        repository.setReturnError(true)

        // StartFetching
        galleryViewModel.refresh()

        // Loading
        assertThat(galleryViewModel.dataLoading.getOrAwaitValue()).isTrue()

        // Observe the items to keep LiveData emitting
        galleryViewModel.items.observeForTesting {

            // Execute pending coroutines actions
            mainCoroutineRule.resumeDispatcher()

            // loading is done
            assertThat(galleryViewModel.dataLoading.getOrAwaitValue()).isFalse()

            // If isDataLoadingError response was error
            assertThat(galleryViewModel.error.value).isInstanceOf(Exception::class.java)
        }
    }

}