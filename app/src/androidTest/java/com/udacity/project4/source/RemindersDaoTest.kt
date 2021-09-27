package com.udacity.project4.source

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import com.udacity.project4.TestModelUtils
import com.udacity.project4.db.AppDatabase
import com.udacity.project4.db.RemindersDao
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class RemindersDaoTest {

  private lateinit var database: AppDatabase
  private lateinit var remainderDao: RemindersDao

  private val reminderData = TestModelUtils.getTestReminder()

  @get:Rule
  val instantTaskExecutorRule = InstantTaskExecutorRule()

  @Before
  fun initDatabase() {
    database = Room.inMemoryDatabaseBuilder(
      ApplicationProvider.getApplicationContext(),
      AppDatabase::class.java
    ).build()

    remainderDao = database.remindersDao()
  }

  @After
  fun closeDatabase() {
    database.close()
  }

  @Test
  fun saveRemainderToDatabase() = runBlockingTest {
    remainderDao.insertRemainder(reminderData)
    val list = remainderDao.getRemainders()
    assertThat(list.isEmpty()).isFalse()
    assertThat(list).contains(reminderData)
  }

  @Test
  fun retrieveFromDBSucceeds() = runBlockingTest {
    remainderDao.insertRemainder(reminderData)
    val reminder = remainderDao.getRemainderById(reminderData.id)
    assertThat(reminder).isNotNull()
    assertThat(reminder?.title).isEqualTo(reminderData.title)
    assertThat(reminder?.description).isEqualTo(reminderData.description)
    assertThat(reminder?.latitude).isEqualTo(reminderData.latitude)
    assertThat(reminder?.longitude).isEqualTo(reminderData.longitude)
  }

  @Test
  fun deleteRemainderFromDatabase() = runBlockingTest {
    remainderDao.insertRemainder(reminderData)
    val list = remainderDao.getRemainders()
    assertThat(list.isEmpty()).isFalse()
    remainderDao.deleteAllRemainders()
    assertThat(remainderDao.getRemainders()).isEmpty()
  }
}