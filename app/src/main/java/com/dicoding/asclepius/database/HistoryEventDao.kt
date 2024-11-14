package com.dicoding.asclepius.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface HistoryEventDao {
    @Query("DELETE FROM history WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(hostoryEvent: HistoryEvent)

    @Query("SELECT * FROM history")
    fun getAllHistories(): LiveData<List<HistoryEvent>>
}

