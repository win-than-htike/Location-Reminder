package me.onething.locationreminder.ui

import android.location.Address
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import me.onething.locationreminder.MainCoroutineRule
import me.onething.locationreminder.R
import me.onething.locationreminder.feature.add.AddNewRemainderViewModel
import me.onething.locationreminder.getOrAwaitValue
import me.onething.locationreminder.model.Point
import me.onething.locationreminder.source.FakeRepository
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import java.util.Locale

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class AddRemainderViewModelTest {

  @get:Rule
  var instantExecutorRule = InstantTaskExecutorRule()

  @get:Rule
  val mainCoroutineRule = MainCoroutineRule()

  private lateinit var addRemainderViewModel: AddNewRemainderViewModel
  private lateinit var repository: FakeRepository


  @Before
  fun setup() {
    stopKoin()
    repository = FakeRepository()
    addRemainderViewModel =
      AddNewRemainderViewModel(ApplicationProvider.getApplicationContext(), repository)
  }

  @Test
  fun addNewRemainder_setSavedRemainderEvent() {
    addRemainderViewModel.savedRemainder()
    val value = addRemainderViewModel.savedRemainderEvent.getOrAwaitValue()
    assertThat(value, not(nullValue()))
  }

  @Test
  fun checkValidToSaveNewRemainder_return_valid() {
    addRemainderViewModel.title.value = "test title"
    addRemainderViewModel.description.value = "test description"
    addRemainderViewModel.updatePOI(
      Point(LatLng(0.0, 0.0), Address(Locale.ENGLISH))
    )
    val isValid = addRemainderViewModel.isValidToSave()
    assertThat(isValid, `is`(true))
  }

  @Test
  fun checkValidToSaveNewRemainder_return_notValid() {
    addRemainderViewModel.title.value = "test title"
    addRemainderViewModel.description.value = "test description"
    val isValid = addRemainderViewModel.isValidToSave()
    assertThat(addRemainderViewModel.showSnackBarInt.getOrAwaitValue(), not(nullValue()))
    assertThat(isValid, `is`(false))
  }

  @Test
  fun addNewRemainder_dataAndSnackbarUpdated() = mainCoroutineRule.runBlockingTest {
    addRemainderViewModel.title.value = "test title"
    addRemainderViewModel.description.value = "test description"
    addRemainderViewModel.savedRemainder()

    val snackBarValue = addRemainderViewModel.savedRemainderEvent.getOrAwaitValue()
    assertThat(
      snackBarValue?.getContentIfNotHandled(),
      `is`(R.string.text_add_new_remainder_sucess)
    )
  }
}