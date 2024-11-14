package com.dicoding.asclepius

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class HistoryEventViewModelFactory(private val repository: HistoryEventRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HistoryEventViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HistoryEventViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
