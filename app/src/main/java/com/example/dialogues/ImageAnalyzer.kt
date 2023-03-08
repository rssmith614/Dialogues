package com.example.dialogues

import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.text.Text

class ImageAnalyzer(onTextFound: (Text) -> Unit): ImageAnalysis.Analyzer {
    private val textRecognizer = TextRecognizer(onTextFound)

    @ExperimentalGetImage
    override fun analyze(imageProxy: ImageProxy) {
        val image = imageProxy.image ?: return
//        textRecognizer.recognizeImageText(image, imageProxy.imageInfo.rotationDegrees) {
//            imageProxy.close()
//        }
    }
}