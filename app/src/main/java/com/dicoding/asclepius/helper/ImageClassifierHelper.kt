package com.dicoding.asclepius.helper

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.SystemClock
import android.util.Log
import com.dicoding.asclepius.view.MainActivity
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.common.ops.CastOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.task.core.BaseOptions
import org.tensorflow.lite.task.vision.classifier.ImageClassifier

class ImageClassifierHelper(
    private val context: Context,
    private val classifierListener: MainActivity
) {
    private var imageClassifier: ImageClassifier? = null

    init {
        setupImageClassifier()
    }

    private fun setupImageClassifier() {
        val optionsBuilder = ImageClassifier.ImageClassifierOptions.builder()
            .setScoreThreshold(0.1f)
            .setMaxResults(3)

        val baseOptionsBuilder = BaseOptions.builder().setNumThreads(4)
        optionsBuilder.setBaseOptions(baseOptionsBuilder.build())

        try {
            imageClassifier = ImageClassifier.createFromFileAndOptions(
                context,
                "cancer_classification.tflite",
                optionsBuilder.build()
            )
        } catch (e: Exception) {
            classifierListener.onError("Failed to initialize classifier")
            Log.e("ImageClassifierHelper", e.message.toString())
        }
    }

    fun classifyStaticImage(imageUri: Uri) {
        val bitmap = getBitmapFromUri(imageUri)
        val imageProcessor = ImageProcessor.Builder()
            .add(ResizeOp(224, 224, ResizeOp.ResizeMethod.NEAREST_NEIGHBOR))
            .add(CastOp(DataType.UINT8))
            .build()

        val tensorImage = imageProcessor.process(TensorImage.fromBitmap(bitmap))
        val inferenceTime = SystemClock.uptimeMillis()

        val results = imageClassifier?.classify(tensorImage)
        val elapsedTime = SystemClock.uptimeMillis() - inferenceTime

        classifierListener.onResults(results, elapsedTime)
    }

    private fun getBitmapFromUri(uri: Uri): Bitmap {
        return context.contentResolver.openInputStream(uri)?.use { inputStream ->
            android.graphics.BitmapFactory.decodeStream(inputStream)
        } ?: Bitmap.createBitmap(224, 224, Bitmap.Config.ARGB_8888)
    }

}

