package com.udacity.project4.source

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import com.udacity.project4.TestModelUtils
import com.udacity.project4.model.Reminder
import com.udacity.project4.repo.RemindersRepository
import com.udacity.project4.repo.RemindersRepositoryImpl
import com.udacity.project4.utils.Result
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.notNullValue
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class ReminderRepositoryTest {

  private lateinit var fakeDataSource: FakeDataSource
  private lateinit var reminderRepository: RemindersRepository

  @Before
  fun init() {
    fakeDataSource = FakeDataSource()
    reminderRepository = RemindersRepositoryImpl(fakeDataSource)
  }

  @Test
  fun saveReminder_return_notEmptyList() = runBlockingTest {
    reminderRepository.saveReminder(TestModelUtils.getTestReminder())
    val list = reminderRepository.getReminders() as Result.Success<List<Reminder>>
    assertThat(list.data.isNotEmpty(), `is`(true))
  }

  @Test
  fun saveReminder_retrieveWithId_return_notNull() = runBlockingTest {
    reminderRepository.saveReminder(TestModelUtils.getTestReminder())
    val list = reminderRepository.getReminderById("1")
    assertThat(list, `is`(notNullValue()))
  }

  @Test
  fun saveReminder_andDeleteReminder_return_EmptyList() = runBlockingTest {
    reminderRepository.saveReminder(TestModelUtils.getTestReminder())
    val list = reminderRepository.getReminders() as Result.Success<List<Reminder>>
    assertThat(list.data.isEmpty(), `is`(false))
    reminderRepository.deleteReminder(TestModelUtils.getTestReminder())
    val savedList = reminderRepository.getReminders() as Result.Success<List<Reminder>>
    assertThat(savedList.data.isEmpty(), `is`(true))
  }


  @Test
  fun returnNullForReminderById_whenError() = runBlockingTest {
    fakeDataSource.setShouldReturnError(true)
    val reminder = reminderRepository.getReminderById("1")
    assertThat(reminder is Result.Error, `is`(true))
  }

}