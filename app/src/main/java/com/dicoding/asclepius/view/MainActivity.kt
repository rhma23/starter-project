package com.dicoding.asclepius.view

import android.Manifest
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.dicoding.asclepius.HistoryActivity
import com.dicoding.asclepius.HistoryEventRepository
import com.dicoding.asclepius.NewsActivity
import com.dicoding.asclepius.R
import com.dicoding.asclepius.database.AppDatabase
import com.dicoding.asclepius.database.HistoryEvent
import com.dicoding.asclepius.databinding.ActivityMainBinding
import com.dicoding.asclepius.helper.ImageClassifierHelper
import com.dicoding.asclepius.HistoryEventViewModel
import org.tensorflow.lite.task.vision.classifier.Classifications
import java.io.File
import com.yalantis.ucrop.UCrop
import com.dicoding.asclepius.HistoryEventViewModelFactory
import com.jakewharton.threetenabp.AndroidThreeTen
import org.threeten.bp.LocalDate

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val intent = Intent()
    private lateinit var imageClassifierHelper: ImageClassifierHelper
    private lateinit var historyEventViewModel: HistoryEventViewModel
    private lateinit var mainViewModel: MainViewModel

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                showToast("Permission request granted")
            } else {
                showToast("Permission request denied")
            }
        }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            this,
            REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]

        val database = AppDatabase.getDatabase(this)
        val historyEventDao = database.historyEventDao()
        val repository = HistoryEventRepository(historyEventDao)

        AndroidThreeTen.init(this)

        intent.action = ACTION_GET_CONTENT
        intent.type = "image/*"

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // inisialisasi ViewModelFactory dan ViewModel
        val factory = HistoryEventViewModelFactory(repository)
        historyEventViewModel = ViewModelProvider(this, factory)[HistoryEventViewModel::class.java]
        imageClassifierHelper = ImageClassifierHelper(this, this)

        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }
        setSupportActionBar(binding.toolbar)

        // cek apakah sudah ada gambar yang disimpan di ViewModel
        mainViewModel.currentImageUri.observe(this) { uri ->
            if (uri != null) {
                showImage(uri)
            }
        }

        binding.galleryButton.setOnClickListener { startGallery() }
        binding.analyzeButton.setOnClickListener {
            // Periksa apakah gambar sudah dipilih
            if (mainViewModel.currentImageUri.value != null) {
                analyzeImage() // Jika gambar ada, analisis
            } else {
                showToast(getString(R.string.empty_image_warning)) // Jika tidak ada gambar
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.navigation_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.news -> {
                val intent = Intent(this, NewsActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.history -> {
                val intent = Intent(this, HistoryActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg = result.data?.data as Uri
            startCrop(selectedImg)
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private fun startGallery() {
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private fun startCrop(sourceUri: Uri) {
        val destinationUri = Uri.fromFile(File(cacheDir, "cropped_${System.currentTimeMillis()}.jpg"))
        UCrop.of(sourceUri, destinationUri)
            .withAspectRatio(16f, 9f)
            .withMaxResultSize(1080, 720)
            .start(this)
    }

    @Deprecated("This method has been deprecated in favor of using the Activity Result API\n      which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      contracts for common intents available in\n      {@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)}\n      with the appropriate {@link ActivityResultContract} and handling the result in the\n      {@link ActivityResultCallback#onActivityResult(Object) callback}.")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            if (requestCode == UCrop.REQUEST_CROP) {
                val resultUri = UCrop.getOutput(data!!)
                if (resultUri != null) {
                    mainViewModel.setCurrentImageUri(resultUri) // Update URI ke ViewModel
                    showImage(resultUri) // Tampilkan gambar yang sudah dicrop
                }
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            val cropError = UCrop.getError(data!!)
            showToast("Crop error: ${cropError?.message}")
        } else if (requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_CANCELED) {
            // jika cropping dibatalkan, gunakan URI gambar yang lama
            mainViewModel.currentImageUri.value?.let {
                showImage(it)
            // tampilkan gambar yang sebelumnya dipilih
            }
        }
    }

    private fun showImage(uri: Uri) {
        Log.d("Image URI", "showImage: $uri")
        binding.previewImageView.setImageURI(uri)
    }

    private fun analyzeImage() {
        mainViewModel.currentImageUri.value?.let { uri ->
            imageClassifierHelper.classifyStaticImage(uri)
        } ?: showToast(getString(R.string.empty_image_warning))
    }

    fun onResults(results: List<Classifications>?, inferenceTime: Long) {
        results?.firstOrNull()?.categories?.firstOrNull()?.let { category ->
            val resultText = "${category.label} ${Math.round(category.score * 100)}%"
            val intent = Intent(this, ResultActivity::class.java).apply {
                putExtra(ResultActivity.EXTRA_IMAGE_URI, mainViewModel.currentImageUri.value.toString())
                putExtra(ResultActivity.EXTRA_RESULT, resultText)
            }

            // buat objek `HistoryEvent` dengan URI gambar dan waktu sekarang
            val historyEvent = mainViewModel.currentImageUri.value?.let { uri ->
                HistoryEvent(
                    image = uri.toString(),
                    category = category.label,
                    confidentScore = Math.round(category.score * 100),
                    timeAdd = LocalDate.now()
                )
            }

            if (historyEvent != null) {
                historyEventViewModel.addHistory(historyEvent)
                Toast.makeText(this, "Data ditambahkan ke history", Toast.LENGTH_SHORT).show()
            }
            startActivity(intent)
        }
    }

    fun onError(error: String) {
        showToast(error)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }
}
