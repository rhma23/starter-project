package com.dicoding.asclepius

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.asclepius.database.HistoryEvent
import kotlinx.coroutines.launch

class HistoryEventViewModel(private val repository: HistoryEventRepository) : ViewModel() {

    val allHistories: LiveData<List<HistoryEvent>> = repository.getAllHistories()

    fun addHistory(event: HistoryEvent) {
        viewModelScope.launch {
            try {
                repository.insertHistory(event)
                Log.d("HistoryEventViewModel", "Event added: ${event.category}")
            } catch (e: Exception) {
                Log.e("HistoryEventViewModel", "Failed to add favorite: ${e.message}")
            }
        }
    }

    fun removeHistory(event: HistoryEvent) {
        viewModelScope.launch {
            try {
                repository.deleteHistoryById(event.id)
                Log.d("HistoryEventViewModel", "History removed with eventId: ${event.id}")
            } catch (e: Exception) {
                Log.e("HistoryEventViewModel", "Failed to remove history: ${e.message}")
            }
        }
    }
}
