package com.udacity.project4.ui

import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import java.util.*
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import android.location.Address
import android.os.Bundle
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.google.android.gms.maps.model.LatLng
import com.google.common.truth.Truth.assertThat
import com.udacity.project4.R
import com.udacity.project4.datasource.local.ReminderDataSource
import com.udacity.project4.datasource.local.ReminderLocalDataSource
import com.udacity.project4.db.DB
import com.udacity.project4.feature.add.AddNewReminderFragment
import com.udacity.project4.feature.add.AddNewReminderFragmentDirections
import com.udacity.project4.feature.add.AddNewReminderViewModel
import com.udacity.project4.model.Point
import com.udacity.project4.repo.RemindersRepository
import com.udacity.project4.repo.RemindersRepositoryImpl
import com.udacity.project4.utils.DataBindingIdlingResource
import com.udacity.project4.utils.EspressoIdlingResource
import com.udacity.project4.utils.ToastMatcher
import com.udacity.project4.utils.monitorFragment
import com.udacity.project4.utils.safeNavigate

@ExperimentalCoroutinesApi
@MediumTest
@RunWith(AndroidJUnit4::class)
class AddNewReminderFragmentTest {
  private val dataBindingIdlingResource = DataBindingIdlingResource()
  private lateinit var viewModel: AddNewReminderViewModel


  @get:Rule
  val instantTaskExecutorRule = InstantTaskExecutorRule()

  @Before
  fun registerIdlingResources(): Unit = IdlingRegistry.getInstance().run {
    register(EspressoIdlingResource.countingIdlingResource)
    register(dataBindingIdlingResource)
  }

  @After
  fun unregisterIdlingResource(): Unit = IdlingRegistry.getInstance().run {
    unregister(EspressoIdlingResource.countingIdlingResource)
    unregister(dataBindingIdlingResource)
  }


  @Before
  fun setup() {
    stopKoin()
    val appModule = module {
      single {
        AddNewReminderViewModel(
          ApplicationProvider.getApplicationContext(),
          get() as RemindersRepository
        )
      }
      single<RemindersRepository> { RemindersRepositoryImpl(get() as ReminderDataSource) }
      single<ReminderDataSource> { ReminderLocalDataSource(get()) }
      single { DB.createReminderDatabase(ApplicationProvider.getApplicationContext()) }
    }

    startKoin {
      androidContext(ApplicationProvider.getApplicationContext())
      modules(listOf(appModule))
    }

    viewModel = GlobalContext.get().koin.get()
  }

  @Test
  fun addNewRemainderFragment_isDisplayedSnackBarAndNavigateMap() {
    val mockNavController = mock(NavController::class.java)
    val scenario =
      launchFragmentInContainer<AddNewReminderFragment>(
        Bundle(),
        R.style.AppTheme
      ).onFragment {
        Navigation.setViewNavController(it.view!!, mockNavController)
      }

    dataBindingIdlingResource.monitorFragment(scenario)
    scenario.onFragment {
      Navigation.setViewNavController(it.view!!, mockNavController)
    }

    onView(withId(R.id.fabSaveReminder)).perform(click())
    onView(withId(com.google.android.material.R.id.snackbar_text))
      .check(matches(withText(R.string.error_message_title_empty)))


    onView(withId(R.id.edTitle)).perform(typeText("TITLE"), closeSoftKeyboard())
    onView(withId(R.id.fabSaveReminder)).perform(click())
    onView(withId(com.google.android.material.R.id.snackbar_text))
      .check(matches(withText(R.string.error_message_description_empty)))


    onView(withId(R.id.edDescription)).perform(typeText("DESCRIPTION"), closeSoftKeyboard())
    onView(withId(R.id.fabSaveReminder)).perform(click())
    onView(withId(com.google.android.material.R.id.snackbar_text))
      .check(matches(withText(R.string.error_message_poi_empty)))

    onView(withId(R.id.btnLocation)).perform(click())
    verify(mockNavController).safeNavigate(AddNewReminderFragmentDirections.actionAddNewReminderToMapFragment())
    Thread.sleep(2000)
  }

  @Test
  fun validAndSaved_showToast() {
    val latlng = LatLng(1.0, 2.0)
    viewModel.updatePOI(Point(latlng, Address(Locale.ENGLISH)))
    val navController = mock(NavController::class.java)
    val scenario =
      launchFragmentInContainer<AddNewReminderFragment>(Bundle.EMPTY, R.style.AppTheme)
    dataBindingIdlingResource.monitorFragment(scenario)

    scenario.onFragment {
      Navigation.setViewNavController(it.view!!, navController)
    }
    onView(withId(R.id.edTitle)).perform(typeText("Title"))
    onView(withId(R.id.edDescription)).perform(typeText("Description"))
    Espresso.closeSoftKeyboard()
    onView(withId(R.id.fabSaveReminder)).perform(click())
    onView(withText(R.string.text_add_new_reminder_sucess)).inRoot(ToastMatcher())
      .check(matches(isDisplayed()))
  }
}