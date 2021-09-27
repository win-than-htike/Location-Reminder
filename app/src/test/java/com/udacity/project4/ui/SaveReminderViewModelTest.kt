package com.udacity.project4.ui

import android.location.Address
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import com.udacity.project4.MainCoroutineRule
import com.udacity.project4.R
import com.udacity.project4.feature.add.AddNewReminderViewModel
import com.udacity.project4.getOrAwaitValue
import com.udacity.project4.model.Point
import com.udacity.project4.source.FakeRepository
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
class SaveReminderViewModelTest {

  @get:Rule
  var instantExecutorRule = InstantTaskExecutorRule()

  @get:Rule
  val mainCoroutineRule = MainCoroutineRule()

  private lateinit var addReminderViewModel: AddNewReminderViewModel
  private lateinit var repository: FakeRepository


  @Before
  fun setup() {
    stopKoin()
    repository = FakeRepository()
    addReminderViewModel =
      AddNewReminderViewModel(ApplicationProvider.getApplicationContext(), repository)
  }

  @Test
  fun addNewReminder_setSavedReminderEvent() {
    addReminderViewModel.savedReminder()
    val value = addReminderViewModel.savedReminderEvent.getOrAwaitValue()
    assertThat(value, not(nullValue()))
  }

  @Test
  fun checkValidToSaveNewReminder_return_valid() {
    addReminderViewModel.title.value = "test title"
    addReminderViewModel.description.value = "test description"
    addReminderViewModel.updatePOI(
      Point(LatLng(0.0, 0.0), Address(Locale.ENGLISH))
    )
    val isValid = addReminderViewModel.isValidToSave()
    assertThat(isValid, `is`(true))
  }

  @Test
  fun checkValidToSaveNewReminder_return_notValid() {
    addReminderViewModel.title.value = "test title"
    addReminderViewModel.description.value = "test description"
    val isValid = addReminderViewModel.isValidToSave()
    assertThat(addReminderViewModel.showSnackBarInt.getOrAwaitValue(), not(nullValue()))
    assertThat(isValid, `is`(false))
  }

  @Test
  fun addNewReminder_dataAndSnackbarUpdated() = mainCoroutineRule.runBlockingTest {
    addReminderViewModel.title.value = "test title"
    addReminderViewModel.description.value = "test description"
    addReminderViewModel.savedReminder()

    val snackBarValue = addReminderViewModel.savedReminderEvent.getOrAwaitValue()
    assertThat(
      snackBarValue?.getContentIfNotHandled(),
      `is`(R.string.text_add_new_reminder_sucess)
    )
  }
}