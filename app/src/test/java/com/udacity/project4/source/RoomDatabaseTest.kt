package com.udacity.project4.source

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import com.udacity.project4.TestModelUtils
import com.udacity.project4.db.AppDatabase
import com.udacity.project4.db.RemindersDao
import com.udacity.project4.model.Reminder
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.notNullValue
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import java.io.IOException

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class RoomDatabaseTest {
  private lateinit var reminderDao: RemindersDao
  private lateinit var remindersDatabase: AppDatabase

  @get:Rule
  var instantExecutorRule = InstantTaskExecutorRule()


  @Before
  fun createDb() {
    stopKoin()
    val context = ApplicationProvider.getApplicationContext<Context>()
    remindersDatabase = Room.inMemoryDatabaseBuilder(
      context, AppDatabase::class.java
    ).allowMainThreadQueries().build()
    reminderDao = remindersDatabase.remindersDao()
  }


  @After
  @Throws(IOException::class)
  fun closeDb() {
    remindersDatabase.close()
  }

  @Test
  fun insertTaskAndGetById() = runBlockingTest {
    // GIVEN - Insert a task.
    val reminder = TestModelUtils.getTestReminder()
    reminderDao.insertReminder(reminder)

    // WHEN - Get the task by id from the database.
    val loadedReminder = reminderDao.getReminderById(reminder.id)

    // THEN - The loaded data contains the expected values.
    assertThat(loadedReminder as Reminder, notNullValue())
    assertThat(loadedReminder.id, `is`(reminder.id))
    assertThat(loadedReminder.title, `is`(reminder.title))
    assertThat(loadedReminder.description, `is`(reminder.description))
    assertThat(loadedReminder.latitude, `is`(reminder.latitude))
    assertThat(loadedReminder.longitude, `is`(reminder.longitude))
    assertThat(loadedReminder.place, `is`(reminder.place))
  }


  @Test
  @Throws(Exception::class)
  fun saveReminder_read_returnEqual() = runBlocking {
    val reminder: Reminder = TestModelUtils.getTestReminder()
    reminderDao.insertReminder(reminder)
    val byPlaceId = reminderDao.getReminderById(reminder.id)
    assertThat(byPlaceId, equalTo(reminder))
  }


  @Test
  @Throws(Exception::class)
  fun saveReminder_readList_returnNotEmpty() = runBlockingTest {
    val reminder: Reminder = TestModelUtils.getTestReminder()
    reminderDao.insertReminder(reminder)
    val reminders = reminderDao.getReminders()
    assertThat(reminders.isEmpty(), `is`(false))
  }


  @Test
  @Throws(Exception::class)
  fun deleteAllReminders_should_return_empty() = runBlocking(Dispatchers.IO) {
    val reminder: Reminder = TestModelUtils.getTestReminder()
    reminderDao.insertReminder(reminder)
    val deleted = reminderDao.deleteAllReminders()
    assertThat(deleted, `is`(1))
  }

  @Test
  fun insertReminderAndUpdateGetId() = runBlockingTest {
    val reminder = TestModelUtils.getTestReminder()
    reminderDao.insertReminder(reminder)
    reminderDao.updateReminder(reminder.copy(title = "Updated"))
    val updatedReminder = reminderDao.getReminderById((reminder.id))
    assertThat(updatedReminder?.title, `is`("Updated"))
  }
}
