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
  fun addRemainderAndRetrieve_listNotEmpty() = runBlockingTest {
    dataSource.saveReminder(TestModelUtils.getTestRemainder())
    val list = dataSource.getReminders() as Result.Success<List<Reminder>>
    assertThat(list.data.isEmpty(), `is`(false))
  }


  @Test
  fun saveAndRetrieveById() = runBlockingTest {
    val remainder = TestModelUtils.getTestRemainder()
    dataSource.saveReminder(remainder)

    val savedRemainder = dataSource.getReminderById(remainder.id)
    assertThat(savedRemainder is Result.Success,`is`(true))
    savedRemainder as Result.Success

    assertThat(remainder.title, `is`(savedRemainder.data.title))
  }


  @Test
  fun saveAndDeleteByID() = runBlockingTest {
    val remainder = TestModelUtils.getTestRemainder()
    dataSource.saveReminder(remainder)
    val list = dataSource.getReminders() as Result.Success<List<Reminder>>
    assertThat(list.data.isNotEmpty(), `is`(true))
    dataSource.deleteReminder(remainder)
    val savedRemainder = dataSource.getReminderById(remainder.id)
    assertThat(savedRemainder is Result.Error, `is`(true))

  }

}