package com.dicoding.asclepius.view

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.net.Uri

class MainViewModel : ViewModel() {
    private val _currentImageUri = MutableLiveData<Uri?>()
    val currentImageUri: LiveData<Uri?> = _currentImageUri

    fun setCurrentImageUri(uri: Uri?) {
        _currentImageUri.value = uri
    }
}
