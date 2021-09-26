package me.onething.locationreminder.repo

import androidx.lifecycle.LiveData
import me.onething.locationreminder.model.Remainder
import me.onething.locationreminder.utils.Result

interface RemaindersRepository {

    fun observeRemainders(): LiveData<List<Remainder>>

    suspend fun saveReminder(remainder: Remainder)

    suspend fun deleteRemainder(remainder: Remainder)

    suspend fun getRemainderById(id: String): Result<Remainder>

    suspend fun getRemainders(): Result<List<Remainder>>

}