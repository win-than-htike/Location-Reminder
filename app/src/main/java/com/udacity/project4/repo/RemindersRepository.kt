package com.udacity.project4.repo

import androidx.lifecycle.LiveData
import com.udacity.project4.model.Remainder
import com.udacity.project4.utils.Result

interface RemindersRepository {

    fun observeRemainders(): LiveData<List<Remainder>>

    suspend fun saveReminder(remainder: Remainder)

    suspend fun deleteRemainder(remainder: Remainder)

    suspend fun getRemainderById(id: String): Result<Remainder>

    suspend fun getRemainders(): Result<List<Remainder>>

}