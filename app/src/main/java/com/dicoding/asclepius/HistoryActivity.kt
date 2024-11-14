package com.dicoding.asclepius

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.asclepius.database.AppDatabase
import com.dicoding.asclepius.databinding.ActivityHistoryBinding

class HistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryBinding
    private lateinit var repository: HistoryEventRepository
    private val eventViewModel: EventViewModel by viewModels()
    private val historyEventViewModel: HistoryEventViewModel by viewModels {
        HistoryEventViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val appDatabase = AppDatabase.getDatabase(this)
        repository = HistoryEventRepository(appDatabase.historyEventDao())

        val adapter = EventAdapterHistory(historyEventViewModel) // Pass ViewModel here
        binding.recyclerHistory.adapter = adapter
        binding.recyclerHistory.layoutManager = LinearLayoutManager(this)

        binding.progressBarHistory.visibility = View.VISIBLE
        binding.recyclerHistory.visibility = View.INVISIBLE

        Handler(Looper.getMainLooper()).postDelayed({
            historyEventViewModel.allHistories.observe(this) { history ->
                if (history.isNullOrEmpty()) {
                    binding.recyclerHistory.visibility = View.INVISIBLE
                } else {
                    adapter.submitList(history)
                    binding.recyclerHistory.visibility = View.VISIBLE
                }
                binding.progressBarHistory.visibility = View.GONE
            }
        }, 1000)

        eventViewModel.errorLiveData.observe(this) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
