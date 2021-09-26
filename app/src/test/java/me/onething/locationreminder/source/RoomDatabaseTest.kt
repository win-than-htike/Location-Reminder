package me.onething.locationreminder.source

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import me.onething.locationreminder.TestModelUtils
import me.onething.locationreminder.db.AppDatabase
import me.onething.locationreminder.db.RemaindersDao
import me.onething.locationreminder.model.Remainder
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
  private lateinit var remainderDao: RemaindersDao
  private lateinit var remaindersDatabase: AppDatabase

  @get:Rule
  var instantExecutorRule = InstantTaskExecutorRule()


  @Before
  fun createDb() {
    stopKoin()
    val context = ApplicationProvider.getApplicationContext<Context>()
    remaindersDatabase = Room.inMemoryDatabaseBuilder(
      context, AppDatabase::class.java
    ).allowMainThreadQueries().build()
    remainderDao = remaindersDatabase.remaindersDao()
  }


  @After
  @Throws(IOException::class)
  fun closeDb() {
    remaindersDatabase.close()
  }

  @Test
  fun insertTaskAndGetById() = runBlockingTest {
    // GIVEN - Insert a task.
    val remainder = TestModelUtils.getTestRemainder()
    remainderDao.insertRemainder(remainder)

    // WHEN - Get the task by id from the database.
    val loadedRemainder = remainderDao.getRemainderById(remainder.id)

    // THEN - The loaded data contains the expected values.
    assertThat(loadedRemainder as Remainder, notNullValue())
    assertThat(loadedRemainder.id, `is`(remainder.id))
    assertThat(loadedRemainder.title, `is`(remainder.title))
    assertThat(loadedRemainder.description, `is`(remainder.description))
    assertThat(loadedRemainder.latitude, `is`(remainder.latitude))
    assertThat(loadedRemainder.longitude, `is`(remainder.longitude))
    assertThat(loadedRemainder.place, `is`(remainder.place))
  }


  @Test
  @Throws(Exception::class)
  fun saveRemainder_read_returnEqual() = runBlocking {
    val remainder: Remainder = TestModelUtils.getTestRemainder()
    remainderDao.insertRemainder(remainder)
    val byPlaceId = remainderDao.getRemainderById(remainder.id)
    assertThat(byPlaceId, equalTo(remainder))
  }


  @Test
  @Throws(Exception::class)
  fun saveRemainder_readList_returnNotEmpty() = runBlockingTest {
    val remainder: Remainder = TestModelUtils.getTestRemainder()
    remainderDao.insertRemainder(remainder)
    val remainders = remainderDao.getRemainders()
    assertThat(remainders.isEmpty(), `is`(false))
  }


  @Test
  @Throws(Exception::class)
  fun deleteAllRemainders_should_return_empty() = runBlocking(Dispatchers.IO) {
    val remainder: Remainder = TestModelUtils.getTestRemainder()
    remainderDao.insertRemainder(remainder)
    val deleted = remainderDao.deleteAllRemainders()
    assertThat(deleted, `is`(1))
  }

  @Test
  fun insertRemainderAndUpdateGetId() = runBlockingTest {
    val remainder = TestModelUtils.getTestRemainder()
    remainderDao.insertRemainder(remainder)
    remainderDao.updateRemainder(remainder.copy(title = "Updated"))
    val updatedRemainder = remainderDao.getRemainderById((remainder.id))
    assertThat(updatedRemainder?.title, `is`("Updated"))
  }
}
