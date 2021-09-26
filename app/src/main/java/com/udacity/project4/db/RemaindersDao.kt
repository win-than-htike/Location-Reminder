package com.udacity.project4.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.udacity.project4.model.Remainder

@Dao
interface RemaindersDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRemainder(remainder: Remainder)

    @Query("SELECT * FROM remainder")
    fun observeRemainders(): LiveData<List<Remainder>>

    @Query("SELECT * FROM remainder")
    fun getRemainders(): List<Remainder>

    @Query("SELECT * FROM remainder WHERE remainderId = :id LIMIT 1")
    suspend fun getRemainderById(id: String): Remainder?

    @Update
    fun updateRemainder(remainder: Remainder)

    @Delete
    fun deleteRemainder(remainder: Remainder)

    @Query("DELETE FROM remainder")
    fun deleteAllRemainders(): Int
}