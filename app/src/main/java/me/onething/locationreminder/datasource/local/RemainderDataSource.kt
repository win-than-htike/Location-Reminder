package me.onething.locationreminder.datasource.local

import androidx.lifecycle.LiveData
import me.onething.locationreminder.model.Remainder
import me.onething.locationreminder.utils.Result

interface RemainderDataSource {

    suspend fun saveRemainder(remainder: Remainder)

    suspend fun getRemainderById(id: String): Result<Remainder>

    suspend fun getRemainders(): Result<List<Remainder>>

    fun observeRemainders(): LiveData<List<Remainder>>

    suspend fun deleteRemainder(remainder: Remainder)

    suspend fun deleteAllRemainder()

}