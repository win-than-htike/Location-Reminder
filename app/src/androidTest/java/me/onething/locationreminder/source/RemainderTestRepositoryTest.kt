package me.onething.locationreminder.source

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
import me.onething.locationreminder.TestModelUtils
import me.onething.locationreminder.datasource.local.ReminderLocalDataSource
import me.onething.locationreminder.db.AppDatabase
import me.onething.locationreminder.model.Remainder
import me.onething.locationreminder.repo.RemaindersRepository
import me.onething.locationreminder.repo.RemaindersRepositoryImpl
import me.onething.locationreminder.utils.MainCoroutineRule
import me.onething.locationreminder.utils.Result

@MediumTest
@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemainderTestRepositoryTest {

  private lateinit var remainderRepository: RemaindersRepository
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

    val dataSource = ReminderLocalDataSource(database.remaindersDao())
    remainderRepository = RemaindersRepositoryImpl(dataSource)
  }

  @Test
  fun saveRemainderSuccess() = runBlocking {
    remainderRepository.saveReminder(TestModelUtils.getTestRemainder())
    val result = remainderRepository.getRemainderById(TestModelUtils.getTestRemainder().id)
    assertThat(result is Result.Success, `is`(true))
    result as Result.Success
    assertThat(result.data.id, `is`(TestModelUtils.getTestRemainder().id))
    assertThat(result.data.title, `is`(notNullValue()))
    assertThat(result.data.description, `is`(notNullValue()))
    assertThat(result.data.latitude, `is`(notNullValue()))
    assertThat(result.data.longitude, `is`(notNullValue()))
  }


  @Test
  fun saveRemainder_return_notEmptyList() = runBlocking {
    remainderRepository.saveReminder(TestModelUtils.getTestRemainder())
    val result = remainderRepository.getRemainders()
    val list = remainderRepository.getRemainders() as Result.Success<List<Remainder>>
    assertThat(list.data.isNotEmpty(), `is`(true))
  }

  @Test
  fun saveRemainder_retrieveWithId_return_notNull() = runBlocking {
    remainderRepository.saveReminder(TestModelUtils.getTestRemainder())
    val list = remainderRepository.getRemainderById("1")
    assertThat(list, `is`(notNullValue()))
  }

  @Test
  fun saveRemainder_andDeleteRemainder_return_EmptyList() = runBlocking {
    remainderRepository.saveReminder(TestModelUtils.getTestRemainder())
    val list = remainderRepository.getRemainders() as Result.Success<List<Remainder>>
    assertThat(list.data.isNotEmpty(), `is`(true))
    remainderRepository.deleteRemainder(TestModelUtils.getTestRemainder())
    val savedList = remainderRepository.getRemainders() as Result.Success<List<Remainder>>
    assertThat(savedList.data.isEmpty(), `is`(true))
  }
}
