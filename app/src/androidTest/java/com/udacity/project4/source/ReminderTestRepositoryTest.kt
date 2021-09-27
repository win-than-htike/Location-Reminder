package com.udacity.project4.source

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.TestModelUtils
import com.udacity.project4.datasource.local.ReminderLocalDataSource
import com.udacity.project4.db.AppDatabase
import com.udacity.project4.model.Reminder
import com.udacity.project4.repo.RemindersRepository
import com.udacity.project4.repo.RemindersRepositoryImpl
import com.udacity.project4.utils.MainCoroutineRule
import com.udacity.project4.utils.Result

@MediumTest
@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class ReminderTestRepositoryTest {

  private lateinit var reminderRepository: RemindersRepository
  private lateinit var database: AppDatabase

  @get:Rule
  val instantTaskExecutorRule = InstantTaskExecutorRule()

  @get:Rule
  val mainCoroutineRule = MainCoroutineRule()

  @Before
  fun initDatabase() {
    database = Room.inMemoryDatabaseBuilder(
      ApplicationProvider.getApplicationContext(),
      AppDatabase::class.java
    ).allowMainThreadQueries().build()

    val dataSource = ReminderLocalDataSource(database.remindersDao())
    reminderRepository = RemindersRepositoryImpl(dataSource)
  }

  @Test
  fun saveReminderSuccess() = runBlocking {
    reminderRepository.saveReminder(TestModelUtils.getTestReminder())
    val result = reminderRepository.getReminderById(TestModelUtils.getTestReminder().id)
    assertThat(result is Result.Success, `is`(true))
    result as Result.Success
    assertThat(result.data.id, `is`(TestModelUtils.getTestReminder().id))
    assertThat(result.data.title, `is`(notNullValue()))
    assertThat(result.data.description, `is`(notNullValue()))
    assertThat(result.data.latitude, `is`(notNullValue()))
    assertThat(result.data.longitude, `is`(notNullValue()))
  }


  @Test
  fun saveReminder_return_notEmptyList() = runBlocking {
    reminderRepository.saveReminder(TestModelUtils.getTestReminder())
    val result = reminderRepository.getReminders()
    val list = reminderRepository.getReminders() as Result.Success<List<Reminder>>
    assertThat(list.data.isNotEmpty(), `is`(true))
  }

  @Test
  fun saveReminder_retrieveWithId_return_notNull() = runBlocking {
    reminderRepository.saveReminder(TestModelUtils.getTestReminder())
    val list = reminderRepository.getReminderById("1")
    assertThat(list, `is`(notNullValue()))
  }

  @Test
  fun saveReminder_andDeleteReminder_return_EmptyList() = runBlocking {
    reminderRepository.saveReminder(TestModelUtils.getTestReminder())
    val list = reminderRepository.getReminders() as Result.Success<List<Reminder>>
    assertThat(list.data.isNotEmpty(), `is`(true))
    reminderRepository.deleteReminder(TestModelUtils.getTestReminder())
    val savedList = reminderRepository.getReminders() as Result.Success<List<Reminder>>
    assertThat(savedList.data.isEmpty(), `is`(true))
  }
}
