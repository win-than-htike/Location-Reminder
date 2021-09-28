package com.udacity.project4.ui

import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.util.HumanReadables
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.google.android.material.R
import com.udacity.project4.R.id
import com.udacity.project4.R.string
import com.udacity.project4.R.style
import com.udacity.project4.TestAndroidModelUtils
import com.udacity.project4.datasource.local.ReminderDataSource
import com.udacity.project4.datasource.local.ReminderLocalDataSource
import com.udacity.project4.db.DB
import com.udacity.project4.db.RemindersDao
import com.udacity.project4.feature.list.RemindersFragment
import com.udacity.project4.feature.list.RemindersFragmentDirections
import com.udacity.project4.feature.list.RemindersViewModel
import com.udacity.project4.repo.RemindersRepository
import com.udacity.project4.repo.RemindersRepositoryImpl
import com.udacity.project4.utils.DataBindingIdlingResource
import com.udacity.project4.utils.RecyclerViewAction
import com.udacity.project4.utils.monitorFragment
import com.udacity.project4.utils.safeNavigate
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import org.junit.Before
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.RunWith
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.mockito.Mockito
import java.lang.AssertionError

@MediumTest
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class RemaindersFragmentTest : TestWatcher() {

  private lateinit var repository: RemindersRepository
  private val dataBindingIdlingResource = DataBindingIdlingResource()


  @Before
  fun setup() {
    stopKoin()
    val appModule = module {
      viewModel {
        RemindersViewModel(
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
  fun clickTask_navigateToDetailFragment() = runBlocking {
    val navController = Mockito.mock(NavController::class.java)
    val remainder = TestAndroidModelUtils.getTestReminder()
    repository.saveReminder(remainder)
    val scenario =
      launchFragmentInContainer<RemindersFragment>(
        Bundle.EMPTY,
        style.AppTheme
      ).onFragment {
        Navigation.setViewNavController(it.view!!, navController)
      }
    dataBindingIdlingResource.monitorFragment(scenario)
    scenario.onFragment {
      Navigation.setViewNavController(it.view!!, navController)
    }
    Espresso.onView(withId(id.rvReminders))
      .perform(
        RecyclerViewActions.actionOnItemAtPosition<ViewHolder>(0, ViewActions.click())
      )
    Mockito.verify(navController).safeNavigate(
      RemindersFragmentDirections.actionRemindersFragmentToReminderDetailFragment(
        remainder.id
      )
    )
  }

  @Test
  fun clickTask_navigateToAddNewRemainderFragment() = runBlocking {
    val navController = Mockito.mock(NavController::class.java)
    val remainder = TestAndroidModelUtils.getTestReminder()
    repository.saveReminder(remainder)
    val scenario =
      launchFragmentInContainer<RemindersFragment>(Bundle(), style.AppTheme).onFragment {
        Navigation.setViewNavController(it.view!!, navController)
      }
    dataBindingIdlingResource.monitorFragment(scenario)
    scenario.onFragment {
      Navigation.setViewNavController(it.view!!, navController)
    }
    Espresso.onView(withId(id.fabAddReminder)).perform(ViewActions.click())
    Mockito.verify(navController).safeNavigate(
      RemindersFragmentDirections.actionRemindersFragmentToAddNewReminder()
    )
  }


  @Test
  fun delete_remainder_FromRecyclerView_Show_SnackBar() = runBlocking {
    val navController = Mockito.mock(NavController::class.java)
    withTimeoutOrNull(1300L) {
      repository.saveReminder(TestAndroidModelUtils.getTestReminder())
    }
    val scenario =
      launchFragmentInContainer<RemindersFragment>(Bundle(), style.AppTheme).onFragment {
        Navigation.setViewNavController(it.view!!, navController)
      }
    dataBindingIdlingResource.monitorFragment(scenario)
    scenario.onFragment {
      Navigation.setViewNavController(it.view!!, navController)
    }
    Espresso.onView(withId(id.rvReminders)).perform(
      RecyclerViewActions.actionOnItemAtPosition<ViewHolder>(
        0,
        RecyclerViewAction.clickChildViewWithId(id.ivDelete)
      )
    )
    Espresso.onView(ViewMatchers.withId(R.id.snackbar_text))
      .check(ViewAssertions.matches(ViewMatchers.withText(string.reminder_deleted)))
    Unit
  }

  @Test
  fun loading_showUI() {
    val navController = Mockito.mock(NavController::class.java)
    val scenario =
      launchFragmentInContainer<RemindersFragment>(Bundle(), style.AppTheme).onFragment {
        Navigation.setViewNavController(it.view!!, navController)
      }
    dataBindingIdlingResource.monitorFragment(scenario)
    scenario.onFragment {
      Navigation.setViewNavController(it.view!!, navController)
    }
    Espresso.onView(ViewMatchers.withId(id.progressBar)).check(isNotDisplayed())
  }

}

fun isNotDisplayed(): ViewAssertion {
  return ViewAssertion { view, noView ->
    if (view != null && ViewMatchers.isDisplayed().matches(view)) {
      throw AssertionError(
        "View is present in the hierarchy and Displayed: "
          + HumanReadables.describe(view)
      )
    }
  }
}