package com.udacity.project4.source

import com.udacity.project4.TestModelUtils
import com.udacity.project4.model.Reminder
import com.udacity.project4.utils.Result
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class DataSourceTest {

  private lateinit var dataSource: FakeDataSource


  @Before
  fun init() {
    dataSource = FakeDataSource()
  }


  @Test
  fun addReminderAndRetrieve_listNotEmpty() = runBlockingTest {
    dataSource.saveReminder(TestModelUtils.getTestReminder())
    val list = dataSource.getReminders() as Result.Success<List<Reminder>>
    assertThat(list.data.isEmpty(), `is`(false))
  }


  @Test
  fun saveAndRetrieveById() = runBlockingTest {
    val reminder = TestModelUtils.getTestReminder()
    dataSource.saveReminder(reminder)

    val savedReminder = dataSource.getReminderById(reminder.id)
    assertThat(savedReminder is Result.Success,`is`(true))
    savedReminder as Result.Success

    assertThat(reminder.title, `is`(savedReminder.data.title))
  }


  @Test
  fun saveAndDeleteByID() = runBlockingTest {
    val reminder = TestModelUtils.getTestReminder()
    dataSource.saveReminder(reminder)
    val list = dataSource.getReminders() as Result.Success<List<Reminder>>
    assertThat(list.data.isNotEmpty(), `is`(true))
    dataSource.deleteReminder(reminder)
    val savedReminder = dataSource.getReminderById(reminder.id)
    assertThat(savedReminder is Result.Error, `is`(true))

  }

}