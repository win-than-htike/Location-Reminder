package com.udacity.project4.datasource.local

import androidx.lifecycle.LiveData
import com.udacity.project4.model.Remainder
import com.udacity.project4.utils.Result

interface RemainderDataSource {

    suspend fun saveRemainder(remainder: Remainder)

    suspend fun getRemainderById(id: String): Result<Remainder>

    suspend fun getRemainders(): Result<List<Remainder>>

    fun observeRemainders(): LiveData<List<Remainder>>

    suspend fun deleteRemainder(remainder: Remainder)

    suspend fun deleteAllRemainder()

}