package com.example.dialogues

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Vibrator
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ui.AppBarConfiguration
import com.example.dialogues.databinding.ActivityOcrconfirmationBinding
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import java.io.IOException

class OCRConfirmation : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityOcrconfirmationBinding

    private lateinit var imageUri: Uri
    private lateinit var mutableBitmap: Bitmap

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
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        fun vibrateTime() {
            val hapticFeedbackPreferences = getSharedPreferences("hfPrefs", Context.MODE_PRIVATE)
            val HapticFeedbackOn = hapticFeedbackPreferences.getBoolean("HapticFeedbackEnabled", false)
            if (HapticFeedbackOn) {
                vibrator.vibrate(50)
            }
        }
//        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
//            findViewById<Button>(R.id.confirm).setBackgroundColor(resources.getColor(R.color.solid_blue))
//        } else {
//            findViewById<Button>(R.id.confirm).setBackgroundColor(resources.getColor(R.color.solid_blue))
//        }
        // assign button to next activity and pass selected text to it
        findViewById<Button>(R.id.confirm).setOnClickListener {
            // take text only from boxes still selected
            var result = ""
            for ((i, line) in textBoxes.withIndex()) {
                if (selectedStates[i]) {
                    result += "$line "
                }
            }

            // call Translation activity
            val intent = Intent(this, TranslationScreen::class.java).putExtra("Text", result)
            startActivity(intent)
            //vibrateTime()
        }

        findViewById<Button>(R.id.back_button).setOnClickListener {
            onBackPressed()
            vibrateTime()
        }

        findViewById<Button>(R.id.settings_button).setOnClickListener {
            val intent = Intent(this, Settings::class.java)
            startActivity(intent)
        }

        // pull "image" from process that started the activity to call OCR
        val intentUri = intent.extras?.getString("imglocation")!!
        imageUri = Uri.parse(intentUri)

        val image: InputImage



        try {
            image = InputImage.fromFilePath(this, imageUri)
            // call the text recognition routine
            if (intent.extras?.getBoolean("Confirm")!!) {
                TextRecognizer(::textFound).recognizeImageText(image, 0, ::resultText)
            } else {
                findViewById<Button>(R.id.confirm).visibility = View.GONE
                findViewById<Button>(R.id.settings_button).visibility = View.GONE
                TextRecognizer(::passResultText).recognizeImageText(image, 0, ::resultText)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        // prevent image scaling so boxes are drawn on same size image that was sent to OCR
        val options = BitmapFactory.Options()
        options.inScaled = false

        val inStream = this.contentResolver.openInputStream(imageUri)
        val bitmap = BitmapFactory.decodeStream(inStream)
        val rotatedBitmap = bitmap.rotate(90f)

        // copy the image so we can draw on it
        mutableBitmap = rotatedBitmap.copy(Bitmap.Config.ARGB_8888, false)

        binding.imageView.setImageBitmap(mutableBitmap)
        binding.imageView.scaleType = ImageView.ScaleType.CENTER_CROP
    }

    /* OCR CALLBACK FUNCTIONS */
    // Process detected text
    private fun textFound(text: Text) {
        // Loop through each text block and draw a box around it
        for (block in text.textBlocks) {

            val boundingBox = block.boundingBox
            if (boundingBox != null) {
                // boxes must be at least 1% as large as the image
                // otherwise they're too hard to tap on
                if ((boundingBox.width() * boundingBox.height()).toDouble()/(mutableBitmap.width * mutableBitmap.height) < 0.01){
                    // ignore the puny boxes
                    continue
                }

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
        // hide loading icon, since image has been loaded and OCR call has finished
        findViewById<RelativeLayout>(R.id.loadingPanel).visibility = View.GONE
    }

    private fun passResultText(text: Text) {
        // take text only from boxes still selected
        var result = ""
        for (line in text.textBlocks) {
            result += "${line.text} "
        }

        // call Translation activity
        val intent = Intent(this, TranslationScreen::class.java).putExtra("Text", result)
        startActivity(intent)
    }

    private fun Bitmap.rotate(degrees: Float): Bitmap {
        val matrix = Matrix().apply { postRotate(degrees) }
        return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
    }

    private fun drawBoxes() {
        val copiedBitmap = mutableBitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(copiedBitmap)

        val selected = resources.getDrawable(R.drawable.selected_text)

        val unselected = resources.getDrawable(R.drawable.unselected_text)

        for ((i, boundingBox) in boundingBoxes.withIndex()) {
            if (selectedStates[i]) {
                selected.bounds = boundingBox
                selected.draw(canvas)
            } else {
                unselected.bounds = boundingBox
                unselected.draw(canvas)
            }
        }

        // we need the size ratio between the original image and the bitmap for clicking boxes
        viewToBitmapHeightScaleFactor = mutableBitmap.height.toDouble() / binding.imageView.height.toDouble()
        viewToBitmapWidthScaleFactor = mutableBitmap.width.toDouble() / binding.imageView.width.toDouble()

        // put the new image on screen
        binding.imageView.setImageBitmap(copiedBitmap)
        binding.imageView.scaleType = ImageView.ScaleType.CENTER_CROP
    }
}
