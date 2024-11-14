package com.dicoding.asclepius

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EventViewModel : ViewModel() {

    private val _eventLiveData = MutableLiveData<ApiResponse<ArticlesItem>>()
    val eventLiveData: LiveData<ApiResponse<ArticlesItem>> get() = _eventLiveData
    private val _isLoading = MutableLiveData<Boolean>()
    val errorLiveData = MutableLiveData<String?>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun fetchTopHeadlines() {
        val query = "cancer"
        val category = "health"
        val language = "en"
        val apiKey = "1e0c4202ce62425094ccadf2200aad21"

        _isLoading.value = true
        RetrofitClient.apiService.getTopHeadlines(query, category, language, apiKey).enqueue(object : Callback<ApiResponse<ArticlesItem>> {
            override fun onResponse(call: Call<ApiResponse<ArticlesItem>>,
                                    response: Response<ApiResponse<ArticlesItem>>) {
                if (response.isSuccessful) {
                    response.body()?.let { eventResponse ->
                        _eventLiveData.value = eventResponse
                        Log.d("EventViewModel", "Data fetched: ${eventResponse.articles}")
                    } ?: run {
                        Log.e("EventViewModel", "Response body is null")
                    }
                } else {
                    Log.e("EventViewModel", "API response not successful: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ApiResponse<ArticlesItem>>, t: Throwable) {
                Log.e("EventViewModel", "API call failed: ${t.message}")
            }
        })
        _isLoading.value = false
    }
}
