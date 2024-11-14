package com.dicoding.asclepius

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.dicoding.asclepius.databinding.ActivityNewsBinding

class NewsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewsBinding
    private val eventViewModel: EventViewModel by viewModels()
    private lateinit var adapter: EventAdapter
    private val TAG = "NewsActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = EventAdapter(emptyList()) { event ->
        }

        eventViewModel.fetchTopHeadlines()

        binding.recyclernews.layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        binding.recyclernews.adapter = adapter

        Handler(Looper.getMainLooper()).postDelayed({
            eventViewModel.eventLiveData.observe(this) { apiResponse ->
                apiResponse?.let {
                    Log.d(TAG, "onCreate: " + it.articles)
                    adapter.updateData(it.articles ?: emptyList())
                }
            }

            eventViewModel.isLoading.observe(this) { isLoading ->
                if (isLoading) {
                    binding.recyclernews.visibility = View.INVISIBLE
                } else {
                    binding.progressBarNews.visibility = View.INVISIBLE
                    binding.recyclernews.visibility = View.VISIBLE
                }
            }
        }, 1000)
    }
}
