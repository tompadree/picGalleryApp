package com.example.picgalleryapp.camera

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.picgalleryapp.data.models.ImageUri
import com.example.picgalleryapp.data.source.FakeRepository
import com.example.picgalleryapp.di.AppModule
import com.example.picgalleryapp.di.DataModule
import com.example.picgalleryapp.getOrAwaitValue
import com.example.picgalleryapp.observeForTesting
import com.example.picgalleryapp.ui.camera.CameraViewModel
import com.example.picgalleryapp.ui.gallery.GalleryViewModel
import com.example.picgalleryapp.util.MainCoroutineRule
import com.google.common.truth.Truth
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule
import org.koin.test.inject
import java.io.File
import com.google.common.truth.Truth.*

/**
 * @author Tomislav Curis
 */

@ExperimentalCoroutinesApi
class CameraViewModelTest : KoinTest {

    // What is testing
    private lateinit var cameraViewModel: CameraViewModel

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


    @Before
    fun setupViewModel() {
        repository = FakeRepository()
//        val imageUri = ImageUri("Uri1")
//        val imageUri2 = ImageUri("Uri2")
//        val imageUri3 = ImageUri("Uri3")
//        repository.currentListPics = mutableListOf(imageUri, imageUri2, imageUri3)

        cameraViewModel = CameraViewModel(repository)
    }

    @Test
    fun saveImageFromaCamera() {
        // Pause dispatcher so we can verify initial values
        mainCoroutineRule.pauseDispatcher()

        // Set photo observable
        cameraViewModel.photoFile.set(
            File.createTempFile(
                "/data/user/0/com.example.picgalleryapp/cache/image_manager_disk_cache/bd74a2e2b3f9c627dedc0d88a0dad5c9936406802a73c63516ca35492a6612c3",
                ".0"
            )
        )

        // Trigger saving of image
        cameraViewModel.saveRetake(true)

        // Execute pending coroutines actions
        mainCoroutineRule.resumeDispatcher()

        // Photo is set null after saving
        assertThat(cameraViewModel.photoFile.get()).isNull()
    }
}

//    @Test
//    fun saveImageGetError() {
//        // Pause dispatcher so we can verify initial values
//        mainCoroutineRule.pauseDispatcher()
//
//        // Set images return error
//        repository.setReturnError(true)
//
//        // Set photo observable
//        cameraViewModel.photoFile.set(
//            File.createTempFile(
//                "/data/user/0/com.example.picgalleryapp/cache/image_manager_disk_cache/bd74a2e2b3f9c627dedc0d88a0dad5c9936406802a73c63516ca35492a6612c3", ".0"))
//
//        // Trigger saving of image
//        cameraViewModel.saveRetake(true)
//
//        // Execute pending coroutines actions
//        mainCoroutineRule.resumeDispatcher()
//
//        assertThat(cameraViewModel.error.value).isInstanceOf(Exception::class.java)

//        // Loading
//        Truth.assertThat(galleryViewModel.dataLoading.getOrAwaitValue()).isTrue()
//
//        // Observe the items to keep LiveData emitting
//        galleryViewModel.items.observeForTesting {
//
//            // Execute pending coroutines actions
//            mainCoroutineRule.resumeDispatcher()
//
//            // loading is done
//            Truth.assertThat(galleryViewModel.dataLoading.getOrAwaitValue()).isFalse()
//
//            // If isDataLoadingError response was error
//            Truth.assertThat(galleryViewModel.error.value).isInstanceOf(Exception::class.java)
//        }
//    }
//}