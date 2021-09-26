package com.udacity.project4.source

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import com.udacity.project4.TestModelUtils
import com.udacity.project4.model.Remainder
import com.udacity.project4.repo.RemaindersRepository
import com.udacity.project4.repo.RemaindersRepositoryImpl
import com.udacity.project4.utils.Result
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.notNullValue
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class RemainderRepositoryTest {

  private lateinit var fakeDataSource: FakeDataSource
  private lateinit var remainderRepository: RemaindersRepository

  @Before
  fun init() {
    fakeDataSource = FakeDataSource()
    remainderRepository = RemaindersRepositoryImpl(fakeDataSource)
  }

  @Test
  fun saveRemainder_return_notEmptyList() = runBlockingTest {
    remainderRepository.saveReminder(TestModelUtils.getTestRemainder())
    val list = remainderRepository.getRemainders() as Result.Success<List<Remainder>>
    assertThat(list.data.isNotEmpty(), `is`(true))
  }

  @Test
  fun saveRemainder_retrieveWithId_return_notNull() = runBlockingTest {
    remainderRepository.saveReminder(TestModelUtils.getTestRemainder())
    val list = remainderRepository.getRemainderById("1")
    assertThat(list, `is`(notNullValue()))
  }

  @Test
  fun saveRemainder_andDeleteRemainder_return_EmptyList() = runBlockingTest {
    remainderRepository.saveReminder(TestModelUtils.getTestRemainder())
    val list = remainderRepository.getRemainders() as Result.Success<List<Remainder>>
    assertThat(list.data.isEmpty(), `is`(false))
    remainderRepository.deleteRemainder(TestModelUtils.getTestRemainder())
    val savedList = remainderRepository.getRemainders() as Result.Success<List<Remainder>>
    assertThat(savedList.data.isEmpty(), `is`(true))
  }


  @Test
  fun returnNullForRemainderById_whenError() = runBlockingTest {
    fakeDataSource.setShouldReturnError(true)
    val remainder = remainderRepository.getRemainderById("1")
    assertThat(remainder is Result.Error, `is`(true))
  }

}