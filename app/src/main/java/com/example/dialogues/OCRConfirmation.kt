package com.example.dialogues

import android.annotation.SuppressLint
import android.graphics.*
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ui.AppBarConfiguration
import com.example.dialogues.databinding.ActivityOcrconfirmationBinding
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import java.io.IOException

class OCRConfirmation : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityOcrconfirmationBinding

    private val boundingBoxes = mutableListOf<Rect>()

    private val textBoxes = mutableListOf<String>()

    // Define a list of selected states for each bounding box
    private val selectedStates = mutableListOf<Boolean>()

    companion object {
        private val TAG = OCRConfirmation::class.java.name
        private var viewToBitmapWidthScaleFactor = 1.0
        private var viewToBitmapHeightScaleFactor = 1.0
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityOcrconfirmationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // let the image listen for touches in order to select/unselect boxes of text
        binding.imageView.setOnTouchListener { view, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                val x = event.x * viewToBitmapWidthScaleFactor
                val y = event.y * viewToBitmapHeightScaleFactor

                for ((i, rect) in boundingBoxes.withIndex()) {
                    // If the touch event is inside a bounding box, toggle its selected state
                    if (rect.contains(x.toInt(), y.toInt())) {
                        selectedStates[i] = !selectedStates[i]
                        // re-render boxes around text
                        drawBoxes()
                    }
                }
            }

            true
        }

        // assign button to next activity and pass selected text to it
        findViewById<Button>(R.id.confirm).setOnClickListener {
            // take text only from boxes still selected
            var result = ""
            for ((i, line) in textBoxes.withIndex()) {
                if (selectedStates[i]) {
                    val cleanLine = line.replace('\n', ' ')
                    result += "$cleanLine "
                }
            }
            // call Translation activity
            // val intent = Intent(this, TranslationActivity::class.java).putExtra("Text", result)
            // startActivity(intent)

            Log.i(TAG, "Calling translation activity: $result")
        }

        // pull "image" from process that started the activity to call OCR
        val intentUri = intent.extras?.getString("Image")
        val uri = Uri.parse(intentUri)

        val image: InputImage

        try {
            image = InputImage.fromFilePath(this, uri)
            // call the text recognition routine
            TextRecognizer(::textFound).recognizeImageText(image, 0, ::resultText)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    /* OCR CALLBACK FUNCTIONS */
    // Process detected text
    private fun textFound(text: Text) {
        // Loop through each text block and draw a box around it
        for (block in text.textBlocks) {

            val boundingBox = block.boundingBox
            if (boundingBox != null) {
                textBoxes.add(block.text)
                boundingBoxes.add(boundingBox)
                // boxes are selected by default
                selectedStates.add(true)
            }
        }
        drawBoxes()
    }

    // Process success state of text detection
    private fun resultText(b: Boolean) {
        if (!b) {
            Log.e(TAG, "No text found")
        }
    }

    private fun drawBoxes() {
        // pull "image" from process that started the activity use it to draw boxes
        val intentUri = intent.extras?.getString("Image")
        val uri = Uri.parse(intentUri)
        // prevent image scaling so boxes are drawn on same size image that was sent to OCR
        val options = BitmapFactory.Options()
        options.inScaled = false
        val bitmap = BitmapFactory.decodeStream(this.contentResolver.openInputStream(uri))
        // copy the image so we can draw on it
        val mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)

        val canvas = Canvas(mutableBitmap)

        val selectedPaint = Paint()
        selectedPaint.color = Color.GREEN
        selectedPaint.style = Paint.Style.STROKE
        selectedPaint.strokeWidth = 16f

        val unselectedPaint = Paint()
        unselectedPaint.color = Color.RED
        unselectedPaint.style = Paint.Style.STROKE
        unselectedPaint.strokeWidth = 16f

        for ((i, boundingBox) in boundingBoxes.withIndex()) {
            if (selectedStates[i]) {
                canvas.drawRect(boundingBox, selectedPaint)
            } else {
                canvas.drawRect(boundingBox, unselectedPaint)
            }
        }

        // we need the size ratio between the original image and the bitmap for clicking boxes
        viewToBitmapHeightScaleFactor = bitmap.height.toDouble() / binding.imageView.height.toDouble()
        viewToBitmapWidthScaleFactor = bitmap.width.toDouble() / binding.imageView.width.toDouble()

        // put the new image on screen
        binding.imageView.setImageBitmap(mutableBitmap)
        binding.imageView.scaleType = ImageView.ScaleType.CENTER_CROP
    }
}