package com.udacity.project4.ui

import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.R.id
import com.udacity.project4.R.style
import com.udacity.project4.TestAndroidModelUtils
import com.udacity.project4.datasource.local.ReminderDataSource
import com.udacity.project4.datasource.local.ReminderLocalDataSource
import com.udacity.project4.db.DB
import com.udacity.project4.db.RemindersDao
import com.udacity.project4.feature.detail.ReminderDetailFragment
import com.udacity.project4.feature.detail.ReminderDetailViewModel
import com.udacity.project4.repo.RemindersRepository
import com.udacity.project4.repo.RemindersRepositoryImpl
import com.udacity.project4.utils.DataBindingIdlingResource
import com.udacity.project4.utils.GeofenceUtils
import com.udacity.project4.utils.monitorFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.mockito.Mockito

@ExperimentalCoroutinesApi
@MediumTest
@RunWith(AndroidJUnit4::class)
class RemainderDetailFragmentTest {

  private lateinit var repository: RemindersRepository
  private val dataBindingIdlingResource = DataBindingIdlingResource()

  @Before
  fun setup() {
    stopKoin()
    val appModule = module {

      viewModel {
        ReminderDetailViewModel(
          get() as RemindersRepository
        )
      }
      single {
        DB.createReminderDatabase(ApplicationProvider.getApplicationContext())
      }
      single<RemindersRepository> { RemindersRepositoryImpl(get() as ReminderDataSource) }
      single<ReminderDataSource> { ReminderLocalDataSource(get() as RemindersDao) }
    }
    startKoin {
      androidContext(ApplicationProvider.getApplicationContext())
      modules(listOf(appModule))
    }
    repository = GlobalContext.get().koin.get()
  }


  @Test
  fun remainderDetails_DisplayedInUi() = runBlocking {
    val remainder = TestAndroidModelUtils.getTestReminder()
    repository.saveReminder(remainder)
    val bundle = Bundle()
    bundle.putString(GeofenceUtils.GEOFENCE_EXTRA, remainder.id)
    val scenario = launchFragmentInContainer<ReminderDetailFragment>(bundle, style.AppTheme)

    val navController = Mockito.mock(NavController::class.java)
    dataBindingIdlingResource.monitorFragment(scenario)

    scenario.onFragment {
      Navigation.setViewNavController(it.view!!, navController)
    }

    Espresso.onView(ViewMatchers.withId(id.tvTitleValue))
      .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    Espresso.onView(ViewMatchers.withId(id.tvTitleValue))
      .check(ViewAssertions.matches(withText(remainder.title)))

    Espresso.onView(ViewMatchers.withId(id.tvDescriptionValue))
      .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    Espresso.onView(ViewMatchers.withId(id.tvDescriptionValue))
      .check(ViewAssertions.matches(withText(remainder.description)))

    Espresso.onView(ViewMatchers.withId(id.tvPlaceValue))
      .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    Espresso.onView(ViewMatchers.withId(id.tvPlaceValue))
      .check(ViewAssertions.matches(withText(remainder.place)))

    Espresso.onView(ViewMatchers.withId(id.tvLatLngValue))
      .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    Espresso.onView(ViewMatchers.withId(id.tvLatLngValue))
      .check(ViewAssertions.matches(ViewMatchers.withText("${remainder.latitude}, ${remainder.longitude}")))
    Thread.sleep(2000)
  }
}