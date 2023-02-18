package com.example.dialogues

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.*
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import com.example.dialogues.databinding.ActivityMainBinding
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val resourceId = R.drawable.stc_sign

    private val isAllPermissionsGranted get() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private val TAG = MainActivity::class.java.name
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }

    private fun requestPermissions() = ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
//            if (isAllPermissionsGranted) {
//                startCamera()
//            } else {
//                Snackbar.make(binding.previewView, "Camera permission not granted. \nCannot perform magic ritual.", Snackbar.LENGTH_LONG).setAction("Retry") {
//                    requestPermissions()
//                }.show()
//                Log.e(TAG, "Camera permission not granted")
//            }
        }
    }

    private val cameraAdapter = CameraAdapter {
        Log.d(TAG, "Text Found: $it")
    }

//    private fun startCamera() = cameraAdapter.startCamera(this, this, binding.previewView.createSurfaceProvider())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val image: InputImage
//        pull image from resources
        val uri = Uri.parse("android.resource://${packageName}/${resourceId}")

        try {
            image = InputImage.fromFilePath(this, uri)
            Log.i(TAG, image.height.toString())
//            call the OCR activity
            TextRecognizer(::textfound).recognizeImageText(image, 0, ::resulttext)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

//    OCR Callback functions
//    Passes detected text into this one
    private fun textfound(text: Text) {
//        prevent image scaling so boxes are drawn on same size image that was sent to OCR
        val options = BitmapFactory.Options()
        options.inScaled = false
        val bitmap = BitmapFactory.decodeResource(resources, resourceId, options)
//        copy the image so we can draw on it
        val mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)

        val canvas = Canvas(mutableBitmap)
        val paint = Paint()
        paint.color = Color.RED
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 5f

//        val fillColor = Color.argb(90, 0, 100, 100)
//        paint.style = Paint.Style.FILL
//        paint.color = fillColor

    // Loop through each text block and draw a box around it
        for (block in text.textBlocks) {
            val boundingBox = block.boundingBox
            Log.i(TAG, boundingBox.toString())
            if (boundingBox != null) {
                canvas.drawRect(boundingBox, paint)
            }
        }

        binding.textView.text = text.text

//        put the new image on screen
        binding.imageView.setImageBitmap(mutableBitmap)
    }

//    passes boolean indicating successful text detection
    private fun resulttext(b: Boolean) {
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraAdapter.shutdown()
    }
}