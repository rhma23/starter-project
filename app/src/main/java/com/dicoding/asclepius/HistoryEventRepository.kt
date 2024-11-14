package com.dicoding.asclepius

import androidx.lifecycle.LiveData
import com.dicoding.asclepius.database.HistoryEventDao
import com.dicoding.asclepius.database.HistoryEvent

class HistoryEventRepository(private val historyEventDao: HistoryEventDao) {

    fun getAllHistories(): LiveData<List<HistoryEvent>> {
        return historyEventDao.getAllHistories()
    }

    suspend fun insertHistory(event: HistoryEvent) {
        try {
            historyEventDao.insert(event)
        } catch (e: Exception) {

        }
    }

    suspend fun deleteHistoryById(id: Int) {
        try {
            historyEventDao.deleteById(id)
        } catch (e: Exception) {

        }
    }
}
