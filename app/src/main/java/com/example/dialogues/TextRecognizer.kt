package com.example.dialogues

import android.media.Image
import android.util.Log
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

class TextRecognizer(private val onTextFound: (Text) -> Unit) {
    fun recognizeImageText(inputImage: InputImage, rotationDegrees: Int, onResult: (Boolean) -> Unit) {
        TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
            .process(inputImage)
            .addOnSuccessListener { recognizedText ->
                processTextFromImage(recognizedText)
                onResult(true)
            }
            .addOnFailureListener { error ->
                Log.d(TAG, "Failed to recognize image text")
                error.printStackTrace()
                onResult(false)
            }
    }

    private fun processTextFromImage(text: Text) {
        text.textBlocks.joinToString {
            it.text.lines().joinToString(" ")
        }.let {
            if (it.isNotBlank()) {
                onTextFound(text)
            }
        }
    }

    companion object {
        private val TAG = TextRecognizer::class.java.name
    }
}